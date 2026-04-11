package top.tatobamail.backend.purchaseRecords.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.tatobamail.backend.VcdInventory.VcdInventory;
import top.tatobamail.backend.VcdInventory.VcdInventoryRepository;
import top.tatobamail.backend.purchaseRecords.PurchaseRecords;
import top.tatobamail.backend.purchaseRecords.PurchaseRecordsRepository;
import top.tatobamail.backend.purchaseRecords.PurchaseRecordsService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PurchaseRecordsServiceImpl implements PurchaseRecordsService {

    @Autowired
    private PurchaseRecordsRepository purchaseRecordsRepository;

    @Autowired
    private VcdInventoryRepository vcdInventoryRepository;

    @Override
    @Transactional
    public PurchaseRecords createPurchaseRecord(PurchaseRecords record) {
        // 设置默认采购日期
        if (record.getPurchaseDate() == null) {
            record.setPurchaseDate(LocalDate.now());
        }

        // 触发器逻辑：采购后增加库存
        VcdInventory inventory = vcdInventoryRepository.findByVcd(record.getVcd())
                .orElseThrow(() -> new NoSuchElementException("VCD库存不存在"));

        // 增加库存数量（采购数量）
        inventory.setStock(inventory.getStock() + record.getQuantity());
        vcdInventoryRepository.save(inventory);

        return purchaseRecordsRepository.save(record);
    }

    @Override
    public PurchaseRecords getPurchaseRecordById(Long id) {
        return purchaseRecordsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase record not found"));
    }

    @Override
    public List<PurchaseRecords> getAllPurchaseRecords() {
        return purchaseRecordsRepository.findAll();
    }

    @Override
    public void deletePurchaseRecord(Long id) {
        purchaseRecordsRepository.deleteById(id);
    }
}
