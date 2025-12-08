package app.service.report;

import app.data.model.ForecastResult;
import app.data.model.SaleRecord;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportExporter {

    public void exportReport(String path, List<SaleRecord> data, ForecastResult forecast, String methodName, int stock, int pack) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write("start_date;end_date;total_sold;avg;instability;safety_stock;forecast_one_day;trend;method;stock;pack;purchase_one_day;purchase_period\n");

            int startIndex = Math.max(data.size() - forecast.window(), 0);
            String startDate = data.get(startIndex).getDate();
            String endDate = data.get(data.size() - 1).getDate();

            int purchaseDay = (int) Math.ceil(forecast.forecast());
            int purchasePeriod = (int) Math.ceil(forecast.forecast() * forecast.window());

            purchaseDay = (purchaseDay - stock <= 0) ? 0 : ((purchaseDay - stock + pack - 1) / pack) * pack;
            purchasePeriod = (purchasePeriod - stock <= 0) ? 0 : ((purchasePeriod - stock + pack - 1) / pack) * pack;

            writer.write(String.format("%s;%s;%.2f;%.2f;%.2f;%.2f;%.2f;%.2f;%s;%d;%d;%d;%d\n",
                    startDate, endDate, forecast.totalSold(), forecast.avg(), forecast.instability(),
                    forecast.safetyStock(), forecast.forecast(), forecast.trend(), methodName,
                    stock, pack, purchaseDay, purchasePeriod));

            System.out.println("Отчет успешно экспортирован в " + path);

        } catch (IOException e) {
            System.out.println("Ошибка при экспорте: " + e.getMessage());
        }
    }
}