package main.java.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import main.java.agents.DirectorAgent;
import main.java.agents.Phase2Agent;
import main.java.agents.ProducerAgent;
import main.java.utils.RepartitionBudget;

import java.util.Map;

public class RepartitionEvaluateBehaviour extends OneShotBehaviour {
    int nextState;

    public RepartitionEvaluateBehaviour(Agent a){
        super(a);
    }

    public static boolean respectePlanchers(
            RepartitionBudget p,
            Map<String, Integer> planchers) {

        if (p.getScript() < planchers.getOrDefault("script", 0)) return false;
        if (p.getProduction() < planchers.getOrDefault("production", 0)) return false;
        if (p.getCasting() < planchers.getOrDefault("casting", 0)) return false;
        if (p.getVfx() < planchers.getOrDefault("vfx", 0)) return false;
        if (p.getMusic() < planchers.getOrDefault("music", 0)) return false;

        return true;
    }

    public static int calculEcartTotal(
            RepartitionBudget p1,
            RepartitionBudget p2) {

        int ecart = 0;

        ecart += Math.abs(p1.getScript() - p2.getScript());
        ecart += Math.abs(p1.getProduction() - p2.getProduction());
        ecart += Math.abs(p1.getCasting() - p2.getCasting());
        ecart += Math.abs(p1.getVfx() - p2.getVfx());
        ecart += Math.abs(p1.getMusic() - p2.getMusic());

        return ecart;
    }

    @Override
    public void action() {
        Phase2Agent agent = (Phase2Agent) myAgent;
        RepartitionBudget propositionReceived =
                (RepartitionBudget) getDataStore()
                        .get("repartition_received");

        RepartitionBudget propositionCourante =
                (RepartitionBudget) getDataStore()
                        .get("repartition_courante");

        int iteration = (int) getDataStore().get("iteration");


        int ecartTotal = calculEcartTotal(
                propositionReceived, propositionCourante
        );

        String decision;

        int budget = agent.getBudgetAccorde();

        boolean planchersRespected = respectePlanchers(
                propositionReceived, agent.getPlanchers()
        );

        decision = agent.getDecision2(budget, iteration,
                ecartTotal, planchersRespected
                );


        switch (decision) {
            case "Accept" -> nextState = 2;
            case "Reevaluate" -> {
                nextState = 1;

                RepartitionBudget newProposition =
                        agent.genererContreOffrePhase2(
                                propositionReceived, iteration
                        );

                getDataStore().put(
                        "repartition_to_send", newProposition
                );
            }
        }
    }
}
