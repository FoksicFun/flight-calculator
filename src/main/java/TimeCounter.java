import java.time.*;
import java.util.*;
import java.time.temporal.TemporalAdjusters;
import java.time.format.DateTimeFormatter;

public class TimeCounter {
    private static final double MAX_MONTHLY_HOURS = 80;
    private static final double MAX_WEEKLY_HOURS = 36;
    private static final double MAX_DAILY_HOURS = 8;

    public List<WorkerReport> count(List<Worker> workers, List<Flight> flights) {
        Map<String, WorkerReport> reports = new HashMap<>();

        // Инициализируем отчёты для всех работников
        for (Worker worker : workers) {
            WorkerReport report = new WorkerReport();
            report.worker = worker;
            reports.put(worker.name, report);
        }

        // Группируем полётное время по сотрудникам и датам
        Map<String, Map<LocalDate, Double>> dailyHoursMap = new HashMap<>();
        Map<String, Map<YearMonth, Double>> monthlyHoursMap = new HashMap<>();

        for (Flight flight : flights) {
            Duration duration = Duration.between(flight.start, flight.end);
            double hours = Math.round(duration.toMinutes() / 60.0 * 10) / 10.0; // округление до десятых

            for (String crewMember : flight.crew) {
                LocalDate date = flight.start.toLocalDate();
                YearMonth month = YearMonth.from(date);

                dailyHoursMap.computeIfAbsent(crewMember, k -> new HashMap<>())
                        .compute(date, (d, v) -> (v == null) ? hours : v + hours);

                monthlyHoursMap.computeIfAbsent(crewMember, k -> new HashMap<>())
                        .compute(month, (m, v) -> (v == null) ? hours : v + hours);
            }
        }

        // Заполняем месяцы и добавляем предупреждения
        for (Map.Entry<String, WorkerReport> entry : reports.entrySet()) {
            String name = entry.getKey();
            WorkerReport report = entry.getValue();

            Map<YearMonth, Double> totalMonthly = monthlyHoursMap.getOrDefault(name, Collections.emptyMap());
            Map<LocalDate, Double> totalDaily = dailyHoursMap.getOrDefault(name, Collections.emptyMap());

            for (Map.Entry<YearMonth, Double> monthEntry : totalMonthly.entrySet()) {
                YearMonth month = monthEntry.getKey();
                double totalHours = monthEntry.getValue();

                WorkerReport.MonthData monthData = new WorkerReport.MonthData();
                monthData.month = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                monthData.hours = Math.round(totalHours * 10) / 10.0;

                // Предупреждения
                if (totalHours > MAX_MONTHLY_HOURS) {
                    monthData.warnings.add("Превышено рабочее время за месяц (" + MAX_MONTHLY_HOURS + " ч): " + monthData.hours + " ч");
                }

                // Проверяем недели этого месяца
                Set<LocalDate> weeksChecked = new HashSet<>();
                for (LocalDate day = month.atDay(1); !day.isAfter(month.atEndOfMonth()); day = day.plusDays(1)) {
                    LocalDate weekStart = day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    if (!weeksChecked.contains(weekStart)) {
                        weeksChecked.add(weekStart);
                        double weeklyTotal = 0;
                        for (int i = 0; i < 7; i++) {
                            LocalDate currentDay = weekStart.plusDays(i);
                            Double daily = totalDaily.getOrDefault(currentDay, 0.0);
                            weeklyTotal += daily;
                        }
                        if (weeklyTotal > MAX_WEEKLY_HOURS) {
                            monthData.warnings.add("Превышено рабочее время за неделю: неделя с " + weekStart + " по " + weekStart.plusDays(6) + " (" + weeklyTotal + " ч)");
                        }
                    }
                }

                // Проверяем дни
                for (LocalDate day = month.atDay(1); !day.isAfter(month.atEndOfMonth()); day = day.plusDays(1)) {
                    Double daily = totalDaily.getOrDefault(day, 0.0);
                    if (daily > MAX_DAILY_HOURS) {
                        monthData.warnings.add("Превышено рабочее время за день: " + day + " (" + daily + " ч)");
                    }
                }

                report.months.add(monthData);
            }
        }

        return new ArrayList<>(reports.values());
    }
}