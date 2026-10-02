import { useNavigate } from "react-router-dom";
import "../styles/profile.css";

function ProfileMenu({
    showProfileMenu,
    handleLogout,
    result,
    company,
    role,
    requiredSkills,
    file,
}) {
    const navigate = useNavigate();

    if (!showProfileMenu) return null;

    const name = localStorage.getItem("name");
    const email = localStorage.getItem("userEmail");
    const initial = name ? name.charAt(0).toUpperCase() : "U";

    const handleHistoryNavigation = () => {
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
        });
    };

    return (
        <div className="profile-menu" role="dialog" aria-label="User Account Menu">
            {/* 1. Profile Header Block */}
            <div className="profile-menu-header">
                <div className="profile-avatar-badge" aria-hidden="true">
                    {initial}
                </div>
                <div className="profile-user-details">
                    <span className="profile-user-name" title={name || "User"}>
                        {name || "User"}
                    </span>
                    <span className="profile-user-email" title={email || ""}>
                        {email || "user@example.com"}
                    </span>
                </div>
            </div>

            <hr className="profile-menu-divider" />

            {/* 2. Navigation Items */}
            <div className="profile-menu-nav">
                <button
                    type="button"
                    className="profile-menu-item"
                    onClick={handleHistoryNavigation}
                    aria-label="View Analysis History"
                >
                    <svg
                        className="menu-item-icon"
                        width="15"
                        height="15"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        aria-hidden="true"
                    >
                        <path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span>Analysis History</span>
                </button>
            </div>

            <hr className="profile-menu-divider" />

            {/* 3. Account Actions */}
            <div className="profile-menu-actions">
                <button
                    type="button"
                    className="profile-menu-item profile-logout-item"
                    onClick={handleLogout}
                    aria-label="Log Out"
                >
                    <svg
                        className="menu-item-icon"
                        width="15"
                        height="15"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        aria-hidden="true"
                    >
                        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                        <polyline points="16 17 21 12 16 7" />
                        <line x1="21" y1="12" x2="9" y2="12" />
                    </svg>
                    <span>Log out</span>
                </button>
            </div>

            {/* 4. Compact Version Metadata */}
            <div className="profile-menu-footer">
                <span>Resume Fit Analyzer v1.0</span>
            </div>
        </div>
    );
}

export default ProfileMenu;