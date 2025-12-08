package app.data.repository;

import app.data.model.SaleRecord;
import java.util.ArrayList;
import java.util.List;

public class SalesRepository {
    private List<SaleRecord> records = new ArrayList<>();

    public void setRecords(List<SaleRecord> records) {
        if (records == null) this.records = new ArrayList<>();
        else this.records = records;
    }

    public List<SaleRecord> getRecords() {
        return records;
    }

    public boolean isLoaded() {
        return records != null && !records.isEmpty();
    }
}