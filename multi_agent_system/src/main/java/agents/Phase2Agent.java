package main.java.agents;

import main.java.utils.RepartitionBudget;

import java.util.Map;

public interface Phase2Agent {
    public void setBudgetAccorde(int budget);
    public int getBudgetAccorde();

    String getDecision2(int budget, int iteration, int gap, boolean floors_respected);

    public Map<String, Integer> getPlanchers();

    public RepartitionBudget genererPremiereOffrePhase2();

    RepartitionBudget genererContreOffrePhase2(
            RepartitionBudget recue,
            int iteration);
}
