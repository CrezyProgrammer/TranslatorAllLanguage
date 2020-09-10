package com.allword.translation

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.facebook.ads.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_scan.*
import java.io.File

class ScanActivity : AppCompatActivity() {
    val EXTRA_MESSAGE = "com.ltapps.textscanner.message"

    private var adView: AdView? = null
    private var interstitialAd: InterstitialAd? = null
    private var outputFileUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        adView = AdView(this, "320675462379332_321536232293255", AdSize.BANNER_HEIGHT_90)
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






        image.setOnClickListener {


            interstitialAd!!.setAdListener(object : InterstitialAdListener {
                override fun onInterstitialDisplayed(ad: Ad) {
                    // Interstitial ad displayed callback
                    Log.e("123321", "Interstitial ad displayed.")
                }

                override fun onInterstitialDismissed(ad: Ad) {
                    // Interstitial dismissed callback
                    Log.e("123321", "Interstitial ad dismissed.")



interstitialAd!!.loadAd()

                    Dexter.withActivity(this@ScanActivity)
                            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(object : MultiplePermissionsListener {
                                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                    if (report.areAllPermissionsGranted()) {
                                        selectImage()
                                    } else {
                                        // TODO - handle permission denied case
                                    }
                                }

                                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken) {
                                    token.continuePermissionRequest()
                                }
                            }).check()


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

{            Dexter.withActivity(this)
                    .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            if (report.areAllPermissionsGranted()) {
                                selectImage()
                            } else {
                                // TODO - handle permission denied case
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken) {
                            token.continuePermissionRequest()
                        }
                    }).check()
}        }
        history.setOnClickListener {
            interstitialAd!!.setAdListener(object : InterstitialAdListener {
                override fun onInterstitialDisplayed(ad: Ad) {
                    // Interstitial ad displayed callback
                    Log.e("123321", "Interstitial ad displayed.")
                }

                override fun onInterstitialDismissed(ad: Ad) {
                    // Interstitial dismissed callback
                    Log.e("123321", "Interstitial ad dismissed.")




interstitialAd!!.loadAd()
                    val intent: Intent = Intent(applicationContext, HistoryActivity::class.java)
                    intent.putExtra("name", "History.db")
                    startActivity(intent)


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
{
            val intent: Intent = Intent(applicationContext, HistoryActivity::class.java)
            intent.putExtra("name", "History.db")
            startActivity(intent)
}

        }
    }
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
        val intent = Intent(this@ScanActivity, CropAndRotate::class.java)
        intent.putExtra(EXTRA_MESSAGE, file)
        Log.i("123321","901:"+file)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("123321","428:"+requestCode)
        if (resultCode == Activity.RESULT_OK) {
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




    }

}
    }