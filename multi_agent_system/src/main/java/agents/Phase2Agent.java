package main.java.agents;

import main.java.utils.RepartitionBudget;

import java.util.Map;

public interface Phase2Agent {
    public int getBudgetAccorde();

    String getDecision2(int budget, int iteration, int gap, boolean floors_respected);

    public Map<String, Integer> getPlanchers();

    RepartitionBudget genererContreOffrePhase2(
            RepartitionBudget recue,
            int iteration);
}
