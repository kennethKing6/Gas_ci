package com.example.gasci.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.gasci.R;
import com.omarshehe.forminputkotlin.FormInputAutoComplete;
import com.omarshehe.forminputkotlin.FormInputText;

public class LocationDialog extends DialogFragment {


    public static final String VILLE_SHARED_KEY = "villeTextView";
    public static final String COMMUNE_SHARED_KEY = "communeInputText";


    private SharedPreferences sharedPreferencesVille;
    private SharedPreferences sharedPreferencesCommune;

    Context context;
    public LocationDialog( Context context) {
        sharedPreferencesVille = context.getSharedPreferences(VILLE_SHARED_KEY, Context.MODE_PRIVATE);
        sharedPreferencesCommune = context.getSharedPreferences(COMMUNE_SHARED_KEY, Context.MODE_PRIVATE);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.location_layout, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        TextView villeTextView = view.findViewById(R.id.ville);
                        SharedPreferences.Editor editorone = sharedPreferencesVille.edit();
                        editorone.putString(VILLE_SHARED_KEY, villeTextView.getText().toString());
                        editorone.apply();

                        FormInputAutoComplete communeAutoComplete = view.findViewById(R.id.commune);
                        SharedPreferences.Editor editortwo = sharedPreferencesCommune.edit();
                        editortwo.putString(VILLE_SHARED_KEY, communeAutoComplete.getValue());
                        editortwo.apply();

                        Toast.makeText(getContext(), "Just made a query", Toast.LENGTH_SHORT).show();

                        //TODO: make sure to make a different query every time user press save
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).setMessage("Choisir ton lieu et decouvre les magazin de gas dans les alentours");
        return builder.create();

    }
}
