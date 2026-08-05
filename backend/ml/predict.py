from pathlib import Path

import joblib
import pandas as pd

model_path = (
    Path(__file__).resolve().parent.parent
    / "models"
    / "random_forest.pkl"
)

model = joblib.load(model_path)


def predict_role(skills, all_skills):

    row = {}

    for skill in all_skills:
        row[skill] = 1 if skill in skills else 0

    df = pd.DataFrame([row])

    probabilities = model.predict_proba(df)[0]

    classes = model.classes_

    predictions = sorted(
        zip(classes, probabilities),
        key=lambda x: x[1],
        reverse=True
    )

    top_predictions = [
        {
            "role": role,
            "confidence": round(prob * 100, 2)
        }
        for role, prob in predictions[:3]
    ]

    return {
        "role": top_predictions[0]["role"],
        "confidence": top_predictions[0]["confidence"],
        "top_predictions": top_predictions
    }