package main.java.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import main.java.agents.ProducerAgent;

public class EndBehaviour extends OneShotBehaviour {
    public EndBehaviour(Agent a){
        super(a);
    }

    @Override
    public void action() {
        System.out.println("------Négociation terminée------");
    }
}
