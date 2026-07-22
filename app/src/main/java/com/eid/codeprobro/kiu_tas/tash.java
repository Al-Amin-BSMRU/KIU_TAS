package com.eid.codeprobro.kiu_tas;

public class tash {
//    //schadule_dashboard.java
////package com.eid.codeprobro.kiu_tas;
////
////import android.content.Intent;
////import android.os.Bundle;
////import android.view.View;
////import android.widget.Button;
////import android.widget.EditText;
////import android.widget.Toast;
////import androidx.activity.EdgeToEdge;
////import androidx.appcompat.app.AppCompatActivity;
////import com.google.firebase.database.DataSnapshot;
////import com.google.firebase.database.DatabaseError;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////import com.google.firebase.database.ServerValue;
////import com.google.firebase.database.ValueEventListener;
////import java.text.SimpleDateFormat;
////import java.util.Date;
////import java.util.Locale;
////
////public class schadule_dashboard extends AppCompatActivity implements View.OnClickListener {
////
////    EditText Course_Name, Course_Code, Total_Student, session;
////    Button starts,DbStructure;
////    DatabaseReference mDatabase;
////    String uni_name, name, idi, depertment;
////
////    @Override
////    protected void onCreate(Bundle Bundle) {
////        super.onCreate(Bundle);
////        EdgeToEdge.enable(this);
////        setContentView(R.layout.schadule_dashboard);
////
////        Course_Name = findViewById(R.id.Course_Name);
////        Course_Code = findViewById(R.id.Course_Code);
////        Total_Student = findViewById(R.id.Total_Student);
////        session = findViewById(R.id.session);
////        starts = findViewById(R.id.starts);
////        DbStructure=findViewById(R.id.DbStructure);
////
////        mDatabase = FirebaseDatabase.getInstance().getReference();
////
////        Intent in = getIntent();
////        if (in != null) {
////            uni_name = in.getStringExtra("uni_name");
////            name = in.getStringExtra("name");
////            idi = in.getStringExtra("id");
////            depertment = in.getStringExtra("depertment"); // টিচারের ডিপার্টমেন্ট (যেমন: CSE)
////        }
////        starts.setOnClickListener(this);
////    }
////
////    @Override
////    public void onClick(View v) {
////        if (v.getId() == R.id.starts) {
////            String cName = Course_Name.getText().toString().trim();
////
////            String cCode = Course_Code.getText().toString().trim().toUpperCase().replace(" ", "_");
////
////            String sessInput = session.getText().toString().trim();
////            String totalStd = Total_Student.getText().toString().trim();
////
////            // সব ইনপুট দেওয়া হয়েছে কিনা ভ্যালিডেশন চেক
////            if (cName.isEmpty() || cCode.isEmpty() || sessInput.isEmpty() || totalStd.isEmpty()) {
////                Toast.makeText(this, "Please fill all info to start class!", Toast.LENGTH_SHORT).show();
////                return;
////            }
////
////            starts.setText("Initializing System...");
////            starts.setEnabled(false);
////
////            String activeUni = (uni_name != null && !uni_name.isEmpty()) ? uni_name : "OX";
////            mDatabase.child(activeUni).child("Current_Class_Config").child("Course_Code").setValue(cCode);
////
////            mDatabase.child(activeUni).child(depertment).child("courses").child(cCode).child("total_students").setValue(totalStd);
////
////            fetchFirebaseServerDate(cCode); // ফিক্সড: আন্ডারস্কোরসহ কোডটি মেথডে পাঠানো হলো
////        }
////        if(v.getId()==R.id.DbStructure){
////            Intent in=new Intent(schadule_dashboard.this,database_link.class);
////            startActivity(in);
////        }
////
////    }
////local data
////    private void fetchFirebaseServerDate(final String optimizedCourseCode) {
////        String activeUni = (uni_name != null && !uni_name.isEmpty()) ? uni_name : "OX";
////
////        Date deviceCurrentDate = new Date();
////
////        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
////        String secureRealTimeDate = dateFormat.format(deviceCurrentDate);
////
////        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
////        String deviceTimeStr = timeFormat.format(deviceCurrentDate);
////
////        long deviceTimestamp = deviceCurrentDate.getTime();
////        DatabaseReference timeRef = mDatabase.child(activeUni).child("Current_Class_Config").child("server_time_trigger");
////
////        timeRef.setValue(deviceTimestamp);
////
////        DatabaseReference configRef = mDatabase.child(activeUni).child("Current_Class_Config");
////        configRef.child("Date").setValue(secureRealTimeDate);
////        configRef.child("Start_Time").setValue(deviceTimeStr);
////
////        // UI আপডেট
////        starts.setText("START");
////        starts.setEnabled(true);
////
////        Intent in = new Intent(schadule_dashboard.this, student_attandance.class);
////        in.putExtra("SERVER_DATE_KEY", secureRealTimeDate); // ফোনের লোকাল ডেট পাস হচ্ছে
////        in.putExtra("COURSE_CODE_KEY", optimizedCourseCode);
////
////        String sessionInput = session.getText().toString().trim();
////        if (sessionInput.length() >= 4) {
////            String firstFourDigits = sessionInput.substring(0, 4);
////            sessionInput = firstFourDigits.substring(2, 4);
////        } else if (sessionInput.length() == 2) {
////            // যা আছে তাই থাকবে
////        } else {
////            sessionInput = "21";
////        }
////
////        configRef.child("Class_Session").setValue(sessionInput);
////
////        in.putExtra("SESSION_KEY", sessionInput);
////        in.putExtra("depertment", depertment);
////        in.putExtra("uni_name", activeUni);
////
////        startActivity(in);
////        finish();
////    }
//
////    real data
//
////    private void fetchFirebaseServerDate(final String optimizedCourseCode) {
////        String activeUni = (uni_name != null && !uni_name.isEmpty()) ? uni_name : "OX";
////        final DatabaseReference timeRef = mDatabase.child(activeUni).child("Current_Class_Config").child("server_time_trigger");
////        timeRef.setValue(ServerValue.TIMESTAMP);
////
////        timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                if (dataSnapshot.exists()) {
////                    Long timestamp = (Long) dataSnapshot.getValue();
////                    if (timestamp != null) {
////                        Date date = new Date(timestamp);
////                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
////                        String secureRealTimeDate = dateFormat.format(date);
////
////                        timeRef.removeValue(); // ট্রিগার নোডটি মুছে ক্লিয়ার করা হলো
////                        starts.setText("START");
////                        starts.setEnabled(true);
////
////                        // পরবর্তী হাজিরা স্ক্রিনে (student_attandance) ডেটা পাঠানো
////                        Intent in = new Intent(schadule_dashboard.this, student_attandance.class);
////                        in.putExtra("SERVER_DATE_KEY", secureRealTimeDate);
////
////                        in.putExtra("COURSE_CODE_KEY", optimizedCourseCode);
////
////                        String sessionInput = session.getText().toString().trim();
////
////                        if (sessionInput.length() >= 4) {
////                            // প্রথমে শুধু প্রথম ৪টি ক্যারেক্টার আলাদা করা হলো (যেমন: "2021")
////                            String firstFourDigits = sessionInput.substring(0, 4);
////                            // এবার তার শেষ ২ ডিজিট কাটা হলো (2021 থেকে বের হবে "21")
////                            sessionInput = firstFourDigits.substring(2, 4);
////                        } else if (sessionInput.length() == 2) {
////                        } else {
////                            sessionInput = "21";
////                        }
////
////                        mDatabase.child(activeUni).child("Current_Class_Config").child("Class_Session").setValue(sessionInput);
////
////                        in.putExtra("SESSION_KEY", sessionInput);
////                        in.putExtra("depertment", depertment);
////                        in.putExtra("uni_name", activeUni);
////                        startActivity(in);
////                        finish();
////                    }
////                }
////            }
//
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////                starts.setEnabled(true);
////                starts.setText("START");
////                Toast.makeText(schadule_dashboard.this, "Database Error! Try Again.", Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
////}
//
//
//    previous _4_schadule_dashboardcodes:
   ////////// ///_4_schadule_dashboard files
//package com.eid.codeprobro.kiu_tas;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//public class _4_schadule_dashboard extends AppCompatActivity implements View.OnClickListener {
//
//    EditText Course_Name, Course_Code, Total_Student, session;
//    Button starts_btn, Attandance_list;
//    DatabaseReference mDatabase;
//    String uni_name, name, idi, depertment;
//
//    @Override
//    protected void onCreate(Bundle Bundle) {
//        super.onCreate(Bundle);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.schadule_dashboard);
//
//        Course_Name = findViewById(R.id.Course_Name);
//        Course_Code = findViewById(R.id.Course_Code);
//        Total_Student = findViewById(R.id.Total_Student);
//        session = findViewById(R.id.session);
//        starts_btn = findViewById(R.id.starts);
//        Attandance_list = findViewById(R.id.Attandance_list);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//
//        Intent in = getIntent();
//        if (in != null) {
//            uni_name = in.getStringExtra("uni_name");
//            name = in.getStringExtra("name");
//            idi = in.getStringExtra("id");
//            depertment = in.getStringExtra("depertment");
//        }
//        starts_btn.setOnClickListener(this);
//        Attandance_list.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.starts) {
//            String cName = Course_Name.getText().toString().trim();
//            String cCode = Course_Code.getText().toString().trim().toUpperCase().replace(" ", "_");
//            String sessInput = session.getText().toString().trim();
//            String totalStd = Total_Student.getText().toString().trim();
//
//            if (cName.isEmpty() || cCode.isEmpty() || sessInput.isEmpty() || totalStd.isEmpty()) {
//                Toast.makeText(this, "Please fill all info to start class!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            starts_btn.setText("Initializing System...");
//            starts_btn.setEnabled(false);
//
//            String activeUni = (uni_name != null && !uni_name.isEmpty()) ? uni_name : "OX";
//
//            mDatabase.child(activeUni).child("Current_Class_Config").child("Course_Code").setValue(cCode);
//            mDatabase.child(activeUni).child(depertment).child("courses").child(cCode).child("total_students").setValue(totalStd);
//
//            startAttendanceWithPhoneDate(cCode);
//        }
//
//        if(v.getId() == R.id.Attandance_list){
//            Intent in = new Intent(_4_schadule_dashboard.this, _5_database_link.class);
//            in.putExtra("uni_name", uni_name != null ? uni_name : "OX");
//            in.putExtra("depertment", depertment != null ? depertment : "CSE");
//            startActivity(in);
//        }
//    }
//
////         set the nodes date on the firebase real date
////    private void fetchFirebaseServerDate(final String optimizedCourseCode) {
////        String activeUni;
////        if (uni_name != null && !uni_name.isEmpty()) {
////            activeUni = uni_name;
////        } else {
////            activeUni = "OX";
////        }
////
////        final DatabaseReference timeRef = mDatabase.child(activeUni).child("Current_Class_Config").child("server_time_trigger");
////        timeRef.setValue(ServerValue.TIMESTAMP);
/////// //////creating a tempory time at that time in the database
////        timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
////            /// ////reading time convert in real data
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                if (dataSnapshot.exists()) {
////                    Long timestamp = (Long) dataSnapshot.getValue();
////                    if (timestamp != null) {
////                        Date date = new Date(timestamp);
////                        /// ////reading time convert in real data
////
////                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
////                        String secureRealTimeDate = dateFormat.format(date);
/////////////////convert the date in the real time humman redable dates
////                        timeRef.removeValue();     //////here removed the nodes  so we cant seee
////                        starts_btn.setText("START");
////                        starts_btn.setEnabled(true);
////
////                        Intent in = new Intent(_4_schadule_dashboard.this, _5_student_attandance.class);
////                        in.putExtra("SERVER_DATE_KEY", secureRealTimeDate);
////                        in.putExtra("COURSE_CODE_KEY", optimizedCourseCode);
////
////                        String sessionInput = session.getText().toString().trim();
////                        if (sessionInput.length() >= 4) {
////                            String firstFourDigits = sessionInput.substring(0, 4); ///////2021-22--->>>2021
////                            sessionInput = firstFourDigits.substring(2, 4);///////2021---->21
////                        }
////                        /// ///ox
////                        mDatabase.child(activeUni).child("Current_Class_Config").child("Class_Session").setValue(sessionInput);
////
////                        in.putExtra("SESSION_KEY", sessionInput);
////                        in.putExtra("depertment", depertment);
////                        in.putExtra("uni_name", activeUni);
////                        startActivity(in);
////                    }
////                }
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////                starts_btn.setEnabled(true);
////                starts_btn.setText("START");
////                Toast.makeText(_4_schadule_dashboard.this, "Database Error! Try Again.", Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
//// Phones date calculation and upload to firebase
//private void startAttendanceWithPhoneDate(final String optimizedCourseCode) {
//    String activeUni = (uni_name != null && !uni_name.isEmpty()) ? uni_name : "OX";
//
//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//    String secureRealTimeDate = sdf.format(new Date());
//
//    String sessionInput = session.getText().toString().trim();
//    if (sessionInput.length() >= 4) {
//        String firstFourDigits = sessionInput.substring(0, 4);
//        sessionInput = firstFourDigits.substring(2, 4);
//    }
//
//    DatabaseReference configRef = mDatabase.child(activeUni).child("Current_Class_Config");
//    configRef.child("Class_Session").setValue(sessionInput);
//    configRef.child("Class_Date").setValue(secureRealTimeDate);
//
//    starts_btn.setText("START");
//    starts_btn.setEnabled(true);
//
//    Intent in = new Intent(_4_schadule_dashboard.this, _5_student_attandance.class);
//    in.putExtra("SERVER_DATE_KEY", secureRealTimeDate);
//    in.putExtra("COURSE_CODE_KEY", optimizedCourseCode);
//    in.putExtra("SESSION_KEY", sessionInput);
//    in.putExtra("depertment", depertment);
//    in.putExtra("uni_name", activeUni);
//
//    startActivity(in);
//}

//}

////////////////    _5_student_attandance:

    //package com.eid.codeprobro.kiu_tas;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.google.firebase.database.*;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//public class _5_student_attandance extends AppCompatActivity {
//
//    // UI Elements
//    TextView tvDisplayCourseCode, tvDisplaySession, tvMonthName, tvTotalStudentsCount, tvPresentStudentsCount;
//    TextView btnPrevMonth, btnNextMonth;
//    Button btnFinishAttendance;
//    LinearLayout layoutDateStrip;
//    RecyclerView rvStudentAttendanceList;
//
//    // Data Variables
//    String receivedServerDate, selectedDate;
//    String courseCode, sessionText;
//    String uni_name, depertment;
//
//    // Firebase References
//    DatabaseReference mDatabase;
//    ValueEventListener attendanceListener;
//    DatabaseReference attendanceRef;
//
//    // Adapter & Lists
//    _5_StudentAdapter adapter;
//    List<_5_StudentModel> studentList = new ArrayList<>();
//    Map<String, _5_StudentModel> allSessionStudentsMap = new HashMap<>();
//
//    Calendar currentCalendarInstance = Calendar.getInstance();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.student_attandance);
//
//        initUI();
//        setupIntentData();
//        setupCalendarButtons();
//
//        refreshCalendarAndStrip();
//        loadStudentsAndStartTracking();
//        updateClassEndTimeNode();
//    }
//
//    private void initUI() {
//        tvDisplayCourseCode = findViewById(R.id.tvDisplayCourseCode);
//        tvDisplaySession = findViewById(R.id.tvDisplaySession);
//        tvMonthName = findViewById(R.id.tvMonthName);
//        tvTotalStudentsCount = findViewById(R.id.tvTotalStudentsCount);
//        tvPresentStudentsCount = findViewById(R.id.tvPresentStudentsCount);
//        btnFinishAttendance = findViewById(R.id.btnFinishAttendance);
//        layoutDateStrip = findViewById(R.id.layoutDateStrip);
//        btnPrevMonth = findViewById(R.id.btnPrevMonth);
//        btnNextMonth = findViewById(R.id.btnNextMonth);
//
//        rvStudentAttendanceList = findViewById(R.id.rvStudentAttendanceList);
//        rvStudentAttendanceList.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new _5_StudentAdapter(studentList);
//        rvStudentAttendanceList.setAdapter(adapter);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//    }
//
//    private void setupIntentData() {
//        Intent in = getIntent();
//        if (in != null) {
//            uni_name = in.getStringExtra("uni_name");
//            depertment = in.getStringExtra("depertment");
//            String rawDate = in.getStringExtra("SERVER_DATE_KEY");
//            courseCode = in.getStringExtra("COURSE_CODE_KEY");
//            sessionText = in.getStringExtra("SESSION_KEY");
//
//            try {
//                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                Date parsedDate = inputFormat.parse(rawDate);
//                if (parsedDate != null) {
//                    receivedServerDate = inputFormat.format(parsedDate);
//                    currentCalendarInstance.setTime(parsedDate);
//                } else {
//                    receivedServerDate = rawDate;
//                }
//            } catch (Exception e) {
//                receivedServerDate = rawDate;
//            }
//
//            if (uni_name == null || uni_name.isEmpty()) uni_name = "OX";
//            if (depertment == null || depertment.isEmpty()) depertment = "CSE";
//
//            selectedDate = receivedServerDate;
//            if (courseCode != null) tvDisplayCourseCode.setText(courseCode.replace("_", " "));
//            tvDisplaySession.setText("Session: " + sessionText);
//        }
//    }
//
//    private void setupCalendarButtons() {
//        btnPrevMonth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentCalendarInstance.add(Calendar.MONTH, -1);
//                refreshCalendarAndStrip();
//            }
//        });
//
//        btnNextMonth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentCalendarInstance.add(Calendar.MONTH, 1);
//                refreshCalendarAndStrip();
//            }
//        });
//
//        btnFinishAttendance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showFinishAttendanceDialog();
//            }
//        });
//    }
//
//    private void showFinishAttendanceDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Finish Class?");
//        builder.setMessage("Are you sure you want to close attendance?");
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                DatabaseReference configRef = mDatabase.child(uni_name).child("Current_Class_Config");
//                configRef.child("Course_Code").setValue("Finished");
//                configRef.child("Is_Locked").setValue(true);
//                finish();
//            }
//        });
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.show();
//    }
//
//    private void updateClassEndTimeNode() {
//        if (courseCode != null && !courseCode.isEmpty()) {
//            DatabaseReference configRef = mDatabase.child(uni_name).child("Current_Class_Config");
//            configRef.child("Duration_Minutes").setValue("2");
//            configRef.child("Is_Locked").setValue(false);
//            Toast.makeText(this, "Attendance time set for 2 mins", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void refreshCalendarAndStrip() {
//        SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
//        tvMonthName.setText(formatter.format(currentCalendarInstance.getTime()).toUpperCase());
//
//        layoutDateStrip.removeAllViews();
//
//        int maxDaysInMonth = currentCalendarInstance.getActualMaximum(Calendar.DAY_OF_MONTH);
//        int currentYear = currentCalendarInstance.get(Calendar.YEAR);
//        int currentMonth = currentCalendarInstance.get(Calendar.MONTH);
//
//        for (int i = 1; i <= maxDaysInMonth; i++) {
//            TextView tvDateButton = new TextView(this);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(110, 110);
//            params.setMargins(10, 10, 10, 10);
//            tvDateButton.setLayoutParams(params);
//            tvDateButton.setText(String.valueOf(i));
//            tvDateButton.setGravity(Gravity.CENTER);
//            tvDateButton.setTextSize(16);
//
//            final String targetDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", currentYear, currentMonth + 1, i);
//            updateButtonVisuals(tvDateButton, targetDate.equals(selectedDate));
//            tvDateButton.setTag(targetDate);
//
//            tvDateButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    selectedDate = (String) v.getTag();
//                    for (int j = 0; j < layoutDateStrip.getChildCount(); j++) {
//                        TextView btn = (TextView) layoutDateStrip.getChildAt(j);
//                        updateButtonVisuals(btn, btn.getTag().equals(selectedDate));
//                    }
//                    startAttendanceTracking();
//                }
//            });
//            layoutDateStrip.addView(tvDateButton);
//        }
//    }
//
//    private void updateButtonVisuals(TextView tv, boolean isSelected) {
//        if (isSelected) {
//            tv.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
//            tv.setTextColor(ContextCompat.getColor(this, android.R.color.white));
//        } else {
//            tv.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
//            tv.setTextColor(ContextCompat.getColor(this, android.R.color.black));
//        }
//    }
//
//    private void loadStudentsAndStartTracking() {
//        mDatabase.child(uni_name).child(depertment).child("student")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        allSessionStudentsMap.clear();
//                        int validStudentCount = 0;
//
//                        String targetSession = "";
//                        if (sessionText != null && sessionText.length() >= 2) {
//                            targetSession = sessionText.substring(sessionText.length() - 2);
//                        }
//
//                        for (DataSnapshot studentData : snapshot.getChildren()) {
//                            Object sessValue = studentData.child("session").getValue();
//                            String dbSession = sessValue != null ? String.valueOf(sessValue).trim() : "";
//
//                            if (!dbSession.isEmpty() && dbSession.endsWith(targetSession)) {
//
//                                String punchKey = "";
//
//                                if (studentData.child("puid").exists()) {
//                                    punchKey = String.valueOf(studentData.child("puid").getValue());
//                                }
//                                else if (studentData.child("punch").exists()) {
//                                    punchKey = String.valueOf(studentData.child("punch").getValue());
//                                }
//                                else if (studentData.child("punch_id").exists()) {
//                                    punchKey = String.valueOf(studentData.child("punch_id").getValue());
//                                }
//                                else {
//                                    punchKey = studentData.getKey();
//                                }
//
//                                String displayId = String.valueOf(studentData.child("id").getValue());
//                                String name = String.valueOf(studentData.child("name").getValue());
//
//                                if (name.contains("_")) {
//                                    name = name.split("_")[0];
//                                }
//
//                                allSessionStudentsMap.put(punchKey, new _5_StudentModel(displayId, name));
//                                validStudentCount++;
//                            }
//                        }
//
//                        tvTotalStudentsCount.setText(String.valueOf(validStudentCount));
//                        startAttendanceTracking();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(_5_student_attandance.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void startAttendanceTracking() {
//        if (attendanceRef != null && attendanceListener != null) {
//            attendanceRef.removeEventListener(attendanceListener);
//        }
//
//        attendanceRef = mDatabase.child(uni_name)
//                .child(depertment)
//                .child("attendance")
//                .child(selectedDate)
//                .child(courseCode)
//                .child("session_" + sessionText);
//
//        attendanceListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                studentList.clear();
//
//                if (snapshot.exists()) {
//                    tvPresentStudentsCount.setText(String.valueOf(snapshot.getChildrenCount()));
//                    for (DataSnapshot presentStudentSnap : snapshot.getChildren()) {
//                        String punchId = presentStudentSnap.getKey();
//
//                        if (punchId != null && allSessionStudentsMap.containsKey(punchId)) {
//                            _5_StudentModel s = allSessionStudentsMap.get(punchId);
//                            if (s != null) {
//                                s.setPresent(true);
//                                studentList.add(s);
//                            }
//                        }
//                    }
//                } else {
//                    tvPresentStudentsCount.setText("0");
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        };
//        attendanceRef.addValueEventListener(attendanceListener);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (attendanceRef != null && attendanceListener != null) {
//            attendanceRef.removeEventListener(attendanceListener);
//        }
//    }
//}



}
