package org.example;

import java.util.HashMap;
import java.util.Map;

public class NLPClassifier {
    private final Map<String, String[]> keywordMap = new HashMap<>();

    public NLPClassifier() {
        // These keywords will trigger the "Sorting" logic
        keywordMap.put("Finance", new String[]{"invoice", "tax", "payment", "salary", "receipt"});
        keywordMap.put("Education", new String[]{"assignment", "professor", "syllabus", "exam", "mpj"});
        keywordMap.put("Legal", new String[]{"contract", "agreement", "terms", "clause"});
        keywordMap.put("Medical", new String[]{"prescription", "diagnosis", "patient", "doctor"});
    }

    public String classify(String text) {
        if (text == null || text.isEmpty()) return "General";

        String lowerText = text.toLowerCase();
        for (Map.Entry<String, String[]> entry : keywordMap.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (lowerText.contains(keyword)) {
                    return entry.getKey();
                }
            }
        }
        return "General";
    }
}