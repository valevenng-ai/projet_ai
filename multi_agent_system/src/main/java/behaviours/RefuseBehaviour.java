package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.agents.ProducerAgent;
import main.java.utils.Proposition;

public class RefuseBehaviour extends OneShotBehaviour{

    public RefuseBehaviour(Agent a){
        super(a);
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
        if (myAgent instanceof ProducerAgent){
            msg.addReceiver(new AID("Director", AID.ISLOCALNAME));

        }
        else {
            msg.addReceiver(new AID("Producer", AID.ISLOCALNAME));
        }
        msg.setContent("Proposal refused");
        myAgent.send(msg);
    }

}
