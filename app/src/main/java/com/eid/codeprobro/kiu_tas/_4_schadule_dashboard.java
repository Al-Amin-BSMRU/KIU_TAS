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

public class _4_schadule_dashboard extends AppCompatActivity implements View.OnClickListener {

    EditText Course_Name, Course_Code, Total_Student, session;
    Button starts, DbStructure;
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

        // find buttons
        DbStructure = findViewById(R.id.DbStructure);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent in = getIntent();
        if (in != null) {
            uni_name = in.getStringExtra("uni_name");
            name = in.getStringExtra("name");
            idi = in.getStringExtra("id");
            depertment = in.getStringExtra("depertment");
        }
        starts.setOnClickListener(this);
        DbStructure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.starts) {
            String cName = Course_Name.getText().toString().trim();
//            CS 321--->CS_321
            String cCode = Course_Code.getText().toString().trim().toUpperCase().replace(" ", "_");
            String sessInput = session.getText().toString().trim();
            String totalStd = Total_Student.getText().toString().trim();

            if (cName.isEmpty() || cCode.isEmpty() || sessInput.isEmpty() || totalStd.isEmpty()) {
                Toast.makeText(this, "Please fill all info to start class!", Toast.LENGTH_SHORT).show();
                return;
            }

            starts.setText("Initializing System...");
//            set the class config status:false   no class
            starts.setEnabled(false);

            String activeUni = (uni_name != null && !uni_name.isEmpty()) ? uni_name : "OX";
            mDatabase.child(activeUni).child("Current_Class_Config").child("Course_Code").setValue(cCode);
            mDatabase.child(activeUni).child(depertment).child("courses").child(cCode).child("total_students").setValue(totalStd);

            fetchFirebaseServerDate(cCode);
        }
//  redirect to the firebase pages
        if(v.getId() == R.id.DbStructure){
            Intent in = new Intent(_4_schadule_dashboard.this, _5_database_link.class);
            startActivity(in);
        }
    }
//     set the nodes date on the firebase real date
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

                        timeRef.removeValue();
                        starts.setText("START");
                        starts.setEnabled(true);

                        Intent in = new Intent(_4_schadule_dashboard.this, _5_student_attandance.class);
                        in.putExtra("SERVER_DATE_KEY", secureRealTimeDate);
                        in.putExtra("COURSE_CODE_KEY", optimizedCourseCode);

                        String sessionInput = session.getText().toString().trim();
                        if (sessionInput.length() >= 4) {
                            String firstFourDigits = sessionInput.substring(0, 4);
                            sessionInput = firstFourDigits.substring(2, 4);
                        }

                        mDatabase.child(activeUni).child("Current_Class_Config").child("Class_Session").setValue(sessionInput);

                        in.putExtra("SESSION_KEY", sessionInput);
                        in.putExtra("depertment", depertment);
                        in.putExtra("uni_name", activeUni);
                        startActivity(in);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                starts.setEnabled(true);
                starts.setText("START");
                Toast.makeText(_4_schadule_dashboard.this, "Database Error! Try Again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}