package main.java;

import main.java.DirectorAgent;
import main.java.ProducerAgent;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class App {
    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        Profile config = new ProfileImpl("localhost", 8888, null);
        config.setParameter("gui", "true");
        AgentContainer mc = runtime.createMainContainer(config);
        AgentController ac;
        AgentController ac2;
        try {
            ac = mc.createNewAgent("Director", DirectorAgent.class.getName(), null);
            ac.start();
            ac2 = mc.createNewAgent("Producer", ProducerAgent.class.getName(), null);
            ac2.start();
        } catch (StaleProxyException e) { }
    }
}