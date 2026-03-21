package main.java;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class DirectorAgent extends Agent{
    int budget_actuel;
    int duree_actuel;
    protected void setup(){
        Behaviour behaviour = new OneShotBehaviour((this)) {
            public void action(){
                ACLMessage msg_received = receive();
                if (msg_received != null) {
                    System.out.println("Received message from agent : "
                    + msg_received.getSender().getName() + " > " + msg_received.getContent()
                    );
                    // Proposition p = Proposition.fromJSON(msg_received.getContent());
                    // System.out.println(p.toString());
                    // budget_actuel = p.getBudget() + 2_500_000;
                    // duree_actuel = p.getDuree() + 10;
                }
                else{
                    block();
                }
                Proposition p = new Proposition(budget_actuel, duree_actuel);
                ACLMessage msg_sent = new ACLMessage(ACLMessage.INFORM);
                msg_sent.addReceiver(new AID("Producer", AID.ISLOCALNAME));
                msg_sent.setContent("Hello Director");
                send(msg_sent);
                doDelete();
            }
        };
        addBehaviour(behaviour);

    }
}
