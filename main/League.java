package main;

import java.util.ArrayList;
import java.util.List;

public class League {
    private int ID;
    private String name;
    private List<Team> teams = new ArrayList<Team>();
    private List<Match> matches = new ArrayList<Match>();

    public League(int id, String lN, List<Team> teams) {
        this.ID = id;
        this.name = lN;
        this.teams = teams;
    }

    public int getID() {
        return this.ID;
    }

    public String getName() {
        return this.name;
    }

    public List<Team> getTeams() {
        return this.teams;
    }

    public void clearResults() {
        /* Clear results */
        this.matches.clear();

        /* Reset league table */
        for (int i = 0; i < teams.size(); i++) {
            teams.get(i).resetStats();
        }
    }

    public void generateFixtures(List<Team> teamList) {
        this.clearResults();

        int teams = teamList.size();
        int totalRounds = teams - 1;
        int matchesPerRound = teams / 2;

        Match[][] rounds = new Match[totalRounds][matchesPerRound];

        for (int round = 0; round < totalRounds - 1; round++) {
        }
    }
}