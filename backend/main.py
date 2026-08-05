import re
from fastapi import FastAPI, UploadFile, File, Form
from fastapi.middleware.cors import CORSMiddleware
from PyPDF2 import PdfReader
from job_data import COMPANY_SUGGESTIONS
from ml.predict import predict_role
from database import (
    user_exists,
    create_user,
    get_user_by_email,
    save_resume_history,
    get_resume_history,
    get_dashboard_stats,
    get_best_resume_record,
    get_resume_improvement,
    save_notification,
    get_notifications,
    mark_notifications_as_read,
    delete_resume_history,
    get_history_result
)
import json
import io
import time
from fastapi import HTTPException
from datetime import datetime
from pydantic import BaseModel
import bcrypt

class UserSignup(BaseModel):
    name: str
    email: str
    password: str


class UserLogin(BaseModel):
    email: str
    password: str

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def home():
    return {"message": "Backend Working Successfully"}

@app.post("/signup")
def signup(user: UserSignup):

    hashed_password = bcrypt.hashpw(
        user.password.encode("utf-8"),
        bcrypt.gensalt()
    )

    if user_exists(user.email):
        raise HTTPException(
            status_code=400,
            detail="Email already exists"
        )

    create_user(
        user.name,
        user.email,
        hashed_password.decode("utf-8")
    )

    return {
        "message": "User registered successfully"
    }
    

@app.post("/login")
def login(user: UserLogin):

    result = get_user_by_email(user.email)

    if result is None:
        return {
            "success": False,
            "message": "User not found"
        }

    name = result[0]
    email = result[1]
    stored_password = result[2]

    if bcrypt.checkpw(
        user.password.encode("utf-8"),
        stored_password.encode("utf-8")
    ):
        return {
            "success": True,
            "message": "Login successful",
            "name": name,
            "email": email
        }

    return {
        "success": False,
        "message": "Incorrect password"
    }


