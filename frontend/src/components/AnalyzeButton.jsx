function AnalyzeButton({
    analyzeResume,
    loading,
    loadingStep,
    successMessage,
}) {
    return (
        <>
            <button
                className="btn btn-primary"
                onClick={analyzeResume}
                disabled={loading}
            >
                {loading ? "⏳ Analyzing Resume..." : "🚀 Analyze Resume"}
            </button>

            {loading && (
                <div className="loading-card">

                    <div className="loading-title">
                        📄 Resume Analyzing
                    </div>

                    <div className="loading-step">
                        {loadingStep}
                    </div>

                    <div className="loading-bar">
                        <div className="loading-progress"></div>
                    </div>

                    <small>
                        Please wait while your report is being prepared...
                    </small>

                </div>
            )}

            {successMessage && (
                <p className="success-message">
                    {successMessage}
                </p>
            )}
        </>
    );
}

export default AnalyzeButton;