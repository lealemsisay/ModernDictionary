package service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DictionaryAPI {
    // Merriam-Webster API Keys
    private static final String MW_DICTIONARY_KEY = "39f842a6-743a-45c8-a9e6-36d87489f405";
    private static final String MW_THESAURUS_KEY = "94266e04-58e7-4389-9754-571cfdb5167d";
    private static final String MW_DICTIONARY_URL = "https://dictionaryapi.com/api/v3/references/collegiate/json/";
    private static final String MW_THESAURUS_URL = "https://dictionaryapi.com/api/v3/references/thesaurus/json/";
    private static final String FREE_API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    
    // File paths - FIXED for your project structure
    private static final String CACHE_FILE = "dictionary_cache.json";
    private static final String FAVORITES_FILE = "favorites.json";
    
    // Data storage
    private JSONObject cacheData;
    private JSONObject builtInData;
    private JSONObject favoritesData;

    public DictionaryAPI() {
        loadBuiltInDictionary();
        loadCache();
        loadFavorites();
        System.out.println("DictionaryAPI initialized with favorites support");
    }

    public WordDefinition fetchWordDefinition(String word) {
        if (word == null || word.trim().isEmpty()) {
            return null;
        }
        
        WordDefinition definition = null;
        
        // 1. Try Free Dictionary API first
        System.out.println("Trying Free Dictionary API for: " + word);
        definition = fetchFromFreeDictionary(word);
        if (definition != null && hasCompleteData(definition)) {
            definition.setSource("Free Dictionary API");
            saveToCache(definition);
            return definition;
        }
        
        // 2. Try built-in dictionary
        System.out.println("Trying built-in dictionary for: " + word);
        definition = fetchFromBuiltIn(word);
        if (definition != null) {
            definition.setSource("Built-in Dictionary");
            return definition;
        }
        
        // 3. Try cache
        System.out.println("Trying cache for: " + word);
        definition = fetchFromCache(word);
        if (definition != null) {
            definition.setSource("Local Cache");
            return definition;
        }
        
        System.out.println("No definition found for: " + word);
        return null;
    }
    
    private WordDefinition fetchFromFreeDictionary(String word) {
        try {
            String apiUrl = FREE_API_URL + word.toLowerCase().trim();
            System.out.println("Calling Free API: " + apiUrl);
            String jsonResponse = makeHttpRequest(apiUrl);
            
            if (jsonResponse != null && !jsonResponse.trim().isEmpty()) {
                System.out.println("Free API response received");
                return parseFreeDictionaryResponse(jsonResponse, word);
            } else {
                System.out.println("Free API returned null or empty response");
            }
        } catch (Exception e) {
            System.err.println("Free Dictionary API error: " + e.getMessage());
        }
        return null;
    }
    
    private WordDefinition parseFreeDictionaryResponse(String jsonResponse, String word) {
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            if (jsonArray.length() == 0) {
                System.out.println("Free API: No entries found");
                return null;
            }

            JSONObject firstEntry = jsonArray.getJSONObject(0);
            WordDefinition definition = new WordDefinition();
            definition.setWord(firstEntry.getString("word"));

            // Get phonetic
            if (firstEntry.has("phonetic")) {
                definition.setPhonetic(firstEntry.getString("phonetic"));
            } else if (firstEntry.has("phonetics")) {
                JSONArray phonetics = firstEntry.getJSONArray("phonetics");
                for (int i = 0; i < phonetics.length(); i++) {
                    JSONObject ph = phonetics.getJSONObject(i);
                    if (ph.has("text") && !ph.isNull("text")) {
                        definition.setPhonetic(ph.getString("text"));
                        break;
                    }
                }
            }

            // Get origin
            if (firstEntry.has("origin")) {
                definition.setOrigin(firstEntry.getString("origin"));
            }

            // Parse meanings
            if (firstEntry.has("meanings")) {
                JSONArray meaningsArray = firstEntry.getJSONArray("meanings");
                List<WordDefinition.Meaning> meanings = new ArrayList<>();
                
                for (int i = 0; i < meaningsArray.length(); i++) {
                    JSONObject meaningObj = meaningsArray.getJSONObject(i);
                    WordDefinition.Meaning meaning = new WordDefinition.Meaning();
                    
                    // Part of speech
                    if (meaningObj.has("partOfSpeech")) {
                        meaning.setPartOfSpeech(meaningObj.getString("partOfSpeech"));
                    }
                    
                    // Definitions
                    if (meaningObj.has("definitions")) {
                        JSONArray defArray = meaningObj.getJSONArray("definitions");
                        List<String> definitions = new ArrayList<>();
                        List<String> examples = new ArrayList<>();
                        
                        for (int j = 0; j < defArray.length(); j++) {
                            JSONObject defObj = defArray.getJSONObject(j);
                            if (defObj.has("definition")) {
                                definitions.add(defObj.getString("definition"));
                            }
                            if (defObj.has("example")) {
                                examples.add(defObj.getString("example"));
                            }
                        }
                        meaning.setDefinitions(definitions);
                        meaning.setExamples(examples);
                    }
                    
                    // Synonyms
                    if (meaningObj.has("synonyms")) {
                        JSONArray synArray = meaningObj.getJSONArray("synonyms");
                        List<String> synonyms = new ArrayList<>();
                        for (int j = 0; j < synArray.length(); j++) {
                            synonyms.add(synArray.getString(j));
                        }
                        meaning.setSynonyms(synonyms);
                    }
                    
                    // Antonyms
                    if (meaningObj.has("antonyms")) {
                        JSONArray antArray = meaningObj.getJSONArray("antonyms");
                        List<String> antonyms = new ArrayList<>();
                        for (int j = 0; j < antArray.length(); j++) {
                            antonyms.add(antArray.getString(j));
                        }
                        meaning.setAntonyms(antonyms);
                    }
                    
                    meanings.add(meaning);
                }
                definition.setMeanings(meanings);
            }

            System.out.println("Successfully parsed Free API response");
            return definition;
        } catch (Exception e) {
            System.err.println("Error parsing Free Dictionary: " + e.getMessage());
            return null;
        }
    }
    
    private boolean hasCompleteData(WordDefinition definition) {
        return definition != null && 
               definition.getMeanings() != null && 
               !definition.getMeanings().isEmpty() &&
               !definition.getMeanings().get(0).getDefinitions().isEmpty();
    }

    // Cache methods
    private WordDefinition fetchFromCache(String word) {
        try {
            String searchWord = word.toLowerCase();
            if (cacheData.has(searchWord)) {
                return parseWordDefinition(cacheData.getJSONObject(searchWord), word);
            }
        } catch (Exception e) {
            System.err.println("Error fetching from cache: " + e.getMessage());
        }
        return null;
    }
    
    private void saveToCache(WordDefinition definition) {
        try {
            cacheData.put(definition.getWord().toLowerCase(), wordDefinitionToJson(definition));
            FileWriter writer = new FileWriter(CACHE_FILE);
            writer.write(cacheData.toString(2));
            writer.close();
        } catch (Exception e) {
            System.err.println("Error saving to cache: " + e.getMessage());
        }
    }
    
    private JSONObject wordDefinitionToJson(WordDefinition definition) {
        JSONObject json = new JSONObject();
        if (definition.getPhonetic() != null) json.put("phonetic", definition.getPhonetic());
        if (definition.getOrigin() != null) json.put("origin", definition.getOrigin());
        
        JSONArray meaningsArray = new JSONArray();
        for (WordDefinition.Meaning meaning : definition.getMeanings()) {
            JSONObject meaningJson = new JSONObject();
            if (meaning.getPartOfSpeech() != null) meaningJson.put("partOfSpeech", meaning.getPartOfSpeech());
            
            JSONArray definitions = new JSONArray();
            for (String def : meaning.getDefinitions()) definitions.put(def);
            meaningJson.put("definitions", definitions);
            
            JSONArray synonyms = new JSONArray();
            for (String syn : meaning.getSynonyms()) synonyms.put(syn);
            meaningJson.put("synonyms", synonyms);
            
            JSONArray antonyms = new JSONArray();
            for (String ant : meaning.getAntonyms()) antonyms.put(ant);
            meaningJson.put("antonyms", antonyms);
            
            JSONArray examples = new JSONArray();
            for (String ex : meaning.getExamples()) examples.put(ex);
            meaningJson.put("examples", examples);
            
            meaningsArray.put(meaningJson);
        }
        json.put("meanings", meaningsArray);
        
        return json;
    }

    // Built-in dictionary - FIXED PATH
    private WordDefinition fetchFromBuiltIn(String word) {
        try {
            String searchWord = word.toLowerCase();
            if (builtInData != null && builtInData.has(searchWord)) {
                System.out.println("Found in built-in dictionary: " + word);
                return parseWordDefinition(builtInData.getJSONObject(searchWord), word);
            }
        } catch (Exception e) {
            System.err.println("Error fetching from built-in: " + e.getMessage());
        }
        return null;
    }
    
    private WordDefinition parseWordDefinition(JSONObject wordData, String word) {
        WordDefinition definition = new WordDefinition();
        definition.setWord(word);
        
        try {
            if (wordData.has("phonetic")) definition.setPhonetic(wordData.getString("phonetic"));
            if (wordData.has("origin")) definition.setOrigin(wordData.getString("origin"));
            
            if (wordData.has("meanings")) {
                List<WordDefinition.Meaning> meanings = new ArrayList<>();
                JSONArray meaningsArray = wordData.getJSONArray("meanings");
                
                for (int i = 0; i < meaningsArray.length(); i++) {
                    JSONObject meaningObj = meaningsArray.getJSONObject(i);
                    WordDefinition.Meaning meaning = new WordDefinition.Meaning();
                    
                    if (meaningObj.has("partOfSpeech")) meaning.setPartOfSpeech(meaningObj.getString("partOfSpeech"));
                    
                    if (meaningObj.has("definitions")) {
                        JSONArray defArray = meaningObj.getJSONArray("definitions");
                        List<String> definitions = new ArrayList<>();
                        for (int j = 0; j < defArray.length(); j++) definitions.add(defArray.getString(j));
                        meaning.setDefinitions(definitions);
                    }
                    
                    if (meaningObj.has("synonyms")) {
                        JSONArray synArray = meaningObj.getJSONArray("synonyms");
                        List<String> synonyms = new ArrayList<>();
                        for (int j = 0; j < synArray.length(); j++) synonyms.add(synArray.getString(j));
                        meaning.setSynonyms(synonyms);
                    }
                    
                    if (meaningObj.has("antonyms")) {
                        JSONArray antArray = meaningObj.getJSONArray("antonyms");
                        List<String> antonyms = new ArrayList<>();
                        for (int j = 0; j < antArray.length(); j++) antonyms.add(antArray.getString(j));
                        meaning.setAntonyms(antonyms);
                    }
                    
                    if (meaningObj.has("examples")) {
                        JSONArray exArray = meaningObj.getJSONArray("examples");
                        List<String> examples = new ArrayList<>();
                        for (int j = 0; j < exArray.length(); j++) examples.add(exArray.getString(j));
                        meaning.setExamples(examples);
                    }
                    
                    meanings.add(meaning);
                }
                definition.setMeanings(meanings);
            }
        } catch (Exception e) {
            System.err.println("Error parsing word definition: " + e.getMessage());
        }
        
        return definition;
    }

    // File loading - FIXED for your structure
    private void loadBuiltInDictionary() {
        try {
            // Try multiple possible paths for the built-in dictionary
            String[] possiblePaths = {
                "/data/dictionary.json",
                "data/dictionary.json", 
                "src/data/dictionary.json",
                "../data/dictionary.json"
            };
            
            InputStream stream = null;
            for (String path : possiblePaths) {
                stream = getClass().getResourceAsStream(path);
                if (stream != null) {
                    System.out.println("Found built-in dictionary at: " + path);
                    break;
                }
            }
            
            if (stream == null) {
                // Try loading from file system
                File file = new File("data/dictionary.json");
                if (file.exists()) {
                    stream = new FileInputStream(file);
                    System.out.println("Found built-in dictionary at: data/dictionary.json (file system)");
                } else {
                    file = new File("src/data/dictionary.json");
                    if (file.exists()) {
                        stream = new FileInputStream(file);
                        System.out.println("Found built-in dictionary at: src/data/dictionary.json (file system)");
                    }
                }
            }
            
            if (stream != null) {
                builtInData = new JSONObject(new JSONTokener(stream));
                System.out.println("Built-in dictionary loaded with " + builtInData.length() + " words");
                stream.close();
            } else {
                builtInData = new JSONObject();
                System.err.println("Built-in dictionary file not found in any location");
                // Create a minimal built-in dictionary
                createMinimalDictionary();
            }
        } catch (Exception e) {
            System.err.println("Error loading built-in dictionary: " + e.getMessage());
            builtInData = new JSONObject();
            createMinimalDictionary();
        }
    }
    
    private void createMinimalDictionary() {
        try {
            // Create a minimal dictionary with common words
            String minimalDict = """
            {
                "hello": {
                    "phonetic": "/həˈləʊ/",
                    "meanings": [
                        {
                            "partOfSpeech": "noun",
                            "definitions": ["Used as a greeting or to begin a conversation"],
                            "synonyms": ["greeting", "welcome", "salutation"],
                            "antonyms": ["goodbye", "farewell"],
                            "examples": ["She said hello to everyone in the room."]
                        }
                    ]
                },
                "world": {
                    "phonetic": "/wɜːrld/",
                    "meanings": [
                        {
                            "partOfSpeech": "noun",
                            "definitions": ["The earth and all life on it"],
                            "synonyms": ["earth", "globe", "planet"],
                            "antonyms": [],
                            "examples": ["He traveled around the world."]
                        }
                    ]
                },
                "computer": {
                    "phonetic": "/kəmˈpjuːtər/",
                    "meanings": [
                        {
                            "partOfSpeech": "noun",
                            "definitions": ["An electronic device for storing and processing data"],
                            "synonyms": ["PC", "workstation", "processor"],
                            "antonyms": [],
                            "examples": ["She works on her computer all day."]
                        }
                    ]
                }
            }
            """;
            builtInData = new JSONObject(minimalDict);
            System.out.println("Created minimal built-in dictionary with " + builtInData.length() + " words");
        } catch (Exception e) {
            System.err.println("Error creating minimal dictionary: " + e.getMessage());
        }
    }
    
    private void loadCache() {
        try {
            File file = new File(CACHE_FILE);
            if (file.exists()) {
                cacheData = new JSONObject(new JSONTokener(new FileInputStream(file)));
                System.out.println("Cache loaded with " + cacheData.length() + " entries");
            } else {
                cacheData = new JSONObject();
                System.out.println("No cache file found, starting fresh");
            }
        } catch (Exception e) {
            System.err.println("Error loading cache: " + e.getMessage());
            cacheData = new JSONObject();
        }
    }

    // FAVORITES METHODS
    private void loadFavorites() {
        try {
            File favoritesFile = new File(FAVORITES_FILE);
            if (favoritesFile.exists()) {
                FileInputStream inputStream = new FileInputStream(favoritesFile);
                JSONTokener tokener = new JSONTokener(inputStream);
                favoritesData = new JSONObject(tokener);
                inputStream.close();
                System.out.println("Favorites loaded: " + favoritesData.length() + " words");
            } else {
                favoritesData = new JSONObject();
                System.out.println("No favorites file found, starting fresh");
            }
        } catch (Exception e) {
            System.err.println("Error loading favorites: " + e.getMessage());
            favoritesData = new JSONObject();
        }
    }

    private void saveFavorites() {
        try {
            FileWriter fileWriter = new FileWriter(FAVORITES_FILE);
            fileWriter.write(favoritesData.toString(2));
            fileWriter.close();
        } catch (Exception e) {
            System.err.println("Error saving favorites: " + e.getMessage());
        }
    }

    public void addToFavorites(String word) {
        try {
            favoritesData.put(word.toLowerCase(), true);
            saveFavorites();
            System.out.println("Added to favorites: " + word);
        } catch (Exception e) {
            System.err.println("Error adding to favorites: " + e.getMessage());
        }
    }

    public void removeFromFavorites(String word) {
        try {
            favoritesData.remove(word.toLowerCase());
            saveFavorites();
            System.out.println("Removed from favorites: " + word);
        } catch (Exception e) {
            System.err.println("Error removing from favorites: " + e.getMessage());
        }
    }

    public boolean isWordFavorite(String word) {
        return favoritesData.has(word.toLowerCase());
    }

    public List<String> getFavorites() {
        List<String> favorites = new ArrayList<>();
        try {
            String[] keys = JSONObject.getNames(favoritesData);
            if (keys != null) {
                for (String key : keys) {
                    favorites.add(key);
                }
            }
            // Sort alphabetically
            Collections.sort(favorites);
        } catch (Exception e) {
            System.err.println("Error getting favorites: " + e.getMessage());
        }
        return favorites;
    }

    public List<String> getBuiltInWords() {
        List<String> words = new ArrayList<>();
        try {
            if (builtInData != null) {
                // Get all keys from built-in dictionary
                String[] keys = JSONObject.getNames(builtInData);
                if (keys != null) {
                    for (String key : keys) {
                        words.add(key);
                    }
                }
            }
            // Sort alphabetically
            Collections.sort(words);
        } catch (Exception e) {
            System.err.println("Error getting built-in words: " + e.getMessage());
        }
        return words;
    }

    // HTTP utility
    private String makeHttpRequest(String apiUrl) {
        try {
            System.out.println("Making HTTP request to: " + apiUrl);
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestProperty("User-Agent", "ModernDictionaryApp/1.0");
            conn.setRequestProperty("Accept", "application/json");
            
            int responseCode = conn.getResponseCode();
            System.out.println("HTTP Response Code: " + responseCode);
            
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                System.out.println("HTTP request successful");
                return response.toString();
            } else {
                System.err.println("HTTP request failed with code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("HTTP request error: " + e.getMessage());
        }
        return null;
    }
}