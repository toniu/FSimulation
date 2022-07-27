package main;

public class Team {
    /* General */
    private int teamID;
    private String name;
    private String abbreviation;
    private int leagueID;
    private String location;

    /* Ratings */
    private int attack;
    private int midfield;
    private int defence;
    private int OVR;

    /* Table */
    private int wins;
    private int draws;
    private int losses;
    private int GD;
    private int GF;
    private int GA;
    private int points;
    private int position;
    private int form; /* e.g. WWWLW = 4 (for four wins; maximum 5 wins) */

    public Team(int newTID, String newName, String newAbbr, int newLID, String newLoc, int newAtt, int newMid, int newDef) {
        /* Set general information */
        this.teamID = newTID;
        this.name = newName;
        this.abbreviation = newAbbr;
        this.leagueID = newLID;
        this.location = newLoc;

        /* Set ratings */
        this.attack = newAtt;
        this.midfield = newMid;
        this.defence = newDef;
        this.OVR = (int) Math.floor(((double) (newAtt + newMid + newDef)) / 3.0);

        /* Initialise league statistics */
        this.resetStats();
    }

    public void resetStats() {
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
        this.GD = 0;
        this.GF = 0;
        this.GA = 0;
        this.points = 0;
        this.position = 0;
        this.form = 0;
    }

    /* Getters */
    public int getID() {
        return this.teamID;
    }

    public int getLeagueID() {
        return this.leagueID;
    }

    public String getName() {
        return this.name;
    }

    public String getLocation() {
        return this.location;
    }

    public String getAbbreviation() {
        return this.abbreviation;
    }

    public int getAttackRating() {
        return this.attack;
    }

    public int getMidfieldRating() {
        return this.midfield;
    }

    public int getDefenceRating() {
        return this.defence;
    }

    public int getOVR() {
        return this.OVR;
    }

    public int getWins() {
        return this.wins;
    }

    public int getDraws() {
        return this.draws;
    }

    public int getLosses() {
        return this.losses;
    }

    public int getGD() {
        return this.GD;
    }

    public int getGF() {
        return this.GF;
    }

    public int getGA() {
        return this.GA;
    }

    public int getPoints() {
        return this.points;
    }

    public int getPosition() {
        return this.position;
    }

    public int getForm() {
        return this.form;
    }

    /* Setters */
    public void setWins(int nV) {
        this.wins = nV;
    }

    public void setDraws(int nV) {
        this.draws = nV;
    }

    public void setLosses(int nV) {
        this.losses = nV;
    }

    public void setGD(int nV) {
        this.GD = nV;
    }

    public void setGF(int nV) {
        this.GF = nV;
    }

    public void setGA(int nV) {
        this.GA = nV;
    }

    public void setPoints(int nV) {
        this.points = nV;
    }

    public void setPosition(int nV) {
        this.position = nV;
    }

    public void setForm(int nV) {
        /* Under a 5-match run: */
        if (nV > 5) {
            /* Maximum number of wins in form (W-W-W-W-W): 5 */
            nV = 5;
        } else if (nV < 0) {
            /* Minimum number of wins (L-L-L-L-L): 0 */
            nV = 0;
        }
        this.form = nV;
    }
}