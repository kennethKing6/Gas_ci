package com.example.gasci.Utils;

import android.util.Log;

import com.example.gasci.BusinessDetails;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class QueryUtils {

    /**
     * Business Location Query keys
     */
    public static final String QUERY_COMMUNE_CONSTANT = "commune";
    public static final String QUERY_VILLE_CONSTANT = "ville";
    public static final String QUERY_QUARTIER_CONSTANT = "quartier";
    public static final String N_A = "N/A";
    public static final String TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }



    /**
     * Helper method for making a query for a particular location
     *
     * @param commune est la commune du business
     * @return a querry object and Task<QuerySnapshot>
     */
    public static Query makeQuery(String ville, String commune, String quartier) {
        Query query = FirebaseFirestore.getInstance().collection(BusinessDetails.INFO_BUSINESS);

        if (ville.equals(N_A) && commune.equals(N_A) && !quartier.equals(N_A)) {
            query = FirebaseFirestore.getInstance().collection(BusinessDetails.INFO_BUSINESS).
                    whereEqualTo(QUERY_QUARTIER_CONSTANT, quartier);
        } else if (ville.equals(N_A) && !commune.equals(N_A) && quartier.equals(N_A)) {
            query = FirebaseFirestore.getInstance().collection(BusinessDetails.INFO_BUSINESS).
                    whereEqualTo(QUERY_COMMUNE_CONSTANT, commune);
        } else if (ville.equals(N_A) && !commune.equals(N_A) && !quartier.equals(N_A)) {
            query = FirebaseFirestore.getInstance().collection(BusinessDetails.INFO_BUSINESS).
                    whereEqualTo(QUERY_COMMUNE_CONSTANT, commune).whereEqualTo(QUERY_QUARTIER_CONSTANT, quartier);
        } else if (!ville.equals(N_A) && commune.equals(N_A) && quartier.equals(N_A)) {
            query = FirebaseFirestore.getInstance().collection(BusinessDetails.INFO_BUSINESS).
                    whereEqualTo(QUERY_VILLE_CONSTANT, ville);
        } else if (!ville.equals(N_A) && commune.equals(N_A) && !quartier.equals(N_A)) {
            query = FirebaseFirestore.getInstance().collection(BusinessDetails.INFO_BUSINESS).
                    whereEqualTo(QUERY_VILLE_CONSTANT, ville).whereEqualTo(QUERY_QUARTIER_CONSTANT, quartier);
        } else if (!ville.equals(N_A) && !commune.equals(N_A) && quartier.equals(N_A)) {
            query = FirebaseFirestore.getInstance().collection(BusinessDetails.INFO_BUSINESS).
                    whereEqualTo(QUERY_VILLE_CONSTANT, ville).whereEqualTo(QUERY_COMMUNE_CONSTANT, commune);
        } else if (!ville.equals(N_A) && !commune.equals(N_A) && !quartier.equals(N_A)) {
            query = FirebaseFirestore.getInstance().collection(BusinessDetails.INFO_BUSINESS).
                    whereEqualTo(QUERY_VILLE_CONSTANT, ville).whereEqualTo(QUERY_COMMUNE_CONSTANT, commune)
                    .whereEqualTo(QUERY_QUARTIER_CONSTANT, quartier);
        }


        return query;
    }
}
