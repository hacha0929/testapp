package com.vishwas.testapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;

import static android.content.ContentValues.TAG;
import static com.vishwas.testapp.MainActivity.compressedImageFile;
import static com.vishwas.testapp.MainActivity.king;
import static com.vishwas.testapp.MainActivity.mobileno;


public class Mobile extends Fragment {

    private Context mCon;
    private EditText ed_no;
    private Button checkno;
    private visitor visit;
    private boolean result;
    private CameraKitView cameraKitView;
    public ProgressDialog dialog;



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraKitView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        cameraKitView.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_mobile, container, false);
        mCon=getActivity();
        ed_no=(EditText)view.findViewById(R.id.mobile_no);
        checkno=(Button)view.findViewById(R.id.checkno);
        cameraKitView = view.findViewById(R.id.camera);
        checkno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileno=ed_no.getText().toString();
                if(mobileno.length()<10)
                {
                    ed_no.setError("Enter Valid Mobile Number ");
                    ed_no.requestFocus();

                    return;
                }

                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, byte[] bytes) {
                        File savedPhoto = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
                        try {
                            FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                            outputStream.write(bytes);
                            outputStream.close();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            compressedImageFile=new Compressor(getContext()).compressToBitmap(savedPhoto);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                dialog=new ProgressDialog(getContext());
                dialog.setTitle("Verifying Credentials");
                dialog.setMessage("Please wait");
                dialog.show();

                new CountDownTimer(3000, 1000) {
                    public void onFinish() {
                        checkifexists(mobileno);

                    }

                    public void onTick(long millisUntilFinished) {

                    }
                }.start();
            }
        });

        return view;
    }

    public void checkifexists(final String mobileno)
    {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference ref = database.child("visitors");
        Query query = database.child("visitors").orderByChild("mobileno").equalTo(mobileno);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                visitor k=dataSnapshot.getValue(visitor.class);
                if(dataSnapshot.exists())
                {

                    king=String.valueOf(dataSnapshot.getValue());
                    ((MainActivity)mCon).splitkey();
                     dialog.dismiss();
                    ((MainActivity)mCon).replaceFragment(new Registered());
                    result=true;

                }
                else
                {
                    dialog.dismiss();
                    ((MainActivity)mCon).replaceFragment(new Otp());
                    result=false;
                    //Toast.makeText(getContext(),String.valueOf(result),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
