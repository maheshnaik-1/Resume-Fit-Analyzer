function NotificationPopup({
    showNotifications,
    notifications,
    onNotificationClick,
}) {

    if (!showNotifications) return null;

    return (
        <div className="notification-menu">

            <h3>🔔 Notifications</h3>

            {notifications.length === 0 ? (

                <div className="notification-empty">

                <div className="empty-icon">
                    📭
                </div>

                <div className="empty-title">
                    No notifications yet
                </div>

                <div className="empty-text">
                    Your resume activities
                    <br />
                    will appear here.
                </div>

            </div>

            ) : (

                notifications.map((item, index) => {
                    const isAnalysis = item.type === "analysis";

                    return (
                        <div
                            key={index}
                            className={`notification-item ${isAnalysis ? "notification-clickable" : ""}`}
                            role={isAnalysis ? "button" : undefined}
                            tabIndex={isAnalysis ? 0 : undefined}
                            aria-label={isAnalysis ? `Open analysis: ${item.message}` : undefined}
                            onClick={() => {
                                if (isAnalysis && onNotificationClick) {
                                    onNotificationClick(item);
                                }
                            }}
                            onKeyDown={(e) => {
                                if (isAnalysis && onNotificationClick && (e.key === "Enter" || e.key === " ")) {
                                    e.preventDefault();
                                    onNotificationClick(item);
                                }
                            }}
                        >
                            <div className="notification-content">
                                <div className="notification-message">
                                    {item.type === "analysis" && "📄 "}
                                    {item.type === "success" && "✅ "}
                                    {item.type === "warning" && "⚠️ "}
                                    {item.type === "error" && "❌ "}
                                    {item.message}
                                </div>
                                <div className="notification-time">
                                    🕒 {item.created_at}
                                </div>
                            </div>
                        </div>
                    );
                })

            )}

        </div>
    );
}

export default NotificationPopup;