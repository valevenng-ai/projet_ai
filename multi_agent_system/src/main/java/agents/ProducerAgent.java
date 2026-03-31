package main.java.agents;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import main.java.Proposition;
import main.java.behaviours.AcceptBehaviour;
import main.java.behaviours.EvaluateBehaviour;
import main.java.behaviours.NegociationBehaviour;
import main.java.behaviours.SendPropositionBehaviour;
import main.java.behaviours.WaitPropositionBehaviour;


public class ProducerAgent extends Agent{
    final static private int BUDGET_MAX = 20_000_000; 
    final static private int BUDGET_TARGET = 16_000_000; 
    final static private int DUREE = 120; 

    private static final int TO_EVAL = 0;
    private static final int TO_SEND = 1;
    private static final int TO_ACC = 2;
    private static final int TO_WAIT = 3;

    protected void setup(){
        Proposition proposition = new Proposition(ProducerAgent.BUDGET_TARGET, ProducerAgent.DUREE);

        ACLMessage msg_sent = new ACLMessage(ACLMessage.PROPOSE);
                msg_sent.addReceiver(new AID("Director", AID.ISLOCALNAME));
                msg_sent.setContent(proposition.toJSON());
                send(msg_sent);
                
        Behaviour behaviour = new CyclicBehaviour((this)) {
            public void action(){
                ACLMessage msg_received = receive();
                if (msg_received != null) {
                System.out.println("Received message from agent : "
                + msg_received.getSender().getName() + " > "
                + "\n" + msg_received.getContent().toString());
                }
                else{
                    block();
                }
                ACLMessage msg_sent = new ACLMessage(ACLMessage.INFORM);
                msg_sent.addReceiver(new AID("Director", AID.ISLOCALNAME));
                msg_sent.setContent(proposition.toJSON());
                send(msg_sent);
            }
        };
        FSMBehaviour fsm = new FSMBehaviour(this);
        fsm.registerFirstState(new WaitPropositionBehaviour(this), "WAIT_REPLY");
        fsm.registerState(new EvaluateBehaviour(this), "EVALUATE");
        fsm.registerState(new SendPropositionBehaviour(this), "SEND_PROPOSITION");
        fsm.registerLastState(new AcceptBehaviour(this), "ACCEPT");

        fsm.registerTransition("WAIT_REPLY",  "EVALUATE",  TO_EVAL);
        fsm.registerTransition("EVALUATE", "SEND_PROPOSITION", TO_SEND);
        fsm.registerTransition("EVALUATE",  "ACCEPT", TO_ACC);
        fsm.registerTransition("SEND_PROPOSITION",  "WAIT_REPLY",  TO_WAIT);
        addBehaviour(fsm);
    }
}
