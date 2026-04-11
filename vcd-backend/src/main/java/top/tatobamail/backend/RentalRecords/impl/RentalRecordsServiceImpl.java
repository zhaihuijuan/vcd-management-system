package top.tatobamail.backend.RentalRecords.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.tatobamail.backend.RentalRecords.RentalRecords;
import top.tatobamail.backend.RentalRecords.RentalRecordsRepository;
import top.tatobamail.backend.RentalRecords.RentalRecordsService;
import top.tatobamail.backend.VcdInventory.VcdInventory;
import top.tatobamail.backend.VcdInventory.VcdInventoryRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RentalRecordsServiceImpl implements RentalRecordsService {

    @Autowired
    private RentalRecordsRepository rentalRecordsRepository;

    @Autowired
    private VcdInventoryRepository vcdInventoryRepository;

    @Override
    @Transactional
    public RentalRecords createRentalRecord(RentalRecords record) {
        // 设置默认租赁日期
        if (record.getRentalDate() == null) {
            record.setRentalDate(LocalDate.now());
        }

        // 触发器逻辑：检查库存并增加租出数量
        VcdInventory inventory = vcdInventoryRepository.findByVcd(record.getVcd())
                .orElseThrow(() -> new NoSuchElementException("VCD库存不存在"));

        // 检查可用库存（总库存 - 已租出数量）
        int availableStock = inventory.getStock() - inventory.getRentCount();
        if (availableStock <= 0) {
            throw new IllegalStateException("VCD库存不足，无法租赁");
        }

        // 增加租出数量
        inventory.setRentCount(inventory.getRentCount() + 1);
        vcdInventoryRepository.save(inventory);

        record.setReturned(false);
        return rentalRecordsRepository.save(record);
    }

    @Override
    public RentalRecords getRentalRecordById(Long id) {
        return rentalRecordsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental record not found"));
    }

    @Override
    public List<RentalRecords> getAllRentalRecords() {
        return rentalRecordsRepository.findAll();
    }

    @Override
    public void deleteRentalRecord(Long id) {
        rentalRecordsRepository.deleteById(id);
    }
}
