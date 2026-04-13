## 📖 Dictionary Application

### 🧾 Overview
A feature-rich desktop dictionary application built with JavaFX. It allows users to search for word definitions, listen to pronunciations, manage favorites, and track recent searches.

The app fetches definitions from a free dictionary API (or falls back to built-in data) and provides a clean, responsive two-panel interface.

---

### 🚀 Features

#### 🔍 Word Search
Type a word and get detailed definitions, including:
- Part of speech  
- Examples  
- Synonyms  
- Antonyms  

#### 🔊 Text-to-Speech (TTS)
Hear the pronunciation of the current word  
*(Requires FreeTTS or system speech support)*

#### ⭐ Favorites
- Mark words as favorites  
- Displayed in the sidebar  
- Persist across sessions  

#### 🕒 Recent Searches
- Automatically stores recently searched words  
- Quick access from sidebar  

#### 💡 Search Suggestions
As you type, suggestions appear from:
- Search history  
- Favorites  
- Built-in words  
- Common-word list  

#### 📐 Responsive Layout
- Sidebar: recent searches & suggestions  
- Main panel: word details and definitions  

#### 💾 Persistent Storage
- Saves favorites and search history locally  
- Managed via:
  - `AppStateService`  
  - `DictionaryAPI`  

---

### 🛠️ Technologies Used
- Java  
- JavaFX  
- Free Dictionary API  
- FreeTTS (optional)

---

### 📌 Notes
- Works offline using built-in dictionary data if API is unavailable  
- Designed for a smooth and intuitive user experience  

---

### 📷 UI Structure
 ![Image Alt](https://github.com/lealemsisay/ModernDictionary/blob/40214df7b7e26723671f3046691034884046b2af/Screenshot%202026-04-13%20120436.png)
  ![Image Alt](https://github.com/lealemsisay/ModernDictionary/blob/cc47ddd8391edfbed9fd83509200ad66b1c84c65/Screenshot%202026-04-13%20132032.png)
  
