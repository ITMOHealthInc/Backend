## Модель рецепта

Рецепт имеет следующую структуру:

```json
{
    "id": 1,
    "name": "Название рецепта",
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
    ]
}
```

### Обязательные поля
- `name`: Название рецепта
- `productIds`: Массив ID продуктов, входящих в рецепт

## Конечные точки

### Создание рецепта
Создает новый рецепт. Все продукты в рецепте должны быть либо общие(GENERAL), либо принадлежать аутентифицированному пользователю(USER).

**Конечная точка:** `POST /recipes`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)
- `Content-Type`: application/json

**Тело запроса:**
```json
{
    "name": "Фруктовый салат",
    "productIds": [1, 2]
}
```

**Ответ (201 Created):**
```json
{
    "id": 1,
    "name": "Фруктовый салат",
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
    ]
}
```

**Пример curl:**
```bash
curl -X POST http://localhost:5022/recipes \
  -H "X-User-ID: username" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Фруктовый салат",
    "productIds": [1, 2]
  }'
```

### Получение всех рецептов
Получает все рецепты, созданные аутентифицированным пользователем.

**Конечная точка:** `GET /recipes`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
```json
[
    {
        "id": 1,
        "name": "Фруктовый салат",
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
        ]
    }
]
```

**Пример curl:**
```bash
curl -X GET http://localhost:5022/recipes \
  -H "X-User-ID: username"
```

### Получение рецепта по ID
Получает конкретный рецепт по его ID. Возвращает рецепт только если он принадлежит аутентифицированному пользователю.

**Конечная точка:** `GET /recipes/{id}`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
```json
{
    "id": 1,
    "name": "Фруктовый салат",
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
    ]
}
```

**Пример curl:**
```bash
curl -X GET http://localhost:5022/recipes/1 \
  -H "X-User-ID: username"
```

### Обновление рецепта
Обновляет существующий рецепт. Разрешает обновление только владельцу рецепта.

**Конечная точка:** `PUT /recipes/{id}`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)
- `Content-Type`: application/json

**Тело запроса:**
```json
{
    "name": "Обновленный фруктовый салат",
    "productIds": [1, 2, 3]
}
```

**Ответ (200 OK):**
```json
{
    "id": 1,
    "name": "Обновленный фруктовый салат",
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
        },
        {
            "id": 3,
            "name": "Апельсин",
            "affiliation": "USER",
            "water": 0.87,
            "mass": 100.0,
            "fiber": 2.4,
            "sugar": 8.2,
            "saturatedFat": 0.0,
            "salt": 0.0,
            "kbzhu": {
                "calories": 43.0,
                "proteins": 0.9,
                "fats": 0.0,
                "carbohydrates": 10.6
            }
        }
    ]
}
```

**Пример curl:**
```bash
curl -X PUT http://localhost:5022/recipes/1 \
  -H "X-User-ID: username" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Обновленный фруктовый салат",
    "productIds": [1, 2, 3]
  }'
```

### Удаление рецепта
Удаляет рецепт. Разрешает удаление только владельцу рецепта.

**Конечная точка:** `DELETE /recipes/{id}`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
Пустое тело ответа

**Пример curl:**
```bash
curl -X DELETE http://localhost:5022/recipes/1 \
  -H "X-User-ID: username"
```
