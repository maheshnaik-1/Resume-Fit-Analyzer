import "../styles/hero.css";

function HeroPlaceholder() {
    return (
        <div className="hero">

            <div className="hero-icon">
                🎯
            </div>

            <h1>Resume Fit Analyzer</h1>

            <p className="hero-subtitle">
                Upload your resume to evaluate ATS compatibility, compare required skills, and receive actionable resume insights.
            </p>

            <div className="hero-features">

                <div className="feature-card">
                    📊
                    <span>ATS Prediction</span>
                </div>

                <div className="feature-card">
                    🧠
                    <span>Skill Matching</span>
                </div>

                <div className="feature-card">
                    💼
                    <span>Company Insights</span>
                </div>

                <div className="feature-card">
                    📈
                    <span>Resume Analytics</span>
                </div>

                <div className="feature-card">
                    ✨
                    <span>Resume Suggestions</span>
                </div>

            </div>

            <div className="hero-upload-box">

                <div className="upload-icon">
                    📄
                </div>

                <h2>No Resume Uploaded</h2>

                <p>
                    Upload your resume to receive an ATS score, skill matching, company insights, and personalized improvement suggestions.
                </p>

                <div className="upload-info">
                    PDF Only • Max 5 MB
                </div>

            </div>

        </div>
    );
}

export default HeroPlaceholder;