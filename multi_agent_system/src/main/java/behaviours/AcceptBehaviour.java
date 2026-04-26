package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.agents.ProducerAgent;
import main.java.utils.Proposition;

public class AcceptBehaviour extends OneShotBehaviour{

        public AcceptBehaviour(Agent a){
            super(a);
        }

        @Override
        public void action() {
            ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            if (myAgent instanceof ProducerAgent){
                msg.addReceiver(new AID("Director", AID.ISLOCALNAME));

            }
            else {
                msg.addReceiver(new AID("Producer", AID.ISLOCALNAME));
            }
            msg.setContent("Proposal accepted");
            myAgent.send(msg);
        }
        
    }
