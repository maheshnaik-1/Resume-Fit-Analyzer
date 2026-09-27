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
import { API_BASE_URL } from "../utils/api";

function Dashboard() {
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
    const notificationRef = useRef(null);
    const [notifications, setNotifications] = useState([]);
    const [theme, setTheme] = useState(
        localStorage.getItem("theme") || "ocean"
    );

    useEffect(() => {
        localStorage.setItem("theme", theme);
    }, [theme]);

  const unreadCount = notifications.filter(
    (item) => item.is_read === 0
  ).length;
  
  useEffect(() => {
      function handleClickOutside(event) {

          if (
              profileMenuRef.current &&
              !profileMenuRef.current.contains(event.target)
          ) {
              setShowProfileMenu(false);
          }

          if (
              notificationRef.current &&
              !notificationRef.current.contains(event.target)
          ) {
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
<div className={`container theme-${theme}`}>

    <div className="dashboard-header">

        <div className="header-left">
            <h1>🎯 Resume Fit Analyzer</h1>
            <p>Optimize your resume for your target company and role.</p>
        </div>

        <div className="header-right">

            <button
                className="icon-btn"
                onClick={() => {

                    setShowProfileMenu(false);

                    const opening = !showNotifications;

                    setShowNotifications(opening);

                    if (opening) {
                        markNotificationsAsRead();
                    }

                }}
            >

                🔔

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
                className="profile-circle"
                onClick={() => {
                    setShowNotifications(false);
                    setShowProfileMenu(!showProfileMenu);
                }}
            >
                {localStorage.getItem("name")?.charAt(0).toUpperCase()}
            </button>

            <div ref={profileMenuRef}>
                <ProfileMenu
                    showProfileMenu={showProfileMenu}
                    handleLogout={handleLogout}
                    setTheme={setTheme}

                    result={result}
                    company={company}
                    role={role}
                    requiredSkills={requiredSkills}
                    file={file}
                />
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
                    theme={theme}
                />

                <RoleChart
                    history={history}
                    theme={theme}
                />

            </div>

            <div className="analytics-full">

                <ATSChart
                    history={history}
                    theme={theme}
                />

            </div>

        </div>

    </div>

)}

<footer className="dashboard-footer">

    <strong>
        Resume Fit Analyzer v1.0
    </strong>

    Built with React • FastAPI • SQLite • Python

    <div className="footer-version">
        Version 2.0 • Coming Soon
    </div>

</footer>

</div>
        
);
}

export default Dashboard;