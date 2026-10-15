import "../styles/toast.css";

function ToastIcon({ type }) {
    switch (type) {
        case "success":
            return (
                <svg width="18" height="18" viewBox="0 0 20 20" fill="none" aria-hidden="true">
                    <path
                        d="M10 18a8 8 0 100-16 8 8 0 000 16zm-2.5-8l2 2 4.5-4.5"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                    />
                </svg>
            );
        case "error":
            return (
                <svg width="18" height="18" viewBox="0 0 20 20" fill="none" aria-hidden="true">
                    <path
                        d="M10 18a8 8 0 100-16 8 8 0 000 16zm-3-5l6-6m0 6l-6-6"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                    />
                </svg>
            );
        case "warning":
            return (
                <svg width="18" height="18" viewBox="0 0 20 20" fill="none" aria-hidden="true">
                    <path
                        d="M10 3.5l7 12.5H3L10 3.5zM10 8.5v4M10 14.5v.5"
                        stroke="currentColor"
                        strokeWidth="1.8"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                    />
                </svg>
            );
        case "info":
        default:
            return (
                <svg width="18" height="18" viewBox="0 0 20 20" fill="none" aria-hidden="true">
                    <path
                        d="M10 18a8 8 0 100-16 8 8 0 000 16zm0-9v5m0-7.5v.5"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                    />
                </svg>
            );
    }
}

export function ToastItem({ toast, onDismiss }) {
    const isError = toast.type === "error";

    return (
        <div
            className={`toast-item toast-${toast.type || "info"}`}
            role={isError ? "alert" : "status"}
            aria-live={isError ? "assertive" : "polite"}
            aria-atomic="true"
        >
            <span className="toast-icon">
                <ToastIcon type={toast.type} />
            </span>
            <div className="toast-content">
                <span className="toast-message">{toast.message}</span>
            </div>
            <button
                type="button"
                className="toast-close"
                onClick={() => onDismiss(toast.id)}
                aria-label="Close notification"
            >
                <svg width="12" height="12" viewBox="0 0 16 16" fill="none" aria-hidden="true">
                    <path
                        d="M3 3l10 10m0-10L3 13"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                    />
                </svg>
            </button>
        </div>
    );
}

export function ToastContainer({ toasts = [], onDismiss }) {
    if (!toasts || toasts.length === 0) {
        return null;
    }

    return (
        <div
            className="toast-container"
            role="region"
            aria-label="Notifications"
        >
            {toasts.map((toast) => (
                <ToastItem
                    key={toast.id}
                    toast={toast}
                    onDismiss={onDismiss}
                />
            ))}
        </div>
    );
}

export default ToastContainer;
