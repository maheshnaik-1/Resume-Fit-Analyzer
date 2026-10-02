import { companyRoles } from "../jobData";

function CompanySelector({
    company,
    setCompany,
    setRole,
    setJobDescription,
    setRequiredSkills,
    loading,
}) {
    return (
        <>
            <label>Select Company</label>

            <select
                value={company}
                onChange={(e) => {
                    setCompany(e.target.value);
                    setRole("");
                    setJobDescription("");
                    setRequiredSkills([]);
                }}
                disabled={loading}
            >
                <option value="">Choose a Company</option>

                {Object.keys(companyRoles).map((companyName) => (
                    <option key={companyName} value={companyName}>
                        {companyName}
                    </option>
                ))}
            </select>
        </>
    );
}

export default CompanySelector;