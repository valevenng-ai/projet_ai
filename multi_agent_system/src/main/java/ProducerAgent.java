package main.java;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;


public class ProducerAgent extends Agent{
    static private int BUDGET_MAX = 20_000_000; 
    static private int BUDGET_TARGET = 16_000_000; 
    static private int DUREE = 120; 

    protected void setup(){
        Proposition proposition = new Proposition(BUDGET_TARGET, DUREE);

        ACLMessage msg_sent = new ACLMessage(ACLMessage.INFORM);
                msg_sent.addReceiver(new AID("Director", AID.ISLOCALNAME));
                msg_sent.setContent("20");
                send(msg_sent);
                
        Behaviour behaviour = new CyclicBehaviour((this)) {
            public void action(){
                ACLMessage msg_received = receive();
                if (msg_received != null) {
                System.out.println("Received message from agent : "
                + msg_received.getSender().getName() + " > "
                + msg_received.getContent());
                }
                ACLMessage msg_sent = new ACLMessage(ACLMessage.INFORM);
                msg_sent.addReceiver(new AID("Director", AID.ISLOCALNAME));
                msg_sent.setContent("Hello agent1");
                send(msg_sent);

            }
        };
        addBehaviour(behaviour);
    }
}
