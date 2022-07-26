package main;

public class Match {
    private boolean played;
    private boolean friendly;
    private League league;
    private int round;

    private Team homeTeam;
    private Team awayTeam;

    private int homeScore;
    private int awayScore;

    private int homeAttempts;
    private int awayAttempts;

    /* For friendlies */
    public Match(Team homeTeam, Team awayTeam) {
        /* The teams playing in the match */
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        /* A new instance of match initially a fixture */
        played = false;
        friendly = true;
    }

    /* For league matches */
    public Match(League league, Team homeTeam, Team awayTeam, int round) {
        /* The league of the match and the round */
        this.league = league;
        this.round = round;

        /* The teams playing in the match */
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;


        /* A new instance of match initially a fixture */
        played = false;
        friendly = false;
    }

    public League getLeague() {
        if (friendly) {
            return null;
        }
        return this.league;
    }

    public int getRound() {
        if (friendly) {
            return 0;
        }
        return this.round;
    }

    public Team getHomeTeam() {
        return this.homeTeam;
    }

    public Team getAwayTeam() {
        return this.awayTeam;
    }

    public int getHomeScore() {
        if (!played) {
            return 0;
        }
        return this.homeScore;
    }

    public int getAwayScore() {
        if (!played) {
            return 0;
        }
        return this.awayScore;
    }

    public int getHomeAttempts() {
        if (!played) {
            return 0;
        }
        return this.homeAttempts;
    }

    public int getAwayAttempts() {
        if (!played) {
            return 0;
        }
        return this.awayAttempts;
    }

    public void printResult() {
        System.out.println(this.homeTeam.getName() + " " +
         this.homeScore + " - " + 
         this.awayScore + " " + this.awayTeam.getName());
    }

    public void setHomeTeam(Team hT) {
        this.homeTeam = hT;
    }

    public void setAwayTeam(Team aT) {
        this.awayTeam = aT;
    }

    public void updateScores(boolean qR, int HS, int AS, int HA, int AA) {
        /* Only update score if the match has been played */
        this.played = true;

        this.homeScore = HS;
        this.awayScore = AS;

        this.homeAttempts = HA;
        this.awayAttempts = AA;

        /* If the match is quick run i.e. simulated under a league
         * print the result
         */
        if (qR) {
            this.printResult();
        }
    }
}
