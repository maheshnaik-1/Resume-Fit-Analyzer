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

function CompanyChart({ history, theme }) {

    const colors = themeColors[theme];

    const companyCounts = {};

    history.forEach((item) => {

        const normalized = item.company.trim().toLowerCase();

        const company =
            normalized.charAt(0).toUpperCase() +
            normalized.slice(1);

        companyCounts[company] =
            (companyCounts[company] || 0) + 1;

    });

    const chartData = Object.entries(companyCounts)
        .map(([company, analyses]) => ({
            company,
            analyses,
        }))
        .sort((a, b) => b.analyses - a.analyses);

    return (

        <div className="card analytics-card">

            <h2>🏢 Company Analysis</h2>

            <div className="chart-scroll">

                <ResponsiveContainer
                    width={Math.max(chartData.length * 75, 700)}
                    height="100%"
                >

                    <BarChart
                        data={chartData}
                        margin={{
                            top: 20,
                            right: 20,
                            left: 10,
                            bottom: 25,
                        }}
                    >

                        <CartesianGrid
                            stroke={colors.border}
                            strokeDasharray="3 3"
                        />

                        <XAxis
                            dataKey="company"
                            angle={-20}
                            interval={0}
                            tick={{
                                fill: colors.textLight,
                                fontSize: 13,
                            }}
                            axisLine={{
                                stroke: colors.border,
                            }}
                            tickLine={{
                                stroke: colors.border,
                            }}
                        />

                        <YAxis
                            tick={{
                                fill: colors.textLight,
                                fontSize: 13,
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
                                value,
                                "Analyses",
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
                            radius={[6, 6, 0, 0]}
                            isAnimationActive={false}
                        />

                    </BarChart>

                </ResponsiveContainer>

            </div>

        </div>
    );

}

export default CompanyChart;