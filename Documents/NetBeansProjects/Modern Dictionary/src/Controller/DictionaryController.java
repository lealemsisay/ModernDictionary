package Controller;

import service.DictionaryAPI;
import service.WordDefinition;
import service.AppStateService;
import service.TextToSpeechService;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.animation.PauseTransition;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class DictionaryController implements Initializable {
    @FXML private TextField searchField;
    @FXML private Button ttsButton;
    @FXML private MenuButton settingsMenuButton;
    @FXML private MenuItem themeMenuItem;
    @FXML private MenuItem fullscreenMenuItem;
    @FXML private MenuItem aboutMenuItem;
    @FXML private Button favoriteToggleButton;
    @FXML private VBox meaningsContainer;
    
    // Layout components
    @FXML private VBox sidebar;
    @FXML private VBox mainContent;
    @FXML private VBox recentSearchesContainer;
    @FXML private VBox favoritesContainer;
    @FXML private VBox wordHeaderSection;
    
    // Labels
    @FXML private Label wordLabel;
    @FXML private Label phoneticLabel;
    @FXML private Label originLabel;
    @FXML private Label sourceLabel;
    @FXML private ProgressIndicator progressIndicator;
    
    @FXML private VBox searchContainer;
    private ListView<String> suggestionsListView;
    private VBox suggestionsContainer;

    // Configuration
    private static final int MAX_MEANINGS = 3;
    private static final int MAX_DEFINITIONS_PER_MEANING = 3;
    private static final int MAX_EXAMPLES_PER_MEANING = 3;
    private static final int MAX_RELATED_WORDS = 6;

    private Stage stage;
    private boolean isFullScreen = false;
    private DictionaryAPI dictionaryAPI;
    private AppStateService appStateService;
    private TextToSpeechService ttsService;
    private boolean isDarkTheme = true; // Start with dark theme
    private String currentWord = "";
    private boolean isSpeaking = false;


@Override
public void initialize(URL location, ResourceBundle resources) {
    dictionaryAPI = new DictionaryAPI();
    appStateService = new AppStateService();
    ttsService = new TextToSpeechService();
    
    setupEventHandlers();
    setupSearchSuggestions();
    setupSidebar();
    favoriteToggleButton.setVisible(false);
    
    updateTTSButtonStyle();
    testDictionary();
    
    // Apply dark theme immediately and force meanings container dark
    applyForcedDarkTheme();
    forceMeaningsContainerDark();
    
    // Start maximized
    Platform.runLater(() -> {
        if (searchField.getScene() != null) {
            stage = (Stage) searchField.getScene().getWindow();
            stage.setMaximized(true);
            // Force dark background on the scene
            stage.getScene().setFill(javafx.scene.paint.Color.valueOf("#0D1117"));
            
            // Additional safety: force meanings container dark after scene is shown
            Platform.runLater(() -> {
                forceMeaningsContainerDark();
            });
        }
    });
}

private void forceMeaningsContainerDark() {
    if (meaningsContainer != null) {
        meaningsContainer.setStyle("-fx-spacing: 24; -fx-background: #0F141A; -fx-background-color: #0F141A;");
        
        // Also set the parent scroll pane background
        javafx.scene.Parent parent = meaningsContainer.getParent();
        if (parent instanceof ScrollPane) {
            ScrollPane scrollPane = (ScrollPane) parent;
            scrollPane.setStyle("-fx-background: #0F141A; -fx-background-color: #0F141A; -fx-border-color: transparent;");
        }
    }
}
// Update createMeaningBox to ensure transparent backgrounds
// Update addRelatedWordsSection to ensure transparent backgrounds
// Update the createSidebarItem method to ensure dark backgrounds
// Update the applyDarkTheme method
// Update the displayWordDefinition method to ensure dark backgrounds for meaning boxes
// Update the displayWordDefinition method to ensure dark backgrounds for meaning boxes



private void applyCompleteDarkTheme() {
    isDarkTheme = true;
    
    // Apply dark background to EVERY container and component
    Platform.runLater(() -> {
        try {
            // Set root background
            if (mainContent.getScene() != null && mainContent.getScene().getRoot() != null) {
                BorderPane root = (BorderPane) mainContent.getScene().getRoot();
                root.setStyle("-fx-background-color: #0D1117;");
            }
            
            // Force all main containers to dark
            if (mainContent != null) {
                mainContent.setStyle("-fx-background-color: #0F141A; -fx-padding: 36; -fx-spacing: 32;");
            }
            if (meaningsContainer != null) {
                meaningsContainer.setStyle("-fx-spacing: 24; -fx-background-color: #0F141A;");
            }
            if (sidebar != null) {
                sidebar.setStyle("-fx-background-color: #161B22; -fx-padding: 32; -fx-spacing: 32; -fx-pref-width: 320;");
            }
            if (searchField != null) {
                searchField.setStyle("-fx-background-color: #1F242C; -fx-border-color: #1E293B; -fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 14 16; -fx-font-size: 14px; -fx-text-fill: #F1F5F9; -fx-prompt-text-fill: #64748B;");
            }
            if (recentSearchesContainer != null) {
                recentSearchesContainer.setStyle("-fx-spacing: 12; -fx-background-color: #161B22;");
            }
            if (favoritesContainer != null) {
                favoritesContainer.setStyle("-fx-spacing: 12; -fx-background-color: #161B22;");
            }
            if (wordHeaderSection != null) {
                wordHeaderSection.setStyle("-fx-spacing: 20; -fx-background-color: #0F141A;");
            }
            
            // Update buttons
            if (!isSpeaking && ttsButton != null) {
                ttsButton.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F1F5F9; -fx-background-radius: 20; -fx-min-width: 44; -fx-min-height: 44; -fx-font-size: 14px;");
            }
            
            if (themeMenuItem != null) {
                themeMenuItem.setText("☀️ Switch to Light Theme");
            }
            
            // Update text colors
            updateWordDisplayForTheme();
            
            // Update sidebar
            updateRecentSearchesDisplay();
            updateFavoritesDisplay();
            updateSidebarContainers();
            updateFavoriteButtonState();
            updateSettingsMenuItems();
            
        } catch (Exception e) {
            System.err.println("Error in applyCompleteDarkTheme: " + e.getMessage());
        }
    });
}
// Update the createSidebarItem method to ensure dark backgrounds
// Replace the applyInitialTheme method

private void applyForcedDarkTheme() {
    // Apply dark theme immediately without any delays
    isDarkTheme = true;
    
    // Set all container backgrounds to dark
    if (mainContent != null) {
        mainContent.setStyle("-fx-background-color: #0F141A; -fx-padding: 36; -fx-spacing: 32;");
    }
    if (meaningsContainer != null) {
        meaningsContainer.setStyle("-fx-spacing: 24; -fx-background-color: #0F141A;");
    }
    if (sidebar != null) {
        sidebar.setStyle("-fx-background-color: #161B22; -fx-padding: 32; -fx-spacing: 32; -fx-pref-width: 320;");
    }
    if (searchField != null) {
        searchField.setStyle("-fx-background-color: #1F242C; -fx-border-color: #1E293B; -fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 14 16; -fx-font-size: 14px; -fx-text-fill: #F1F5F9; -fx-prompt-text-fill: #64748B;");
    }
    
    // Update other elements
    updateSettingsMenuItems();
    updateSidebarContainers();
}
// Update the applyDarkTheme method to be more aggressive
// Remove the problematic applyInitialTheme method and replace it


    
    private void updateSidebarContainers() {
    // Update sidebar containers with current theme
    if (isDarkTheme) {
        recentSearchesContainer.setStyle("-fx-background-color: #161B22;");
        favoritesContainer.setStyle("-fx-background-color: #161B22;");
        
        // Update existing sidebar items
        for (javafx.scene.Node node : recentSearchesContainer.getChildren()) {
            if (node instanceof HBox) {
                node.setStyle("-fx-padding: 12 16; -fx-background-color: #1F242C; -fx-background-radius: 8; -fx-cursor: hand;");
            }
        }
        for (javafx.scene.Node node : favoritesContainer.getChildren()) {
            if (node instanceof HBox) {
                node.setStyle("-fx-padding: 12 16; -fx-background-color: #1F242C; -fx-background-radius: 8; -fx-cursor: hand;");
            }
        }
    } else {
        recentSearchesContainer.setStyle("-fx-background-color: white;");
        favoritesContainer.setStyle("-fx-background-color: white;");
        
        // Update existing sidebar items
        for (javafx.scene.Node node : recentSearchesContainer.getChildren()) {
            if (node instanceof HBox) {
                node.setStyle("-fx-padding: 12 16; -fx-background-color: #F9FAFB; -fx-background-radius: 8; -fx-cursor: hand;");
            }
        }
        for (javafx.scene.Node node : favoritesContainer.getChildren()) {
            if (node instanceof HBox) {
                node.setStyle("-fx-padding: 12 16; -fx-background-color: #F9FAFB; -fx-background-radius: 8; -fx-cursor: hand;");
            }
        }
    }
}
    
    

    private void setupEventHandlers() {
        // Search on Enter key press
        searchField.setOnAction(e -> {
            hideSuggestions();
            searchWord();
        });
        
        ttsButton.setOnAction(e -> speakCurrentWord());
        themeMenuItem.setOnAction(e -> toggleTheme());
        favoriteToggleButton.setOnAction(e -> toggleFavorite());
        fullscreenMenuItem.setOnAction(e -> toggleFullscreen());
        aboutMenuItem.setOnAction(e -> showAbout());
    }

    private void applyInitialTheme() {
        // Apply theme safely without accessing scene immediately
        Platform.runLater(() -> {
            try {
                // Wait a bit to ensure scene is ready
                PauseTransition delay = new PauseTransition(Duration.millis(100));
                delay.setOnFinished(e -> {
                    if (isDarkTheme) {
                        applyDarkTheme();
                    } else {
                        applyLightTheme();
                    }
                });
                delay.play();
            } catch (Exception e) {
                System.err.println("Error applying initial theme: " + e.getMessage());
                // Apply basic dark theme as fallback
                applyBasicDarkTheme();
            }
        });
    }

private void applyDarkTheme() {
    try {
        // Safely get the root
        if (mainContent.getScene() != null && mainContent.getScene().getRoot() != null) {
            BorderPane root = (BorderPane) mainContent.getScene().getRoot();
            root.setStyle("-fx-background-color: #0D1117;");
        }
        
        // Apply component styles
        sidebar.setStyle("-fx-background-color: #161B22; -fx-padding: 32; -fx-spacing: 32; -fx-pref-width: 320; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 25, 0, 0, 8);");
        mainContent.setStyle("-fx-background-color: #0F141A; -fx-padding: 36; -fx-spacing: 32; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 6);");
        searchField.setStyle("-fx-background-color: #1F242C; -fx-border-color: #1E293B; -fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 14 16; -fx-font-size: 14px; -fx-text-fill: #F1F5F9; -fx-prompt-text-fill: #64748B;");
        
        // CRITICAL: Set meanings container background to dark
        meaningsContainer.setStyle("-fx-spacing: 24; -fx-background-color: #0F141A;");
        
        // Update TTS button for dark theme (only if not speaking)
        if (!isSpeaking) {
            ttsButton.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F1F5F9; -fx-background-radius: 20; -fx-min-width: 44; -fx-min-height: 44; -fx-font-size: 14px;");
        }
        
        themeMenuItem.setText("☀️ Switch to Light Theme");
        
        // Update current word display if exists
        if (currentWord != null && !currentWord.isEmpty()) {
            updateWordDisplayForTheme();
        }
        
        // Update sidebar displays and containers
        updateRecentSearchesDisplay();
        updateFavoritesDisplay();
        updateSidebarContainers();
        
        // Update favorite button
        updateFavoriteButtonState();
        
        // Update settings menu styling
        updateSettingsMenuItems();
        
    } catch (Exception e) {
        System.err.println("Error in applyDarkTheme: " + e.getMessage());
        applyBasicDarkTheme();
    }
}

private void applyBasicDarkTheme() {
    // Apply theme without accessing scene root
    sidebar.setStyle("-fx-background-color: #161B22; -fx-padding: 32; -fx-spacing: 32; -fx-pref-width: 320; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 25, 0, 0, 8);");
    mainContent.setStyle("-fx-background-color: #0F141A; -fx-padding: 36; -fx-spacing: 32; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 6);");
    searchField.setStyle("-fx-background-color: #1F242C; -fx-border-color: #1E293B; -fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 14 16; -fx-font-size: 14px; -fx-text-fill: #F1F5F9; -fx-prompt-text-fill: #64748B;");
    
    // CRITICAL: Set meanings container background to dark
    meaningsContainer.setStyle("-fx-spacing: 24; -fx-background-color: #0F141A;");
    
    // Update TTS button for dark theme
    if (!isSpeaking) {
        ttsButton.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F1F5F9; -fx-background-radius: 20; -fx-min-width: 44; -fx-min-height: 44; -fx-font-size: 14px;");
    }
    
    themeMenuItem.setText("☀️ Switch to Light Theme");
    
    // Update sidebar containers
    updateSidebarContainers();
    
    // Update settings menu styling
    updateSettingsMenuItems();
}

private void applyLightTheme() {
    try {
        // Safely get the root
        if (mainContent.getScene() != null && mainContent.getScene().getRoot() != null) {
            BorderPane root = (BorderPane) mainContent.getScene().getRoot();
            root.setStyle("-fx-background-color: #F3F4F6;");
        }
        
        // Apply component styles
        sidebar.setStyle("-fx-background-color: white; -fx-padding: 24; -fx-spacing: 24; -fx-pref-width: 300; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");
        mainContent.setStyle("-fx-background-color: white; -fx-padding: 32; -fx-spacing: 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        searchField.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 12 16; -fx-font-size: 14px; -fx-text-fill: #111827; -fx-prompt-text-fill: #6B7280;");
        
        // CRITICAL: Set meanings container background to light
        meaningsContainer.setStyle("-fx-spacing: 24; -fx-background-color: white;");
        
        // Update TTS button for light theme (green color, only if not speaking)
        if (!isSpeaking) {
            ttsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 20; -fx-min-width: 44; -fx-min-height: 44; -fx-font-size: 14px;");
        }
        
        themeMenuItem.setText("🌙 Switch to Dark Theme");
        
        // Update current word display if exists
        if (currentWord != null && !currentWord.isEmpty()) {
            updateWordDisplayForTheme();
        }
        
        // Update sidebar displays
        updateRecentSearchesDisplay();
        updateFavoritesDisplay();
        
        // Update favorite button
        updateFavoriteButtonState();
        
        // Update settings menu styling
        updateSettingsMenuItems();
        
    } catch (Exception e) {
        System.err.println("Error in applyLightTheme: " + e.getMessage());
    }
}

    private void updateSettingsMenuItems() {
        if (isDarkTheme) {
            // Dark theme - white text
            themeMenuItem.setStyle("-fx-text-fill: #F1F5F9;");
            fullscreenMenuItem.setStyle("-fx-text-fill: #F1F5F9;");
            aboutMenuItem.setStyle("-fx-text-fill: #F1F5F9;");
            settingsMenuButton.setStyle("-fx-background-color: #161B22; -fx-border-color: #1E293B; -fx-border-width: 1; -fx-border-radius: 12; -fx-padding: 12 16; -fx-text-fill: #F1F5F9; -fx-font-size: 14px;");
        } else {
            // Light theme - dark text
            themeMenuItem.setStyle("-fx-text-fill: #111827;");
            fullscreenMenuItem.setStyle("-fx-text-fill: #111827;");
            aboutMenuItem.setStyle("-fx-text-fill: #111827;");
            settingsMenuButton.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-border-radius: 8; -fx-padding: 8 12; -fx-text-fill: #111827; -fx-font-size: 14px;");
        }
    }

    private void updateWordDisplayForTheme() {
        if (isDarkTheme) {
            // Dark theme word styling
            wordLabel.setStyle("-fx-font-size: 56px; -fx-font-weight: bold; -fx-text-fill: #F1F5F9;");
            phoneticLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: 500; -fx-text-fill: #A8FFCE;");
            originLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #CBD5E1; -fx-font-style: italic;");
            sourceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #CBD5E1;");
        } else {
            // Light theme word styling
            wordLabel.setStyle("-fx-font-size: 56px; -fx-font-weight: bold; -fx-text-fill: #111827;");
            phoneticLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: 500; -fx-text-fill: #059669;");
            originLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4B5563; -fx-font-style: italic;");
            sourceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4B5563;");
        }
    }

    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        
        if (isDarkTheme) {
            applyDarkTheme();
        } else {
            applyLightTheme();
        }
        
        // Update the current word display if there is one
        if (currentWord != null && !currentWord.isEmpty()) {
            CompletableFuture.supplyAsync(() -> dictionaryAPI.fetchWordDefinition(currentWord))
                .thenAcceptAsync(result -> {
                    Platform.runLater(() -> {
                        if (result != null) {
                            displayWordDefinition(result);
                        }
                    });
                });
        }
    }

    private void setupSearchSuggestions() {
        suggestionsListView = new ListView<>();
        suggestionsListView.setPrefHeight(0);
        suggestionsListView.setMaxHeight(200);
        
        suggestionsContainer = new VBox();
        suggestionsContainer.setVisible(false);
        suggestionsContainer.setManaged(false);
        
        suggestionsContainer.prefWidthProperty().bind(searchField.widthProperty());
        suggestionsContainer.maxWidthProperty().bind(searchField.widthProperty());
        
        VBox.setMargin(suggestionsContainer, new Insets(5, 0, 0, 0));
        VBox.setVgrow(suggestionsListView, Priority.ALWAYS);
        suggestionsContainer.getChildren().add(suggestionsListView);
        searchContainer.getChildren().add(suggestionsContainer);
        
        // Search field listeners
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() >= 1) {
                showSuggestions(newValue);
            } else {
                hideSuggestions();
            }
        });
        
        suggestionsListView.setOnMouseClicked(event -> {
            String selected = suggestionsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                searchField.setText(selected);
                hideSuggestions();
                searchWord();
            }
        });
        
        // Handle Enter key on suggestions
        suggestionsListView.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    String selected = suggestionsListView.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        searchField.setText(selected);
                        hideSuggestions();
                        searchWord();
                        event.consume();
                    }
                    break;
                case ESCAPE:
                    hideSuggestions();
                    searchField.requestFocus();
                    event.consume();
                    break;
            }
        });
    }

    private void showSuggestions(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            hideSuggestions();
            return;
        }
        
        final String query = searchText.toLowerCase().trim();
        
        CompletableFuture.supplyAsync(() -> getSuggestions(query))
            .thenAcceptAsync(suggestions -> {
                Platform.runLater(() -> {
                    if (suggestions.isEmpty() || searchField.getText().isEmpty()) {
                        hideSuggestions();
                        return;
                    }
                    
                    suggestionsListView.getItems().setAll(suggestions);
                    
                    // Style suggestions based on current theme
                    if (isDarkTheme) {
                        suggestionsListView.setStyle("-fx-background-color: #1F242C; -fx-border-color: #1E293B; -fx-border-radius: 8;");
                        suggestionsListView.setCellFactory(lv -> new ListCell<String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText(null);
                                    setStyle("-fx-background-color: #1F242C; -fx-text-fill: #F1F5F9;");
                                } else {
                                    setText(item);
                                    setStyle("-fx-background-color: #1F242C; -fx-text-fill: #F1F5F9; -fx-font-size: 14px; -fx-padding: 8 12;");
                                }
                            }
                        });
                    } else {
                        suggestionsListView.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-radius: 8;");
                        suggestionsListView.setCellFactory(lv -> new ListCell<String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText(null);
                                    setStyle("-fx-background-color: white; -fx-text-fill: #111827;");
                                } else {
                                    setText(item);
                                    setStyle("-fx-background-color: white; -fx-text-fill: #111827; -fx-font-size: 14px; -fx-padding: 8 12;");
                                }
                            }
                        });
                    }
                    
                    suggestionsContainer.setVisible(true);
                    suggestionsContainer.setManaged(true);
                    suggestionsListView.setPrefHeight(Math.min(200, suggestions.size() * 35 + 2));
                });
            });
    }

    private List<String> getSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        
        List<String> historySuggestions = getSuggestionsFromHistory(query);
        List<String> favoriteSuggestions = getSuggestionsFromFavorites(query);
        List<String> builtInSuggestions = getSuggestionsFromBuiltIn(query);
        List<String> commonSuggestions = getSuggestionsFromCommonWords(query);
        
        for (String suggestion : historySuggestions) {
            if (!suggestions.contains(suggestion) && suggestions.size() < 20) {
                suggestions.add(suggestion);
            }
        }
        
        for (String suggestion : favoriteSuggestions) {
            if (!suggestions.contains(suggestion) && suggestions.size() < 20) {
                suggestions.add(suggestion);
            }
        }
        
        for (String suggestion : builtInSuggestions) {
            if (!suggestions.contains(suggestion) && suggestions.size() < 20) {
                suggestions.add(suggestion);
            }
        }
        
        for (String suggestion : commonSuggestions) {
            if (!suggestions.contains(suggestion) && suggestions.size() < 20) {
                suggestions.add(suggestion);
            }
        }
        
        return suggestions.size() > 20 ? suggestions.subList(0, 20) : suggestions;
    }

    private List<String> getSuggestionsFromHistory(String query) {
        List<String> suggestions = new ArrayList<>();
        List<String> history = appStateService.getSearchHistory();
        
        for (String word : history) {
            if (word.toLowerCase().startsWith(query)) {
                suggestions.add(word);
            }
        }
        
        for (String word : history) {
            if (word.toLowerCase().contains(query) && !suggestions.contains(word)) {
                suggestions.add(word);
            }
        }
        
        return suggestions;
    }

    private List<String> getSuggestionsFromFavorites(String query) {
        List<String> suggestions = new ArrayList<>();
        List<String> favorites = dictionaryAPI.getFavorites();
        
        for (String word : favorites) {
            if (word.toLowerCase().startsWith(query)) {
                suggestions.add(word);
            }
        }
        
        for (String word : favorites) {
            if (word.toLowerCase().contains(query) && !suggestions.contains(word)) {
                suggestions.add(word);
            }
        }
        
        return suggestions;
    }

    private List<String> getSuggestionsFromBuiltIn(String query) {
        List<String> suggestions = new ArrayList<>();
        
        try {
            List<String> builtInWords = dictionaryAPI.getBuiltInWords();
            
            for (String word : builtInWords) {
                if (word.toLowerCase().startsWith(query)) {
                    suggestions.add(word);
                }
            }
            
            for (String word : builtInWords) {
                if (word.toLowerCase().contains(query) && !suggestions.contains(word)) {
                    suggestions.add(word);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting built-in suggestions: " + e.getMessage());
        }
        
        return suggestions;
    }

    private List<String> getSuggestionsFromCommonWords(String query) {
        List<String> suggestions = new ArrayList<>();
        
        String[] commonWords = {
            "hello", "world", "computer", "programming", "dictionary", "software", 
            "algorithm", "database", "network", "system", "application", "development", 
            "technology", "internet", "digital", "code", "function", "variable", "class",
            "object", "method", "interface", "package", "library", "framework", "api",
            "web", "mobile", "desktop", "server", "client", "cloud", "data", "file",
            "document", "text", "word", "sentence", "language", "english", "grammar",
            "vocabulary", "meaning", "definition", "pronunciation", "phonetic", "speech"
        };
        
        for (String word : commonWords) {
            if (word.toLowerCase().startsWith(query)) {
                suggestions.add(word);
            }
        }
        
        for (String word : commonWords) {
            if (word.toLowerCase().contains(query) && !suggestions.contains(word)) {
                suggestions.add(word);
            }
        }
        
        return suggestions;
    }

    private void hideSuggestions() {
        suggestionsContainer.setVisible(false);
        suggestionsContainer.setManaged(false);
        suggestionsListView.getItems().clear();
    }

    private void setupSidebar() {
        updateRecentSearchesDisplay();
        updateFavoritesDisplay();
    }

    private void updateRecentSearchesDisplay() {
        recentSearchesContainer.getChildren().clear();
        List<String> recentSearches = appStateService.getSearchHistory();
        
        int count = Math.min(5, recentSearches.size());
        for (int i = 0; i < count; i++) {
            final String word = recentSearches.get(i);
            HBox searchItem = createSidebarItem(word, false);
            recentSearchesContainer.getChildren().add(searchItem);
        }
        
        if (recentSearches.isEmpty()) {
            Label emptyLabel = new Label("No recent searches");
            if (isDarkTheme) {
                emptyLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 14px;");
            } else {
                emptyLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 14px;");
            }
            recentSearchesContainer.getChildren().add(emptyLabel);
        }
    }

    private void updateFavoritesDisplay() {
        favoritesContainer.getChildren().clear();
        List<String> favorites = dictionaryAPI.getFavorites();
        
        int count = Math.min(5, favorites.size());
        for (int i = 0; i < count; i++) {
            final String word = favorites.get(i);
            HBox favoriteItem = createSidebarItem(word, true);
            favoritesContainer.getChildren().add(favoriteItem);
        }
        
        if (favorites.isEmpty()) {
            Label emptyLabel = new Label("No favorites yet");
            if (isDarkTheme) {
                emptyLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 14px;");
            } else {
                emptyLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 14px;");
            }
            favoritesContainer.getChildren().add(emptyLabel);
        }
    }

    private HBox createSidebarItem(String word, boolean isFavorite) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        
        if (isDarkTheme) {
            item.setStyle("-fx-padding: 12 16; -fx-background-color: #1F242C; -fx-background-radius: 8; -fx-cursor: hand;");
        } else {
            item.setStyle("-fx-padding: 12 16; -fx-background-color: #F9FAFB; -fx-background-radius: 8; -fx-cursor: hand;");
        }
        
        final String finalWord = word;
        
        // Add star icon for favorites
        if (isFavorite) {
            Label star = new Label("★");
            star.setStyle("-fx-text-fill: #FACC15; -fx-font-size: 14px;");
            item.getChildren().add(star);
        }
        
        Label wordLabel = new Label(word);
        if (isDarkTheme) {
            wordLabel.setStyle("-fx-text-fill: #F1F5F9; -fx-font-size: 14px; -fx-font-weight: 500;");
        } else {
            wordLabel.setStyle("-fx-text-fill: #111827; -fx-font-size: 14px; -fx-font-weight: 500;");
        }
        
        item.getChildren().add(wordLabel);
        
        // Hover effects
        item.setOnMouseEntered(e -> {
            if (isDarkTheme) {
                item.setStyle("-fx-padding: 12 16; -fx-background-color: #334155; -fx-background-radius: 8; -fx-cursor: hand;");
            } else {
                item.setStyle("-fx-padding: 12 16; -fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-cursor: hand;");
            }
        });
        
        item.setOnMouseExited(e -> {
            if (isDarkTheme) {
                item.setStyle("-fx-padding: 12 16; -fx-background-color: #1F242C; -fx-background-radius: 8; -fx-cursor: hand;");
            } else {
                item.setStyle("-fx-padding: 12 16; -fx-background-color: #F9FAFB; -fx-background-radius: 8; -fx-cursor: hand;");
            }
        });
        
        item.setOnMouseClicked(e -> {
            searchField.setText(finalWord);
            searchWord();
        });
        
        return item;
    }

    private void searchWord() {
        final String word = searchField.getText().trim();
        if (word.isEmpty()) return;

        hideSuggestions();
        appStateService.addToSearchHistory(word);
        showLoading();
        
        CompletableFuture.supplyAsync(() -> dictionaryAPI.fetchWordDefinition(word))
            .thenAcceptAsync(result -> {
                Platform.runLater(() -> {
                    hideLoading();
                    if (result != null) {
                        currentWord = result.getWord();
                        displayWordDefinition(result);
                        updateFavoriteButtonState();
                        wordHeaderSection.setVisible(true);
                        updateRecentSearchesDisplay();
                    } else {
                        showError("Word '" + word + "' not found. Please check spelling or try another word.");
                        currentWord = "";
                        favoriteToggleButton.setVisible(false);
                        wordHeaderSection.setVisible(false);
                    }
                });
            })
            .exceptionally(throwable -> {
                Platform.runLater(() -> {
                    hideLoading();
                    showError("Network error: Unable to connect to dictionary service. Please check your internet connection.");
                    currentWord = "";
                    favoriteToggleButton.setVisible(false);
                    wordHeaderSection.setVisible(false);
                });
                return null;
            });
    }

    private void displayWordDefinition(WordDefinition definition) {
        wordLabel.setText(definition.getWord());
        
        String phoneticText = definition.getPhonetic() != null ? 
            definition.getPhonetic() : "";
        phoneticLabel.setText(phoneticText);
        
        // Set origin and source
        if (definition.getOrigin() != null && !definition.getOrigin().isEmpty()) {
            originLabel.setText("Origin: " + definition.getOrigin());
            originLabel.setVisible(true);
        } else {
            originLabel.setVisible(false);
        }
        
        if (definition.getSource() != null) {
            sourceLabel.setText("Source: " + definition.getSource());
            sourceLabel.setVisible(true);
        } else {
            sourceLabel.setVisible(false);
        }
        
        meaningsContainer.getChildren().clear();
        
        if (definition.getMeanings() != null && !definition.getMeanings().isEmpty()) {
            definition.getMeanings().stream()
                .limit(MAX_MEANINGS)
                .forEach(meaning -> {
                    VBox meaningBox = createMeaningBox(meaning);
                    meaningsContainer.getChildren().add(meaningBox);
                });
            
            addRelatedWordsSection(definition);
        } else {
            Label noMeaningsLabel = new Label("No detailed meanings available for this word.");
            if (isDarkTheme) {
                noMeaningsLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 14px;");
            } else {
                noMeaningsLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 14px;");
            }
            meaningsContainer.getChildren().add(noMeaningsLabel);
        }
        
        favoriteToggleButton.setVisible(true);
        animateResults();
    }

  
    
    private void addRelatedWordsSection(WordDefinition definition) {
    if (definition.getMeanings().isEmpty()) return;
    
    List<String> allSynonyms = new ArrayList<>();
    for (WordDefinition.Meaning meaning : definition.getMeanings()) {
        if (meaning.getSynonyms() != null) {
            allSynonyms.addAll(meaning.getSynonyms());
        }
    }
    
    if (allSynonyms.isEmpty()) return;
    
    VBox relatedSection = new VBox(12);
    // Remove background styling to make it transparent
    relatedSection.setStyle("-fx-padding: 20;");
    
    Label title = new Label("Related Words");
    if (isDarkTheme) {
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #94A3B8;");
    } else {
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #4B5563;");
    }
    
    FlowPane relatedWordsContainer = new FlowPane();
    relatedWordsContainer.setHgap(10);
    relatedWordsContainer.setVgap(10);
    
    for (int i = 0; i < Math.min(MAX_RELATED_WORDS, allSynonyms.size()); i++) {
        final String word = allSynonyms.get(i);
        
        Button wordButton = new Button(word);
        if (isDarkTheme) {
            wordButton.setStyle("-fx-background-color: #334155; -fx-text-fill: #E2E8F0; -fx-font-weight: 600; -fx-background-radius: 20; -fx-padding: 8 14; -fx-font-size: 14px; -fx-cursor: hand;");
        } else {
            wordButton.setStyle("-fx-background-color: #E5E7EB; -fx-text-fill: #374151; -fx-font-weight: 600; -fx-background-radius: 20; -fx-padding: 8 14; -fx-font-size: 14px; -fx-cursor: hand;");
        }
        
        wordButton.setOnMouseEntered(e -> {
            wordButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 20; -fx-padding: 8 14; -fx-font-size: 14px; -fx-cursor: hand;");
        });
        
        wordButton.setOnMouseExited(e -> {
            if (isDarkTheme) {
                wordButton.setStyle("-fx-background-color: #334155; -fx-text-fill: #E2E8F0; -fx-font-weight: 600; -fx-background-radius: 20; -fx-padding: 8 14; -fx-font-size: 14px; -fx-cursor: hand;");
            } else {
                wordButton.setStyle("-fx-background-color: #E5E7EB; -fx-text-fill: #374151; -fx-font-weight: 600; -fx-background-radius: 20; -fx-padding: 8 14; -fx-font-size: 14px; -fx-cursor: hand;");
            }
        });
        
        wordButton.setOnAction(e -> {
            searchField.setText(word);
            searchWord();
        });
        
        relatedWordsContainer.getChildren().add(wordButton);
    }
    
    relatedSection.getChildren().addAll(title, relatedWordsContainer);
    meaningsContainer.getChildren().add(relatedSection);
}
    


    private VBox createMeaningBox(WordDefinition.Meaning meaning) {
    VBox meaningBox = new VBox(16);
    // Remove the background styling to make it transparent
    meaningBox.setStyle("-fx-padding: 20;");
    
    if (meaning.getPartOfSpeech() != null && !meaning.getPartOfSpeech().isEmpty()) {
        Label posLabel = new Label(meaning.getPartOfSpeech());
        if (isDarkTheme) {
            posLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #A8FFCE; -fx-font-style: italic;");
        } else {
            posLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #059669; -fx-font-style: italic;");
        }
        meaningBox.getChildren().add(posLabel);
    }
    
    if (meaning.getDefinitions() != null && !meaning.getDefinitions().isEmpty()) {
        VBox definitionsBox = new VBox(10);
        
        Label definitionsTitle = new Label("Definitions:");
        if (isDarkTheme) {
            definitionsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #94A3B8;");
        } else {
            definitionsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #4B5563;");
        }
        definitionsBox.getChildren().add(definitionsTitle);
        
        meaning.getDefinitions().stream()
            .limit(MAX_DEFINITIONS_PER_MEANING)
            .forEach(def -> {
                Label defLabel = new Label("• " + def);
                if (isDarkTheme) {
                    defLabel.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 15px; -fx-wrap-text: true; -fx-line-spacing: 4;");
                } else {
                    defLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 15px; -fx-wrap-text: true; -fx-line-spacing: 4;");
                }
                definitionsBox.getChildren().add(defLabel);
            });
        
        meaningBox.getChildren().add(definitionsBox);
    }
    
    if (meaning.getExamples() != null && !meaning.getExamples().isEmpty()) {
        VBox examplesBox = new VBox(10);
        
        Label examplesTitle = new Label("Examples:");
        if (isDarkTheme) {
            examplesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #94A3B8;");
        } else {
            examplesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #4B5563;");
        }
        examplesBox.getChildren().add(examplesTitle);
        
        meaning.getExamples().stream()
            .limit(MAX_EXAMPLES_PER_MEANING)
            .forEach(example -> {
                Label exampleLabel = new Label("💬 " + example);
                if (isDarkTheme) {
                    exampleLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 15px; -fx-font-style: italic; -fx-wrap-text: true; -fx-line-spacing: 4;");
                } else {
                    exampleLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 15px; -fx-font-style: italic; -fx-wrap-text: true; -fx-line-spacing: 4;");
                }
                examplesBox.getChildren().add(exampleLabel);
            });
        
        meaningBox.getChildren().add(examplesBox);
    }
    
    return meaningBox;
}
    
    

    private void updateFavoriteButtonState() {
        if (currentWord != null && !currentWord.isEmpty()) {
            boolean isFavorite = dictionaryAPI.isWordFavorite(currentWord);
            if (isFavorite) {
                favoriteToggleButton.setText("★");
                if (isDarkTheme) {
                    favoriteToggleButton.setStyle("-fx-background-color: #FACC15; -fx-text-fill: #0F141A; -fx-font-weight: bold; -fx-background-radius: 20; -fx-min-width: 44; -fx-min-height: 44; -fx-font-size: 18px;");
                } else {
                    favoriteToggleButton.setStyle("-fx-background-color: #FACC15; -fx-text-fill: #111827; -fx-font-weight: bold; -fx-background-radius: 20; -fx-min-width: 44; -fx-min-height: 44; -fx-font-size: 18px;");
                }
            } else {
                favoriteToggleButton.setText("☆");
                if (isDarkTheme) {
                    favoriteToggleButton.setStyle("-fx-background-color: #334155; -fx-text-fill: #FACC15; -fx-font-weight: bold; -fx-background-radius: 20; -fx-min-width: 44; -fx-min-height: 44; -fx-font-size: 18px;");
                } else {
                    favoriteToggleButton.setStyle("-fx-background-color: #E5E7EB; -fx-text-fill: #6B7280; -fx-font-weight: bold; -fx-background-radius: 20; -fx-min-width: 44; -fx-min-height: 44; -fx-font-size: 18px;");
                }
            }
        }
    }

    private void toggleFavorite() {
        if (currentWord != null && !currentWord.isEmpty()) {
            boolean isCurrentlyFavorite = dictionaryAPI.isWordFavorite(currentWord);
            
            if (isCurrentlyFavorite) {
                dictionaryAPI.removeFromFavorites(currentWord);
                showInformation("Favorite Removed", "Word '" + currentWord + "' removed from favorites.");
            } else {
                dictionaryAPI.addToFavorites(currentWord);
                showInformation("Favorite Added", "Word '" + currentWord + "' added to favorites.");
            }
            
            updateFavoriteButtonState();
            updateFavoritesDisplay();
        }
    }

    private void toggleFullscreen() {
        if (stage != null) {
            isFullScreen = !isFullScreen;
            stage.setFullScreen(isFullScreen);
            if (isFullScreen) {
                stage.setFullScreenExitHint("Press ESC to exit fullscreen");
            }
        }
    }

    private void showAbout() {
        showInformation("About Modern Dictionary", 
            "Modern Dictionary v1.0\n\n" +
            "A feature-rich dictionary application with:\n" +
            "• Word definitions and meanings\n" +
            "• Text-to-speech functionality\n" +
            "• Dark/Light theme support\n" +
            "• Search history and favorites\n" +
            "• Fullscreen mode\n\n" +
            "Created with JavaFX");
    }

 private void updateTTSButtonStyle() {
    Platform.runLater(() -> {
        if (ttsService.isAvailable()) {
            if (isSpeaking) {
                ttsButton.setText("⏸");
                ttsButton.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-background-radius: 20; -fx-min-width: 44; -fx-min-height: 44; -fx-font-size: 14px;");
            } else {
                ttsButton.setText("▶"); // Always show play icon when not speaking
                if (isDarkTheme) {
                    ttsButton.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F1F5F9; -fx-background-radius: 20; -fx-min-width: 44; -fx-min-height: 44; -fx-font-size: 14px;");
                } else {
                    ttsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 20; -fx-min-width: 44; -fx-min-height: 44; -fx-font-size: 14px;");
                }
            }
            ttsButton.setDisable(false);
        } else {
            ttsButton.setText("❌");
            ttsButton.setDisable(true);
        }
    });
}

    private void speakCurrentWord() {
        String word = wordLabel.getText();
        if (word.isEmpty() || word.equals("Welcome")) {
            showInformation("Text-to-Speech", "No word to speak. Please search for a word first.");
            return;
        }

        if (!ttsService.isAvailable()) {
            showError("Text-to-Speech Error", 
                "TTS is not available on your system.\n\n" +
                "Please install FreeTTS or check your system's speech capabilities.");
            return;
        }

        isSpeaking = true;
        updateTTSButtonStyle();

        CompletableFuture.runAsync(() -> {
            try {
                ttsService.speak(word);
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("TTS Error", "Failed to speak the word: " + e.getMessage());
                });
            } finally {
                Platform.runLater(() -> {
                    isSpeaking = false;
                    updateTTSButtonStyle();
                });
            }
        });
    }

    private void showLoading() {
        progressIndicator.setVisible(true);
        mainContent.setDisable(true);
    }

    private void hideLoading() {
        progressIndicator.setVisible(false);
        mainContent.setDisable(false);
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            styleAlert(alert);
            alert.showAndWait();
        });
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            styleAlert(alert);
            alert.showAndWait();
        });
    }

    private void showInformation(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            styleAlert(alert);
            alert.showAndWait();
        });
    }

    private void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        if (isDarkTheme) {
            dialogPane.setStyle("-fx-background-color: #0F141A; -fx-text-fill: #F1F5F9;");
            dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 14px;");
        } else {
            dialogPane.setStyle("-fx-background-color: white; -fx-text-fill: #111827;");
            dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #4B5563; -fx-font-size: 14px;");
        }
    }

    private void animateResults() {
        FadeTransition ft = new FadeTransition(Duration.millis(500), mainContent);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    public void cleanup() {
        if (ttsService != null) {
            ttsService.cleanup();
        }
    }

    
    
    private void testDictionary() {
        System.out.println("=== Testing Dictionary ===");
        String[] testWords = {"hello", "world", "computer", "nonexistent"};
        
        for (String word : testWords) {
            CompletableFuture.supplyAsync(() -> dictionaryAPI.fetchWordDefinition(word))
                .thenAcceptAsync(result -> {
                    Platform.runLater(() -> {
                        if (result != null) {
                            System.out.println("✓ Found: " + word + " - " + result.getSource());
                        } else {
                            System.out.println("✗ Not found: " + word);
                        }
                    });
                });
        }
    }

    
}