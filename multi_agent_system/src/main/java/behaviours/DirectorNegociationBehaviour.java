package main.java.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import main.java.agents.DirectorAgent;
import main.java.utils.Proposition;

public class DirectorNegociationBehaviour extends FSMBehaviour{
    private static final int TO_EVAL = 0;
    private static final int TO_SEND = 1;
    private static final int TO_ACC = 2;
    private static final int TO_WAIT = 3;


    public ProducerNegociationBehaviour(DirectorAgent a){
        super(a);
        registerFirstState(new WaitPropositionBehaviour(a), "WAIT_REPLY");
        registerState(new DirectorEvaluateBehaviour(a), "EVALUATE");
        registerState(new SendPropositionBehaviour(a), "SEND_PROPOSITION");
        registerLastState(new AcceptBehaviour(a), "ACCEPT");

        registerTransition("WAIT_REPLY",  "EVALUATE",  TO_EVAL);
        registerTransition("EVALUATE", "SEND_PROPOSITION", TO_SEND);
        registerTransition("EVALUATE",  "ACCEPT", TO_ACC);
        registerTransition("SEND_PROPOSITION",  "WAIT_REPLY",  TO_WAIT);
    }
}
