import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

function History(){

    const [history, setHistory] = useState([]);
    const [search, setSearch] = useState("");
    const [sortBy, setSortBy] = useState("newest");
    const [currentPage, setCurrentPage] = useState(1);
    const rowsPerPage = 10;
    const [theme] = useState(
        localStorage.getItem("theme") || "ocean"
    );
    const navigate = useNavigate();
    const location = useLocation();

    const fetchHistory = async () => {

        const email = localStorage.getItem("userEmail");

        if (!email) return;

        try {

            const response = await fetch(
                `http://127.0.0.1:8000/history/${email}`
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

        fetchHistory();

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
                `http://127.0.0.1:8000/history/result/${historyId}`
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
            `http://127.0.0.1:8000/history/${historyId}`,
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

        <div className={`history-container theme-${theme}`}>

        <div className="history-topbar">

            <button
                className="history-back-btn"
                onClick={() =>
                    navigate("/dashboard", {
                        state: location.state,
                    })
                }
            >
                ← Back to Results
            </button>

            <div className="history-page-header">

                <h2>📜 Analysis History</h2>

                <p>
                    View, search and manage all your previous resume analyses.
                </p>

            </div>

        </div>

        <div className="history-summary">

            <div className="summary-card">
                <h3>📄 Analyses</h3>
                <p>{totalAnalyses}</p>
            </div>

            <div className="summary-card">
                <h3>🏆 Highest ATS</h3>
                <p>{highestATS}%</p>
            </div>

            <div className="summary-card">
                <h3>🏢 Companies</h3>
                <p>{totalCompanies}</p>
            </div>

            <div className="summary-card">
                <h3>💼 Roles</h3>
                <p>{totalRoles}</p>
            </div>

        </div>

        <div className="history-toolbar">

            <div className="history-search-box">

                <span className="search-icon">🔎</span>

                <input
                    type="text"
                    placeholder="Search by company or role..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    className="history-search"
                />

            </div>

            <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
                className="history-sort"
            >
                <option value="newest">🕒 Newest First</option>
                <option value="oldest">📅 Oldest First</option>
                <option value="highest">🏆 Highest ATS</option>
                <option value="lowest">📉 Lowest ATS</option>
            </select>

            <button
                className="export-btn btn btn-secondary"
                onClick={exportCSV}
            >
                📥 CSV
            </button>

            <button
                className="export-btn btn btn-primary"
                onClick={exportPDF}
            >
                📄 PDF
            </button>

        </div>

        {history.length > 0 && (

            <div className="result-card history-card">

            <div className="history-table-wrapper">

                <table className="history-table">

                <thead>

                    <tr>
                    <th>Company</th>
                    <th>Role</th>
                    <th>ATS Score</th>
                    <th>Date</th>
                    <th>Action</th>
                    </tr>

                </thead>

                <tbody>

                    {sortedHistory.length === 0 ? (

                    <tr>

                        <td
                        colSpan="5"
                        style={{
                            textAlign: "center",
                            padding: "30px",
                            color: "#64748b",
                            fontWeight: "600",
                        }}
                        >
                        🔍 No matching history found
                        </td>

                    </tr>

                    ) : (

                    currentRows.map((item, index) => (

                        <tr
                            key={index}
                            onClick={() => openHistoryResult(item.id)}
                            style={{ cursor: "pointer" }}
                        >

                        <td>{item.company}</td>

                        <td>{item.role}</td>

                        <td>

                            <span
                            style={{
                                background:
                                item.ats_score >= 80
                                    ? "#22c55e"
                                    : item.ats_score >= 60
                                    ? "#f59e0b"
                                    : "#ef4444",

                                color: "white",
                                padding: "8px 16px",
                                borderRadius: "20px",
                                fontWeight: "bold",
                            }}
                            >
                            {Number(item.ats_score).toFixed(2)}%
                            </span>

                        </td>

                        <td>
                            {new Date(item.analyzed_at).toLocaleString()}
                        </td>

                        <td>

                            <button
                                className="btn btn-danger delete-history-btn"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    deleteHistory(item.id);
                                }}
                            >
                                🗑 Delete
                            </button>

                        </td>

                        </tr>

                    ))

                    )}

                </tbody>

                </table>

            </div>

            <div className="pagination">

                <button
                    disabled={currentPage === 1}
                    onClick={() => setCurrentPage(currentPage - 1)}
                >
                    ← Previous
                </button>

                <span>
                    Page {currentPage} of {totalPages}
                </span>

                <button
                    disabled={currentPage === totalPages}
                    onClick={() => setCurrentPage(currentPage + 1)}
                >
                    Next →
                </button>

            </div>

            </div>

        )}

        </div>

    );

}

export default History;