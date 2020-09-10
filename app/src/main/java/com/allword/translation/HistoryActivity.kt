package com.allword.translation

import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.facebook.ads.*
import kotlinx.android.synthetic.main.activity_history.*
import java.util.*

class HistoryActivity : AppCompatActivity() {
    var adapter: CustomAdapter? = null

    var s: String? = null
    var t1: TextToSpeech? = null

    private var adView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        ButterKnife.bind(this)
        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true
        manager.stackFromEnd = true
        recycler!!.layoutManager = manager
        s = intent.getStringExtra("name")
        setSupportActionBar(toolbar)

        adView = AdView(this, "320675462379332_321536232293255", AdSize.BANNER_HEIGHT_50)
        AdSettings.addTestDevice("c77ae079-ddd2-45a2-a632-47ab1cd7f060")
        //

        if (BuildConfig.DEBUG) {
            AdSettings.setTestMode(true)

        }


        val adContainer: LinearLayout = findViewById<View>(R.id.banner_container) as LinearLayout

        adContainer.addView(adView)

        // Request an ad

        // Request an ad
        adView!!.loadAd()
        AdSettings.addTestDevice("c77ae079-ddd2-45a2-a632-47ab1cd7f060");

        tool_title.text = if (s == "Favourites.db") "Bookmark" else "History"
        val dataBaseHelper = DataBaseHelper(applicationContext, s)
        val arrayList = dataBaseHelper.allWords
        Log.i("123321", "" + arrayList)
        adapter = CustomAdapter(applicationContext, arrayList)
        recycler!!.adapter = adapter
        val clear = toolbar!!.findViewById<TextView>(R.id.clear)
        clear.setOnClickListener { v: View? ->
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Are you Sure?")
            builder.setMessage("Delete it permanently")
            builder.setPositiveButton("ok") { dialog: DialogInterface?, which: Int ->
                dataBaseHelper.deleteAllWords()
                val arrayList0 = dataBaseHelper.allWords
                adapter = CustomAdapter(applicationContext, arrayList0)
                recycler!!.adapter = adapter
            }
            builder.setNegativeButton("cancel", null)
            builder.show()
        }
    }

    inner class CustomAdapter(context: Context,
                              objects: ArrayList<Word>) : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
        private val originalItems: ArrayList<Word>
        private var filteredItems: ArrayList<Word>
        private val inflater: LayoutInflater
        val count: Int
            get() = filteredItems.size

        fun getItem(position: Int): Word {
            return filteredItems[position]
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = inflater.inflate(R.layout.list_item, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.details(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemCount(): Int {
            return originalItems.size
        }

        val filter: Filter
            get() = object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    val results = FilterResults()
                    val filteredList: ArrayList<Word> = ArrayList()
                    if (constraint == "" || constraint.toString().trim { it <= ' ' }.length == 0) {
                        results.values = originalItems
                    } else {
                        val textToFilter = constraint.toString().toLowerCase()
                        for (word in originalItems) {
                            if (word.word.length >= textToFilter.length &&
                                    word.word.toLowerCase().contains(textToFilter)) {
                                filteredList.add(word)
                            }
                        }
                        results.values = filteredList
                    }
                    return results
                }

                override fun publishResults(constraint: CharSequence, results: FilterResults) {
                    if (results.values != null) {
                        filteredItems = results.values as ArrayList<Word>
                        notifyDataSetChanged()
                    }
                }
            }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var l1: TextView
            var l2: TextView
            var input: TextView
            var output: TextView
            var cardView: CardView
            var copy: ImageView
            var share: ImageView
            var speak: ImageView
            fun details(position: Int) {
                val word = originalItems[position]
                l1.text = word.sourceLanguage
                l2.text = word.targetLanguage
                input.text = word.word
                output.text = word.translation
                cardView.setOnLongClickListener { v: View? ->
                    Toast.makeText(this@HistoryActivity, "Sucessfully Deleted", Toast.LENGTH_SHORT).show()
                    val dataBaseHelper = DataBaseHelper(applicationContext, s)
                    dataBaseHelper.deleteWord(word)
                    adapter!!.notifyItemRemoved(position)
                    adapter!!.notifyDataSetChanged()
                    recycler!!.adapter = adapter
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, originalItems.size)
                    val arrayList = dataBaseHelper.allWords
                    Log.i("123321", "" + arrayList)
                    adapter = CustomAdapter(applicationContext, arrayList)
                    recycler!!.adapter = adapter
                    true
                }


                var interstitialAd: InterstitialAd? = null
                AdSettings.addTestDevice("c77ae079-ddd2-45a2-a632-47ab1cd7f060")
                //

                if (BuildConfig.DEBUG) {
                    AdSettings.setTestMode(true)

                }
                interstitialAd = InterstitialAd(copy.context, "320675462379332_321528522294026")

                interstitialAd!!.loadAd()





                copy.setOnClickListener {


                    interstitialAd!!.setAdListener(object : InterstitialAdListener {
                        override fun onInterstitialDisplayed(ad: Ad) {
                            // Interstitial ad displayed callback
                            Log.e("123321", "Interstitial ad displayed.")
                        }

                        override fun onInterstitialDismissed(ad: Ad) {
                            // Interstitial dismissed callback
                            Log.e("123321", "Interstitial ad dismissed.")




interstitialAd.loadAd()
                            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("text", word.translation)

                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(this@HistoryActivity, "Successfully copied", Toast.LENGTH_SHORT).show()


                        }

                        override fun onError(ad: Ad?, adError: AdError) {
                            // Ad error callback
                            Log.e("123321", "Interstitial ad failed to load: " + adError.getErrorMessage())
                        }

                        override fun onAdLoaded(ad: Ad) {
                            // Interstitial ad is loaded and ready to be displayed
                            Log.d("123321", "Interstitial ad is loaded and ready to be displayed!")
                            // Show the ad

                        }

                        override fun onAdClicked(ad: Ad) {
                            // Ad clicked callback
                            Log.d("123321", "Interstitial ad clicked!")
                        }

                        override fun onLoggingImpression(ad: Ad) {
                            // Ad impression logged callback
                            Log.d("123321", "Interstitial ad impression logged!")
                        }
                    })

                    if(interstitialAd!!.isAdLoaded)
                    {interstitialAd!!.show()
                        interstitialAd!!.loadAd()

                    }

                    else

                 {   val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("text", word.translation)

                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(this@HistoryActivity, "Successfully copied", Toast.LENGTH_SHORT).show()}
                }
                speak.setOnClickListener { v: View? ->
                    run {
                        t1 = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
                            if (status != TextToSpeech.ERROR) {
                                val loc = Locale(word.targetLanguage, "")
                                when (t1!!.setLanguage(loc)) {
                                    TextToSpeech.LANG_AVAILABLE -> Log.i("TAG", "LANG_AVAILABLE")
                                    TextToSpeech.LANG_COUNTRY_AVAILABLE -> Log.i("TAG", "LANG_COUNTRY_AVAILABLE")
                                    TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> Log.i("TAG", "LANG_COUNTRY_VAR_AVAILABLE")
                                    TextToSpeech.LANG_MISSING_DATA -> Log.i("TAG", "LANG_MISSING_DATA")
                                    TextToSpeech.LANG_NOT_SUPPORTED -> Log.i("TAG", "LANG_NOT_SUPPORTED")
                                }
                                t1!!.speak(
                                        word.translation, TextToSpeech.QUEUE_FLUSH, null)
                            } else Log.i("123321", "383")
                        })
                    }
                }
                share.setOnClickListener { v: View? ->
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, word.translation)
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Title goes here")
                    startActivity(Intent.createChooser(intent, "Share"))
                }
            }

            init {
                l1 = itemView.findViewById(R.id.l1)
                l2 = itemView.findViewById(R.id.l2)
                input = itemView.findViewById(R.id.input)
                output = itemView.findViewById(R.id.out)
                cardView = itemView.findViewById(R.id.card)
                copy = itemView.findViewById(R.id.copy)
                share = itemView.findViewById(R.id.share)
                speak = itemView.findViewById(R.id.speak)
            }
        }

        init {
            originalItems = ArrayList()
            originalItems.addAll(objects)
            filteredItems = ArrayList()
            filteredItems.addAll(originalItems)
            inflater = LayoutInflater.from(context)
        }
    }
}