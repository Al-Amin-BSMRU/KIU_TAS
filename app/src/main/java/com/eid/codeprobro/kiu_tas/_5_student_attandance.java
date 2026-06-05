package com.eid.codeprobro.kiu_tas;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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

public class _5_student_attandance extends AppCompatActivity {

    TextView tvDisplayCourseCode, tvDisplaySession, tvMonthName, tvTotalStudentsCount, tvPresentStudentsCount;
    TextView btnPrevMonth, btnNextMonth;
    Button btnFinishAttendance;
    LinearLayout layoutDateStrip;
    RecyclerView rvStudentAttendanceList;

    String receivedServerDate, selectedDate;
    String courseCode, sessionText;
    String uni_name, depertment;

    DatabaseReference mDatabase;
    ValueEventListener attendanceListener;
    DatabaseReference attendanceRef;

    _5_StudentAdapter adapter;
    List<_5_StudentModel> studentList = new ArrayList<>();
    Map<String,_5_StudentModel> allSessionStudentsMap = new HashMap<>();

    // getting the current dates for the apps
    Calendar currentCalendarInstance = Calendar.getInstance();

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

        // initializing previous and recent buttons
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);

        rvStudentAttendanceList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new _5_StudentAdapter(studentList);
        rvStudentAttendanceList.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent in = getIntent();
        if (in != null) {
            uni_name = in.getStringExtra("uni_name");
            depertment = in.getStringExtra("depertment");
            String rawDate = in.getStringExtra("SERVER_DATE_KEY");
            courseCode = in.getStringExtra("COURSE_CODE_KEY");
            sessionText = in.getStringExtra("SESSION_KEY");

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date parsedDate = inputFormat.parse(rawDate);
                if (parsedDate != null) {
                    receivedServerDate = inputFormat.format(parsedDate);
                    // global calander is setting as the recent dates
                    currentCalendarInstance.setTime(parsedDate);
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
        }

        // recent month is loading
        refreshCalendarAndStrip();
        loadStudentsAndStartTracking();
        updateClassEndTimeNode();

        // previous month going
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendarInstance.add(Calendar.MONTH, -1);
            refreshCalendarAndStrip();
        });

        // next month going
        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCalendarInstance.add(Calendar.MONTH, 1);
                refreshCalendarAndStrip();
            }
        });



        btnFinishAttendance.setOnClickListener(v -> {
            new AlertDialog.Builder(_5_student_attandance.this)
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
            Toast.makeText(this, "The attendance time is set for " + tim + " min", Toast.LENGTH_SHORT).show();
            configRef.child("Is_Locked").setValue(false);
        }
    }

    // dynamiclly change the months and generate the each months :1 to 30, 31 ,29
    private void refreshCalendarAndStrip() {
        // মাসের হেডার টেক্সট সেট (যেমন: JANUARY 2026)
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthName.setText(formatter.format(currentCalendarInstance.getTime()).toUpperCase());

        layoutDateStrip.removeAllViews();

        int maxDaysInMonth = currentCalendarInstance.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currentYear = currentCalendarInstance.get(Calendar.YEAR);
        int currentMonth = currentCalendarInstance.get(Calendar.MONTH);

        for (int i = 1; i <= maxDaysInMonth; i++) {
            TextView tvDateButton = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(110, 110);
            params.setMargins(10, 10, 10, 10);
            tvDateButton.setLayoutParams(params);
            tvDateButton.setText(String.valueOf(i));
            tvDateButton.setGravity(Gravity.CENTER);
            tvDateButton.setTextSize(16);

            // yyyy-MM-dd
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

    private void loadStudentsAndStartTracking() {
        mDatabase.child(uni_name).child(depertment).child("student")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allSessionStudentsMap.clear();
                        int validStudentCountForThisSession = 0;

                        String targetSessionClean = (sessionText != null) ? sessionText.trim() : "";
                        if (targetSessionClean.length() >= 4) {
                            targetSessionClean = targetSessionClean.substring(targetSessionClean.length() - 2);
                        }

                        for (DataSnapshot studentData : snapshot.getChildren()) {
                            Object sessionObj = studentData.child("session").getValue();
                            String dbSession = sessionObj != null ? String.valueOf(sessionObj).trim() : "";

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

                                allSessionStudentsMap.put(punchKey, new _5_StudentModel(displayId, name));
                                validStudentCountForThisSession++;
                            }
                        }

                        tvTotalStudentsCount.setText(String.valueOf(validStudentCountForThisSession));
                        startAttendanceTracking();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(_5_student_attandance.this, "Failed to load students: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                            _5_StudentModel s = allSessionStudentsMap.get(punchId);
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