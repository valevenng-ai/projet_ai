package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.utils.Proposition;

public class EvaluateBehaviour extends OneShotBehaviour{

        public EvaluateBehaviour(Agent a){
            super(a);
        }

        @Override
        public void action() {
        }
        @Override public int onEnd() { return 1; }
    }
