package com.quiz.translatoraalllanguage;


import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ferfalk.simplesearchview.SimpleSearchView;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class DictioanryFragment extends Fragment {
    CustomAdapter adapter;
    ArrayList<Dictionary> arrayList;
    ImageView voice;
    SearchView searchView;
TextToSpeech t1;
    private boolean isFavourite;
    SimpleSearchView simpleSearchView;

    public DictioanryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment




        View view=inflater.inflate(R.layout.fragment_dictioanry, container, false);
        voice=view.findViewById(R.id.voice);
        RecyclerView recyclerView=view.findViewById(R.id.recycler_view);
        LinearLayoutManager manager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        final DatabaseHelper2 dataBaseHelper = new DatabaseHelper2(getActivity());
        if(dataBaseHelper.create()) {

            arrayList = dataBaseHelper.getAllWords();
        }
        Log.i("123321", "" + arrayList);

        adapter = new CustomAdapter(getActivity(), arrayList);

        recyclerView.setAdapter(adapter);
        setHasOptionsMenu(true);
 searchView=view.findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

           @Override
           public boolean onQueryTextSubmit(String query) {
               adapter.getFilter().filter(query);
               return false;
           }

           @Override
           public boolean onQueryTextChange(String query) {
               adapter.getFilter().filter(query);
               // Here is where we are going to implement the filter logic
               return true;
           }

       });

voice.setOnClickListener(v -> {
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
            getString(R.string.speech_prompt));
    try {
        startActivityForResult(intent, 100);
    } catch (ActivityNotFoundException a) {
        Toast.makeText(getActivity(),
                getString(R.string.speech_not_supported),
                Toast.LENGTH_SHORT).show();
    }
});
        return view;
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

        private ArrayList<Dictionary> originalItems;
        private ArrayList<Dictionary> filteredItems;
        private LayoutInflater inflater;

        public CustomAdapter(@NonNull Context context,
                             @NonNull ArrayList<Dictionary> objects) {

            this.originalItems = new ArrayList<>();
            this.originalItems.addAll(objects);
            filteredItems = new ArrayList<Dictionary>();
            filteredItems.addAll(this.originalItems);
            inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return filteredItems.size();
        }

        public Dictionary getItem(int position) {
            return filteredItems.get(position);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.list_item2, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.details(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return filteredItems.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView l1, l2, input, output;

            ImageView copy, share, speak,bookmark;

            public MyViewHolder(View itemView) {
                super(itemView);
                l1 = itemView.findViewById(R.id.source);
                l2 = itemView.findViewById(R.id.target);

                copy = itemView.findViewById(R.id.copy);
                share = itemView.findViewById(R.id.share);
                speak = itemView.findViewById(R.id.speak);
                bookmark=itemView.findViewById(R.id.bookmark);
            }

            public void checkIfInFavourites() {
                String text = l1.getText().toString();
                if (!text.equals("")) {

                    DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity(), "Dictionary.db");
                    if (dataBaseHelper.isInDataBase(new Word(text, l2.getText().toString(),
                           "",""))) {

                        bookmark.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.like2));

                        isFavourite = true;
                    } else {
                        bookmark.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.like));

                        isFavourite = false;
                    }
                    dataBaseHelper.close();
                } else {
                    isFavourite = false;

                }
            }
            public void details(int position ) {
                Dictionary word=filteredItems.get(position);
                if (filteredItems.size() != 0) {
                    try {
                        Dictionary Dictionary = filteredItems.get(position);
                        l1.setText(Dictionary.getSource());
                        l2.setText(Dictionary.getResult());
                        checkIfInFavourites();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                copy.setOnClickListener(v -> {
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", word.getSource()+"-"+word.getResult());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getActivity(), "Successfully copied", Toast.LENGTH_SHORT).show();
                });
                speak.setOnClickListener(v -> {

                    {

                        t1 = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    final Locale loc = new Locale(word.getSource(), "");

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
                                            //   Toast.makeText(getApplicationContext(), "Language not found", Toast.LENGTH_SHORT).show();
                                            break;
                                    }

                                    t1.speak(
                                            word.getSource(), TextToSpeech.QUEUE_FLUSH, null);
                                } else Log.i("123321", "383");
                            }
                        });

                    }
                });
                share.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, word.getSource()+"-"+word.getResult());
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Title goes here");
                    startActivity(Intent.createChooser(intent, "Share"));
                });
                bookmark.setOnClickListener(v -> {


                            DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity(),
                                    "Dictionary.db");
                            String text = word.getSource();
                            String translation = word.getResult();
                            String source ="";
                            String target ="";
                            Word item = new Word(text, translation, source, target);
                            if (dataBaseHelper.isInDataBase(item)) {
                                dataBaseHelper.deleteWord(item);

                                isFavourite = false;
                            } else {
                                isFavourite = true;
                                dataBaseHelper.insertWord(item);
                                bookmark.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.like2));
                            }
                            dataBaseHelper.close();



                });

            }
        }

        @NonNull

        public Filter getFilter() {
            Filter filter = new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    final ArrayList<Dictionary> filteredList = new ArrayList<Dictionary>();

                    if (constraint.equals("") || constraint.toString().trim().length() == 0) {
                        results.values = originalItems;
                    } else {
                        String textToFilter = constraint.toString().toLowerCase();

                        for (Dictionary Dictionary : originalItems) {
                            if (Dictionary.getSource().length() >= textToFilter.length() &&
                                    Dictionary.getSource().toLowerCase().startsWith(textToFilter)) {
                                filteredList.add(Dictionary);
                            }

                        }
                        results.values = filteredList;
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.values != null) {
                        filteredItems = (ArrayList<Dictionary>) results.values;
                        notifyDataSetChanged();
                    }
                }
            };
            return filter;
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                 searchView.setQuery(result.get(0),true);
                    Toast.makeText(getActivity(), ""+result.get(0), Toast.LENGTH_SHORT).show();
                  //  adapter.getFilter().filter(result.get(0));
                }
                break;
            }

        }
    }
}
