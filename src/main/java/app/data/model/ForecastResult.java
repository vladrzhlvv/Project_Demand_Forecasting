package app.data.model;

public record ForecastResult(
        int window,
        double totalSold,
        double avg,
        double instability,
        double safetyStock,
        double forecast,
        double trend
) {}