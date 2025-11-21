package service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AppStateService {
    private static final String SEARCH_HISTORY_FILE = "search_history.json";
    private static final String APP_STATE_FILE = "app_state.json";
    private static final int MAX_HISTORY_ITEMS = 20;
    
    // Word of the day list - you can expand this
    private static final String[] WORD_OF_THE_DAY_LIST = {
        "serendipity", "ephemeral", "ubiquitous", "nostalgia", "eloquent",
        "resilience", "paradox", "empathy", "benevolent", "mellifluous",
        "luminous", "quintessential", "ethereal", "perennial", "ambivalent",
        "magnanimous", "vicarious", "zenith", "nadir", "panacea"
    };
    
    private JSONArray searchHistory;
    private JSONObject appState;

    public AppStateService() {
        loadSearchHistory();
        loadAppState();
    }

    // Search History Methods
    public void addToSearchHistory(String word) {
        if (word == null || word.trim().isEmpty()) return;
        
        String searchWord = word.toLowerCase().trim();
        
        // Remove if already exists (to avoid duplicates)
        for (int i = 0; i < searchHistory.length(); i++) {
            if (searchWord.equals(searchHistory.getString(i))) {
                searchHistory.remove(i);
                break;
            }
        }
        
        // Add to beginning
        searchHistory.put(0, searchWord);
        
        // Limit history size
        while (searchHistory.length() > MAX_HISTORY_ITEMS) {
            searchHistory.remove(MAX_HISTORY_ITEMS);
        }
        
        saveSearchHistory();
    }

    public List<String> getSearchHistory() {
        List<String> history = new ArrayList<>();
        for (int i = 0; i < searchHistory.length(); i++) {
            history.add(searchHistory.getString(i));
        }
        return history;
    }

    public void removeFromSearchHistory(String word) {
        String searchWord = word.toLowerCase().trim();
        for (int i = 0; i < searchHistory.length(); i++) {
            if (searchWord.equals(searchHistory.getString(i))) {
                searchHistory.remove(i);
                saveSearchHistory();
                break;
            }
        }
    }

    public void clearSearchHistory() {
        searchHistory = new JSONArray();
        saveSearchHistory();
    }

    // Word of the Day Methods
    public String getWordOfTheDay() {
        String today = getTodayDate();
        
        // Check if we have a word for today
        if (appState.has("wordOfTheDay") && appState.has("wordOfTheDayDate")) {
            String storedDate = appState.getString("wordOfTheDayDate");
            if (today.equals(storedDate)) {
                return appState.getString("wordOfTheDay");
            }
        }
        
        // Generate new word of the day
        String newWord = generateWordOfTheDay();
        appState.put("wordOfTheDay", newWord);
        appState.put("wordOfTheDayDate", today);
        saveAppState();
        
        return newWord;
    }

    private String generateWordOfTheDay() {
        // Use date as seed for consistent word per day
        String today = getTodayDate();
        long seed = today.hashCode();
        Random random = new Random(seed);
        
        int index = random.nextInt(WORD_OF_THE_DAY_LIST.length);
        return WORD_OF_THE_DAY_LIST[index];
    }

    public boolean shouldShowWordOfTheDay() {
        if (!appState.has("lastWotdShownDate")) {
            return true;
        }
        
        String lastShownDate = appState.getString("lastWotdShownDate");
        String today = getTodayDate();
        
        return !today.equals(lastShownDate);
    }

    public void markWordOfTheDayShown() {
        appState.put("lastWotdShownDate", getTodayDate());
        saveAppState();
    }

    // File Operations
    private void loadSearchHistory() {
        try {
            File file = new File(SEARCH_HISTORY_FILE);
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                JSONTokener tokener = new JSONTokener(inputStream);
                searchHistory = new JSONArray(tokener);
                inputStream.close();
            } else {
                searchHistory = new JSONArray();
            }
        } catch (Exception e) {
            System.err.println("Error loading search history: " + e.getMessage());
            searchHistory = new JSONArray();
        }
    }

    private void saveSearchHistory() {
        try {
            FileWriter writer = new FileWriter(SEARCH_HISTORY_FILE);
            writer.write(searchHistory.toString(2));
            writer.close();
        } catch (Exception e) {
            System.err.println("Error saving search history: " + e.getMessage());
        }
    }

    private void loadAppState() {
        try {
            File file = new File(APP_STATE_FILE);
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                JSONTokener tokener = new JSONTokener(inputStream);
                appState = new JSONObject(tokener);
                inputStream.close();
            } else {
                appState = new JSONObject();
            }
        } catch (Exception e) {
            System.err.println("Error loading app state: " + e.getMessage());
            appState = new JSONObject();
        }
    }

    private void saveAppState() {
        try {
            FileWriter writer = new FileWriter(APP_STATE_FILE);
            writer.write(appState.toString(2));
            writer.close();
        } catch (Exception e) {
            System.err.println("Error saving app state: " + e.getMessage());
        }
    }

    private String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ISO_DATE);
    }
}