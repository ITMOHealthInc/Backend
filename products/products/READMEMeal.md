## Модель приема пищи

Прием пищи имеет следующую структуру:

```json
{
    "id": 1,
    "type": "BREAKFAST",
    "addedAt": "2024-03-20T08:00:00",
    "username": "user123",
    "products": [
        {
            "id": 1,
            "name": "Яблоко",
            "affiliation": "USER",
            "water": 0.85,
            "mass": 100.0,
            "fiber": 2.4,
            "sugar": 10.4,
            "saturatedFat": 0.1,
            "salt": 0.0,
            "kbzhu": {
                "calories": 52.0,
                "proteins": 0.0,
                "fats": 0.1,
                "carbohydrates": 12.8
            }
        }
    ],
    "recipes": [
        {
            "id": 1,
            "name": "Фруктовый салат",
            "username": "user123",
            "products": [
                {
                    "id": 2,
                    "name": "Банан",
                    "affiliation": "GENERAL",
                    "water": 0.75,
                    "mass": 100.0,
                    "fiber": 2.6,
                    "sugar": 12.2,
                    "saturatedFat": 0.1,
                    "salt": 0.0,
                    "kbzhu": {
                        "calories": 89.0,
                        "proteins": 1.1,
                        "fats": 0.1,
                        "carbohydrates": 14.8
                    }
                }
            ]
        }
    ]
}
```

### Обязательные поля для создания/обновления
- `type`: Тип приема пищи (BREAKFAST, LUNCH, DINNER, SNACK)
- `productIds`: Массив ID продуктов, входящих в прием пищи
- `recipeIds`: Массив ID рецептов, входящих в прием пищи

## Конечные точки

### Создание приема пищи
Создает новый прием пищи. Все рецепты и продукты в приеме пищи должны принадлежать аутентифицированному пользователю.

**Конечная точка:** `POST /meals`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)
- `Content-Type`: application/json

**Тело запроса:**
```json
{
    "type": "BREAKFAST",
    "productIds": [1],
    "recipeIds": [1]
}
```

**Ответ (201 Created):**
```json
{
    "id": 1,
    "type": "BREAKFAST",
    "addedAt": "2024-03-20T08:00:00",
    "username": "user123",
    "products": [
        {
            "id": 1,
            "name": "Яблоко",
            "affiliation": "USER",
            "water": 0.85,
            "mass": 100.0,
            "fiber": 2.4,
            "sugar": 10.4,
            "saturatedFat": 0.1,
            "salt": 0.0,
            "kbzhu": {
                "calories": 52.0,
                "proteins": 0.0,
                "fats": 0.1,
                "carbohydrates": 12.8
            }
        }
    ],
    "recipes": [
        {
            "id": 1,
            "name": "Фруктовый салат",
            "username": "user123",
            "products": [
                {
                    "id": 2,
                    "name": "Банан",
                    "affiliation": "GENERAL",
                    "water": 0.75,
                    "mass": 100.0,
                    "fiber": 2.6,
                    "sugar": 12.2,
                    "saturatedFat": 0.1,
                    "salt": 0.0,
                    "kbzhu": {
                        "calories": 89.0,
                        "proteins": 1.1,
                        "fats": 0.1,
                        "carbohydrates": 14.8
                    }
                }
            ]
        }
    ]
}
```

**Пример curl:**
```bash
curl -X POST http://localhost:5022/meals \
  -H "X-User-ID: username" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "BREAKFAST",
    "productIds": [1],
    "recipeIds": [1]
  }'
```

### Получение всех приемов пищи
Получает все приемы пищи, созданные аутентифицированным пользователем.

**Конечная точка:** `GET /meals`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
```json
[
    {
        "id": 1,
        "type": "BREAKFAST",
        "addedAt": "2024-03-20T08:00:00",
        "username": "user123",
        "products": [
            {
                "id": 1,
                "name": "Яблоко",
                "affiliation": "USER",
                "water": 0.85,
                "mass": 100.0,
                "fiber": 2.4,
                "sugar": 10.4,
                "saturatedFat": 0.1,
                "salt": 0.0,
                "kbzhu": {
                    "calories": 52.0,
                    "proteins": 0.0,
                    "fats": 0.1,
                    "carbohydrates": 12.8
                }
            }
        ],
        "recipes": [
            {
                "id": 1,
                "name": "Фруктовый салат",
                "username": "user123",
                "products": [
                    {
                        "id": 2,
                        "name": "Банан",
                        "affiliation": "GENERAL",
                        "water": 0.75,
                        "mass": 100.0,
                        "fiber": 2.6,
                        "sugar": 12.2,
                        "saturatedFat": 0.1,
                        "salt": 0.0,
                        "kbzhu": {
                            "calories": 89.0,
                            "proteins": 1.1,
                            "fats": 0.1,
                            "carbohydrates": 14.8
                        }
                    }
                ]
            }
        ]
    }
]
```

**Пример curl:**
```bash
curl -X GET http://localhost:5022/meals \
  -H "X-User-ID: username"
```

