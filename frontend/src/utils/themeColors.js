export const lightThemeColors = {
    primary: "#6D4AFF",
    primaryDark: "#5635D6",
    surface: "#FFFFFF",
    surfaceElevated: "#FBF9F5",
    background: "#F7F5F0",
    text: "#171717",
    textSecondary: "#374151",
    textLight: "#374151",
    textMuted: "#6B7280",
    textSubdued: "#9CA3AF",
    border: "#E0DDD5",
    shadow: "0 4px 16px rgba(23, 23, 23, 0.05)",
    cyan: "#18A6C8",
    success: "#15803D",
    warning: "#B45309",
    danger: "#B91C1C",
};

export const darkThemeColors = {
    primary: "#3B82F6",
    primaryDark: "#2563EB",
    surface: "#1A1D21",
    surfaceElevated: "#22262C",
    background: "#121417",
    text: "#F3F4F6",
    textSecondary: "#D1D5DB",
    textLight: "#D1D5DB",
    textMuted: "#9CA3AF",
    textSubdued: "#6B7280",
    border: "#2E333D",
    shadow: "0 4px 16px rgba(0, 0, 0, 0.35)",
    cyan: "#06B6D4",
    success: "#22C55E",
    warning: "#F59E0B",
    danger: "#EF4444",
};

export function getThemeColors(theme) {
    return theme === "dark" ? darkThemeColors : lightThemeColors;
}

const themeColors = {
    ...lightThemeColors,
    canonical: lightThemeColors,
    light: lightThemeColors,
    dark: darkThemeColors,
    getThemeColors,

    // Backward-compatibility aliases
    ocean: lightThemeColors,
    emerald: lightThemeColors,
    purple: lightThemeColors,
    sunset: lightThemeColors,
    rose: lightThemeColors,
};

export default themeColors;