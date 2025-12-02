# Инструкция по сборке и деплою на Tomcat 11

## Требования
- Java 17+ (Tomcat 11 требует минимум Java 17)
- Apache Tomcat 11.x
- Maven (опционально, если собираете через IDE)

## 1. Сборка проекта

### Через Maven (командная строка):
```bash
cd D:\JavaProjects\Lab-2
mvn clean package
```

После успешной сборки WAR-файл будет в `target/Lab-2-1.0-SNAPSHOT.war`

### Через IDE (IntelliJ IDEA / Eclipse):
1. Правой кнопкой на `pom.xml` → `Maven` → `Reload project`
2. `Maven` → `Lifecycle` → `package` (или `Build` → `Build Project`)

## 2. Деплой на Tomcat 11

### Вариант A: Деплой в корневой контекст (рекомендуется)

Чтобы API было доступно по `http://localhost:8080/api/...`:

1. **Остановите Tomcat** (если запущен):
   ```bash
   C:\apache-tomcat-11\bin\shutdown.bat
   ```

2. **Удалите или переименуйте** существующую папку `ROOT` в `webapps`:
   ```bash
   # Переименуйте старую папку (если нужна)
   ren C:\apache-tomcat-11\webapps\ROOT C:\apache-tomcat-11\webapps\ROOT_backup
   ```

3. **Переименуйте WAR-файл в `ROOT.war`**:
   ```bash
   copy target\Lab-2-1.0-SNAPSHOT.war C:\apache-tomcat-11\webapps\ROOT.war
   ```

4. **Запустите Tomcat**:
   ```bash
   C:\apache-tomcat-11\bin\startup.bat
   ```

5. **Проверьте**:
   - `http://localhost:8080` — должна открыться ваша страница (или 404, если нет index.html)
   - `http://localhost:8080/api/functions` — должен вернуть ответ (или ошибку, если БД не настроена)

### Вариант B: Деплой с именем приложения

Если хотите сохранить стандартный ROOT и деплоить отдельно:

1. **Скопируйте WAR-файл** в `webapps`:
   ```bash
   copy target\Lab-2-1.0-SNAPSHOT.war C:\apache-tomcat-11\webapps\
   ```

2. **Запустите Tomcat** (если не запущен):
   ```bash
   C:\apache-tomcat-11\bin\startup.bat
   ```

3. **API будет доступно по**:
   ```
   http://localhost:8080/Lab-2-1.0-SNAPSHOT/api/functions
   ```

   **Важно**: Обновите `baseUrl` в `postman/FunctionStorage.postman_environment.json`:
   ```json
   "baseUrl": "http://localhost:8080/Lab-2-1.0-SNAPSHOT/api"
   ```

## 3. Настройка базы данных

Перед запуском API убедитесь, что:

1. **PostgreSQL запущен** (или другая БД, если используете)
2. **Таблицы созданы** (скрипты в `src/main/resources/scripts/`)
3. **Настройки подключения** в `DBConnection.java` корректны

## 4. Проверка работы

### Через браузер:
```
GET http://localhost:8080/api/functions?userId=1
```

### Через Postman/Newman:
```bash
newman run postman/FunctionStorage.postman_collection.json \
  -e postman/FunctionStorage.postman_environment.json
```

## 5. Логи

Логи Tomcat находятся в:
- `C:\apache-tomcat-11\logs\catalina.out` (общие логи)
- `C:\apache-tomcat-11\logs\localhost.YYYY-MM-DD.log` (логи приложения)

Логи приложения (logback) будут в `logs/` внутри развёрнутого приложения или в `catalina.out`.

## 6. Устранение проблем

### Ошибка "ClassNotFoundException: jakarta.servlet..."
- Убедитесь, что используете Tomcat 11 (не 10 или 9)
- Проверьте, что в `pom.xml` указана `jakarta.servlet-api` версии 5.0.0+

### Ошибка "404 Not Found" на `/api/functions`
- Проверьте, что сервлет развёрнут (смотрите логи Tomcat)
- Убедитесь, что URL правильный (с учётом контекста приложения)
- Проверьте, что `@WebServlet` аннотация корректна

### Ошибка подключения к БД
- Проверьте, что PostgreSQL запущен
- Проверьте настройки в `DBConnection.java`
- Убедитесь, что драйвер PostgreSQL добавлен в `pom.xml` (если используется)

## 7. Пересборка после изменений

После изменения кода:

1. **Пересоберите проект**:
   ```bash
   mvn clean package
   ```

2. **Удалите старую версию** из `webapps`:
   ```bash
   # Если ROOT.war
   del C:\apache-tomcat-11\webapps\ROOT.war
   rmdir /s /q C:\apache-tomcat-11\webapps\ROOT
   
   # Или если Lab-2-1.0-SNAPSHOT.war
   del C:\apache-tomcat-11\webapps\Lab-2-1.0-SNAPSHOT.war
   rmdir /s /q C:\apache-tomcat-11\webapps\Lab-2-1.0-SNAPSHOT
   ```

3. **Скопируйте новый WAR** и перезапустите Tomcat

---

**Готово!** Ваше API должно быть доступно по `http://localhost:8080/api/...`

