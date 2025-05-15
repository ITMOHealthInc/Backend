## Модель целей пользователя

Цель пользователя имеет следующую структуру:

```json
{
    "user_id": "user123",
    "goal_type": "WEIGHT_LOSS",
    "activity_level": "MEDIUM",
    "calorie_goal": 2000,
    "water_goal": 2500,
    "steps_goal": 8000,
    "proteins_goal": 120,
    "fats_goal": 40,
    "carbohydrates_goal": 150,
    "weight_goal": 75.5
}
```

**Важно:** Для одного пользователя может быть создана только одна цель. При попытке создать вторую цель будет возвращена ошибка. Идентификатор пользователя (`user_id`) является первичным ключом.

### Обязательные поля для создания/обновления
- `goal_type`: Тип цели (WEIGHT_LOSS, WEIGHT_GAIN, WEIGHT_MAINTENANCE)
- `activity_level`: Уровень активности (LOW, MEDIUM, HIGH)
- `weight_goal`: Целевой вес в килограммах, который хочет достичь пользователь

При создании или обновлении цели пользователя нужно указать тип цели, уровень активности и целевой вес. Остальные значения (калории, вода, шаги, белки, жиры, углеводы) будут рассчитаны автоматически на основе указанных параметров **с учетом целевого веса**. Например, количество белка рассчитывается как определенное количество грамм на каждый килограмм целевого веса.

## Конечные точки

### Создание цели пользователя
Создает новую цель для пользователя. Все дополнительные значения рассчитываются автоматически на основе указанных параметров и целевого веса. Если у пользователя уже есть цель, будет возвращена ошибка.

**Конечная точка:** `POST /user-goals`

**Заголовки:**
- `X-User-ID`: ID пользователя (только для тестов. В проде этот заголовок проставляется сам из токена)
- `Content-Type`: application/json

**Тело запроса:**
```json
{
    "goal_type": "WEIGHT_LOSS",
    "activity_level": "MEDIUM",
    "weight_goal": 75.5
}
```

**Ответ (201 Created):**
```json
{
    "user_id": "user123",
    "goal_type": "WEIGHT_LOSS",
    "activity_level": "MEDIUM",
    "calorie_goal": 2000,
    "water_goal": 2500,
    "steps_goal": 8000,
    "proteins_goal": 120,
    "fats_goal": 40,
    "carbohydrates_goal": 150,
    "weight_goal": 75.5
}
```

**Ответ (400 Bad Request) при попытке создать вторую цель:**
```json
{
    "message": "User already has a goal"
}
```

**Пример curl:**
```bash
curl -X POST http://localhost:5015/user-goals \
  -H "X-User-ID: user123" \
  -H "Content-Type: application/json" \
  -d '{
    "goal_type": "WEIGHT_LOSS",
    "activity_level": "MEDIUM",
    "weight_goal": 75.5
  }'
```

### Получение цели пользователя
Получает цель, принадлежащую аутентифицированному пользователю.

**Конечная точка:** `GET /user-goals`

**Заголовки:**
- `X-User-ID`: ID пользователя (только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (200 OK):**
```json
{
    "user_id": "user123",
    "goal_type": "WEIGHT_LOSS",
    "activity_level": "MEDIUM",
    "calorie_goal": 2000,
    "water_goal": 2500,
    "steps_goal": 8000,
    "proteins_goal": 120,
    "fats_goal": 40,
    "carbohydrates_goal": 150,
    "weight_goal": 75.5
}
```

**Ответ (404 Not Found) если у пользователя нет цели:**
```json
{
    "message": "Goal not found for user"
}
```

**Пример curl:**
```bash
curl -X GET http://localhost:5015/user-goals \
  -H "X-User-ID: user123"
```

### Обновление цели пользователя
Обновляет существующую цель пользователя. Все дополнительные значения пересчитываются автоматически на основе указанных параметров и нового целевого веса.

**Конечная точка:** `PUT /user-goals`

**Заголовки:**
- `X-User-ID`: ID пользователя (только для тестов. В проде этот заголовок проставляется сам из токена)
- `Content-Type`: application/json

**Тело запроса:**
```json
{
    "goal_type": "WEIGHT_MAINTENANCE",
    "activity_level": "HIGH",
    "weight_goal": 80.0
}
```

**Ответ (200 OK):**
```json
{
    "user_id": "user123",
    "goal_type": "WEIGHT_MAINTENANCE",
    "activity_level": "HIGH",
    "calorie_goal": 2800,
    "water_goal": 3000,
    "steps_goal": 12000,
    "proteins_goal": 90,
    "fats_goal": 65,
    "carbohydrates_goal": 250,
    "weight_goal": 80.0
}
```

**Пример curl:**
```bash
curl -X PUT http://localhost:5015/user-goals \
  -H "X-User-ID: user123" \
  -H "Content-Type: application/json" \
  -d '{
    "goal_type": "WEIGHT_MAINTENANCE",
    "activity_level": "HIGH",
    "weight_goal": 80.0
  }'
```

### Удаление цели пользователя
Удаляет цель пользователя.

**Конечная точка:** `DELETE /user-goals`

**Заголовки:**
- `X-User-ID`: ID пользователя (только для тестов. В проде этот заголовок проставляется сам из токена)

**Ответ (204 No Content):**
Пустое тело ответа

**Пример curl:**
```bash
curl -X DELETE http://localhost:5015/user-goals \
  -H "X-User-ID: user123"
```
