package app.service.forecast;

import app.data.model.ForecastResult;
import app.data.model.SaleRecord;

import java.util.List;

public class AutoSelectForecast implements ForecastStrategy {

    private String bestMethodName;
    private double bestError;
    private double lastActual; // последнее фактическое значение

    @Override
    public ForecastResult forecast(List<SaleRecord> data, int window, double safetyCoef) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Нет данных для прогноза.");
        }
        if (window <= 0) {
            throw new IllegalArgumentException("Окно анализа должно быть больше 0.");
        }

        SimpleAverageForecast simple = new SimpleAverageForecast();
        TrendForecast trend = new TrendForecast();
        HoltLinearTrend holt = new HoltLinearTrend();

        ForecastResult simpleRes = simple.forecast(data, window, safetyCoef);
        ForecastResult trendRes = trend.forecast(data, window, safetyCoef);
        ForecastResult holtRes = holt.forecast(data, window, safetyCoef);

        lastActual = data.get(data.size() - 1).getSold();

        double errSimple = Math.abs(lastActual - simpleRes.forecast());
        double errTrend = Math.abs(lastActual - trendRes.forecast());
        double errHolt = Math.abs(lastActual - holtRes.forecast());

        if (errSimple <= errTrend && errSimple <= errHolt) {
            bestMethodName = "SimpleAverage";
            bestError = errSimple;
        } else if (errTrend <= errSimple && errTrend <= errHolt) {
            bestMethodName = "TrendForecast";
            bestError = errTrend;
        } else {
            bestMethodName = "HoltLinearTrend";
            bestError = errHolt;
        }

        System.out.println("\n===== Автовыбор метода (ошибки последнего дня) =====");
        printError("SimpleAverage", errSimple, simpleRes.forecast());
        printError("TrendForecast", errTrend, trendRes.forecast());
        printError("HoltLinearTrend", errHolt, holtRes.forecast());
        System.out.println("Выбран метод: " + bestMethodName + "\n");

        // прогноз лучшего метода
        return switch (bestMethodName) {
            case "SimpleAverage" -> simpleRes;
            case "TrendForecast" -> trendRes;
            default -> holtRes;
        };
    }

    private void printError(String method, double err, double forecast) {
        double perc = (lastActual != 0) ? (err / lastActual * 100) : 0;
        String chosen = method.equals(bestMethodName) ? " ← выбран" : "";
        System.out.printf("%s: ошибка %.2f (%.1f%%)%s\n", method, err, perc, chosen);
    }

    public String getBestMethodName() {
        return bestMethodName;
    }
}
