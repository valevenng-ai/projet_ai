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

                int i = (int) getDataStore().get("iteration");

                int minBudget = (int) getDataStore().get("BUDGET_MIN");

                String decision = agent.getDecision1(receivedBudget, i);

                switch (decision) {
                        case "Accept proposition" -> {
                                agent.setBudgetAccorde(receivedBudget);
                                nextState = 2;
                        }
                        case "Counter with higher budget" -> {
                                nextState = 1;

                                double counterPercentage = (5-i) * 0.05;

                                int newBudget =  (int)Math.round(minBudget * (1 + counterPercentage));

                                Proposition newProposition = new Proposition(newBudget);

                                getDataStore().put("proposition_to_send", newProposition);
                        }
                        case "End negociation" -> nextState = 4;
                }

        }
        @Override public int onEnd() { return nextState; }
    
}
