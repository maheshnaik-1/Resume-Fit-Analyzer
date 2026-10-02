import { useNavigate } from "react-router-dom";
import "../styles/footer.css";

function Footer({
    result,
    company,
    role,
    requiredSkills,
    file,
}) {
    const navigate = useNavigate();

    const scrollToTop = () => {
        window.scrollTo({ top: 0, behavior: "smooth" });
    };

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
        <footer className="dashboard-footer">
            <div className="footer-main">
                {/* 1. Brand Area */}
                <div className="footer-brand">
                    <div className="footer-logo">
                        <span className="footer-icon" aria-hidden="true">🎯</span>
                        <span className="footer-title">Resume Fit Analyzer</span>
                    </div>
                    <p className="footer-tagline">
                        Intelligent resume benchmarking, ATS compatibility scoring, and skill gap analytics.
                    </p>
                </div>

                {/* 2. Navigation Area */}
                <div className="footer-nav">
                    <span className="footer-section-label">Navigation</span>
                    <nav className="footer-links" aria-label="Footer navigation">
                        <button
                            type="button"
                            className="footer-link-btn"
                            onClick={scrollToTop}
                        >
                            Dashboard
                        </button>
                        <button
                            type="button"
                            className="footer-link-btn"
                            onClick={handleHistoryNavigation}
                        >
                            Analysis History
                        </button>
                    </nav>
                </div>

                {/* 3. Tech Stack / System Info */}
                <div className="footer-tech">
                    <span className="footer-section-label">Architecture</span>
                    <p className="footer-tech-stack">
                        React • FastAPI • SQLite • Python
                    </p>
                </div>
            </div>

            {/* 4. Bottom Divider & Copyright */}
            <div className="footer-bottom">
                <p className="footer-copyright">
                    © {new Date().getFullYear()} Resume Fit Analyzer. All rights reserved.
                </p>
                <div className="footer-status-indicator">
                    <span className="status-dot" aria-hidden="true"></span>
                    <span className="status-text">Systems Operational</span>
                </div>
            </div>
        </footer>
    );
}

export default Footer;
