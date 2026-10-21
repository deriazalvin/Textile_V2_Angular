package com.example.textile.entity;

/**
 * Les etapes du workflow de production, dans leur ordre strict.
 * COMMANDE -> COUPE -> COUTURE -> FINITION -> CONTROLE_QUALITE -> LIVRAISON
 */
public enum TypeEtape {
    COMMANDE("Commande", 0),
    COUPE("Coupe", 1),
    COUTURE("Couture", 2),
    FINITION("Finition", 3),
    CONTROLE_QUALITE("Controle qualite", 4),
    LIVRAISON("Livraison", 5);

    private final String libelle;
    private final int rang;

    TypeEtape(String libelle, int rang) {
        this.libelle = libelle;
        this.rang = rang;
    }

    public String getLibelle() {
        return libelle;
    }

    public int getRang() {
        return rang;
    }

    /** Etape suivante dans le workflow, ou {@code null} si c'est la derniere. */
    public TypeEtape suivant() {
        for (TypeEtape e : values()) {
            if (e.rang == this.rang + 1) {
                return e;
            }
        }
        return null;
    }

    /** Etape precedente dans le workflow, ou {@code null} si c'est la premiere. */
    public TypeEtape precedent() {
        for (TypeEtape e : values()) {
            if (e.rang == this.rang - 1) {
                return e;
            }
        }
        return null;
    }

    public static TypeEtape depuisRang(int rang) {
        for (TypeEtape e : values()) {
            if (e.rang == rang) {
                return e;
            }
        }
        throw new IllegalArgumentException("Rang d'etape inconnu : " + rang);
    }
}