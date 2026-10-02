package com.resumeanalyzer.catalog;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RoleCatalog {

    private RoleCatalog() {
    }

    public static final Map<String, List<String>> ROLE_SKILLS;

    static {
        Map<String, List<String>> map = new LinkedHashMap<>();

        map.put("Software Engineer", List.of(
                "Java", "Python", "C++", "OOP",
                "Algorithms", "Data Structures",
                "DSA", "Git", "SQL"
        ));

        map.put("Java Developer", List.of(
                "Java", "Spring Boot", "Hibernate",
                "REST API", "MySQL", "SQL",
                "Git", "OOP"
        ));

        map.put("Python Developer", List.of(
                "Python", "FastAPI",
                "REST API", "SQL",
                "Git", "OOP"
        ));

        map.put("Backend Developer", List.of(
                "Java", "Spring Boot",
                "FastAPI", "REST API",
                "SQL", "MySQL",
                "Docker", "Git"
        ));

        map.put("Frontend Developer", List.of(
                "HTML", "CSS",
                "JavaScript", "React",
                "Bootstrap",
                "Responsive Design",
                "Git"
        ));

        map.put("Full Stack Developer", List.of(
                "HTML", "CSS",
                "JavaScript", "React",
                "FastAPI", "SQL",
                "Git", "Docker"
        ));

        map.put("React Developer", List.of(
                "React",
                "HTML",
                "CSS",
                "JavaScript",
                "Bootstrap",
                "Git"
        ));

        map.put("Data Analyst", List.of(
                "Python",
                "SQL",
                "Excel",
                "Pandas",
                "Statistics",
                "Power BI",
                "Data Visualization"
        ));

        map.put("Data Engineer", List.of(
                "Python",
                "SQL",
                "ETL",
                "Spark",
                "Data Warehousing",
                "AWS",
                "Docker"
        ));

        map.put("Cloud Engineer", List.of(
                "AWS",
                "Azure",
                "Docker",
                "Kubernetes",
                "Linux",
                "Cloud Computing"
        ));

        map.put("DevOps Engineer", List.of(
                "Docker",
                "Kubernetes",
                "Linux",
                "AWS",
                "CI/CD",
                "Git"
        ));

        map.put("Machine Learning Engineer", List.of(
                "Python",
                "Machine Learning",
                "TensorFlow",
                "PyTorch",
                "Pandas",
                "Statistics"
        ));

        map.put("AI Engineer", List.of(
                "Python",
                "Machine Learning",
                "Deep Learning",
                "TensorFlow",
                "PyTorch",
                "NLP"
        ));

        map.put("Spring Boot Developer", List.of(
                "Java",
                "Spring Boot",
                "Hibernate",
                "REST API",
                "SQL",
                "Git"
        ));

        map.put("iOS Developer", List.of(
                "Swift",
                "UIKit",
                "SwiftUI",
                "Xcode",
                "REST API",
                "Git"
        ));

        map.put("Embedded Systems Engineer", List.of(
                "C",
                "C++",
                "Embedded C",
                "Microcontrollers",
                "RTOS",
                "Debugging"
        ));

        map.put("Cyber Security Analyst", List.of(
                "Networking",
                "Linux",
                "Cyber Security",
                "SIEM",
                "Risk Assessment",
                "Python"
        ));

        map.put("Testing Engineer", List.of(
                "Manual Testing",
                "Selenium",
                "Java",
                "Bug Tracking",
                "JUnit",
                "Git"
        ));

        ROLE_SKILLS = Collections.unmodifiableMap(map);
    }
}
