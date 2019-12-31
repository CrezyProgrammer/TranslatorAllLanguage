package com.quiz.translatoraalllanguage;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity implements OnCountryPickerListener {
    @BindView(R.id.textToTranslate)
    EditText textToTranslate;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @BindView(R.id.translatedText)
    TextView translatedText;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.lang2)
    Button lang2;
    @BindView(R.id.lang1)
    Button lang1;
    @BindView(R.id.change)
    FloatingActionButton change;
    @BindView(R.id.bookmark)
    FloatingActionButton bookmark;
    @BindView(R.id.group)
    Group group;
    @BindView(R.id.root)
    ConstraintLayout rootView;
    private Spinner spinner1;
    private Spinner spinner2;
    private boolean isFavourite; // if current word is favourite.
    private boolean noTranslate; // do not translate at 1-st text changing. Need when initialize
    // with some text.
    Toolbar toolbar;
    private int sortBy = CountryPicker.SORT_BY_NONE;
    private CountryPicker countryPicker;
    String lan1text = "English", lan2text = "Bangla", lan1code = "en", lan2code = "bn";
    boolean lan = false;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar = findViewById(R.id.toolbar);
        SharedPreferences preferences = getSharedPreferences("name", MODE_PRIVATE);
        lan1code = preferences.getString("lan1code", "en");
        lan2code = preferences.getString("lan2code", "bn");
        lan1text = preferences.getString("lan1text", "English");
        lan2text = preferences.getString("lan2text", "Bangla");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        lang1.setText(lan1text);
        lang2.setText(lan2text);
        //textToTranslate = findViewById(R.id.textToTranslate);
        setSupportActionBar(toolbar);
        translatedText.setMovementMethod(new ScrollingMovementMethod());
        translatedText = findViewById(R.id.translatedText);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setArgs();
        textChangedListener();
        change.setOnClickListener(v -> {
            change.setRotation(change.getRotation() + 180);
            String l1 = lan1text;
            String l2 = lan2text;
            String lc1 = lan1code;
            String lc2 = lan2code;
            String t1 = textToTranslate.getText().toString();
            String t2 = translatedText.getText().toString();
            lan1text = l2;
            lan2text = l1;
            lan1code = lc2;
            lan2code = lc1;
            textToTranslate.setText(t2);
            translatedText.setText(t1);

            lang1.setText(lan1text);
            lang2.setText(lan2text);
            // translatedText.setText("Translating");
            SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
            editor.putString("lan1text", lan1text);
            editor.putString("lan1code", lan1code);
            editor.putString("lan2text", lan2text);
            editor.putString("lan2code", lan2code);
            editor.apply();
            translate(textToTranslate.getText().toString().trim());
        });
        navView.setNavigationItemSelectedListener(menuItem -> {
                    int id = menuItem.getItemId();
                    switch (id) {
                        case R.id.history:
                            Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                            intent.putExtra("name", "History.db");
                            startActivity(intent);
                            drawerLayout.closeDrawers();
                            break;
                        case R.id.bookmark:
                            Intent intent2 = new Intent(getApplicationContext(), HistoryActivity.class);
                            intent2.putExtra("name", "Favourites.db");
                            startActivity(intent2);
                            drawerLayout.closeDrawers();
                    }
                    return false;
                }
        );
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();

                if (heightDiff > 500) {
                  group.setVisibility(View.GONE);
                } else {
                    Log.e("123321", "keyboard closed");
                    group.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public void setArgs() {
        SharedPreferences sharedPref = getSharedPreferences("default", Context.MODE_PRIVATE);
        String text = sharedPref.getString("textToTranslate", "");
        String translation = sharedPref.getString("translatedText", "");
        int selection1 = sharedPref.getInt("selection1", 0);
        int selection2 = sharedPref.getInt("selection2", 1);
        isFavourite = sharedPref.getBoolean("isFavourite", false);
        if (!text.equals("")) {
            noTranslate = true;
            textToTranslate.setText(text);
            spinner1.setSelection(selection1);
            spinner2.setSelection(selection2);
            translatedText.setText(translation);

            if (isFavourite) {
                bookmark.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.like2));

            } else {
                bookmark.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.like));

            }
        }
    }

    public void textChangedListener() {

        // Translate the text after 500 milliseconds when user ends to typing
        RxTextView.textChanges(textToTranslate).
                filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        return charSequence.length() > 0;
                    }
                }).
                debounce(500, TimeUnit.MILLISECONDS).
                subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {

                        translate(charSequence.toString().trim());
                    }
                });

        RxTextView.textChanges(textToTranslate).
                filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        return charSequence.length() == 0;
                    }
                }).
                subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //    checkIfInFavourites();
                            }
                        });
                    }
                });
    }

    private void translate(String text) {

        if (noTranslate) {
            noTranslate = false;
            return;

        }
        Log.i("123321", "191 " + text);

        String APIKey = "trnsl.1.1.20170314T200256Z.c558a20c3d6824ff.7" +
                "860377e797dffcf9ce4170e3c21266cbc696f08";
        //   Log.i("123321", "210: "+l);

        Retrofit query = new Retrofit.Builder().baseUrl("https://translate.yandex.net/").
                addConverterFactory(GsonConverterFactory.create()).build();
        APIHelper apiHelper = query.create(APIHelper.class);
        Call<TranslatedText> call = apiHelper.getTranslation(APIKey, text,
                lan1code + "-" + lan2code);

        call.enqueue(new Callback<TranslatedText>() {
            @Override
            public void onResponse(Call<TranslatedText> call, final Response<TranslatedText> response) {

                Log.i("123321", "206 " + response);
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("123321", "210");
                            translatedText.setText(response.body().getText().get(0));
                            checkIfInFavourites();
                            addToHistory();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<TranslatedText> call, Throwable t) {
                Log.i("123321", "221");
            }
        });
    }


    private void showPicker() {
        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        CountryPicker.Builder builder =
                new CountryPicker.Builder().with(MainActivity.this)
                        .listener(this);
//        if (styleSwitch.isChecked()) {
//            builder.style(R.style.CountryPickerStyle);
//        }
        //builder.theme(themeSwitch.isChecked() ? CountryPicker.THEME_NEW : CountryPicker.THEME_OLD);
        builder.canSearch(true);
        builder.sortBy(sortBy);
        countryPicker = builder.build();
        if (true) {
            countryPicker.showBottomSheet(MainActivity.this);
        } else {
            countryPicker.showDialog(MainActivity.this);
        }
    }


    @Override
    public void onSelectCountry(Country country) {
        if (lan) {
            lan1text = country.getName();
            lang1.setText(country.getName());
            lan1code = country.getCode();
            SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
            editor.putString("lan1text", lan1text);
            editor.putString("lan1code", lan1code);
            editor.apply();
        } else {
            lan2text = country.getName();
            lang2.setText(country.getName());
            lan2code = country.getCode();
            SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
            editor.putString("lan2text", lan2text);
            editor.putString("lan2code", lan2code);
            editor.apply();
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @OnClick(R.id.lang2)
    public void onLang2Clicked() {
        lan = false;
        showPicker();
    }

    @OnClick(R.id.lang1)
    public void onLang1Clicked() {
        lan = true;
        showPicker();
    }

    @OnClick(R.id.translate)
    public void onViewClicked() {
        //    translatedText.setText("Translating");
        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);

        translate(textToTranslate.getText().toString().trim());
        try{
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);}
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.voice)
    public void onViewClicke0d() {
        promptSpeechInput();
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lan1code);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textToTranslate.setText(result.get(0));
                }
                break;
            }

        }
    }

    @OnClick(R.id.clear)
    public void onClearClicked() {
        textToTranslate.setText("");
        translatedText.setText("");
    }

    @OnClick(R.id.copy)
    public void onCopyClicked() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", translatedText.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Successfully copied", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.speak)
    public void onSpeakClicked() {

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    final Locale loc = new Locale(lan2code, "");

                    // switch(tts.isLanguageAvailable(loc)){
                    switch (t1.setLanguage(loc)) {

                        case TextToSpeech.LANG_AVAILABLE:
                            Log.i("TAG", "LANG_AVAILABLE");
                            break;
                        case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                            Log.i("TAG", "LANG_COUNTRY_AVAILABLE");
                            break;
                        case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                            Log.i("TAG", "LANG_COUNTRY_VAR_AVAILABLE");
                            break;
                        case TextToSpeech.LANG_MISSING_DATA:
                            Log.i("TAG", "LANG_MISSING_DATA");
                            break;
                        case TextToSpeech.LANG_NOT_SUPPORTED:
                            Log.i("TAG", "LANG_NOT_SUPPORTED");
                            Toast.makeText(MainActivity.this, "Language not found", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    Log.i("123321", translatedText.getText().toString());
                    t1.speak(
                            translatedText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                } else Log.i("123321", "383");
            }
        });

    }

    public void addToHistory() {
        String text = String.valueOf(textToTranslate.getText()).trim();
        if (!text.equals("")) {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext(), "History.db");
            dataBaseHelper.insertWord(new Word(textToTranslate.getText().toString().trim(),
                    translatedText.getText().toString(), lan1code,
                    lan2code));
            dataBaseHelper.close();
        }
    }

    public void checkIfInFavourites() {
        String text = String.valueOf(textToTranslate.getText());
        if (!text.equals("")) {

            DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext(), "Favourites.db");
            if (dataBaseHelper.isInDataBase(new Word(text, translatedText.getText().toString(),
                    lan1code, lan2code))) {

                bookmark.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.like2));

                isFavourite = true;
            } else {
                bookmark.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.like));

                isFavourite = false;
            }
            dataBaseHelper.close();
        } else {
            isFavourite = false;

        }
    }

    @OnClick(R.id.share)
    public void onShareClicked() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, translatedText.getText().toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, "Title goes here");
        startActivity(Intent.createChooser(intent, "Share"));
    }

    @OnClick(R.id.bookmark)
    public void onBookmarkClicked() {
        if (textToTranslate.getText().toString().trim().equals(""))
            Toast.makeText(this, "Please Write Something", Toast.LENGTH_SHORT).show();
        else {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext(),
                    "Favourites.db");
            String text = textToTranslate.getText().toString().trim();
            String translation = translatedText.getText().toString();
            String source = lan1code;
            String target = lan2code;
            Word item = new Word(text, translation, source, target);
            if (dataBaseHelper.isInDataBase(item)) {
                dataBaseHelper.deleteWord(item);

                isFavourite = false;
            } else {
                isFavourite = true;
                dataBaseHelper.insertWord(item);
                bookmark.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.like2));
            }
            dataBaseHelper.close();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            group.setVisibility(View.VISIBLE);
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            group.setVisibility(View.GONE);
        }
    }


}
