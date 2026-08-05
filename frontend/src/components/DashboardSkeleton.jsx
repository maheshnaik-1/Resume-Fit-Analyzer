import "../styles/dashboardSkeleton.css";

function DashboardSkeleton() {
    return (

        <div className="dashboard-skeleton">

            <div className="skeleton-header"></div>

            <div className="skeleton-score-card"></div>

            <div className="skeleton-row">

                <div className="skeleton-half"></div>

                <div className="skeleton-half"></div>

            </div>

            <div className="skeleton-card"></div>

            <div className="skeleton-card"></div>

            <div className="skeleton-chart"></div>

        </div>

    );
}

export default DashboardSkeleton;