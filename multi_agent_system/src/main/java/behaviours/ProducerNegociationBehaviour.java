package main.java.behaviours;

import jade.core.behaviours.DataStore;
import jade.core.behaviours.FSMBehaviour;
import main.java.agents.ProducerAgent;


public class ProducerNegociationBehaviour extends FSMBehaviour {
    private static final int TO_EVAL = 0;
    private static final int TO_SEND = 1;
    private static final int TO_ACC = 2;
    private static final int TO_WAIT = 3;


    public ProducerNegociationBehaviour(ProducerAgent a){
        super(a);
        DataStore ds = new DataStore();
        setDataStore(ds);

        WaitPropositionBehaviour s1 = new WaitPropositionBehaviour(a);
        s1.setDataStore(ds);
        registerFirstState(s1, "WAIT_REPLY");

        ProducerEvaluateBehaviour s2 = new ProducerEvaluateBehaviour(a);
        s2.setDataStore(ds);
        registerState(s2, "EVALUATE");

        SendPropositionBehaviour s3 = new SendPropositionBehaviour(a);
        s3.setDataStore(ds);
        registerState(s3, "SEND_PROPOSITION");

        AcceptBehaviour s4 = new AcceptBehaviour(a);
        s4.setDataStore(ds);
        registerLastState(s4, "ACCEPT");

        registerTransition("WAIT_REPLY",  "EVALUATE",  TO_EVAL);
        registerTransition("EVALUATE", "SEND_PROPOSITION", TO_SEND);
        registerTransition("EVALUATE",  "ACCEPT", TO_ACC);
        registerTransition("SEND_PROPOSITION",  "WAIT_REPLY",  TO_WAIT);
    }

    

    
    

    
}
