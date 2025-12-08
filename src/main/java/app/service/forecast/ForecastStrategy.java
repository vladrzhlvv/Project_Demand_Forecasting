package app.service.forecast;

import app.data.model.ForecastResult;
import app.data.model.SaleRecord;
import java.util.List;

public interface ForecastStrategy {
    ForecastResult forecast(List<SaleRecord> data, int window, double reserveCoef);
}