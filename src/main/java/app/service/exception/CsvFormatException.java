package app.service.exception;

public class CsvFormatException extends RuntimeException {
    public CsvFormatException(String message) { super(message); }
}
