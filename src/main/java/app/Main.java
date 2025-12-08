package app;

import app.data.repository.SalesRepository;
import app.service.loader.CsvDataLoader;
import app.ui.MainMenu;
import app.ui.StartMenu;

public class Main {
    public static void main(String[] args) {
        SalesRepository repo = new SalesRepository();
        CsvDataLoader loader = new CsvDataLoader();

        StartMenu startMenu = new StartMenu(repo, loader);
        startMenu.start();

        MainMenu menu = new MainMenu(repo);
        menu.run();
    }
}