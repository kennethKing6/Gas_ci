package com.example.gasci;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.gasci.Dialogs.LocationDialog;
import com.example.gasci.models.Magasin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LesMagazinActivity extends AppCompatActivity {

    public static final String COMMUNE = "commune";
    public static final String TAG = LesMagazinActivity.class.getSimpleName();
    private List<DocumentSnapshot> myContent;

    @BindView(R.id.rvRecyclerView)
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_les_magazin);

        ButterKnife.bind(this);
        Query query = FirebaseFirestore.getInstance().collection(MainActivity.INFO_BUSINESS).whereEqualTo(COMMUNE, "Koumassi");


        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    myContent = task.getResult().getDocuments();

                    if (myContent.size() > 0) {
                        ParallaxRecyclerAdapter<DocumentSnapshot> adapter = new ParallaxRecyclerAdapter<DocumentSnapshot>(myContent) {
                            @Override
                            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<DocumentSnapshot> adapter, int i) {


                                Magasin magasin = myContent.get(i).toObject(Magasin.class);

                                TextView nomMagazinTxtView = viewHolder.itemView.findViewById(R.id.nom_magazin_text_view);
                                TextView prenomTxtView = viewHolder.itemView.findViewById(R.id.prenom_text_view);
                                TextView quartierTxtView = viewHolder.itemView.findViewById(R.id.quartier_text_view);

                                nomMagazinTxtView.setText(magasin.getNomDeMagazin());
                                prenomTxtView.setText(magasin.getPrenom());
                                quartierTxtView.setText(magasin.getQuartier());


                            }

                            @Override
                            public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, final ParallaxRecyclerAdapter<DocumentSnapshot> adapter, int i) {
                                // Here is where you inflate your row and pass it to the constructor of your ViewHolder
                                return new MyCustomViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.magazin_item, viewGroup, false));
                            }

                            @Override
                            public int getItemCountImpl(ParallaxRecyclerAdapter<DocumentSnapshot> adapter) {
                                // return the content of your array
                                return myContent.size();
                            }
                        };
                        adapter.setParallaxHeader(LayoutInflater.from(LesMagazinActivity.this).inflate(
                                R.layout.myparallaxview, null, false), recyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(adapter);
                    } else {
                        //TODO: Show empty list layout
                        Log.e(TAG, "Empty screen");
                    }
                } else {
                    Log.e(TAG, "onComplete: task not successful ");
                }
            }
        });


    }

    private class MyCustomViewHolder extends RecyclerView.ViewHolder {


        public MyCustomViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }


    @OnClick(R.id.location_item)
    void cherchGaz() {
        LocationDialog locationDialog = new LocationDialog(this);
        locationDialog.show(getSupportFragmentManager(),"position request");
    }


    @OnClick(R.id.reglage_item)
    void ouvreReglage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
