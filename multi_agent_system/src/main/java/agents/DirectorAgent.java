package main.java.agents;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import main.java.behaviours.DirectorNegociationBehaviour;
import main.java.utils.Proposition;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DirectorAgent extends Agent{
    int budget_actuel;
    int duree_actuel;
    protected void setup(){

        addBehaviour(new DirectorNegociationBehaviour(this));

    }
}
