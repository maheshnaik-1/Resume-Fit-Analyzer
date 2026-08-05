# 🚀 Resume Fit Analyzer

An intelligent web application that analyzes resumes against a selected company and job role, calculates an ATS (Applicant Tracking System) score, identifies missing skills, and provides actionable suggestions to improve resume quality.

---

## 📌 Features

- 📄 Upload Resume (PDF)
- 🎯 ATS Score Calculation
- 🏢 Company & Role Selection
- ✅ Matched Skills Detection
- ❌ Missing Skills Identification
- 📊 Score Breakdown
- 🎓 Education Detection
- 📜 Certification Detection
- 💼 Project Detection
- 📈 Resume Improvement Tracking
- 🏆 Best Resume Record
- 📚 Resume Analysis History
- 🔔 Notification System
- 📋 Dashboard Analytics
- 🎯 Career Match Prediction
- 💡 Resume Improvement Suggestions
- 📥 Download Analysis Report as PDF
- 🌙 Light / Dark Theme
- 🔐 User Authentication (Login & Signup)

---

## 🛠 Tech Stack

### Frontend

- React.js
- Vite
- CSS3
- Axios

### Backend

- FastAPI
- Python

### Database

- SQLite

### Machine Learning

- Scikit-learn
- Random Forest Classifier

### Libraries

- PyPDF2
- Pandas
- Joblib

---

## 📂 Project Structure

```
Resume-Fit-Analyzer
│
├── backend
│   ├── main.py
│   ├── database.py
│   ├── job_data.py
│   ├── dataset
│   ├── ml
│   └── models
│
├── frontend
│   ├── src
│   ├── public
│   └── package.json
│
└── README.md
```

---

## ⚙️ Installation

### Clone Repository

```bash
git clone https://github.com/maheshnaik-1/Resume-Fit-Analyzer.git
```

### Backend

```bash
cd backend

pip install fastapi
pip install uvicorn
pip install scikit-learn
pip install pandas
pip install PyPDF2
pip install joblib
```

Run backend

```bash
uvicorn main:app --reload
```

---

### Frontend

```bash
cd frontend

npm install

npm run dev
```

---

## 🧠 How It Works

1. User uploads a resume in PDF format.
2. Resume text is extracted using PyPDF2.
3. Skills, education, certifications, and projects are detected.
4. The selected company and job role determine the required skills.
5. ATS score is calculated based on skill matching.
6. Resume insights and recommendations are generated.
7. Analysis is stored in SQLite for history and analytics.
8. Machine Learning predicts suitable career matches.
9. Users can download the complete report as a PDF.

---

## 📊 Current Features

- ATS Score Analysis
- Resume History
- Analytics Dashboard
- Notification Center
- Career Prediction
- Resume Health Report
- PDF Report Generation
- Authentication System

---

## 🔮 Future Enhancements

- AI-powered Resume Recommendations using LLMs
- DOCX Resume Support
- Cloud Database Integration
- Resume Version Comparison
- Recruiter Dashboard
- Email Notifications
- Deployment on AWS / Render
- Docker Support

---

## 👨‍💻 Author

**Mahesh Naik**

B.Tech Computer Science Engineering Student

GitHub:
https://github.com/maheshnaik-1

---

## 📄 License

This project is intended for educational and learning purposes. It may be used as a reference for academic and personal learning.
