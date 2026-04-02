package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.utils.Proposition;

public class AcceptBehaviour extends OneShotBehaviour{

        public AcceptBehaviour(Agent a){
            super(a);
        }

        @Override
        public void action() {
            System.out.println("accept");
            myAgent.doDelete();
        }
        
    }
