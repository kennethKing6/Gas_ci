package com.example.gasci;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.entire.sammalik.samlocationandgeocoding.SamLocationRequestService;
import com.example.gasci.models.Magasin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.omarshehe.forminputkotlin.FormInputAutoComplete;
import com.omarshehe.forminputkotlin.FormInputText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String CITIES = "cities";
    public static final String INFO_BUSINESS = "infoBusiness";
    public static final String INFO_USER_SHARED_PREF_KEY = "info_user";
    SamLocationRequestService samLocationRequestService;

    FormInputText textView;
    private FirebaseFirestore firestoredb;

    // binding views
    @BindView(R.id.nom_magazin)
    FormInputText nomMagazinView;
    @BindView(R.id.prenom)
    FormInputText prenomView;
    @BindView(R.id.ville)
    TextView villeTextView;
    @BindView(R.id.commune)
    FormInputAutoComplete communeView;
    @BindView(R.id.nomQuartier)
    FormInputText quartierView;
    @BindView(R.id.phoneNumber)
    FormInputText numeroView;

    //Shared preference

    SharedPreferences sharedPref;

    DocumentReference document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getPreferences(Context.MODE_PRIVATE);

        ButterKnife.bind(this);
        firestoredb = FirebaseFirestore.getInstance();
        textView = findViewById(R.id.nom_magazin);


    }


    private void findLocation() {
        samLocationRequestService = new SamLocationRequestService(MainActivity.this, new SamLocationRequestService.SamLocationListener() {
            @Override
            public void onLocationUpdate(Location location, Address address) {
                Log.e(TAG, "onLocationUpdate: " + address.getLocality());
                textView.setValue(address.getLocality());
            }
        }, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            samLocationRequestService.startLocationUpdates();
            Log.e(TAG, "Voir");
        }
    }

    public void registerMagasin(View view) {

        //TODO: validate the values inside before submitting to the database

        CollectionReference collection = firestoredb.collection(INFO_BUSINESS);

        if (sharedPref.getString(INFO_USER_SHARED_PREF_KEY, "").isEmpty()) {
            document = collection.document();

        } else {
            document = collection.document(sharedPref.getString(INFO_USER_SHARED_PREF_KEY, ""));
        }


        document.set(new Magasin(nomMagazinView.getValue(),
                prenomView.getValue(), villeTextView.getText().toString(), communeView.getValue(),
                quartierView.getValue(), numeroView.getValue())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "onComplete: document created");
                    Toast.makeText(MainActivity.this, "Finished posting", Toast.LENGTH_SHORT).show();

                    if (sharedPref.getString(INFO_USER_SHARED_PREF_KEY, "").isEmpty()) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(INFO_USER_SHARED_PREF_KEY, document.getId());
                        editor.apply();
                    }
                } else {
                    Log.e(TAG, "onComplete: document not created " + task.getResult());
                }
            }
        });


    }
}
