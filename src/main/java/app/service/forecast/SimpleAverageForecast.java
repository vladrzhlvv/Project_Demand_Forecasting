package app.service.forecast;

import app.data.model.ForecastResult;
import app.data.model.SaleRecord;
import java.util.List;

public class SimpleAverageForecast implements ForecastStrategy {

    @Override
    public ForecastResult forecast(List<SaleRecord> data, int window, double safetyCoef) {
        if (data == null || data.isEmpty()) throw new IllegalArgumentException("Нет данных для прогноза.");
        int size = data.size();
        int start = Math.max(size - window, 0);

        double sum = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (int i = start; i < size; i++) {
            double sold = data.get(i).getSold();
            sum += sold;
            min = Math.min(min, sold);
            max = Math.max(max, sold);
        }

        double avg = sum / window;
        double instability = max - min;
        double safetyStock = Math.max(instability * safetyCoef, Math.max(0.5, avg * 0.02));
        double forecast = avg + safetyStock;

        return new ForecastResult(window, sum, avg, instability, safetyStock, forecast, 0.0);
    }
}
