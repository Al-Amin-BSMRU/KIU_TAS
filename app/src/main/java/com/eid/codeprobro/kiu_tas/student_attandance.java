package com.eid.codeprobro.kiu_tas;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class student_attandance extends AppCompatActivity {

    TextView tvDisplayCourseCode, tvDisplaySession, tvMonthName, tvTotalStudentsCount, tvPresentStudentsCount;
    Button btnFinishAttendance;
    LinearLayout layoutDateStrip;
    RecyclerView rvStudentAttendanceList;

    String receivedServerDate, selectedDate;
    String courseCode, sessionText; // শিক্ষকের দেওয়া বছর (যেমন: "2021")
    String uni_name, depertment;

    DatabaseReference mDatabase;
    ValueEventListener attendanceListener;
    DatabaseReference attendanceRef;

    StudentAdapter adapter;
    List<StudentModel> studentList = new ArrayList<>();
    Map<String, StudentModel> allSessionStudentsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle Bundle) {
        super.onCreate(Bundle);
        setContentView(R.layout.student_attandance);

        tvDisplayCourseCode = findViewById(R.id.tvDisplayCourseCode);
        tvDisplaySession = findViewById(R.id.tvDisplaySession);
        tvMonthName = findViewById(R.id.tvMonthName);
        tvTotalStudentsCount = findViewById(R.id.tvTotalStudentsCount);
        tvPresentStudentsCount = findViewById(R.id.tvPresentStudentsCount);
        btnFinishAttendance = findViewById(R.id.btnFinishAttendance);
        layoutDateStrip = findViewById(R.id.layoutDateStrip);
        rvStudentAttendanceList = findViewById(R.id.rvStudentAttendanceList);

        rvStudentAttendanceList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(studentList);
        rvStudentAttendanceList.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent in = getIntent();
        if (in != null) {

            uni_name = in.getStringExtra("uni_name");
            depertment = in.getStringExtra("depertment");
            String rawDate = in.getStringExtra("SERVER_DATE_KEY"); // ড্যাশবোর্ড থেকে আসা ডেট
            courseCode = in.getStringExtra("COURSE_CODE_KEY");
            sessionText = in.getStringExtra("SESSION_KEY");

            // 🎯 ডেট ফরম্যাট স্ট্যান্ডার্ডাইজ করা (যেন সবসময় yyyy-MM-dd ফরম্যাট নিশ্চিত হয়)
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date parsedDate = inputFormat.parse(rawDate);
                if (parsedDate != null) {
                    receivedServerDate = inputFormat.format(parsedDate); // এটি ৫-১ কে ০৫-০১ বানিয়ে দেবে
                } else {
                    receivedServerDate = rawDate;
                }
            } catch (Exception e) {
                receivedServerDate = rawDate;
            }

            if (uni_name == null || uni_name.isEmpty()) uni_name = "OX";
            if (depertment == null || depertment.isEmpty()) depertment = "CSE";

            selectedDate = receivedServerDate;
            if (courseCode != null) tvDisplayCourseCode.setText(courseCode.replace("_", " "));
            tvDisplaySession.setText("Session: " + sessionText);

            updateMonthNameHeader(receivedServerDate);
//            uni_name = in.getStringExtra("uni_name");
//            depertment = in.getStringExtra("depertment");
//            receivedServerDate = in.getStringExtra("SERVER_DATE_KEY");
//            courseCode = in.getStringExtra("COURSE_CODE_KEY");
//            sessionText = in.getStringExtra("SESSION_KEY"); // শিক্ষকের ইনপুট (যেমন "2021")
//
//            if (uni_name == null || uni_name.isEmpty()) uni_name = "OX";
//            if (depertment == null || depertment.isEmpty()) depertment = "CSE";
//
//            selectedDate = receivedServerDate;
//            if (courseCode != null) tvDisplayCourseCode.setText(courseCode.replace("_", " "));
//            tvDisplaySession.setText("Session: " + sessionText);
//
//            updateMonthNameHeader(receivedServerDate);
        }

        generateDynamicCalendarStrip(receivedServerDate);
        loadStudentsAndStartTracking();

        updateClassEndTimeNode();

        btnFinishAttendance.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Finish Class?")
                    .setMessage("Are you sure you want to close attendance for this course?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        DatabaseReference configRef = mDatabase.child(uni_name).child("Current_Class_Config");
                        configRef.child("Course_Code").setValue("Finished");
                        configRef.child("Is_Locked").setValue(true);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void updateClassEndTimeNode() {
        if (courseCode != null && !courseCode.isEmpty()) {
            DatabaseReference configRef = mDatabase.child(uni_name).child("Current_Class_Config");
            String tim = "2";
            configRef.child("Duration_Minutes").setValue(tim);
            Toast.makeText(this, "The attandance times is setted for " + tim + " min", Toast.LENGTH_SHORT).show();
            configRef.child("Is_Locked").setValue(false);
        }
    }

    private void updateMonthNameHeader(String dateStr) {
        if (dateStr == null) return;
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        try {
            Date date = parser.parse(dateStr);
            if (date != null) tvMonthName.setText(formatter.format(date).toUpperCase());
        } catch (Exception e) {
            tvMonthName.setText(dateStr);
        }
    }

    private void generateDynamicCalendarStrip(String baseDateStr) {
        if (baseDateStr == null) return;
        layoutDateStrip.removeAllViews();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = parser.parse(baseDateStr);
            Calendar cal = Calendar.getInstance();
            if (date != null) cal.setTime(date);

            int maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            int currentYear = cal.get(Calendar.YEAR);
            int currentMonth = cal.get(Calendar.MONTH);

            for (int i = 1; i <= maxDaysInMonth; i++) {
                TextView tvDateButton = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(110, 110);
                params.setMargins(10, 10, 10, 10);
                tvDateButton.setLayoutParams(params);
                tvDateButton.setText(String.valueOf(i));
                tvDateButton.setGravity(Gravity.CENTER);
                tvDateButton.setTextSize(16);

                final String targetDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", currentYear, currentMonth + 1, i);
                updateButtonVisuals(tvDateButton, targetDate.equals(selectedDate));
                tvDateButton.setTag(targetDate);

                tvDateButton.setOnClickListener(v -> {
                    selectedDate = (String) v.getTag();
                    for (int j = 0; j < layoutDateStrip.getChildCount(); j++) {
                        TextView btn = (TextView) layoutDateStrip.getChildAt(j);
                        updateButtonVisuals(btn, btn.getTag().equals(selectedDate));
                    }
                    startAttendanceTracking();
                });
                layoutDateStrip.addView(tvDateButton);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateButtonVisuals(TextView tv, boolean isSelected) {
        if (isSelected) {
            tv.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            tv.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        } else {
            tv.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            tv.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
    }

    // 🎯 [আপডেটেড মেথড]: "2021-22" ফরম্যাটের ডেটার সাথে শিক্ষকের "2021" ম্যাচ করার লজিক
    private void loadStudentsAndStartTracking() {
        mDatabase.child(uni_name).child(depertment).child("student")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allSessionStudentsMap.clear();
                        int validStudentCountForThisSession = 0;

                        // 🎯 শিক্ষকের ইনপুট ক্লিনিং লজিক: "2022" দিলে কেটে "22" হবে, আর "22" দিলে "22"-ই থাকবে
                        String targetSessionClean = (sessionText != null) ? sessionText.trim() : "";
                        if (targetSessionClean.length() >= 4) {
                            targetSessionClean = targetSessionClean.substring(targetSessionClean.length() - 2);
                        }

                        for (DataSnapshot studentData : snapshot.getChildren()) {
                            Object sessionObj = studentData.child("session").getValue();
                            String dbSession = sessionObj != null ? String.valueOf(sessionObj).trim() : "";

                            // 🎯 [নিখুঁত ম্যাচিং কন্ডিশন]: ডাটাবেসের ২-ডিজিট সেশনের সাথে সরাসরি .equals() দিয়ে চেক
                            if (!dbSession.isEmpty() && !targetSessionClean.isEmpty() && dbSession.equals(targetSessionClean)) {

                                Object punchIdObj = studentData.child("punch_id").getValue();
                                if (punchIdObj == null) punchIdObj = studentData.child("puid").getValue();
                                if (punchIdObj == null) punchIdObj = studentData.child("punch").getValue();

                                String punchKey = (punchIdObj != null) ? String.valueOf(punchIdObj) : studentData.getKey();

                                Object idObj = studentData.child("id").getValue();
                                String displayId = idObj != null ? String.valueOf(idObj) : studentData.getKey();

                                Object nameObj = studentData.child("name").getValue();
                                if (nameObj == null) nameObj = studentData.child("namid").getValue();
                                String name = nameObj != null ? String.valueOf(nameObj) : "Unknown Student";

                                if (name.contains("_")) {
                                    name = name.split("_")[0];
                                }

                                // শুধুমাত্র সেশন ম্যাচ হওয়া স্টুডেন্ট ম্যাপে অ্যাড হবে
                                allSessionStudentsMap.put(punchKey, new StudentModel(displayId, name));

                                // কাঙ্ক্ষিত সেশনের জন্য কাউন্টার ১ বাড়ানো হলো
                                validStudentCountForThisSession++;
                            }
                        }

                        // 🎯 এখন একদম পারফেক্টলি ফিল্টারড কাউন্ট (যেমন: শুধু ২ জন) স্ক্রিনে দেখাবে
                        tvTotalStudentsCount.setText(String.valueOf(validStudentCountForThisSession));

                        startAttendanceTracking();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(student_attandance.this, "Failed to load students: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startAttendanceTracking() {
        if (attendanceRef != null && attendanceListener != null) {
            attendanceRef.removeEventListener(attendanceListener);
        }

        attendanceRef = mDatabase.child(uni_name)
                .child(depertment)
                .child("attendance")
                .child(selectedDate)
                .child(courseCode)
                .child("session_" + sessionText);

        attendanceListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear();

                if (snapshot.exists()) {
                    tvPresentStudentsCount.setText(String.valueOf(snapshot.getChildrenCount()));
                    for (DataSnapshot presentStudentSnap : snapshot.getChildren()) {
                        String punchId = presentStudentSnap.getKey();

                        if (punchId != null && allSessionStudentsMap.containsKey(punchId)) {
                            StudentModel s = allSessionStudentsMap.get(punchId);
                            if (s != null) {
                                s.setPresent(true);
                                studentList.add(s);
                            }
                        }
                    }
                } else {
                    tvPresentStudentsCount.setText("0");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        attendanceRef.addValueEventListener(attendanceListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (attendanceRef != null && attendanceListener != null) {
            attendanceRef.removeEventListener(attendanceListener);
        }
    }
}