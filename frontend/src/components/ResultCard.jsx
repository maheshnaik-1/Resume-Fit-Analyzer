function ResultCard({
    title,
    items,
    emptyMessage = "No data available"
}) {
    const isSuggestions = title.includes("Suggestions");

    return (
        <div className={`result-card ${isSuggestions ? "suggestions-card" : ""}`}>
            <h2>{title}</h2>

            {isSuggestions ? (
                <ul className="suggestions-list">
                    {(items || []).map((item, index) => (
                        <li
                            key={index}
                            className="suggestion-item"
                        >
                            <span className="suggestion-icon">💡</span>
                            <span>{item}</span>
                        </li>
                    ))}
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