### Получение приема пищи по ID
Получает конкретный прием пищи по его ID. Возвращает прием пищи только если он принадлежит аутентифицированному пользователю.

**Конечная точка:** `GET /meals/{id}`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
```json
{
    "id": 1,
    "type": "BREAKFAST",
    "addedAt": "2024-03-20T08:00:00",
    "username": "user123",
    "products": [
        {
            "id": 1,
            "name": "Яблоко",
            "affiliation": "USER",
            "water": 0.85,
            "mass": 100.0,
            "fiber": 2.4,
            "sugar": 10.4,
            "saturatedFat": 0.1,
            "salt": 0.0,
            "kbzhu": {
                "calories": 52.0,
                "proteins": 0.0,
                "fats": 0.1,
                "carbohydrates": 12.8
            }
        }
    ],
    "recipes": [
        {
            "id": 1,
            "name": "Фруктовый салат",
            "username": "user123",
            "products": [
                {
                    "id": 2,
                    "name": "Банан",
                    "affiliation": "GENERAL",
                    "water": 0.75,
                    "mass": 100.0,
                    "fiber": 2.6,
                    "sugar": 12.2,
                    "saturatedFat": 0.1,
                    "salt": 0.0,
                    "kbzhu": {
                        "calories": 89.0,
                        "proteins": 1.1,
                        "fats": 0.1,
                        "carbohydrates": 14.8
                    }
                }
            ]
        }
    ]
}
```

**Пример curl:**
```bash
curl -X GET http://localhost:5022/meals/1 \
  -H "X-User-ID: username"
```

### Обновление приема пищи
Обновляет существующий прием пищи. Разрешает обновление только владельцу приема пищи. Все рецепты и продукты в приеме пищи должны принадлежать аутентифицированному пользователю.

**Конечная точка:** `PUT /meals/{id}`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)
- `Content-Type`: application/json

**Тело запроса:**
```json
{
    "type": "LUNCH",
    "productIds": [1, 2],
    "recipeIds": [1, 2]
}
```

**Ответ (200 OK):**
```json
{
    "id": 1,
    "type": "LUNCH",
    "addedAt": "2024-03-20T08:00:00",
    "username": "user123",
    "products": [
        {
            "id": 1,
            "name": "Яблоко",
            "affiliation": "USER",
            "water": 0.85,
            "mass": 100.0,
            "fiber": 2.4,
            "sugar": 10.4,
            "saturatedFat": 0.1,
            "salt": 0.0,
            "kbzhu": {
                "calories": 52.0,
                "proteins": 0.0,
                "fats": 0.1,
                "carbohydrates": 12.8
            }
        },
        {
            "id": 2,
            "name": "Банан",
            "affiliation": "GENERAL",
            "water": 0.75,
            "mass": 100.0,
            "fiber": 2.6,
            "sugar": 12.2,
            "saturatedFat": 0.1,
            "salt": 0.0,
            "kbzhu": {
                "calories": 89.0,
                "proteins": 1.1,
                "fats": 0.1,
                "carbohydrates": 14.8
            }
        }
    ],
    "recipes": [
        {
            "id": 1,
            "name": "Фруктовый салат",
            "username": "user123",
            "products": [
                {
                    "id": 2,
                    "name": "Банан",
                    "affiliation": "GENERAL",
                    "water": 0.75,
                    "mass": 100.0,
                    "fiber": 2.6,
                    "sugar": 12.2,
                    "saturatedFat": 0.1,
                    "salt": 0.0,
                    "kbzhu": {
                        "calories": 89.0,
                        "proteins": 1.1,
                        "fats": 0.1,
                        "carbohydrates": 14.8
                    }
                }
            ]
        },
        {
            "id": 2,
            "name": "Овсяная каша",
            "username": "user123",
            "products": [
                {
                    "id": 3,
                    "name": "Овсяные хлопья",
                    "affiliation": "GENERAL",
                    "water": 0.1,
                    "mass": 100.0,
                    "fiber": 10.6,
                    "sugar": 1.0,
                    "saturatedFat": 1.2,
                    "salt": 0.0,
                    "kbzhu": {
                        "calories": 389.0,
                        "proteins": 13.2,
                        "fats": 6.5,
                        "carbohydrates": 66.3
                    }
                }
            ]
        }
    ]
}
```

**Пример curl:**
```bash
curl -X PUT http://localhost:5022/meals/1 \
  -H "X-User-ID: username" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "LUNCH",
    "productIds": [1, 2],
    "recipeIds": [1, 2]
  }'
```

### Удаление приема пищи
Удаляет прием пищи. Разрешает удаление только владельцу приема пищи.

**Конечная точка:** `DELETE /meals/{id}`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
Пустое тело ответа

**Пример curl:**
```bash
curl -X DELETE http://localhost:5022/meals/1 \
  -H "X-User-ID: username"
```

### Получение сводки приема пищи
Получает сводную информацию о приеме пищи, включая общее содержание воды и КБЖУ (калории, белки, жиры, углеводы) всех продуктов и рецептов в приеме пищи. Возвращает сводку только если прием пищи принадлежит аутентифицированному пользователю.

