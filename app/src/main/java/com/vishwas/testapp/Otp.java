package com.vishwas.testapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static android.support.constraint.Constraints.TAG;
import static com.vishwas.testapp.MainActivity.compressedImageFile;
import static com.vishwas.testapp.MainActivity.mobileno;


public class Otp extends Fragment {

    private Context mCon;
    private FirebaseAuth mAuth;
    private EditText ot;
    private TextView welcome;
    private String mVerificationId;
    private Button verify;
    public FirebaseStorage storage=FirebaseStorage.getInstance();
    private DatabaseReference mReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_otp, container, false);
        verify=(Button)view.findViewById(R.id.verifyotp);
        mCon=getActivity();
        mAuth = FirebaseAuth.getInstance();
        ot=(EditText)view.findViewById(R.id.otp);
        welcome=(TextView) view.findViewById(R.id.Welcome);
        sendVerificationCode(MainActivity.mobileno);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=ot.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            }
        });
        return view;
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {


        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            Toast.makeText(getContext(),"Verification Code Sent",Toast.LENGTH_LONG).show();
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            uploading("visitors");
                        }
                        else {
                            Toast.makeText(getContext(), "OTP entered is incorrect", Toast.LENGTH_LONG).show();
                            uploading("suspicious_users");
                            }
                        }
                    });
    }

    private void uploading(final String update)
    {
        StorageReference storageReference=storage.getReference();
        final StorageReference mountainref=storageReference.child(String.valueOf(String.valueOf(mobileno)));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        compressedImageFile.recycle();
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setTitle("Wait Till Image is Uploaded");
        dialog.setMessage("Please wait");
        dialog.show();
        UploadTask uploadTask = mountainref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if(taskSnapshot.getMetadata()!=null)
                {
                    if(taskSnapshot.getMetadata().getReference()!=null)
                    {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                mReference = FirebaseDatabase.getInstance().getReference().child(update);
                                if(update=="suspicious_users")
                                {
                                suspicious sus=new suspicious(mobileno,imageUrl);
                                mReference.push().setValue(sus);
                                ((MainActivity)mCon).replaceFragment(new Mobile());}
                                else
                                {
                                    visitor k=new visitor(imageUrl,mobileno,1);
                                    mReference.push().setValue(k);
                                    welcome.setText("Congratulation's\nNew Visitor Saved");
                                }
                            }
                        });
                    }
                }
                dialog.dismiss();

//

            }
        });


    }

}
