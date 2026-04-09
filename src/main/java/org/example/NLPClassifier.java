package org.example;

import java.util.HashMap;
import java.util.Map;

public class NLPClassifier {
    private final Map<String, String[]> keywordMap = new HashMap<>();

    public NLPClassifier() {
        keywordMap.put("Finance", new String[]{"invoice", "tax", "payment", "salary", "receipt", "fees", "bank"});
        keywordMap.put("Education", new String[]{"assignment", "professor", "syllabus", "exam", "mpj", "project", "university"});
        keywordMap.put("Technical", new String[]{"github", "api", "database", "backend", "frontend", "maven", "sprint", "deployment"});
        keywordMap.put("Travel", new String[]{"flight", "hotel", "booking", "ticket", "passport", "visa", "itinerary"});
        keywordMap.put("Personal", new String[]{"rent", "utilities", "grocery", "insurance", "subscription", "bills"});
        keywordMap.put("Medical", new String[]{"prescription", "diagnosis", "patient", "doctor", "report", "lab"});
        keywordMap.put("Legal", new String[]{"contract", "agreement", "terms", "clause", "policy", "notary"});
        keywordMap.put("Career", new String[]{"resume", "cv", "application", "interview", "offer"});
    }

    public String classify(String text) {
        if (text == null || text.isBlank()) return "General";
        String cleanText = text.toLowerCase();

        String bestCategory = "General";
        int highestScore = 0;

        for (Map.Entry<String, String[]> entry : keywordMap.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                int index = cleanText.indexOf(keyword);
                while (index != -1) {
                    score++;
                    index = cleanText.indexOf(keyword, index + keyword.length());
                }
            }
            if (score > highestScore) {
                highestScore = score;
                bestCategory = entry.getKey();
            }
        }
        return (highestScore > 0) ? bestCategory : "General";
    }
}