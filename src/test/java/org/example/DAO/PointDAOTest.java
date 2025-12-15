package org.example.DAO;

import org.example.models.Point;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PointDAOTest {
    private static final Logger logger = LoggerFactory.getLogger(PointDAOTest.class);
    private PointDAO pointDAO;
    private Point testPoint;
    private List<Integer> cleanupPointIds = new ArrayList<>();
    private Integer testFunctionId = 1; // Предполагаем, что функция с ID=1 существует

    @BeforeAll
    void setUp() {
        logger.info("Инициализация PointDAO тестов");
        pointDAO = new PointDAO();

        // Создаем тестовую точку
        testPoint = new Point();
        testPoint.setFunctionId(testFunctionId);
        testPoint.setXValue(10.5);
        testPoint.setYValue(20.7);
    }

    @AfterAll
    void tearDown() {
        logger.info("Очистка тестовых данных PointDAO");

        // Удаляем созданные точки
        for (Integer pointId : cleanupPointIds) {
            try {
                pointDAO.delete(pointId);
            } catch (Exception e) {
                logger.warn("Не удалось удалить точку ID {}: {}", pointId, e.getMessage());
            }
        }
    }

    @Test
    @Order(1)
    void testInsertPoint() {
        logger.info("Тест: создание точки");

        Point insertedPoint = pointDAO.insert(testPoint);

        assertNotNull(insertedPoint, "Созданная точка не должна быть null");
        assertNotNull(insertedPoint.getId(), "ID точки должен быть установлен");
        assertEquals(testPoint.getFunctionId(), insertedPoint.getFunctionId(), "ID функции должен совпадать");
        assertEquals(testPoint.getXValue(), insertedPoint.getXValue(), 0.001, "X значение должно совпадать");
        assertEquals(testPoint.getYValue(), insertedPoint.getYValue(), 0.001, "Y значение должно совпадать");

        testPoint = insertedPoint;
        cleanupPointIds.add(testPoint.getId());
        logger.info("Создана точка с ID: {}", testPoint.getId());
    }

    @Test
    @Order(2)
    void testFindById() {
        logger.info("Тест: поиск точки по ID");

        Optional<Point> foundPointOpt = pointDAO.findById(testPoint.getId());

        assertTrue(foundPointOpt.isPresent(), "Точка должна быть найдена по ID");
        Point foundPoint = foundPointOpt.get();

        assertEquals(testPoint.getId(), foundPoint.getId(), "ID должен совпадать");
        assertEquals(testPoint.getFunctionId(), foundPoint.getFunctionId(), "ID функции должен совпадать");
        assertEquals(testPoint.getXValue(), foundPoint.getXValue(), 0.001, "X значение должно совпадать");
        assertEquals(testPoint.getYValue(), foundPoint.getYValue(), 0.001, "Y значение должно совпадать");
    }

    @Test
    @Order(3)
    void testFindByFunctionId() {
        logger.info("Тест: поиск точек по ID функции");

        List<Point> points = pointDAO.findByFunctionId(testFunctionId);

        assertNotNull(points, "Список точек не должен быть null");
        assertFalse(points.isEmpty(), "Список точек не должен быть пустым");

        // Проверяем, что наша тестовая точка есть в списке
        boolean found = points.stream()
                .anyMatch(point -> point.getId().equals(testPoint.getId()));
        assertTrue(found, "Тестовая точка должна быть в списке");

        // Проверяем сортировку по X
        for (int i = 1; i < points.size(); i++) {
            assertTrue(points.get(i).getXValue() >= points.get(i - 1).getXValue(),
                    "Точки должны быть отсортированы по X");
        }
    }

    @Test
    @Order(4)
    void testFindByXRange() {
        logger.info("Тест: поиск точек по диапазону X");

        double minX = testPoint.getXValue() - 5.0;
        double maxX = testPoint.getXValue() + 5.0;

        List<Point> points = pointDAO.findByXRange(minX, maxX);

        assertNotNull(points, "Список точек не должен быть null");

        // Проверяем, что наша тестовая точка есть в списке
        boolean found = points.stream()
                .anyMatch(point -> point.getId().equals(testPoint.getId()));
        assertTrue(found, "Тестовая точка должна быть в диапазоне");

        // Проверяем, что все точки в диапазоне
        for (Point point : points) {
            assertTrue(point.getXValue() >= minX && point.getXValue() <= maxX,
                    "Все точки должны быть в указанном диапазоне X");
        }
    }

    @Test
    @Order(5)
    void testFindByYRange() {
        logger.info("Тест: поиск точек по диапазону Y");

        double minY = testPoint.getYValue() - 10.0;
        double maxY = testPoint.getYValue() + 10.0;

        List<Point> points = pointDAO.findByYRange(minY, maxY);

        assertNotNull(points, "Список точек не должен быть null");

        // Проверяем, что наша тестовая точка есть в списке
        boolean found = points.stream()
                .anyMatch(point -> point.getId().equals(testPoint.getId()));
        assertTrue(found, "Тестовая точка должна быть в диапазоне");

        // Проверяем, что все точки в диапазоне
        for (Point point : points) {
            assertTrue(point.getYValue() >= minY && point.getYValue() <= maxY,
                    "Все точки должны быть в указанном диапазоне Y");
        }
    }

    @Test
    @Order(6)
    void testFindByFunctionIdAndX() {
        logger.info("Тест: поиск точки по functionId и X");

        Optional<Point> foundPointOpt = pointDAO.findByFunctionIdAndX(testFunctionId, testPoint.getXValue());

        assertTrue(foundPointOpt.isPresent(), "Точка должна быть найдена");
        Point foundPoint = foundPointOpt.get();

        assertEquals(testPoint.getId(), foundPoint.getId(), "ID должен совпадать");
        assertEquals(testPoint.getFunctionId(), foundPoint.getFunctionId(), "ID функции должен совпадать");
        assertEquals(testPoint.getXValue(), foundPoint.getXValue(), 0.001, "X значение должно совпадать");
    }

    @Test
    @Order(7)
    void testInsertBatch() {
        logger.info("Тест: массовое добавление точек");

        List<Point> batchPoints = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Point point = new Point();
            point.setFunctionId(testFunctionId);
            point.setXValue(100.0 + i);
            point.setYValue(200.0 + i);
            batchPoints.add(point);
        }

        int insertedCount = pointDAO.insertBatch(batchPoints);

        assertEquals(5, insertedCount, "Должно быть вставлено 5 точек");

        // Проверяем, что точки действительно созданы
        List<Point> allPoints = pointDAO.findByFunctionId(testFunctionId);
        int pointsCount = pointDAO.countByFunctionId(testFunctionId);

        assertTrue(pointsCount >= 6, "Должно быть как минимум 6 точек (1 старая + 5 новых)");

        // Сохраняем ID для последующей очистки
        allPoints.stream()
                .filter(p -> p.getId() != null && !cleanupPointIds.contains(p.getId()))
                .forEach(p -> cleanupPointIds.add(p.getId()));
    }

    @Test
    @Order(8)
    void testUpdatePoint() {
        logger.info("Тест: обновление точки");

        Point updatedPoint = new Point();
        updatedPoint.setId(testPoint.getId());
        updatedPoint.setFunctionId(testFunctionId + 1); // Меняем функцию
        updatedPoint.setXValue(50.0);
        updatedPoint.setYValue(60.0);

        boolean updateResult = pointDAO.update(updatedPoint);
        assertTrue(updateResult, "Обновление должно быть успешным");

        // Проверяем обновленные данные
        Optional<Point> foundPointOpt = pointDAO.findById(testPoint.getId());
        assertTrue(foundPointOpt.isPresent());
        Point foundPoint = foundPointOpt.get();

        assertEquals(updatedPoint.getFunctionId(), foundPoint.getFunctionId(), "ID функции должен быть обновлен");
        assertEquals(updatedPoint.getXValue(), foundPoint.getXValue(), 0.001, "X значение должно быть обновлено");
        assertEquals(updatedPoint.getYValue(), foundPoint.getYValue(), 0.001, "Y значение должно быть обновлено");

        testPoint = foundPoint;
    }

    @Test
    @Order(9)
    void testUpdateXValue() {
        logger.info("Тест: обновление X значения точки");

        double newXValue = 75.5;
        boolean updateResult = pointDAO.updateXValue(testPoint.getId(), newXValue);
        assertTrue(updateResult, "Обновление X должно быть успешным");

        // Проверяем обновленное значение
        Optional<Point> foundPointOpt = pointDAO.findById(testPoint.getId());
        assertTrue(foundPointOpt.isPresent());
        assertEquals(newXValue, foundPointOpt.get().getXValue(), 0.001, "X значение должно быть обновлено");

        testPoint.setXValue(newXValue);
    }

    @Test
    @Order(10)
    void testUpdateYValue() {
        logger.info("Тест: обновление Y значения точки");

        double newYValue = 85.5;
        boolean updateResult = pointDAO.updateYValue(testPoint.getId(), newYValue);
        assertTrue(updateResult, "Обновление Y должно быть успешным");

        // Проверяем обновленное значение
        Optional<Point> foundPointOpt = pointDAO.findById(testPoint.getId());
        assertTrue(foundPointOpt.isPresent());
        assertEquals(newYValue, foundPointOpt.get().getYValue(), 0.001, "Y значение должно быть обновлено");

        testPoint.setYValue(newYValue);
    }

    @Test
    @Order(11)
    void testUpdateBatch() {
        logger.info("Тест: массовое обновление точек");

        // Создаем несколько точек для обновления
        List<Point> pointsToUpdate = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Point point = new Point();
            point.setFunctionId(testFunctionId);
            point.setXValue(i * 10.0);
            point.setYValue(i * 20.0);
            Point inserted = pointDAO.insert(point);
            assertNotNull(inserted);
            cleanupPointIds.add(inserted.getId());

            // Меняем данные для обновления
            inserted.setXValue(inserted.getXValue() + 100.0);
            inserted.setYValue(inserted.getYValue() + 100.0);
            pointsToUpdate.add(inserted);
        }

        int updatedCount = pointDAO.updateBatch(pointsToUpdate);
        assertEquals(3, updatedCount, "Должно быть обновлено 3 точки");
    }

    @Test
    @Order(12)
    void testDeleteById() {
        logger.info("Тест: удаление точки по ID");

        // Создаем временную точку для удаления
        Point tempPoint = new Point();
        tempPoint.setFunctionId(testFunctionId);
        tempPoint.setXValue(999.9);
        tempPoint.setYValue(888.8);

        Point insertedTempPoint = pointDAO.insert(tempPoint);
        assertNotNull(insertedTempPoint);

        boolean deleteResult = pointDAO.delete(insertedTempPoint.getId());
        assertTrue(deleteResult, "Удаление должно быть успешным");

        // Проверяем, что точка удалена
        Optional<Point> foundPoint = pointDAO.findById(insertedTempPoint.getId());
        assertFalse(foundPoint.isPresent(), "Удаленная точка не должна быть найдена");
    }

    @Test
    @Order(13)
    void testDeleteByFunctionId() {
        logger.info("Тест: удаление всех точек функции");

        // Создаем несколько точек для определенной функции
        int specificFunctionId = 999;
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Point point = new Point();
            point.setFunctionId(specificFunctionId);
            point.setXValue(i * 5.0);
            point.setYValue(i * 10.0);
            pointDAO.insert(point);
            points.add(point);
        }

        // Удаляем все точки функции
        boolean deleteResult = pointDAO.deleteByFunctionId(specificFunctionId);
        assertTrue(deleteResult, "Удаление должно быть успешным");

        // Проверяем, что точек нет
        List<Point> foundPoints = pointDAO.findByFunctionId(specificFunctionId);
        assertTrue(foundPoints.isEmpty(), "Не должно быть точек после удаления");
    }

    @Test
    @Order(14)
    void testDeleteByIds() {
        logger.info("Тест: массовое удаление точек по списку ID");

        // Создаем несколько точек
        List<Integer> pointIdsToDelete = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Point point = new Point();
            point.setFunctionId(testFunctionId);
            point.setXValue(i * 15.0);
            point.setYValue(i * 25.0);
            Point inserted = pointDAO.insert(point);
            assertNotNull(inserted);
            pointIdsToDelete.add(inserted.getId());
        }

        // Удаляем массово
        int deletedCount = pointDAO.deleteByIds(pointIdsToDelete);
        assertEquals(3, deletedCount, "Должно быть удалено 3 точки");

        // Проверяем, что точки удалены
        for (Integer id : pointIdsToDelete) {
            Optional<Point> foundPoint = pointDAO.findById(id);
            assertFalse(foundPoint.isPresent(), "Удаленная точка с ID " + id + " не должна быть найдена");
        }
    }

    @Test
    @Order(15)
    void testCountAll() {
        logger.info("Тест: подсчет всех точек");

        int count = pointDAO.countAll();
        assertTrue(count > 0, "Количество точек должно быть больше 0");

        logger.info("Всего точек: {}", count);
    }

    @Test
    @Order(16)
    void testCountByFunctionId() {
        logger.info("Тест: подсчет точек функции");

        int count = pointDAO.countByFunctionId(testFunctionId);
        assertTrue(count > 0, "Должна быть хотя бы одна точка у функции");

        logger.info("Точек у функции {}: {}", testFunctionId, count);
    }

    @Test
    @Order(17)
    void testExistsByFunctionIdAndX() {
        logger.info("Тест: проверка существования точки по functionId и X");

        // Создаем новую точку с известными координатами для этого теста
        Point testPointForExists = new Point();
        testPointForExists.setFunctionId(testFunctionId);
        testPointForExists.setXValue(123.456); // Уникальное значение для этого теста
        testPointForExists.setYValue(789.012);

        Point insertedPoint = pointDAO.insert(testPointForExists);
        assertNotNull(insertedPoint);

        // Сохраняем ID для очистки
        if (insertedPoint.getId() != null) {
            cleanupPointIds.add(insertedPoint.getId());
        }

        // Теперь проверяем существование с актуальными значениями
        boolean exists = pointDAO.existsByFunctionIdAndX(testFunctionId, 123.456);
        assertTrue(exists, "Точка должна существовать с указанными functionId и X");

        boolean notExists = pointDAO.existsByFunctionIdAndX(testFunctionId, 9999.9);
        assertFalse(notExists, "Точка с несуществующим X не должна существовать");

        // Также проверяем с другим functionId
        boolean notExistsDifferentFunction = pointDAO.existsByFunctionIdAndX(testFunctionId + 1000, 123.456);
        assertFalse(notExistsDifferentFunction, "Точка с другим functionId не должна существовать");
    }

    @Test
    @Order(18)
    void testGetXRangeForFunction() {
        logger.info("Тест: получение диапазона X для функции");

        double[] range = pointDAO.getXRangeForFunction(testFunctionId);

        assertNotNull(range, "Диапазон не должен быть null");
        assertEquals(2, range.length, "Диапазон должен содержать 2 значения");
        assertTrue(range[0] <= range[1], "Min должен быть <= Max");

        logger.info("Диапазон X для функции {}: от {} до {}", testFunctionId, range[0], range[1]);
    }

    @Test
    @Order(19)
    void testGetYRangeForFunction() {
        logger.info("Тест: получение диапазона Y для функции");

        double[] range = pointDAO.getYRangeForFunction(testFunctionId);

        assertNotNull(range, "Диапазон не должен быть null");
        assertEquals(2, range.length, "Диапазон должен содержать 2 значения");
        assertTrue(range[0] <= range[1], "Min должен быть <= Max");

        logger.info("Диапазон Y для функции {}: от {} до {}", testFunctionId, range[0], range[1]);
    }

    @Test
    @Order(20)
    void testFindWithSorting() {
        logger.info("Тест: поиск с сортировкой");

        // Тестируем разные варианты сортировки
        List<Point> pointsByIdAsc = pointDAO.findWithSorting("id", "ASC");
        List<Point> pointsByIdDesc = pointDAO.findWithSorting("id", "DESC");
        List<Point> pointsByX = pointDAO.findWithSorting("x_value", "ASC");
        List<Point> pointsByY = pointDAO.findWithSorting("y_value", "DESC");

        assertNotNull(pointsByIdAsc);
        assertNotNull(pointsByIdDesc);
        assertNotNull(pointsByX);
        assertNotNull(pointsByY);

        // Проверяем сортировку по ID
        for (int i = 1; i < pointsByIdAsc.size(); i++) {
            assertTrue(pointsByIdAsc.get(i).getId() > pointsByIdAsc.get(i - 1).getId(),
                    "Сортировка по ID ASC должна быть восходящей");
        }

        for (int i = 1; i < pointsByIdDesc.size(); i++) {
            assertTrue(pointsByIdDesc.get(i).getId() < pointsByIdDesc.get(i - 1).getId(),
                    "Сортировка по ID DESC должна быть нисходящей");
        }
    }

    @Test
    @Order(21)
    void testFindByFunctionIds() {
        logger.info("Тест: поиск точек по списку ID функций");

        // Создаем точки для разных функций
        List<Integer> functionIds = new ArrayList<>();
        functionIds.add(testFunctionId);

        // Добавляем еще одну функцию, если она существует
        int anotherFunctionId = testFunctionId + 1;
        functionIds.add(anotherFunctionId);

        List<Point> points = pointDAO.findByFunctionIds(functionIds);

        assertNotNull(points, "Список точек не должен быть null");

        // Проверяем, что все точки принадлежат указанным функциям
        for (Point point : points) {
            assertTrue(functionIds.contains(point.getFunctionId()),
                    "Точка должна принадлежать одной из указанных функций");
        }
    }

    @Test
    @Order(22)
    void testEdgeCases() {
        logger.info("Тест: проверка граничных случаев");

        // Поиск несуществующей точки
        Optional<Point> nonExistentPoint = pointDAO.findById(-999);
        assertFalse(nonExistentPoint.isPresent(), "Несуществующая точка не должна быть найдена");

        // Поиск по несуществующей функции
        List<Point> nonExistentFunctionPoints = pointDAO.findByFunctionId(-999);
        assertNotNull(nonExistentFunctionPoints, "Список должен быть возвращен");
        assertTrue(nonExistentFunctionPoints.isEmpty(), "Список точек несуществующей функции должен быть пустым");

        // Поиск по пустому списку functionIds
        List<Point> emptyFunctionIdsResult = pointDAO.findByFunctionIds(List.of());
        assertNotNull(emptyFunctionIdsResult, "Результат не должен быть null");
        assertTrue(emptyFunctionIdsResult.isEmpty(), "Результат поиска по пустому списку должен быть пустым");

        // Удаление несуществующей точки
        boolean deleteResult = pointDAO.delete(-999);
        assertFalse(deleteResult, "Удаление несуществующей точки должно вернуть false");

        // Обновление несуществующей точки
        Point nonExistentPointForUpdate = new Point();
        nonExistentPointForUpdate.setId(-999);
        nonExistentPointForUpdate.setFunctionId(1);
        nonExistentPointForUpdate.setXValue(1.0);
        nonExistentPointForUpdate.setYValue(2.0);

        boolean updateResult = pointDAO.update(nonExistentPointForUpdate);
        assertFalse(updateResult, "Обновление несуществующей точки должно вернуть false");
    }

    @Test
    @Order(23)
    void testFindByFunctionIdAndXInAndIdNotIn() {
        logger.info("Тест: поиск точек по functionId, списку X значений и исключению ID");

        // Создаем тестовые точки
        int testFuncId = 1000;
        List<Double> xValues = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        List<Integer> pointIds = new ArrayList<>();

        // Создаем 5 точек с разными X значениями
        for (int i = 0; i < xValues.size(); i++) {
            Point point = new Point();
            point.setFunctionId(testFuncId);
            point.setXValue(xValues.get(i));
            point.setYValue(i * 10.0);
            Point inserted = pointDAO.insert(point);
            assertNotNull(inserted);
            pointIds.add(inserted.getId());
            cleanupPointIds.add(inserted.getId());
        }

        // Создаем еще одну точку с тем же X, но другим ID для исключения
        Point excludedPoint = new Point();
        excludedPoint.setFunctionId(testFuncId);
        excludedPoint.setXValue(2.0); // Такое же X значение как у точки с индексом 1
        excludedPoint.setYValue(999.0);
        Point insertedExcluded = pointDAO.insert(excludedPoint);
        assertNotNull(insertedExcluded);
        cleanupPointIds.add(insertedExcluded.getId());

        // Тест 1: Ищем точки с X в списке [1.0, 3.0, 5.0],
        // исключая точку с ID=pointIds.get(1) (x=2.0) и insertedExcluded (x=2.0)
        // Важно: ID исключаются только если они УЖЕ найдены по xValues!
        // Точки с x=2.0 НЕ будут найдены вообще, поэтому их исключение не имеет значения
        List<Double> searchXValues = Arrays.asList(1.0, 3.0, 5.0);
        List<Integer> excludedIds = Arrays.asList(pointIds.get(1), insertedExcluded.getId());

        List<Point> result = pointDAO.findByFunctionIdAndXInAndIdNotIn(
                testFuncId, searchXValues, excludedIds);

        assertNotNull(result, "Результат не должен быть null");
        // Должны найтись 3 точки: с x=1.0 (ID=68), x=3.0 (ID=70), x=5.0 (ID=72)
        // Исключаемые точки с x=2.0 (ID=69 и 73) не найдены, так как x=2.0 нет в searchXValues
        assertEquals(3, result.size(), "Должны найтись 3 точки (x=1.0, 3.0, 5.0)");

        // Проверяем, что найденные точки имеют правильные X значения
        List<Double> foundXValues = result.stream()
                .map(Point::getXValue)
                .collect(Collectors.toList());
        assertTrue(foundXValues.contains(1.0), "Должна быть точка с X=1.0");
        assertTrue(foundXValues.contains(3.0), "Должна быть точка с X=3.0");
        assertTrue(foundXValues.contains(5.0), "Должна быть точка с X=5.0");
        assertFalse(foundXValues.contains(2.0), "Не должно быть точки с X=2.0 (не в searchXValues)");
        assertFalse(foundXValues.contains(4.0), "Не должно быть точки с X=4.0 (не в searchXValues)");

        // Тест 2: Теперь ищем точки с X в [2.0] и исключаем одну из них
        List<Double> searchXValues2 = Arrays.asList(2.0);
        List<Integer> excludedIds2 = Arrays.asList(pointIds.get(1)); // Исключаем первую точку с x=2.0

        List<Point> result2 = pointDAO.findByFunctionIdAndXInAndIdNotIn(
                testFuncId, searchXValues2, excludedIds2);

        assertNotNull(result2, "Результат не должен быть null");
        // Должна найтись 1 точка: вторая точка с x=2.0 (ID=73), так как первую исключили
        assertEquals(1, result2.size(), "Должна найтись 1 точка (вторая точка с x=2.0)");
        assertEquals(insertedExcluded.getId(), result2.get(0).getId(),
                "Должна быть вторая точка с x=2.0");
        assertEquals(2.0, result2.get(0).getXValue(), 0.001);

        // Тест 3: Ищем точки с X в [2.0] и исключаем обе
        List<Integer> excludedIds3 = Arrays.asList(pointIds.get(1), insertedExcluded.getId());

        List<Point> result3 = pointDAO.findByFunctionIdAndXInAndIdNotIn(
                testFuncId, searchXValues2, excludedIds3);

        assertNotNull(result3, "Результат не должен быть null");
        assertTrue(result3.isEmpty(), "Результат должен быть пустым (обе точки исключены)");

        // Тест 4: Пустой список X значений
        List<Point> resultEmptyX = pointDAO.findByFunctionIdAndXInAndIdNotIn(
                testFuncId, new ArrayList<>(), new ArrayList<>());
        assertNotNull(resultEmptyX, "Результат не должен быть null");
        assertTrue(resultEmptyX.isEmpty(), "Пустой список X - результат должен быть пустым");

        // Тест 5: Null список исключаемых ID
        List<Point> resultNullExcluded = pointDAO.findByFunctionIdAndXInAndIdNotIn(
                testFuncId, Arrays.asList(1.0, 2.0), null);
        assertNotNull(resultNullExcluded, "Результат не должен быть null");
        // Должны найтись точки с x=1.0 и x=2.0 (всего 3: 1.0, 2.0, 2.0)
        assertTrue(resultNullExcluded.size() >= 3,
                "Должны найтись все точки с x=1.0 и x=2.0 (3 точки)");

        logger.info("Тест findByFunctionIdAndXInAndIdNotIn выполнен успешно");
    }
    @Test
    @Order(24)
    void testUpdatePointsBatch() {
        logger.info("Тест: массовое обновление точек через UpdatePointCoordinate");

        // Создаем несколько точек для обновления
        int testFuncId = 2000;
        List<Point> originalPoints = new ArrayList<>();
        List<PointDAO.UpdatePointCoordinate> updates = new ArrayList<>();

        // Создаем 3 точки
        for (int i = 0; i < 3; i++) {
            Point point = new Point();
            point.setFunctionId(testFuncId);
            point.setXValue(i * 10.0);
            point.setYValue(i * 20.0);
            Point inserted = pointDAO.insert(point);
            assertNotNull(inserted);
            originalPoints.add(inserted);
            cleanupPointIds.add(inserted.getId());

            // Создаем обновления для этих точек
            PointDAO.UpdatePointCoordinate update = new PointDAO.UpdatePointCoordinate();
            update.setId(inserted.getId());
            update.setXValue(inserted.getXValue() + 100.0); // Смещаем X
            update.setYValue(inserted.getYValue() + 200.0); // Смещаем Y
            updates.add(update);
        }

        // Выполняем массовое обновление
        List<Point> updatedPoints = pointDAO.updatePointsBatch(testFuncId, updates);

        assertNotNull(updatedPoints, "Список обновленных точек не должен быть null");
        assertEquals(3, updatedPoints.size(), "Должны быть обновлены все 3 точки");

        // Проверяем, что точки действительно обновлены
        for (int i = 0; i < originalPoints.size(); i++) {
            Point original = originalPoints.get(i);
            PointDAO.UpdatePointCoordinate update = updates.get(i);

            Optional<Point> foundPointOpt = pointDAO.findById(original.getId());
            assertTrue(foundPointOpt.isPresent(), "Точка должна существовать после обновления");

            Point foundPoint = foundPointOpt.get();
            assertEquals(update.getXValue(), foundPoint.getXValue(), 0.001,
                    "X значение должно быть обновлено");
            assertEquals(update.getYValue(), foundPoint.getYValue(), 0.001,
                    "Y значение должно быть обновлено");
            assertEquals(testFuncId, foundPoint.getFunctionId(),
                    "FunctionId должен остаться прежним");
        }

        // Тест с точкой, не принадлежащей функции
        // Создаем точку в другой функции
        Point otherFunctionPoint = new Point();
        otherFunctionPoint.setFunctionId(testFuncId + 1);
        otherFunctionPoint.setXValue(500.0);
        otherFunctionPoint.setYValue(600.0);
        Point insertedOther = pointDAO.insert(otherFunctionPoint);
        assertNotNull(insertedOther);
        cleanupPointIds.add(insertedOther.getId());

        // Пытаемся обновить эту точку как принадлежащую testFuncId
        PointDAO.UpdatePointCoordinate wrongUpdate = new PointDAO.UpdatePointCoordinate();
        wrongUpdate.setId(insertedOther.getId());
        wrongUpdate.setXValue(700.0);
        wrongUpdate.setYValue(800.0);

        List<Point> updatedWithWrong = pointDAO.updatePointsBatch(testFuncId, Arrays.asList(wrongUpdate));
        assertNotNull(updatedWithWrong);
        assertTrue(updatedWithWrong.isEmpty(),
                "Точка другой функции не должна быть обновлена");

        // Проверяем, что точка другой функции не изменилась
        Optional<Point> stillOriginalOpt = pointDAO.findById(insertedOther.getId());
        assertTrue(stillOriginalOpt.isPresent());
        Point stillOriginal = stillOriginalOpt.get();
        assertEquals(500.0, stillOriginal.getXValue(), 0.001,
                "Точка другой функции не должна измениться");
        assertEquals(600.0, stillOriginal.getYValue(), 0.001,
                "Точка другой функции не должна измениться");

        logger.info("Тест updatePointsBatch выполнен успешно");
    }

    @Test
    @Order(25)
    void testUpdatePointCoordinateClass() {
        logger.info("Тест: внутренний класс UpdatePointCoordinate");

        // Тестируем конструкторы и геттеры/сеттеры
        PointDAO.UpdatePointCoordinate coord1 = new PointDAO.UpdatePointCoordinate();
        assertNull(coord1.getId());
        assertNull(coord1.getXValue());
        assertNull(coord1.getYValue());

        // Устанавливаем значения
        coord1.setId(123);
        coord1.setXValue(456.789);
        coord1.setYValue(987.654);

        assertEquals(Integer.valueOf(123), coord1.getId());
        assertEquals(456.789, coord1.getXValue(), 0.001);
        assertEquals(987.654, coord1.getYValue(), 0.001);

        // Тестируем конструктор с параметрами
        PointDAO.UpdatePointCoordinate coord2 = new PointDAO.UpdatePointCoordinate(456, 789.012, 345.678);
        assertEquals(Integer.valueOf(456), coord2.getId());
        assertEquals(789.012, coord2.getXValue(), 0.001);
        assertEquals(345.678, coord2.getYValue(), 0.001);

        logger.info("Тест внутреннего класса UpdatePointCoordinate выполнен успешно");
    }

    @Test
    @Order(26)
    void testFindAll() {
        logger.info("Тест: получение всех точек из базы данных");

        // Сначала очищаем все точки для этого теста
        pointDAO.deleteByFunctionId(testFunctionId);

        // Создаем несколько тестовых точек
        List<Point> createdPoints = new ArrayList<>();
        int numberOfPoints = 5;

        for (int i = 0; i < numberOfPoints; i++) {
            Point point = new Point();
            point.setFunctionId(testFunctionId);
            point.setXValue(i * 10.0);
            point.setYValue(i * 20.0);
            Point inserted = pointDAO.insert(point);
            assertNotNull(inserted, "Точка должна быть создана");
            assertNotNull(inserted.getId(), "ID должен быть установлен");
            createdPoints.add(inserted);
            cleanupPointIds.add(inserted.getId());
        }

        logger.info("Создано {} тестовых точек", createdPoints.size());

        // Вызываем метод findAll()
        List<Point> allPoints = pointDAO.findAll();

        // Проверяем результаты
        assertNotNull(allPoints, "Список всех точек не должен быть null");
        assertFalse(allPoints.isEmpty(), "Список всех точек не должен быть пустым");

        // Проверяем, что все созданные точки присутствуют в результате
        for (Point createdPoint : createdPoints) {
            boolean found = allPoints.stream()
                    .anyMatch(p -> p.getId().equals(createdPoint.getId()));
            assertTrue(found, String.format(
                    "Созданная точка с ID=%d должна присутствовать в результате findAll()",
                    createdPoint.getId()));
        }

        // Также можем проверить структуру точек
        for (Point point : allPoints) {
            assertNotNull(point.getId(), "ID точки не должен быть null");
            assertNotNull(point.getFunctionId(), "FunctionId не должен быть null");
            assertNotNull(point.getXValue(), "XValue не должен быть null");
            assertNotNull(point.getYValue(), "YValue не должен быть null");
        }

        logger.info("Метод findAll() вернул {} точек", allPoints.size());

        // Проверим, что количество точек в базе соответствует ожидаемому
        int totalCount = pointDAO.countAll();
        assertEquals(totalCount, allPoints.size(),
                "Количество точек в findAll() должно совпадать с countAll()");
    }

    @Test
    @Order(27)
    void testFindAllWhenEmpty() {
        logger.info("Тест: findAll() при пустой таблице точек");

        // Временно удаляем все точки для этого теста
        // Сначала сохраняем текущие ID для восстановления
        List<Point> currentPoints = pointDAO.findAll();
        List<Integer> currentIds = currentPoints.stream()
                .map(Point::getId)
                .collect(Collectors.toList());

        // Удаляем все точки
        if (!currentIds.isEmpty()) {
            int deleted = pointDAO.deleteByIds(currentIds);
            logger.info("Временно удалено {} точек для теста", deleted);
        }

        try {
            // Теперь таблица должна быть пустой
            List<Point> emptyResult = pointDAO.findAll();

            assertNotNull(emptyResult, "Результат не должен быть null даже при пустой таблице");
            assertTrue(emptyResult.isEmpty(), "Список должен быть пустым при пустой таблице");

            // Проверяем, что countAll() также возвращает 0
            int count = pointDAO.countAll();
            assertEquals(0, count, "countAll() должен возвращать 0 при пустой таблице");

            logger.info("Тест findAll() при пустой таблице выполнен успешно");
        } finally {
            // Восстанавливаем данные (если нужно)
            // В реальном тесте обычно используется транзакция или тестовая БД
            logger.info("Тест завершен, данные будут восстановлены другими тестами");
        }
    }

    @Test
    @Order(28)
    void testFindAllWithLargeDataset() {
        logger.info("Тест: findAll() с большим набором данных");

        // Создаем больше точек для проверки производительности
        int largeDatasetSize = 50; // Можно увеличить для стресс-теста

        List<Point> largeDataset = new ArrayList<>();
        for (int i = 0; i < largeDatasetSize; i++) {
            Point point = new Point();
            point.setFunctionId(testFunctionId);
            point.setXValue(Math.random() * 1000);
            point.setYValue(Math.random() * 1000);
            largeDataset.add(point);
        }

        // Массовая вставка
        int inserted = pointDAO.insertBatch(largeDataset);
        assertEquals(largeDatasetSize, inserted, "Все точки должны быть вставлены");

        // Сохраняем ID для очистки
        List<Point> insertedPoints = pointDAO.findByFunctionId(testFunctionId);
        insertedPoints.forEach(p -> cleanupPointIds.add(p.getId()));

        // Измеряем время выполнения
        long startTime = System.currentTimeMillis();
        List<Point> result = pointDAO.findAll();
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        assertNotNull(result);
        assertTrue(result.size() >= largeDatasetSize,
                "Результат должен содержать как минимум " + largeDatasetSize + " точек");

        logger.info("findAll() обработал {} точек за {} мс ({} точек/сек)",
                result.size(), duration,
                duration > 0 ? (result.size() * 1000 / duration) : "N/A");

        // Проверяем, что все вставленные точки присутствуют
        for (Point insertedPoint : insertedPoints) {
            boolean found = result.stream()
                    .anyMatch(p -> p.getId().equals(insertedPoint.getId()));
            assertTrue(found, "Вставленная точка должна быть в результате findAll()");
        }
    }
}
