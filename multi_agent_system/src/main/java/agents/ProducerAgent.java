package main.java.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import main.java.behaviours.ProducerNegociationBehaviour;
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
import java.util.Map;

public class ProducerAgent extends Agent implements Phase2Agent{
    final static private int BUDGET_MAX = 20_000_000;
    final static private int BUDGET_MIN = 15_000_000;
    final static private int BUDGET_TARGET = 16_000_000;

    private int budgetAccorde;

    private Map<String, Integer> planchers;

    private static final String API_URL = "https://api.ai-raison.com/executions/PRJ31125/latest";
    private static final String API_URL_PHASE2 = "https://api.ai-raison.com/executions/PRJ32125/latest";
    private static final String API_KEY = "qiezMHtEPxaqORBSerFNZ1F2ynE6hwvt44Mok2wW";

    public String getDecision1(int budget, int iteration){
        // ─── 1. Construction du body JSON ───────────────────────────────────────

        JSONObject paramBudget = new JSONObject();
        paramBudget.put("name", "Budget");
        paramBudget.put("value", String.valueOf(budget));

        JSONObject paramMax = new JSONObject();
        paramMax.put("name", "Max");
        paramMax.put("value", String.valueOf(BUDGET_MAX));

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
        element1.put("id", "OPT411818");
        element1.put("label", "How much does Director want");
        element1.put("parameters", parameters1);

        JSONArray parameters2 = new JSONArray();
        parameters2.put(paramMax);

        JSONObject element2 = new JSONObject();
        element2.put("id", "OPT411868");
        element2.put("label", "What's the max budget");
        element2.put("parameters", parameters2);

        JSONArray parameters3 = new JSONArray();
        parameters3.put(paramMin);

        JSONObject element3 = new JSONObject();
        element3.put("id", "OPT413018");
        element3.put("label", "What's the min budget");
        element3.put("parameters", parameters3);

        JSONArray parameters4 = new JSONArray();
        parameters4.put(paramI);

        JSONObject element4 = new JSONObject();
        element4.put("id", "OPT411518");
        element4.put("label", "What iteration");
        element4.put("parameters", parameters4);

        JSONArray elements = new JSONArray();
        elements.put(element1);
        elements.put(element2);
        elements.put(element3);
        elements.put(element4);

        JSONObject option1 = new JSONObject();
        option1.put("id", "OPT403118");

        JSONObject option2 = new JSONObject();
        option2.put("id", "OPT402818");

        JSONObject option3 = new JSONObject();
        option3.put("id", "OPT402868");

        JSONObject option4 = new JSONObject();
        option4.put("id", "OPT402768");

        JSONArray options = new JSONArray();
        options.put(option1);
        options.put(option2);
        options.put(option3);
        options.put(option4);

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

    public void setBudgetAccorde(int budget) {
        this.budgetAccorde = budget;
        this.planchers = Planchers
                .calculerPlanchersProducteur(budget);
    }

    public Map<String, Integer> getPlanchers() {
        return planchers;
    }

    public int getBudgetAccorde(){
        return budgetAccorde;
    }

    @Override
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
        parameters4.put(paramI);

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

        JSONArray options = new JSONArray();
        options.put(option1);
        options.put(option2);

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

    @Override
    public RepartitionBudget genererContreOffrePhase2(RepartitionBudget recue, int iteration) {
        return null;
    }

    protected void setup(){
        Proposition proposition = new Proposition(ProducerAgent.BUDGET_TARGET);

        ACLMessage msg_sent = new ACLMessage(ACLMessage.PROPOSE);
        msg_sent.addReceiver(new AID("Director", AID.ISLOCALNAME));
        msg_sent.setContent(proposition.toJSON());
        send(msg_sent);

        ProducerNegociationBehaviour negociationBehaviour = new ProducerNegociationBehaviour(this);

        negociationBehaviour.getDataStore().put("BUDGET_MAX", ProducerAgent.BUDGET_MAX);

        int iteration = 3;
        negociationBehaviour.getDataStore().put("iteration", iteration);

        negociationBehaviour.getDataStore().put("phase", 1);

        addBehaviour(negociationBehaviour);
    }
}
