import { useMemo } from "react";

function useNotifications(stats, result) {
    return useMemo(() => {

        const notifications = [];

        // Welcome
        notifications.push(
            `🎉 Welcome back, ${localStorage.getItem("name") || "User"}!`
        );

        // Analysis completed
        if (result) {
            notifications.push("✅ Resume analyzed successfully");
        }

        // Excellent score
        if (result?.ats_score >= 90) {
            notifications.push("🌟 Outstanding ATS Score!");
        }

        // Good score
        else if (result?.ats_score >= 80) {
            notifications.push("🏆 Excellent Resume!");
        }

        // Needs improvement
        else if (result?.ats_score < 60 && result) {
            notifications.push("💡 Your resume can be improved.");
        }

        // Highest ATS
        if (stats.highest_score > 0) {
            notifications.push(
                `🏅 Highest ATS: ${stats.highest_score.toFixed(2)}%`
            );
        }

        // Average ATS
        if (stats.average_score > 0) {
            notifications.push(
                `📊 Average ATS: ${stats.average_score.toFixed(2)}%`
            );
        }

        // Total analyses
        if (stats.total_analyses > 0) {
            notifications.push(
                `📄 Total Analyses: ${stats.total_analyses}`
            );
        }

        // Achievements
        if (stats.total_analyses === 1) {
            notifications.push("🎯 Achievement: First Resume Analysis!");
        }

        if (stats.total_analyses === 5) {
            notifications.push("🔥 Achievement: 5 Resume Analyses!");
        }

        if (stats.total_analyses === 10) {
            notifications.push("🚀 Achievement: 10 Resume Analyses!");
        }

        // Motivation
        notifications.push(
            "💪 Keep improving your resume!"
        );

        return notifications;

    }, [stats, result]);
}

export default useNotifications;