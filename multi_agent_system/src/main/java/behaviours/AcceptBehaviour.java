package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.agents.ProducerAgent;
import main.java.utils.Proposition;

// Behaviour pour envoyer un message ACCEPT_PROPOSAL
public class AcceptBehaviour extends OneShotBehaviour{
        int nextState;

        public AcceptBehaviour(Agent a){
            super(a);
        }

        @Override
        public void action() {
            int phase = (int) getDataStore().get("phase");
            if (phase == 1){
                ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                if (myAgent instanceof ProducerAgent){
                    msg.addReceiver(new AID("Director", AID.ISLOCALNAME));
                }
                else {
                    msg.addReceiver(new AID("Producer", AID.ISLOCALNAME));
                }

                msg.setContent("------ Phase 2 ------");
                myAgent.send(msg);

                getDataStore().put("phase", 2);
                getDataStore().put("iteration", 0);

                nextState = 7;
            }
            else{
                ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                if (myAgent instanceof ProducerAgent){
                    msg.addReceiver(new AID("Director", AID.ISLOCALNAME));
                }
                else {
                    msg.addReceiver(new AID("Producer", AID.ISLOCALNAME));
                }
                msg.setContent("Proposal accepted");
                myAgent.send(msg);
                nextState = 5;
            }
        }

    @Override public int onEnd() {return nextState; }
    }