@app.post("/upload")
async def upload_resume(
    file: UploadFile = File(...),
    job_description: str = Form(...),
    email: str = Form(...),
    company: str = Form(...),
    role: str = Form(...)
):
    start_time = time.perf_counter()

    # Read PDF
    pdf_bytes = await file.read()

    pdf_reader = PdfReader(io.BytesIO(pdf_bytes))

    text = ""

    for page in pdf_reader.pages:
        extracted_text = page.extract_text()

        if extracted_text:
            text += extracted_text

    # =========================
    # Skills Database
    # =========================

    skills_database = [
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

    # Skill Aliases
    skill_aliases = {

        # Programming Languages
        "Java": ["Java", "Core Java", "Java SE"],
        "Python": ["Python", "Python3", "Python Programming"],
        "C": ["C", "C Language"],
        "C++": ["C++", "CPP", "C Plus Plus"],
        "JavaScript": ["JavaScript", "JS", "ECMAScript"],

        # Frontend
        "HTML": ["HTML", "HTML5"],
        "CSS": ["CSS", "CSS3"],
        "React": ["React", "ReactJS", "React.js"],

        # Backend
        "Node.js": ["Node.js", "NodeJS"],
        "FastAPI": ["FastAPI", "Fast API"],
        "Spring Boot": ["Spring Boot", "Spring", "SpringBoot"],
        "Hibernate": ["Hibernate", "Hibernate ORM"],
        "REST API": ["REST API", "REST APIs", "RESTful API", "RESTful APIs"],

        # Database
        "MySQL": ["MySQL", "My SQL"],
        "MongoDB": ["MongoDB", "Mongo DB"],
        "SQL": ["SQL", "Structured Query Language"],

        # Version Control
        "Git": ["Git", "GitHub", "Github"],
        "Docker": ["Docker", "Docker Container"],
        "Kubernetes": ["Kubernetes", "K8s"],
        "CI/CD": ["CI/CD", "CI CD", "Continuous Integration"],

        # Cloud
        "AWS": [
            "AWS",
            "Amazon Web Services",
            "EC2",
            "S3",
            "Lambda"
        ],
        "Azure": [
            "Azure",
            "Microsoft Azure"
        ],
        "Cloud Computing": [
            "Cloud Computing",
            "Cloud"
        ],
        "Cloud Architecture": [
            "Cloud Architecture",
            "Cloud Architect"
        ],

        # Data Science
        "Pandas": ["Pandas", "Pandas Library"],
        "Statistics": ["Statistics", "Statistical Analysis"],
        "Power BI": ["Power BI", "PowerBI"],
        "Data Visualization": [
            "Data Visualization",
            "Visualization"
        ],

        # AI
        "Machine Learning": [
            "Machine Learning",
            "ML"
        ],
        "Deep Learning": [
            "Deep Learning",
            "DL"
        ],
        "TensorFlow": [
            "TensorFlow",
            "Tensor Flow"
        ],
        "PyTorch": [
            "PyTorch",
            "Torch"
        ],
        "NLP": [
            "NLP",
            "Natural Language Processing"
        ],

        # Data Engineering
        "ETL": [
            "ETL",
            "Extract Transform Load"
        ],
        "Data Warehousing": [
            "Data Warehouse",
            "Data Warehousing"
        ],
        "Data Processing": [
            "Data Processing",
            "Data Pipeline"
        ],

        # CS Fundamentals
        "OOP": [
            "OOP",
            "Object Oriented Programming"
        ],
        "Algorithms": [
            "Algorithms",
            "Algorithm Design"
        ],
        "Data Structures": [
            "Data Structures",
            "DS"
        ],
        "DSA": [
            "DSA",
            "Data Structures and Algorithms"
        ],
        "Problem Solving": [
            "Problem Solving",
            "Analytical Thinking"
        ],

        # Testing
        "Manual Testing": [
            "Manual Testing",
            "Software Testing"
        ],
        "Selenium": [
            "Selenium",
            "Selenium WebDriver"
        ],
        "JUnit": [
            "JUnit",
            "JUnit5"
        ],
        "Bug Tracking": [
            "Bug Tracking",
            "Bug Fixing"
        ],

        # iOS
        "Swift": [
            "Swift",
            "Swift Language"
        ],
        "SwiftUI": [
            "SwiftUI"
        ],
        "UIKit": [
            "UIKit"
        ],
        "Objective-C": [
            "Objective-C",
            "Objective C"
        ],
        "Xcode": [
            "Xcode"
        ],
        "iOS Development": [
            "iOS Development",
            "iOS"
        ],

        # Embedded
        "Embedded C": [
            "Embedded C",
            "Embedded-C"
        ],
        "Microcontrollers": [
            "Microcontroller",
            "Microcontrollers",
            "Arduino",
            "ESP32",
            "ESP8266",
            "STM32",
            "8051",
            "PIC"
        ],
        "RTOS": [
            "RTOS",
            "FreeRTOS",
            "Real Time Operating System"
        ],
        "Debugging": [
            "Debugging",
            "Debug"
        ],

        # Misc
        "Responsive Design": [
            "Responsive Design",
            "Responsive UI"
        ],
        "Communication": [
            "Communication",
            "Communication Skills"
        ],
        "Requirement Analysis": [
            "Requirement Analysis",
            "Requirements Gathering"
        ],
        "Documentation": [
            "Documentation",
            "Technical Documentation"
        ],
        "Cyber Security": [
            "Cyber Security",
            "Cybersecurity"
        ],
        "SIEM": [
            "SIEM",
            "Security Information and Event Management"
        ],
        "Risk Assessment": [
            "Risk Assessment",
            "Risk Analysis"
        ],
        "Spark": [
            "Spark",
            "Apache Spark"
        ],
        "Bootstrap": [
            "Bootstrap",
            "Bootstrap Framework"
        ],
        "System Design": [
            "System Design",
            "Software Architecture"
        ]
    }

    detected_skills = set()

    for skill in skills_database:

        aliases = skill_aliases.get(skill, [skill])

        for alias in aliases:
            pattern = rf'(?<!\w){re.escape(alias)}(?!\w)'

            if re.search(pattern, text, re.IGNORECASE):
                detected_skills.add(skill)
                break
    detected_skills = sorted(list(detected_skills))

    # =========================
    # Education Database
    # =========================

    education_database = [
        "B.Tech",
        "Bachelor of Technology",
        "Computer Science Engineering",
        "Computer Science",
        "CSE",
        "Information Technology",
        "IT",
        "M.Tech",
        "B.Sc",
        "BCA",
        "MCA",
        "Diploma"
    ]

    detected_education = []

    # Degree
    if re.search(r"\b(B\.?Tech|Bachelor of Technology)\b", text, re.I):
        detected_education.append("B.Tech")

    elif re.search(r"\bM\.?Tech\b", text, re.I):
        detected_education.append("M.Tech")

    elif re.search(r"\bBCA\b", text, re.I):
        detected_education.append("BCA")

    elif re.search(r"\bMCA\b", text, re.I):
        detected_education.append("MCA")

    elif re.search(r"\bB\.?Sc\b", text, re.I):
        detected_education.append("B.Sc")

    elif re.search(r"\bDiploma\b", text, re.I):
        detected_education.append("Diploma")

    # Branch
    if re.search(r"\b(Computer Science Engineering|CSE)\b", text, re.I):
        detected_education.append("Computer Science Engineering")

    elif re.search(r"\bComputer Science\b", text, re.I):
        detected_education.append("Computer Science")

    elif re.search(r"\b(Information Technology|IT)\b", text, re.I):
        detected_education.append("Information Technology")

    # =========================
    # Certification Database
    # =========================

    certification_database = [
        "Java Programming",
        "Python Programming",
        "Generative AI",
        "AWS",
        "Azure",
        "Google Cloud",
        "Machine Learning",
        "Data Science"
    ]

    detected_certifications = []

    for cert in certification_database:
        if cert.lower() in text.lower():
            detected_certifications.append(cert)

    # =========================
    # Project Database
    # =========================

    project_keywords = [
        "project",
        "projects",
        "academic project",
        "personal project",
        "mini project",
        "major project"
    ]

    project_count = 0

    for keyword in project_keywords:
        project_count += len(re.findall(keyword, text, re.IGNORECASE))

    project_count = min(project_count, 3)

    detected_projects = [
        f"{project_count} Project{'s' if project_count != 1 else ''} Detected"
    ] if project_count > 0 else []

    # =========================
    # Job Skills Detection
    # =========================

    job_skills = []

    for skill in skills_database:
        pattern = rf'(?<!\w){re.escape(skill)}(?!\w)'

        if re.search(
            pattern,
            job_description,
            re.IGNORECASE
        ):
            job_skills.append(skill)

    # =========================
    # Match Skills
    # =========================

    matched_skills = []
    missing_skills = []

    for skill in job_skills:
        if skill in detected_skills:
            matched_skills.append(skill)
        else:
            missing_skills.append(skill)

    # =========================
    # ATS Score Breakdown
    # =========================

    # Skills Score
    if len(job_skills) > 0:
        skills_score = round((len(matched_skills) / len(job_skills)) * 100)
    else:
        skills_score = 0

    # Projects Score
    if len(detected_projects) >= 3:
        projects_score = 100
    elif len(detected_projects) == 2:
        projects_score = 75
    elif len(detected_projects) == 1:
        projects_score = 50
    else:
        projects_score = 0

    # Education Score
    education_score = 100 if detected_education else 0

    # Certification Score
    if len(detected_certifications) >= 3:
        certification_score = 100
    elif len(detected_certifications) == 2:
        certification_score = 75
    elif len(detected_certifications) == 1:
        certification_score = 50
    else:
        certification_score = 0

    # Final ATS Score
    ats_score = round(
        skills_score * 0.70 +
        projects_score * 0.15 +
        education_score * 0.10 +
        certification_score * 0.05,
        2
    )

    # # =========================
    # # Recommended Roles
    # # =========================

    # recommended_roles = []

    # # Java
    # if "Java" in detected_skills:
    #     recommended_roles.append("Java Developer")

    # if "Java" in detected_skills and "SQL" in detected_skills:
    #     recommended_roles.append("Backend Developer")

    # if "Java" in detected_skills and "Spring Boot" in detected_skills:
    #     recommended_roles.append("Spring Boot Developer")


    # # Python
    # if "Python" in detected_skills:
    #     recommended_roles.append("Python Developer")

    # if "Python" in detected_skills and "SQL" in detected_skills:
    #     recommended_roles.append("Data Analyst")

    # if "Python" in detected_skills and "Machine Learning" in detected_skills:
    #     recommended_roles.append("Machine Learning Engineer")

    # if (
    #     "Python" in detected_skills and
    #     ("TensorFlow" in detected_skills or "PyTorch" in detected_skills)
    # ):
    #     recommended_roles.append("AI Engineer")


    # # Frontend
    # if (
    #     "HTML" in detected_skills and
    #     "CSS" in detected_skills and
    #     "JavaScript" in detected_skills
    # ):
    #     recommended_roles.append("Frontend Developer")

    # if "React" in detected_skills:
    #     recommended_roles.append("React Developer")


    # # Full Stack
    # if (
    #     "React" in detected_skills and
    #     "FastAPI" in detected_skills
    # ):
    #     recommended_roles.append("Full Stack Developer")


    # # Backend
    # if "FastAPI" in detected_skills:
    #     recommended_roles.append("Backend Developer")


    # # General Software Engineer
    # if len(detected_skills) >= 6:
    #     recommended_roles.append("Software Engineer")


    # # Remove duplicates
    # recommended_roles = list(dict.fromkeys(recommended_roles))

    # # Keep only top 5
    # recommended_roles = recommended_roles[:5]

    # =========================
    # Suggestions
    # =========================
    suggestions = []

    prediction = predict_role(
        detected_skills,
        skills_database
    )

    predicted_role = prediction["role"]
    prediction_confidence = prediction["confidence"]
    top_predictions = prediction["top_predictions"]

    recommended_roles = [predicted_role]

    if prediction_confidence < 60:
        suggestions.append(
            "Consider adding more role-specific skills to improve prediction confidence."
        )
    
    # ML-based recommendation

    suggestions.append(
        f"Your resume is best suited for the '{predicted_role}' role according to the trained Machine Learning model."
    )

    # Missing Skills
    for skill in missing_skills:
        suggestions.append(f"Learn {skill} through practical projects.")

    # Low ATS
    if ats_score < 60:
        suggestions.append(
            "Build 1-2 projects related to your target job role."
        )
        suggestions.append(
            "Tailor your resume for each job application."
        )

    # Medium ATS
    elif ats_score < 80:
        suggestions.append(
            "Strengthen your technical skills with certifications."
        )
        suggestions.append(
            "Add measurable achievements to your projects."
        )

    # Resume Quality
    if len(detected_projects) < 2:
        suggestions.append(
            "Include more personal or academic projects."
        )

    if len(detected_certifications) == 0:
        suggestions.append(
            "Earn at least one relevant certification."
        )

    suggestions.append(
        "Improve resume keywords for better ATS compatibility."
    )

    suggestions.append(
        "Keep your GitHub and LinkedIn profiles updated."
    )

    company_name = company.strip().lower()
    role_name = role.strip().lower()

    if company_name in COMPANY_SUGGESTIONS:
        if role_name in COMPANY_SUGGESTIONS[company_name]:
            suggestions.extend(
                COMPANY_SUGGESTIONS[company_name][role_name]
            )

    save_notification(
        email=email,
        message=f"Resume analyzed for {company.strip().title()} - {role}",
        notification_type="analysis",
        created_at=datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    )

    analysis_time = round(
        time.perf_counter() - start_time,
        2
    )

    resume_health = []

    if skills_score >= 80:
        resume_health.append("🟢 Strong technical skills")
    elif skills_score >= 50:
        resume_health.append("🟡 Skills section can be improved")
    else:
        resume_health.append("🔴 Add more relevant technical skills")

    if projects_score >= 80:
        resume_health.append("🟢 Excellent project portfolio")
    elif projects_score >= 50:
        resume_health.append("🟡 Add one stronger project")
    else:
        resume_health.append("🔴 Projects section is weak")

    if education_score >= 80:
        resume_health.append("🟢 Education section looks complete")
    else:
        resume_health.append("🟡 Improve education details")

    if certification_score >= 80:
        resume_health.append("🟢 Certifications strengthen your profile")
    elif certification_score >= 50:
        resume_health.append("🟡 Add more certifications")
    else:
        resume_health.append("🔴 Certifications are missing")

    

    # =========================
    # Return Output
    # =========================

    result = {
    "filename": file.filename,

    "resume_summary": {
        "education": detected_education,
        "skills": detected_skills,
        "projects": detected_projects,
        "certifications": detected_certifications
    },
    "job_skills": job_skills,
    "matched_skills": matched_skills,
    "missing_skills": missing_skills,
    "ats_score": round(ats_score, 2),
    "score_breakdown": {
        "skills": skills_score,
        "projects": projects_score,
        "education": education_score,
        "certifications": certification_score
    },
    "recommended_roles": recommended_roles,
    "prediction_confidence": prediction_confidence,
    "top_predictions": top_predictions,
    "analysis_time": analysis_time,
    "resume_health": resume_health,
    "suggestions": suggestions
    
}

    save_resume_history(
                email=email,
                company=company.strip().title(),
                role=role,
                ats_score=ats_score,
                analyzed_at=datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
                result_json=json.dumps(result)
            )
    
    best_resume = get_best_resume_record(email)

    resume_improvement = get_resume_improvement(
        email,
        company,
        role
    )

    result["best_resume"] = best_resume
    result["resume_improvement"] = resume_improvement

    return result

@app.get("/history/{email}")
def get_history(email: str):
    return get_resume_history(email)

@app.delete("/history/{history_id}")
def delete_history(history_id: int):

    delete_resume_history(history_id)

    return {
        "message": "History deleted successfully"
    }

@app.get("/dashboard-stats/{email}")
def dashboard_stats(email: str):
    return get_dashboard_stats(email)

@app.get("/notifications/{email}")
def notifications(email: str):
    return get_notifications(email)

@app.post("/notifications/read/{email}")
def read_notifications(email: str):

    mark_notifications_as_read(email)

    return {
        "message": "Notifications marked as read"
    }

@app.get("/history/result/{history_id}")
def history_result(history_id: int):
    return get_history_result(history_id)