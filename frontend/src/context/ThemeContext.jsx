/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState, useEffect, useMemo } from "react";
import { getThemeColors } from "../utils/themeColors";

const ThemeContext = createContext(null);

const STORAGE_KEY = "rfa_theme";

export function ThemeProvider({ children }) {
    const [theme, setTheme] = useState(() => {
        try {
            const saved = localStorage.getItem(STORAGE_KEY);
            return saved === "dark" ? "dark" : "light";
        } catch {
            return "light";
        }
    });

    useEffect(() => {
        try {
            document.documentElement.setAttribute("data-theme", theme);
            localStorage.setItem(STORAGE_KEY, theme);
        } catch (e) {
            console.error("Failed to update theme in storage", e);
        }
    }, [theme]);

    const toggleTheme = () => {
        setTheme((prev) => (prev === "dark" ? "light" : "dark"));
    };

    const colors = useMemo(() => getThemeColors(theme), [theme]);

    const value = {
        theme,
        isDark: theme === "dark",
        toggleTheme,
        setTheme,
        colors,
    };

    return (
        <ThemeContext.Provider value={value}>
            {children}
        </ThemeContext.Provider>
    );
}

export function useTheme() {
    const context = useContext(ThemeContext);
    if (!context) {
        throw new Error("useTheme must be used within a ThemeProvider");
    }
    return context;
}

export default ThemeContext;
