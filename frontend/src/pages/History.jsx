import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";
import { API_BASE_URL } from "../utils/api";

function History(){

    const [history, setHistory] = useState([]);
    const [search, setSearch] = useState("");
    const [sortBy, setSortBy] = useState("newest");
    const [currentPage, setCurrentPage] = useState(1);
    const rowsPerPage = 10;
    const navigate = useNavigate();
    const location = useLocation();

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

            }

        } catch (error) {

            console.log(error);

        }

    };

    useEffect(() => {

        let isMounted = true;

        async function loadHistory() {

            const email = localStorage.getItem("userEmail");

            if (!email) return;

            try {

                const response = await fetch(
                    `${API_BASE_URL}/history/${email}`
                );

                const data = await response.json();

                if (isMounted && Array.isArray(data)) {

                    setHistory(data);

                }

            } catch (error) {

                console.log(error);

            }

        }

        loadHistory();

        return () => {

            isMounted = false;

        };

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

    const totalPages = Math.ceil(sortedHistory.length / rowsPerPage);

    const startIndex = (currentPage - 1) * rowsPerPage;

    const currentRows = sortedHistory.slice(
        startIndex,
        startIndex + rowsPerPage
    );

    const totalAnalyses = history.length;

    const highestATS =
        history.length > 0
            ? Math.max(...history.map(item => item.ats_score)).toFixed(2)
            : 0;

    const totalCompanies =
        new Set(history.map(item => item.company)).size;

    const totalRoles =
        new Set(history.map(item => item.role)).size;

    const exportCSV = () => {

        if (history.length === 0) {
            alert("No history available.");
            return;
        }

        const headers = [
            "Company",
            "Role",
            "ATS Score",
            "Date"
        ];

        const rows = history.map(item => [

            item.company,

            item.role,

            item.ats_score,

            item.analyzed_at

        ]);

        const csvContent = [

            headers.join(","),

            ...rows.map(row => row.join(","))

        ].join("\n");

        const blob = new Blob(

            [csvContent],

            { type: "text/csv;charset=utf-8;" }

        );

        const url = URL.createObjectURL(blob);

        const link = document.createElement("a");

        link.href = url;

        const today = new Date().toISOString().split("T")[0];

        link.download = `Resume_History_${today}.csv`;

        link.click();

        URL.revokeObjectURL(url);

    };

    const exportPDF = () => {

        if (history.length === 0) {

            alert("No history available.");

            return;

        }

        const doc = new jsPDF();

        doc.setFontSize(20);

        doc.text("Resume Fit Analyzer", 14, 18);

        doc.setFontSize(14);

        doc.text("Resume Analysis History", 14, 30);

        doc.setFontSize(11);

        doc.text(
            `User: ${localStorage.getItem("userEmail")}`,
            14,
            40
        );

        doc.text(
            `Export Date: ${new Date().toLocaleDateString()}`,
            14,
            48
        );

        autoTable(doc, {

            startY: 58,

            head: [[
                "Company",
                "Role",
                "ATS Score",
                "Date"
            ]],

            body: history.map(item => [

                item.company,

                item.role,

                `${item.ats_score}%`,

                new Date(item.analyzed_at).toLocaleString()

            ])

        });

        const today = new Date().toISOString().split("T")[0];

        doc.save(`Resume_History_${today}.pdf`);

    };

    const openHistoryResult = async (historyId) => {
        try {

            const response = await fetch(
                `${API_BASE_URL}/history/result/${historyId}`
            );

            const data = await response.json();

            if (!response.ok) {
                alert(data.detail || "Unable to open saved analysis.");
                return;
            }

            navigate("/dashboard", {
                state: {
                    analysisResult: data
                }
            });

        } catch (error) {
            console.log(error);
            alert("Unable to open saved analysis.");
        }
    };

    const deleteHistory = async (historyId) => {

    const confirmDelete = window.confirm(
        "Are you sure you want to delete this analysis?"
    );

    if (!confirmDelete) return;

    try {

        await fetch(
            `${API_BASE_URL}/history/${historyId}`,
            {
                method: "DELETE",
            }
        );

        fetchHistory();

    } catch (error) {

        console.log(error);

    }

};

    return (

        <div className="history-container">

        <div className="history-header-block">

            <button
                type="button"
                className="history-back-btn"
                onClick={() =>
                    navigate("/dashboard", {
                        state: location.state,
                    })
                }
                aria-label="Back to Results"
            >
                <svg
                    className="history-back-icon"
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    aria-hidden="true"
                >
                    <line x1="19" y1="12" x2="5" y2="12"></line>
                    <polyline points="12 19 5 12 12 5"></polyline>
                </svg>
                <span>Back to Results</span>
            </button>

            <div className="history-title-group">
                <h1 className="history-title">Analysis History</h1>
                <p className="history-subtitle">
                    View, search and manage all your previous resume analyses.
                </p>
            </div>

        </div>

        <div className="history-summary-ribbon">

            <div className="history-stat-card">
                <div className="history-stat-icon">
                    <svg
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
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                        <polyline points="14 2 14 8 20 8" />
                        <line x1="16" y1="13" x2="8" y2="13" />
                        <line x1="16" y1="17" x2="8" y2="17" />
                        <polyline points="10 9 9 9 8 9" />
                    </svg>
                </div>
                <div className="history-stat-info">
                    <span className="history-stat-label">Total Analyses</span>
                    <span className="history-stat-value">{totalAnalyses}</span>
                </div>
            </div>

            <div className="history-stat-card">
                <div className="history-stat-icon">
                    <svg
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
                        <circle cx="12" cy="8" r="7" />
                        <polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88" />
                    </svg>
                </div>
                <div className="history-stat-info">
                    <span className="history-stat-label">Highest ATS Score</span>
                    <span className="history-stat-value">{highestATS}%</span>
                </div>
            </div>

            <div className="history-stat-card">
                <div className="history-stat-icon">
                    <svg
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
                        <rect x="4" y="2" width="16" height="20" rx="2" ry="2" />
                        <line x1="9" y1="22" x2="9" y2="22.01" />
                        <line x1="15" y1="22" x2="15" y2="22.01" />
                        <line x1="8" y1="6" x2="16" y2="6" />
                        <line x1="8" y1="10" x2="16" y2="10" />
                        <line x1="8" y1="14" x2="16" y2="14" />
                        <line x1="8" y1="18" x2="16" y2="18" />
                    </svg>
                </div>
                <div className="history-stat-info">
                    <span className="history-stat-label">Target Companies</span>
                    <span className="history-stat-value">{totalCompanies}</span>
                </div>
            </div>

            <div className="history-stat-card">
                <div className="history-stat-icon">
                    <svg
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
                        <rect x="2" y="7" width="20" height="14" rx="2" ry="2" />
                        <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16" />
                    </svg>
                </div>
                <div className="history-stat-info">
                    <span className="history-stat-label">Target Roles</span>
                    <span className="history-stat-value">{totalRoles}</span>
                </div>
            </div>

        </div>

        <div className="history-toolbar-card">

            <div className="history-search-wrapper">
                <svg
                    className="history-search-icon"
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    aria-hidden="true"
                >
                    <circle cx="11" cy="11" r="8" />
                    <line x1="21" y1="21" x2="16.65" y2="16.65" />
                </svg>
                <input
                    type="text"
                    placeholder="Search by company or role..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    className="history-search-input"
                    aria-label="Search by company or role"
                />
                {search && (
                    <button
                        type="button"
                        className="history-search-clear"
                        onClick={() => setSearch("")}
                        title="Clear search"
                        aria-label="Clear search"
                    >
                        <svg
                            width="14"
                            height="14"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            aria-hidden="true"
                        >
                            <line x1="18" y1="6" x2="6" y2="18" />
                            <line x1="6" y1="6" x2="18" y2="18" />
                        </svg>
                    </button>
                )}
            </div>

            <div className="history-sort-wrapper">
                <select
                    value={sortBy}
                    onChange={(e) => setSortBy(e.target.value)}
                    className="history-sort-select"
                    aria-label="Sort history by"
                >
                    <option value="newest">Newest First</option>
                    <option value="oldest">Oldest First</option>
                    <option value="highest">Highest ATS</option>
                    <option value="lowest">Lowest ATS</option>
                </select>
            </div>

            <div className="history-export-group">
                <button
                    type="button"
                    className="btn btn-secondary history-export-btn"
                    onClick={exportCSV}
                    title="Export history as CSV"
                >
                    <svg
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
                        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                        <polyline points="7 10 12 15 17 10" />
                        <line x1="12" y1="15" x2="12" y2="3" />
                    </svg>
                    <span>CSV</span>
                </button>

                <button
                    type="button"
                    className="btn btn-primary history-export-btn"
                    onClick={exportPDF}
                    title="Export history as PDF"
                >
                    <svg
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
                        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                        <polyline points="7 10 12 15 17 10" />
                        <line x1="12" y1="15" x2="12" y2="3" />
                    </svg>
                    <span>PDF</span>
                </button>
            </div>

        </div>

        {history.length === 0 ? (

            <div className="result-card history-card history-empty-card">

                <div className="history-empty-container">

                    <div className="history-empty-icon-wrap">
                        <svg
                            className="history-empty-icon"
                            width="36"
                            height="36"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="1.8"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            aria-hidden="true"
                        >
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                            <polyline points="14 2 14 8 20 8" />
                            <line x1="12" y1="18" x2="12" y2="12" />
                            <line x1="9" y1="15" x2="15" y2="15" />
                        </svg>
                    </div>

                    <h3 className="history-empty-title">No analyses yet</h3>

                    <p className="history-empty-desc">
                        Your analyzed resumes will appear here once you complete your first analysis.
                    </p>

                    <button
                        type="button"
                        className="btn btn-primary history-empty-action-btn"
                        onClick={() =>
                            navigate("/dashboard", {
                                state: location.state,
                            })
                        }
                    >
                        <svg
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
                            <line x1="12" y1="5" x2="12" y2="19" />
                            <line x1="5" y1="12" x2="19" y2="12" />
                        </svg>
                        <span>Start New Analysis</span>
                    </button>

                </div>

            </div>

        ) : (

            <div className="result-card history-card">

            <div className="history-table-wrapper">

                <table className="history-table">

                <thead>

                    <tr>
                    <th className="history-th-company">Company</th>
                    <th className="history-th-role">Role</th>
                    <th className="history-th-score">ATS Score</th>
                    <th className="history-th-date">Date</th>
                    <th className="history-th-action">Action</th>
                    </tr>

                </thead>

                <tbody>

                    {sortedHistory.length === 0 ? (

                    <tr className="history-empty-row">

                        <td colSpan="5">
                            <div className="history-search-zero-state">
                                <div className="history-search-zero-icon-wrap">
                                    <svg
                                        className="history-search-zero-icon"
                                        width="28"
                                        height="28"
                                        viewBox="0 0 24 24"
                                        fill="none"
                                        stroke="currentColor"
                                        strokeWidth="2"
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        aria-hidden="true"
                                    >
                                        <circle cx="11" cy="11" r="8" />
                                        <line x1="21" y1="21" x2="16.65" y2="16.65" />
                                        <line x1="8" y1="11" x2="14" y2="11" />
                                    </svg>
                                </div>
                                <h4 className="history-search-zero-title">No matching analyses</h4>
                                <p className="history-search-zero-desc">
                                    No results found{search ? ` for "${search}"` : ""}. Try a different company or role.
                                </p>
                                {search && (
                                    <button
                                        type="button"
                                        className="btn btn-secondary history-reset-search-btn"
                                        onClick={() => {
                                            setSearch("");
                                            setCurrentPage(1);
                                        }}
                                    >
                                        <svg
                                            width="14"
                                            height="14"
                                            viewBox="0 0 24 24"
                                            fill="none"
                                            stroke="currentColor"
                                            strokeWidth="2"
                                            strokeLinecap="round"
                                            strokeLinejoin="round"
                                            aria-hidden="true"
                                        >
                                            <line x1="18" y1="6" x2="6" y2="18" />
                                            <line x1="6" y1="6" x2="18" y2="18" />
                                        </svg>
                                        <span>Clear Search</span>
                                    </button>
                                )}
                            </div>
                        </td>

                    </tr>

                    ) : (

                    currentRows.map((item, index) => (

                        <tr
                            key={item.id || index}
                            className="history-row"
                            onClick={() => openHistoryResult(item.id)}
                            title="Click to view detailed analysis"
                        >

                        <td className="history-col-company">
                            <span className="history-company-name">{item.company}</span>
                        </td>

                        <td className="history-col-role">
                            <span className="history-role-name">{item.role}</span>
                        </td>

                        <td className="history-col-score">

                            <span
                                className={`history-score-badge ${
                                    item.ats_score >= 80
                                        ? "history-score-high"
                                        : item.ats_score >= 60
                                        ? "history-score-mid"
                                        : "history-score-low"
                                }`}
                            >
                                {Number(item.ats_score).toFixed(2)}%
                            </span>

                        </td>

                        <td className="history-col-date">
                            <div className="history-date-wrapper">
                                <svg
                                    className="history-date-icon"
                                    width="13"
                                    height="13"
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    stroke="currentColor"
                                    strokeWidth="2"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    aria-hidden="true"
                                >
                                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                                    <line x1="16" y1="2" x2="16" y2="6" />
                                    <line x1="8" y1="2" x2="8" y2="6" />
                                    <line x1="3" y1="10" x2="21" y2="10" />
                                </svg>
                                <span>{new Date(item.analyzed_at).toLocaleString()}</span>
                            </div>
                        </td>

                        <td className="history-col-action">

                            <button
                                type="button"
                                className="history-delete-btn"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    deleteHistory(item.id);
                                }}
                                title="Delete analysis record"
                                aria-label={`Delete analysis for ${item.company} ${item.role}`}
                            >
                                <svg
                                    className="history-delete-icon"
                                    width="13"
                                    height="13"
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    stroke="currentColor"
                                    strokeWidth="2"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    aria-hidden="true"
                                >
                                    <polyline points="3 6 5 6 21 6" />
                                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                                    <line x1="10" y1="11" x2="10" y2="17" />
                                    <line x1="14" y1="11" x2="14" y2="17" />
                                </svg>
                                <span>Delete</span>
                            </button>

                        </td>

                        </tr>

                    ))

                    )}

                </tbody>

                </table>

            </div>

            {totalPages > 0 && sortedHistory.length > 0 && (

                <div className="history-pagination-wrapper">

                    <div className="pagination">

                        <button
                            type="button"
                            className="pagination-btn pagination-prev-btn"
                            disabled={currentPage === 1}
                            onClick={() => setCurrentPage(currentPage - 1)}
                            aria-label="Previous Page"
                        >
                            <svg
                                width="14"
                                height="14"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="2"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                aria-hidden="true"
                            >
                                <line x1="19" y1="12" x2="5" y2="12" />
                                <polyline points="12 19 5 12 12 5" />
                            </svg>
                            <span className="pagination-btn-text">Previous</span>
                        </button>

                        <div className="pagination-info">
                            <span>Page</span>
                            <span className="pagination-current-page">{currentPage}</span>
                            <span>of</span>
                            <span className="pagination-total-pages">{totalPages || 1}</span>
                        </div>

                        <button
                            type="button"
                            className="pagination-btn pagination-next-btn"
                            disabled={currentPage === totalPages || totalPages === 0}
                            onClick={() => setCurrentPage(currentPage + 1)}
                            aria-label="Next Page"
                        >
                            <span className="pagination-btn-text">Next</span>
                            <svg
                                width="14"
                                height="14"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="2"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                aria-hidden="true"
                            >
                                <line x1="5" y1="12" x2="19" y2="12" />
                                <polyline points="12 5 19 12 12 19" />
                            </svg>
                        </button>

                    </div>

                </div>

            )}

            </div>

        )}

        </div>

    );

}

export default History;