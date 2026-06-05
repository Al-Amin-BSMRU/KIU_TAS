
package com.eid.codeprobro.kiu_tas;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class _2_Register_page extends AppCompatActivity implements View.OnClickListener {

    EditText University_Name, Name, id, Email, Password, Depertment, punchcard_no;
    Button Create;
    boolean boo = false;
    DatabaseReference db;
    FirebaseAuth mAuth;
    TextView registerlink;
    ProgressBar regProgressBar;
    private boolean isWaitingForVerification = false;

    // 🎯 রিডাইরেকশনের সময় পাস করার জন্য গ্লোবাল ভেরিয়েবল
    private String intentName, intentId, intentEmail, intentPassword, intentDept, intentPunch, intentUni;

    public void success_custom_toast() {
        LayoutInflater lf = getLayoutInflater();
        View v = lf.inflate(R.layout.success_reg_custom_toast, (ViewGroup) findViewById(R.id.cstm_toast));
        Toast tt = new Toast(getApplicationContext());
        tt.setDuration(Toast.LENGTH_LONG);
        tt.setGravity(Gravity.BOTTOM, 0, 0);
        tt.setView(v);
        tt.show();
    }

    public void fillinfo() {
        LayoutInflater li = getLayoutInflater();
        View v = li.inflate(R.layout.fill_info, (ViewGroup) findViewById(R.id.fill_cust));
        Toast tt = new Toast(getApplicationContext());
        tt.setDuration(Toast.LENGTH_SHORT);
        tt.setGravity(Gravity.BOTTOM, 0, 0);
        tt.setView(v);
        tt.show();
    }

    public void failed_custom_toast() {
        LayoutInflater lf = getLayoutInflater();
        View v = lf.inflate(R.layout.failed_reg_custom_toast, (ViewGroup) findViewById(R.id.failed_cstm_toast));
        Toast tt = new Toast(getApplicationContext());
        tt.setDuration(Toast.LENGTH_LONG);
        tt.setGravity(Gravity.BOTTOM, 0, 0);
        tt.setView(v);
        tt.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.register_page);

        mAuth = FirebaseAuth.getInstance();
        University_Name = findViewById(R.id.University_Name);
        Name = findViewById(R.id.Name);
        id = findViewById(R.id.id);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        Depertment = findViewById(R.id.Depertment);
        punchcard_no = findViewById(R.id.punchcard_no);
        Create = findViewById(R.id.Create);
        registerlink = findViewById(R.id.registerlink);
        regProgressBar = findViewById(R.id.regProgressBar);

        registerlink.setOnClickListener(this);
        Create.setOnClickListener(this);

        // pre registered and logged in system
        if (mAuth.getCurrentUser() != null) {
            if (mAuth.getCurrentUser().isEmailVerified()) {
                Intent in = new Intent(_2_Register_page.this, _3_second_page.class);
                Toast.makeText(_2_Register_page.this, "Welcome back", Toast.LENGTH_SHORT).show();
                startActivity(in);
                finish();
            } else {
                //hide create button to get varefied in email
                isWaitingForVerification = true;
                Create.setVisibility(View.GONE);
                Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isWaitingForVerification) {
            checkVerifiedAutomatic();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.registerlink) {
            Intent in = new Intent(_2_Register_page.this, _2_Login_page.class);
            startActivity(in);
            finish();
        }
        if (v.getId() == R.id.Create) {
            String uni_name = this.University_Name.getText().toString().trim().toUpperCase();
            String rawname = this.Name.getText().toString().trim();
            String rawidd = this.id.getText().toString().trim();
            String email = this.Email.getText().toString().trim();
            String password = this.Password.getText().toString().trim();
            String depertment = this.Depertment.getText().toString().trim().toUpperCase();
            String punch = this.punchcard_no.getText().toString().trim().toUpperCase();

            if (vlaidation_check(uni_name, rawname, rawidd, email, password, depertment, punch)) {
                startRegistrationProcess(uni_name, rawname, rawidd, email, password, depertment, punch);
            }
        }
    }

    public void checkVerifiedAutomatic() {
        FirebaseUser fu = mAuth.getCurrentUser();
        if (fu != null) {
            regProgressBar.setVisibility(View.VISIBLE); // progress bar visiable
            fu.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // object recall after the loading
                    FirebaseUser updatedUser = mAuth.getCurrentUser();

                    if (updatedUser != null && updatedUser.isEmailVerified()) {
                        regProgressBar.setVisibility(View.GONE);
                        Toast.makeText(_2_Register_page.this, "Verified Successfully!", Toast.LENGTH_SHORT).show();
                        isWaitingForVerification = false;

                        // send next pages
                        Intent in = new Intent(_2_Register_page.this, _3_second_page.class);
                        in.putExtra("name", intentName);
                        in.putExtra("id", intentId);
                        in.putExtra("email", intentEmail);
                        in.putExtra("password", intentPassword);
                        in.putExtra("depertment", intentDept);
                        in.putExtra("session", "");
                        in.putExtra("punch", intentPunch);
                        in.putExtra("uni_name", intentUni);

                        startActivity(in);
                        finish();
                    } else {
                        // not varefied
                        regProgressBar.setVisibility(View.GONE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    regProgressBar.setVisibility(View.GONE);
                    Toast.makeText(_2_Register_page.this, "Status Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public boolean vlaidation_check(String uni, String rwnam, String rwid, String eml, String pas, String dpt, String pun) {
        if (uni.isEmpty() || rwnam.isEmpty() || rwid.isEmpty() || eml.isEmpty() || pas.isEmpty() || dpt.isEmpty() || pun.isEmpty()) {
            fillinfo();
            return false;
        }
        if (rwid.length() != 12) {
            Toast.makeText(this, "Wrong ID! ID must be exactly 12 digits long", Toast.LENGTH_LONG).show();
            return false;
        }
        if (pas.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (uni.contains(".") || uni.contains("#") || uni.contains("$") ||
                dpt.contains(".") || dpt.contains("#") || dpt.contains("$") ||
                pun.contains(".") || pun.contains("#") || pun.contains("$")) {
            Toast.makeText(this, "Special characters (., #, $) are not allowed!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void startRegistrationProcess(String uni_name, String rawname, String rawidd, String email, String password, String depertment, String punch) {

        regProgressBar.setVisibility(View.VISIBLE);
        Create.setEnabled(true);// hold submit button

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                try {
                    String name = rawname.substring(0, 1).toUpperCase() + rawname.substring(1).toLowerCase();
                    String idd = rawidd.substring(0, 6);
                    String roll = rawidd.substring(rawidd.length() - 2);
                    String nam_ses_rol = name + "_" + idd + "__" + roll;
                    //save te iontents
            intentName = name;
            intentId = rawidd;
            intentEmail = email;
            intentPassword = password;
            intentDept = depertment;
            intentPunch = punch;
            intentUni = uni_name;
                    //database upload
                    _2_Register_Model ln = new _2_Register_Model(nam_ses_rol, punch, rawidd, rawname, uni_name, depertment, email, password);
                    db = FirebaseDatabase.getInstance().getReference(uni_name);

            String uid = mAuth.getCurrentUser().getUid();
            db.child("UID_Mappings").child(uid).child("department").setValue(depertment);
            db.child("UID_Mappings").child(uid).child("punch").setValue(punch);

                    db.child(depertment).child("teacher").child(punch).setValue(ln).addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            db.child("Card_Mappings").child(punch).setValue(depertment);
                            success_custom_toast();

                            FirebaseUser fu = mAuth.getCurrentUser();
                            if (fu != null) {
                                fu.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        regProgressBar.setVisibility(View.GONE);
                                        Toast.makeText(_2_Register_page.this, "Verification email sent! Check inbox.", Toast.LENGTH_LONG).show();
                                        Create.setVisibility(View.GONE); // বাটন হাইড
                                        isWaitingForVerification = true; // ফ্ল্যাগ অ্যাক্টিভেট
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        regProgressBar.setVisibility(View.GONE);
                                        Create.setEnabled(true);
                                        Toast.makeText(_2_Register_page.this, "Mail failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            regProgressBar.setVisibility(View.GONE);
                            Create.setEnabled(true);
                            failed_custom_toast();
                        }
                    });

                } catch (Exception e) {
                    regProgressBar.setVisibility(View.GONE);
                    Create.setEnabled(true);
                    Toast.makeText(_2_Register_page.this, "Data parsing error!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                regProgressBar.setVisibility(View.GONE);
                Create.setEnabled(true);
                failed_custom_toast();
                Toast.makeText(_2_Register_page.this, "Auth Failed: " + authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}