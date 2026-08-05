function ResumeUploader({
    file,
    setFile,
    fileInputRef,
    loading,
}) {
    return (
        <>
            <label className="upload-title">
                📄 Resume Upload
            </label>

            <div className="upload-box">

                <label
                    htmlFor="resume-upload"
                    className="upload-btn btn btn-primary"
                >
                    📤 Upload Resume
                </label>

                <input
                    id="resume-upload"
                    ref={fileInputRef}
                    type="file"
                    disabled={loading}
                    accept=".pdf"
                    style={{ display: "none" }}
                    onChange={(e) => {
                        setFile(e.target.files[0]);
                    }}
                />

                <div className="upload-file-name">
                    {file ? (
                        <>
                            📄 <strong>{file.name}</strong>
                        </>
                    ) : (
                        "No resume selected yet"
                    )}
                </div>

                {file && (
                    <div className="upload-success">
                        🚀 Ready for AI Analysis
                    </div>
                )}

                <p className="upload-helper">
                    PDF • Max 5 MB
                </p>

            </div>
        </>
    );
}

export default ResumeUploader;