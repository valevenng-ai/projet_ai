package main.java.utils;
public class Proposition {
    private int budget;
    private int duree;   
    

    public Proposition(int budget, int duree) {
        this.budget = budget;
        this.duree = duree;
    }

    // Sérialisation vers JSON pour l'envoi dans ACLMessage
    public String toJSON() {
        return String.format(
            "{" +
            "\"budget\":%d,"   +
            "\"duree\":%d,"    +
            "}",
            budget,
            duree
        );
    }

    // Désérialisation depuis JSON reçu dans ACLMessage
    public static Proposition fromJSON(String json) {
        int budget = extraireInt(json,    "budget");
        int duree = extraireInt(json,    "duree");
        Proposition p = new Proposition(budget, duree);
        return p;
    }

    // Affichage lisible pour les logs console
    @Override
    public String toString() {

        return String.format(
            "Proposition {" +
            "\n  budget  : %.1fM€" +
            "\n  duree   : %dmin" +
            "\n}",
            budget / 1_000_000.0,
            duree
        );
    }
    
    // ════════════════════════════════════════
    // Méthodes privées de parsing JSON
    // ════════════════════════════════════════

    private static int extraireInt(String json, String cle) {
        try {
            String pattern = "\"" + cle + "\":";
            int debut = json.indexOf(pattern) 
                        + pattern.length();
            // Sauter les espaces
            while (debut < json.length()
                   && Character.isWhitespace(
                       json.charAt(debut)))
                debut++;
            int fin = debut;
            while (fin < json.length()
                   && json.charAt(fin) != ','
                   && json.charAt(fin) != '}')
                fin++;
            return Integer.parseInt(
                json.substring(debut, fin).trim()
            );
        } catch (Exception e) {
            System.err.println(
                "[PropositionPhase1] "        +
                "Erreur parsing int pour : " + cle
            );
            return 0;
        }
    }

    private static String extraireString(String json, 
                                          String cle) {
        try {
            String pattern = "\"" + cle + "\":\"";
            int debut = json.indexOf(pattern) 
                        + pattern.length();
            int fin   = json.indexOf("\"", debut);
            return json.substring(debut, fin);
        } catch (Exception e) {
            System.err.println(
                "[PropositionPhase1] "           +
                "Erreur parsing string pour : " + cle
            );
            return "";
        }
    }

    private static boolean extraireBool(String json, 
                                         String cle) {
        try {
            String pattern = "\"" + cle + "\":";
            int debut = json.indexOf(pattern) 
                        + pattern.length();
            while (debut < json.length()
                   && Character.isWhitespace(
                       json.charAt(debut)))
                debut++;
            int fin = debut;
            while (fin < json.length()
                   && json.charAt(fin) != ','
                   && json.charAt(fin) != '}')
                fin++;
            return Boolean.parseBoolean(
                json.substring(debut, fin).trim()
            );
        } catch (Exception e) {
            System.err.println(
                "[PropositionPhase1] "          +
                "Erreur parsing bool pour : " + cle
            );
            return false;
        }
    }

    public int getBudget() {
        return this.budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getDuree() {
        return this.duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }
}
