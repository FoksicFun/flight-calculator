import java.util.ArrayList;
import java.util.List;

public class WorkerReport {
    public Worker worker;
    public List<MonthData> months = new ArrayList<>();

    public static class MonthData {
        public String month;
        public double hours;
        public List<String> warnings = new ArrayList<>();
    }
}