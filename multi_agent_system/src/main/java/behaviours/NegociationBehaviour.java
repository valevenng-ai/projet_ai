package main.java.behaviours;

import jade.core.behaviours.FSMBehaviour;

public class NegociationBehaviour extends FSMBehaviour{
    private static final String SEND  = "SEND";
    private static final String WAIT  = "WAIT_REPLY";
    private static final String EVAL  = "EVALUATE";
}
