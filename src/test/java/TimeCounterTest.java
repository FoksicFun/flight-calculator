import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TimeCounterTest {

    @Test
    void testBasicFlightCounting() {
        Worker w1 = new Worker();
        w1.name = "Иванов И.И.";
        w1.job = "Пилот";

        Flight f1 = new Flight();
        f1.start = LocalDateTime.of(2023, 12, 1, 10, 0);
        f1.end = LocalDateTime.of(2023, 12, 1, 14, 0); // 4 часа
        f1.crew = List.of(w1.name);

        Flight f2 = new Flight();
        f2.start = LocalDateTime.of(2023, 12, 2, 9, 0);
        f2.end = LocalDateTime.of(2023, 12, 2, 17, 0); // 8 часов
        f2.crew = List.of(w1.name);

        Flight f3 = new Flight();
        f3.start = LocalDateTime.of(2023, 12, 2, 18, 0);
        f3.end = LocalDateTime.of(2023, 12, 2, 20, 0); // ещё 2 часа → 10 часов в день
        f3.crew = List.of(w1.name);

        List<Worker> workers = List.of(w1);
        List<Flight> flights = List.of(f1, f2, f3);

        List<WorkerReport> reports = new TimeCounter().count(workers, flights);
        WorkerReport report = reports.get(0);

        assertNotNull(report);
        assertEquals(1, report.months.size());

        WorkerReport.MonthData data = report.months.get(0);
        assertEquals("2023-12", data.month);
        assertEquals(14.0, data.hours);

        // Теперь ожидаем только одно предупреждение — за превышение дня
        assertEquals(1, data.warnings.size());
        assertTrue(data.warnings.get(0).contains("Превышено рабочее время за день"));
    }
}