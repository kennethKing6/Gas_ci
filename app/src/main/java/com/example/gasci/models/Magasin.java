package com.example.gasci.models;

/**
 * TODO: Document this custopm class that represents each magasin
 */
public class Magasin {

    private String nomDeMagazin;
    private String prenom;
    private String ville;
    private String commune;
    private String quartier;
    private String numero;



    public Magasin() {
    }


    public Magasin(String nomDeMagazin, String prenom, String ville, String commune, String quartier, String numero) {
        this.nomDeMagazin = nomDeMagazin;
        this.prenom = prenom;
        this.ville = ville;
        this.commune = commune;
        this.quartier = quartier;
        this.numero = numero;
    }

    public String getNomDeMagazin() {
        return nomDeMagazin;
    }

    public void setNomDeMagazin(String nomDeMagazin) {
        this.nomDeMagazin = nomDeMagazin;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getQuartier() {
        return quartier;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
