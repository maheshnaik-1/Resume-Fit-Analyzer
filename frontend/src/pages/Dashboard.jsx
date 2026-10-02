import "../styles/Home.css";
import { useState, useEffect, useRef } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { jobDescriptions, companyRoles } from "../jobData";
import NotificationPopup from "../components/NotificationPopup";
import ProfileMenu from "../components/ProfileMenu";
import StatsCards from "../components/StatsCards";
import useResumeAnalysis from "../hooks/useResumeAnalysis";
import ATSChart from "../components/ATSChart";
import CompanyChart from "../components/CompanyChart";
import RoleChart from "../components/RoleChart";
import DashboardBody from "../components/DashboardBody";
import Footer from "../components/Footer";
import { API_BASE_URL } from "../utils/api";
import { useTheme } from "../context/ThemeContext";

function Dashboard() {
    const { isDark, toggleTheme } = useTheme();
    const location = useLocation();
    const navigate = useNavigate();
    const [showResults, setShowResults] = useState(false);
    const [file, setFile] = useState(() => {
        const fileName = location.state?.dashboardState?.fileName;
        return fileName ? { name: fileName } : null;
    });
    const [jobDescription, setJobDescription] = useState("");
    const [company, setCompany] = useState(
        () => location.state?.dashboardState?.company || ""
    );
    const [role, setRole] = useState(
        () => location.state?.dashboardState?.role || ""
    );
    const [showProfileMenu, setShowProfileMenu] = useState(false);
    const [requiredSkills, setRequiredSkills] = useState(
        () => location.state?.dashboardState?.requiredSkills || []
    );
    const [isHistoryPreview, setIsHistoryPreview] = useState(
        () => Boolean(location.state?.analysisResult)
    );
    useEffect(() => {
        const navigation =
            performance.getEntriesByType("navigation")[0];

        if (navigation?.type === "reload") {
            window.history.replaceState(
                { use: null },
                "",
                window.location.pathname
            );
        }
    }, []);
    const [history, setHistory] = useState([]);
    const fileInputRef = useRef(null);
    const [showNotifications, setShowNotifications] = useState(false);
    const profileMenuRef = useRef(null);
    const profileBtnRef = useRef(null);
    const notificationRef = useRef(null);
    const notificationBtnRef = useRef(null);
    const [notifications, setNotifications] = useState([]);

  const unreadCount = notifications.filter(
    (item) => item.is_read === 0
  ).length;
  
  useEffect(() => {
      function handleClickOutside(event) {
          const clickedProfile =
              profileMenuRef.current?.contains(event.target) ||
              profileBtnRef.current?.contains(event.target);

          if (!clickedProfile) {
              setShowProfileMenu(false);
          }

          const clickedNotification =
              notificationRef.current?.contains(event.target) ||
              notificationBtnRef.current?.contains(event.target);

          if (!clickedNotification) {
              setShowNotifications(false);
          }
      }

      function handleEscape(event) {
          if (event.key === "Escape") {
              setShowProfileMenu(false);
              setShowNotifications(false);
          }
      }

      document.addEventListener("mousedown", handleClickOutside);
      window.addEventListener("keydown", handleEscape);

      return () => {
          document.removeEventListener("mousedown", handleClickOutside);
          window.removeEventListener("keydown", handleEscape);
      };

  }, []);

  const [stats, setStats] = useState({
    total_analyses: 0,
    highest_score: 0,
    average_score: 0,
    companies: 0,
  });

  const fetchHistory = async () => {

      const email = localStorage.getItem("userEmail");

      if (!email) return;

      try {

          const response = await fetch(
              `${API_BASE_URL}/history/${email}`
          );

          if (!response.ok) return;

          const data = await response.json();

          if (Array.isArray(data)) {
              setHistory(data);
          }

      } catch (error) {

          console.log(error);

      }
  };

  const fetchDashboardStats = async () => {
    const email = localStorage.getItem("userEmail");

    if (!email) return;

    try {
      const response = await fetch(
        `${API_BASE_URL}/dashboard-stats/${email}`
      );

      if (!response.ok) return;

      const data = await response.json();

      if (data && typeof data === "object" && "total_analyses" in data) {
        setStats(data);
      }
    } catch (error) {
      console.log(error);
    }
  };

  const fetchNotifications = async () => {

        const email = localStorage.getItem("userEmail");

        if (!email) return;

        try {

            const response = await fetch(
                `${API_BASE_URL}/notifications/${email}`
            );

            if (!response.ok) return;

            const data = await response.json();

            if (Array.isArray(data)) {
                setNotifications(data);
            }

        } catch (error) {
            console.log(error);
        }

    };

    const markNotificationsAsRead = async () => {

        const email = localStorage.getItem("userEmail");

        if (!email) return;

        try {

            const response = await fetch(
                `${API_BASE_URL}/notifications/read/${email}`,
                {
                    method: "POST",
                }
            );

            if (response.ok) {
                fetchNotifications();
            }

        } catch (error) {
            console.log(error);
        }
    };

    const initialResult =
        location.state?.analysisResult ||
        location.state?.dashboardState?.result ||
        null;

    const {
        loading,
        loadingStep,
        result,
        setResult,
        successMessage,
        analyzeResume,
    } = useResumeAnalysis({
        initialResult,
        file,
        setFile,
        company,
        role,
        jobDescription,
        fileInputRef,
        fetchHistory,
        fetchDashboardStats,
        fetchNotifications,
        setIsHistoryPreview,
    });

    useEffect(() => {
        if (!result) return;

        const timer = setTimeout(() => {
            setShowResults(true);
        }, 80);

        return () => {
            clearTimeout(timer);
            setShowResults(false);
        };
    }, [result]);

  const handleLogout = () => {
      localStorage.removeItem("loggedIn");
      navigate("/login");
  };

    useEffect(() => {

        async function loadDashboard() {

            await fetchHistory();

            await fetchDashboardStats();

            await fetchNotifications();

        }

        loadDashboard();

    }, []);

    useEffect(() => {
        const handleEsc = (e) => {
            if (e.key === "Escape" && isHistoryPreview) {
                setResult(null);
                setIsHistoryPreview(false);

                navigate("/dashboard", {
                    replace: true,
                    state: null,
                });
            }
        };

        window.addEventListener("keydown", handleEsc);

        return () => {
            window.removeEventListener("keydown", handleEsc);
        };
    }, [isHistoryPreview, navigate, setResult]);

return (
<div className="container">

    <div className="dashboard-header">

        <div className="header-left">
            <h1>🎯 Resume Fit Analyzer</h1>
            <p>Optimize your resume for your target company and role.</p>
        </div>

        <div className="header-right">
            <div className="header-actions">

                <button
                    ref={notificationBtnRef}
                    type="button"
                    className="icon-btn notification-btn"
                    onClick={() => {
                        setShowProfileMenu(false);
                        setShowNotifications((prev) => {
                            const next = !prev;
                            if (next) {
                                markNotificationsAsRead();
                            }
                            return next;
                        });
                    }}
                    title="Notifications"
                    aria-label="Notifications"
                    aria-expanded={showNotifications}
                >
                    <svg
                        className="header-icon"
                        width="18"
                        height="18"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        aria-hidden="true"
                    >
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
                        <path d="M13.73 21a2 2 0 0 1-3.46 0" />
                    </svg>

                    {unreadCount > 0 && (
                        <span className="notification-badge">
                            {unreadCount > 99 ? "99+" : unreadCount}
                        </span>
                    )}
                </button>

                <div ref={notificationRef}>
                    <NotificationPopup
                        showNotifications={showNotifications}
                        notifications={notifications}
                    />
                </div>

                <button
                    type="button"
                    className="icon-btn theme-toggle-btn"
                    onClick={toggleTheme}
                    title={isDark ? "Switch to Light Theme" : "Switch to Dark Theme"}
                    aria-label={isDark ? "Switch to Light Theme" : "Switch to Dark Theme"}
                >
                    {isDark ? (
                        <svg
                            className="header-icon"
                            width="18"
                            height="18"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            aria-hidden="true"
                        >
                            <circle cx="12" cy="12" r="5" />
                            <line x1="12" y1="1" x2="12" y2="3" />
                            <line x1="12" y1="21" x2="12" y2="23" />
                            <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" />
                            <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
                            <line x1="1" y1="12" x2="3" y2="12" />
                            <line x1="21" y1="12" x2="23" y2="12" />
                            <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" />
                            <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
                        </svg>
                    ) : (
                        <svg
                            className="header-icon"
                            width="18"
                            height="18"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            aria-hidden="true"
                        >
                            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
                        </svg>
                    )}
                </button>

                <button
                    ref={profileBtnRef}
                    type="button"
                    className="profile-circle"
                    onClick={() => {
                        setShowNotifications(false);
                        setShowProfileMenu((prev) => !prev);
                    }}
                    title="User Profile"
                    aria-label="User Profile"
                    aria-expanded={showProfileMenu}
                >
                    {localStorage.getItem("name")?.charAt(0).toUpperCase()}
                </button>

                <div ref={profileMenuRef}>
                    <ProfileMenu
                        showProfileMenu={showProfileMenu}
                        handleLogout={handleLogout}
                        result={result}
                        company={company}
                        role={role}
                        requiredSkills={requiredSkills}
                        file={file}
                    />
                </div>

            </div>
        </div>
    </div>

    <StatsCards stats={stats} />

    <DashboardBody
        showResults={showResults}

        loading={loading}
        loadingStep={loadingStep}
        result={result}

        company={company}
        setCompany={setCompany}

        role={role}
        setRole={setRole}

        requiredSkills={requiredSkills}
        setRequiredSkills={setRequiredSkills}

        setJobDescription={setJobDescription}

        file={file}
        setFile={setFile}
        fileInputRef={fileInputRef}

        analyzeResume={analyzeResume}
        successMessage={successMessage}

        jobDescriptions={jobDescriptions}
        companyRoles={companyRoles}
    />

{result && (

    <div className="analytics-section">

        <div className="analytics-container">

            <h2 className="analytics-heading">
                📊 Resume Analytics Dashboard
            </h2>

            <div className="analytics-grid">

                <CompanyChart
                    history={history}
                />

                <RoleChart
                    history={history}
                />

            </div>

            <div className="analytics-full">

                <ATSChart
                    history={history}
                />

            </div>

        </div>

    </div>

)}

<Footer
    result={result}
    company={company}
    role={role}
    requiredSkills={requiredSkills}
    file={file}
/>

</div>
        
);
}

export default Dashboard;