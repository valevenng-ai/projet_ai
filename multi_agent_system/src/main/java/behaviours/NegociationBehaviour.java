package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;

public class NegociationBehaviour extends FSMBehaviour{
    private static final String WAIT  = "WAIT_REPLY";
    private static final String EVAL  = "EVALUATE";
    private static final String SEND  = "SEND_PROPOSITION";
    private static final String ACC = "ACCEPT";

    private static final int TO_EVAL = 0;
    private static final int TO_SEND = 1;
    private static final int TO_ACC = 2;
    private static final int TO_WAIT = 3;

    public NegociationBehaviour(Agent a){
        super(a);
        this.registerFirstState(new WaitPropositionBehaviour(), WAIT);
        this.registerState(new EvaluateBehaviour(), EVAL);
        this.registerState(new SendPropositionBehaviour(), SEND);
        this.registerLastState(new AcceptBehaviour(), ACC);

        this.registerTransition("WAIT",  "EVAL",  TO_EVAL);
        this.registerTransition("EVAL", "SEND", TO_SEND);
        this.registerTransition("EVAL",  "ACC", TO_ACC);
        this.registerTransition("SEND",  "WAIT",  TO_WAIT);
    }

}
