#!/bin/bash
# Скрипт для пересоздания БД с обновленными функциями

echo "Остановка контейнера PostgreSQL..."
docker-compose down

echo "Удаление тома с данными БД..."
docker volume rm infrastructure_repair_pgdata 2>/dev/null || echo "Том не найден или уже удален"

echo "Запуск контейнера с новой БД..."
docker-compose up -d

echo "Ожидание готовности БД..."
sleep 5

echo "Проверка подключения..."
until docker-compose exec -T postgres psql -U admin_user -d repair_department_db -c '\q' 2>/dev/null; do
    echo "Ожидание готовности БД..."
    sleep 2
done

echo "БД успешно пересоздана с обновленными функциями!"


