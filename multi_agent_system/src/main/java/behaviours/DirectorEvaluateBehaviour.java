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
        }

        @Override
        public void action() {
                Proposition proposition_received = (Proposition) getDataStore().get("proposition_received");
                int budget_actuel = proposition_received.getBudget() + 2_500_000;
                int duree = proposition_received.getDuree() + 10;
                Proposition new_proposition = new Proposition(budget_actuel, duree);
                getDataStore().put("proposition_to_send", new_proposition);
        }
        @Override public int onEnd() { return 1; }
    
}
