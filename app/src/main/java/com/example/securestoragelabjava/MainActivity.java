package com.example.securestoragelabjava;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.securestoragelabjava.cache.CacheStore;
import com.example.securestoragelabjava.files.InternalTextStore;
import com.example.securestoragelabjava.files.StudentsJsonStore;
import com.example.securestoragelabjava.model.Student;
import com.example.securestoragelabjava.prefs.AppPrefs;
import com.example.securestoragelabjava.prefs.SecurePrefs;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SecureStorageJava";
    private final List<String> langs = Arrays.asList("fr", "en", "ar");

    private EditText etName;
    private EditText etToken;
    private Spinner spLang;
    private Switch swDark;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etToken = findViewById(R.id.etToken);
        spLang = findViewById(R.id.spLang);
        swDark = findViewById(R.id.swDark);
        tvResult = findViewById(R.id.tvResult);

        setupLangSpinner();

        Button btnSavePrefs = findViewById(R.id.btnSavePrefs);
        Button btnLoadPrefs = findViewById(R.id.btnLoadPrefs);
        Button btnSaveJson = findViewById(R.id.btnSaveJson);
        Button btnLoadJson = findViewById(R.id.btnLoadJson);
        Button btnClear = findViewById(R.id.btnClear);

        btnSavePrefs.setOnClickListener(v -> savePrefs());
        btnLoadPrefs.setOnClickListener(v -> loadPrefsToUi());
        btnSaveJson.setOnClickListener(v -> saveJsonFile());
        btnLoadJson.setOnClickListener(v -> loadJsonFile());
        btnClear.setOnClickListener(v -> clearAll());

        loadPrefsToUi();
    }

    private void setupLangSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, langs
        );
        spLang.setAdapter(adapter);
    }

    private void savePrefs() {
        String name = etName.getText().toString().trim();
        String lang = langs.get(Math.max(0, spLang.getSelectedItemPosition()));
        String theme = swDark.isChecked() ? "dark" : "light";

        boolean ok = AppPrefs.save(this, name, lang, theme, false);

        String token = etToken.getText().toString();
        if (!token.isBlank()) {
            try {
                SecurePrefs.saveToken(this, token);
                Log.d(TAG, "Token sauvegardé (chiffré), longueur: " + token.length());
            } catch (Exception e) {
                tvResult.setText("Erreur chiffrement token : " + e.getMessage());
                return;
            }
        }

        Log.d(TAG, "Prefs sauvegardées ok=" + ok + ", name=" + name + ", lang=" + lang + ", theme=" + theme);

        try {
            CacheStore.write(this, "last_ui.txt", "name=" + name + ", lang=" + lang + ", theme=" + theme);
        } catch (Exception ignored) {}

        tvResult.setText(
                "✅ Sauvegarde prefs terminée.\n" +
                        "name=" + name + "\n" +
                        "lang=" + lang + "\n" +
                        "theme=" + theme + "\n" +
                        "token: stocké chiffré (longueur=" + token.length() + ")"
        );
    }

    private void loadPrefsToUi() {
        AppPrefs.Triple triple = AppPrefs.load(this);

        etName.setText(triple.name);
        swDark.setChecked("dark".equals(triple.theme));

        int idx = langs.indexOf(triple.lang);
        spLang.setSelection(idx >= 0 ? idx : 0);

        int tokenLen = 0;
        boolean hasToken = false;
        try {
            String token = SecurePrefs.loadToken(this);
            if (token != null && !token.isEmpty()) {
                tokenLen = token.length();
                hasToken = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur chargement token: " + e.getMessage());
        }

        tvResult.setText(
                "📂 Chargement prefs terminé.\n" +
                        "name=" + triple.name + "\n" +
                        "lang=" + triple.lang + "\n" +
                        "theme=" + triple.theme + "\n" +
                        "token: " + (hasToken ? "présent (longueur=" + tokenLen + ")" : "absent")
        );

        Log.d(TAG, "Prefs chargées name=" + triple.name + ", lang=" + triple.lang +
                ", theme=" + triple.theme + ", tokenLength=" + tokenLen);
    }

    private void saveJsonFile() {
        List<Student> students = Arrays.asList(
                new Student(1, "Amina", 20),
                new Student(2, "Omar", 21),
                new Student(3, "Sara", 19)
        );

        try {
            StudentsJsonStore.save(this, students);
            InternalTextStore.writeUtf8(this, "note.txt", "Sauvegarde JSON effectuée le " + System.currentTimeMillis());
            Log.d(TAG, "Fichiers internes écrits: students.json, note.txt");
            tvResult.setText("✅ Sauvegarde JSON terminée.\n" + students.size() + " étudiants sauvegardés.");
        } catch (Exception e) {
            tvResult.setText("❌ Erreur sauvegarde JSON : " + e.getMessage());
            Log.e(TAG, "Erreur sauvegarde JSON", e);
        }
    }

    private void loadJsonFile() {
        List<Student> students = StudentsJsonStore.load(this);

        String note = "";
        try {
            note = InternalTextStore.readUtf8(this, "note.txt");
        } catch (Exception e) {
            note = "(note.txt absent ou corrompu)";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📄 Chargement JSON terminé.\n");
        sb.append("note: ").append(note.length() > 50 ? note.substring(0, 50) + "..." : note).append("\n");
        sb.append("students (").append(students.size()).append("):\n");

        for (Student s : students) {
            sb.append("  • ").append(s.name).append(" (id=").append(s.id)
                    .append(", age=").append(s.age).append(" ans)\n");
        }

        tvResult.setText(sb.toString());
        Log.d(TAG, "JSON chargé: " + students.size() + " étudiants");
    }

    private void clearAll() {
        AppPrefs.clear(this);
        Log.d(TAG, "AppPrefs effacées");

        try {
            SecurePrefs.clear(this);
            Log.d(TAG, "SecurePrefs effacées");
        } catch (Exception e) {
            Log.e(TAG, "Erreur effacement secure prefs", e);
        }

        StudentsJsonStore.delete(this);
        InternalTextStore.delete(this, "note.txt");
        Log.d(TAG, "Fichiers JSON et note.txt supprimés");

        int purged = CacheStore.purge(this);
        Log.d(TAG, "Cache purgé: " + purged + " fichier(s)");

        etName.setText("");
        etToken.setText("");
        swDark.setChecked(false);
        spLang.setSelection(0);

        tvResult.setText(
                "🧹 Nettoyage complet terminé.\n" +
                        "✓ SharedPreferences effacées\n" +
                        "✓ SecurePreferences (token) effacées\n" +
                        "✓ students.json supprimé\n" +
                        "✓ note.txt supprimé\n" +
                        "✓ Cache purgé (" + purged + " fichier(s))"
        );
    }
}