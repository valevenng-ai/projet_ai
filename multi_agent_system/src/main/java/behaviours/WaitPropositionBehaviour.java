package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.utils.Proposition;

public class WaitPropositionBehaviour extends OneShotBehaviour{

        private int nextState;

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
            int performative = reply.getPerformative();

            switch (performative) {

                case ACLMessage.PROPOSE:
                    // Nouvelle proposition
                    Proposition p = Proposition.fromJSON(reply.getContent());
                    getDataStore().put("proposition_received", p);

                    nextState = 0;
                    break;

                case ACLMessage.ACCEPT_PROPOSAL:
                    // Accord trouvé
                    System.out.println("Agreement reached!");

                    nextState = 5;
                    break;

                case ACLMessage.CANCEL:
                    // Fin de négociation sans accord
                    System.out.println("Negotiation cancelled.");

                    nextState = 5;
                    break;

                default:
                    // Cas inattendu
                    System.out.println("Unknown performative: " + performative);
                    nextState = 0;
                    break;
            }
        }
        @Override public int onEnd() {return nextState; }
    }
