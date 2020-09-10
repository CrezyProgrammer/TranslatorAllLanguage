package com.allword.translation

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequest
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.*
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

open class CropAndRotate : AppCompatActivity(), View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private var toolbar: Toolbar? = null
    var Text2 = ""

    private val CLOUD_VISION_API_KEY: String = "AIzaSyC-NQxsdFgXrq_culH1dTykIbpAgGl5SRg"
    val EXTRA_MESSAGE = "com.ltapps.textscanner.message"
    private val ANDROID_CERT_HEADER = "X-Android-Cert"
    private val ANDROID_PACKAGE_HEADER = "X-Android-Package"
    var progressDialog: ProgressDialog? =null
    private var mFab: FloatingActionButton? = null
    private var message: String? = null
    var cropImageView: CropImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crop_and_rotate)
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        ViewCompat.setElevation(toolbar!!, 10f)
        toolbar!!.setOnMenuItemClickListener(this)
        val intent = intent
        message = intent.getStringExtra(EXTRA_MESSAGE)
        cropImageView = findViewById<View>(R.id.cropImageView) as CropImageView
        cropImageView!!.setImageUriAsync(Uri.parse(message))
        mFab = findViewById<View>(R.id.nextStep) as FloatingActionButton
        mFab!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.nextStep) {
            cropImageView!!.setOnCropImageCompleteListener { view, result ->
                progressDialog?.setTitle("Uploading Image")
                progressDialog?.setCancelable(false)
                progressDialog?.show()
                callCloudVision(result.bitmap)
            }
            cropImageView!!.getCroppedImageAsync()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_rotate, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.rotate_left -> cropImageView!!.rotateImage(-90)
            R.id.rotate_right -> cropImageView!!.rotateImage(90)
        }
        return false
    }


    open fun uploadImage(uri: Uri?): Unit {
        Log.i("123321","uploading Image")
        Log.i("123321","image uri:"+uri)

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
                Log.i("123321", "Image picking failed because " + e.message)

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

    inner class LableDetectionTask(activity: CropAndRotate, annotate: Vision.Images.Annotate) : AsyncTask<Any?, Void?, String>() {
        private val mActivityWeakReference: WeakReference<CropAndRotate>
        private val mRequest: Vision.Images.Annotate

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
                    ClipboardMonitorService.Text2=s
                    val intent=Intent(applicationContext,OcrActivity::class.java)

                         intent.putExtra("text",s)
                    startActivity(intent)
                }


            } catch (e: GoogleJsonResponseException) {
                Log.i("123321", "failed to make API request because " + e.content)
            } catch (e: IOException) {
                Log.i("123321", "failed to make API request because of other IOException " +
                        e.message)
            }
            return "Cloud Vision API request failed. Check logs for details."
        }

        private fun convertResponseToString0(response: BatchAnnotateImagesResponse): String? {
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
        Log.i("123321","calling vision api")
        // switch text to "Uploading..."
        // GoogleAPI - do the work in an async task, cause we need to use the network anyway
        try { // autogenerated try-catch
            progressDialog= ProgressDialog(this)
            progressDialog?.setTitle("Uploading Image")
            progressDialog?.setCancelable(false)
            progressDialog?.show()
            progressDialog?.setTitle("Extracting Text")
            prepareAnnotationRequest(bitmap)?.let { LableDetectionTask(this
                    , it) }?.execute()
        } catch (e: IOException) {
            Log.d("123321", "failed to make API request because of IOException " +
                    e.message)

            Toast.makeText(applicationContext,"Somethings wants wrong"+e.message, Toast.LENGTH_SHORT).show()

        }
    }


    @Throws(IOException::class)
    private fun prepareAnnotationRequest(bitmap: Bitmap): Vision.Images.Annotate? {
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

}