| # | Запрос | Среднее (мс) | Мин (мс) | Макс (мс) | Примечание |
|---|--------|--------------|----------|-----------|------------|
| 1 | POST /functions (создание базовой) | N/A (API не запущено) | — | — | Заполните после запуска Newman |
| 2 | POST /functions (вторая функция) | N/A | — | — |  |
| 3 | GET /functions/{id} | N/A | — | — |  |
| 4 | PUT /functions/{id} | N/A | — | — |  |
| 5 | GET /functions/owner/{ownerId} | N/A | — | — |  |
| 6 | GET /functions/owner/{ownerId}/type/{typeId} | N/A | — | — |  |
| 7 | GET /functions?userId=... | N/A | — | — |  |
| 8 | GET /functions?withDetails=true | N/A | — | — |  |
| 9 | POST /functions/{id}/operations/binary | N/A | — | — |  |
|10 | POST /functions/{id}/derive | N/A | — | — |  |
|11 | DELETE operationResultId | N/A | — | — |  |
|12 | DELETE derivativeResultId | N/A | — | — |  |
|13 | DELETE secondFunctionId | N/A | — | — |  |
|14 | DELETE functionId | N/A | — | — |  |

> Таблица заполняется по данным `reports/newman-results.json`, которые появляются после запуска тестов.

