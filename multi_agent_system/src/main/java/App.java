package main.java;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import main.java.agents.DirectorAgent;
import main.java.agents.ProducerAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class App {
    final static private int BUDGET_MAX = 20_000_000; 
    final static private int BUDGET_MIN = 15_000_000;

    private static final String API_URL = "https://api.ai-raison.com/executions/PRJ31125/latest";
    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        Profile config = new ProfileImpl("localhost", 8888, null);
        config.setParameter("gui", "true");
        AgentContainer mc = runtime.createMainContainer(config);
        AgentController ac;
        AgentController ac2;
        try {
            ac = mc.createNewAgent("Director", DirectorAgent.class.getName(), null);
            ac2 = mc.createNewAgent("Producer", ProducerAgent.class.getName(), null);
            ac2.start();
            ac.start();
        } catch (StaleProxyException e) { }
    }
}