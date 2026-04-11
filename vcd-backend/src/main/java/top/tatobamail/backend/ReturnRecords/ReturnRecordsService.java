package top.tatobamail.backend.ReturnRecords;

import java.util.List;

public interface ReturnRecordsService {

    ReturnRecords createReturnRecord(ReturnRecords record);

    ReturnRecords getReturnRecordById(Long id);

    List<ReturnRecords> getAllReturnRecords();

    void deleteReturnRecord(Long id);
}
