import AnimatedNumber from "./AnimatedNumber";

function StatsCards({ stats }) {

    return (

        <div className="stats-grid">

            <div className="stat-card total-card">
                <h3>📄 Total Analyses</h3>

                <h2>
                    <AnimatedNumber
                        value={stats.total_analyses}
                        fromZero={true}
                    />
                </h2>
            </div>

            <div className="stat-card highest-card">
                <h3>🏆 Highest Score</h3>

                <h2>
                    <AnimatedNumber
                        value={Number(stats.highest_score).toFixed(2)}
                        fromZero={true}
                    />
                </h2>
            </div>

            <div className="stat-card average-card">
                <h3>📊 Average ATS</h3>

                <h2>
                    <AnimatedNumber
                        value={Number(stats.average_score).toFixed(2)}
                        fromZero={true}
                    />
                </h2>
            </div>

            <div className="stat-card company-card">
                <h3>🏢 Companies Tried</h3>

                <h2>
                    <AnimatedNumber
                        value={stats.companies}
                        fromZero={true}
                    />
                </h2>
            </div>

        </div>

    );

}

export default StatsCards;