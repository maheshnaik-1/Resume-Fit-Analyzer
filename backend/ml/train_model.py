from pathlib import Path

import joblib
import pandas as pd

from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
from sklearn.model_selection import train_test_split

dataset_path = (
    Path(__file__).resolve().parent.parent
    / "dataset"
    / "resumes_dataset.csv"
)

df = pd.read_csv(dataset_path)

# Features (Skills)
X = df.drop("Role", axis=1)

# Target (Role)
y = df["Role"]

print("Features:", X.shape)
print("Labels:", y.shape)

X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.2,
    random_state=42
)

print("Training:", X_train.shape)
print("Testing :", X_test.shape)

model = RandomForestClassifier(
    n_estimators=100,
    random_state=42
)

model.fit(X_train, y_train)

print("Model trained successfully!")

predictions = model.predict(X_test)

accuracy = accuracy_score(
    y_test,
    predictions
)

print("Accuracy:", accuracy)

model_path = (
    Path(__file__).resolve().parent.parent
    / "models"
    / "random_forest.pkl"
)

joblib.dump(model, model_path)

print("Model saved to:", model_path)