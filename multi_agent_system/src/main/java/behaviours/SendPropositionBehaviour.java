package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.agents.ProducerAgent;
import main.java.utils.Proposition;
import main.java.utils.RepartitionBudget;

// Behaviour pour envoyer un message à l'autre agent
public class SendPropositionBehaviour extends OneShotBehaviour {

        public SendPropositionBehaviour(Agent a){
            super(a);
        }

        @Override
        public void action() {

            int iteration = (int) getDataStore().get("iteration");
            iteration++;
            getDataStore().put("iteration", iteration);


            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            if (myAgent instanceof ProducerAgent){
                msg.addReceiver(new AID("Director", AID.ISLOCALNAME));
                System.out.println("Iteration :" + iteration);

            }
            else {
                msg.addReceiver(new AID("Producer", AID.ISLOCALNAME));
            }

            int phase = (int) getDataStore().get("phase");

            if (phase == 1){
                Proposition prop = (Proposition) getDataStore().get("proposition_to_send");
                msg.setContent(prop.toJSON());
            }
            else {
                RepartitionBudget rep = (RepartitionBudget) getDataStore().get("repartition_to_send");
                msg.setContent(rep.toJSON());

            }
            myAgent.send(msg);
        }
        @Override public int onEnd() { return 3; }
    }
