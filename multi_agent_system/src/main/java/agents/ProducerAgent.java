package main.java.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import main.java.behaviours.AcceptBehaviour;
import main.java.behaviours.EvaluateBehaviour;
import main.java.behaviours.ProducerNegociationBehaviour;
import main.java.behaviours.SendPropositionBehaviour;
import main.java.behaviours.WaitPropositionBehaviour;
import main.java.utils.Proposition;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProducerAgent extends Agent{
    final static private int BUDGET_MAX = 20_000_000; 
    final static private int BUDGET_MIN = 15_000_000;
    final static private int BUDGET_TARGET = 16_000_000; 
    final static private int DUREE = 120; 

    private static final int TO_EVAL = 0;
    private static final int TO_SEND = 1;
    private static final int TO_ACC = 2;
    private static final int TO_WAIT = 3;

    private static final String API_URL = "https://api.ai-raison.com/executions/PRJ31125/latest";
    private static final String API_KEY = "QwVTG1jIpX1AhVd29g8kG9GTfrJAepfV5N34xiMh";

    public static void apiPost(int budget){
        // ─── 1. Construction du body JSON ───────────────────────────────────────

        JSONObject paramBudget = new JSONObject();
        paramBudget.put("name", "Budget");
        paramBudget.put("value", String.valueOf(22_000_000));

        JSONObject paramMax = new JSONObject();
        paramMax.put("name", "Max");
        paramMax.put("value", String.valueOf(BUDGET_MAX));

        JSONObject paramMin = new JSONObject();
        paramMin.put("name", "Min");
        paramMin.put("value", String.valueOf(BUDGET_MIN));

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

        JSONArray elements = new JSONArray();
        elements.put(element1);
        elements.put(element2);
        elements.put(element3);

        JSONObject option = new JSONObject();
        option.put("id", "OPT402868");

        JSONArray options = new JSONArray();
        options.put(option);

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

            if (statusCode >= 200 && statusCode < 300) {
                JSONArray responseArray = new JSONArray(responseBuilder.toString());
                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject result = responseArray.getJSONObject(i);

                    JSONObject resultOption = result.getJSONObject("option");
                    String optionId    = resultOption.getString("id");
                    String optionLabel = resultOption.getString("label");

                    boolean isSolution = result.getBoolean("isSolution");
                    JSONArray explanations = result.getJSONArray("explanation");

                    System.out.println("─────────────────────────────");
                    System.out.println("Option ID    : " + optionId);
                    System.out.println("Option Label : " + optionLabel);
                    System.out.println("Is Solution  : " + isSolution);
                    System.out.println("Explanation  :");
                    for (int j = 0; j < explanations.length(); j++) {
                        System.out.println("    - " + explanations.getString(j));
                    }               
                }
            } else {
                System.out.println("Erreur serveur : " + responseBuilder);
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de la requête : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    protected void setup(){
        Proposition proposition = new Proposition(ProducerAgent.BUDGET_TARGET, ProducerAgent.DUREE);

        ACLMessage msg_sent = new ACLMessage(ACLMessage.PROPOSE);
                msg_sent.addReceiver(new AID("Director", AID.ISLOCALNAME));
                msg_sent.setContent(proposition.toJSON());
                send(msg_sent);
                
        Behaviour behaviour = new CyclicBehaviour((this)) {
            public void action(){
                ACLMessage msg_received = receive();
                if (msg_received != null) {
                System.out.println("Received message from agent : "
                + msg_received.getSender().getName() + " > "
                + "\n" + msg_received.getContent().toString());
                }
                else{
                    block();
                }
                ACLMessage msg_sent = new ACLMessage(ACLMessage.INFORM);
                msg_sent.addReceiver(new AID("Director", AID.ISLOCALNAME));
                msg_sent.setContent(proposition.toJSON());
                send(msg_sent);
            }
        };
        addBehaviour(new ProducerNegociationBehaviour(this));
    }
}
