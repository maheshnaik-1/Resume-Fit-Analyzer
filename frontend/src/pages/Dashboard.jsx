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
    const [showResults, setShowResults] = useState(false);
    const [file, setFile] = useState(null);
    const [jobDescription, setJobDescription] = useState("");
    const [company, setCompany] = useState("");
    const [role, setRole] = useState("");
    const [showProfileMenu, setShowProfileMenu] = useState(false);
    const [requiredSkills, setRequiredSkills] = useState([]);
    const location = useLocation();
    const navigate = useNavigate();
    const [isHistoryPreview, setIsHistoryPreview] = useState(false);
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
    const [search, setSearch] = useState("");
    const [sortBy, setSortBy] = useState("newest");
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

          const data = await response.json();

          if (Array.isArray(data)) {
              setHistory(data);
          } else {
              setHistory([]);
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

      const data = await response.json();

      setStats(data);
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

            const data = await response.json();

            setNotifications(data);

        } catch (error) {
            console.log(error);
        }

    };

    const markNotificationsAsRead = async () => {

        const email = localStorage.getItem("userEmail");

        if (!email) return;

        try {

            await fetch(
                `${API_BASE_URL}/notifications/read/${email}`,
                {
                    method: "POST",
                }
            );

            fetchNotifications();

        } catch (error) {
            console.log(error);
        }
    };

  const {
        loading,
        loadingStep,
        result,
        setResult,
        successMessage,
        analyzeResume,
    } = useResumeAnalysis({
      file,
      setFile,
      company,
      role,
      requiredSkills,
      jobDescription,
      fileInputRef,
      fetchHistory,
      fetchDashboardStats,
      fetchNotifications,
      setIsHistoryPreview
  });

  useEffect(() => {
        if (location.state?.analysisResult) {
            setResult(location.state.analysisResult);
            setIsHistoryPreview(true);
        }
    }, [location.state, setResult]);

  useEffect(() => {

        const dashboard = location.state?.dashboardState;

        if (!dashboard) return;

        setResult(dashboard.result);

        setCompany(dashboard.company);

        setRole(dashboard.role);

        setRequiredSkills(dashboard.requiredSkills);

        if (dashboard.fileName) {

            setFile({
                name: dashboard.fileName,
            });

        }

    }, [location.state]);

    useEffect(() => {

        if (!result) {
            setShowResults(false);
            return;
        }

        const timer = setTimeout(() => {
            setShowResults(true);
        }, 80);

        return () => clearTimeout(timer);

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
const filteredHistory = history.filter((item) => {
  const query = search.trim().toLowerCase();

  return (
    item.company.toLowerCase().includes(query) ||
    item.role.toLowerCase().includes(query)
  );
});

const sortedHistory = [...filteredHistory].sort((a, b) => {

  if (sortBy === "highest") {
    return b.ats_score - a.ats_score;
  }

  if (sortBy === "lowest") {
    return a.ats_score - b.ats_score;
  }

  if (sortBy === "oldest") {
    return new Date(a.analyzed_at) - new Date(b.analyzed_at);
  }

  return new Date(b.analyzed_at) - new Date(a.analyzed_at);

});

const chartHistory = company
    ? history.filter(
          (item) =>
              item.company.toLowerCase() === company.toLowerCase()
      )
    : history;

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
                    theme={theme}
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

        history={history}
        search={search}
        setSearch={setSearch}
        sortBy={sortBy}
        setSortBy={setSortBy}
        sortedHistory={sortedHistory}

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