import sqlite3
import json

conn = sqlite3.connect("resume_analyzer.db")

cursor = conn.cursor()

cursor.execute("""
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL
)
""")

cursor.execute("""
CREATE TABLE IF NOT EXISTS resume_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT,
    company TEXT,
    role TEXT,
    ats_score REAL,
    analyzed_at TEXT,
    result_json TEXT
)
""")

cursor.execute("""
CREATE TABLE IF NOT EXISTS notifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT NOT NULL,
    message TEXT NOT NULL,
    type TEXT,
    is_read INTEGER DEFAULT 0,
    created_at TEXT
)
""")

conn.commit()
conn.close()

def save_resume_history(
    email,
    company,
    role,
    ats_score,
    analyzed_at,
    result_json
):
    conn = sqlite3.connect("resume_analyzer.db")
    cursor = conn.cursor()

    cursor.execute("""
    INSERT INTO resume_history
    (email, company, role, ats_score, analyzed_at, result_json)
    VALUES (?, ?, ?, ?, ?, ?)
    """, (
        email,
        company,
        role,
        ats_score,
        analyzed_at,
        result_json
    ))

    conn.commit()
    conn.close()

def get_resume_history(email):
    conn = sqlite3.connect("resume_analyzer.db")
    cursor = conn.cursor()

    cursor.execute("""
        SELECT id, company, role, ats_score, analyzed_at
        FROM resume_history
        WHERE email = ?
        ORDER BY analyzed_at DESC
    """, (email,))

    rows = cursor.fetchall()

    conn.close()

    history = []

    for row in rows:
        history.append({
            "id": row[0],
            "company": row[1],
            "role": row[2],
            "ats_score": row[3],
            "analyzed_at": row[4]
        })

    return history

def get_dashboard_stats(email):
    conn = sqlite3.connect("resume_analyzer.db")
    cursor = conn.cursor()

    cursor.execute("""
        SELECT ats_score, company
        FROM resume_history
        WHERE email = ?
    """, (email,))

    rows = cursor.fetchall()

    conn.close()

    if not rows:
        return {
            "total_analyses": 0,
            "highest_score": 0,
            "average_score": 0,
            "companies": 0
        }

    scores = [row[0] for row in rows]

    companies = len({
        row[1].strip().lower()
        for row in rows
        if row[1]
    })

    return {
        "total_analyses": len(rows),
        "highest_score": round(max(scores), 2),
        "average_score": round(sum(scores) / len(scores), 2),
        "companies": companies
    }

def get_best_resume_record(email):

    conn = sqlite3.connect("resume_analyzer.db")
    cursor = conn.cursor()

    cursor.execute(
        """
        SELECT company,
               role,
               ats_score,
               analyzed_at
        FROM resume_history
        WHERE email=?
        ORDER BY ats_score DESC
        LIMIT 1
        """,
        (email,)
    )

    row = cursor.fetchone()

    conn.close()

    if not row:
        return None

    return {
        "company": row[0],
        "role": row[1],
        "ats_score": row[2],
        "date": row[3],
    }

def get_resume_improvement(email, company, role):
    conn = sqlite3.connect("resume_analyzer.db")
    cursor = conn.cursor()

    cursor.execute("""
    SELECT ats_score, analyzed_at
    FROM resume_history
    WHERE email = ?
    AND company = ?
    AND role = ?
    ORDER BY analyzed_at DESC
    LIMIT 3
    """, (
        email,
        company,
        role
    ))

    rows = cursor.fetchall()
    conn.close()

    if len(rows) == 0:
        return None

    rows.reverse()

    history = [
        {
            "score": row[0],
            "date": row[1]
        }
        for row in rows
    ]

    scores = [item["score"] for item in history]

    return {
        "history": history,
        "first_score": scores[0],
        "latest_score": scores[-1],
        "improvement": round(scores[-1] - scores[0], 2)
    }

def create_user(name, email, password):
    conn = sqlite3.connect("resume_analyzer.db")
    cursor = conn.cursor()

    cursor.execute("""
        INSERT INTO users (name, email, password)
        VALUES (?, ?, ?)
    """, (name, email, password))

    conn.commit()
    conn.close()

def user_exists(email):
    conn = sqlite3.connect("resume_analyzer.db")
    cursor = conn.cursor()

    cursor.execute(
        "SELECT id FROM users WHERE email = ?",
        (email,)
    )

    result = cursor.fetchone()

    conn.close()

    return result is not None

def get_user_by_email(email):
    conn = sqlite3.connect("resume_analyzer.db")
    cursor = conn.cursor()

    cursor.execute("""
        SELECT name, email, password
        FROM users
        WHERE email = ?
    """, (email,))

    user = cursor.fetchone()

    conn.close()

    return user

def save_notification(email, message, notification_type, created_at):
    conn = sqlite3.connect("resume_analyzer.db")
    cursor = conn.cursor()

    cursor.execute("""
        INSERT INTO notifications
        (email, message, type, created_at)
        VALUES (?, ?, ?, ?)
    """, (
        email,
        message,
        notification_type,
        created_at
    ))

    conn.commit()
    conn.close()

def get_notifications(email):
    conn = sqlite3.connect("resume_analyzer.db")
    cursor = conn.cursor()

    cursor.execute("""
        SELECT message, type, created_at, is_read
        FROM notifications
        WHERE email = ?
        ORDER BY created_at DESC
        LIMIT 10
    """, (email,))

    rows = cursor.fetchall()

    conn.close()

    notifications = []

    for row in rows:
        notifications.append({
            "message": row[0],
            "type": row[1],
            "created_at": row[2],
            "is_read": row[3]
        })

    return notifications

def mark_notifications_as_read(email):
    conn = sqlite3.connect("resume_analyzer.db")
    cursor = conn.cursor()

    cursor.execute("""
        UPDATE notifications
        SET is_read = 1
        WHERE email = ?
    """, (email,))

    conn.commit()
    conn.close()

def delete_resume_history(history_id):
    conn = sqlite3.connect("resume_analyzer.db")
    cursor = conn.cursor()

    cursor.execute("""
        DELETE FROM resume_history
        WHERE id = ?
    """, (history_id,))

    conn.commit()
    conn.close()

def get_history_result(history_id):
    conn = sqlite3.connect("resume_analyzer.db")
    cursor = conn.cursor()

    cursor.execute("""
        SELECT result_json
        FROM resume_history
        WHERE id = ?
    """, (history_id,))

    row = cursor.fetchone()

    conn.close()

    if not row:
        return None

    return json.loads(row[0])