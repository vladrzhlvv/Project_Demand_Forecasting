package app.service.forecast;

import app.data.model.ForecastResult;
import app.data.model.SaleRecord;
import java.util.List;

public class HoltLinearTrend implements ForecastStrategy {

    private final double alpha;
    private final double beta;

    public HoltLinearTrend() { this(0.5, 0.3); }
    public HoltLinearTrend(double alpha, double beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    @Override
    public ForecastResult forecast(List<SaleRecord> data, int window, double reserveCoef) {
        if (data == null || data.size() < 2)
            throw new IllegalArgumentException("Недостаточно данных для Holt Linear Trend.");

        int start = Math.max(data.size() - window, 0);
        double[] level = new double[window];
        double[] trend = new double[window];

        level[0] = data.get(start).getSold();
        trend[0] = data.get(start + 1).getSold() - data.get(start).getSold();

        for (int i = 1; i < window; i++) {
            double val = data.get(start + i).getSold();
            level[i] = alpha * val + (1 - alpha) * (level[i - 1] + trend[i - 1]);
            trend[i] = beta * (level[i] - level[i - 1]) + (1 - beta) * trend[i - 1];
        }

        double forecastValue = level[window - 1] + trend[window - 1];

        double sum = 0;
        for (int i = start; i < data.size(); i++) sum += data.get(i).getSold();
        double avg = sum / window;

        double instability = 0;
        for (int i = start; i < data.size(); i++) instability += Math.abs(data.get(i).getSold() - avg);
        instability /= window;

        double safetyStock = Math.max(instability * reserveCoef, Math.max(0.5, avg * 0.02));

        return new ForecastResult(window, sum, avg, instability, safetyStock, forecastValue, trend[window - 1]);
    }
}
