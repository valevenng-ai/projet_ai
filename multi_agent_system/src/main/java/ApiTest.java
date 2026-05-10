package main.java;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import main.java.agents.DirectorAgent;
import main.java.agents.ProducerAgent;

import main.java.utils.RepartitionBudget;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiTest {
    final static private int BUDGET_MAX = 20_000_000; 
    final static private int BUDGET_MIN = 15_000_000;

    private static final String API_URL = "https://api.ai-raison.com/executions/PRJ31125/latest";
    private static final String API_URL_DIRECTOR = "https://api.ai-raison.com/executions/PRJ32025/latest";
    private static final String API_KEY = "QwVTG1jIpX1AhVd29g8kG9GTfrJAepfV5N34xiMh";
    private static final String API_URL_PHASE2 = "https://api.ai-raison.com/executions/PRJ32125/latest";

    public static void apiPost(int budget, int iteration){
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

    public static void apiGet(){
        HttpURLConnection conn = null;

        try {
            // ─── 1. Connexion ────────────────────────────────────────────────────

            URL url = new URL(API_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("x-api-key", API_KEY);

            // ─── 2. Lecture de la réponse ────────────────────────────────────────

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

            // ─── 3. Parsing de la réponse ────────────────────────────────────────

            if (statusCode >= 200 && statusCode < 300) {
                JSONObject response = new JSONObject(responseBuilder.toString());

                // --- Lecture des éléments ---
                JSONArray elements = response.getJSONArray("elements");
                System.out.println("=== ELEMENTS (" + elements.length() + ") ===");

                for (int i = 0; i < elements.length(); i++) {
                    JSONObject element = elements.getJSONObject(i);

                    String name = element.getString("label");
                    String id   = element.getString("id");

                    System.out.println("\n  Élément " + (i + 1) + " :");
                    System.out.println("    Name : " + name);
                    System.out.println("    ID   : " + id);

                    // Paramètres de l'élément
                    JSONArray parameters = element.getJSONArray("parameters");
                    System.out.println("    Paramètres (" + parameters.length() + ") :");
                    for (int j = 0; j < parameters.length(); j++) {
                        JSONObject param = parameters.getJSONObject(j);
                        System.out.println("      - " + param.getString("name"));
                    }
                }

                // --- Lecture des options ---
                JSONArray options = response.getJSONArray("options");
                System.out.println("\n=== OPTIONS (" + options.length() + ") ===");

                for (int i = 0; i < options.length(); i++) {
                    JSONObject option = options.getJSONObject(i);

                    String name = option.getString("label");
                    String id   = option.getString("id");

                    System.out.println("\n  Option " + (i + 1) + " :");
                    System.out.println("    Name : " + name);
                    System.out.println("    ID   : " + id);
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

    public static void apiGetDirector(){
        HttpURLConnection conn = null;

        try {
            // ─── 1. Connexion ────────────────────────────────────────────────────

            URL url = new URL(API_URL_DIRECTOR);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("x-api-key", API_KEY);

            // ─── 2. Lecture de la réponse ────────────────────────────────────────

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

            // ─── 3. Parsing de la réponse ────────────────────────────────────────

            if (statusCode >= 200 && statusCode < 300) {
                JSONObject response = new JSONObject(responseBuilder.toString());

                // --- Lecture des éléments ---
                JSONArray elements = response.getJSONArray("elements");
                System.out.println("=== ELEMENTS (" + elements.length() + ") ===");

                for (int i = 0; i < elements.length(); i++) {
                    JSONObject element = elements.getJSONObject(i);

                    String name = element.getString("label");
                    String id   = element.getString("id");

                    System.out.println("\n  Élément " + (i + 1) + " :");
                    System.out.println("    Name : " + name);
                    System.out.println("    ID   : " + id);

                    // Paramètres de l'élément
                    JSONArray parameters = element.getJSONArray("parameters");
                    System.out.println("    Paramètres (" + parameters.length() + ") :");
                    for (int j = 0; j < parameters.length(); j++) {
                        JSONObject param = parameters.getJSONObject(j);
                        System.out.println("      - " + param.getString("name"));
                    }
                }

                // --- Lecture des options ---
                JSONArray options = response.getJSONArray("options");
                System.out.println("\n=== OPTIONS (" + options.length() + ") ===");

                for (int i = 0; i < options.length(); i++) {
                    JSONObject option = options.getJSONObject(i);

                    String name = option.getString("label");
                    String id   = option.getString("id");

                    System.out.println("\n  Option " + (i + 1) + " :");
                    System.out.println("    Name : " + name);
                    System.out.println("    ID   : " + id);
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

    public static String getDecision1(int budget, int iteration){
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
                    return result.getJSONObject("option").getString("label"); // 👈 retourne le label
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

    public static void apiPostDirector(int budget, int iteration){
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
            URL url = new URL(API_URL_DIRECTOR);
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

    public static void apiGetPhase2(){
        HttpURLConnection conn = null;

        try {
            // ─── 1. Connexion ────────────────────────────────────────────────────

            URL url = new URL(API_URL_PHASE2);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("x-api-key", API_KEY);

            // ─── 2. Lecture de la réponse ────────────────────────────────────────

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

            // ─── 3. Parsing de la réponse ────────────────────────────────────────

            if (statusCode >= 200 && statusCode < 300) {
                JSONObject response = new JSONObject(responseBuilder.toString());

                // --- Lecture des éléments ---
                JSONArray elements = response.getJSONArray("elements");
                System.out.println("=== ELEMENTS (" + elements.length() + ") ===");

                for (int i = 0; i < elements.length(); i++) {
                    JSONObject element = elements.getJSONObject(i);

                    String name = element.getString("label");
                    String id   = element.getString("id");

                    System.out.println("\n  Élément " + (i + 1) + " :");
                    System.out.println("    Name : " + name);
                    System.out.println("    ID   : " + id);

                    // Paramètres de l'élément
                    JSONArray parameters = element.getJSONArray("parameters");
                    System.out.println("    Paramètres (" + parameters.length() + ") :");
                    for (int j = 0; j < parameters.length(); j++) {
                        JSONObject param = parameters.getJSONObject(j);
                        System.out.println("      - " + param.getString("name"));
                    }
                }

                // --- Lecture des options ---
                JSONArray options = response.getJSONArray("options");
                System.out.println("\n=== OPTIONS (" + options.length() + ") ===");

                for (int i = 0; i < options.length(); i++) {
                    JSONObject option = options.getJSONObject(i);

                    String name = option.getString("label");
                    String id   = option.getString("id");

                    System.out.println("\n  Option " + (i + 1) + " :");
                    System.out.println("    Name : " + name);
                    System.out.println("    ID   : " + id);
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

    public static String getDecision1Director(int budget, int iteration){
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
            URL url = new URL(API_URL_DIRECTOR);
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
                    return result.getJSONObject("option").getString("label"); // 👈 retourne le label
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

    public static String getDecision2(int budget, int iteration, int gap, boolean floors_respected){
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

    public static void main(String[] args) {
        //apiGet();
        //apiPost(22_000_000, 5);
        //apiGetDirector();
        //System.out.println(getDecision1(18_000_000, 5));
        //apiPostDirector(16_000_000, 5);
        //System.out.println(getDecision1Director(16_000_000, 5));
        apiGetPhase2();
        //System.out.println(getDecision2(18_000_000, 4, 1_000_000, false));
    }
}