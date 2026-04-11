package top.tatobamail.backend.ReturnRecords.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import top.tatobamail.backend.RentalRecords.RentalRecords;
import top.tatobamail.backend.RentalRecords.RentalRecordsRepository;
import top.tatobamail.backend.ReturnRecords.ReturnRecords;
import top.tatobamail.backend.ReturnRecords.ReturnRecordsRepository;
import top.tatobamail.backend.ReturnRecords.ReturnRecordsService;
import top.tatobamail.backend.ReturnRecords.ReturnStatus;
import top.tatobamail.backend.VcdInventory.VcdInventory;
import top.tatobamail.backend.VcdInventory.VcdInventoryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class ReturnRecordsServiceImpl implements ReturnRecordsService {

    @Autowired
    private ReturnRecordsRepository returnRecordsRepository;

    @Autowired
    private RentalRecordsRepository rentalRecordsRepository;

    @Autowired
    private VcdInventoryRepository vcdInventoryRepository;

    @Override
    @Transactional
    public ReturnRecords createReturnRecord(ReturnRecords record) {
        // 设置默认归还日期
        if (record.getReturnDate() == null) {
            record.setReturnDate(LocalDate.now());
        }

        // 获取对应的租赁记录
        RentalRecords rental = rentalRecordsRepository.findById(record.getRental().getId())
                .orElseThrow(() -> new NoSuchElementException("租赁记录不存在"));

        // 计算逾期天数
        int overdueDays = 0;
        if (rental.getExpectedReturnDate() != null
                && record.getReturnDate().isAfter(rental.getExpectedReturnDate())) {
            overdueDays = (int) ChronoUnit.DAYS.between(
                    rental.getExpectedReturnDate(), record.getReturnDate());
        }
        record.setOverdueDays(overdueDays);

        // 根据逾期天数设置状态
        if (record.getStatus() == null) {
            if (overdueDays > 0) {
                record.setStatus(ReturnStatus.OVERDUE);
            } else {
                record.setStatus(ReturnStatus.NORMAL);
            }
        }

        // 触发器逻辑：归还后恢复库存（减少租出数量）
        VcdInventory inventory = vcdInventoryRepository.findByVcd(record.getVcd())
                .orElseThrow(() -> new NoSuchElementException("VCD库存不存在"));

        if (inventory.getRentCount() > 0) {
            inventory.setRentCount(inventory.getRentCount() - 1);
            vcdInventoryRepository.save(inventory);
        }

        // 更新租赁记录为已归还
        rental.setReturned(true);
        rentalRecordsRepository.save(rental);

        return returnRecordsRepository.save(record);
    }

    @Override
    public ReturnRecords getReturnRecordById(Long id) {
        if (id != null && id < 0) {
            long rentalId = -id;
            List<ReturnRecords> byRental = returnRecordsRepository.findByRentalIdWithAssociations(rentalId);
            if (!byRental.isEmpty()) {
                return byRental.get(0);
            }
            RentalRecords rental = rentalRecordsRepository.findByIdWithAssociations(rentalId)
                    .orElseThrow(() -> new RuntimeException("Return record not found"));
            if (!rental.isReturned()) {
                throw new RuntimeException("Return record not found");
            }
            return buildSyntheticReturn(rental);
        }
        return returnRecordsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return record not found"));
    }

    @Override
    public List<ReturnRecords> getAllReturnRecords() {
        List<ReturnRecords> fromDb = returnRecordsRepository.findAllWithAssociations();
        Set<Long> rentalIdsWithReturn = new HashSet<>();
        for (ReturnRecords rr : fromDb) {
            if (rr.getRental() != null && rr.getRental().getId() != null) {
                rentalIdsWithReturn.add(rr.getRental().getId());
            }
        }
        List<RentalRecords> returnedRentals = rentalRecordsRepository.findReturnedWithAssociations();
        List<ReturnRecords> merged = new ArrayList<>(fromDb);
        for (RentalRecords rental : returnedRentals) {
            if (rental.getId() != null && !rentalIdsWithReturn.contains(rental.getId())) {
                merged.add(buildSyntheticReturn(rental));
            }
        }
        merged.sort(Comparator
                .comparing(ReturnRecords::getReturnDate, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(r -> r.getId() == null ? 0L : Math.abs(r.getId()), Comparator.reverseOrder()));
        return merged;
    }

    @Override
    public void deleteReturnRecord(Long id) {
        if (id != null && id < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "该条为根据租赁记录补全的归还信息，无法单独删除。");
        }
        returnRecordsRepository.deleteById(id);
    }

    /**
     * 租赁表已标记已归还，但 return_records 无对应行时（如脚本只更新了 is_returned），
     * 为列表接口构造只读补全记录；id 为负的租赁主键，避免与真实归还记录 id 冲突。
     */
    private ReturnRecords buildSyntheticReturn(RentalRecords rental) {
        ReturnRecords r = new ReturnRecords();
        r.setId(-rental.getId());
        r.setRental(rental);
        r.setVcd(rental.getVcd());
        r.setCustomer(rental.getCustomer());

        LocalDate ret;
        if (rental.getUpdatedAt() != null) {
            ret = rental.getUpdatedAt().toLocalDate();
        } else {
            ret = LocalDate.now();
        }
        if (rental.getRentalDate() != null && ret.isBefore(rental.getRentalDate())) {
            ret = rental.getRentalDate();
        }

        r.setReturnDate(ret);

        int overdueDays = 0;
        if (rental.getExpectedReturnDate() != null && ret.isAfter(rental.getExpectedReturnDate())) {
            overdueDays = (int) ChronoUnit.DAYS.between(rental.getExpectedReturnDate(), ret);
        }
        r.setOverdueDays(overdueDays);
        r.setStatus(overdueDays > 0 ? ReturnStatus.OVERDUE : ReturnStatus.NORMAL);

        float fee = 0f;
        r.setOverdueFee(fee);
        r.setDepositRefund(rental.getDeposit());
        r.setPrice(Math.max(0f, rental.getDeposit() - fee));

        LocalDateTime ts = rental.getUpdatedAt() != null ? rental.getUpdatedAt() : LocalDateTime.now();
        r.setCreatedAt(ts);
        r.setUpdatedAt(ts);
        return r;
    }
}
