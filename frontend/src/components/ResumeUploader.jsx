function ResumeUploader({
    file,
    setFile,
    fileInputRef,
    loading,
}) {
    return (
        <div className="upload-box">
            <label
                htmlFor="resume-upload"
                className={`upload-btn ${loading ? "disabled" : ""}`}
                role="button"
                tabIndex={loading ? -1 : 0}
                onKeyDown={(e) => {
                    if ((e.key === "Enter" || e.key === " ") && !loading && fileInputRef?.current) {
                        e.preventDefault();
                        fileInputRef.current.click();
                    }
                }}
            >
                <span className="upload-btn-icon" aria-hidden="true">📤</span>
                <span className="upload-btn-text">Upload Resume</span>
            </label>

            <input
                id="resume-upload"
                ref={fileInputRef}
                type="file"
                disabled={loading}
                accept=".pdf"
                style={{ display: "none" }}
                onChange={(e) => {
                    if (e.target.files && e.target.files[0]) {
                        setFile(e.target.files[0]);
                    }
                }}
            />

            <div className="upload-status-info">
                <div className={`upload-file-name ${file ? "has-file" : "is-empty"}`}>
                    {file ? (
                        <>
                            <span className="file-icon" aria-hidden="true">📄</span>
                            <span className="file-name-text" title={file.name}>{file.name}</span>
                        </>
                    ) : (
                        <span className="file-placeholder-text">No resume selected yet</span>
                    )}
                </div>

                {file && (
                    <span className="upload-success">
                        <span className="success-icon" aria-hidden="true">✓</span>
                        <span className="success-text">Ready for Analysis</span>
                    </span>
                )}
            </div>

            <div className="upload-helper">
                <span className="helper-format">PDF</span>
                <span className="helper-dot" aria-hidden="true">•</span>
                <span className="helper-size">Max 5 MB</span>
            </div>
        </div>
    );
}

export default ResumeUploader;