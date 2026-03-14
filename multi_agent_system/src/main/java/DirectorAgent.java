package main.java;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class DirectorAgent extends Agent{
    protected void setup(){
        Behaviour behaviour = new CyclicBehaviour((this)) {
            public void action(){
                ACLMessage msg_received = receive();
                if (msg_received != null) {
                System.out.println("Received message from agent : "
                + msg_received.getSender().getName() + " > "
                + msg_received.getContent());
                }
                ACLMessage msg_sent = new ACLMessage(ACLMessage.INFORM);
                msg_sent.addReceiver(new AID("Producer", AID.ISLOCALNAME));
                msg_sent.setContent("Hello agent1");
                send(msg_sent);

            }
        };
        addBehaviour(behaviour);
    }
}
