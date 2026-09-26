# 🚀 Resume Fit Analyzer

![React](https://img.shields.io/badge/React-19-blue?logo=react)
![FastAPI](https://img.shields.io/badge/FastAPI-Backend-green?logo=fastapi)
![Python](https://img.shields.io/badge/Python-3.12-yellow?logo=python)
![SQLite](https://img.shields.io/badge/SQLite-Database-blue?logo=sqlite)
![Machine Learning](https://img.shields.io/badge/Machine-Learning-orange)
![Git](https://img.shields.io/badge/Git-Version%20Control-red?logo=git)
![License](https://img.shields.io/badge/License-Educational-purple)

---

## 📖 About

Resume Fit Analyzer is a full-stack web application that analyzes resumes against job descriptions to evaluate ATS compatibility, identify missing skills, detect education and certifications, recommend top matching career paths, and provide actionable suggestions for improving resume quality.

This project was developed to strengthen skills in Full Stack Development, REST API development, and software engineering.

---

## 🌐 Demo

> Coming Soon

The application will be deployed after completing the production version.

---

# ✨ Features

- 📄 Upload Resume (PDF)
- 🎯 ATS Score Calculation
- 🏢 Company Selection
- 💼 Role Selection
- 🎯 Top Career Role Matching
- 📊 Interactive Dashboard
- 📈 Skill Detection
- 🎓 Education Detection
- 📜 Certification Detection
- 💼 Project Detection
- 📑 Resume Health Report
- 💡 Resume Improvement Suggestions
- 📊 ATS Score Breakdown
- 📄 Download PDF Report
- 🕒 Resume Analysis History
- 🔐 Login & Signup UI

---

# 🛠 Tech Stack

## Frontend
• React.js
• Vite
• JavaScript (ES6+)
• HTML5
• CSS3

## Backend
• FastAPI
• Python 3

## Role Matching Engine
• Deterministic Skill-Overlap Scoring

## Database
• SQLite

## Development Tools
• VS Code
• Git
• GitHub

---

# 🏗 System Architecture

```text
              Resume (PDF)
                     │
                     ▼
           React Frontend (Vite)
                     │
             REST API Requests
                     │
                     ▼
            FastAPI Backend
                     │
       Resume Text Extraction
                     │
     ATS Analysis + Role Matching
                     │
     SQLite Database Storage
                     │
                     ▼
          Analytics Dashboard
```

---

# 📂 Project Structure

```text
Resume-Fit-Analyzer
│
├── backend
│   ├── main.py
│   ├── database.py
│   ├── role_matcher.py
│   └── job_data.py
│
├── frontend
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── pages/
│   │   ├── styles/
│   │   └── utils/
│
└── README.md
```

---

# 📷 Screenshots

## Home Page

<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/c211431c-1602-4b26-a361-c4df7d54caa3" />

## Dashboard

<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/55d02fbe-6027-471b-a401-57d420825ed8" />

<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/ed5710c6-9dee-46b8-bee7-eafb99b507c7" />

<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/c871f142-96c4-41d2-99f4-a21cf34e1bee" />

## Analytics

<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/b42aaa76-df6f-41ad-819e-1ce067bc4763" />

<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/4e59a9ac-0db4-4738-a099-06bb4d7b285b" />

<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/8c5e18a7-1c50-4f15-8e92-385b22031934" />


---

# ⚙ Installation

## Clone Repository

```bash
git clone https://github.com/maheshnaik-1/Resume-Fit-Analyzer.git
```

### Backend

```bash
cd backend

pip install -r requirements.txt

uvicorn main:app --reload
```

### Frontend

```bash
cd frontend

npm install

npm run dev
```

---

## 🚧 Current Status

This project is under active development.

### Currently Implemented

✔ Resume Upload

✔ ATS Score

✔ Resume Parsing

✔ Skill Detection

✔ Role Matching

✔ Analytics Dashboard

### Planned

• AI Resume Suggestions

• Resume Builder

• Cloud Deployment

---

# 🎯 Future Roadmap

- ✅ Resume Upload
- ✅ ATS Score
- ✅ Skill Detection
- ✅ Role Matching
- ✅ Dashboard

### Upcoming Features

- 🤖 AI Resume Suggestions
- ✍ AI Cover Letter Generator
- 📄 Resume Builder
- 🐳 Docker Support
- ☁ Cloud Deployment
- 🔐 JWT Authentication
- 👨‍💼 Recruiter Dashboard
- 📈 Resume Version Comparison

---

# 🤝 Contributing

Contributions, feature requests, and suggestions are welcome.

Feel free to fork the repository and submit a Pull Request.

---

## 📄 License

This project is open for educational and portfolio purposes.

Please contact the author before using substantial portions of the code in commercial projects.

---

# 👨‍💻 Author

**Mahesh Naik**

B.Tech Computer Science Engineering Student

GitHub:
https://github.com/maheshnaik-1
