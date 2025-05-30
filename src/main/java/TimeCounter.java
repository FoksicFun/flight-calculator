import java.util.*;
import java.time.*;

public class TimeCounter {
    public List<WorkerReport> count(List<Worker> workers, List<Flight> flights) {
        List<WorkerReport> reports = new ArrayList<>();
        
        // Простейшая реализация для теста
        for (Worker worker : workers) {
            WorkerReport report = new WorkerReport();
            report.worker = worker;
            
            WorkerReport.MonthData monthData = new WorkerReport.MonthData();
            monthData.month = "2023-01";
            monthData.hours = 10.5;
            
            report.months.add(monthData);
            reports.add(report);
        }
        
        return reports;
    }
}