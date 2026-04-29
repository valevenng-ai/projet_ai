package main.java.utils;

public class RepartitionBudget {

    private int script;
    private int production;
    private int casting;
    private int vfx;
    private int music;

    public RepartitionBudget(int script, int production, int casting, int vfx, int music) {
        this.script = script;
        this.production = production;
        this.casting = casting;
        this.vfx = vfx;
        this.music = music;
    }

    // ════════════════════════════════════════
    // Sérialisation JSON
    // ════════════════════════════════════════
    public String toJSON() {
        return String.format(
                "{" +
                        "\"script\":%d," +
                        "\"production\":%d," +
                        "\"casting\":%d," +
                        "\"vfx\":%d," +
                        "\"music\":%d" +
                        "}",
                script, production, casting, vfx, music
        );
    }

    // ════════════════════════════════════════
    // Désérialisation JSON
    // ════════════════════════════════════════
    public static RepartitionBudget fromJSON(String json) {

        int script = extraireInt(json, "script");
        int production = extraireInt(json, "production");
        int casting = extraireInt(json, "casting");
        int vfx = extraireInt(json, "vfx");
        int music = extraireInt(json, "music");

        return new RepartitionBudget(script, production, casting, vfx, music);
    }

    // ════════════════════════════════════════
    // Affichage lisible
    // ════════════════════════════════════════
    @Override
    public String toString() {
        return String.format(
                "Proposition Phase 2 {" +
                        "\n  Script     : %.1fM€" +
                        "\n  Production : %.1fM€" +
                        "\n  Casting    : %.1fM€" +
                        "\n  VFX        : %.1fM€" +
                        "\n  Music      : %.1fM€" +
                        "\n}",
                script / 1_000_000.0,
                production / 1_000_000.0,
                casting / 1_000_000.0,
                vfx / 1_000_000.0,
                music / 1_000_000.0
        );
    }

    // ════════════════════════════════════════
    // Parsing JSON (réutilisé)
    // ════════════════════════════════════════
    private static int extraireInt(String json, String cle) {
        try {
            String pattern = "\"" + cle + "\":";
            int debut = json.indexOf(pattern) + pattern.length();

            while (debut < json.length() &&
                    Character.isWhitespace(json.charAt(debut))) {
                debut++;
            }

            int fin = debut;
            while (fin < json.length() &&
                    json.charAt(fin) != ',' &&
                    json.charAt(fin) != '}') {
                fin++;
            }

            return Integer.parseInt(json.substring(debut, fin).trim());

        } catch (Exception e) {
            System.err.println("[PropositionPhase2] Erreur parsing int pour : " + cle);
            return 0;
        }
    }

    // ════════════════════════════════════════
    // Getters & Setters
    // ════════════════════════════════════════
    public int getScript() { return script; }
    public int getProduction() { return production; }
    public int getCasting() { return casting; }
    public int getVfx() { return vfx; }
    public int getMusic() { return music; }

    public void setScript(int script) { this.script = script; }
    public void setProduction(int production) { this.production = production; }
    public void setCasting(int casting) { this.casting = casting; }
    public void setVfx(int vfx) { this.vfx = vfx; }
    public void setMusic(int music) { this.music = music; }
}