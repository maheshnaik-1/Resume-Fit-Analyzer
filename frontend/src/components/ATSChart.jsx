import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
} from "recharts";
import themeColors from "../utils/themeColors";

function ATSTooltip({ active, payload, label, colors }) {
    if (active && payload && payload.length && colors) {
        const dataPoint = payload[0].payload;
        return (
            <div
                style={{
                    background: colors.surface,
                    color: colors.text,
                    border: `1px solid ${colors.border}`,
                    borderRadius: "12px",
                    boxShadow: colors.shadow,
                    padding: "10px 14px",
                    fontSize: "13px",
                }}
            >
                <div style={{ fontWeight: 700, marginBottom: "4px" }}>
                    Analysis #{label}
                </div>
                <div style={{ color: colors.primary, fontWeight: 600, fontSize: "14px" }}>
                    {Number(dataPoint.score).toFixed(2)}% ATS Score
                </div>
                {(dataPoint.company || dataPoint.role) && (
                    <div style={{ color: colors.textLight, fontSize: "12px", marginTop: "4px" }}>
                        {[dataPoint.company, dataPoint.role].filter(Boolean).join(" • ")}
                    </div>
                )}
            </div>
        );
    }
    return null;
}

function ATSChart({ history = [], theme = "ocean" }) {
    const colors = themeColors[theme] || themeColors.ocean;

    const chartData = [...history]
        .reverse()
        .map((item, index) => ({
            analysis: index + 1,
            score: typeof item.ats_score === "number" ? item.ats_score : parseFloat(item.ats_score) || 0,
            company: item.company || "",
            role: item.role || "",
        }));

    const hasData = chartData.length > 0;

    // Calculate reasonable tick interval so labels never overlap
    const tickInterval = chartData.length > 12 ? Math.ceil(chartData.length / 10) : 0;

    return (
        <div className="card analytics-card ats-trend-card">
            <h2>📈 ATS Score Trend</h2>

            {!hasData ? (
                <div className="analytics-empty">
                    <p>No ATS score history available yet.</p>
                </div>
            ) : (
                <div className="chart-scroll">
                    <ResponsiveContainer width="100%" height="100%">
                        <LineChart
                            data={chartData}
                            margin={{
                                top: 15,
                                right: 25,
                                left: 5,
                                bottom: 15,
                            }}
                        >
                            <CartesianGrid
                                stroke={colors.border}
                                strokeDasharray="3 3"
                            />

                            <XAxis
                                dataKey="analysis"
                                interval={tickInterval}
                                tickFormatter={(val) => `#${val}`}
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
                                domain={[0, 100]}
                                ticks={[0, 25, 50, 75, 100]}
                                tickFormatter={(val) => `${val}%`}
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

                            <Tooltip content={<ATSTooltip colors={colors} />} />

                            <Line
                                type="monotone"
                                dataKey="score"
                                stroke={colors.primary}
                                strokeWidth={2.5}
                                dot={chartData.length <= 25 ? {
                                    r: 3.5,
                                    fill: colors.primary,
                                    stroke: colors.surface,
                                    strokeWidth: 1.5,
                                } : false}
                                activeDot={{
                                    r: 6,
                                    fill: colors.primary,
                                    stroke: colors.surface,
                                    strokeWidth: 2,
                                }}
                                isAnimationActive={false}
                            />
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            )}
        </div>
    );
}

export default ATSChart;