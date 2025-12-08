package app.util;

public class CsvValidator {
    public static boolean valid(String line) {
        if (line == null || line.isBlank()) return false;
        String[] parts = line.split(",");
        if (parts.length != 2) return false;
        try {
            Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}