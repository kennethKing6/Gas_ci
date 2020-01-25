package com.example.gasci;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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

import static com.example.gasci.Dialogs.LocationDialog.BUSINESS_LOOK_UP;

public class BusinessDetails extends AppCompatActivity {

    public static final String TAG = BusinessDetails.class.getSimpleName();
    public static final String CITIES = "cities";
    public static final String INFO_BUSINESS = "infoBusiness";
    public static final String INFO_USER_SHARED_PREF_KEY = "info_user";
    public static final String USER_BUSINESS_INFO = "userBusinessInfo";
    //TODO: match +225 78011473 and +22578011473
    public static final String PHONE_NUMBER_PATTERN = "(\\+[2][2][5])(\\s)?(\\d{8})";
    public static final String NON_COMMUNE = "XXXXXX";
    SamLocationRequestService samLocationRequestService;

    FormInputText textView;
    private FirebaseFirestore firestoredb;

    // binding views
    @BindView(R.id.nom_magazin)
    FormInputText nomMagazinView;
    @BindView(R.id.prenom)
    FormInputText prenomView;
    @BindView(R.id.ville)
    FormInputAutoComplete villeInputAutoComplete;
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


        initializeFields();
        attachListeners();


        numeroView.getInputBox().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (numeroView.getValue().matches(PHONE_NUMBER_PATTERN)) {
                    numeroView.getInputBox().setTextColor(Color.parseColor("green"));

                } else {
                    numeroView.getInputBox().setTextColor(Color.parseColor("red"));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (numeroView.getValue().matches(PHONE_NUMBER_PATTERN)) {

                    numeroView.showValidIcon(true);
                }

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

            if (!numeroView.getValue().matches(PHONE_NUMBER_PATTERN)) {
                Toast.makeText(this, "Veuillez entrez un numero valid", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!validerVille()) {
                Toast.makeText(this, "Veuillez choisir une ville de la liste", Toast.LENGTH_SHORT).show();
                villeInputAutoComplete.getInputBox().requestFocus();
                return;
            } else if (communeView.getVisibility() == View.VISIBLE) {
                if (!validerCommune()) {
                    Toast.makeText(this, "Veuillez choisir une commune de la liste", Toast.LENGTH_SHORT).show();
                    communeView.getInputBox().requestFocus();
                    return;
                }
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
            magasin.setCommune(communeView.getVisibility() == View.VISIBLE ? communeView.getValue() : NON_COMMUNE);
            magasin.setNomDeMagazin(nomMagazinView.getValue());
            magasin.setPrenom(prenomView.getValue());
            magasin.setVille(villeInputAutoComplete.getValue());
            magasin.setQuartier(quartierView.getValue());

            //Make sure the the number is always in the format +225 78011473 but not +22578011473
            String numeroCorrectFormat = getCorrectNumberFomat(numeroView.getValue());

            magasin.setNumero(numeroCorrectFormat);
            return magasin;

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Helper method to help correctly format the owner's boutique phone number
     *
     * @param number to be correctly formatted. The phone number is supposed to have space
     *               between the +225 and the 8 digits and that is the job of this method.
     * @return the correctly formatted phone number
     */
    private String getCorrectNumberFomat(String number) {

        if (number.matches("(\\+[2][2][5])(\\d{8})")) {

            //Get the +225 section of the number i.e first section of the number
            Pattern pattern1 = Pattern.compile("\\+[2][2][5]");
            Matcher matcher1 = pattern1.matcher(number);
            matcher1.find();


            //Get the remaining 8 digits  section of the number i.e second section of the number
            Pattern pattern2 = Pattern.compile("(\\+225)(\\d{8})");
            Matcher matcher2 = pattern2.matcher(number);
            matcher2.find();

            number = matcher1.group() + " " + matcher2.group(2);

        }
        return number;
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
        if (communeView.getValue().isEmpty() && communeView.getVisibility() == View.VISIBLE) {
            communeView.getInputBox().setError("Veuillez choisir une commune");
            isValid = false;

        }
        return isValid;
    }

    private void attachListeners() {
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


                    List<String> communeList = Arrays.asList(getResources().getStringArray(R.array.abidjan_commune_array));
                    if (!communeList.contains(communeView.getValue())) {
                        communeView.getInputBox().setError("Veuillez choisir une commune de la liste");
                    }
                }
            }
        });


        SharedPreferences businessLookupSharedPref = getSharedPreferences(BUSINESS_LOOK_UP, Context.MODE_PRIVATE);

        if (villeInputAutoComplete.getValue().equalsIgnoreCase("Abidjan")) {
            communeView.setVisibility(View.VISIBLE);
        }


        villeInputAutoComplete.getInputBox().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    if (villeInputAutoComplete.getValue().equalsIgnoreCase("Abidjan"))
                        communeView.setVisibility(View.VISIBLE);
                    else {
                        communeView.setVisibility(View.GONE);
                    }

                }
            }
        });
    }

    /**
     * Initialize all the Inputbox when this activity begins with this method.
     * Get the values from the persisted data and display it if there are any
     */
    private void initializeFields() {
        Magasin magasin = Paper.book().read(USER_BUSINESS_INFO);

        try {
            communeView.setValue(magasin.getCommune());
            nomMagazinView.setValue(magasin.getNomDeMagazin());
            prenomView.setValue(magasin.getPrenom());
            villeInputAutoComplete.setValue(magasin.getVille());
            quartierView.setValue(magasin.getQuartier());
            numeroView.setValue(magasin.getNumero());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (numeroView.getValue().matches(PHONE_NUMBER_PATTERN)) {

            numeroView.getInputBox().setTextColor(Color.parseColor("green"));


        }

    }

    private boolean validerVille() {
        List<String> ville = Arrays.asList(getResources().getStringArray(R.array.ville_de_cote_ivoire_array));
        return ville.contains(villeInputAutoComplete.getValue());
    }

    private boolean validerCommune() {
        List<String> commune = Arrays.asList(getResources().getStringArray(R.array.abidjan_commune_array));
        return commune.contains(communeView.getValue());
    }

}
