package top.tatobamail.backend.RentalRecords;

import java.util.List;

public interface RentalRecordsService {

    RentalRecords createRentalRecord(RentalRecords record);

    RentalRecords getRentalRecordById(Long id);

    List<RentalRecords> getAllRentalRecords();

    void deleteRentalRecord(Long id);
}
