/* eslint-disable react-refresh/only-export-components */
import {
    createContext,
    useContext,
    useState,
    useEffect,
    useCallback,
    useMemo,
} from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(() => {
        try {
            const loggedIn = localStorage.getItem("loggedIn");
            if (loggedIn === "true") {
                const name = localStorage.getItem("name") || "";
                const email = localStorage.getItem("userEmail") || "";
                return { name, email };
            }
        } catch (err) {
            console.error("Failed to read auth state from storage", err);
        }
        return null;
    });

    // Derive authentication purely from React state (not direct storage reads)
    const isAuthenticated = user !== null;

    const login = useCallback((userData, maybeEmail) => {
        let name = "";
        let email = "";

        if (typeof userData === "object" && userData !== null) {
            name = userData.name || "";
            email = userData.email || "";
        } else if (typeof userData === "string") {
            name = userData;
            email = maybeEmail || "";
        }

        try {
            localStorage.setItem("loggedIn", "true");
            localStorage.setItem("name", name);
            localStorage.setItem("userEmail", email);
            localStorage.removeItem("email");
        } catch (err) {
            console.error("Failed to write auth state to storage", err);
        }

        setUser({ name, email });
    }, []);

    const logout = useCallback(() => {
        try {
            localStorage.removeItem("loggedIn");
            localStorage.removeItem("name");
            localStorage.removeItem("userEmail");
        } catch (err) {
            console.error("Failed to clear auth state from storage", err);
        }

        setUser(null);
    }, []);

    // Sync auth state if changed from another tab or window
    useEffect(() => {
        const handleStorageChange = (e) => {
            if (e.key === "loggedIn" || e.key === "userEmail" || e.key === "name") {
                try {
                    const loggedIn = localStorage.getItem("loggedIn");
                    if (loggedIn === "true") {
                        setUser({
                            name: localStorage.getItem("name") || "",
                            email: localStorage.getItem("userEmail") || "",
                        });
                    } else {
                        setUser(null);
                    }
                } catch (err) {
                    console.error("Failed to sync storage auth state", err);
                }
            }
        };

        window.addEventListener("storage", handleStorageChange);
        return () => window.removeEventListener("storage", handleStorageChange);
    }, []);

    const value = useMemo(
        () => ({
            user,
            isAuthenticated,
            isLoggedIn: isAuthenticated,
            login,
            logout,
        }),
        [user, isAuthenticated, login, logout]
    );

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
}

export default AuthContext;
