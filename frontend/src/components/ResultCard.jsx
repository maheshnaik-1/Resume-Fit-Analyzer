function ResultCard({
    title,
    items,
    emptyMessage = "No data available",
    className = ""
}) {
    const isSuggestions = title.includes("Suggestions");

    return (
        <div className={`result-panel ${isSuggestions ? "suggestions-panel" : ""} ${className}`}>
            <h3>{title}</h3>

            {isSuggestions ? (
                <ul className="suggestions-list">
                    {(items || []).length > 0 ? (
                        items.map((item, index) => (
                            <li
                                key={index}
                                className="suggestion-item"
                            >
                                <span className="suggestion-icon">💡</span>
                                <span>{item}</span>
                            </li>
                        ))
                    ) : (
                        <div className="empty-state">
                            {emptyMessage}
                        </div>
                    )}
                </ul>
            ) : (
                <div className="modern-list">
                    {(items || []).length > 0 ? (
                        items.map((item, index) => (
                            <div
                                key={index}
                                className="modern-item"
                            >
                                <span className="modern-dot">✓</span>
                                <span>{item}</span>
                            </div>
                        ))
                    ) : (
                        <div className="empty-state">
                            {emptyMessage}
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}

export default ResultCard;