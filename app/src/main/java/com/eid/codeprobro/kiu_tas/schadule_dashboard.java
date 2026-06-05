//schadule_dashboard.java
package com.eid.codeprobro.kiu_tas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class schadule_dashboard extends AppCompatActivity implements View.OnClickListener {

    EditText Course_Name, Course_Code, Total_Student, session;
    Button starts;
    DatabaseReference mDatabase;
    String uni_name, name, idi, depertment;

    @Override
    protected void onCreate(Bundle Bundle) {
        super.onCreate(Bundle);
        EdgeToEdge.enable(this);
        setContentView(R.layout.schadule_dashboard);

        Course_Name = findViewById(R.id.Course_Name);
        Course_Code = findViewById(R.id.Course_Code);
        Total_Student = findViewById(R.id.Total_Student);
        session = findViewById(R.id.session);
        starts = findViewById(R.id.starts);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent in = getIntent();
        if (in != null) {
            uni_name = in.getStringExtra("uni_name");
            name = in.getStringExtra("name");
            idi = in.getStringExtra("id");
            depertment = in.getStringExtra("depertment"); // টিচারের ডিপার্টমেন্ট (যেমন: CSE)
        }
        starts.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.starts) {
            String cName = Course_Name.getText().toString().trim();

            // 🛠️ ফিক্সড: স্পেস থাকলে তা আন্ডারস্কোর (_) দিয়ে পরিবর্তন করবে
            String cCode = Course_Code.getText().toString().trim().toUpperCase().replace(" ", "_");

            String sessInput = session.getText().toString().trim();
            String totalStd = Total_Student.getText().toString().trim();

            // সব ইনপুট দেওয়া হয়েছে কিনা ভ্যালিডেশন চেক
            if (cName.isEmpty() || cCode.isEmpty() || sessInput.isEmpty() || totalStd.isEmpty()) {
                Toast.makeText(this, "Please fill all info to start class!", Toast.LENGTH_SHORT).show();
                return;
            }

            starts.setText("Initializing System...");
            starts.setEnabled(false);

            // 🚀 আরডুইনোকে সিগন্যাল দেওয়া: গ্লোবাল কনফিগারেশনে নতুন কোর্স কোড রাইট করা (আন্ডারস্কোরসহ)
            String activeUni = (uni_name != null && !uni_name.isEmpty()) ? uni_name : "OX";
            mDatabase.child(activeUni).child("Current_Class_Config").child("Course_Code").setValue(cCode);

            // কোর্স স্ট্যাটাস বা ইনফো ব্যাকএন্ডে সেভ করে রাখার জন্য
            mDatabase.child(activeUni).child(depertment).child("courses").child(cCode).child("total_students").setValue(totalStd);

            fetchFirebaseServerDate(cCode); // ফিক্সড: আন্ডারস্কোরসহ কোডটি মেথডে পাঠানো হলো
        }
    }
//local data
//    private void fetchFirebaseServerDate(final String optimizedCourseCode) {
//        String activeUni = (uni_name != null && !uni_name.isEmpty()) ? uni_name : "OX";
//
//        // 🎯 [ধাপ ১]: সরাসরি ফোনের লোকাল ডিভাইস থেকে বর্তমান ডেট এবং রিয়েল টাইম নেওয়া হচ্ছে
//        Date deviceCurrentDate = new Date(); // ফোনের বর্তমান ডেট-টাইম অবজেক্ট
//
//        // ফায়ারবেস এবং পরের স্ক্রিনের জন্য ডেট ফরম্যাট (yyyy-MM-dd)
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        String secureRealTimeDate = dateFormat.format(deviceCurrentDate);
//
//        // আরডুইনো বা হার্ডওয়্যারের ট্র্যাকিংয়ের জন্য ফোনের কারেন্ট টাইম ফরম্যাট (HH:mm:ss)
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//        String deviceTimeStr = timeFormat.format(deviceCurrentDate);
//
//        // 🎯 [ধাপ ২]: ফোনের ডিভাইস টাইমস্ট্যাম্প (Milliseconds) সরাসরি সার্ভারে আপলোড করা হচ্ছে
//        long deviceTimestamp = deviceCurrentDate.getTime();
//        DatabaseReference timeRef = mDatabase.child(activeUni).child("Current_Class_Config").child("server_time_trigger");
//
//        // সার্ভার ভ্যালুর বদলে ফোনের নিজস্ব টাইমস্ট্যাম্প পুশ করে দেওয়া হলো
//        timeRef.setValue(deviceTimestamp);
//
//        // 🎯 [ধাপ ৩]: আরডুইনোর সিঙ্ক করার জন্য কারেন্ট ডেট ও টাইম আলাদা নোডেও আপলোড করে দেওয়া হলো
//        DatabaseReference configRef = mDatabase.child(activeUni).child("Current_Class_Config");
//        configRef.child("Date").setValue(secureRealTimeDate);
//        configRef.child("Start_Time").setValue(deviceTimeStr);
//
//        // UI আপডেট
//        starts.setText("START");
//        starts.setEnabled(true);
//
//        // 🎯 [ধাপ ৪]: পরবর্তী হাজিরা স্ক্রিনে (student_attandance) ডেটা পাঠানো
//        Intent in = new Intent(schadule_dashboard.this, student_attandance.class);
//        in.putExtra("SERVER_DATE_KEY", secureRealTimeDate); // ফোনের লোকাল ডেট পাস হচ্ছে
//        in.putExtra("COURSE_CODE_KEY", optimizedCourseCode);
//
//        // 🛠️ সেশন প্রসেসিং লজিক (আগের মতোই ফিক্সড থাকছে)
//        String sessionInput = session.getText().toString().trim();
//        if (sessionInput.length() >= 4) {
//            String firstFourDigits = sessionInput.substring(0, 4);
//            sessionInput = firstFourDigits.substring(2, 4);
//        } else if (sessionInput.length() == 2) {
//            // যা আছে তাই থাকবে
//        } else {
//            sessionInput = "21"; // সেফটি ডিফল্ট
//        }
//
//        // কারেন্ট একটিভ সেশনটি গ্লোবাল নোডে পুশ
//        configRef.child("Class_Session").setValue(sessionInput);
//
//        in.putExtra("SESSION_KEY", sessionInput);
//        in.putExtra("depertment", depertment);
//        in.putExtra("uni_name", activeUni);
//
//        startActivity(in);
//        finish(); // এই স্ক্রিনটি বন্ধ করে দেওয়া হলো
//    }

