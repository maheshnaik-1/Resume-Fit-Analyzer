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
        
                  <option value="Google">Google</option>
                  <option value="Microsoft">Microsoft</option>
                  <option value="Amazon">Amazon</option>
                  <option value="Meta">Meta</option>
                  <option value="Apple">Apple</option>
        
                  <option value="Infosys">Infosys</option>
                  <option value="TCS">TCS</option>
                  <option value="Wipro">Wipro</option>
                  <option value="Accenture">Accenture</option>
                  <option value="Deloitte">Deloitte</option>
                </select>
        </>
    );
}

export default CompanySelector;