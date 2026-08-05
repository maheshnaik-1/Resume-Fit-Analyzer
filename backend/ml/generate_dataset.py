import random
from pathlib import Path
import pandas as pd

ROLES = [
    "Software Engineer",
    "Java Developer",
    "Python Developer",
    "Backend Developer",
    "Frontend Developer",
    "Full Stack Developer",
    "React Developer",
    "Data Analyst",
    "Data Engineer",
    "Cloud Engineer",
    "DevOps Engineer",
    "Machine Learning Engineer",
    "AI Engineer",
    "Spring Boot Developer"
]

SKILLS = [
    "Java",
    "Python",
    "C",
    "C++",
    "HTML",
    "CSS",
    "JavaScript",
    "React",
    "Node.js",
    "FastAPI",
    "Spring Boot",
    "Hibernate",
    "REST API",
    "Microservices",
    "MySQL",
    "MongoDB",
    "SQL",
    "Git",
    "Docker",
    "Kubernetes",
    "CI/CD",
    "AWS",
    "Azure",
    "Cloud Computing",
    "Cloud Architecture",
    "Linux",
    "Networking",
    "Excel",
    "Power BI",
    "Data Visualization",
    "Statistics",
    "Pandas",
    "Machine Learning",
    "Deep Learning",
    "TensorFlow",
    "PyTorch",
    "NLP",
    "ETL",
    "Data Warehousing",
    "Data Processing",
    "OOP",
    "Problem Solving",
    "Algorithms",
    "Data Structures",
    "DSA",
    "Manual Testing",
    "Selenium",
    "JUnit",
    "Bug Tracking",
    "Swift",
    "SwiftUI",
    "UIKit",
    "Objective-C",
    "Xcode",
    "iOS Development",
    "Embedded C",
    "Microcontrollers",
    "RTOS",
    "Debugging",
    "Responsive Design",
    "Communication",
    "Requirement Analysis",
    "Documentation",
    "Cyber Security",
    "SIEM",
    "Risk Assessment",
    "Spark",
    "Bootstrap",
    "System Design"
]

ROLE_SKILLS = {

    "Software Engineer": [
        "Java", "Python", "C++", "OOP",
        "Algorithms", "Data Structures",
        "DSA", "Git", "SQL"
    ],

    "Java Developer": [
        "Java", "Spring Boot", "Hibernate",
        "REST API", "MySQL", "SQL",
        "Git", "OOP"
    ],

    "Python Developer": [
        "Python", "FastAPI",
        "REST API", "SQL",
        "Git", "OOP"
    ],

    "Backend Developer": [
        "Java", "Spring Boot",
        "FastAPI", "REST API",
        "SQL", "MySQL",
        "Docker", "Git"
    ],

    "Frontend Developer": [
        "HTML", "CSS",
        "JavaScript", "React",
        "Bootstrap",
        "Responsive Design",
        "Git"
    ],

    "Full Stack Developer": [
        "HTML", "CSS",
        "JavaScript", "React",
        "FastAPI", "SQL",
        "Git", "Docker"
    ],

    "React Developer": [
        "React",
        "HTML",
        "CSS",
        "JavaScript",
        "Bootstrap",
        "Git"
    ],

    "Data Analyst": [
        "Python",
        "SQL",
        "Excel",
        "Pandas",
        "Statistics",
        "Power BI",
        "Data Visualization"
    ],

    "Data Engineer": [
        "Python",
        "SQL",
        "ETL",
        "Spark",
        "Data Warehousing",
        "AWS",
        "Docker"
    ],

    "Cloud Engineer": [
        "AWS",
        "Azure",
        "Docker",
        "Kubernetes",
        "Linux",
        "Cloud Computing"
    ],

    "DevOps Engineer": [
        "Docker",
        "Kubernetes",
        "Linux",
        "AWS",
        "CI/CD",
        "Git"
    ],

    "Machine Learning Engineer": [
        "Python",
        "Machine Learning",
        "TensorFlow",
        "PyTorch",
        "Pandas",
        "Statistics"
    ],

    "AI Engineer": [
        "Python",
        "Machine Learning",
        "Deep Learning",
        "TensorFlow",
        "PyTorch",
        "NLP"
    ],

    "Spring Boot Developer": [
        "Java",
        "Spring Boot",
        "Hibernate",
        "REST API",
        "SQL",
        "Git"
    ]
}

def generate_resume(role):
    role_skills = ROLE_SKILLS[role]

    selected = random.sample(
        role_skills,
        random.randint(4, min(7, len(role_skills)))
    )

    extra = random.sample(
        [s for s in SKILLS if s not in selected],
        random.randint(1, 3)
    )

    skills = selected + extra

    row = {}

    for skill in SKILLS:
        row[skill] = 1 if skill in skills else 0

    row["Role"] = role

    return row

rows = []

for role in ROLES:
    for _ in range(120):
        rows.append(generate_resume(role))

df = pd.DataFrame(rows)

df = df.sample(frac=1).reset_index(drop=True)

dataset_path = (
    Path(__file__).resolve().parent.parent
    / "dataset"
    / "resumes_dataset.csv"
)

df.to_csv(
    dataset_path,
    index=False
)

print("Dataset generated successfully!")
print(f"Total resumes: {len(df)}")
print(f"Saved to: {dataset_path}")

