package org.example;

import java.util.HashMap;
import java.util.Map;

public class NLPClassifier {
    private final Map<String, String[]> keywordMap = new HashMap<>();

    public NLPClassifier() {
        // 1. PROFESSIONAL & ACADEMIC (Your current core)
        keywordMap.put("Finance", new String[]{"invoice", "tax", "payment", "salary", "receipt", "fees", "bank"});
        keywordMap.put("Education", new String[]{"assignment", "professor", "syllabus", "exam", "mpj", "project", "university"});
        keywordMap.put("Technical", new String[]{"github", "api", "database", "backend", "frontend", "maven", "sprint", "deployment"});

        // 2. LIFESTYLE & LOGISTICS
        keywordMap.put("Travel", new String[]{"flight", "hotel", "booking", "ticket", "passport", "visa", "itinerary", "boarding"});
        keywordMap.put("Personal", new String[]{"rent", "utilities", "grocery", "insurance", "subscription", "bills"});
        keywordMap.put("Automotive", new String[]{"service", "registration", "fuel", "parking", "license", "repair"});

        // 3. HEALTH & LEGAL
        keywordMap.put("Medical", new String[]{"prescription", "diagnosis", "patient", "doctor", "report", "vaccination", "lab"});
        keywordMap.put("Legal", new String[]{"contract", "agreement", "terms", "clause", "policy", "notary", "affidavit"});

        // 4. CAREER & GROWTH
        keywordMap.put("Career", new String[]{"resume", "cv", "application", "interview", "offer", "rejection", "linkedin"});
        keywordMap.put("Government", new String[]{"passport", "id", "pan", "aadhar", "voter", "license", "permit"});

        // 5. ENTERTAINMENT & CREATIVE
        keywordMap.put("Media", new String[]{"script", "lyrics", "recording", "photograph", "portfolio", "creative"});
    }

    public String classify(String text) {
        if (text == null || text.isBlank()) return "General";

        // Clean and normalize the text
        String cleanText = text.toLowerCase().trim();

        Map<String, Integer> scores = new HashMap<>();
        String bestCategory = "General";
        int highestScore = 0;

        // Count occurrences for each category
        for (Map.Entry<String, String[]> entry : keywordMap.entrySet()) {
            String category = entry.getKey();
            int currentCategoryScore = 0;

            for (String keyword : entry.getValue()) {
                // We count how many times the keyword appears
                int index = cleanText.indexOf(keyword);
                while (index != -1) {
                    currentCategoryScore++;
                    index = cleanText.indexOf(keyword, index + keyword.length());
                }
            }

            scores.put(category, currentCategoryScore);

            // Check if this category is the "winner"
            if (currentCategoryScore > highestScore) {
                highestScore = currentCategoryScore;
                bestCategory = category;
            }
        }

        // If no keywords were found at all, return General
        return (highestScore > 0) ? bestCategory : "General";
    }
}