//    real data

    private void fetchFirebaseServerDate(final String optimizedCourseCode) {
        String activeUni = (uni_name != null && !uni_name.isEmpty()) ? uni_name : "OX";
        final DatabaseReference timeRef = mDatabase.child(activeUni).child("Current_Class_Config").child("server_time_trigger");
        timeRef.setValue(ServerValue.TIMESTAMP);

        timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long timestamp = (Long) dataSnapshot.getValue();
                    if (timestamp != null) {
                        Date date = new Date(timestamp);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String secureRealTimeDate = dateFormat.format(date);

                        timeRef.removeValue(); // ট্রিগার নোডটি মুছে ক্লিয়ার করা হলো
                        starts.setText("START");
                        starts.setEnabled(true);

                        // পরবর্তী হাজিরা স্ক্রিনে (student_attandance) ডেটা পাঠানো
                        Intent in = new Intent(schadule_dashboard.this, student_attandance.class);
                        in.putExtra("SERVER_DATE_KEY", secureRealTimeDate);

                        // 🛠️ ফিক্সড: এখানেও আন্ডারস্কোর ফরম্যাট করা কোডটি পাঠানো নিশ্চিত করা হলো
                        in.putExtra("COURSE_CODE_KEY", optimizedCourseCode);

                        // 🛠️ আপনার মূল সেশন লজিক: প্রথম ৪ ডিজিট থেকে বছরের শেষ ২ ডিজিট নেওয়া হবে (যেমন: "2021-22" বা "2021" -> "21")
                        String sessionInput = session.getText().toString().trim();

                        if (sessionInput.length() >= 4) {
                            // প্রথমে শুধু প্রথম ৪টি ক্যারেক্টার আলাদা করা হলো (যেমন: "2021")
                            String firstFourDigits = sessionInput.substring(0, 4);
                            // এবার তার শেষ ২ ডিজিট কাটা হলো (2021 থেকে বের হবে "21")
                            sessionInput = firstFourDigits.substring(2, 4);
                        } else if (sessionInput.length() == 2) {
                            // টিচার যদি সরাসরি শুধু "21" ইনপুট দেয়, তবে সেটা যেমন আছে তেমনই থাকবে
                        } else {
                            sessionInput = "21"; // কোনো ইনপুট না থাকলে বা ভুল হলে সেফটি ডিফল্ট সেশন
                        }

                        // 🛠️ [নতুন সংযোজন]: আরডুইনোর ফিল্টারের জন্য কারেন্ট একটিভ সেশনটি গ্লোবাল নোডে পুশ করা হলো
                        mDatabase.child(activeUni).child("Current_Class_Config").child("Class_Session").setValue(sessionInput);

                        in.putExtra("SESSION_KEY", sessionInput);
                        in.putExtra("depertment", depertment);
                        in.putExtra("uni_name", activeUni);
                        startActivity(in);
                        finish(); // এই স্ক্রিনটি বন্ধ করে দেওয়া হলো
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                starts.setEnabled(true);
                starts.setText("START");
                Toast.makeText(schadule_dashboard.this, "Database Error! Try Again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}