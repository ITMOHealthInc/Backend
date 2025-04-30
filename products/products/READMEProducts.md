## Модель продукта

Продукт имеет следующую структуру:

```json
{
    "id": 1,
    "name": "Название продукта",
    "affiliation": "USER|EUROPE|RUSSIA",
    "water": 0.5,
    "mass": 100.0,
    "proteins": 10.0,
    "fiber": 2.0,
    "sugar": 5.0,
    "addedSugar": 1.0,
    "saturatedFat": 1.0,
    "polyunsaturatedFat": 2.0,
    "cholesterol": 0.0,
    "salt": 0.1,
    "alcohol": 0.0,
    "vitaminB7": 0.0,
    "vitaminC": 0.0,
    "vitaminD": 0.0,
    "vitaminE": 0.0,
    "vitaminK": 0.0,
    "calcium": 0.0,
    "iron": 0.0,
    "zinc": 0.0,
    "kbzhu": {
        "calories": 100.0,
        "proteins": 10.0,
        "fats": 3.0,
        "carbohydrates": 7.0
    }
}
```

### Обязательные поля
- `name`: Название продукта
- `affiliation`: Одно из значений: "USER", "GENERAL"
- `mass`: Должно быть больше 0
- `water`: Должно быть неотрицательным

## Конечные точки

### Создание продукта
Создает новый продукт. Для продуктов с принадлежностью USER, продукт будет связан с аутентифицированным пользователем.

**Конечная точка:** `POST /products`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)
- `Content-Type`: application/json

**Тело запроса:**
```json
{
    "name": "Яблоко",
    "affiliation": "USER",
    "water": 0.85,
    "mass": 100.0,
    "fiber": 2.4,
    "sugar": 10.4,
    "saturatedFat": 0.1,
    "salt": 0.0
}
```

**Ответ (201 Created):**
```json
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
```

**Пример curl:**
```bash
curl -X POST http://localhost:5022/products \
  -H "X-User-ID: username" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Яблоко",
    "affiliation": "USER",
    "water": 0.85,
    "mass": 100.0,
    "fiber": 2.4,
    "sugar": 10.4,
    "saturatedFat": 0.1,
    "salt": 0.0
  }'
```

### Получение всех продуктов
Получает все продукты. Для продуктов с принадлежностью USER возвращает только продукты, принадлежащие аутентифицированному пользователю.

**Конечная точка:** `GET /products`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
```json
[
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
```

**Пример curl:**
```bash
curl -X GET http://localhost:5022/products \
  -H "X-User-ID: username"
```

### Получение продуктов пользователя
Получает все продукты, принадлежащие аутентифицированному пользователю.

**Конечная точка:** `GET /products/user`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
```json
[
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
```

**Пример curl:**
```bash
curl -X GET http://localhost:5022/products/user \
  -H "X-User-ID: username"
```

### Получение продукта по ID
Получает конкретный продукт по его ID. Для продуктов с принадлежностью USER возвращает продукт только если он принадлежит аутентифицированному пользователю.

**Конечная точка:** `GET /products/{id}`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
```json
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
```

**Пример curl:**
```bash
curl -X GET http://localhost:5022/products/1 \
  -H "X-User-ID: username"
```

### Обновление продукта
Обновляет существующий продукт. Для продуктов с принадлежностью USER разрешает обновление только владельцу продукта.

**Конечная точка:** `PUT /products/{id}`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)
- `Content-Type`: application/json

**Тело запроса:**
```json
{
    "name": "Обновленное яблоко",
    "affiliation": "USER",
    "water": 0.85,
    "mass": 100.0,
    "fiber": 2.4,
    "sugar": 10.4,
    "saturatedFat": 0.1,
    "salt": 0.0
}
```

**Ответ (200 OK):**
```json
{
    "id": 1,
    "name": "Обновленное яблоко",
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
```

**Пример curl:**
```bash
curl -X PUT http://localhost:5022/products/1 \
  -H "X-User-ID: username" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Обновленное яблоко",
    "affiliation": "USER",
    "water": 0.85,
    "mass": 100.0,
    "fiber": 2.4,
    "sugar": 10.4,
    "saturatedFat": 0.1,
    "salt": 0.0
  }'
```

### Удаление продукта
Удаляет продукт. Для продуктов с принадлежностью USER разрешает удаление только владельцу продукта.

**Конечная точка:** `DELETE /products/{id}`

**Заголовки:**
- `X-User-ID`: Имя пользователя аутентифицированного пользователя(только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
Пустое тело ответа

**Пример curl:**
```bash
curl -X DELETE http://localhost:5022/products/1 \
  -H "X-User-ID: username"
```
 