**Конечная точка:** `GET /meals/{id}/summary`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
```json
{
    "totalWater": 1.6,
    "totalKbzhu": {
        "calories": 141.0,
        "proteins": 1.1,
        "fats": 0.2,
        "carbohydrates": 27.6
    }
}
```

**Пример curl:**
```bash
curl -X GET http://localhost:5022/meals/1/summary \
  -H "X-User-ID: username"
```

### Получение дневной сводки приемов пищи
Получает агрегированную информацию о всех приемах пищи за конкретный день, включая суммарное содержание воды и КБЖУ (калории, белки, жиры, углеводы). Возвращает сводку только для приемов пищи, принадлежащих аутентифицированному пользователю.

**Конечная точка:** `GET /meals/daily-summary`

**Параметры запроса:**
- `date`: Дата в формате ISO (yyyy-MM-dd), например "2024-05-15". Если не указана, используется текущая дата.

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
```json
{
    "date": "2024-05-15",
    "totalWater": 5.2,
    "totalKbzhu": {
        "calories": 2341.0,
        "proteins": 82.5,
        "fats": 73.6,
        "carbohydrates": 295.8
    }
}
```

**Ответ (404 Not Found):**
```json
{
    "message": "No meals found for this date"
}
```

**Пример curl:**
```bash
curl -X GET "http://localhost:5022/meals/daily-summary?date=2024-05-15" \
  -H "X-User-ID: username"
```

**Пример curl для текущей даты:**
```bash
curl -X GET http://localhost:5022/meals/daily-summary \
  -H "X-User-ID: username"
```

### Получение месячной сводки приемов пищи
Получает агрегированную информацию о всех приемах пищи за конкретный месяц, включая суммарное содержание воды и КБЖУ (калории, белки, жиры, углеводы). Для каждого дня месяца создается отдельная сводка, даже если в этот день не было приемов пищи. Возвращает сводку только для приемов пищи, принадлежащих аутентифицированному пользователю.

**Конечная точка:** `GET /meals/monthly-summary`

**Параметры запроса:**
- `year`: Год в формате YYYY, например "2024". Если не указан, используется текущий год.
- `month`: Месяц в диапазоне 1-12, например "5" для мая. Если не указан, используется текущий месяц.

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
```json
{
    "year": 2024,
    "month": 5,
    "totalWater": 89.4,
    "totalKbzhu": {
        "calories": 56423.0,
        "proteins": 1845.2,
        "fats": 1568.3,
        "carbohydrates": 7521.8
    },
    "dailySummaries": [
        {
            "date": "2024-05-01",
            "totalWater": 5.2,
            "totalKbzhu": {
                "calories": 2341.0,
                "proteins": 82.5,
                "fats": 73.6,
                "carbohydrates": 295.8
            }
        },
        {
            "date": "2024-05-02",
            "totalWater": 0.0,
            "totalKbzhu": {
                "calories": 0.0,
                "proteins": 0.0,
                "fats": 0.0,
                "carbohydrates": 0.0
            }
        },
        {
            "date": "2024-05-31",
            "totalWater": 4.8,
            "totalKbzhu": {
                "calories": 1987.0,
                "proteins": 68.3,
                "fats": 45.2,
                "carbohydrates": 243.1
            }
        }
    ]
}
```

**Ответ (404 Not Found):**
```json
{
    "message": "No meals found for this month"
}
```

**Пример curl:**
```bash
curl -X GET "http://localhost:5022/meals/monthly-summary?year=2024&month=5" \
  -H "X-User-ID: username"
```

**Пример curl для текущего месяца:**
```bash
curl -X GET http://localhost:5022/meals/monthly-summary \
  -H "X-User-ID: username"
```

### Создание приема пищи с водой
Создает новый прием пищи типа SNACK, содержащий только воду. Количество воды округляется до ближайшего значения, кратного 50.

**Конечная точка:** `POST /meals/water`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)
- `Content-Type`: application/json

**Тело запроса:**
```json
{
    "waterAmount": 250
}
```

**Ответ (201 Created):**
```json
{
    "id": 1,
    "type": "SNACK",
    "addedAt": "2024-05-16T10:30:00",
    "username": "user123",
    "products": [
        {
            "id": 5,
            "name": "Вода",
            "affiliation": "GENERAL",
            "water": 250.0,
            "mass": 250.0,
            "fiber": 0.0,
            "sugar": 0.0,
            "saturatedFat": 0.0,
            "salt": 0.0,
            "kbzhu": {
                "calories": 0.0,
                "proteins": 0.0,
                "fats": 0.0,
                "carbohydrates": 0.0
            }
        }
    ],
    "recipes": []
}
```

**Пример curl:**
```bash
curl -X POST http://localhost:5022/meals/water \
  -H "X-User-ID: username" \
  -H "Content-Type: application/json" \
  -d '{
    "waterAmount": 250
  }'
```

**Примечания:**
- Входное значение waterAmount округляется до ближайшего числа, кратного 50
 