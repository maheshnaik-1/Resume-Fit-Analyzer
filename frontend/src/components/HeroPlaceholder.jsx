import "../styles/hero.css";

function HeroPlaceholder({
    file,
    fileInputRef,
}) {
    const handleUploadClick = () => {
        if (fileInputRef?.current) {
            fileInputRef.current.click();
        }
    };

    return (
        <div className="hero-workspace">
            <div className="hero-header">
                <span className="hero-badge">🎯 Resume Benchmarking</span>
                <h1 className="hero-title">Optimize Your Resume for Your Target Role</h1>
                <p className="hero-subtitle">
                    Select your target company and role, upload your resume, and review your resume compatibility and skill gaps.
                </p>
            </div>

            <div className="hero-workflow-grid">
                <div className="workflow-card">
                    <span className="workflow-step-num">1</span>
                    <div className="workflow-icon">🏢</div>
                    <h3>Target Selection</h3>
                    <p>Choose your target company and role.</p>
                </div>

                <div className="workflow-card">
                    <span className="workflow-step-num">2</span>
                    <div className="workflow-icon">📄</div>
                    <h3>Smart Parsing</h3>
                    <p>Extract skills, education, projects, and credentials.</p>
                </div>

                <div className="workflow-card">
                    <span className="workflow-step-num">3</span>
                    <div className="workflow-icon">🚀</div>
                    <h3>ATS Insights</h3>
                    <p>Get ATS scoring, skill gaps, and resume insights.</p>
                </div>
            </div>

            <div
                className={`hero-dropzone ${file ? "has-file" : ""}`}
                onClick={handleUploadClick}
                role="button"
                tabIndex={0}
                onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " ") {
                        e.preventDefault();
                        handleUploadClick();
                    }
                }}
            >
                <div className="dropzone-icon">
                    {file ? "📄" : "📤"}
                </div>

                {file ? (
                    <div className="dropzone-file-info">
                        <h3>{file.name}</h3>
                        <p className="dropzone-status">✓ Resume Attached • Ready for Analysis</p>
                        <span className="dropzone-change-hint">Click to choose a different PDF</span>
                    </div>
                ) : (
                    <div className="dropzone-prompt">
                        <h3>Click to Select Resume</h3>
                        <p>Supported format: PDF • Max 5 MB</p>
                        <span className="dropzone-browse-btn">Browse Files</span>
                    </div>
                )}
            </div>
        </div>
    );
}

export default HeroPlaceholder;