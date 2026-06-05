package com.eid.codeprobro.kiu_tas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class _2_Login_page extends AppCompatActivity implements View.OnClickListener {
    EditText email, Password, punchcard_no, Department;
    TextView loginlink,forgetPassword;
    Button login;
    DatabaseReference Dr;
    FirebaseAuth mAuth;
    boolean boo = false;
    ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        mAuth = FirebaseAuth.getInstance();
        forgetPassword=findViewById(R.id.forgetPassword);
        forgetPassword.setOnClickListener(this);
        email = findViewById(R.id.email);
        Password = findViewById(R.id.Password);
        login = findViewById(R.id.login);
        Department = findViewById(R.id.Department);
        punchcard_no = findViewById(R.id.punchcard_no);
        loginlink = findViewById(R.id.loginlink);
        loginProgressBar = findViewById(R.id.loginProgressBar);

        loginlink.setOnClickListener(this);
        login.setOnClickListener(this);

        if (mAuth.getCurrentUser() != null) {
            Intent in = new Intent(_2_Login_page.this, _3_second_page.class);
            Toast.makeText(_2_Login_page.this, "Welcome back", Toast.LENGTH_SHORT).show();
            startActivity(in);
            finish();
        }
    }

    public void success_custom_toast() {
        LayoutInflater lf = getLayoutInflater();
        View v = lf.inflate(R.layout.success_login_custom_toast, (ViewGroup) findViewById(R.id.cstm_toast));
        Toast tt = new Toast(getApplicationContext());
        tt.setDuration(Toast.LENGTH_LONG);
        tt.setGravity(Gravity.BOTTOM, 0, 0);
        tt.setView(v);
        tt.show();
    }

    public void failed_custom_toast() {
        LayoutInflater lf = getLayoutInflater();
        View v = lf.inflate(R.layout.failed_login_custom_toast, (ViewGroup) findViewById(R.id.failed_cstm_toast));
        Toast tt = new Toast(getApplicationContext());
        tt.setDuration(Toast.LENGTH_LONG);
        tt.setGravity(Gravity.BOTTOM, 0, 0);
        tt.setView(v);
        tt.show();
    }

    public boolean vlaidation_check(String em, String pas, String pun, String dpt) {
        if (em.isEmpty() || dpt.isEmpty() || pun.isEmpty() || pas.isEmpty()) {
            return boo;
        }
        if (!em.contains("@")) {
            Toast.makeText(this, "Wrong email! email must contains @", Toast.LENGTH_LONG).show();
            return boo;
        }
        if (dpt.contains(".") || dpt.contains("#") || dpt.contains("$") ||
                pun.contains(".") || pun.contains("#") || pun.contains("$")) {
            Toast.makeText(this, "Special characters (., #, $) are not allowed!", Toast.LENGTH_LONG).show();
            return boo;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginlink) {
            Intent in = new Intent(_2_Login_page.this, _2_Register_page.class);
            startActivity(in);
        }
        if (v.getId() == R.id.forgetPassword) {
            EditText et = new EditText(v.getContext());  //creating edit text
            AlertDialog.Builder ab = new AlertDialog.Builder(v.getContext());   //initializiong alert box
            ab.setTitle("Reset Password?");    //setting alert box title
            ab.setMessage("Enter your mail address.To reset password");// setting alert box message
            ab.setView(et);   //setting the edit button inside the alert box
            ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                /// setting the yes commands
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String mail = et.getText().toString();

                    mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        /// fb auth send ther reset emails
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(_2_Login_page.this, "check your email to get the reset link.", Toast.LENGTH_LONG).show();
                        }

                        ;
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(_2_Login_page.this, "Failed to sent the reset link." + e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });
                }
            });
            ab.create().show();   //creating the alert box and showing
        }
        if (v.getId() == R.id.login) {
            String eml = email.getText().toString().trim();
            String pas = Password.getText().toString().trim();
            String pun = punchcard_no.getText().toString().trim().toUpperCase();
            String dep = Department.getText().toString().trim().toUpperCase();
            if (vlaidation_check(eml, pas, pun, dep)) {
                upload_fb_auth(eml, pas, dep, pun);
            }
        }
    }

    public void upload_fb_auth(String eml, String pass, final String dep, final String pun) {

        loginProgressBar.setVisibility(View.VISIBLE);
        login.setEnabled(false);

        mAuth.signInWithEmailAndPassword(eml, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                loginProgressBar.setVisibility(View.GONE);
                login.setEnabled(true);

                if (task.isSuccessful()) {
                    String uid = mAuth.getCurrentUser().getUid();
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("OX").child("UID_Mappings").child(uid);
                    dbRef.child("department").setValue(dep);
                    dbRef.child("punch").setValue(pun);

                    success_custom_toast();
                    Toast.makeText(_2_Login_page.this, "successfully Loged in ", Toast.LENGTH_SHORT).show();

                    Intent in = new Intent(_2_Login_page.this, _3_second_page.class);
                    in.putExtra("deprt", dep);
                    in.putExtra("punch", pun);
                    startActivity(in);
                    finish();
                } else {
                    failed_custom_toast();
                    Toast.makeText(_2_Login_page.this, "Failed to login: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}