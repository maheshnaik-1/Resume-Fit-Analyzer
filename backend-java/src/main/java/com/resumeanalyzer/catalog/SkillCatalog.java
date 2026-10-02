package com.resumeanalyzer.catalog;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SkillCatalog {

    private SkillCatalog() {
    }

    public static final List<String> CANONICAL_SKILLS = List.of(
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
    );

    public static final Map<String, List<String>> SKILL_ALIASES;

    static {
        Map<String, List<String>> map = new LinkedHashMap<>();

        // Programming Languages
        map.put("Java", List.of("Java", "Core Java", "Java SE"));
        map.put("Python", List.of("Python", "Python3", "Python Programming"));
        map.put("C", List.of("C", "C Language"));
        map.put("C++", List.of("C++", "CPP", "C Plus Plus"));
        map.put("JavaScript", List.of("JavaScript", "JS", "ECMAScript"));

        // Frontend
        map.put("HTML", List.of("HTML", "HTML5"));
        map.put("CSS", List.of("CSS", "CSS3"));
        map.put("React", List.of("React", "ReactJS", "React.js"));

        // Backend
        map.put("Node.js", List.of("Node.js", "NodeJS"));
        map.put("FastAPI", List.of("FastAPI", "Fast API"));
        map.put("Spring Boot", List.of("Spring Boot", "Spring", "SpringBoot"));
        map.put("Hibernate", List.of("Hibernate", "Hibernate ORM"));
        map.put("REST API", List.of("REST API", "REST APIs", "RESTful API", "RESTful APIs"));

        // Database
        map.put("MySQL", List.of("MySQL", "My SQL"));
        map.put("MongoDB", List.of("MongoDB", "Mongo DB"));
        map.put("SQL", List.of("SQL", "Structured Query Language"));

        // Version Control
        map.put("Git", List.of("Git", "GitHub", "Github"));
        map.put("Docker", List.of("Docker", "Docker Container"));
        map.put("Kubernetes", List.of("Kubernetes", "K8s"));
        map.put("CI/CD", List.of("CI/CD", "CI CD", "Continuous Integration"));

        // Cloud
        map.put("AWS", List.of("AWS", "Amazon Web Services", "EC2", "S3", "Lambda"));
        map.put("Azure", List.of("Azure", "Microsoft Azure"));
        map.put("Cloud Computing", List.of("Cloud Computing", "Cloud"));
        map.put("Cloud Architecture", List.of("Cloud Architecture", "Cloud Architect"));

        // Data Science
        map.put("Pandas", List.of("Pandas", "Pandas Library"));
        map.put("Statistics", List.of("Statistics", "Statistical Analysis"));
        map.put("Power BI", List.of("Power BI", "PowerBI"));
        map.put("Data Visualization", List.of("Data Visualization", "Visualization"));

        // AI
        map.put("Machine Learning", List.of("Machine Learning", "ML"));
        map.put("Deep Learning", List.of("Deep Learning", "DL"));
        map.put("TensorFlow", List.of("TensorFlow", "Tensor Flow"));
        map.put("PyTorch", List.of("PyTorch", "Torch"));
        map.put("NLP", List.of("NLP", "Natural Language Processing"));

        // Data Engineering
        map.put("ETL", List.of("ETL", "Extract Transform Load"));
        map.put("Data Warehousing", List.of("Data Warehouse", "Data Warehousing"));
        map.put("Data Processing", List.of("Data Processing", "Data Pipeline"));

        // CS Fundamentals
        map.put("OOP", List.of("OOP", "Object Oriented Programming"));
        map.put("Algorithms", List.of("Algorithms", "Algorithm Design"));
        map.put("Data Structures", List.of("Data Structures", "DS"));
        map.put("DSA", List.of("DSA", "Data Structures and Algorithms"));
        map.put("Problem Solving", List.of("Problem Solving", "Analytical Thinking"));

        // Testing
        map.put("Manual Testing", List.of("Manual Testing", "Software Testing"));
        map.put("Selenium", List.of("Selenium", "Selenium WebDriver"));
        map.put("JUnit", List.of("JUnit", "JUnit5"));
        map.put("Bug Tracking", List.of("Bug Tracking", "Bug Fixing"));

        // iOS
        map.put("Swift", List.of("Swift", "Swift Language"));
        map.put("SwiftUI", List.of("SwiftUI"));
        map.put("UIKit", List.of("UIKit"));
        map.put("Objective-C", List.of("Objective-C", "Objective C"));
        map.put("Xcode", List.of("Xcode"));
        map.put("iOS Development", List.of("iOS Development", "iOS"));

        // Embedded
        map.put("Embedded C", List.of("Embedded C", "Embedded-C"));
        map.put("Microcontrollers", List.of(
                "Microcontroller",
                "Microcontrollers",
                "Arduino",
                "ESP32",
                "ESP8266",
                "STM32",
                "8051",
                "PIC"
        ));
        map.put("RTOS", List.of("RTOS", "FreeRTOS", "Real Time Operating System"));
        map.put("Debugging", List.of("Debugging", "Debug"));

        // Misc
        map.put("Responsive Design", List.of("Responsive Design", "Responsive UI"));
        map.put("Communication", List.of("Communication", "Communication Skills"));
        map.put("Requirement Analysis", List.of("Requirement Analysis", "Requirements Gathering"));
        map.put("Documentation", List.of("Documentation", "Technical Documentation"));
        map.put("Cyber Security", List.of("Cyber Security", "Cybersecurity"));
        map.put("SIEM", List.of("SIEM", "Security Information and Event Management"));
        map.put("Risk Assessment", List.of("Risk Assessment", "Risk Analysis"));
        map.put("Spark", List.of("Spark", "Apache Spark"));
        map.put("Bootstrap", List.of("Bootstrap", "Bootstrap Framework"));
        map.put("System Design", List.of("System Design", "Software Architecture"));

        SKILL_ALIASES = Collections.unmodifiableMap(map);
    }

    public static List<String> getAliasesForSkill(String skill) {
        return SKILL_ALIASES.getOrDefault(skill, List.of(skill));
    }
}
