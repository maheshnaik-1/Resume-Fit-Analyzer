package com.resumeanalyzer.catalog;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CompanySuggestionCatalog {

    private CompanySuggestionCatalog() {
    }

    public static final Map<String, Map<String, List<String>>> COMPANY_SUGGESTIONS;

    static {
        Map<String, Map<String, List<String>>> companyMap = new LinkedHashMap<>();

        // Google
        Map<String, List<String>> google = new LinkedHashMap<>();
        google.put("default", List.of(
                "Highlight strong computer science fundamentals, scalability, and problem-solving skills.",
                "Demonstrate impactful projects with quantifiable metrics and clean code architecture.",
                "Emphasize collaboration, open-source contributions, and technical leadership."
        ));
        google.put("data analyst", List.of(
                "Improve SQL and Pandas skills with real-world datasets.",
                "Build interactive dashboards using Power BI or Looker.",
                "Practice statistical analysis and clear data storytelling."
        ));
        google.put("software engineer", List.of(
                "Strengthen Data Structures, Algorithms, and System Design fundamentals.",
                "Practice solving coding interview problems with optimal time/space complexity.",
                "Highlight open-source contributions and large-scale projects on your resume."
        ));
        google.put("frontend developer", List.of(
                "Focus on web performance optimization, accessibility, and modern React patterns.",
                "Build responsive, component-driven web applications with clean CSS.",
                "Demonstrate experience with state management and REST API integration."
        ));
        google.put("backend developer", List.of(
                "Strengthen API architecture, database indexing, and microservices concepts.",
                "Build high-throughput backend services using FastAPI, Node.js, or Java.",
                "Practice designing scalable distributed systems."
        ));
        google.put("full stack developer", List.of(
                "Demonstrate end-to-end full stack projects with authentication and database integration.",
                "Showcase expertise across both frontend UI and scalable backend APIs."
        ));
        google.put("ai/ml engineer", List.of(
                "Demonstrate applied Deep Learning and NLP projects with PyTorch or TensorFlow.",
                "Highlight model evaluation metrics, optimization, and production deployment."
        ));
        companyMap.put("google", Collections.unmodifiableMap(google));

        // Amazon
        Map<String, List<String>> amazon = new LinkedHashMap<>();
        amazon.put("default", List.of(
                "Align your project descriptions and achievements with Amazon Leadership Principles.",
                "Emphasize ownership, scalable architecture, and customer-focused problem solving.",
                "Highlight experience with cloud technologies, automation, and reliable systems."
        ));
        amazon.put("sde-1", List.of(
                "Master Object-Oriented Programming (OOP) and Data Structures & Algorithms.",
                "Familiarize yourself with Amazon Leadership Principles and behavioral interview patterns.",
                "Build robust backend services with automated unit and integration tests."
        ));
        amazon.put("data engineer", List.of(
                "Practice scalable ETL pipeline development and data warehouse modeling.",
                "Gain practical hands-on experience with Apache Spark, SQL, and AWS services (S3, Redshift).",
                "Build large-scale data processing and streaming projects."
        ));
        amazon.put("cloud engineer", List.of(
                "Strengthen AWS cloud infrastructure, networking, and security concepts.",
                "Practice Infrastructure-as-Code (IaC) and containerization using Docker and Kubernetes."
        ));
        amazon.put("business analyst", List.of(
                "Demonstrate advanced Excel modeling, SQL queries, and Power BI reporting.",
                "Highlight data-driven decision-making and business problem-solving case studies."
        ));
        companyMap.put("amazon", Collections.unmodifiableMap(amazon));

        // Microsoft
        Map<String, List<String>> microsoft = new LinkedHashMap<>();
        microsoft.put("default", List.of(
                "Demonstrate solid software engineering fundamentals, design patterns, and testing.",
                "Highlight experience with cloud platforms, collaborative development, and CI/CD.",
                "Emphasize continuous learning and building accessible, user-centric software."
        ));
        microsoft.put("software engineer", List.of(
                "Strengthen Core CS concepts: Algorithms, Data Structures, and OOP principles.",
                "Build clean, scalable applications with thorough unit testing and Git workflow."
        ));
        microsoft.put("cloud engineer", List.of(
                "Learn Microsoft Azure fundamentals and cloud resource management.",
                "Practice Docker and Kubernetes deployments for microservice workloads.",
                "Build automated CI/CD deployment pipelines."
        ));
        microsoft.put("backend developer", List.of(
                "Improve REST API development and relational database design.",
                "Build secure, scalable backend applications with modular architecture."
        ));
        microsoft.put("data analyst", List.of(
                "Master complex SQL queries, Power BI dashboards, and Excel pivot analysis.",
                "Practice exploratory data analysis and executive presentation skills."
        ));
        microsoft.put("ai engineer", List.of(
                "Highlight experience in Generative AI, LLM prompting, and modern AI frameworks.",
                "Build real-world AI applications integrating cloud-based cognitive services."
        ));
        companyMap.put("microsoft", Collections.unmodifiableMap(microsoft));

        // Meta
        Map<String, List<String>> meta = new LinkedHashMap<>();
        meta.put("default", List.of(
                "Highlight high-velocity development, rapid prototyping, and scalable system design.",
                "Demonstrate strong foundations in modern frameworks, APIs, and data-driven optimization.",
                "Showcase end-to-end project ownership and performance engineering."
        ));
        meta.put("frontend engineer", List.of(
                "Master advanced React concepts, hooks, component lifecycle, and state architecture.",
                "Focus on web performance, bundle size optimization, and responsive design.",
                "Build rich interactive client-side web applications."
        ));
        meta.put("backend engineer", List.of(
                "Demonstrate scalable backend systems with RESTful APIs or GraphQL.",
                "Focus on database performance, caching strategies, and concurrency."
        ));
        meta.put("machine learning engineer", List.of(
                "Build and optimize ML/DL models with PyTorch, TensorFlow, and Pandas.",
                "Highlight feature engineering, cross-validation, and production inference."
        ));
        meta.put("data scientist", List.of(
                "Strengthen experimental design, A/B testing, and statistical hypothesis testing.",
                "Practice data manipulation with Python/SQL and build clear visualizations."
        ));
        companyMap.put("meta", Collections.unmodifiableMap(meta));

        // Apple
        Map<String, List<String>> apple = new LinkedHashMap<>();
        apple.put("default", List.of(
                "Emphasize attention to detail, high software quality, and seamless user experiences.",
                "Demonstrate strong core programming skills, system efficiency, and robust architecture.",
                "Highlight privacy-conscious design and modular, maintainable code."
        ));
        apple.put("software engineer", List.of(
                "Demonstrate high-performance programming, algorithms, and clean system design.",
                "Write clean, maintainable, well-documented code with strong OOP principles."
        ));
        apple.put("ios developer", List.of(
                "Build modern iOS applications using Swift, SwiftUI, and UIKit.",
                "Practice Xcode profiling, memory management, and REST API integration.",
                "Publish or showcase portfolio iOS projects with intuitive user experience."
        ));
        apple.put("machine learning engineer", List.of(
                "Focus on on-device machine learning, CoreML, and efficient model execution.",
                "Demonstrate deep learning and computer vision or NLP competencies."
        ));
        apple.put("embedded systems engineer", List.of(
                "Practice Embedded C / C++ programming and low-level debugging.",
                "Work with microcontrollers (STM32, ESP32, Arduino) and hardware protocols.",
                "Learn RTOS (FreeRTOS) task scheduling and memory constraints."
        ));
        apple.put("frontend developer", List.of(
                "Focus on pixel-perfect UI implementation, smooth CSS animations, and accessibility.",
                "Build responsive, high-performance web interfaces."
        ));
        companyMap.put("apple", Collections.unmodifiableMap(apple));

        // Infosys
        Map<String, List<String>> infosys = new LinkedHashMap<>();
        infosys.put("default", List.of(
                "Strengthen core programming (Java/Python), object-oriented design, and database skills.",
                "Demonstrate foundational software development lifecycle (SDLC) and version control experience.",
                "Highlight willingness to learn emerging technologies and adapt to enterprise environments."
        ));
        infosys.put("systems engineer", List.of(
                "Strengthen Core Java or Python programming and problem-solving skills.",
                "Practice basic SQL database operations and Linux command-line utilities."
        ));
        infosys.put("java developer", List.of(
                "Master Core Java, Spring Boot, Hibernate, and REST API development.",
                "Build relational database applications using MySQL or PostgreSQL with Git version control."
        ));
        infosys.put("full stack developer", List.of(
                "Build complete web apps using React on the frontend and Node.js or Spring Boot on backend.",
                "Demonstrate CRUD operations and database integration."
        ));
        infosys.put("data analyst", List.of(
                "Improve SQL query writing, Excel data manipulation, and Power BI visualization.",
                "Highlight analytical thinking and structured business reporting."
        ));
        infosys.put("testing engineer", List.of(
                "Master manual testing methodologies, test case creation, and bug reporting.",
                "Learn automation testing with Selenium WebDriver, JUnit, and TestNG."
        ));
        companyMap.put("infosys", Collections.unmodifiableMap(infosys));

        // TCS
        Map<String, List<String>> tcs = new LinkedHashMap<>();
        tcs.put("default", List.of(
                "Emphasize solid fundamentals in programming, database management, and problem solving.",
                "Highlight practical project experience, certifications, and strong communication skills.",
                "Demonstrate familiarity with standard software development practices and tools."
        ));
        tcs.put("software engineer", List.of(
                "Strengthen foundational programming (Java, Python, C++) and OOP concepts.",
                "Practice standard Data Structures, Algorithms, and SQL queries."
        ));
        tcs.put("java developer", List.of(
                "Build enterprise Java applications using Spring Boot and Hibernate.",
                "Practice building RESTful microservices and managing database transactions."
        ));
        tcs.put("cloud engineer", List.of(
                "Gain certification in AWS or Azure cloud fundamentals.",
                "Practice Linux system administration, networking, and Docker containers."
        ));
        tcs.put("business analyst", List.of(
                "Develop strong requirements gathering, documentation, and stakeholder communication skills.",
                "Master Excel, SQL, and business workflow modeling."
        ));
        tcs.put("data engineer", List.of(
                "Practice ETL pipeline design, SQL data transformations, and data warehousing basics.",
                "Learn Apache Spark and cloud storage management."
        ));
        companyMap.put("tcs", Collections.unmodifiableMap(tcs));

        // Wipro
        Map<String, List<String>> wipro = new LinkedHashMap<>();
        wipro.put("default", List.of(
                "Showcase strong foundations in programming languages, databases, and web technologies.",
                "Highlight hands-on project work, problem-solving ability, and continuous upskilling.",
                "Emphasize team collaboration, version control with Git, and reliable delivery."
        ));
        wipro.put("software engineer", List.of(
                "Focus on core programming fundamentals, problem solving, and software development lifecycle.",
                "Build well-structured projects using Git for version control."
        ));
        wipro.put("backend developer", List.of(
                "Develop scalable server-side APIs using Java Spring Boot or Python FastAPI.",
                "Practice relational database modeling with MySQL and containerization with Docker."
        ));
        wipro.put("frontend developer", List.of(
                "Build responsive user interfaces using HTML5, CSS3, JavaScript, and React.",
                "Ensure cross-browser compatibility and clean component design."
        ));
        wipro.put("data analyst", List.of(
                "Master SQL data extraction, exploratory analysis with Pandas, and Power BI dashboards.",
                "Practice interpreting business metrics and presenting actionable insights."
        ));
        wipro.put("cloud engineer", List.of(
                "Understand cloud architecture patterns across AWS and Azure.",
                "Practice CI/CD automation pipelines and infrastructure deployment."
        ));
        companyMap.put("wipro", Collections.unmodifiableMap(wipro));

        // Accenture
        Map<String, List<String>> accenture = new LinkedHashMap<>();
        accenture.put("default", List.of(
                "Demonstrate strong technical versatility across modern frameworks and cloud platforms.",
                "Highlight analytical problem solving, client-oriented solutions, and agile methodology.",
                "Showcase practical project implementations with clean architecture and documentation."
        ));
        accenture.put("application developer", List.of(
                "Strengthen full-lifecycle application development with Java, Spring Boot, and REST APIs.",
                "Practice writing maintainable, clean code following OOP best practices."
        ));
        accenture.put("ai engineer", List.of(
                "Build applied AI applications using Python, machine learning libraries, and LLM APIs.",
                "Demonstrate end-to-end data pipelines and model integration."
        ));
        accenture.put("cloud engineer", List.of(
                "Focus on multi-cloud deployment strategies, Docker containerization, and Kubernetes.",
                "Practice cloud security, networking, and automated infrastructure provisioning."
        ));
        accenture.put("frontend developer", List.of(
                "Build interactive, modern web applications with React and responsive CSS.",
                "Focus on user experience, API consumption, and client-side performance."
        ));
        accenture.put("data analyst", List.of(
                "Strengthen SQL, statistical analysis, and interactive dashboard creation in Power BI / Tableau.",
                "Highlight experience in deriving data-backed business recommendations."
        ));
        companyMap.put("accenture", Collections.unmodifiableMap(accenture));

        // Deloitte
        Map<String, List<String>> deloitte = new LinkedHashMap<>();
        deloitte.put("default", List.of(
                "Highlight analytical problem-solving skills, structured communication, and technical depth.",
                "Demonstrate experience in building secure, scalable solutions and consulting mindset.",
                "Emphasize business value creation and collaboration on complex technical projects."
        ));
        deloitte.put("software engineer", List.of(
                "Strengthen problem-solving abilities, clean coding practices, and system design basics.",
                "Highlight experience working with modern development stacks and agile methodologies."
        ));
        deloitte.put("cyber security analyst", List.of(
                "Learn network security fundamentals, vulnerability assessment, and risk management frameworks.",
                "Gain practical experience with SIEM tools, security auditing, and Linux security administration.",
                "Familiarize yourself with industry security standards and incident response protocols."
        ));
        deloitte.put("data analyst", List.of(
                "Master complex SQL data joins, advanced Excel modeling, and executive dashboard design.",
                "Demonstrate business acumen and clear data-driven presentation skills."
        ));
        deloitte.put("cloud consultant", List.of(
                "Understand enterprise cloud migration strategies across AWS and Azure.",
                "Focus on cloud governance, cost optimization, and secure infrastructure design."
        ));
        deloitte.put("java developer", List.of(
                "Build enterprise-grade microservices with Java, Spring Boot, and relational databases.",
                "Implement secure REST APIs with automated testing and version control."
        ));
        companyMap.put("deloitte", Collections.unmodifiableMap(deloitte));

        COMPANY_SUGGESTIONS = Collections.unmodifiableMap(companyMap);
    }
}
