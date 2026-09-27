import { useState } from "react";
import { API_BASE_URL } from "../utils/api";

function useResumeAnalysis({
    initialResult = null,
    file,
    setFile,
    company,
    role,
    jobDescription,
    fileInputRef,
    fetchHistory,
    fetchDashboardStats,
    fetchNotifications,
    setIsHistoryPreview
}) {

    const [loading, setLoading] = useState(false);
    const [loadingStep, setLoadingStep] = useState("");
    const [result, setResult] = useState(initialResult);
    const [successMessage, setSuccessMessage] = useState("");

    const analyzeResume = async () => {
    
    if (!file) {
        alert("Please upload a resume");
        return;
    }

    if (!company) {
        alert("Please select a company");
        return;
    }

    if (!role) {
        alert("Please select a role");
        return;
    }

    const formData = new FormData();

    formData.append("file", file);
    formData.append("job_description", jobDescription);

    formData.append("company", company);
    formData.append("role", role);

    formData.append(
      "email",
      localStorage.getItem("userEmail")
    );

    try {
      setLoading(true);

    setLoadingStep("📤 Uploading Resume...");
    await new Promise(resolve => setTimeout(resolve, 300));

    setLoadingStep("📄 Reading Resume...");
    await new Promise(resolve => setTimeout(resolve, 300));

    setLoadingStep("🧠 Extracting Skills...");
    await new Promise(resolve => setTimeout(resolve, 300));

    setLoadingStep("📊 Calculating ATS Score...");
    await new Promise(resolve => setTimeout(resolve, 300));

    setLoadingStep("🎯 Matching Suitable Roles...");
    await new Promise(resolve => setTimeout(resolve, 300));

    const response = await fetch(
        `${API_BASE_URL}/upload`,
        {
          method: "POST",
          body: formData,
        }
      );

      const data = await response.json();

      if (!response.ok) {
        alert(data.detail || data.message || "Failed to analyze resume.");
        setLoading(false);
        setLoadingStep("");
        return;
      }

      setLoadingStep("✅ Finalizing Report...");
      setResult(data);
      if (setIsHistoryPreview) {
        setIsHistoryPreview(false);
      }
      console.log("Upload success");

      await fetchHistory();
      console.log("History refreshed");

      await fetchDashboardStats();
      console.log("Stats refreshed");

      await fetchNotifications();
      console.log("Notifications refreshed");

      setLoading(false);

      setLoadingStep("");

      setFile(null);

      if (fileInputRef.current) {
          fileInputRef.current.value = "";
      }

    } catch (error) {
        console.error(error);
        console.error(error.stack);
        alert("Backend Connection Failed");
        setLoading(false);
        setLoadingStep("");
      }
  };

    return {
        loading,
        loadingStep,
        setLoading,

        result,
        setResult,

        successMessage,
        setSuccessMessage,

        analyzeResume,
    };
}

export default useResumeAnalysis;