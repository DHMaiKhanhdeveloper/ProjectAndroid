package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextRegisterFullName, editTextRegisterEmail,
            editTextRegisterDoB,editTextRegisterMobile,editTextRegiterPass,editRegisterConfirmPass;
    private ProgressBar progressBar;
    private RadioButton radioButtonRegisterGenderSelected;
    private RadioGroup radioGroupRegisterGender;
    private static final String TAG="RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register");
        Toast.makeText(RegisterActivity.this, "You can register now",Toast.LENGTH_LONG).show();

        editTextRegisterFullName =findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail =findViewById(R.id.editText_register_email);
        editTextRegisterDoB =findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegiterPass = findViewById(R.id.editText_register_password);
        editRegisterConfirmPass = findViewById(R.id.editText_register_confirm_password);
        progressBar = findViewById(R.id.progressBar);

        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();


        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDoB = editTextRegisterDoB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPassword = editTextRegiterPass.getText().toString();
                String textConfirmPass = editRegisterConfirmPass.getText().toString();
                String textGender;

                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(RegisterActivity.this,"Please enter your full name",Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full Name is required");
                    editTextRegisterFullName.requestFocus();// yeu cau nhap lai
                } else if (TextUtils.isEmpty((textEmail))){
                    Toast.makeText(RegisterActivity.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) { // khác true
                    Toast.makeText(RegisterActivity.this,"Please re-enter your email",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegisterActivity.this,"Please enter date of birth",Toast.LENGTH_LONG).show();
                    editTextRegisterDoB.setError("Date of birth is required");
                    editTextRegisterDoB.requestFocus();
                }else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1){
                    Toast.makeText(RegisterActivity.this,"Please select your gender",Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();
                }else if(TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisterActivity.this,"Please enter your mobile ",Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile is required");
                    editTextRegisterMobile.requestFocus();
                }else if(textMobile.length() !=10){
                    Toast.makeText(RegisterActivity.this,"Please re-enter your mobile ",Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile shoule be 10 digits");
                    editTextRegisterMobile.requestFocus();
                }else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(RegisterActivity.this,"Please enter your password ",Toast.LENGTH_LONG).show();
                    editTextRegiterPass.setError("Password is required");
                    editTextRegiterPass.requestFocus();
                }else if(textPassword.length()<6){
                    Toast.makeText(RegisterActivity.this,"Please should be at least 6 digits ",Toast.LENGTH_LONG).show();
                    editTextRegiterPass.setError("Password too weak");
                    editTextRegiterPass.requestFocus();
                }else if (TextUtils.isEmpty(textConfirmPass)){
                    Toast.makeText(RegisterActivity.this,"Please enter your confirm password ",Toast.LENGTH_LONG).show();
                    editRegisterConfirmPass.setError("Password Confirmation is required");
                    editRegisterConfirmPass.requestFocus();
                }else if (!textPassword.equals(textConfirmPass)){
                    Toast.makeText(RegisterActivity.this,"Please enter same password ",Toast.LENGTH_LONG).show();
                    editRegisterConfirmPass.setError("Password Confirmation is required");
                    editTextRegiterPass.clearComposingText(); // xoa nhap lai
                    editRegisterConfirmPass.clearComposingText();
                }else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName,textEmail,textDoB,textMobile,textGender,textPassword);
                }
            }

            private void registerUser(String textFullName, String textEmail, String textDoB, String textMobile, String textGender, String textPassword) {
                FirebaseAuth auth = FirebaseAuth.getInstance(); // xac thuc firebase
                // tạo user profile
                auth.createUserWithEmailAndPassword(textEmail,textPassword).addOnCompleteListener(RegisterActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    FirebaseUser firebaseUser =auth.getCurrentUser(); // auth la bien xac thuc firebase

                                    //Cập nhật hiện tên user
                                    UserProfileChangeRequest profileChangeRequest= new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                                    firebaseUser.updateProfile(profileChangeRequest);

                                    //Nhap du lieu vao Firebase Realtime Database java object
                                    ReadWriteUserDetails writerUserDetails = new ReadWriteUserDetails(textDoB,textGender,textMobile);

                                    //trích xuất tham chiếu người dùng từ cơ sở dữ liệu để "đăng ký người dùng"
                                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                                    referenceProfile.child(firebaseUser.getUid()).setValue(writerUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                // Gui xac nhan Email
                                                firebaseUser.sendEmailVerification();

                                                Toast.makeText(RegisterActivity.this,"User registered successfully, Please vertify your email",Toast.LENGTH_LONG).show();
                                                //Mo ho so nguoi dung khi dang ki thanh cong
//                                          Intent intent = new Intent(RegisterActivity.this,UserProfileActivity.class);
//                                          // Ngan nguoi dung dang ki thanh cong khong quay lai dang ki lai lan nua , nguoi dung dang ki thanh cong se chuyen den trang ho so
//                                          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                                          startActivity(intent);
//                                          finish(); // dong hoat dong Register
                                            }else{
                                                Toast.makeText(RegisterActivity.this,"User registered failed, Please try again",Toast.LENGTH_LONG).show();

                                            }
                                            // ẩn progressbar dù có đăng nhập thành công hay thất bại
                                            progressBar.setVisibility(View.GONE);

                                        }
                                    });


                                }else{
                                    try{
                                        throw task.getException(); // java exception
                                    } catch (FirebaseAuthWeakPasswordException e){
                                        editTextRegiterPass.setError("Your password is too weak. Kindly use a mix of alphabets, numbers");
                                        editRegisterConfirmPass.requestFocus();
                                    } catch (FirebaseAuthInvalidCredentialsException e){ // Trường hợp ngoại lệ thông tin đăng nhập không hợp lệ của Firebase Auth
                                        editTextRegiterPass.setError("Your email is invalid or already in use. Kindly re-enter");
                                        editTextRegiterPass.requestFocus();
                                    }catch (FirebaseAuthUserCollisionException e){ // Ngoại lệ va chạm người dùng
                                        editTextRegiterPass.setError("User is already registered with this email. Use another email.");
                                        editTextRegiterPass.requestFocus();
                                    }catch (Exception e){
                                        Log.e(TAG, e.getMessage());
                                        Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
           }


        });
    }
}