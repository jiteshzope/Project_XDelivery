package com.xoraano.deliveryboy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.xoraano.deliveryboy.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xoraano.deliveryboy.Common.Common;
import com.xoraano.deliveryboy.Database.MyDeliveryBoyInfoDataBase;
import com.xoraano.deliveryboy.Model.DeliveryPerson;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText login_phone,login_password;
    Button btnLogin;

    FirebaseDatabase database ;
    DatabaseReference deliveryPersons,registeredDeliveryPersonPhones;


    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    String verification_code;

    ProgressDialog mDialogOtp;

    Map<String,Boolean> list_registered_deliveryPersonPhones ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_phone = (MaterialEditText)findViewById(R.id.login_phone);
        login_password = (MaterialEditText)findViewById(R.id.login_password);

        btnLogin = findViewById(R.id.btn_login);

        database = FirebaseDatabase.getInstance();
        deliveryPersons = database.getReference("deliveryPersons");
        registeredDeliveryPersonPhones = database.getReference("list_registered_deliveryPersonPhones");

        auth=FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!=null) {
            final DeliveryPerson databaseperson = new MyDeliveryBoyInfoDataBase(getApplicationContext()).getDeliveryPerson();
            if(databaseperson.getMobileNo().equals(auth.getCurrentUser().getPhoneNumber())) {

                final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("Loading...");
                mDialog.show();

                registeredDeliveryPersonPhones.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<Map<String, Boolean>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Boolean>>() {
                        };
                        list_registered_deliveryPersonPhones = dataSnapshot.getValue(genericTypeIndicator);


                        if(list_registered_deliveryPersonPhones.keySet().contains(databaseperson.getMobileNo())) {

                            deliveryPersons.child(auth.getCurrentUser().getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    mDialog.dismiss();
                                    DeliveryPerson person = dataSnapshot.getValue(DeliveryPerson.class);
                                    if (databaseperson.getPassword().equals(person.getPassword())) {
                                        if(person.getIs_blocked().equals("yes")){
                                            Toast.makeText(MainActivity.this, "You cannot login temporarily", Toast.LENGTH_SHORT).show();
                                        }else {

                                            Toast.makeText(MainActivity.this, "Delivery boy identified..", Toast.LENGTH_SHORT).show();
                                            Common.currentperson = person;
                                            Intent intent = new Intent(MainActivity.this, OrdersPage.class);
                                            startActivity(intent);
                                            finish();
                                        }


                                    } else {
                                        auth.signOut();
                                        new MyDeliveryBoyInfoDataBase(getApplicationContext()).clearInfo();
                                        Common.currentperson = null;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    mDialog.dismiss();
                                    auth.signOut();
                                    new MyDeliveryBoyInfoDataBase(getApplicationContext()).clearInfo();
                                    Common.currentperson = null;
                                }
                            });
                        }else {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "delivery boy not registered", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                            new MyDeliveryBoyInfoDataBase(getApplicationContext()).clearInfo();
                            Common.currentperson = null;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        auth.signOut();
                        mDialog.dismiss();
                        new MyDeliveryBoyInfoDataBase(getApplicationContext()).clearInfo();
                        Common.currentperson = null;
                    }
                });

            }else {

                auth.signOut();
                new MyDeliveryBoyInfoDataBase(getApplicationContext()).clearInfo();
                Common.currentperson=null;
            }
        }else {

            auth.signOut();
            new MyDeliveryBoyInfoDataBase(getApplicationContext()).clearInfo();
            Common.currentperson=null;
        }

        //otp codes
        auth = FirebaseAuth.getInstance();

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verification_code = s;
                Toast.makeText(MainActivity.this, "An OTP has been sent to your mobile no.", Toast.LENGTH_SHORT).show();
                mDialogOtp.dismiss();
                showEnterOtpDialog();

            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(login_phone.getText().toString().length()==10){
                    auth.signOut();
                    Common.currentperson=null;
                    mDialogOtp = new ProgressDialog(MainActivity.this);
                    mDialogOtp.setMessage("Wait for OTP....");
                    mDialogOtp.show();

                    send_sms("+91"+login_phone.getText().toString());
                }else {
                    Toast.makeText(MainActivity.this, "Enter correct 10 digit phone no.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void send_sms(String phoneNumber){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60, TimeUnit.SECONDS,this,mCallback);
    }

    public void signInWithPhone(PhoneAuthCredential credential){

        auth.signOut();

        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    registeredDeliveryPersonPhones.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<Map<String, Boolean>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Boolean>>() {
                            };
                            list_registered_deliveryPersonPhones = dataSnapshot.getValue(genericTypeIndicator);

                            if(list_registered_deliveryPersonPhones.keySet().contains(auth.getCurrentUser().getPhoneNumber())) {
                                mDialogOtp.dismiss();
                                deliveryPersons.child(auth.getCurrentUser().getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        DeliveryPerson deliveryPerson = dataSnapshot.getValue(DeliveryPerson.class);
                                        if (deliveryPerson.getPassword().equals(login_password.getText().toString())) {
                                            if(deliveryPerson.getIs_blocked().equals("yes")){
                                                Toast.makeText(MainActivity.this, "You cannot login temporarily", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Common.currentperson = deliveryPerson;
                                                SaveInfo();
                                            }

                                        } else {
                                            Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else {
                    Toast.makeText(MainActivity.this, "Mobile no. verification failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void verify(String input_code){

        verifyPhoneNumber(verification_code,input_code);
    }

    private void verifyPhoneNumber(String verification_code, String input_code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_code,input_code);
        signInWithPhone(credential);
    }

    private void showEnterOtpDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Mobile No. verification");
        alertDialog.setMessage("Enter OTP sent to your registered mobile no.");

        LayoutInflater inflater = this.getLayoutInflater();
        View enter_otp_dialog = inflater.inflate(R.layout.enter_otp_dialog,null);

        final EditText txtOTP = enter_otp_dialog.findViewById(R.id.txtOTP);

        alertDialog.setView(enter_otp_dialog);
        alertDialog.setIcon(R.drawable.ic_add_black_24dp);

        alertDialog.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                mDialogOtp = new ProgressDialog(MainActivity.this);
                mDialogOtp.setMessage("Please wait...........");
                mDialogOtp.show();
                verify(txtOTP.getText().toString());
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });



        alertDialog.show();

    }

    private void SaveInfo() {

        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();

        new MyDeliveryBoyInfoDataBase(getApplicationContext()).clearInfo();
        //DeliveryPerson person = new DeliveryPerson(Common.currentperson.getName(), Common.currentperson.getImage(), Common.currentperson.getMobileNo(), Common.currentperson.getPassword(), Common.currentperson.getPincode());
        new MyDeliveryBoyInfoDataBase(getApplicationContext()).saveInfo(Common.currentperson);

        Intent intent = new Intent(MainActivity.this, OrdersPage.class);
        startActivity(intent);
        finish();

    }
}
