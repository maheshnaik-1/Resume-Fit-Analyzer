import AnimatedNumber from "./AnimatedNumber";

function StatsCards({ stats }) {
    return (
        <div className="stats-ribbon">
            <div className="stat-item">
                <span className="stat-icon">📄</span>
                <div className="stat-info">
                    <span className="stat-label">Total Analyses</span>
                    <strong className="stat-value">
                        <AnimatedNumber
                            value={stats?.total_analyses ?? 0}
                            fromZero={true}
                        />
                    </strong>
                </div>
            </div>

            <div className="stat-divider"></div>

            <div className="stat-item">
                <span className="stat-icon">🏆</span>
                <div className="stat-info">
                    <span className="stat-label">Highest Score</span>
                    <strong className="stat-value">
                        <AnimatedNumber
                            value={Number(stats?.highest_score ?? 0)}
                            decimals={2}
                            suffix="%"
                            fromZero={true}
                        />
                    </strong>
                </div>
            </div>

            <div className="stat-divider"></div>

            <div className="stat-item">
                <span className="stat-icon">📊</span>
                <div className="stat-info">
                    <span className="stat-label">Average ATS</span>
                    <strong className="stat-value">
                        <AnimatedNumber
                            value={Number(stats?.average_score ?? 0)}
                            decimals={2}
                            suffix="%"
                            fromZero={true}
                        />
                    </strong>
                </div>
            </div>

            <div className="stat-divider"></div>

            <div className="stat-item">
                <span className="stat-icon">🏢</span>
                <div className="stat-info">
                    <span className="stat-label">Companies Tried</span>
                    <strong className="stat-value">
                        <AnimatedNumber
                            value={stats?.companies ?? 0}
                            fromZero={true}
                        />
                    </strong>
                </div>
            </div>
        </div>
    );
}

export default StatsCards;