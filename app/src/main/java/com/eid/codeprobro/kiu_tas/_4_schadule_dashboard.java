
package com.eid.codeprobro.kiu_tas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class _4_schadule_dashboard extends AppCompatActivity implements View.OnClickListener {

    EditText Course_Name, Course_Code, Total_Student, session;
    Button starts_btn, Attandance_list;
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
        starts_btn = findViewById(R.id.starts);
        Attandance_list = findViewById(R.id.Attandance_list);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent in = getIntent();
        if (in != null) {
            uni_name = in.getStringExtra("uni_name");
            name = in.getStringExtra("name");
            idi = in.getStringExtra("id");
            depertment = in.getStringExtra("depertment");
        }
        starts_btn.setOnClickListener(this);
        Attandance_list.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.starts) {
            String cName = Course_Name.getText().toString().trim();
            String cCode = Course_Code.getText().toString().trim().toUpperCase().replace(" ", "_");
            String sessInput = session.getText().toString().trim();
            String totalStd = Total_Student.getText().toString().trim();

            if (cName.isEmpty() || cCode.isEmpty() || sessInput.isEmpty() || totalStd.isEmpty()) {
                Toast.makeText(this, "Please fill all info to start class!", Toast.LENGTH_SHORT).show();
                return;
            }

            starts_btn.setText("Initializing System...");
            starts_btn.setEnabled(false);

            String activeUni;
            if (uni_name != null && !uni_name.isEmpty()) {
                activeUni = uni_name;
            } else {
                activeUni = "OX";
            }

            mDatabase.child(activeUni).child("Current_Class_Config").child("Course_Code").setValue(cCode);
            mDatabase.child(activeUni).child("Current_Class_Config").child("Is_Locked").setValue(false);

            mDatabase.child(activeUni).child(depertment).child("courses").child(cCode).child("total_students").setValue(totalStd);

            startAttendanceWithPhoneDate(cCode);
        }

        if (v.getId() == R.id.Attandance_list) {
            Intent in = new Intent(_4_schadule_dashboard.this, _5_Attandance_list.class);

            if (uni_name != null) {
                in.putExtra("uni_name", uni_name);
            } else {
                in.putExtra("uni_name", "OX");
            }

            if (depertment != null) {
                in.putExtra("depertment", depertment);
            } else {
                in.putExtra("depertment", "CSE");
            }

            startActivity(in);
        }
    }

    private void startAttendanceWithPhoneDate(final String optimizedCourseCode) {
        String activeUni;
        if (uni_name != null && !uni_name.isEmpty()) {
            activeUni = uni_name;
        } else {
            activeUni = "OX";
        }

        String activeDept;
        if (depertment != null && !depertment.isEmpty()) {
            activeDept = depertment;
        } else {
            activeDept = "CSE";
        }
   /// ///featch date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String secureRealTimeDate = sdf.format(new Date());

        String sessionInput = session.getText().toString().trim();
        if (sessionInput.length() >= 4) {
            String firstFourDigits = sessionInput.substring(0, 4);
            sessionInput = firstFourDigits.substring(2, 4);
        }

        DatabaseReference configRef = mDatabase.child(activeUni).child("Current_Class_Config");
        configRef.child("Class_Session").setValue(sessionInput);
        configRef.child("Class_Date").setValue(secureRealTimeDate);
        configRef.child("Department").setValue(activeDept);

        starts_btn.setText("START");
        starts_btn.setEnabled(true);

        Intent in = new Intent(_4_schadule_dashboard.this, _5_student_attandance.class);
        in.putExtra("SERVER_DATE_KEY", secureRealTimeDate);
        in.putExtra("COURSE_CODE_KEY", optimizedCourseCode);
        in.putExtra("SESSION_KEY", sessionInput);
        in.putExtra("depertment", activeDept);
        in.putExtra("uni_name", activeUni);

        startActivity(in);
    }
}