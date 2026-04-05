package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.utils.Proposition;

public class WaitPropositionBehaviour extends OneShotBehaviour{

        public WaitPropositionBehaviour(Agent a){
            super(a);
        }

        @Override
        public void action() {
            ACLMessage reply = null;
            while (reply == null){
                reply = myAgent.receive();
                if (reply == null) block(100);
            }
            System.out.println("Received message from agent : "
            + reply.getSender().getName() + " > "
            + "\n" + reply.getContent().toString());
            Proposition p = Proposition.fromJSON(reply.getContent());
            getDataStore().put("proposition_received", p);
        }
        @Override public int onEnd() {return 0; }
    }
