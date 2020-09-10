package com.allword.translation

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.Vision.Images.Annotate
import com.google.api.services.vision.v1.VisionRequest
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.*
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.cloud.translate.Translation
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.listeners.OnCountryPickerListener
import com.allword.translation.ImagePickerActivity.PickerOptionListener
import com.facebook.ads.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.dialog.view.*
import kotlinx.android.synthetic.main.layout.*
import kotlinx.android.synthetic.main.ocr.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

open class OcrActivity() : AppCompatActivity(), OnCountryPickerListener {
    val EXTRA_MESSAGE = "com.ltapps.textscanner.message"

    private var outputFileUri: Uri? = null
    private val mImageDetails: TextView? = null
    private val ANDROID_CERT_HEADER = "X-Android-Cert"
    private val ANDROID_PACKAGE_HEADER = "X-Android-Package"
    private val REQ_CODE_SPEECH_INPUT = 100
    private val MAX_DIMENSION = 1200 // img hq
    private val CLOUD_VISION_API_KEY: String = "AIzaSyC-NQxsdFgXrq_culH1dTykIbpAgGl5SRg"
    var timer = Timer()
    var progressDialog: ProgressDialog? =null
    val DELAY: Long = 2000 // milliseconds
    private val spinner1: Spinner? = null
    private val spinner2: Spinner? = null
 // do not translate at 1-st text changing. Need when initialize = false
 private val TAG: String? = OcrActivity::class.java.simpleName
    val REQUEST_IMAGE = 100
    // with some text.
    var toolbar: Toolbar? = null
    private val sortBy = CountryPicker.SORT_BY_NONE
    private var countryPicker: CountryPicker? = null
    var lan1text: String? = "English"
    var lan2text: String? = "Bangla"
    var lan1code: String? = "en"
    var lan2code: String? = "bn"
    var lan = false
    var isFavourite:Boolean = false
    var noTranslate:Boolean = false
    private var adView: com.facebook.ads.AdView? = null
    private var interstitialAd: InterstitialAd? = null

