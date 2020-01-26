package com.example.gasci.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.gasci.LesMagazinActivity;
import com.example.gasci.R;
import com.omarshehe.forminputkotlin.FormInputAutoComplete;

import butterknife.OnTextChanged;

import static com.example.gasci.Utils.QueryUtils.N_A;

public class LocationDialog extends DialogFragment {


    public static final String BUSINESS_LOOK_UP = "businessLookup";


    public static final String VILLE_SHARED_KEY = "villeInputText";
    public static final String COMMUNE_SHARED_KEY = "communeInputText";
    public static final String QUARTIER_SHARED_KEY = "quartierInputText";
    public static final String TAG = LocationDialog.class.getSimpleName();



    private SharedPreferences businessLookupSharedPref;
    private Context context;

    //Dialog main layout
    View mainDialogLayout;


    public LocationDialog(Context context) {
        businessLookupSharedPref = context.getSharedPreferences(BUSINESS_LOOK_UP, Context.MODE_PRIVATE);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);

         mainDialogLayout = inflater.inflate(R.layout.location_layout, null);

        // sign in the user ...
        FormInputAutoComplete villeAutoComplete = mainDialogLayout.findViewById(R.id.ville);
        FormInputAutoComplete communeAutoComplete = mainDialogLayout.findViewById(R.id.commune);
        FormInputAutoComplete quartierAutoComplete = mainDialogLayout.findViewById(R.id.quartier);
        villeAutoComplete.requestFocus();




        prepareViews(villeAutoComplete, communeAutoComplete, quartierAutoComplete);

        //Set the fields with the previously registered data
        if (!businessLookupSharedPref.getString(VILLE_SHARED_KEY,
                "").isEmpty()) {
            villeAutoComplete.setHint(businessLookupSharedPref.getString(VILLE_SHARED_KEY,
                    ""));
        }
        if (!businessLookupSharedPref.getString(COMMUNE_SHARED_KEY,
                "").isEmpty()) {
            communeAutoComplete.setHint(businessLookupSharedPref.getString(COMMUNE_SHARED_KEY,
                    ""));
        }
        if (!businessLookupSharedPref.getString(QUARTIER_SHARED_KEY,
                "").isEmpty()) {
            quartierAutoComplete.setHint(businessLookupSharedPref.getString(QUARTIER_SHARED_KEY,
                    ""));
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent mainDialogLayout because its going in the dialog layout
        builder.setView(mainDialogLayout)
                // Add action buttons
                .setPositiveButton("Rechercher", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                        SharedPreferences.Editor businesspref = businessLookupSharedPref.edit();
                        if (!villeAutoComplete.getValue().isEmpty()) {
                            businesspref.putString(VILLE_SHARED_KEY, villeAutoComplete.getValue());
                        } else {
                            businesspref.putString(VILLE_SHARED_KEY, N_A);
                        }

                        if (!communeAutoComplete.getValue().isEmpty()) {
                            businesspref.putString(COMMUNE_SHARED_KEY,
                                    communeAutoComplete.getVisibility() == View.VISIBLE ? communeAutoComplete.getValue() : N_A);
                        } else {
                            businesspref.putString(COMMUNE_SHARED_KEY, N_A);
                        }

                        if (!quartierAutoComplete.getValue().isEmpty()) {
                            businesspref.putString(QUARTIER_SHARED_KEY, quartierAutoComplete.getValue());
                        } else {
                            businesspref.putString(QUARTIER_SHARED_KEY, N_A);
                        }

                        businesspref.apply();


                        Intent intent = new Intent(context, LesMagazinActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        startActivity(intent);


                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).setMessage("Choisir ton lieu et decouvre les magazin de gaz dans les alentours");


        return builder.create();

    }

    private void prepareViews(FormInputAutoComplete villeAutoComplete, FormInputAutoComplete communeAutoComplete,
                              FormInputAutoComplete quartierAutoComplet) {

        if ((businessLookupSharedPref.getString(VILLE_SHARED_KEY,
                "").equals("Abidjan"))) {
            communeAutoComplete.setVisibility(View.VISIBLE);
        }


        villeAutoComplete.getInputBox().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                    if (villeAutoComplete.getValue().equalsIgnoreCase("Abidjan"))
                        communeAutoComplete.setVisibility(View.VISIBLE);
                    else
                        communeAutoComplete.setVisibility(View.GONE);



            }
        });
    }




}
