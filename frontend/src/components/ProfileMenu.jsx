import { useState } from "react";
import { useNavigate } from "react-router-dom";

function ProfileMenu({
    showProfileMenu,
    handleLogout,
    theme,
    setTheme,

    result,
    company,
    role,
    requiredSkills,
    file
}) {
    const [showThemeMenu, setShowThemeMenu] = useState(false);
    const navigate = useNavigate();

    if (!showProfileMenu) return null;
    
    return (
    <div className="profile-menu">

        <div className="profile-item">

            <span className="profile-icon">👤</span>
            <div>
                <strong>{localStorage.getItem("name")}</strong>
            </div>

        </div>

        <div className="profile-item">

            <span className="profile-icon">📧</span>
            <div>
                {localStorage.getItem("userEmail")}
            </div>

        </div>
        
        <hr />

        <div className="theme-menu-wrapper">

            <button
                className="profile-theme-btn btn btn-secondary"
                onClick={() => setShowThemeMenu(!showThemeMenu)}
            >
                <span>🎨 Theme</span>
                <span className="theme-arrow">
                    {showThemeMenu ? "◀" : "▶"}
                </span>
            </button>

            {showThemeMenu && (
                <div className="theme-submenu">
                    <div className="theme-title">
                        Appearance
                    </div>
                    
                    <button 
                        className="btn btn-theme"
                        onClick={() => {
                            setTheme("ocean");
                            setShowThemeMenu(false);
                        }}
                    >
                        🔵 Ocean Blue
                    </button>

                    <button
                        className="btn btn-theme"
                        onClick={() => {
                            setTheme("emerald");
                            setShowThemeMenu(false);
                        }}
                    >
                        🟢 Emerald
                    </button>

                    <button
                        className="btn btn-theme"
                        onClick={() => {
                            setTheme("purple");
                            setShowThemeMenu(false);
                        }}
                    >
                        🟣 Purple
                    </button>

                    <button
                        className="btn btn-theme"
                        onClick={() => {
                            setTheme("sunset");
                            setShowThemeMenu(false);
                        }}
                    >
                        🟠 Sunset
                    </button>

                    <button
                        className="btn btn-theme"
                        onClick={() => {
                            setTheme("rose");
                            setShowThemeMenu(false);
                        }}
                    >
                        🌸 Rose
                    </button>
                    
                    <button
                        className="btn btn-theme"
                        onClick={() => {
                            setTheme("dark");
                            setShowThemeMenu(false);
                        }}
                    >
                        🌙 Dark
                    </button>

                </div>
            )}

        </div>

        <button
            className="profile-history-btn btn btn-secondary"
            onClick={() =>
                navigate("/history", {
                    state: {
                        dashboardState: {
                            result,
                            company,
                            role,
                            requiredSkills,
                            fileName: file?.name,
                        },
                    },
                })
            }
        >
            📜 History
        </button>

        <button
            className="logout-menu-btn btn btn-danger"
            onClick={handleLogout}
        >
            🚪 Logout
        </button>

        <div className="profile-version">
            Resume Fit Analyzer v1.0
            <br />
            ✨ Version 2.0 in Development
        </div>
    
    </div>
    );
}

export default ProfileMenu;