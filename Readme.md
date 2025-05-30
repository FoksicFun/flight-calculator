# Flight Calculator

Java приложение для учета рабочего времени летных специалистов

## Функционал
- Чтение данных о перелетах из JSON-файла
- Расчет рабочего времени по месяцам
- Проверка на превышение лимитов
- Генерация отчетов в JSON

## Технологии
- Java 17
- Maven
- Jackson (для работы с JSON)

## Как запустить
1. Установите JDK 17
2. Соберите проект: `mvn clean package`
3. Запустите: `java -jar target/flight-calculator-1.0-jar-with-dependencies.jar input.json output.json`

## Пример входных данных
Создайте файл `input.json`:
```json
{
  "workers": [
    {"name": "Иванов И.И.", "job": "Пилот"}
  ],
  "flights": [
    {
      "planeType": "Боинг-737",
      "start": "2023-01-01T08:00:00",
      "end": "2023-01-01T10:30:00",
      "crew": ["Иванов И.И."]
    }
  ]
}