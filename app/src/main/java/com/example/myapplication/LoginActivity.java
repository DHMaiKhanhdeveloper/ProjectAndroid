package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail , editTextLoginPassword;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");
        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPassword = findViewById(R.id.editText_login_password);

        progressBar = findViewById(R.id.progressBar);
        authProfile = FirebaseAuth.getInstance();

        ImageView imageViewShowHidepassword = findViewById(R.id.imageView_show_hide_password);
        imageViewShowHidepassword.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  kiểm tra mật khẩu có hiển thi
                if(editTextLoginPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //Nếu mật khẩu hiển thị thì hãy ẩn mật khẩu
                    editTextLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // thay đổi icon ẩn
                    imageViewShowHidepassword.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    //mật khẩu có hiện thị
                    editTextLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    // thay đổi icon hiện
                    imageViewShowHidepassword.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPassword = editTextLoginPassword.getText().toString();
                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this, "Please Enter Your Emai", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LoginActivity.this,"Please re_enter your email",Toast.LENGTH_LONG).show();
                    editTextLoginEmail.setError("Valid Email is required");
                    editTextLoginEmail.requestFocus();
                }else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(LoginActivity.this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Password is required");
                    editTextLoginEmail.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginuser(textEmail,textPassword);
                }
            }
        });
    }

    private void loginuser(String Email, String Password) {
        authProfile.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "You are logged in now", Toast.LENGTH_SHORT).show();
                }else {
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setError("User does not exists or is no longer valid.Please register again");
                        editTextLoginEmail.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        editTextLoginEmail.setError("Invalid credentials. Kindly , check and re-enter");
                        editTextLoginEmail.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG , e.getMessage());
                        Toast.makeText(LoginActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}