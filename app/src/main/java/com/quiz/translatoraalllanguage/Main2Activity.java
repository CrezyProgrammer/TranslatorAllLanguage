package com.quiz.translatoraalllanguage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.translate, R.id.dictionary})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.translate:
                startActivity(new Intent(this, MainActivity.class));
                
                break;
            case R.id.dictionary:
                startActivity(new Intent(this, DictionaryActivity.class));
                
                break;
        }
    }
    @Override
    public void onBackPressed() {
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit");
            builder.setMessage("Are you Sure ?");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                    System.exit(0);

                }
            });
            builder.setNegativeButton("cancel",null );
            builder.show();
        }
    }

}
