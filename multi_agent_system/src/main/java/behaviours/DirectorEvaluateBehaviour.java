package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.agents.DirectorAgent;
import main.java.utils.Proposition;

public class DirectorEvaluateBehaviour extends OneShotBehaviour{
        public DirectorEvaluateBehaviour(DirectorAgent a){
            super(a);
            proposition = p;
        }

        @Override
        public void action() {
        }
        @Override public int onEnd() { return 1; }
    
}
