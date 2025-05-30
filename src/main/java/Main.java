import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Программа запущена");
        
        if (args.length < 2) {
            System.out.println("Использование: java -jar app.jar input.json output.json");
            return;
        }

        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

        // Чтение входных данных
        InputData data = mapper.readValue(new File(args[0]), InputData.class);
        System.out.println("Прочитано работников: " + data.workers.size());
        System.out.println("Прочитано перелетов: " + data.flights.size());

        // Обработка данных
        List<WorkerReport> reports = new TimeCounter().count(data.workers, data.flights);

        // Запись результатов
        mapper.writerWithDefaultPrettyPrinter()
            .writeValue(new File(args[1]), reports);
        
        System.out.println("Результат записан в " + args[1]);
    }

    static class InputData {
        public List<Worker> workers;
        public List<Flight> flights;
    }
}