    var t1: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ocr)



        adView = AdView(this, "320675462379332_321536232293255", AdSize.BANNER_HEIGHT_50)
        AdSettings.addTestDevice("c77ae079-ddd2-45a2-a632-47ab1cd7f060")
        //

        if (BuildConfig.DEBUG) {
            AdSettings.setTestMode(true)

        }
        interstitialAd = InterstitialAd(this, "320675462379332_321528522294026")

        interstitialAd!!.loadAd()

        val adContainer: LinearLayout = findViewById<View>(R.id.banner_container) as LinearLayout

        adContainer.addView(adView)

        // Request an ad

        // Request an ad
        adView!!.loadAd()
        AdSettings.addTestDevice("c77ae079-ddd2-45a2-a632-47ab1cd7f060");




        val intent = Intent(this, ClipboardMonitorService::class.java)
        startService(intent)

        navView0!!.setNavigationItemSelectedListener { menuItem: MenuItem ->
            val id: Int = menuItem.getItemId()
            when (id) {

                R.id.bookmark -> {
                    val intent2: Intent = Intent(getApplicationContext(), HistoryActivity::class.java)
                    intent2.putExtra("name", "Favourites.db")
                    startActivity(intent2)
                    drawerLayout!!.closeDrawers()
                }
                R.id.privecy->{
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.websitepolicies.com/policies/view/y8d2ESJc"))
                    startActivity(browserIntent)
                }

                R.id.about->{
                    val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                    // ...Irrelevant code for customizing the buttons and title
                    // ...Irrelevant code for customizing the buttons and title
                    val inflater = this.layoutInflater
                    val dialogView: View = inflater.inflate(R.layout.dialog, null)
                    dialogBuilder.setView(dialogView)

                    val alertDialog = dialogBuilder.create()
                    alertDialog.show()
                    dialogView.close.setOnClickListener { alertDialog.dismiss() }
                }

            }
            false
        }

        textToTranslate.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        ButterKnife.bind(this)
        toolbar = findViewById(R.id.toolbar)

        val preferences = getSharedPreferences("name", Context.MODE_PRIVATE)
        lan1code = preferences.getString("lan1code", "en")
        lan2code = preferences.getString("lan2code", "bn")
        lan1text = preferences.getString("lan1text", "English")
        lan2text = preferences.getString("lan2text", "Bangla")

        lang1!!.text = lan1text
        indecator.visibility=View.GONE
        lang2!!.text = lan2text
        lang2.setOnClickListener {
            Log.i("123321","lang1 clicked")
            lan = false
            showPicker() }
        lang1.setOnClickListener {     lan = true
            showPicker() }
        clear.setOnClickListener {
            textToTranslate!!.setText("")
            translatedText!!.text = ""
        }
        progressDialog= ProgressDialog(this)

        bookmark.setOnClickListener {
            if ((textToTranslate!!.text.toString().trim { it <= ' ' } == "")) Toast.makeText(this, "Please Write Something", Toast.LENGTH_SHORT).show() else {
                val dataBaseHelper = DataBaseHelper(applicationContext,
                        "Favourites.db")
                val text = textToTranslate!!.text.toString().trim { it <= ' ' }
                val translation = translatedText!!.text.toString()
                val source = lan1code
                val target = lan2code
                val item = Word(text, translation, source, target)
                if (dataBaseHelper.isInDataBase(item)) {
                    dataBaseHelper.deleteWord(item)
                    isFavourite = false
                } else {
                    isFavourite = true
                    dataBaseHelper.insertWord(item)
                    bookmark!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_book_black_24dp2))
                }
                dataBaseHelper.close()
            }
        }
        share.setOnClickListener {
            if (textToTranslate.text.toString().isNotEmpty()) {

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, translatedText!!.text.toString())
                intent.putExtra(Intent.EXTRA_SUBJECT, "Title goes here")
                startActivity(Intent.createChooser(intent, "Share"))
            }
            else{
             Toast.makeText(this,"Nothing to share",Toast.LENGTH_SHORT).show()
            }
        }
        speak.setOnClickListener {
            if(textToTranslate.text.toString().isNotEmpty()){

                t1 = TextToSpeech(applicationContext, object : TextToSpeech.OnInitListener {
                    override fun onInit(status: Int) {
                        if (status != TextToSpeech.ERROR) {
                            val loc = Locale(lan2code, "")
                            when (t1!!.setLanguage(loc)) {
                                TextToSpeech.LANG_AVAILABLE -> Log.i("TAG", "LANG_AVAILABLE")
                                TextToSpeech.LANG_COUNTRY_AVAILABLE -> Log.i("TAG", "LANG_COUNTRY_AVAILABLE")
                                TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> Log.i("TAG", "LANG_COUNTRY_VAR_AVAILABLE")
                                TextToSpeech.LANG_MISSING_DATA -> Log.i("TAG", "LANG_MISSING_DATA")
                                TextToSpeech.LANG_NOT_SUPPORTED -> {
                                    Log.i("TAG", "LANG_NOT_SUPPORTED")
                                    Toast.makeText(this@OcrActivity, "Language not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                            Log.i("123321", translatedText!!.text.toString())
                            t1!!.speak(
                                    translatedText!!.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
                        } else Log.i("123321", "383")
                    }
                })
            }
            else{
                Toast.makeText(this,"Nothing to speak",Toast.LENGTH_SHORT).show()

            }
        }

        copy.setOnClickListener {


            if(textToTranslate.text.toString().isNotEmpty()){

                interstitialAd!!.loadAd()

                interstitialAd!!.setAdListener(object : InterstitialAdListener {
                    override fun onInterstitialDisplayed(ad: Ad) {
                        // Interstitial ad displayed callback
                        Log.e("123321", "Interstitial ad displayed.")
                    }

                    override fun onInterstitialDismissed(ad: Ad) {
                        // Interstitial dismissed callback
                        Log.e("123321", "Interstitial ad dismissed.")






                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("text", translatedText!!.text.toString())
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(applicationContext, "Successfully copied", Toast.LENGTH_SHORT).show()


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

                else{

                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("text", translatedText!!.text.toString())
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(applicationContext, "Successfully copied", Toast.LENGTH_SHORT).show()
                }}
            else{
                Toast.makeText(this,"Nothing to copy",Toast.LENGTH_SHORT).show()

            }
        }
        //textToTranslate = findViewById(R.id.textToTranslate);
        setSupportActionBar(toolbar)
        translatedText!!.movementMethod = ScrollingMovementMethod()

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white)
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()
        setArgs()
        textChangedListener()
        change!!.setOnClickListener { v: View? ->
            change!!.setRotation(change!!.getRotation() + 180)
            val l1: String? = lan1text
            val l2: String? = lan2text
            val lc1: String? = lan1code
            val lc2: String? = lan2code
            val t1: String = textToTranslate?.text.toString()
            val t2: String = translatedText.text.toString()
            lan1text = l2
            lan2text = l1
            lan1code = lc2
            lan2code = lc1
            textToTranslate!!.setText(t2)
            translatedText!!.setText(t1)
            lang1!!.setText(lan1text)
            lang2!!.setText(lan2text)

            // translatedText.setText("Translating");
            val editor: SharedPreferences.Editor = getSharedPreferences("name", Context.MODE_PRIVATE).edit()
            editor.putString("lan1text", lan1text)
            editor.putString("lan1code", lan1code)
            editor.putString("lan2text", lan2text)
            editor.putString("lan2code", lan2code)
            editor.apply()
            translate(textToTranslate!!.getText().toString().trim { it <= ' ' })
        }
     /*   rootView!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val heightDiff = rootView!!.rootView.height - rootView!!.height
                if (heightDiff > 500) {
                    group!!.visibility = View.GONE
                } else {
                    Log.e("123321", "keyboard closed")
                    group!!.visibility = View.VISIBLE
                }
            }
        })
  */
        textToTranslate.setText(ClipboardMonitorService.Text2)


    }


  /*  override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }*/

    fun setArgs() {
        val sharedPref = getSharedPreferences("default", Context.MODE_PRIVATE)
        val text = sharedPref.getString("textToTranslate", "")
        val translation = sharedPref.getString("translatedText", "")
        val selection1 = sharedPref.getInt("selection1", 0)
        val selection2 = sharedPref.getInt("selection2", 1)
        isFavourite = sharedPref.getBoolean("isFavourite", false)
        if (text != "") {
            noTranslate = true
            textToTranslate!!.setText(text)
            spinner1!!.setSelection(selection1)
            spinner2!!.setSelection(selection2)
            translatedText!!.text = translation
            if (isFavourite) {
                bookmark!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_book_black_24dp2))
            } else {
                bookmark!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_book_black_24dp))
            }
        }
    }

    fun textChangedListener() {

        // Translate the text after 500 milliseconds when user ends to typing
        textToTranslate?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                timer.cancel()
                timer = Timer()
                timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                // TODO: do what you need here (refresh list)
                                // you will probably need to use runOnUiThread(Runnable action) for some specific actions (e.g. manipulating views)
                              if(textToTranslate.text.isNotEmpty())  translate(textToTranslate!!.text.toString())
                            }
                        },
                        DELAY
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun translate(text: String) {
        runOnUiThread {
            // Stuff that updates the UI
            indecator.visibility=View.VISIBLE
            translatedText.visibility=View.GONE
        }

        if (noTranslate) {
            noTranslate = false
            return
        }


     object : AsyncTask<Void?, Void?, String?>() {
            @SuppressLint("StaticFieldLeak")
            override fun doInBackground(vararg params: Void?): String? {


                try {
                    val options: TranslateOptions = TranslateOptions.newBuilder()
                            .setApiKey(CLOUD_VISION_API_KEY)
                            .build()
                    val translate: Translate = options.getService()
                    val translation: Translation = translate.translate(text ,
                            Translate.TranslateOption.sourceLanguage(lan1code),
                            Translate.TranslateOption.targetLanguage(lan2code))

                    Log.i("123321","340:"+translation.translatedText)

                    return translation.translatedText
                } catch (e: Exception) {
                    return ""
                }
            }

         override fun onPostExecute(result: String?) {
             super.onPostExecute(result)
             Log.i("123321","347:"+result)
             translatedText!!.text =result
             indecator.visibility=View.GONE
             translatedText.visibility=View.VISIBLE
             checkIfInFavourites()
             addToHistory()


         }

        }.execute()

    }

    private fun showPicker() {
        val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        try {
            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
        }
        val builder = CountryPicker.Builder().with(this@OcrActivity)
                .listener(this)
        //        if (styleSwitch.isChecked()) {
//            builder.style(R.style.CountryPickerStyle);
//        }
        //builder.theme(themeSwitch.isChecked() ? CountryPicker.THEME_NEW : CountryPicker.THEME_OLD);
        builder.canSearch(true)
        builder.sortBy(sortBy)
        countryPicker = builder.build()
        countryPicker!!.showBottomSheet(this@OcrActivity)
    }

    override fun onSelectCountry(country: Country) {
        if (lan) {
            lan1text = country.name
            lang1!!.text = country.name
            lan1code = country.code
            val editor = getSharedPreferences("name", Context.MODE_PRIVATE).edit()
            editor.putString("lan1text", lan1text)
            editor.putString("lan1code", lan1code)
            editor.apply()
        } else {
            lan2text = country.name
            lang2!!.text = country.name
            lan2code = country.code
            val editor = getSharedPreferences("name", Context.MODE_PRIVATE).edit()
            editor.putString("lan2text", lan2text)
            editor.putString("lan2code", lan2code)
            editor.apply()
        }
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {}

    @OnClick(R.id.lang2)
    fun onLang2Clicked() {

    }

    @OnClick(R.id.lang1)
    fun onLang1Clicked() {
        lan = true
        showPicker()
    }


    /**
     * Receiving speech input
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
Log.i("123321","428:"+requestCode)
        if (requestCode == 1) {
            val isCamera: Boolean
            isCamera = if (data == null || data.data == null) {
                true
            } else {
                val action = data.action
                action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
            }
            val selectedImageUri: Uri?
            if (isCamera) {
                selectedImageUri = outputFileUri
                if (selectedImageUri == null) return
                nextStep(selectedImageUri.toString())
            } else {
                selectedImageUri = data!!.data
                if (selectedImageUri == null) return
                nextStep(selectedImageUri.toString())
            }
        }

        when (requestCode) {
            REQUEST_IMAGE-> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = data?.getParcelableExtra("path")
                    Log.i("123321", "428:"+uri)
                    uploadImage(uri)

                }
            }
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    textToTranslate!!.setText(result[0])
                }
            }
        }
    }
    @OnClick(R.id.clear)
    fun onClearClicked() {
        textToTranslate!!.setText("")
        translatedText!!.text = ""
    }

    @OnClick(R.id.copy)
    fun onCopyClicked() {

        if(textToTranslate.text.toString().isNotEmpty()){
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("text", translatedText!!.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(applicationContext, "Successfully copied", Toast.LENGTH_SHORT).show()
        }
    }


    @OnClick(R.id.speak)
    fun onSpeakClicked() {
        if(textToTranslate.text.toString().isNotEmpty()){
            t1 = TextToSpeech(applicationContext, object : TextToSpeech.OnInitListener {
                override fun onInit(status: Int) {
                    if (status != TextToSpeech.ERROR) {
                        val loc = Locale(lan2code, "")
                        when (t1!!.setLanguage(loc)) {
                            TextToSpeech.LANG_AVAILABLE -> Log.i("TAG", "LANG_AVAILABLE")
                            TextToSpeech.LANG_COUNTRY_AVAILABLE -> Log.i("TAG", "LANG_COUNTRY_AVAILABLE")
                            TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> Log.i("TAG", "LANG_COUNTRY_VAR_AVAILABLE")
                            TextToSpeech.LANG_MISSING_DATA -> Log.i("TAG", "LANG_MISSING_DATA")
                            TextToSpeech.LANG_NOT_SUPPORTED -> {
                                Log.i("TAG", "LANG_NOT_SUPPORTED")
                                Toast.makeText(applicationContext, "Language not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                        Log.i("123321", translatedText!!.text.toString())
                        t1!!.speak(
                                translatedText!!.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
                    } else Log.i("123321", "383")
                }
            })
        }

    }
    fun addToHistory() {
        val text = textToTranslate!!.text.toString().trim { it <= ' ' }
        if (text != "") {
            val dataBaseHelper = DataBaseHelper(applicationContext, "History.db")
            dataBaseHelper.insertWord(Word(textToTranslate!!.text.toString().trim { it <= ' ' },
                    translatedText!!.text.toString(), lan1code,
                    lan2code))
            dataBaseHelper.close()
        }
    }

    fun checkIfInFavourites() {
        val text = textToTranslate!!.text.toString()
        if (text != "") {
            val dataBaseHelper = DataBaseHelper(applicationContext, "Favourites.db")
            if (dataBaseHelper.isInDataBase(Word(text, translatedText!!.text.toString(),
                            lan1code, lan2code))) {
                bookmark!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_book_black_24dp2))
                isFavourite = true
            } else {
                bookmark!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_book_black_24dp))
                isFavourite = false
            }
            dataBaseHelper.close()
        } else {
            isFavourite = false
        }
    }

    @OnClick(R.id.share)
    fun onShareClicked() {
        if(textToTranslate.text.toString().isNotEmpty()){
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, translatedText!!.text.toString())
            intent.putExtra(Intent.EXTRA_SUBJECT, "Title goes here")
            startActivity(Intent.createChooser(intent, "Share"))
        }}

    @OnClick(R.id.bookmark)
    fun onBookmarkClicked() {
        if ((textToTranslate!!.text.toString().trim { it <= ' ' } == "")) Toast.makeText(this, "Please Write Something", Toast.LENGTH_SHORT).show() else {
            val dataBaseHelper = DataBaseHelper(applicationContext,
                    "Favourites.db")
            val text = textToTranslate!!.text.toString().trim { it <= ' ' }
            val translation = translatedText!!.text.toString()
            val source = lan1code
            val target = lan2code
            val item = Word(text, translation, source, target)
            if (dataBaseHelper.isInDataBase(item)) {
                dataBaseHelper.deleteWord(item)
                isFavourite = false
            } else {
                isFavourite = true
                dataBaseHelper.insertWord(item)
                bookmark!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_book_black_24dp2))

            }
            dataBaseHelper.close()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)


        // Checks whether a hardware keyboard is available
    /*    if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            group!!.visibility = View.VISIBLE
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            group!!.visibility = View.GONE
        }*/
    }

    private fun launchCameraIntent() {
        val intent = Intent(this@OcrActivity, ImagePickerActivity::class.java)
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE)

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    private fun launchGalleryIntent() {
        val intent = Intent(this@OcrActivity, ImagePickerActivity::class.java)
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE)

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)
        startActivityForResult(intent, REQUEST_IMAGE)
    }
    private fun showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, object : PickerOptionListener {
            override fun onTakeCameraSelected() {
                launchCameraIntent()
            }

            override fun onChooseGallerySelected() {
                launchGalleryIntent()
            }
        })
    }


    //*****************OCR*******************************
    open fun uploadImage(uri: Uri?): Unit {
        progressDialog?.setTitle("Uploading Image")
        progressDialog?.setCancelable(false)
        progressDialog?.show()
        if (uri != null) {
            try {
                val ANDROID_CERT_HEADER = "X-Android-Cert"
                val ANDROID_PACKAGE_HEADER = "X-Android-Package"
                // scale the image to save on bandwidth
                val bitmap: Bitmap = scaleBitmapDown(
                        MediaStore.Images.Media.getBitmap(contentResolver, uri),
                        1200 // img hq
                )
                callCloudVision(bitmap)

            } catch (e: IOException) {
                Log.d("123321", "Image picking failed because " + e.message)

            }
        } else {
            Log.d("123321", "Image picker gave us a null image.")

        }
    }


    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        var resizedWidth = maxDimension
        var resizedHeight = maxDimension
        if (originalHeight > originalWidth) {
            resizedWidth = (resizedHeight * originalWidth.toFloat() / originalHeight.toFloat()).toInt()
        } else if (originalWidth > originalHeight) {
            resizedHeight = (resizedWidth * originalHeight.toFloat() / originalWidth.toFloat()).toInt()
        }

        // else --> originalHeight == originalWidth, so Width == Height == maxDimension
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false) // resized img
    }

    inner class LableDetectionTask(activity: OcrActivity, annotate: Annotate) : AsyncTask<Any?, Void?, String>() {
        private val mActivityWeakReference: WeakReference<OcrActivity>
        private val mRequest: Annotate

        // GoogleAPI request in background
        @SuppressLint("WrongThread")
        override fun doInBackground(vararg params: Any?): String? {
            try {

                Log.d("123321", "created Cloud Vision request object, sending request")
                val response = mRequest.execute()
                val responses: List<AnnotateImageResponse> = response.getResponses()
              lateinit var category: ArrayList<String>
                category= ArrayList()
                for (res in responses) {

                    // For full list of available annotations, see http://g.co/cloud/vision/docs
                    for (annotation in res.textAnnotations) {
                        Log.i("123321","674:"+"Text: %s\n"+ annotation.description)
                        category.add(annotation.description)
                       // out.printf("Position : %s\n", annotation.boundingPoly)
                    }
                }
                Log.i("123321", "322" + convertResponseToString0(response))

                Log.i("123321","682:"+category[0])
                progressDialog!!.dismiss()
                runOnUiThread {
                    // Stuff that updates the UI
                    var s:String=category[0].replace("\r","\n")
                    s=s.replace("\r\n","\n")
                    s=s.replace("\\n","\n")
                textToTranslate.setText(s)
            }


            } catch (e: GoogleJsonResponseException) {
                Log.d("123321", "failed to make API request because " + e.content)
            } catch (e: IOException) {
                Log.d("123321", "failed to make API request because of other IOException " +
                        e.message)
            }
            return "Cloud Vision API request failed. Check logs for details."
        }

        fun convertResponseToString0(response: BatchAnnotateImagesResponse): String? {
            Log.i("123321","685"+response)
            var message = ""
            val labels = response.responses[0].textAnnotations
            for (label in labels!!) {
                message += if (labels != null) {
                    println(label.description)
                    """
     ${String.format(Locale.US, "%s", label.description)}
     
     """.trimIndent()
                } else {
                    "nothing"
                }
            }

            return message
        }


        // POST req
        override fun onPostExecute(result: String) {
            val activity = mActivityWeakReference.get()
            if (activity != null && !activity.isFinishing) {
               Log.i("123321","660:"+result)
                progressDialog?.dismiss()


            }
        }

        // GoogleAPI request prepare
        init {
            mActivityWeakReference = WeakReference(activity)
            mRequest = annotate
        }
    }

    private fun callCloudVision(bitmap: Bitmap) {
        // switch text to "Uploading..."
        // GoogleAPI - do the work in an async task, cause we need to use the network anyway
        try { // autogenerated try-catch
            progressDialog!!.setTitle("Extracting Text")
            prepareAnnotationRequest(bitmap)?.let { LableDetectionTask(this, it) }?.execute()
        } catch (e: IOException) {
            Log.d("123321", "failed to make API request because of IOException " +
                    e.message)

            Toast.makeText(applicationContext,"Somethings wants wrong"+e.message,Toast.LENGTH_SHORT).show()

        }
    }


    @Throws(IOException::class)
    private fun prepareAnnotationRequest(bitmap: Bitmap): Annotate? {
        val httpTransport = AndroidHttp.newCompatibleTransport()
        val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
        val requestInitializer: VisionRequestInitializer = object : VisionRequestInitializer(CLOUD_VISION_API_KEY) {
            /**
             * We override this so we can inject important identifying fields into the HTTP
             * headers. This enables use of a restricted cloud platform API key.
             */
            @Throws(IOException::class)
            override fun initializeVisionRequest(visionRequest: VisionRequest<*>) {
                super.initializeVisionRequest(visionRequest)
                val packageName = packageName
                visionRequest.requestHeaders[ANDROID_PACKAGE_HEADER] = packageName
                val sig: String? = PackageManagerUtils.getSignature(packageManager, packageName)
                visionRequest.requestHeaders[ANDROID_CERT_HEADER] = sig
            }
        }
        val builder = Vision.Builder(httpTransport, jsonFactory, null)
        builder.setVisionRequestInitializer(requestInitializer)
        val vision = builder.build()
        val batchAnnotateImagesRequest = BatchAnnotateImagesRequest()
        batchAnnotateImagesRequest.requests = object : ArrayList<AnnotateImageRequest?>() {
            init {
                val annotateImageRequest = AnnotateImageRequest()

                // Add the image
                val base64EncodedImage = Image()
                // Convert the bitmap to a JPEG
                // Just in case it's a format that Android understands but Cloud Vision
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
                val imageBytes = byteArrayOutputStream.toByteArray()

                // Base64 encode the JPEG
                base64EncodedImage.encodeContent(imageBytes)
                annotateImageRequest.image = base64EncodedImage

                // add the features we want
                annotateImageRequest.features = object : ArrayList<Feature?>() {
                    init {
                        val textDetection = Feature()
                        textDetection.type = "TEXT_DETECTION"
                        textDetection.maxResults = 1200
                        add(textDetection)
                    }
                }

                // Add the list of one thing to the request
                add(annotateImageRequest)
            }
        }
        val annotateRequest = vision.images().annotate(batchAnnotateImagesRequest)
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.disableGZipContent = true
        Log.d("123321", "created Cloud Vision request object, sending request")
        return annotateRequest
    }

    // --- end of request

    //*************END OF OCR****************************


    private fun selectImage() {
        val fname = "img_" + System.currentTimeMillis() + ".jpg"
        val sdImageMainDirectory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fname)
        outputFileUri = Uri.fromFile(sdImageMainDirectory)

        // Camera.
        val cameraIntents: MutableList<Intent> = java.util.ArrayList()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = packageManager
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            cameraIntents.add(intent)
        }
        // Filesystem.
        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_PICK

        // Chooser of filesystem options.
        val chooserIntent = Intent.createChooser(galleryIntent, "Select Source")

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())
        startActivityForResult(chooserIntent, 1)
    }
    open fun nextStep(file: String) {
        val intent = Intent(this@OcrActivity, CropAndRotate::class.java)
        intent.putExtra(EXTRA_MESSAGE, file)
        startActivity(intent)
    }


    override fun onBackPressed() {
        ClipboardMonitorService.Text=""
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
    override fun onDestroy() {
// Don't forget to shutdown tts!
        if (t1 != null) {
            t1!!.stop()
            t1!!.shutdown()
        }
        super.onDestroy()
    }
    override fun onPause() {
// Don't forget to shutdown tts!
        if (t1 != null) {
            t1!!.stop()
            t1!!.shutdown()
        }
        super.onPause()
    }

}