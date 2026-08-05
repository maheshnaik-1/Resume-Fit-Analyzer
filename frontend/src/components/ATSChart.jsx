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

function ATSChart({ history, theme }) {

  const colors = themeColors[theme];

  const chartData = [...history]
    .reverse()
    .map((item, index) => ({
      analysis: index + 1,
      score: item.ats_score,
    }));

  return (
    <div className="card analytics-card">

      <h2>📈 ATS Score Trend</h2>

      <div className="chart-scroll">

        <div
          style={{
            width:`${Math.max(chartData.length * 38,1100)}px`,
            height: "360px"
          }}
        >

          <ResponsiveContainer width="100%" height="100%">

            <LineChart
                data={chartData}
                margin={{
                    top:15,
                    right:20,
                    left:5,
                    bottom:20,
                }}
            >

              <CartesianGrid
                  stroke={colors.border}
                  strokeDasharray="3 3"
              />

              <XAxis
                  dataKey="analysis"
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
                  domain={[0,100]}
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
                      `${Number(value).toFixed(2)}%`,
                      "ATS Score",
                  ]}
                  labelFormatter={(label) => `Analysis #${label}`}
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

              <Line
                type="natural"
                dataKey="score"
                stroke={colors.primary}
                strokeWidth={3}
                dot={false}
                activeDot={{
                    r: 7,
                    fill: colors.primary,
                    stroke: colors.surface,
                    strokeWidth: 2,
                }}
                animationDuration={600}
                isAnimationActive={false}
                animationEasing="ease-in-out"
              />

            </LineChart>

          </ResponsiveContainer>

        </div>

      </div>

    </div>
  );
}

export default ATSChart;