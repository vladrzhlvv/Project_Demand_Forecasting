package app.data.model;

public class SaleRecord {
    private final String date;
    private final int sold;

    public SaleRecord(String date, int sold) {
        this.date = date;
        this.sold = sold;
    }

    public String getDate() {
        return date;
    }

    public int getSold() {
        return sold;
    }

    @Override
    public String toString() {
        return date + ", " + sold;
    }
}