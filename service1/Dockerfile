# Используем официальный образ Python
FROM python:3.9-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем зависимости
COPY . /app

# Устанавливаем зависимости
RUN pip install --no-cache-dir -r requirements.txt

# Устанавливаем Gunicorn
RUN pip install --no-cache-dir gunicorn

# Открываем порт, на котором будет работать сервис
EXPOSE 5001

# Команда для запуска приложения с использованием Gunicorn
CMD ["gunicorn", "--bind", "0.0.0.0:5001", "app:app"]