package main.java;
public class Proposition {
    private int budget;     // P    // M
    private int duree;   
    

    public Proposition(int budget, int duree) {
        this.budget = budget;
        this.duree = duree;
    }

    public int getBudget() {
        return this.budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getDuree() {
        return this.duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }
}
