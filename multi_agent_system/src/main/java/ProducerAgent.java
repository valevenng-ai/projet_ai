package main.java;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;


public class ProducerAgent extends Agent{
    final static private int BUDGET_MAX = 20_000_000; 
    final static private int BUDGET_TARGET = 16_000_000; 
    final static private int DUREE = 120; 

    protected void setup(){
        Proposition proposition = new Proposition(ProducerAgent.BUDGET_TARGET, ProducerAgent.DUREE);

        ACLMessage msg_sent = new ACLMessage(ACLMessage.INFORM);
                msg_sent.addReceiver(new AID("Director", AID.ISLOCALNAME));
                msg_sent.setContent(proposition.toJSON());
                send(msg_sent);
                
        Behaviour behaviour = new OneShotBehaviour((this)) {
            public void action(){
                ACLMessage msg_received = receive();
                if (msg_received != null) {
                System.out.println("Received message from agent : "
                + msg_received.getSender().getName() + " > "
                + msg_received.getContent());
                }
                else{
                    block();
                }
                ACLMessage msg_sent = new ACLMessage(ACLMessage.INFORM);
                msg_sent.addReceiver(new AID("Director", AID.ISLOCALNAME));
                msg_sent.setContent("Hello agent1");
                send(msg_sent);
                doDelete();
            }
        };
        addBehaviour(behaviour);
    }
}
