package main.java.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import main.java.agents.DirectorAgent;
import main.java.utils.Proposition;

public class NegociationBehaviour extends FSMBehaviour {
    private static final String WAIT  = "WAIT_REPLY";
    private static final String EVAL  = "EVALUATE";
    private static final String SEND  = "SEND_PROPOSITION";
    private static final String ACC = "ACCEPT";

    private static final int TO_EVAL = 0;
    private static final int TO_SEND = 1;
    private static final int TO_ACC = 2;
    private static final int TO_WAIT = 3;

    private String otherAgent;

    public NegociationBehaviour(Agent a){
        super(a);
        registerFirstState(new WaitPropositionBehaviour(a), WAIT);
        registerState(new EvaluateBehaviour(a), EVAL);
        registerState(new SendPropositionBehaviour(a), SEND);
        registerLastState(new AcceptBehaviour(a), ACC);

        registerTransition("WAIT",  "EVAL",  TO_EVAL);
        registerTransition("EVAL", "SEND", TO_SEND);
        registerTransition("EVAL",  "ACC", TO_ACC);
        registerTransition("SEND",  "WAIT",  TO_WAIT);
    }

    

    
    

    
}
