package com.example.gasci;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.entire.sammalik.samlocationandgeocoding.SamLocationRequestService;
import com.example.gasci.models.Magasin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omarshehe.forminputkotlin.FormInputAutoComplete;
import com.omarshehe.forminputkotlin.FormInputText;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class BusinessDetails extends AppCompatActivity {

    public static final String TAG = BusinessDetails.class.getSimpleName();
    public static final String CITIES = "cities";
    public static final String INFO_BUSINESS = "infoBusiness";
    public static final String INFO_USER_SHARED_PREF_KEY = "info_user";
    public static final String USER_BUSINESS_INFO = "userBusinessInfo";
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

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.business_details_layout)
    ScrollView businessDetailsLayout;

    @BindViews({R.id.nom_magazin, R.id.prenom, R.id.nomQuartier, R.id.phoneNumber})
    List<FormInputText> inputTexts;

    //Shared preference
    SharedPreferences sharedPref;

    //Firebase Firestore document
    DocumentReference document;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);


        sharedPref = getPreferences(Context.MODE_PRIVATE);

        ButterKnife.bind(this);
        Paper.init(this);

        firestoredb = FirebaseFirestore.getInstance();
        textView = findViewById(R.id.nom_magazin);


        listenForEmptyField();
        initializeFields();


        numeroView.getInputBox().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start < 7) {
                    numeroView.getInputBox().setTextColor(Color.parseColor("red"));

                } else if (numeroView.getValue().matches("[+]225 (\\d{8})")) {
                    Magasin magasin = Paper.book().read(USER_BUSINESS_INFO);
                    String correctNumber = "";
                    for (int i = 0; i < magasin.getNumero().length(); i++) {
                        correctNumber += s.charAt(i);

                    }
                    numeroView.setValue(correctNumber);
                    numeroView.getInputBox().setTextColor(Color.parseColor("green"));
                    Toast.makeText(BusinessDetails.this, "knd", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onTextChanged: matches ");

                } else if (start > 7) {
                    String correctNumber = "";
                    for (int i = 0; i < 8; i++) {
                        correctNumber += s.charAt(i);

                    }
                    numeroView.setValue(correctNumber);
                    numeroView.getInputBox().setTextColor(Color.parseColor("green"));

                } else if (start == 7) {
                    numeroView.getInputBox().setTextColor(Color.parseColor("green"));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void findLocation() {
        samLocationRequestService = new SamLocationRequestService(BusinessDetails.this, new SamLocationRequestService.SamLocationListener() {
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


        if (checkValidity()) {

            if (!validerNumero()) {
                Toast.makeText(this, "Veuillez entrez un numero valid", Toast.LENGTH_SHORT).show();
                return;
            }


            CollectionReference collection = firestoredb.collection(INFO_BUSINESS);

            if (sharedPref.getString(INFO_USER_SHARED_PREF_KEY, "").isEmpty()) {
                document = collection.document();

            } else {
                document = collection.document(sharedPref.getString(INFO_USER_SHARED_PREF_KEY, ""));
            }

            Magasin magasin = getMagasinInPaper();

            businessDetailsLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            document.set(magasin).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.e(TAG, "onComplete:  task has begun");
                    if (task.isSuccessful()) {
                        Log.e(TAG, "onComplete:  task is successful ");

                        if (sharedPref.getString(INFO_USER_SHARED_PREF_KEY, "").isEmpty()) {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(INFO_USER_SHARED_PREF_KEY, document.getId());
                            editor.apply();


                        }
                        Paper.book().write(USER_BUSINESS_INFO, magasin);

                        Toast.makeText(BusinessDetails.this, "Enregistrement Reussie", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.e(TAG, "onComplete: document not created " + task.getResult());
                    }
                }
            });

        } else {
            Toast.makeText(this, "Valider seulement lorsque chaque case est remplir", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper method for registerMagasin()  getting the first element  locally persisted Magasin object
     * and set it with new values. This is done to push it to firestore and update the first Magasin object locally
     *
     * @return Magasin object
     */
    private Magasin getMagasinInPaper() {

        try {
            Magasin magasin = Paper.book().read(USER_BUSINESS_INFO);

            if (magasin == null) {
                magasin = new Magasin();
            }
            magasin.setCommune(communeView.getValue());
            magasin.setNomDeMagazin(nomMagazinView.getValue());
            magasin.setPrenom(prenomView.getValue());
            magasin.setVille(villeTextView.getText().toString());
            magasin.setQuartier(quartierView.getValue());
            magasin.setNumero("+225 " + numeroView.getValue());
            return magasin;

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * if any of the fields are empty or  data enterred then is not valid
     *
     * @return true if it is valid
     */
    private boolean checkValidity() {
        boolean isValid = true;
        for (FormInputText inputText : inputTexts) {
            if (inputText.getValue().isEmpty()) {
                inputText.getInputBox().setError("Veuillez remplir cette case");
                isValid = false;
            }

        }
        if (communeView.getValue().isEmpty()) {
            communeView.getInputBox().setError("Veuillez choisir une commune");
            isValid = false;

        }
        return isValid;
    }

    private void listenForEmptyField() {
        for (FormInputText inputText : inputTexts) {

            inputText.getInputBox().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && inputText.getValue().isEmpty()) {
                        inputText.getInputBox().setError("Veuillez remplir cette case");
                    }
                }
            });
        }


        communeView.getInputBox().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && communeView.getValue().isEmpty()) {
                    communeView.getInputBox().setError("Veuillez remplir cette case");
                } else if (!hasFocus && !communeView.getValue().isEmpty()) {


                    List<String> communeList = Arrays.asList(getResources().getStringArray(R.array.commune_array));
                    if (!communeList.contains(communeView.getValue())) {
                        communeView.getInputBox().setError("Veuillez choisir une commune de la liste");
                    }
                }
            }
        });
    }

    private void initializeFields() {
        Magasin magasin = Paper.book().read(USER_BUSINESS_INFO);

        try {
            communeView.setValue(magasin.getCommune());
            nomMagazinView.setValue(magasin.getNomDeMagazin());
            prenomView.setValue(magasin.getPrenom());
            villeTextView.setText(magasin.getVille());
            quartierView.setValue(magasin.getQuartier());
            numeroView.setValue(magasin.getNumero());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (validerNumero() || numeroView.getValue().matches("[+]225 (\\d{8})")) {

            numeroView.getInputBox().setTextColor(Color.parseColor("green"));
            numeroView.showValidIcon(false);


        }

    }

    private boolean validerNumero() {
        return numeroView.getValue().length() == 8;
    }
}
