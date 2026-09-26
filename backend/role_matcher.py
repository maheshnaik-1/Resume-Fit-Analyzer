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
    ],
    "iOS Developer": [
        "Swift",
        "UIKit",
        "SwiftUI",
        "Xcode",
        "REST API",
        "Git"
    ],
    "Embedded Systems Engineer": [
        "C",
        "C++",
        "Embedded C",
        "Microcontrollers",
        "RTOS",
        "Debugging"
    ],
    "Cyber Security Analyst": [
        "Networking",
        "Linux",
        "Cyber Security",
        "SIEM",
        "Risk Assessment",
        "Python"
    ],
    "Testing Engineer": [
        "Manual Testing",
        "Selenium",
        "Java",
        "Bug Tracking",
        "JUnit",
        "Git"
    ]
}


def match_roles(detected_skills: list, top_n: int = 3) -> list:
    """
    Deterministically computes role-skill match percentage for detected resume skills
    against predefined role requirement profiles.
    """
    detected_set = set(detected_skills)
    scored_roles = []

    for role, required_skills in ROLE_SKILLS.items():
        if not required_skills:
            continue
        req_set = set(required_skills)
        overlap = detected_set & req_set
        match_score = round((len(overlap) / len(req_set)) * 100, 2)
        scored_roles.append({
            "role": role,
            "match_score": match_score
        })

    # Sort descending by match_score, then alphabetically by role name for stable ranking
    scored_roles.sort(key=lambda x: (-x["match_score"], x["role"]))

    return scored_roles[:top_n]
