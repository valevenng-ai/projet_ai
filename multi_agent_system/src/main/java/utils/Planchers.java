package main.java.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class Planchers {

    // Pourcentages réalisateur par poste
    private static final Map<String, Double>
            POURCENTAGES_REALISATEUR = Map.of(
            "script",           0.04,
            "production",       0.25,
            "casting",          0.08,
            "effets_speciaux",  0.15,
            "musique",          0.07
    );

    // Pourcentages producteur par poste
    private static final Map<String, Double>
            POURCENTAGES_PRODUCTEUR = Map.of(
            "script",           0.02,
            "production",       0.18,
            "casting",          0.20,
            "effets_speciaux",  0.04,
            "musique",          0.02
    );

    // Calcule les planchers réalisateur
    public static Map<String, Integer>
    calculerPlanchersRealisateur(int budgetTotal) {
        return calculer(budgetTotal,
                POURCENTAGES_REALISATEUR);
    }

    // Calcule les planchers producteur
    public static Map<String, Integer>
    calculerPlanchersProducteur(int budgetTotal) {
        return calculer(budgetTotal,
                POURCENTAGES_PRODUCTEUR);
    }

    // Méthode commune de calcul
    private static Map<String, Integer> calculer(
            int budgetTotal,
            Map<String, Double> pourcentages) {

        Map<String, Integer> planchers =
                new LinkedHashMap<>();

        for (Map.Entry<String, Double> e
                : pourcentages.entrySet()) {
            int valeur = (int)(budgetTotal * e.getValue());
            planchers.put(e.getKey(), valeur);
        }
        return planchers;
    }

    // Vérifie que les planchers sont compatibles
    // avec le budget total
    public static boolean sontCompatibles(
            int budgetTotal,
            Map<String, Integer> planchers) {

        int somme = planchers.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (somme > budgetTotal) {
            System.out.println(
                    "INCOMPATIBLE : planchers " +
                            somme / 1_000_000.0 +
                            "M€ > budget " +
                            budgetTotal / 1_000_000.0 + "M€"
            );
            return false;
        }
        return true;
    }
}
