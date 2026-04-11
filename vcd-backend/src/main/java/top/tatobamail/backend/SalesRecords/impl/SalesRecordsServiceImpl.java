package top.tatobamail.backend.SalesRecords.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.tatobamail.backend.SalesRecords.SaleRecords;
import top.tatobamail.backend.SalesRecords.SalesRecordsRepository;
import top.tatobamail.backend.SalesRecords.SalesRecordsService;
import top.tatobamail.backend.Vcd.Vcd;
import top.tatobamail.backend.Vcd.VcdRepository;
import top.tatobamail.backend.VcdInventory.VcdInventory;
import top.tatobamail.backend.VcdInventory.VcdInventoryRepository;
import top.tatobamail.backend.customers.customers;
import top.tatobamail.backend.customers.customersRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SalesRecordsServiceImpl implements SalesRecordsService {

    @Autowired
    private SalesRecordsRepository salesRecordsRepository;

    @Autowired
    private VcdInventoryRepository vcdInventoryRepository;

    @Autowired
    private VcdRepository vcdRepository;

    @Autowired
    private customersRepository customersRepository;

    @Override
    @Transactional
    public SaleRecords registerSaleRecord(SaleRecords saleRecord) {
        // 设置默认销售日期
        if (saleRecord.getSaleDate() == null) {
            saleRecord.setSaleDate(LocalDate.now());
        }

        // 触发器逻辑：检查并更新库存
        VcdInventory inventory = vcdInventoryRepository.findByVcd(saleRecord.getVcd())
                .orElseThrow(() -> new NoSuchElementException("VCD库存不存在"));

        // 检查库存是否充足（可用库存 = 总库存 - 已租出数量）
        int availableStock = inventory.getStock() - inventory.getRentCount();
        if (availableStock <= 0) {
            throw new IllegalStateException("VCD库存不足");
        }

        // 减少库存
        inventory.setStock(inventory.getStock() - 1);
        vcdInventoryRepository.save(inventory);

        // 保存销售记录
        return salesRecordsRepository.save(saleRecord);
    }

    @Override
    @Transactional
    public SaleRecords updateSaleRecord(Long id, SaleRecords incoming) {
        SaleRecords existing = salesRecordsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale record not found"));

        Vcd newVcd = vcdRepository.findById(incoming.getVcd().getId())
                .orElseThrow(() -> new NoSuchElementException("VCD不存在"));
        customers newCustomer = customersRepository.findById(incoming.getCustomer().getId())
                .orElseThrow(() -> new NoSuchElementException("客户不存在"));

        Long oldVcdId = existing.getVcd().getId();
        if (!oldVcdId.equals(newVcd.getId())) {
            VcdInventory oldInv = vcdInventoryRepository.findByVcd(existing.getVcd())
                    .orElseThrow(() -> new NoSuchElementException("VCD库存不存在"));
            oldInv.setStock(oldInv.getStock() + 1);
            vcdInventoryRepository.save(oldInv);

            VcdInventory newInv = vcdInventoryRepository.findByVcd(newVcd)
                    .orElseThrow(() -> new NoSuchElementException("VCD库存不存在"));
            int availableStock = newInv.getStock() - newInv.getRentCount();
            if (availableStock <= 0) {
                throw new IllegalStateException("VCD库存不足");
            }
            newInv.setStock(newInv.getStock() - 1);
            vcdInventoryRepository.save(newInv);
        }

        if (incoming.getSaleDate() == null) {
            incoming.setSaleDate(LocalDate.now());
        }

        existing.setVcd(newVcd);
        existing.setCustomer(newCustomer);
        existing.setPrice(incoming.getPrice());
        existing.setSaleDate(incoming.getSaleDate());

        return salesRecordsRepository.save(existing);
    }

    @Override
    public SaleRecords getSaleRecordById(Long id) {
        return salesRecordsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale record not found"));
    }

    @Override
    public List<SaleRecords> getAllSaleRecords() {
        return salesRecordsRepository.findAll();
    }

    @Override
    public void deleteSaleRecord(Long id) {
        salesRecordsRepository.deleteById(id);
    }
}
