package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.agents.ProducerAgent;
import main.java.utils.Proposition;

public class SendPropositionBehaviour extends OneShotBehaviour {

        public SendPropositionBehaviour(Agent a){
            super(a);
        }

        @Override
        public void action() {

            int iteration = (int) getDataStore().get("iteration");
            iteration++;
            getDataStore().put("iteration", iteration);

            Proposition prop = (Proposition) getDataStore().get("proposition_to_send");
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            if (myAgent instanceof ProducerAgent){
                msg.addReceiver(new AID("Director", AID.ISLOCALNAME));
                System.out.println("Iteration :" + iteration);

            }
            else {
                msg.addReceiver(new AID("Producer", AID.ISLOCALNAME));
            }
            msg.setContent(prop.toJSON());
            myAgent.send(msg);
        }
        @Override public int onEnd() { return 3; }
    }
