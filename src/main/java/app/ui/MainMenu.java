package app.ui;

import app.data.model.ForecastResult;
import app.data.model.SaleRecord;
import app.data.repository.SalesRepository;
import app.service.forecast.*;
import app.service.report.ReportExporter;

import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private final Scanner sc = new Scanner(System.in);
    private final SalesRepository repo;
    private ForecastResult lastForecast;
    private String lastMethodName;
    private int lastStock = -1;
    private int lastPack = -1;

    public MainMenu(SalesRepository repo) {
        this.repo = repo;
    }

    public void run() {
        while (true) {
            System.out.println("\n===== ГЛАВНОЕ МЕНЮ =====");
            System.out.println("1. Показать данные");
            System.out.println("2. Рассчитать прогноз");
            System.out.println("3. Рассчитать закупку");
            System.out.println("4. Экспорт отчёта CSV");
            System.out.println("5. Выход");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> showData();
                case "2" -> calculateForecast();
                case "3" -> calculatePurchase();
                case "4" -> exportReport();
                case "5" -> System.exit(0);
                default -> System.out.println("Некорректный ввод");
            }
        }
    }

    private void showData() {
        List<SaleRecord> records = repo.getRecords();
        if (records.isEmpty()) {
            System.out.println("Данные ещё не загружены.");
            return;
        }
        records.forEach(r -> System.out.println(r.getDate() + ", " + r.getSold()));
    }

    private void calculateForecast() {
        if (repo.getRecords().isEmpty()) {
            System.out.println("Сначала загрузите данные.");
            return;
        }

        System.out.println("Выберите метод прогнозирования:");
        System.out.println("1 — Простое среднее");
        System.out.println("2 — Линейный тренд");
        System.out.println("3 — Holt Linear Trend");
        System.out.println("4 — Автовыбор");

        String choice = sc.nextLine().trim();

        System.out.print("Введите окно анализа (например 7): ");
        int window = Integer.parseInt(sc.nextLine());

        System.out.print("Введите коэффициент запаса (0.1–0.3): ");
        double reserveCoef = Double.parseDouble(sc.nextLine());

        ForecastStrategy strategy;
        switch (choice) {
            case "2" -> strategy = new TrendForecast();
            case "3" -> strategy = new HoltLinearTrend();
            case "4" -> strategy = new AutoSelectForecast();
            default -> strategy = new SimpleAverageForecast();
        }

        try {
            lastForecast = strategy.forecast(repo.getRecords(), window, reserveCoef);
        } catch (Exception e) {
            System.out.println("Ошибка при расчете прогноза: " + e.getMessage());
            return;
        }

        lastMethodName = (strategy instanceof AutoSelectForecast auto) ? auto.getBestMethodName() : strategy.getClass().getSimpleName();

        System.out.println("\n===== ПРОГНОЗ =====");
        System.out.println("Продано за период: " + lastForecast.totalSold());
        System.out.println("Средний спрос: " + lastForecast.avg());
        System.out.println("Нестабильность: " + lastForecast.instability());
        System.out.println("Страховой запас: " + lastForecast.safetyStock());
        System.out.println("Итоговый прогноз (на 1 день): " + lastForecast.forecast());
        System.out.println("Метод прогнозирования: " + lastMethodName);
    }

    private void calculatePurchase() {
        if (lastForecast == null) {
            System.out.println("Сначала рассчитайте прогноз (пункт 2).");
            return;
        }

        System.out.print("Введите остаток на складе: ");
        lastStock = Integer.parseInt(sc.nextLine());

        System.out.print("Введите кратность упаковки (1 если штучно): ");
        lastPack = Integer.parseInt(sc.nextLine());

        int forecastDay = (int) Math.ceil(lastForecast.forecast());
        int forecastPeriod = (int) Math.ceil(lastForecast.forecast() * lastForecast.window());

        int purchaseDay = roundToPack(Math.max(0, forecastDay - lastStock), lastPack);
        int purchasePeriod = roundToPack(Math.max(0, forecastPeriod - lastStock), lastPack);

        System.out.println("\n===== РЕКОМЕНДУЕМАЯ ЗАКУПКА =====");
        System.out.println("Остаток на складе: " + lastStock);
        System.out.println("Кратность упаковки: " + lastPack);
        System.out.println("На 1 день: " + purchaseDay + " шт.");
        System.out.println("На весь период окна анализа (" + lastForecast.window() + " дней): " + purchasePeriod + " шт.");
    }

    private void exportReport() {
        if (lastForecast == null) {
            System.out.println("Сначала рассчитайте прогноз (пункт 2).");
            return;
        }

        System.out.print("Введите путь для сохранения CSV: ");
        String path = sc.nextLine();

        int stockToUse = lastStock >= 0 ? lastStock : 0;
        int packToUse = lastPack >= 1 ? lastPack : 1;

        ReportExporter exporter = new ReportExporter();
        exporter.exportReport(path, repo.getRecords(), lastForecast, lastMethodName, stockToUse, packToUse);
    }

    private int roundToPack(int amount, int pack) {
        if (pack <= 1) return amount;
        int remainder = amount % pack;
        return remainder == 0 ? amount : amount + (pack - remainder);
    }
}