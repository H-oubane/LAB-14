# SecureStorageLabJava

## Application Android de stockage securise

Une application Android developpee en **Java** demontrant les bonnes pratiques de stockage local securise sur Android.

---

## Objectifs du laboratoire

- Stockage de preferences non sensibles avec `SharedPreferences`
- Stockage chiffre de tokens/secrets avec `EncryptedSharedPreferences` et `MasterKey`
- Gestion de fichiers internes (texte UTF-8 et JSON)
- Utilisation du cache temporaire avec purge manuelle
- Nettoyage complet des donnees
- Respect des regles de securite Android

---

## Architecture du projet
com.example.securestoragelabjava/

├── ui/

    └── MainActivity.java # Interface utilisateur principale

├── prefs/

    ├── AppPrefs.java # SharedPreferences (non chiffre)

    └── SecurePrefs.java # EncryptedSharedPreferences (chiffre)

├── files/

    ├── InternalTextStore.java # Stockage texte UTF-8

    └── StudentsJsonStore.java # Stockage JSON

├── cache/

    └── CacheStore.java # Gestion du cache temporaire

├── external/

    └── ExternalAppFilesStore.java # Stockage externe app-specific

└── model/

    └── Student.java # Modele de donnees



---

## Technologies utilisees

| Technologie | Version | Utilisation |
|-------------|---------|-------------|
| Android SDK | API 24+ | Minimum SDK |
| Java | 8+ | Langage |
| AndroidX Security Crypto | 1.1.0-alpha06 | Chiffrement des SharedPreferences |
| JSON | org.json | Serialisation des donnees |

---

## Capture video

https://github.com/user-attachments/assets/fbf47d1f-4fad-4bef-8ecd-b4c53f9f98cd


### Device File Explorer - Fichiers internes

<img width="1600" height="827" alt="image" src="https://github.com/user-attachments/assets/53699bab-2a66-4b30-8ca6-ea5ce79a25ac" />

### Logcat securise (aucun token en clair)

<img width="1600" height="886" alt="image" src="https://github.com/user-attachments/assets/1dd389ee-577f-40a4-bc0d-580ea30529ed" />

---

## Fonctionnalites

### 1. SharedPreferences (non chiffre)
- Stockage du nom d'utilisateur
- Stockage de la langue (fr/en/ar)
- Stockage du theme (light/dark/system)
- Utilisation de `apply()` (asynchrone) et `commit()` (synchrone)

### 2. EncryptedSharedPreferences (chiffre)
- Stockage securise des tokens
- Utilisation de `MasterKey` (Keystore Android)
- Chiffrement AES256-GCM pour les valeurs
- Chiffrement AES256-SIV pour les cles

### 3. Stockage interne
- Fichiers texte UTF-8
- Fichiers JSON (liste d'etudiants)
- Acces via `openFileOutput()` et `openFileInput()`

### 4. Cache temporaire
- Donnees regenerables
- Purge manuelle complete
- Acces via `getCacheDir()`

### 5. Nettoyage complet
- Suppression des SharedPreferences
- Suppression des fichiers internes
- Purge du cache

---

## Prerequis

- Android Studio Hedgehog | 2023.1.1 ou superieur
- Android SDK API 24+
- JDK 8 ou superieur
- Emulateur ou appareil Android physique

---

## Auteur
**H-oubane**
