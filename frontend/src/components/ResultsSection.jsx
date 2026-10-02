import ResultCard from "./ResultCard";
import AnimatedNumber from "./AnimatedNumber";
import { generatePDF } from "../utils/pdfGenerator";

function ResultsSection({
    result,
}) {
    return (
        <>
        <div className="results-container results-fade">

            <div className="analysis-summary-card">
                <div className="summary-hero-pane">
                    <span className="metric-tile-label">🎯 ATS Match Score</span>
                    <p className="score score-pop">
                        <AnimatedNumber
                            value={Number(result.ats_score)}
                            decimals={2}
                            suffix="%"
                            duration={1200}
                            fromZero={true}
                        />
                    </p>
                    <p className="rating rating-pop">
                        {result.ats_score >= 80
                            ? "★★★★★"
                            : result.ats_score >= 60
                            ? "★★★★☆"
                            : "★★☆☆☆"}
                    </p>
                    {result.ats_score >= 80 && (
                        <p className="excellent badge-pop">
                            🟢 Excellent Resume
                        </p>
                    )}
                    {result.ats_score >= 60 && result.ats_score < 80 && (
                        <p className="good badge-pop">
                            🟡 Good Resume
                        </p>
                    )}
                    {result.ats_score < 60 && (
                        <p className="poor badge-pop">
                            🔴 Needs Improvement
                        </p>
                    )}
                    <div className="progress-bar">
                        <div
                            className="progress-fill"
                            style={{
                                width: `${(
                                    ((result.matched_skills || []).length /
                                    Math.max((result.job_skills || []).length, 1)) * 100
                                )}%`,
                                background:
                                    result.ats_score >= 80
                                        ? "var(--success)"
                                        : result.ats_score >= 60
                                        ? "var(--warning)"
                                        : "var(--danger)",
                            }}
                        ></div>
                    </div>
                </div>

                <div className="summary-metrics-grid">
                    <div className="metric-tile">
                        <div className="metric-tile-header">
                            <span className="metric-tile-label">🟢 Skills Match</span>
                            <span>
                                {Math.round(
                                    (((result.matched_skills || []).length /
                                    Math.max((result.job_skills || []).length, 1)) * 100)
                                )}%
                            </span>
                        </div>
                        <div className="metric-tile-value">
                            {(result.matched_skills || []).length} / {(result.job_skills || []).length}
                        </div>
                        <p className="metric-tile-subtext">
                            {(result.missing_skills || []).length} missing skills identified
                        </p>
                    </div>

                    <div className="metric-tile">
                        <div className="metric-tile-header">
                            <span className="metric-tile-label">⚡ Analysis Time</span>
                            <span>
                                {Number(result.analysis_time) < 0.20
                                    ? "🚀 Fast"
                                    : "✓ Done"}
                            </span>
                        </div>
                        <div className="metric-tile-value">
                            <AnimatedNumber
                                value={Number(result.analysis_time)}
                                decimals={2}
                                suffix=" sec"
                                duration={800}
                                fromZero={true}
                            />
                        </div>
                        <p className="metric-tile-subtext">
                            {Number(result.analysis_time) < 0.20
                                ? "Lightning Fast Processing"
                                : Number(result.analysis_time) < 0.50
                                ? "Fast Processing"
                                : "Processing Complete"}
                        </p>
                    </div>

                    <div className="metric-tile">
                        <div className="metric-tile-header">
                            <span className="metric-tile-label">🏆 Best Record</span>
                            {result.best_resume && (
                                <span>
                                    {new Date(result.best_resume.date).toLocaleDateString("en-GB", {
                                        day: "2-digit",
                                        month: "short",
                                    })}
                                </span>
                            )}
                        </div>
                        <div className="metric-tile-value">
                            {result.best_resume ? (
                                <AnimatedNumber
                                    value={Number(result.best_resume.ats_score)}
                                    decimals={2}
                                    suffix="%"
                                    duration={1000}
                                    fromZero={true}
                                />
                            ) : (
                                <span>Initial Run</span>
                            )}
                        </div>
                        <p className="metric-tile-subtext">
                            {result.best_resume
                                ? `${result.best_resume.company} • ${result.best_resume.role}`
                                : "Benchmark record established"}
                        </p>
                    </div>

                    <div className="metric-tile">
                        <div className="metric-tile-header">
                            <span className="metric-tile-label">📈 Improvement</span>
                            <span>
                                {result.resume_improvement?.history?.length > 1
                                    ? `${result.resume_improvement.history.length} scans`
                                    : "Baseline"}
                            </span>
                        </div>
                        <div className="metric-tile-value">
                            {result.resume_improvement && result.resume_improvement.improvement !== 0 ? (
                                <span>
                                    {result.resume_improvement.improvement > 0 ? "+" : ""}
                                    {result.resume_improvement.improvement}%
                                </span>
                            ) : (
                                <span>Baseline</span>
                            )}
                        </div>
                        <p className="metric-tile-subtext">
                            {result.resume_improvement?.improvement > 0
                                ? `Improved by ${result.resume_improvement.improvement}%`
                                : result.resume_improvement?.improvement < 0
                                ? `Dropped by ${Math.abs(result.resume_improvement.improvement)}%`
                                : "First analysis for target role"}
                        </p>
                    </div>

                    {(result.company || result.role) && (
                        <div className="summary-context-bar">
                            <span>🎯 Target:</span>
                            <strong>
                                {result.company || result.target_company}
                            </strong>
                            {(result.role || result.target_role) && (
                                <>
                                    <span>•</span>
                                    <span>{result.role || result.target_role}</span>
                                </>
                            )}
                        </div>
                    )}
                </div>
            </div>

            <div className="skill-analysis-card">
                <div className="skill-breakdown-pane">
                    <h2>📈 Score Breakdown</h2>

                    {result?.score_breakdown && (
                        <div className="score-breakdown-card">
                            {/* Skills */}
                            <div className="breakdown-item">
                                <div className="breakdown-header">
                                    <span>💻 Skills</span>
                                    <span>{result.score_breakdown.skills}%</span>
                                </div>
                                <div className="breakdown-track">
                                    <div
                                        className="breakdown-fill"
                                        style={{
                                            width: `${result.score_breakdown.skills}%`,
                                        }}
                                    ></div>
                                </div>
                            </div>

                            {/* Projects */}
                            <div className="breakdown-item">
                                <div className="breakdown-header">
                                    <span>📁 Projects</span>
                                    <span>{result.score_breakdown.projects}%</span>
                                </div>
                                <div className="breakdown-track">
                                    <div
                                        className="breakdown-fill"
                                        style={{
                                            width: `${result.score_breakdown.projects}%`,
                                        }}
                                    ></div>
                                </div>
                            </div>

                            {/* Education */}
                            <div className="breakdown-item">
                                <div className="breakdown-header">
                                    <span>🎓 Education</span>
                                    <span>{result.score_breakdown.education}%</span>
                                </div>
                                <div className="breakdown-track">
                                    <div
                                        className="breakdown-fill"
                                        style={{
                                            width: `${result.score_breakdown.education}%`,
                                        }}
                                    ></div>
                                </div>
                            </div>

                            {/* Certifications */}
                            <div className="breakdown-item">
                                <div className="breakdown-header">
                                    <span>📜 Certifications</span>
                                    <span>{result.score_breakdown.certifications}%</span>
                                </div>
                                <div className="breakdown-track">
                                    <div
                                        className="breakdown-fill"
                                        style={{
                                            width: `${result.score_breakdown.certifications}%`,
                                        }}
                                    ></div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                <div className="skill-matrix-pane">
                    <div className="skill-group-section">
                        <div className="skill-group-header">
                            <span>✅ Matched Skills</span>
                            <span>{(result.matched_skills || []).length}</span>
                        </div>
                        <div className="skills-list">
                            {result.matched_skills?.length > 0 ? (
                                result.matched_skills.map((skill, index) => (
                                    <span
                                        key={index}
                                        className="matched-badge"
                                    >
                                        {skill}
                                    </span>
                                ))
                            ) : (
                                <div className="empty-state">
                                    🎯 No matching skills found
                                </div>
                            )}
                        </div>
                    </div>

                    <div className="skill-group-section">
                        <div className="skill-group-header">
                            <span>⚠️ Missing Skills</span>
                            <span>{(result.missing_skills || []).length}</span>
                        </div>
                        <div className="skills-list">
                            {result.missing_skills?.length > 0 ? (
                                result.missing_skills.map((skill, index) => (
                                    <span
                                        key={index}
                                        className="missing-badge"
                                    >
                                        {skill}
                                    </span>
                                ))
                            ) : (
                                <div className="empty-state success">
                                    🎉 No missing skills
                                </div>
                            )}
                        </div>
                    </div>

                    <div className="skill-group-section">
                        <div className="skill-group-header">
                            <span>🛠️ Detected Skills</span>
                            <span>{(result.resume_summary?.skills || []).length}</span>
                        </div>
                        <div className="skills-list">
                            {result.resume_summary?.skills?.length > 0 ? (
                                result.resume_summary.skills.map((skill, index) => (
                                    <span
                                        key={index}
                                        className="detected-badge"
                                    >
                                        {skill}
                                    </span>
                                ))
                            ) : (
                                <div className="empty-state">
                                    📄 No detected skills extracted
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>

            {/* ==========================================================
               SECTION 1: RESUME OVERVIEW
            ========================================================== */}
            <div className="results-overview-section">
                <div className="section-header">
                    <h2>📋 Resume Overview</h2>
                </div>

                <div className="overview-grid">
                    {/* Resume Health */}
                    <div className="result-panel health-panel">
                        <h3>🩺 Resume Health Report</h3>
                        <div className="health-list">
                            {result.resume_health?.length > 0 ? (
                                result.resume_health.map((item, index) => (
                                    <div
                                        key={index}
                                        className="health-item"
                                    >
                                        {item}
                                    </div>
                                ))
                            ) : (
                                <div className="empty-state">
                                    🩺 No health report available
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Education */}
                    <div className="result-panel education-panel">
                        <h3>🎓 Education</h3>
                        <div className="modern-list">
                            {result.resume_summary?.education?.length > 0 ? (
                                result.resume_summary.education.map((item, index) => (
                                    <div
                                        key={index}
                                        className="modern-item"
                                    >
                                        <span className="modern-dot">🎓</span>
                                        <span>{item}</span>
                                    </div>
                                ))
                            ) : (
                                <div className="empty-state">
                                    🎓 No education detected
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Certifications */}
                    <ResultCard
                        title="📜 Certifications"
                        items={result.resume_summary?.certifications}
                        emptyMessage="📜 No certifications detected"
                        className="certifications-panel"
                    />
                </div>
            </div>

            {/* ==========================================================
               SECTION 2: CAREER & RESUME INSIGHTS
            ========================================================== */}
            <div className="results-insights-section">
                <div className="section-header">
                    <h2>💡 Career & Resume Insights</h2>
                </div>

                <div className="insights-grid">
                    {/* Projects */}
                    <ResultCard
                        title="💼 Projects"
                        items={result.resume_summary?.projects}
                        emptyMessage="📁 No projects detected"
                        className="projects-panel"
                    />

                    {/* Top Career Matches */}
                    <div className="result-panel roles-panel">
                        <h3>🎯 Top Career Matches</h3>
                        <div className="modern-list">
                            {(result.top_matches || []).length > 0 ? (
                                result.top_matches.map((match, index) => {
                                    const matchScore = match.match_score ?? match.confidence ?? 0;
                                    return (
                                        <div
                                            key={index}
                                            className="modern-item"
                                        >
                                            <span className="modern-dot">
                                                {index === 0
                                                    ? "🥇"
                                                    : index === 1
                                                    ? "🥈"
                                                    : "🥉"}
                                            </span>

                                            <div style={{ width: "100%" }}>
                                                <strong>
                                                    {match.role}
                                                </strong>
                                                <br />
                                                <small>
                                                    Skill Match: {matchScore}%
                                                </small>

                                                {index === 0 && (
                                                    <div
                                                        style={{
                                                            marginTop: "6px",
                                                            fontSize: "12.5px",
                                                            color: "var(--text-secondary)"
                                                        }}
                                                    >
                                                        Top match based on your detected technical skills.
                                                    </div>
                                                )}

                                                <div
                                                    style={{
                                                        marginTop: "6px",
                                                        height: "6px",
                                                        borderRadius: "20px",
                                                        background: "var(--border)",
                                                        overflow: "hidden"
                                                    }}
                                                >
                                                    <div
                                                        style={{
                                                            width: `${matchScore}%`,
                                                            height: "100%",
                                                            background: "var(--primary)"
                                                        }}
                                                    ></div>
                                                </div>
                                            </div>
                                        </div>
                                    );
                                })
                            ) : (
                                <div className="empty-state">
                                    🎯 No career matches available
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Suggestions */}
                    <ResultCard
                        title="💡 Suggestions"
                        items={result.suggestions}
                        emptyMessage="🎉 No suggestions needed"
                        className="suggestions-panel"
                    />
                </div>
            </div>
</div>

<div className="download-report">
    <button
        className="download-btn btn btn-primary"
        onClick={() => generatePDF(result)}
    >
        📄 Download PDF Report
    </button>
</div>
    </>
    );
}

export default ResultsSection;