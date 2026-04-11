package top.tatobamail.backend.purchaseRecords;

import java.util.List;

public interface PurchaseRecordsService {

    PurchaseRecords createPurchaseRecord(PurchaseRecords record);

    PurchaseRecords getPurchaseRecordById(Long id);

    List<PurchaseRecords> getAllPurchaseRecords();

    void deletePurchaseRecord(Long id);
}
