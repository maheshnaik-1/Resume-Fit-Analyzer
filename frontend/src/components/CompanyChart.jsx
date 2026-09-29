import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    Tooltip,
    CartesianGrid,
    ResponsiveContainer,
} from "recharts";
import themeColors from "../utils/themeColors";

function CompanyChart({ history = [], theme = "ocean" }) {
    const colors = themeColors[theme] || themeColors.ocean;

    const companyMap = {};

    history.forEach((item) => {
        if (!item.company) return;
        const cleaned = item.company.trim().replace(/\s+/g, " ");
        if (!cleaned) return;

        const key = cleaned.toLowerCase();

        if (!companyMap[key]) {
            companyMap[key] = {
                company: cleaned,
                analyses: 0,
            };
        }
        companyMap[key].analyses += 1;
    });

    const chartData = Object.values(companyMap)
        .sort((a, b) => b.analyses - a.analyses)
        .slice(0, 8);

    const hasData = chartData.length > 0;

    return (
        <div className="card analytics-card">
            <h2>🏢 Company Analysis</h2>

            {!hasData ? (
                <div className="analytics-empty">
                    <p>No company analysis history available yet.</p>
                </div>
            ) : (
                <div className="role-scroll">
                    <ResponsiveContainer
                        width="100%"
                        height={Math.max(chartData.length * 44 + 40, 240)}
                    >
                        <BarChart
                            data={chartData}
                            layout="vertical"
                            barSize={18}
                            margin={{
                                top: 10,
                                right: 30,
                                left: 5,
                                bottom: 10,
                            }}
                        >
                            <CartesianGrid
                                stroke={colors.border}
                                strokeDasharray="3 3"
                                horizontal={false}
                            />

                            <XAxis
                                type="number"
                                allowDecimals={false}
                                tick={{
                                    fill: colors.textLight,
                                    fontSize: 12,
                                }}
                                axisLine={{
                                    stroke: colors.border,
                                }}
                                tickLine={{
                                    stroke: colors.border,
                                }}
                            />

                            <YAxis
                                dataKey="company"
                                type="category"
                                width={115}
                                tick={{
                                    fill: colors.textLight,
                                    fontSize: 12,
                                }}
                                axisLine={{
                                    stroke: colors.border,
                                }}
                                tickLine={{
                                    stroke: colors.border,
                                }}
                            />

                            <Tooltip
                                formatter={(value) => [
                                    `${value} ${value === 1 ? "analysis" : "analyses"}`,
                                    "Total",
                                ]}
                                labelFormatter={(label) => `Company: ${label}`}
                                contentStyle={{
                                    background: colors.surface,
                                    color: colors.text,
                                    border: `1px solid ${colors.border}`,
                                    borderRadius: "12px",
                                    boxShadow: colors.shadow,
                                }}
                                labelStyle={{
                                    color: colors.text,
                                    fontWeight: 600,
                                }}
                                itemStyle={{
                                    color: colors.text,
                                }}
                                cursor={{
                                    stroke: colors.primary,
                                    strokeDasharray: "4 4",
                                }}
                            />

                            <Bar
                                dataKey="analyses"
                                fill={colors.primary}
                                radius={[0, 6, 6, 0]}
                                isAnimationActive={false}
                            />
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            )}
        </div>
    );
}

export default CompanyChart;