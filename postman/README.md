# Function Storage API – Newman tests

## Подготовка данных

1. Соберите и запустите приложение (Tomcat/Jetty/Spring Boot) так, чтобы API было доступно по адресу `http://localhost:8080/api`.
2. Заполните таблицы БД значениями пользователей и типов функций, которые соответствуют переменным окружения `ownerId` и `functionTypeId`.
3. Сгенерируйте сериализованные функции в формате base64 (для `serializedFunction`, `serializedFunctionSecondary`, `serializedFunctionUpdated`):
   - Откройте класс `org.example.tools.FunctionSerializer`.
   - Запустите `main` (через IDE или командой `java -cp target/classes org.example.tools.FunctionSerializer` при наличии зависимостей в classpath).
   - Скопируйте полученную строку base64 в файл `postman/FunctionStorage.postman_environment.json`.

## Запуск Newman

```bash
newman run postman/FunctionStorage.postman_collection.json \
  -e postman/FunctionStorage.postman_environment.json \
  --delay-request 200 \
  --reporters cli,json \
  --reporter-json-export reports/newman-results.json
```

- `--delay-request` сглаживает нагрузку и помогает замерять скорость.
- JSON-отчёт (`reports/newman-results.json`) содержит `run.executions[*].responseTime`, которые используются для таблицы сравнения.

## Что покрыто

Коллекция охватывает:
- CRUD над функциями;
- выборки по владельцу, типу и query-параметрам;
- бинарные операции над функциями;
- вычисление производных;
- зачистку тестовых данных (удаление созданных функций).

## Как получить метрики

1. После выполнения Newman откройте `reports/newman-results.json`.
2. Для каждого запроса вычислите:
   - среднее время (можно с помощью скрипта Node.js или простого jq);
   - минимальное и максимальное время (есть в массиве `executions`).
3. Заполните значения в `reports/api-performance.md`.

