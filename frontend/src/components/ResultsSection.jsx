import ResultCard from "./ResultCard";
import AnimatedNumber from "./AnimatedNumber";
import { generatePDF } from "../utils/pdfGenerator";

function ResultsSection({
    result,
}) {
    return (
        <>
        <div className="results-container results-fade">

              <div className="result-card ats-card">
  <h2>📊 ATS Score</h2>

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

  {result.ats_score >= 60 &&
    result.ats_score < 80 && (
      <p className="good badge-pop">
        🟡 Good Resume
      </p>
  )}

  {result.ats_score < 60 && (
    <p className="poor badge-pop">
      🔴 Needs Improvement
    </p>
  )}

<div className="score-summary">

    <div className="score-summary-item matched">

        <div>
            <span className="summary-title">
                🟢 Matched Skills
            </span>

            <small>
                Skills found in your resume
            </small>
        </div>

        <strong>
            {result.matched_skills.length}
        </strong>

    </div>

    <div className="score-summary-item missing">

        <div>
            <span className="summary-title">
                🔴 Missing Skills
            </span>

            <small>
                Skills to improve
            </small>
        </div>

        <strong>
            {result.missing_skills.length}
        </strong>

    </div>

</div>

<p className="score-summary">
  {(result.matched_skills || []).length} / {(result.job_skills || []).length}
  {" "}Skills Matched
</p>

  <div className="progress-bar">
    <div
      className="progress-fill"
      style={{
        width: `${
          (
            ((result.matched_skills || []).length /
            Math.max((result.job_skills || []).length, 1))
          ) * 100
        }%`,
        background:
          result.ats_score >= 80
            ? "#22c55e"
            : result.ats_score >= 60
            ? "#f59e0b"
            : "#ef4444",
      }}
    ></div>
  </div>
</div>

<div className="result-card breakdown-card">
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
                        width: `${result.score_breakdown.skills}%`
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
                        width: `${result.score_breakdown.projects}%`
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
                        width: `${result.score_breakdown.education}%`
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
                        width: `${result.score_breakdown.certifications}%`
                    }}
                ></div>
            </div>

        </div>

    </div>

    )}

</div>

<div className="result-card best-card">
  <h2>🏆 Best Resume Record</h2>

  {result.best_resume ? (
        <>
            <div className="best-resume-content">

                <h3 className="company-name">
                     {result.best_resume.company}
                </h3>

                <p className="role-name">
                    {result.best_resume.role}
                </p>

                <div className="best-score">
                    ⭐{" "}
                    <AnimatedNumber
                        value={Number(result.best_resume.ats_score)}
                        decimals={2}
                        suffix="%"
                        duration={1000}
                        fromZero={true}
                    />
                </div>

                <div className="best-date">
                    🗓{" "}
                    {new Date(result.best_resume.date).toLocaleDateString("en-GB", {
                        day: "2-digit",
                        month: "short",
                        year: "numeric",
                    })}
                </div>

            </div>
        </>
    ) : (
        <p>No previous records available.</p>
    )}
</div>

{result.resume_improvement && (
  <div className="result-card improvement-card">
    <h2>📈 Resume Improvement</h2>

    {result.resume_improvement.history?.map((item, index) => (
        <div
            className="improvement-row"
            key={index}
        >
            <div>
                <strong>
                    {index === 0
                        ? "Oldest"
                        : index === result.resume_improvement.history.length - 1
                        ? "Latest"
                        : "Previous"}
                </strong>

                <br />

                <small>
                    {new Date(item.date).toLocaleDateString("en-GB", {
                        day: "2-digit",
                        month: "short",
                        year: "numeric",
                    })}
                    {" • "}
                    {new Date(item.date).toLocaleTimeString("en-US", {
                        hour: "numeric",
                        minute: "2-digit",
                        hour12: true,
                    })}
                </small>
            </div>

            <strong>
                <AnimatedNumber
                    value={Number(item.score)}
                    decimals={2}
                    suffix="%"
                    duration={900}
                    fromZero={true}
                />
            </strong>
        </div>
    ))}

    <div className="improvement-badge">
      {result.resume_improvement.improvement > 0
        ? `📈 Improved by ${result.resume_improvement.improvement}%`
        : result.resume_improvement.improvement < 0
        ? `📉 Dropped by ${Math.abs(result.resume_improvement.improvement)}%`
        : "➖ No Change"}
    </div>
  </div>
)}

    <div className="result-card matched-card">
      <h2>✅ Matched Skills</h2>

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

    <div className="result-card missing-card">
      <h2>⚠️ Missing Skills</h2>

        <div className="skills-list">

            {result.missing_skills?.length > 0 ? (

                result.missing_skills.map((skill,index)=>(

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

    <div className="result-card time-card">
    <h2>⚡ Analysis Time</h2>

    {(() => {
        const analysisTime = Number(result.analysis_time);

        return (
            <>
                <div className="analysis-time">
                    <AnimatedNumber
                        value={analysisTime}
                        decimals={2}
                        suffix=" sec"
                        duration={800}
                        fromZero={true}
                    />
                </div>

                <div className="analysis-status">
                    {
                        analysisTime < 0.20
                            ? "🚀 Lightning Fast"
                            : analysisTime < 0.50
                            ? "⚡ Fast Processing"
                            : "⏳ Processing Complete"
                    }
                </div>
            </>
        );
    })()}
</div>

<div className="result-card health-card">

    <h2>🩺 Resume Health Report</h2>

    <div className="health-list">

        {result.resume_health?.map((item, index) => (

            <div
                key={index}
                className="health-item"
            >
                {item}
            </div>

        ))}

    </div>

</div>

<div className="result-card detected-card">
    <h2>🛠️ Detected Skills</h2>

    <div className="modern-list">
        {result.resume_summary?.skills?.map((skill, index) => (
            <div
                key={index}
                className="modern-item"
            >
                <span className="modern-dot">✓</span>

                <span>{skill}</span>
            </div>
        ))}
    </div>
</div>

<div className="result-card roles-card">

    <h2>🎯 Top Career Matches</h2>

    <div className="modern-list">

        {((result.top_matches || result.top_predictions) || []).map((match, index) => {
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

                    <div
                        style={{ width: "100%" }}
                    >

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
                                    marginTop: "8px",
                                    fontSize: "13px",
                                    color: "var(--text-light)"
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
                                background: "#e5e7eb",
                                overflow: "hidden"
                            }}
                        >

                            <div
                                style={{
                                    width: `${matchScore}%`,
                                    height: "100%",
                                    background: "#3b82f6"
                                }}
                            ></div>

                        </div>

                    </div>

                </div>
            );
        })}

    </div>

</div>

<div className="result-card education-card">
    <h2>🎓 Education</h2>

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

<div className="certifications-card certifications-section">
    <ResultCard
        title="📜 Certifications"
        items={result.resume_summary?.certifications}
        emptyMessage="📜 No certifications detected"
    />
</div>

<div className="projects-card projects-section">
    <ResultCard
        title="💼 Projects"
        items={result.resume_summary?.projects}
        emptyMessage="📁 No projects detected"
    />
</div>

<div className="suggestions-card suggestions-section">
    <ResultCard
        title="💡 Suggestions"
        items={result.suggestions}
    />
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