package top.tatobamail.backend.SalesRecords;

import java.util.List;

public interface SalesRecordsService {

    SaleRecords registerSaleRecord(SaleRecords saleRecord);

    SaleRecords updateSaleRecord(Long id, SaleRecords saleRecord);

    SaleRecords getSaleRecordById(Long id);

    List<SaleRecords> getAllSaleRecords();

    void deleteSaleRecord(Long id);
}
