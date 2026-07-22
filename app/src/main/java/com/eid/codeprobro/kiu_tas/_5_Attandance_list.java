package com.eid.codeprobro.kiu_tas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class _5_Attandance_list extends AppCompatActivity {

    TextView tvCurrentLevel;
    RecyclerView rvDatabaseBrowser;
    Button btnBackLevel;

    DatabaseReference mDatabase;
    String uni_name = "OX", depertment = "CSE";

    String selectedCourse = "";
    String selectedSession = "";
    String selectedDate = "";

    int currentLevel = 0;
    List<String> browserList = new ArrayList<>();
    _5_Attandance_list_adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_link);

        tvCurrentLevel = findViewById(R.id.tvCurrentLevel);
        rvDatabaseBrowser = findViewById(R.id.rvDatabaseBrowser);
        btnBackLevel = findViewById(R.id.btnBackLevel);

        Intent in = getIntent();
        if (in != null) {
            if(in.getStringExtra("uni_name") != null)
            {
                uni_name = in.getStringExtra("uni_name");
            }
            if(in.getStringExtra("depertment") != null) {
                depertment = in.getStringExtra("depertment");
            }
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child(uni_name).child(depertment);
        rvDatabaseBrowser.setLayoutManager(new LinearLayoutManager(this));

        adapter = new _5_Attandance_list_adapter(browserList, new _5_Attandance_list_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                handleRowClick(item);
            }
        });
        rvDatabaseBrowser.setAdapter(adapter);

        loadCourses();

        btnBackLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackLevel();
            }
        });
    }

    private void handleRowClick(String item) {
        if (currentLevel == 0) {
            selectedCourse = item;
            currentLevel = 1;
            loadSessions();
        } else if (currentLevel == 1) {
            selectedSession = item;
            currentLevel = 2;
            loadDates();
        } else if (currentLevel == 2) {
            selectedDate = item;
            currentLevel = 3;
            loadPresentStudents();
        }
    }

    private void loadCourses() {
        currentLevel = 0;
        tvCurrentLevel.setText("Select Course");
        btnBackLevel.setVisibility(View.GONE);

        mDatabase.child("courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                browserList.clear();
                for (DataSnapshot courseSnap : snapshot.getChildren()) {
                    browserList.add(courseSnap.getKey());
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadSessions() {
        tvCurrentLevel.setText("Course: " + selectedCourse + " | Select Session");
        btnBackLevel.setVisibility(View.VISIBLE);

        mDatabase.child("attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                browserList.clear();
                for (DataSnapshot dateSnap : snapshot.getChildren()) {
                    if (dateSnap.child(selectedCourse).exists()) {
                        for (DataSnapshot sessSnap : dateSnap.child(selectedCourse).getChildren()) {
                            String sessionKey = sessSnap.getKey();
                            if (!browserList.contains(sessionKey)) {
                                browserList.add(sessionKey);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadDates() {
        tvCurrentLevel.setText("Session: " + selectedSession + " | Select Date");

        mDatabase.child("attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                browserList.clear();
                for (DataSnapshot dateSnap : snapshot.getChildren()) {
                    if (dateSnap.child(selectedCourse).child(selectedSession).exists()) {
                        browserList.add(dateSnap.getKey());
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadPresentStudents() {
        tvCurrentLevel.setText("Date: " + selectedDate + " | Present Students");

        mDatabase.child("attendance").child(selectedDate).child(selectedCourse).child(selectedSession)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot attendanceSnapshot) {
                        List<String> presentPunchIds = new ArrayList<>();
                        for (DataSnapshot stdSnap : attendanceSnapshot.getChildren()) {
                            presentPunchIds.add(stdSnap.getKey());
                        }

                        mDatabase.child("student").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot studentSnapshot) {
                                browserList.clear();
                                for (String punchId : presentPunchIds) {
                                    if (studentSnapshot.child(punchId).exists()) {
                                        String name = String.valueOf(studentSnapshot.child(punchId).child("name").getValue());
                                        String studentId = String.valueOf(studentSnapshot.child(punchId).child("id").getValue());
                                        browserList.add(studentId + " - " + name);
                                    } else {
                                        browserList.add("Unknown Card: " + punchId);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void goBackLevel() {
        if (currentLevel == 3) {
            currentLevel = 2;
            loadDates();
        } else if (currentLevel == 2) {
            currentLevel = 1;
            loadSessions();
        } else if (currentLevel == 1) {
            currentLevel = 0;
            loadCourses();
        }
    }
}