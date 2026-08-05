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

function RoleChart({ history, theme }) {

    const colors = themeColors[theme];

  const roleCounts = {};

  history.forEach((item) => {
    const role = item.role.trim();

    roleCounts[role] = (roleCounts[role] || 0) + 1;
  });

  const chartData = Object.entries(roleCounts)
    .map(([role, analyses]) => ({
        role,
        analyses,
    }))
    .sort((a, b) => b.analyses - a.analyses)
    .slice(0, 8);

  return (
    <div className="card analytics-card role-card">
      <h2>👨‍💻 Role Analysis</h2>

      <div className="role-scroll">
            <ResponsiveContainer
                width="100%"
                height={Math.max(chartData.length * 55, 355)}
            >
                <BarChart
                    data={chartData}
                    layout="vertical"
                    barSize={22}
                    margin={{
                        top:15,
                        right:45,
                        left:0,
                        bottom:10,
                    }}
                >
                    <CartesianGrid
                        stroke={colors.border}
                        strokeDasharray="3 3"
                    />

                    <XAxis
                        type="number"
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
                        dataKey="role"
                        type="category"
                        width={150}
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
                        radius={[0, 8, 8, 0]}
                        animationDuration={600}
                        isAnimationActive={false}
                        animationEasing="ease-in-out"
                    />
                </BarChart>
            </ResponsiveContainer>
        </div>
    </div>
);
}

export default RoleChart;