
package com.eid.codeprobro.kiu_tas;

import android.content.DialogInterface;
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

    // UI Elements
    TextView tvDisplayCourseCode, tvDisplaySession, tvMonthName, tvTotalStudentsCount, tvPresentStudentsCount;
    TextView btnPrevMonth, btnNextMonth;
    Button btnFinishAttendance;
    LinearLayout layoutDateStrip;
    RecyclerView rvStudentAttendanceList;

    // Data Variables
    String receivedServerDate, selectedDate;
    String courseCode, sessionText;
    String uni_name, depertment;

    DatabaseReference mDatabase;
    ValueEventListener attendanceListener;
    DatabaseReference attendanceRef;

    DatabaseReference configRef;
    ValueEventListener configListener;

    _5_StudentAdapter adapter;
    List<_5_StudentModel> studentList = new ArrayList<_5_StudentModel>();
    Map<String, _5_StudentModel> allSessionStudentsMap = new HashMap<>();

    Calendar currentCalendarInstance = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_attandance);

        initUI();
        setupIntentData();
        setupCalendarButtons();

        refreshCalendarAndStrip();
        loadStudentsAndStartTracking();
        updateClassEndTimeNode();

        startArduinoLockMonitoring();
    }

    private void initUI() {
        tvDisplayCourseCode = findViewById(R.id.tvDisplayCourseCode);
        tvDisplaySession = findViewById(R.id.tvDisplaySession);
        tvMonthName = findViewById(R.id.tvMonthName);
        tvTotalStudentsCount = findViewById(R.id.tvTotalStudentsCount);
        tvPresentStudentsCount = findViewById(R.id.tvPresentStudentsCount);
        btnFinishAttendance = findViewById(R.id.btnFinishAttendance);
        layoutDateStrip = findViewById(R.id.layoutDateStrip);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);

        rvStudentAttendanceList = findViewById(R.id.rvStudentAttendanceList);
        rvStudentAttendanceList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new _5_StudentAdapter(studentList);
        rvStudentAttendanceList.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void setupIntentData() {
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
                    currentCalendarInstance.setTime(parsedDate);
                } else {
                    receivedServerDate = rawDate;
                }
            } catch (Exception e) {
                receivedServerDate = rawDate;
            }

            if (uni_name == null || uni_name.isEmpty())
            {
                uni_name = "OX";
            }
            if (depertment == null || depertment.isEmpty()) {
                depertment = "CSE";
            }
            selectedDate = receivedServerDate;
            if (courseCode != null) tvDisplayCourseCode.setText(courseCode.replace("_", " "));
            tvDisplaySession.setText("Session: " + sessionText);
        }
    }

    private void setupCalendarButtons() {
        btnPrevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCalendarInstance.add(Calendar.MONTH, -1);
                refreshCalendarAndStrip();
            }
        });

        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCalendarInstance.add(Calendar.MONTH, 1);
                refreshCalendarAndStrip();
            }
        });

        btnFinishAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFinishAttendanceDialog();
            }
        });
    }

    private void showFinishAttendanceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Finish Class?");
        builder.setMessage("Are you sure you want to close attendance?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference configRef = mDatabase.child(uni_name).child("Current_Class_Config");
                configRef.child("Course_Code").setValue("Finished");
                configRef.child("Is_Locked").setValue(true);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void updateClassEndTimeNode() {
        if (courseCode != null && !courseCode.isEmpty()) {
            DatabaseReference configRef = mDatabase.child(uni_name).child("Current_Class_Config");
            configRef.child("Duration_Minutes").setValue("2");
            configRef.child("Is_Locked").setValue(false);
            Toast.makeText(this, "Attendance time set for 2 mins", Toast.LENGTH_SHORT).show();
        }
    }

    private void startArduinoLockMonitoring() {
        configRef = mDatabase.child(uni_name).child("Current_Class_Config").child("Course_Code");

        configListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if (status != null && status.equals("Finished")) {
                        Toast.makeText(_5_student_attandance.this, "Attendance Closed (Timer Finished)!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        configRef.addValueEventListener(configListener);
    }

    private void refreshCalendarAndStrip() {
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

            final String targetDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", currentYear, currentMonth + 1, i);
            updateButtonVisuals(tvDateButton, targetDate.equals(selectedDate));
            tvDateButton.setTag(targetDate);

            tvDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedDate = (String) v.getTag();
                    for (int j = 0; j < layoutDateStrip.getChildCount(); j++) {
                        TextView btn = (TextView) layoutDateStrip.getChildAt(j);
                        updateButtonVisuals(btn, btn.getTag().equals(selectedDate));
                    }
                    startAttendanceTracking();
                }
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
                        int validStudentCount = 0;

                        String targetSession = "";
                        if (sessionText != null && sessionText.length() >= 2) {
                            targetSession = sessionText.substring(sessionText.length() - 2);
                        }

                        for (DataSnapshot studentData : snapshot.getChildren()) {
                            Object sessValue = studentData.child("session").getValue();
                            String dbSession;

                            if (sessValue != null) {
                                dbSession = String.valueOf(sessValue).trim();
                            } else {
                                dbSession = "";
                            }
                            if (!dbSession.isEmpty() && dbSession.endsWith(targetSession)) {

                                String punchKey = "";

                                if (studentData.child("puid").exists()) {
                                    punchKey = String.valueOf(studentData.child("puid").getValue());
                                }
                                else if (studentData.child("punch").exists()) {
                                    punchKey = String.valueOf(studentData.child("punch").getValue());
                                }
                                else if (studentData.child("punch_id").exists()) {
                                    punchKey = String.valueOf(studentData.child("punch_id").getValue());
                                }
                                else {
                                    punchKey = studentData.getKey();
                                }

                                String displayId = String.valueOf(studentData.child("id").getValue());
                                String name = String.valueOf(studentData.child("name").getValue());

                                if (name.contains("_")) {
                                    name = name.split("_")[0];
                                }

                                allSessionStudentsMap.put(punchKey, new _5_StudentModel(displayId, name));
                                validStudentCount++;
                            }
                        }

                        tvTotalStudentsCount.setText(String.valueOf(validStudentCount));
                        startAttendanceTracking();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(_5_student_attandance.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (configRef != null && configListener != null) {
            configRef.removeEventListener(configListener);
        }
    }
}