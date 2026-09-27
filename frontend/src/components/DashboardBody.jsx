import CompanySelector from "./CompanySelector";
import RoleSelector from "./RoleSelector";
import RequiredSkills from "./RequiredSkills";
import ResumeUploader from "./ResumeUploader";
import AnalyzeButton from "./AnalyzeButton";
import ResultsSection from "./ResultsSection";
import HeroPlaceholder from "./HeroPlaceholder";
import DashboardSkeleton from "./DashboardSkeleton";

function DashboardBody({
    showResults,
    loading,
    loadingStep,
    result,

    company,
    setCompany,

    role,
    setRole,

    requiredSkills,
    setRequiredSkills,

    setJobDescription,

    file,
    setFile,
    fileInputRef,

    analyzeResume,
    successMessage,

    jobDescriptions,
    companyRoles
}) {

    return (

<div className="dashboard-grid">

    <div className={`sidebar-area ${loading ? "loading-sidebar" : ""}`}>

        <div className="left-panel">

            <div className="card">

                <div className="setup-header">
                    <h2>⚡ Analysis Setup</h2>
                    <p>Select target role & upload resume</p>
                </div>

                <CompanySelector
                    company={company}
                    setCompany={setCompany}
                    setRole={setRole}
                    setJobDescription={setJobDescription}
                    setRequiredSkills={setRequiredSkills}
                    loading={loading}
                />

                <RoleSelector
                    company={company}
                    role={role}
                    setRole={setRole}
                    jobDescriptions={jobDescriptions}
                    companyRoles={companyRoles}
                    setRequiredSkills={setRequiredSkills}
                    setJobDescription={setJobDescription}
                    loading={loading}
                />

                <RequiredSkills
                    requiredSkills={requiredSkills}
                />

                <ResumeUploader
                    file={file}
                    setFile={setFile}
                    fileInputRef={fileInputRef}
                    loading={loading}
                />

                <AnalyzeButton
                    analyzeResume={analyzeResume}
                    loading={loading}
                    loadingStep={loadingStep}
                    successMessage={successMessage}
                />

            </div>

        </div>

    </div>

    <div className="results-area">

        <div className="right-panel">

            {loading ? (

                <DashboardSkeleton />

            ) : result ? (

                <div
                    className={`results-transition ${
                        showResults ? "show" : ""
                    }`}
                >
                    <ResultsSection
                        result={result}
                    />
                </div>

            ) : (

                <HeroPlaceholder
                    file={file}
                    fileInputRef={fileInputRef}
                />

            )}

        </div>

    </div>

</div>

    );
}

export default DashboardBody;