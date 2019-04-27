package com.vishwas.testapp;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    public static Bitmap compressedImageFile;
    public static String mobileno;
    public static String king;
    public String county;
    public static int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        replaceFragment(new Mobile());
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void splitkey()
    {
        String key[]=king.split("=");
        king=key[0].substring(1,key[0].length());
        county=key[key.length-1].substring(0,key[key.length-1].length()-2);
        a=Integer.valueOf(county);
        a++;
        Log.e("dj",king);
        Log.e("hello",county);
        FirebaseDatabase data=FirebaseDatabase.getInstance();
        DatabaseReference ref = data.getReference().child("visitors").child(king);
        ref.child("visitcount").setValue(String.valueOf(a));
    }


}
