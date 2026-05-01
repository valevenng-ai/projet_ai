package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import main.java.agents.Phase2Agent;
import main.java.agents.ProducerAgent;
import main.java.utils.Proposition;
import main.java.utils.RepartitionBudget;

public class Phase2InitBehaviour extends OneShotBehaviour {
    public int nextState;

    public Phase2InitBehaviour(Agent a){
        super(a);
    }

    @Override
    public void action() {
        getDataStore().put("phase", 2);
        if (myAgent instanceof ProducerAgent){
            getDataStore().put("iteration", 0);
            nextState = 1;
        }
        else{
            getDataStore().put("iteration", 1);
            nextState = 3;
        }
        Phase2Agent agent = (Phase2Agent) myAgent;
        RepartitionBudget premiereOffre =  agent.genererPremiereOffrePhase2();
        getDataStore().put("repartition_to_send", premiereOffre);
    }

    @Override
    public int onEnd() {
        return nextState;
    }
}
