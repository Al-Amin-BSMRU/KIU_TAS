package com.eid.codeprobro.kiu_tas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // সেশন যেহেতু টিচারের জন্য দরকার নেই, তাই ভ্যারিয়েবল থেকে বাদ দেওয়া হয়েছে বা ফাঁকা রাখা যাবে
    EditText University_Name, Name, id, Email, Password, Depertment, punchcard_no;
    Button Create;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        University_Name = findViewById(R.id.University_Name);
        Name = findViewById(R.id.Name);
        id = findViewById(R.id.id);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        Depertment = findViewById(R.id.Depertment);
        punchcard_no = findViewById(R.id.punchcard_no);
        Create = findViewById(R.id.Create);

        Create.setOnClickListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, windowInsets) -> {
            Insets imeInsets = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            Insets systemBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, imeInsets.bottom);
            return windowInsets;
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Create) {

            String uni_name = this.University_Name.getText().toString().trim().toUpperCase();
            String rawname = this.Name.getText().toString().trim();
            String rawidd = this.id.getText().toString().trim();
            String email = this.Email.getText().toString().trim();
            String password = this.Password.getText().toString().trim();
            String depertment = this.Depertment.getText().toString().trim().toUpperCase();
            String punch = this.punchcard_no.getText().toString().trim().toUpperCase(); // কার্ড আইডি ক্যাপিটাল লেটার করা হলো

            // ভ্যালিডেশন চেক
            if (uni_name.isEmpty() || rawname.isEmpty() || rawidd.isEmpty() || punch.isEmpty() || password.isEmpty() || depertment.isEmpty()) {
                Toast.makeText(this, "Please fill in all information", Toast.LENGTH_SHORT).show();
                return;
            }
            if (rawidd.length() < 12) {
                Toast.makeText(this, "Wrong ID! ID must be 12 digits long", Toast.LENGTH_LONG).show();
                return;
            }

            // স্পেশাল ক্যারেক্টার চেক (ফায়ারবেস নোড সুরক্ষার জন্য)
            if (uni_name.contains(".") || uni_name.contains("#") || uni_name.contains("$") ||
                    depertment.contains(".") || depertment.contains("#") || depertment.contains("$") ||
                    punch.contains(".") || punch.contains("#") || punch.contains("$")) {
                Toast.makeText(this, "Special characters (., #, $) are not allowed!", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                // নামের প্রথম অক্ষর বড় হাতের এবং বাকি ছোট হাতের করা
                String name = rawname.substring(0, 1).toUpperCase() + rawname.substring(1).toLowerCase();
                String idd = rawidd.substring(0, 6);
                String roll = rawidd.substring(rawidd.length() - 2);

                // আপনার অরিজিনাল লজিক অনুযায়ী নাম কম্বিনেশন তৈরি
                String nam_ses_rol = name + "_" + idd + "__" + roll;

                // login মডেলে ডাটা পাস করা (নিশ্চিত করুন login ক্লাসে এই ৫টি প্যারামিটার আছে)
                login ln = new login(nam_ses_rol, punch, email, password, rawidd);

                db = FirebaseDatabase.getInstance().getReference(uni_name);

                // টিচার নোডের অধীনে ডেটা সেভ করা হচ্ছে
                db.child(depertment).child("teacher").child(punch).setValue(ln).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // আরডুইনোর দ্রুত চেক করার জন্য কার্ড ম্যাপিং নোডেও এন্ট্রি দেওয়া হলো
                        db.child("Card_Mappings").child(punch).setValue(depertment);

                        Toast.makeText(MainActivity.this, "Registration Success!", Toast.LENGTH_SHORT).show();

                        // পরবর্তী পেজে ডেটা পাঠানো (সেশন ফাঁকা পাঠানো হলো যেহেতু টিচারের সেশন হয় না)
                        Intent in = new Intent(MainActivity.this, second_page.class);
                        in.putExtra("name", name);
                        in.putExtra("id", rawidd);
                        in.putExtra("email", email);
                        in.putExtra("password", password);
                        in.putExtra("depertment", depertment);
                        in.putExtra("session", "");
                        in.putExtra("punch", punch);
                        in.putExtra("uni_name", uni_name);

                        startActivity(in);
                        finish(); // কারেন্ট অ্যাক্টিভিটি ক্লোজ করা ভালো
                    } else {
                        Toast.makeText(MainActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Toast.makeText(this, "An error occurred! Try again.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}