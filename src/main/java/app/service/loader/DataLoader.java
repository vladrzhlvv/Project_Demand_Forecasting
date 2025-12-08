package app.service.loader;

import app.data.model.SaleRecord;
import java.util.List;

public interface DataLoader {
    List<SaleRecord> load(String path);
}
