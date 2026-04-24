package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.agents.DirectorAgent;
import main.java.agents.ProducerAgent;
import main.java.utils.Proposition;

public class DirectorEvaluateBehaviour extends OneShotBehaviour{
        int nextState;
        public DirectorEvaluateBehaviour(DirectorAgent a){
            super(a);
        }

        @Override
        public void action() {
                DirectorAgent agent = (DirectorAgent) myAgent;

                Proposition proposition_received =
                        (Proposition) getDataStore().get("proposition_received");

                int receivedBudget = proposition_received.getBudget();


        }
        @Override public int onEnd() { return nextState; }
    
}
