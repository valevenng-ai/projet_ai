package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.agents.Phase2Agent;
import main.java.agents.ProducerAgent;
import main.java.utils.Proposition;
import main.java.utils.RepartitionBudget;

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

            int phase = (int) getDataStore().get("phase");

            switch (performative) {

                case ACLMessage.PROPOSE:
                    if (phase == 1){
                        // Nouvelle proposition
                        Proposition p = Proposition.fromJSON(reply.getContent());
                        getDataStore().put("proposition_received", p);

                        nextState = 0;
                    }
                    else {
                        RepartitionBudget r = RepartitionBudget.fromJSON(reply.getContent());
                        getDataStore().put("repartition_received", r);

                        nextState = 6;
                    }
                    break;

                case ACLMessage.ACCEPT_PROPOSAL:
                    if (phase == 1){
                        Phase2Agent agent = (Phase2Agent) myAgent;
                        Proposition prop = (Proposition) getDataStore().get("proposition_to_send");
                        int budget = prop.getBudget();
                        agent.setBudgetAccorde(budget);
                        nextState = 7;
                        break;
                    }
                    else{
                        System.out.println("Agreement reached!");

                        nextState = 5;
                        break;
                    }


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
