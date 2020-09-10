package com.mukesh.countrypicker;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mukesh.countrypicker.listeners.BottomSheetInteractionListener;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
import com.mukesh.countrypicker.listeners.OnItemClickListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CountryPicker implements BottomSheetInteractionListener, LifecycleObserver {

  // region Countries
  private final Country[] COUNTRIES = {


        new Country("af","Afrikaans","",0,""),
        new Country("sq","Albanian","",0,""),
        new Country("am","Amharic","",0,""),
        new Country("ar","Arabic","",0,""),
        new Country("hy","Armenian","",0,""),
        new Country("az","Azerbaijani","",0,""),
        new Country("eu","Basque","",0,""),
        new Country("be","Belarusian","",0,""),
        new Country("bn","Bengali","",0,""),
        new Country("bs","Bosnian","",0,""),
        new Country("bg","Bulgarian","",0,""),
        new Country("ca","Catalan","",0,""),
        new Country("ceb","Cebuano","",0,""),
        new Country("zh","Chinese (Simplified)","",0,""),
       // new Country("h-TW","Chinese (Traditional)","",0,""),
        new Country("co","Corsican","",0,""),
        new Country("hr","Croatian","",0,""),
        new Country("cs","Czech","",0,""),
        new Country("da","Danish","",0,""),
        new Country("nl","Dutch","",0,""),
        new Country("en","English","",0,""),
        new Country("eo","Esperanto","",0,""),
        new Country("et","Estonian","",0,""),
        new Country("fi","Finnish","",0,""),
        new Country("fr","French","",0,""),
        new Country("fy","Frisian","",0,""),
        new Country("gl","Galician","",0,""),
        new Country("ka","Georgian","",0,""),
        new Country("de","German","",0,""),
        new Country("el","Greek","",0,""),
        new Country("gu","Gujarati","",0,""),
        new Country("ht","Haitian Creole","",0,""),
        new Country("ah","Hausa","",0,""),
        new Country("	haw ","Hawaiian","",0,""),
        new Country("iw","Hebrew	he or","",0,""),
        new Country("hi","Hindi","",0,""),
        new Country("hmn","Hmong","",0,""),
        new Country("hu","Hungarian","",0,""),
        new Country("is","Icelandic","",0,""),
        new Country("ig","Igbo","",0,""),
        new Country("id","Indonesian","",0,""),
        new Country("ga","Irish","",0,""),
        new Country("it","Italian","",0,""),
        new Country("ja","Japanese","",0,""),
        new Country("jv","Javanese","",0,""),
        new Country("kn","Kannada","",0,""),
        new Country("kk","Kazakh","",0,""),
        new Country("km","Khmer","",0,""),
        new Country("rw","Kinyarwanda","",0,""),
        new Country("ko","Korean","",0,""),
        new Country("ku","Kurdish","",0,""),
        new Country("ky","Kyrgyz","",0,""),
        new Country("lo","Lao","",0,""),
        new Country("la","Latin","",0,""),
        new Country("lv","Latvian","",0,""),
        new Country("lt","Lithuanian","",0,""),
        new Country("lb","Luxembourgish","",0,""),
        new Country("mk","Macedonian","",0,""),
        new Country("mg","Malagasy","",0,""),
        new Country("ms","Malay","",0,""),
        new Country("ml","Malayalam","",0,""),
        new Country("mt","Maltese","",0,""),
        new Country("mi","Maori","",0,""),
        new Country("mr","Marathi","",0,""),
        new Country("mn","Mongolian","",0,""),
        new Country("my","Myanmar (Burmese)","",0,""),
        new Country("ne","Nepali","",0,""),
        new Country("no","Norwegian","",0,""),
        new Country("ny","Nyanja (Chichewa)","",0,""),
        new Country("or","Odia (Oriya)","",0,""),
        new Country("ps","Pashto","",0,""),
        new Country("fa","Persian","",0,""),
        new Country("pl","Polish","",0,""),
        new Country("pt","Portuguese (Portugal, Brazil)","",0,""),
        new Country("pa","Punjabi","",0,""),
        new Country("ro","Romanian","",0,""),
        new Country("ru","Russian","",0,""),
        new Country("sm","Samoan","",0,""),
        new Country("gd","Scots Gaelic","",0,""),
        new Country("sr","Serbian","",0,""),
        new Country("st","Sesotho","",0,""),
        new Country("sn","Shona","",0,""),
        new Country("sd","Sindhi","",0,""),
        new Country("si","Sinhala (Sinhalese)","",0,""),
        new Country("sk","Slovak","",0,""),
        new Country("sl","Slovenian","",0,""),
        new Country("so","Somali","",0,""),
        new Country("es","Spanish","",0,""),
        new Country("su","Sundanese","",0,""),
        new Country("sw","Swahili","",0,""),
        new Country("sv","Swedish","",0,""),
        new Country("tl","Tagalog (Filipino)","",0,""),
        new Country("tg","Tajik","",0,""),
        new Country("ta","Tamil","",0,""),
        new Country("tt","Tatar","",0,""),
        new Country("te","Telugu","",0,""),
        new Country("th","Thai","",0,""),
        new Country("tr","Turkish","",0,""),
        new Country("tk","Turkmen","",0,""),
        new Country("uk","Ukrainian","",0,""),
        new Country("ur","Urdu","",0,""),
        new Country("ug","Uyghur","",0,""),
        new Country("uz","Uzbek","",0,""),
        new Country("vi","Vietnamese","",0,""),
        new Country("cy","Welsh","",0,""),
        new Country("xh","Xhosa","",0,""),
        new Country("yi","Yiddish","",0,""),
        new Country("yo","Yoruba","",0,""),
        new Country("zu","Zulu","",0,""),


  };
  // endregion

  // region Variables
  public static final int SORT_BY_NONE = 0;
  public static final int SORT_BY_NAME = 1;
  public static final int SORT_BY_ISO = 2;
  public static final int SORT_BY_DIAL_CODE = 3;
  public static final int THEME_OLD = 1;
  public static final int THEME_NEW = 2;
  private int theme;

  private int style;
  private Context context;
  private int sortBy = SORT_BY_NONE;
  private OnCountryPickerListener onCountryPickerListener;
  private boolean canSearch = true;

  private List<Country> countries;
  private EditText searchEditText;
  private RecyclerView countriesRecyclerView;
  private LinearLayout rootView;
  private int textColor;
  private int hintColor;
  private int backgroundColor;
  private int searchIconId;
  private Drawable searchIcon;
  private CountriesAdapter adapter;
  private List<Country> searchResults;
  private BottomSheetDialogView bottomSheetDialog;
  private Dialog dialog;
  // endregion

  // region Constructors
  private CountryPicker() {
  }

  CountryPicker(Builder builder) {
    sortBy = builder.sortBy;
    if (builder.onCountryPickerListener != null) {
      onCountryPickerListener = builder.onCountryPickerListener;
    }
    style = builder.style;
    context = builder.context;
    canSearch = builder.canSearch;
    theme = builder.theme;
    countries = new ArrayList<>(Arrays.asList(COUNTRIES));
    sortCountries(countries);
  }
  // endregion

  // region Listeners
  private void sortCountries(@NonNull List<Country> countries) {
    if (sortBy == SORT_BY_NAME) {
      Collections.sort(countries, new Comparator<Country>() {
        @Override
        public int compare(Country country1, Country country2) {
          return country1.getName().trim().compareToIgnoreCase(country2.getName().trim());
        }
      });
    } else if (sortBy == SORT_BY_ISO) {
      Collections.sort(countries, new Comparator<Country>() {
        @Override
        public int compare(Country country1, Country country2) {
          return country1.getCode().trim().compareToIgnoreCase(country2.getCode().trim());
        }
      });
    } else if (sortBy == SORT_BY_DIAL_CODE) {
      Collections.sort(countries, new Comparator<Country>() {
        @Override
        public int compare(Country country1, Country country2) {
          return country1.getDialCode().trim().compareToIgnoreCase(country2.getDialCode().trim());
        }
      });
    }
  }
  // endregion

  // region Utility Methods
  public void showDialog(@NonNull AppCompatActivity activity) {
    if (countries == null || countries.isEmpty()) {
      throw new IllegalArgumentException(context.getString(R.string.error_no_countries_found));
    } else {
      activity.getLifecycle().addObserver(this);
      dialog = new Dialog(activity);
      View dialogView = activity.getLayoutInflater().inflate(R.layout.country_picker, null);
      initiateUi(dialogView);
      setCustomStyle(dialogView);
      setSearchEditText();
      setupRecyclerView(dialogView);
      dialog.setContentView(dialogView);
      if (dialog.getWindow() != null) {
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);
        if (theme == THEME_NEW) {
          Drawable background =
              ContextCompat.getDrawable(context, R.drawable.ic_dialog_new_background);
          if (background != null) {
            background.setColorFilter(
                new PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP));
          }
          rootView.setBackgroundDrawable(background);
          dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
      }
      dialog.show();
    }
  }

  // region BottomSheet Methods
  public void showBottomSheet(AppCompatActivity activity) {
    if (countries == null || countries.isEmpty()) {
      throw new IllegalArgumentException(context.getString(R.string.error_no_countries_found));
    } else {
      activity.getLifecycle().addObserver(this);
      bottomSheetDialog = BottomSheetDialogView.newInstance(theme);
      bottomSheetDialog.setListener(this);
      bottomSheetDialog.show(activity.getSupportFragmentManager(), "bottomsheet");
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  private void dismissDialogs() {
    if (bottomSheetDialog != null) {
      bottomSheetDialog.dismiss();
    }
    if (dialog != null) {
      dialog.dismiss();
    }
  }

  @Override public void setupRecyclerView(View sheetView) {
    searchResults = new ArrayList<>();
    searchResults.addAll(countries);
    adapter = new CountriesAdapter(sheetView.getContext(), searchResults,
        new OnItemClickListener() {
          @Override public void onItemClicked(Country country) {
            if (onCountryPickerListener != null) {
              onCountryPickerListener.onSelectCountry(country);
              if (bottomSheetDialog != null) {
                bottomSheetDialog.dismiss();
              }
              if (dialog != null) {
                dialog.dismiss();
              }
              dialog = null;
              bottomSheetDialog = null;
              textColor = 0;
              hintColor = 0;
              backgroundColor = 0;
              searchIconId = 0;
              searchIcon = null;
            }
          }
        },
        textColor);
    countriesRecyclerView.setHasFixedSize(true);
    LinearLayoutManager layoutManager = new LinearLayoutManager(sheetView.getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    countriesRecyclerView.setLayoutManager(layoutManager);
    countriesRecyclerView.setAdapter(adapter);
  }

  @Override public void setSearchEditText() {
    if (canSearch) {
      searchEditText.addTextChangedListener(new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
          // Intentionally Empty
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
          // Intentionally Empty
        }

        @Override
        public void afterTextChanged(Editable searchQuery) {
          search(searchQuery.toString());
        }
      });
      searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
          InputMethodManager imm = (InputMethodManager) searchEditText.getContext()
              .getSystemService(Context.INPUT_METHOD_SERVICE);
          if (imm != null) {
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
          }
          return true;
        }
      });
    } else {
      searchEditText.setVisibility(View.GONE);
    }
  }

  private void search(String searchQuery) {
    searchResults.clear();
    for (Country country : countries) {
      if (country.getName().toLowerCase(Locale.ENGLISH).contains(searchQuery.toLowerCase())) {
        searchResults.add(country);
      }
    }
    sortCountries(searchResults);
    adapter.notifyDataSetChanged();
  }

  @SuppressWarnings("ResourceType")
  @Override public void setCustomStyle(View sheetView) {
    if (style != 0) {
      int[] attrs =
          {
              android.R.attr.textColor, android.R.attr.textColorHint, android.R.attr.background,
              android.R.attr.drawable
          };
      TypedArray ta = sheetView.getContext().obtainStyledAttributes(style, attrs);
      textColor = ta.getColor(0, Color.BLACK);
      hintColor = ta.getColor(1, Color.GRAY);
      backgroundColor = ta.getColor(2, Color.WHITE);
      searchIconId = ta.getResourceId(3, R.drawable.ic_search);
      searchEditText.setTextColor(textColor);
      searchEditText.setHintTextColor(hintColor);
      searchIcon = ContextCompat.getDrawable(searchEditText.getContext(), searchIconId);
      if (searchIconId == R.drawable.ic_search) {
        searchIcon.setColorFilter(new PorterDuffColorFilter(hintColor, PorterDuff.Mode.SRC_ATOP));
      }
      searchEditText.setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, null, null);
      rootView.setBackgroundColor(backgroundColor);
      ta.recycle();
    }
  }

  @Override public void initiateUi(View sheetView) {
    searchEditText = sheetView.findViewById(R.id.country_code_picker_search);
    countriesRecyclerView = sheetView.findViewById(R.id.countries_recycler_view);
    rootView = sheetView.findViewById(R.id.rootView);
  }
  // endregion

  public Country getCountryFromSIM() {
    TelephonyManager telephonyManager =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if (telephonyManager != null
        && telephonyManager.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
      return getCountryByISO(telephonyManager.getSimCountryIso());
    }
    return null;
  }

  public Country getCountryByLocale(@NonNull Locale locale) {
    String countryIsoCode = locale.getISO3Country().substring(0, 2).toLowerCase();
    return getCountryByISO(countryIsoCode);
  }

  public Country getCountryByName(@NonNull String countryName) {
    Collections.sort(countries, new NameComparator());
    Country country = new Country();
    country.setName(countryName);
    int i = Collections.binarySearch(countries, country, new NameComparator());
    if (i < 0) {
      return null;
    } else {
      return countries.get(i);
    }
  }

  public Country getCountryByISO(@NonNull String countryIsoCode) {
    Collections.sort(countries, new ISOCodeComparator());
    Country country = new Country();
    country.setCode(countryIsoCode);
    int i = Collections.binarySearch(countries, country, new ISOCodeComparator());
    if (i < 0) {
      return null;
    } else {
      return countries.get(i);
    }
  }
  // endregion

  // region Builder
  public static class Builder {
    private Context context;
    private int sortBy = SORT_BY_NONE;
    private boolean canSearch = true;
    private OnCountryPickerListener onCountryPickerListener;
    private int style;
    private int theme = THEME_NEW;

    public Builder with(@NonNull Context context) {
      this.context = context;
      return this;
    }

    public Builder style(@NonNull @StyleRes int style) {
      this.style = style;
      return this;
    }

    public Builder sortBy(@NonNull int sortBy) {
      this.sortBy = sortBy;
      return this;
    }

    public Builder listener(@NonNull OnCountryPickerListener onCountryPickerListener) {
      this.onCountryPickerListener = onCountryPickerListener;
      return this;
    }

    public Builder canSearch(@NonNull boolean canSearch) {
      this.canSearch = canSearch;
      return this;
    }

    public Builder theme(@NonNull int theme) {
      this.theme = theme;
      return this;
    }

    public CountryPicker build() {
      return new CountryPicker(this);
    }
  }
  // endregion

  // region Comparators
  public static class ISOCodeComparator implements Comparator<Country> {
    @Override
    public int compare(Country country, Country nextCountry) {
      return country.getCode().compareToIgnoreCase(nextCountry.getCode());
    }
  }

  public static class NameComparator implements Comparator<Country> {
    @Override
    public int compare(Country country, Country nextCountry) {
      return country.getName().compareToIgnoreCase(nextCountry.getName());
    }
  }
  // endregion
}
