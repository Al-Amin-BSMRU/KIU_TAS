package com.eid.codeprobro.kiu_tas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class _3_second_page extends AppCompatActivity implements View.OnClickListener {

    TextView University_Name, Name, id, Email, Password, Depertment, punchcard_no;
    Button register, schadule,logoutBtn;
    String uni_name, name, idi, email, password, depertment, apunchid;
    DatabaseReference db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.second_page);

        mAuth = FirebaseAuth.getInstance();
        /// //hardcoded uniname
        uni_name = "OX";

        University_Name = findViewById(R.id.University_Name);
        Name = findViewById(R.id.Name);
        id = findViewById(R.id.id);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        Depertment = findViewById(R.id.Depertment);
        punchcard_no = findViewById(R.id.punchcard_no);
        register = findViewById(R.id.register);
        schadule = findViewById(R.id.schadule);
        logoutBtn=findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(this);
        register.setOnClickListener(this);
        schadule.setOnClickListener(this);

        Intent in = getIntent();

        if (in != null && (in.getStringExtra("name") != null)) {
            name = in.getStringExtra("name");
            idi = in.getStringExtra("id");
            email = in.getStringExtra("email");
            password = in.getStringExtra("password");
            depertment = in.getStringExtra("depertment");
            apunchid = in.getStringExtra("punch");

            displayUiData();

        }
        //  manual login pages have only email ,punchno,password,dept
        else if (in != null && in.getStringExtra("deprt") != null) {
            String department = in.getStringExtra("deprt");
            apunchid = in.getStringExtra("punch");
            fetchTeacherData(department, apunchid);
        }
        else if (mAuth.getCurrentUser() != null) {
            //getting from the firebaaaaase  dB;
            String uid = mAuth.getCurrentUser().getUid();
            DatabaseReference uidRef = FirebaseDatabase.getInstance().getReference(uni_name).child("UID_Mappings").child(uid);

            uidRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        /// //.snapsot was id:78bBiiG5HWTmjA6cBGIKHbM1otz2 its  snap dept & punch
                        String dept = snapshot.child("department").getValue(String.class); ////string class for the LAW and the punch id was string
                        String punch = snapshot.child("punch").getValue(String.class);

                        if (dept != null && punch != null) {
                            fetchTeacherData(dept, punch);
                        }
                    } else {
                        Toast.makeText(_3_second_page.this, "Session Expired! Please login again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    private void fetchTeacherData(String dept, String punch) {
        db = FirebaseDatabase.getInstance().getReference(uni_name);
        db.child(dept).child("teacher").child(punch).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    _2_Register_Model teach_info = snapshot.getValue(_2_Register_Model.class);
                    if (teach_info != null) {
                        name = teach_info.getName();
                        idi = teach_info.getId();
                        email = teach_info.getEmail();
                        password = teach_info.getPassword();
                        depertment = teach_info.getDept();
                        apunchid = teach_info.getPuid();

                        displayUiData();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void displayUiData() {
        University_Name.setText(uni_name);
        punchcard_no.setText(apunchid);
        Name.setText(name);
        id.setText(idi);
        Email.setText(email);
        Password.setText(password);
        Depertment.setText(depertment);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            showLogoutConfirmationDialog();
        }
if(v.getId()==R.id.logoutBtn){
    TextView tv= new TextView(v.getContext());
    AlertDialog.Builder ab=new AlertDialog.Builder(v.getContext());
    ab.setTitle("you wants to logout?");
    ab.setMessage("you will be loged out and redirect to the register page");
    ab.setPositiveButton("yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mAuth.signOut();
            Intent in =new Intent(_3_second_page.this,_2_Register_page.class);
            startActivity(in);
        }
    }).setNegativeButton("no", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    });

    ab.create().show();
}
        if (v.getId() == R.id.schadule) {
            if (apunchid != null) {
                Intent in = new Intent(_3_second_page.this, _4_schadule_dashboard.class);
                in.putExtra("uni_name", uni_name);
                in.putExtra("name", name);
                in.putExtra("id", idi);
                in.putExtra("depertment", depertment);
                in.putExtra("password", password);
                in.putExtra("punch", apunchid);
                startActivity(in);
            } else {
                Toast.makeText(this, "Data is loading, please wait...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showLogoutConfirmationDialog() {
        //popup
        AlertDialog.Builder builder = new AlertDialog.Builder(_3_second_page.this);
        builder.setTitle("Action Required");
        builder.setMessage("You are currently logged in. What would you like to do?");
        builder.setCancelable(true);


        builder.setPositiveButton("Logout & Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /// //////sing out logics of firebase
                FirebaseAuth.getInstance().signOut();
                Intent in = new Intent(_3_second_page.this, _2_Register_page.class);
                startActivity(in);
                Toast.makeText(_3_second_page.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNeutralButton("Switch to Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //////////logout condition
                FirebaseAuth.getInstance().signOut();
                Intent in = new Intent(_3_second_page.this, _2_Login_page.class);
                startActivity(in);
                Toast.makeText(_3_second_page.this, "Logged out! Please login again", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // close popups
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}