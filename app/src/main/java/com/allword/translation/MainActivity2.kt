package com.allword.translation

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.facebook.ads.*
import kotlinx.android.synthetic.main.activity_main4.*


class MainActivity2 : AppCompatActivity() {
    private var adView: AdView? = null
    private var interstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        adView = AdView(this, "320675462379332_321536232293255", AdSize.BANNER_HEIGHT_90)
        AdSettings.addTestDevice("c77ae079-ddd2-45a2-a632-47ab1cd7f060")
        //

        if (BuildConfig.DEBUG) {
            AdSettings.setTestMode(true)

        }

        tran2.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://freedownloadimage.com"))
            startActivity(browserIntent)
        }
        interstitialAd = InterstitialAd(this, "320675462379332_321528522294026")

        interstitialAd!!.loadAd()

        val adContainer: LinearLayout = findViewById<View>(R.id.banner_container) as LinearLayout

        adContainer.addView(adView)

        // Request an ad

        // Request an ad
        adView!!.loadAd()
        AdSettings.addTestDevice("c77ae079-ddd2-45a2-a632-47ab1cd7f060");
        setSupportActionBar(main_tool)
        supportActionBar?.title = ""
        tran.setOnClickListener {


                    startActivity(Intent(this, MainActivity::class.java))





    }
        }
    override fun onBackPressed() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit")
        builder.setMessage("Are you Sure ?")
        builder.setPositiveButton("ok") { dialog: DialogInterface?, which: Int ->
            finishAffinity()
            System.exit(0)
        }
        builder.setNegativeButton("cancel", null)
        builder.show()
    }

    
}