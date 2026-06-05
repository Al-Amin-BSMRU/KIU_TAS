package com.eid.codeprobro.kiu_tas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class second_page extends AppCompatActivity implements View.OnClickListener {

    TextView University_Name, Name, id, Email, Password, Depertment, punchcard_no;
    Button register, schadule;
    String uni_name, name, idi, email, password, depertment, punchid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.second_page);

        // ভিউগুলো ফাইন্ড করা
        University_Name = findViewById(R.id.University_Name);
        Name = findViewById(R.id.Name);
        id = findViewById(R.id.id);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        Depertment = findViewById(R.id.Depertment);
        punchcard_no = findViewById(R.id.punchcard_no);
        register = findViewById(R.id.register);
        schadule = findViewById(R.id.schadule);

        // লিসেনার সেট করা
        register.setOnClickListener(this);
        schadule.setOnClickListener(this);

        // প্রথম পেজ থেকে পাঠানো ডেটা রিসিভ করা
        Intent in = getIntent();
        if (in != null) {
            uni_name = in.getStringExtra("uni_name");
            name = in.getStringExtra("name");
            idi = in.getStringExtra("id");
            email = in.getStringExtra("email");
            password = in.getStringExtra("password");
            depertment = in.getStringExtra("depertment");
            punchid = in.getStringExtra("punch");

            // টেক্সটভিউতে ডাটা সেট করা
            University_Name.setText(uni_name);
            punchcard_no.setText(punchid);
            Name.setText(name);
            id.setText(idi);
            Email.setText(email);
            Password.setText(password);
            Depertment.setText(depertment);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            // নতুন আরেকটা MainActivity খোলার বদলে আগের পেজে ব্যাক করাই নিয়ম,
            // এতে মেমোরি বাঁচে এবং স্ট্যাক ওভারফ্লো হয় না।
            Intent in = new Intent(second_page.this, MainActivity.class);
            startActivity(in);
            finish(); // কারেন্ট পেজটি বন্ধ করে দেওয়া হলো
        }

        if (v.getId() == R.id.schadule) {
            Intent in = new Intent(second_page.this, schadule_dashboard.class);
            in.putExtra("uni_name", uni_name);
            in.putExtra("name", name);
            in.putExtra("id", idi);
            in.putExtra("depertment", depertment);
            in.putExtra("password", password);
            // সেফটি ফিক্স: টিচারের পরবর্তী ড্যাশবোর্ডের জন্য punch_id ও পাস করে রাখা ভালো
            in.putExtra("punch", punchid);
            startActivity(in);
        }
    }
}