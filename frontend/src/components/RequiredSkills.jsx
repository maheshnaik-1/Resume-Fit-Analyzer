function RequiredSkills({ requiredSkills }) {
    return (
        <>
        <label>📋 Required Skills</label>

        <div className="skills-list">

        {requiredSkills.length === 0 ? (

        <div className="empty-text">
            <span className="empty-icon">💡</span>

            <div>
                <strong>Select a Role</strong>
                <small>Required skills will appear here.</small>
            </div>
        </div>

        ) : (

        requiredSkills.map((skill,index)=>(
            <span
                key={index}
                className="skill-badge"
            >
                {skill}
            </span>
        ))

        )}

        </div>
        </>
    );
}

export default RequiredSkills;