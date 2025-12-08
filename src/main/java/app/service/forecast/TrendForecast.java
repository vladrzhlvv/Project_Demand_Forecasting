package app.service.forecast;

import app.data.model.ForecastResult;
import app.data.model.SaleRecord;
import java.util.List;

public class TrendForecast implements ForecastStrategy {

    @Override
    public ForecastResult forecast(List<SaleRecord> data, int window, double reserveCoef) {
        if (data == null || data.size() < window)
            throw new IllegalArgumentException("Недостаточно данных для окна тренда.");

        List<SaleRecord> slice = data.subList(data.size() - window, data.size());

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        int i = 1;
        for (SaleRecord r : slice) {
            double y = r.getSold();
            sumY += y;
            sumX += i;
            sumXY += i * y;
            sumX2 += i * i;
            i++;
        }

        double avg = sumY / window;
        double b = (window * sumXY - sumX * sumY) / (window * sumX2 - sumX * sumX);
        double a = avg - b * (window + 1) / 2.0;

        double instability = slice.stream().mapToDouble(r -> Math.abs(r.getSold() - avg)).sum() / window;
        double safetyStock = Math.max(instability * reserveCoef, Math.max(0.5, avg * 0.02));
        double forecast = a + b * (window + 1) + safetyStock;

        return new ForecastResult(window, sumY, avg, instability, safetyStock, forecast, b);
    }
}
