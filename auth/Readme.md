# Микросервис аутентификации и профиля пользователя

> Базовый путь: `/auth`  
> Запросы, требующие аутентификации: заголовок `Authorization: Bearer <токен>`

---

## Эндпоинты

### 1. Регистрация пользователя

- **Метод:** `POST`  
- **URL:** `/register`  
- **Аутентификация:** Не требуется

#### Пример запроса
```bash
curl -X POST http://localhost/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user123",
    "password": "securePass123",
    "name": "John Doe"
  }'
```

#### Тело запроса

| Поле                 | Тип        | Обязательно | Описание                          |
|----------------------|------------|-------------|-----------------------------------|
| `username`           | `string`   | Да          | Уникальный никнейм (мин. 3 символа) |
| `password`           | `string`   | Да          | Пароль (мин. 6 символов)          |
| `name`               | `string`   | Да          | Реальное имя пользователя         |

#### Успешный ответ `201 Created`
```json
{
  "message": "User registered successfully"
}
```

#### Возможные ошибки
- `400 Bad Request` — невалидные данные (короткий username/password, пустое name)
- `409 Conflict` — пользователь уже существует
- `500 Internal Server Error` — ошибка сервера

---

### 2. Авторизация

- **Метод:** `POST`  
- **URL:** `/login`  
- **Аутентификация:** Не требуется

#### Пример запроса
```bash
curl -X POST http://localhost/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user123",
    "password": "securePass123"
  }'
```

#### Тело запроса

| Поле       | Тип      | Описание   |
|------------|----------|------------|
| `username` | `string` | Никнейм    |
| `password` | `string` | Пароль     |

#### Успешный ответ `200 OK`
```json
{
  "token": "jwt.token.here"
}
```

#### Ошибки
- `401 Unauthorized` — неверные учетные данные

---

### 3. Валидация токена

- **Метод:** `GET`  
- **URL:** `/validate`  
- **Аутентификация:** Требуется (JWT)

#### Пример запроса
```bash
curl -X GET http://localhost/validate \
  -H "Authorization: Bearer <токен>"
```

#### Успешный ответ `200 OK`
```json
{
  "username": "user123",
  "message": "Token is valid"
}
```

#### Заголовок ответа
- `X-User-ID` — имя пользователя

#### Ошибки
- `401 Unauthorized` — невалидный токен

---

### 4. Получение профиля

- **Метод:** `GET`  
- **URL:** `/profile`  
- **Аутентификация:** Требуется

#### Пример запроса
```bash
curl -X GET http://localhost/profile \
  -H "Authorization: Bearer <токен>"
```

#### Успешный ответ `200 OK`
```json
{
  "username": "user123",
  "name": "John Doe",
  "profilePictureUrl": "/profile/image/filename.jpg"
}
```

---

### 5. Обновление профиля

- **Метод:** `PUT`  
- **URL:** `/profile`  
- **Аутентификация:** Требуется

#### Пример запроса
```bash
curl -X PUT http://localhost/profile \
  -H "Authorization: Bearer <токен>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Smith",
    "profilePictureBase64": "newbase64image..."
  }'
```

#### Тело запроса

| Поле                 | Тип      | Описание               |
|----------------------|----------|------------------------|
| `name`               | `string` | Новое имя (опционально) |
| `profilePictureBase64` | `string` | Новое фото (опционально) |

#### Успешный ответ `200 OK`
```json
{
  "message": "Profile updated successfully",
  "profilePictureUrl": "/profile/image/newfilename.jpg"
}
```

---

### 6. Получение изображения профиля

- **Метод:** `GET`  
- **URL:** `/profile/image/{filename}`  
- **Аутентификация:** Требуется

#### Пример запроса
```bash
curl -X GET http://localhost/profile/image/filename.jpg \
  -H "Authorization: Bearer <токен>"
```

#### Успешный ответ `200 OK`
- Бинарные данные изображения (Content-Type: image/jpeg)

#### Ошибки
- `404 Not Found` — изображение не существует

---

## Модели данных

```kotlin
// Регистрация
data class RegisterRequest(
    val username: String,
    val password: String,
    val name: String,
    val profilePictureBase64: String? = null
)

// Авторизация
data class LoginRequest(
    val username: String,
    val password: String
)

// Ответ с токеном
data class AuthResponse(
    val token: String
)

// Ответ об ошибке
data class ErrorResponse(
    val message: String
)

// Валидация токена
data class ValidateResponse(
    val username: String,
    val message: String = "Token is valid"
)

// Обновление профиля
data class UpdateProfileRequest(
    val name: String? = null,
    val profilePictureBase64: String? = null
)
```
