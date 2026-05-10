package main.java.agents;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import main.java.behaviours.DirectorNegociationBehaviour;
import main.java.utils.Planchers;
import main.java.utils.Proposition;
import main.java.utils.RepartitionBudget;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DirectorAgent extends Agent implements Phase2Agent{
    final static private int BUDGET_MIN = 15_000_000;

    private Map<String, Integer> planchers;

    private static final Map<String, Integer> PRIORITES =
            new LinkedHashMap<>() {{
                put("script",     30);
                put("production", 90);
                put("casting",    20);
                put("vfx",        65);
                put("music",      70);
            }};

    public Map<String, Integer> getPriorites() {
        return PRIORITES;
    }

    private static final String API_URL = "https://api.ai-raison.com/executions/PRJ32025/latest";
    private static final String API_URL_PHASE2 = "https://api.ai-raison.com/executions/PRJ32125/latest";
    private static final String API_KEY = "QwVTG1jIpX1AhVd29g8kG9GTfrJAepfV5N34xiMh";

    private int budgetAccorde;

    public String getDecision1(int budget, int iteration){
        /* Appelle l'api Raison et obtient la décision pour la phase 1. */

        // ─── 1. Construction du body JSON ───────────────────────────────────────

        JSONObject paramBudget = new JSONObject();
        paramBudget.put("name", "Budget");
        paramBudget.put("value", String.valueOf(budget));

        JSONObject paramMin = new JSONObject();
        paramMin.put("name", "Min");
        paramMin.put("value", String.valueOf(BUDGET_MIN));

        JSONObject paramI = new JSONObject();
        paramI.put("name", "I");
        paramI.put("value", String.valueOf(iteration));

        JSONArray parameters1 = new JSONArray();
        parameters1.put(paramBudget);

        // Élément
        JSONObject element1 = new JSONObject();
        element1.put("id", "OPT416518");
        element1.put("label", "How much does Producer offer");
        element1.put("parameters", parameters1);


        JSONArray parameters3 = new JSONArray();
        parameters3.put(paramMin);

        JSONObject element3 = new JSONObject();
        element3.put("id", "OPT416568");
        element3.put("label", "What's minimum expected");
        element3.put("parameters", parameters3);

        JSONArray parameters4 = new JSONArray();
        parameters4.put(paramI);

        JSONObject element4 = new JSONObject();
        element4.put("id", "OPT416668");
        element4.put("label", "What iteration");
        element4.put("parameters", parameters4);

        JSONArray elements = new JSONArray();
        elements.put(element1);
        elements.put(element3);
        elements.put(element4);

        //Options
        JSONObject option1 = new JSONObject();
        option1.put("id", "OPT416268");

        JSONObject option2 = new JSONObject();
        option2.put("id", "OPT416218");

        JSONObject option3 = new JSONObject();
        option3.put("id", "OPT416168");

        JSONArray options = new JSONArray();
        options.put(option1);
        options.put(option2);
        options.put(option3);

        JSONObject requestBody = new JSONObject();
        requestBody.put("elements", elements);
        requestBody.put("options", options);
        System.out.println("Body envoyé : " + requestBody.toString());

        // ─── 2. Envoi de la requête POST ────────────────────────────────────────

        HttpURLConnection conn = null;

        try {
            URL url = new URL(API_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("x-api-key", API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // Envoi du body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // ─── 3. Lecture de la réponse ───────────────────────────────────────

            int statusCode = conn.getResponseCode();
            System.out.println("Status HTTP : " + statusCode);

            BufferedReader reader;
            if (statusCode >= 200 && statusCode < 300) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            // ─── 4. Parsing et affichage de la réponse ──────────────────────────

            JSONArray responseArray = new JSONArray(responseBuilder.toString());

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject result = responseArray.getJSONObject(i);
                boolean isSolution = result.getBoolean("isSolution");

                if (isSolution) {
                    System.out.println("Decision : " + result.getJSONObject("option").getString("label"));
                    return result.getJSONObject("option").getString("label");
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return null; // aucune solution trouvée
    }


    public Map<String, Integer> getPlanchers() {
        return planchers;
    }

    public void setBudgetAccorde(int budget) {
        this.budgetAccorde = budget;
        this.planchers = Planchers
                .calculerPlanchersRealisateur(budget);
    }

    public int getBudgetAccorde(){
        return budgetAccorde;
    }

    /**
     * Appelle l'api Raison et obtient la décision pour la phase 2.
     * @param budget Budget total convenue pendant la phase 1
     * @param iteration L'iteration actuelle
     * @param gap   L'écart totale entre les répartitions proposées par les deux agents
     * @param floors_respected  Si les seuils sont respectés
     * @return
     */
    public String getDecision2(int budget, int iteration, int gap, boolean floors_respected){
        // ─── 1. Construction du body JSON ───────────────────────────────────────

        JSONObject paramBudget = new JSONObject();
        paramBudget.put("name", "Budget");
        paramBudget.put("value", String.valueOf(budget));

        JSONObject paramI = new JSONObject();
        paramI.put("name", "I");
        paramI.put("value", String.valueOf(iteration));

        JSONObject paramGap = new JSONObject();
        paramGap.put("name", "Gap");
        paramGap.put("value", String.valueOf(gap));

        JSONObject paramF = new JSONObject();
        paramF.put("name", "Floors_respected");
        paramF.put("value", String.valueOf(floors_respected));

        JSONArray parameters1 = new JSONArray();
        parameters1.put(paramBudget);

        // Élément
        JSONObject element1 = new JSONObject();
        element1.put("id", "OPT432818");
        element1.put("label", "Budget");
        element1.put("parameters", parameters1);


        JSONArray parameters3 = new JSONArray();
        parameters3.put(paramGap);

        JSONObject element3 = new JSONObject();
        element3.put("id", "OPT432868");
        element3.put("label", "Gap");
        element3.put("parameters", parameters3);

        JSONArray parameters4 = new JSONArray();
        parameters4.put(paramI);

        JSONObject element4 = new JSONObject();
        element4.put("id", "OPT432718");
        element4.put("label", "Current iteration");
        element4.put("parameters", parameters4);

        JSONArray parameters5 = new JSONArray();
        parameters5.put(paramF);

        JSONObject element5 = new JSONObject();
        element5.put("id", "OPT433068");
        element5.put("label", "floors are respected");
        element5.put("parameters", parameters5);

        JSONArray elements = new JSONArray();
        elements.put(element1);
        elements.put(element3);
        elements.put(element4);
        elements.put(element5);

        //Options
        JSONObject option1 = new JSONObject();
        option1.put("id", "OPT421468");

        JSONObject option2 = new JSONObject();
        option2.put("id", "OPT421418");

        JSONObject option3 = new JSONObject();
        option2.put("id", "OPT441518");

        JSONArray options = new JSONArray();
        options.put(option1);
        options.put(option2);
        options.put(option3);

        JSONObject requestBody = new JSONObject();
        requestBody.put("elements", elements);
        requestBody.put("options", options);
        System.out.println("Body envoyé : " + requestBody.toString());

        // ─── 2. Envoi de la requête POST ────────────────────────────────────────

        HttpURLConnection conn = null;

        try {
            URL url = new URL(API_URL_PHASE2);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("x-api-key", API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // Envoi du body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // ─── 3. Lecture de la réponse ───────────────────────────────────────

            int statusCode = conn.getResponseCode();
            System.out.println("Status HTTP : " + statusCode);

            BufferedReader reader;
            if (statusCode >= 200 && statusCode < 300) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            // ─── 4. Parsing et affichage de la réponse ──────────────────────────

            JSONArray responseArray = new JSONArray(responseBuilder.toString());

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject result = responseArray.getJSONObject(i);
                boolean isSolution = result.getBoolean("isSolution");

                if (isSolution) {
                    System.out.println("Decision : " + result.getJSONObject("option").getString("label"));
                    return result.getJSONObject("option").getString("label");
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return null; // aucune solution trouvée
    }

    /**
     * Calcule la nouvelle valeur d'un poste budgétaire
     * en concédant progressivement vers la valeur reçue.
     *
     * @param valeurIdeale  valeur souhaitée par l'agent
     * @param valeurRecue   valeur proposée par l'adversaire
     * @param taux          taux de concession (entre 0.0 et 1.0)
     * @param plancher      valeur minimale acceptable
     * @return              nouvelle valeur après concession
     */
    public static int calculerNouvelleValeur(
            int valeurIdeale,
            int valeurRecue,
            double taux,
            int plancher) {

        // Écart entre la position idéale et la proposition reçue
        int ecart = valeurIdeale - valeurRecue;

        // Concéder une fraction de l'écart
        // vers la proposition reçue
        int nouvelleValeur = (int)(valeurIdeale - (ecart * taux));

        // Garantir le plancher absolu
        return Math.max(nouvelleValeur, plancher);
    }

    /**
     * Rééquilibre la répartition pour que la somme
     * des postes soit exactement égale au budget total.
     * L'ajustement se fait sur le poste le moins
     * prioritaire selon le profil de l'agent.
     *
     * @param offre         répartition à reequilibrer
     * @param budgetTotal   budget total à respecter
     * @param planchers     planchers par poste
     * @param priorites     priorités par poste
     * @return              répartition rééquilibrée
     */
    public static RepartitionBudget reequilibrer(
            RepartitionBudget offre,
            int budgetTotal,
            Map<String, Integer> planchers,
            Map<String, Integer> priorites) {

        int somme = offre.getScript()
                + offre.getProduction()
                + offre.getCasting()
                + offre.getVfx()
                + offre.getMusic();

        int diff = budgetTotal - somme;

        // Déjà équilibré
        if (diff == 0) return offre;

        // Trier les postes par priorité croissante
        // → ajuster en priorité sur les postes
        //   les moins importants pour l'agent
        List<String> postesParPriorite = priorites
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Parcourir les postes du moins au plus prioritaire
        // et ajuster tant que diff != 0
        Map<String, Integer> valeurs = new LinkedHashMap<>();
        valeurs.put("script",          offre.getScript());
        valeurs.put("production",      offre.getProduction());
        valeurs.put("casting",         offre.getCasting());
        valeurs.put("vfx",             offre.getVfx());
        valeurs.put("music",           offre.getMusic());

        for (String poste : postesParPriorite) {
            if (diff == 0) break;

            int valeurActuelle = valeurs.get(poste);
            int plancher       = planchers.getOrDefault(poste, 0);

            if (diff > 0) {
                // Somme trop faible → augmenter ce poste
                valeurs.put(poste, valeurActuelle + diff);
                diff = 0;

            } else {
                // Somme trop élevée → réduire ce poste
                // sans descendre sous le plancher
                int reduction = Math.min(
                        Math.abs(diff),
                        valeurActuelle - plancher
                );
                valeurs.put(poste, valeurActuelle - reduction);
                diff += reduction;
            }
        }

        // Si diff != 0 après tous les postes
        // → budget incompatible avec les planchers
        if (diff != 0) {
            System.err.println(
                    "[reequilibrer] Impossible d'équilibrer : " +
                            "diff restant = " + diff +
                            " — planchers trop élevés pour le budget"
            );
        }

        return new RepartitionBudget(
                valeurs.get("script"),
                valeurs.get("production"),
                valeurs.get("casting"),
                valeurs.get("vfx"),
                valeurs.get("music")
        );
    }

    // Génère la première offre Phase 2
    // basée sur les priorités du réalisateur
    public RepartitionBudget genererPremiereOffrePhase2() {

        int budgetTotal = getBudgetAccorde();

        // Calcul de la somme totale des priorités
        int sommePriorites = PRIORITES.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        // Calcul de chaque poste proportionnellement
        // à sa priorité
        Map<String, Integer> planchers = getPlanchers();

        int script     = (int)(budgetTotal
                * PRIORITES.get("script")
                / (double) sommePriorites);
        int production = (int)(budgetTotal
                * PRIORITES.get("production")
                / (double) sommePriorites);
        int casting    = (int)(budgetTotal
                * PRIORITES.get("casting")
                / (double) sommePriorites);
        int vfx        = (int)(budgetTotal
                * PRIORITES.get("vfx")
                / (double) sommePriorites);
        int music      = (int)(budgetTotal
                * PRIORITES.get("music")
                / (double) sommePriorites);

        // Vérification des planchers
        script     = Math.max(script,     planchers.get("script"));
        production = Math.max(production, planchers.get("production"));
        casting    = Math.max(casting,    planchers.get("casting"));
        vfx        = Math.max(vfx,        planchers.get("vfx"));
        music      = Math.max(music,      planchers.get("music"));

        // Rééquilibrage pour respecter le budget total
        RepartitionBudget offre = new RepartitionBudget(
                script, production, casting, vfx, music
        );
        offre = reequilibrer(
                offre, budgetTotal, planchers, PRIORITES
        );

        System.out.println(
                "[DirectorAgent] Première offre Phase 2 :" +
                        "\n  Script     : " + offre.getScript()     / 1_000_000.0 + "M€" +
                        "\n  Production : " + offre.getProduction() / 1_000_000.0 + "M€" +
                        "\n  Casting    : " + offre.getCasting()    / 1_000_000.0 + "M€" +
                        "\n  VFX        : " + offre.getVfx()        / 1_000_000.0 + "M€" +
                        "\n  Musique    : " + offre.getMusic()       / 1_000_000.0 + "M€"
        );

        return offre;
    }

    // Génère une contre-offre Phase 2
    // pour le réalisateur
    @Override
    public RepartitionBudget genererContreOffrePhase2(
            RepartitionBudget recue, int iteration) {

        int budgetTotal    = getBudgetAccorde();
        int maxIterations  = 5;

        Map<String, Integer> planchers = getPlanchers();

        // Taux de concession augmente avec les itérations
        // le réalisateur cède de plus en plus
        double tauxConcession = (double) iteration / maxIterations;

        // Position idéale du réalisateur
        RepartitionBudget ideale = genererPremiereOffrePhase2();

        // Pour chaque poste, concéder en direction
        // de la proposition reçue selon la priorité

        // Script (priorité 50 — moyenne)
        int script = calculerNouvelleValeur(
                ideale.getScript(),
                recue.getScript(),
                tauxConcession * 0.75,   // taux base 20%
                planchers.get("script")
        );

        // Production (priorité 90 — très haute)
        // résiste fortement
        int production = calculerNouvelleValeur(
                ideale.getProduction(),
                recue.getProduction(),
                tauxConcession * 0.50,   // taux base 5%
                planchers.get("production")
        );

        // Casting (priorité 30 — faible)
        // cède facilement
        int casting = calculerNouvelleValeur(
                ideale.getCasting(),
                recue.getCasting(),
                tauxConcession * 0.95,   // taux base 40%
                planchers.get("casting")
        );

        // VFX (priorité 75 — haute)
        int vfx = calculerNouvelleValeur(
                ideale.getVfx(),
                recue.getVfx(),
                tauxConcession * 0.60,   // taux base 5%
                planchers.get("vfx")
        );

        // Musique (priorité 80 — haute)
        int music = calculerNouvelleValeur(
                ideale.getMusic(),
                recue.getMusic(),
                tauxConcession * 0.60,   // taux base 5%
                planchers.get("music")
        );

        // Package deal si dernières itérations
        // et écart encore important
        if (iteration >= maxIterations - 1) {
            // Cède sur casting (priorité faible)
            // pour préserver VFX et musique
            casting = recue.getCasting();
        }

        RepartitionBudget offre = new RepartitionBudget(
                script, production, casting, vfx, music
        );

        // Rééquilibrage obligatoire
        offre = reequilibrer(offre, budgetTotal, planchers, PRIORITES);

        System.out.println(
                "[DirectorAgent] Contre-offre Phase 2" +
                        " iteration " + iteration + " :" +
                        "\n  Script     : " + offre.getScript()     / 1_000_000.0 + "M€" +
                        "\n  Production : " + offre.getProduction() / 1_000_000.0 + "M€" +
                        "\n  Casting    : " + offre.getCasting()    / 1_000_000.0 + "M€" +
                        "\n  VFX        : " + offre.getVfx()        / 1_000_000.0 + "M€" +
                        "\n  Musique    : " + offre.getMusic()       / 1_000_000.0 + "M€"
        );

        return offre;
    }

    protected void setup(){
        DirectorNegociationBehaviour negociationBehaviour = new DirectorNegociationBehaviour(this);

        negociationBehaviour.getDataStore().put("BUDGET_MIN", DirectorAgent.BUDGET_MIN);

        int iteration = 5;
        negociationBehaviour.getDataStore().put("iteration", iteration);

        negociationBehaviour.getDataStore().put("phase", 1);

        addBehaviour(negociationBehaviour);

    }
}
