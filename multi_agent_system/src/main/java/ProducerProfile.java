package main.java;

public class ProducerProfile {
    private int budgetMax;
    private int budgetTarget;
    private int duree;

    public ProducerProfile(int budgetMax, int budgetTarget, int duree){
        this.budgetMax = budgetMax;
        this.budgetTarget = budgetTarget;
        this.duree = duree;
    }
     // Désérialisation depuis JSON
    public static ProducerProfile fromJSON(String json) {
        ProducerProfile p = new ProducerProfile(0, 0, 0);
        p.budgetMax    = extraireInt(json,    "budgetMax");
        p.budgetTarget   = extraireInt(json,    "budgetMax");
        p.duree     = extraireInt(json,    "duree");
        return p;
    }

    private static int extraireInt(String json, String cle) {
        String pattern = "\"" + cle + "\":";
        int debut = json.indexOf(pattern) + pattern.length();
        int fin   = json.indexOf(",", debut);
        if (fin == -1) fin = json.indexOf("}", debut);
        return Integer.parseInt(json.substring(debut, fin).trim());
    }


    public int getBudgetMax() {
        return this.budgetMax;
    }

    public int getBudgetTarget() {
        return this.budgetTarget;
    }

    public int getDuree() {
        return this.duree;
    }

}
