package app.ui;

import app.data.model.SaleRecord;
import app.data.repository.SalesRepository;
import app.service.loader.CsvDataLoader;
import app.service.exception.CsvFormatException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StartMenu {

    private final SalesRepository repo;
    private final CsvDataLoader loader;
    private final Scanner sc = new Scanner(System.in);

    public StartMenu(SalesRepository repo, CsvDataLoader loader) {
        this.repo = repo;
        this.loader = loader;
    }

    public void start() {
        while (true) {
            System.out.println("--Программа прогноза спроса и планирования закупок--");
            System.out.println();
            System.out.println("Выберите способ ввода данных:");
            System.out.println("1 — CSV файл");
            System.out.println("2 — Ввести данные вручную");
            System.out.println("0 — Выход");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> loadCsv();
                case "2" -> manualInput();
                case "0" -> System.exit(0);
                default -> System.out.println("Некорректный ввод.");
            }

            if (repo.isLoaded()) break;
        }
    }

    private void loadCsv() {
        System.out.print("Введите путь к CSV: ");
        String path = sc.nextLine();
        try {
            List<SaleRecord> loaded = loader.load(path);
            if (loaded.isEmpty()) {
                System.out.println("Файл пустой, данные не загружены.");
                return;
            }
            repo.setRecords(loaded);
            System.out.println("Данные успешно загружены. Всего записей: " + loaded.size());
        } catch (CsvFormatException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Произошла ошибка при загрузке CSV: " + e.getMessage());
        }
    }

    private void manualInput() {
        System.out.println("Введите данные построчно в формате: дата количество");
        System.out.println("Пример: 2024-03-25 10");
        System.out.println("Для окончания ввода оставьте строку пустой и нажмите Enter.");

        List<SaleRecord> records = new ArrayList<>();

        while (true) {
            System.out.print("Введите запись: ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) break;

            String[] parts = line.split("\\s+");
            if (parts.length != 2) {
                System.out.println("Ошибка: каждая запись должна содержать дату и количество, разделённые пробелом.");
                continue;
            }

            try {
                String date = parts[0];
                int sold = Integer.parseInt(parts[1]);
                records.add(new SaleRecord(date, sold));
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: количество должно быть числом.");
            }
        }

        if (records.isEmpty()) {
            System.out.println("Данные не были введены.");
            return;
        }

        repo.setRecords(records);
        System.out.println("Данные успешно загружены. Всего записей: " + records.size());
    }
}