//package com.eid.codeprobro.kiu_tas;
//
//import android.os.Bundle;
//import android.view.View;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//public class database_link extends AppCompatActivity implements View.OnClickListener{
//WebView web_vew;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_database_link);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    web_vew=findViewById(R.id.web_vew);
//    web_vew.loadUrl("https://console.firebase.google.com/project/myloginapp-d44de/database/myloginapp-d44de-default-rtdb/data/~2FOX");
//    web_vew.setWebViewClient(new WebViewClient());
//    web_vew.getSettings().setJavaScriptEnabled(true);
//        WebSettings webSettings = web_vew.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setDomStorageEnabled(true); // ফায়ারবেস লোকাল ক্যাশিংয়ের জন্য আবশ্যক
//        webSettings.setDatabaseEnabled(true);
//        webSettings.setBuiltInZoomControls(true); // শিক্ষক যেন জুম ইন/আউট করে নোড দেখতে পারেন
//        webSettings.setDisplayZoomControls(false);
//        web_vew.loadUrl("https://console.firebase.google.com/project/myloginapp-d44de/database/myloginapp-d44de-default-rtdb/data/~2FOX");
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        if(web_vew.canGoBack()){
//            web_vew.goBack();
//        }
//        else{
//            super.onBackPressed();        }
//    }
//
//    @Override
//    public void onClick(View v) {
//
//    }
//}




package com.eid.codeprobro.kiu_tas;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class _5_database_link extends AppCompatActivity {
    WebView web_vew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_database_link);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        web_vew = findViewById(R.id.web_vew);


        web_vew.setWebViewClient(new WebViewClient());


        WebSettings webSettings = web_vew.getSettings();
        webSettings.setJavaScriptEnabled(true); //js enable

//        webSettings.setDomStorageEnabled(true); //locao cassing
//        webSettings.setDatabaseEnabled(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);

        web_vew.loadUrl("https://console.firebase.google.com/project/myloginapp-d44de/database/myloginapp-d44de-default-rtdb/data/~2FOX");
    }

    @Override
    public void onBackPressed() {
//get back to the back page
        if (web_vew.canGoBack()) {
            web_vew.goBack();
        } else {
            super.onBackPressed();
        }
    }
}