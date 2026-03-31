package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

import main.java.Proposition;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

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
