package main.java.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
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


    public DirectorNegociationBehaviour(DirectorAgent a){
        super(a);

        DataStore ds = new DataStore();
        setDataStore(ds);

        WaitPropositionBehaviour s1 = new WaitPropositionBehaviour(a);
        s1.setDataStore(ds);
        registerFirstState(s1, "WAIT_REPLY");

        DirectorEvaluateBehaviour s2 = new DirectorEvaluateBehaviour(a);
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
