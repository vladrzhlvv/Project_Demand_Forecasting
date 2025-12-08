package app.service.loader;

import app.data.model.SaleRecord;
import app.service.exception.CsvFormatException;
import app.util.CsvValidator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvDataLoader implements DataLoader {

    @Override
    public List<SaleRecord> load(String path) {
        List<SaleRecord> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String lower = line.toLowerCase().trim();
                if (lower.equals("date,sold") || lower.startsWith("date,")) continue;
                if (!CsvValidator.valid(line)) {
                    throw new CsvFormatException("Ошибка формата CSV: " + line);
                }
                String[] p = line.split(",");
                list.add(new SaleRecord(p[0].trim(), Integer.parseInt(p[1].trim())));
            }
        } catch (Exception e) {
            throw new CsvFormatException(e.getMessage());
        }
        return list;
    }
}
