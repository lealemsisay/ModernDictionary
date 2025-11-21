package service;

import java.util.List;
import java.util.ArrayList;

public class WordDefinition {
    private String word;
    private String phonetic;
    private String origin;  // Word origin/etymology
    private String source;  // Data source: "Online API", "Local Cache", or "Built-in Dictionary"
    private List<Meaning> meanings;
    
    // Constructors
    public WordDefinition() {
        this.meanings = new ArrayList<>();
    }
    
    public WordDefinition(String word, String phonetic, List<Meaning> meanings) {
        this.word = word;
        this.phonetic = phonetic;
        this.meanings = meanings != null ? meanings : new ArrayList<>();
    }
    
    // Getters and Setters
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    
    public String getPhonetic() { return phonetic; }
    public void setPhonetic(String phonetic) { this.phonetic = phonetic; }
    
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public List<Meaning> getMeanings() { return meanings; }
    public void setMeanings(List<Meaning> meanings) { 
        this.meanings = meanings != null ? meanings : new ArrayList<>();
    }

    public static class Meaning {
        private String partOfSpeech;
        private List<String> definitions;
        private List<String> synonyms;
        private List<String> antonyms;
        private List<String> examples;  // Example sentences
        
        // Constructors
        public Meaning() {
            this.definitions = new ArrayList<>();
            this.synonyms = new ArrayList<>();
            this.antonyms = new ArrayList<>();
            this.examples = new ArrayList<>();
        }
        
        public Meaning(String partOfSpeech, List<String> definitions, 
                      List<String> synonyms, List<String> antonyms) {
            this.partOfSpeech = partOfSpeech;
            this.definitions = definitions != null ? definitions : new ArrayList<>();
            this.synonyms = synonyms != null ? synonyms : new ArrayList<>();
            this.antonyms = antonyms != null ? antonyms : new ArrayList<>();
            this.examples = new ArrayList<>();
        }
        
        // Getters and Setters
        public String getPartOfSpeech() { return partOfSpeech; }
        public void setPartOfSpeech(String partOfSpeech) { this.partOfSpeech = partOfSpeech; }
        
        public List<String> getDefinitions() { return definitions; }
        public void setDefinitions(List<String> definitions) { 
            this.definitions = definitions != null ? definitions : new ArrayList<>();
        }
        
        public List<String> getSynonyms() { return synonyms; }
        public void setSynonyms(List<String> synonyms) { 
            this.synonyms = synonyms != null ? synonyms : new ArrayList<>();
        }
        
        public List<String> getAntonyms() { return antonyms; }
        public void setAntonyms(List<String> antonyms) { 
            this.antonyms = antonyms != null ? antonyms : new ArrayList<>();
        }
        
        public List<String> getExamples() { return examples; }
        public void setExamples(List<String> examples) { 
            this.examples = examples != null ? examples : new ArrayList<>();
        }
    }
}