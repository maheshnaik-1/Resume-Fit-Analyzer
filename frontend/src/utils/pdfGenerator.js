import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

function checkPage(doc, currentY, spaceNeeded = 10) {
    if (currentY + spaceNeeded > 280) {
        doc.addPage();
        return 20;
    }

    return currentY;
}

export function generatePDF(result) {
    const doc = new jsPDF();

    // ===============================
    // Cover Page
    // ===============================

    doc.setFillColor(30, 144, 255);
    doc.rect(0, 0, 210, 45, "F");

    doc.setTextColor(255,255,255);
    doc.setFont("helvetica","bold");
    doc.setFontSize(24);

    doc.text(
        "Resume Fit Analyzer",
        105,
        22,
        { align:"center" }
    );

    doc.setFontSize(13);

    doc.text(
        "Professional Resume Analysis Report",
        105,
        34,
        { align:"center" }
    );

    doc.setTextColor(0);

    doc.setFontSize(18);
    doc.text("Analysis Summary",14,60);

    doc.setDrawColor(220);
    doc.roundedRect(14,68,182,65,4,4);

    doc.setFontSize(12);

    doc.text(
        `Company : ${result.company || result.target_company || result.best_resume?.company || "-"}`,
        20,
        84
    );

    doc.text(
        `Role : ${result.role || result.target_role || result.best_resume?.role || "-"}`,
        20,
        96
    );

    doc.text(
        `Generated : ${new Date().toLocaleString()}`,
        20,
        108
    );

    doc.text(
        `Analysis Time : ${result.analysis_time} sec`,
        20,
        120
    );

    let grade = "Needs Improvement";

    if (result.ats_score >= 80) {
        grade = "Excellent";
    }
    else if (result.ats_score >= 60) {
        grade = "Good";
    }

    doc.setFont("helvetica","bold");

    if (grade === "Excellent") {
        doc.setTextColor(34,197,94);
    }
    else if (grade === "Good") {
        doc.setTextColor(245,158,11);
    }
    else {
        doc.setTextColor(239,68,68);
    }

    doc.text(`Grade: ${grade}`,14,80);

    doc.setTextColor(0,0,0);

    autoTable(doc, {
        startY: 90,
        head: [["Category", "Score"]],
        body: [
            ["Skills", `${result.score_breakdown.skills}%`],
            ["Projects", `${result.score_breakdown.projects}%`],
            ["Education", `${result.score_breakdown.education}%`],
            ["Certifications", `${result.score_breakdown.certifications}%`],
        ],
    });

    let currentY = doc.lastAutoTable.finalY + 12;

    doc.setFont("helvetica", "bold");
    doc.setFontSize(15);
    doc.text("Matched Skills", 14, currentY);

    doc.setFont("helvetica", "normal");
    doc.setFontSize(11);

    (result.matched_skills || []).forEach((skill, index) => {
        doc.text(`• ${skill}`, 18, currentY + 10 + index * 7);
    });

    currentY =
        currentY +
        18 +
        ((result.matched_skills || []).length * 7);

    // ---------- Resume Summary ----------

    currentY += 15;

    doc.setFont("helvetica", "bold");
    doc.setFontSize(15);
    doc.text("Resume Summary", 14, currentY);

    currentY += 10;

    doc.setFont("helvetica", "normal");
    doc.setFontSize(11);

    const summary = result.resume_summary || {};

    const sections = [
        {
            title: "Detected Skills",
            items: summary.skills || []
        },
        {
            title: "Education",
            items: summary.education || []
        },
        {
            title: "Projects",
            items: summary.projects || []
        },
        {
            title: "Certifications",
            items: summary.certifications || []
        }
    ];

    sections.forEach(section => {

        currentY = checkPage(doc, currentY, 20);

        doc.setFont("helvetica", "bold");
        doc.text(section.title, 14, currentY);

        currentY += 8;

        doc.setFont("helvetica", "normal");

        if (section.items.length === 0) {

            currentY = checkPage(doc, currentY);

            doc.text("-", 18, currentY);

            currentY += 8;

        } else {

            section.items.forEach(item => {

                currentY = checkPage(doc, currentY);

                doc.text(`• ${item}`, 18, currentY);

                currentY += 7;

            });

        }

        currentY += 8;

    });

    doc.setFont("helvetica", "bold");
    doc.setFontSize(15);

    doc.text("Missing Skills", 14, currentY);

    doc.setFont("helvetica", "normal");
    doc.setFontSize(11);

    (result.missing_skills || []).forEach((skill) => {
        doc.text(`• ${skill}`, 18, currentY + 10);
        currentY += 7;
    });

    currentY += 18;

    if (currentY > 250) {
        doc.addPage();
        currentY = 20;
    }

    doc.setFont("helvetica", "bold");
    doc.setFontSize(15);

    doc.text("Top Career Matches", 14, currentY);

    currentY += 10;

    doc.setFont("helvetica", "normal");
    const careerMatches = result.top_matches || result.top_predictions || [];

    careerMatches.forEach((match, index) => {
        const score = match.match_score ?? match.confidence ?? 0;
        doc.text(
            `${index + 1}. ${match.role} (${score}% match)`,
            18,
            currentY
        );

        currentY += 7;
    });

    currentY += 10;

    if (currentY > 250) {
        doc.addPage();
        currentY = 20;
    }

    doc.setFont("helvetica", "bold");
    doc.setFontSize(15);

    doc.text("Resume Health Report", 14, currentY);

    doc.setFont("helvetica", "normal");
    doc.setFontSize(11);

    (result.resume_health || []).forEach((item) => {

        const cleanItem =
            item.replace(/[^\x20-\x7E]/g, "").trim();

        doc.text(`• ${cleanItem}`,18,currentY+10);

        currentY += 7;

    });

    currentY += 18;

    if (currentY > 250) {
        doc.addPage();
        currentY = 20;
    }

    doc.setFont("helvetica", "bold");
    doc.setFontSize(15);

    doc.text("Suggestions", 14, currentY);

    doc.setFont("helvetica", "normal");
    doc.setFontSize(11);

    (result.suggestions || []).forEach((item) => {

        if (currentY > 270) {
            doc.addPage();
            currentY = 20;
        }

        doc.text(`• ${item}`,18,currentY+10);

        currentY += 7;

    });

    const pageCount = doc.getNumberOfPages();

    for(let i=1;i<=pageCount;i++){

        doc.setPage(i);

        doc.setFontSize(10);
        doc.setTextColor(120);

        doc.text(
            "Generated by Resume Fit Analyzer",
            14,
            290
        );

        doc.text(
            `Page ${i} of ${pageCount}`,
            170,
            290
        );
    }

    doc.setTextColor(0);
    
    doc.save("Resume_Report.pdf");
}
