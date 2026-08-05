function RoleSelector({
    company,
    role,
    setRole,
    jobDescriptions,
    companyRoles,
    setRequiredSkills,
    setJobDescription,
    loading,
}) {
    return (
        <>
        <label>Select Role</label>

        <select
              value={role}
              disabled={!company || loading}
              onChange={(e) => {
              const selectedRole = e.target.value;

              setRole(selectedRole);

              if (
                company &&
                jobDescriptions[company] &&
                jobDescriptions[company][selectedRole]
              ) {
                const skills =
                  jobDescriptions[company][selectedRole];

                setRequiredSkills(skills);

                setJobDescription(
                  skills.join(" ")
                );
              }
            }}
          >

          <option value="">
            {company ? "Choose a Role" : "Select a Company First"}
          </option>

          {company &&
            companyRoles[company]?.map((roleOption, index) => (
              <option
                key={index}
                value={roleOption}
              >
                {roleOption}
              </option>
            ))
          }

        </select>
        </>
    );
}

export default RoleSelector;