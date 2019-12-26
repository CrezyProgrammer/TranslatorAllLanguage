package com.quiz.translatoraalllanguage;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity {
    CustomAdapter adapter;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    String s;
    TextToSpeech t1;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recycler.setLayoutManager(manager);
        s = getIntent().getStringExtra("name");
        setSupportActionBar(toolbar);
        TextView title=toolbar.findViewById(R.id.title);
        title.setText(s.equals("Favourites.db") ? "Bookmark" : "History");
        final DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext(), s);
        ArrayList<Word> arrayList = dataBaseHelper.getAllWords();
        Log.i("123321", "" + arrayList);

        adapter = new CustomAdapter(getApplicationContext(), arrayList);

        recycler.setAdapter(adapter);
        TextView clear=toolbar.findViewById(R.id.clear);
        clear.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you Sure?");
            builder.setMessage("Delete it permanently");
            builder.setPositiveButton("ok",(dialog, which) -> {
                dataBaseHelper.deleteAllWords();
                ArrayList<Word> arrayList0 = dataBaseHelper.getAllWords();


                adapter = new CustomAdapter(getApplicationContext(), arrayList0);

                recycler.setAdapter(adapter);
            } );
            builder.setNegativeButton("cancel",null );
            builder.show();
        });
    }


    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

        private ArrayList<Word> originalItems;
        private ArrayList<Word> filteredItems;
        private LayoutInflater inflater;

        public CustomAdapter(@NonNull Context context,
                             @NonNull ArrayList<Word> objects) {

            this.originalItems = new ArrayList<>();
            this.originalItems.addAll(objects);
            filteredItems = new ArrayList<Word>();
            filteredItems.addAll(this.originalItems);
            inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return filteredItems.size();
        }

        public Word getItem(int position) {
            return filteredItems.get(position);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.list_item, parent, false);
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
            return originalItems.size();
        }

        @NonNull

        public Filter getFilter() {
            Filter filter = new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    final ArrayList<Word> filteredList = new ArrayList<Word>();

                    if (constraint.equals("") || constraint.toString().trim().length() == 0) {
                        results.values = originalItems;
                    } else {
                        String textToFilter = constraint.toString().toLowerCase();
                        for (Word word : originalItems) {
                            if (word.getWord().length() >= textToFilter.length() &&
                                    word.getWord().toLowerCase().contains(textToFilter)) {
                                filteredList.add(word);
                            }
                        }
                        results.values = filteredList;
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.values != null) {
                        filteredItems = (ArrayList<Word>) results.values;
                        notifyDataSetChanged();
                    }
                }
            };
            return filter;
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView l1, l2, input, output;
            CardView cardView;
            ImageView copy, share, speak;

            public MyViewHolder(View itemView) {
                super(itemView);
                l1 = itemView.findViewById(R.id.l1);
                l2 = itemView.findViewById(R.id.l2);
                input = itemView.findViewById(R.id.input);
                output = itemView.findViewById(R.id.out);
                cardView = itemView.findViewById(R.id.card);
                copy = itemView.findViewById(R.id.copy);
                share = itemView.findViewById(R.id.share);
                speak = itemView.findViewById(R.id.speak);


            }

            public void details(int position) {
                Word word = originalItems.get(position);
                l1.setText(word.getSourceLanguage());
                l2.setText(word.getTargetLanguage());
                input.setText(word.getWord());
                output.setText(word.getTranslation());
                cardView.setOnLongClickListener(v -> {

                    Toast.makeText(HistoryActivity.this, "Sucessfully Deleted", Toast.LENGTH_SHORT).show();
                    DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext(), s);
                    dataBaseHelper.deleteWord(word);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyDataSetChanged();
                    recycler.setAdapter(adapter);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, originalItems.size());
                    ArrayList<Word> arrayList = dataBaseHelper.getAllWords();
                    Log.i("123321", "" + arrayList);

                    adapter = new CustomAdapter(getApplicationContext(), arrayList);

                    recycler.setAdapter(adapter);
                    return true;
                });
                copy.setOnClickListener(v -> {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", word.getTranslation());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(HistoryActivity.this, "Successfully copied", Toast.LENGTH_SHORT).show();
                });
                speak.setOnClickListener(v -> {
                    
                    {

                        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    final Locale loc = new Locale(word.getTargetLanguage(), "");

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
                                            word.getTranslation(), TextToSpeech.QUEUE_FLUSH, null);
                                } else Log.i("123321", "383");
                            }
                        });

                    }
                });
                share.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, word.getTranslation());
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Title goes here");
                    startActivity(Intent.createChooser(intent, "Share"));
                });

            }
        }
    }

}
