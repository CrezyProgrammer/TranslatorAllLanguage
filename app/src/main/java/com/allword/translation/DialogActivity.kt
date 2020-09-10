package com.allword.translation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_dailog.*

class DialogActivity : Activity() {
    val text = "123321"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dailog)
        Log.i("123321","12:Intent Data :"+intent.getStringExtra("text"))
        close.setOnClickListener { finish() }
        translate.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java)
                    .putExtra("text","test")) }
    }
}