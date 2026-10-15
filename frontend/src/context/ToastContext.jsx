/* eslint-disable react-refresh/only-export-components */
import {
    createContext,
    useContext,
    useState,
    useRef,
    useEffect,
    useCallback,
    useMemo,
} from "react";
import ToastContainer from "../components/Toast";

const ToastContext = createContext(null);

const DEFAULT_DURATION = 4000;

export function ToastProvider({ children }) {
    const [toasts, setToasts] = useState([]);
    const timersRef = useRef(new Map());
    const idCounterRef = useRef(0);

    const removeToast = useCallback((id) => {
        if (timersRef.current.has(id)) {
            clearTimeout(timersRef.current.get(id));
            timersRef.current.delete(id);
        }
        setToasts((prev) => prev.filter((t) => t.id !== id));
    }, []);

    const addToast = useCallback(
        (message, type = "info", duration = DEFAULT_DURATION) => {
            idCounterRef.current += 1;
            const id = `toast-${Date.now()}-${idCounterRef.current}`;

            setToasts((prev) => [...prev, { id, message, type }]);

            if (duration && duration > 0) {
                const timerId = setTimeout(() => {
                    removeToast(id);
                }, duration);
                timersRef.current.set(id, timerId);
            }

            return id;
        },
        [removeToast]
    );

    // Clean up all pending timers when provider unmounts
    useEffect(() => {
        const activeTimers = timersRef.current;
        return () => {
            activeTimers.forEach((timerId) => clearTimeout(timerId));
            activeTimers.clear();
        };
    }, []);

    const toast = useMemo(
        () => ({
            success: (msg, duration) => addToast(msg, "success", duration),
            error: (msg, duration) => addToast(msg, "error", duration),
            warning: (msg, duration) => addToast(msg, "warning", duration),
            info: (msg, duration) => addToast(msg, "info", duration),
            dismiss: (id) => removeToast(id),
        }),
        [addToast, removeToast]
    );

    const value = useMemo(
        () => ({
            toast,
            addToast,
            removeToast,
            success: toast.success,
            error: toast.error,
            warning: toast.warning,
            info: toast.info,
            dismiss: toast.dismiss,
        }),
        [toast, addToast, removeToast]
    );

    return (
        <ToastContext.Provider value={value}>
            {children}
            <ToastContainer toasts={toasts} onDismiss={removeToast} />
        </ToastContext.Provider>
    );
}

export function useToast() {
    const context = useContext(ToastContext);
    if (!context) {
        throw new Error("useToast must be used within a ToastProvider");
    }
    return context;
}

export default ToastContext;
