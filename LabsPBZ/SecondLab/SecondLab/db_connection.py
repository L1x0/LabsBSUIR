import psycopg2
from psycopg2.extras import RealDictCursor
import os

def get_db_connection():
    try:
        conn = psycopg2.connect(
            host="localhost",
            port=5432,
            database="repair_department_db",
            user="admin_user",
            password="dev"
        )
        return conn
    except Exception as e:
        raise Exception(f"Ошибка подключения к БД: {e}")

def execute_query(query, params=None, fetch=True):
    conn = get_db_connection()
    try:
        with conn.cursor(cursor_factory=RealDictCursor) as cur:
            cur.execute(query, params)
            if fetch:
                result = cur.fetchall()
                return [dict(row) for row in result]
            else:
                conn.commit()
                return None
    except Exception as e:
        conn.rollback()
        raise Exception(f"Ошибка выполнения запроса: {e}")
    finally:
        conn.close()

def execute_function(function_name, params=None):

    conn = get_db_connection()
    try:
        with conn.cursor() as cur:
            if params:
                cur.execute(f"SELECT {function_name}(%s)", (params,))
            else:
                cur.execute(f"SELECT {function_name}()")
            result = cur.fetchone()
            conn.commit()
            return result[0] if result else None
    except Exception as e:
        conn.rollback()
        raise Exception(f"Ошибка выполнения функции: {e}")
    finally:
        conn.close()


