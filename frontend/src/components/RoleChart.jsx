import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    Tooltip,
    CartesianGrid,
    ResponsiveContainer,
} from "recharts";
import { useTheme } from "../context/ThemeContext";

function RoleChart({ history = [] }) {
    const { colors } = useTheme();

    const roleMap = {};

    history.forEach((item) => {
        if (!item.role) return;
        const cleaned = item.role.trim().replace(/\s+/g, " ");
        if (!cleaned) return;

        const key = cleaned.toLowerCase();

        if (!roleMap[key]) {
            roleMap[key] = {
                role: cleaned,
                analyses: 0,
            };
        }
        roleMap[key].analyses += 1;
    });

    const chartData = Object.values(roleMap)
        .sort((a, b) => b.analyses - a.analyses)
        .slice(0, 8);

    const hasData = chartData.length > 0;

    return (
        <div className="card analytics-card role-card">
            <h2>👨‍💻 Role Analysis</h2>

            {!hasData ? (
                <div className="analytics-empty">
                    <p>No role analysis history available yet.</p>
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
                                    fill: colors.textSecondary,
                                    fontSize: 12,
                                    fontWeight: 500,
                                }}
                                axisLine={{
                                    stroke: colors.border,
                                }}
                                tickLine={{
                                    stroke: colors.border,
                                }}
                            />

                            <YAxis
                                dataKey="role"
                                type="category"
                                width={125}
                                tick={{
                                    fill: colors.textSecondary,
                                    fontSize: 12,
                                    fontWeight: 600,
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
                                labelFormatter={(label) => `Role: ${label}`}
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

export default RoleChart;