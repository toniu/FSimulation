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
 
    public Match flipMatch(Match flippedMatch) {
        /* Swap home and away team */
        Team hT = flippedMatch.getHomeTeam();
        Team aT = flippedMatch.getAwayTeam();

        flippedMatch.setHomeTeam(aT);
        flippedMatch.setAwayTeam(hT);

        return flippedMatch;
    }

    public void generateFixtures(List<Team> teamList) {
        this.clearResults();

        int teams = teamList.size();
        int totalRounds = teams - 1;
        int matchesPerRound = teams / 2;

        Match[][] rounds = new Match[totalRounds][matchesPerRound];

        /* For each round */
        for (int round = 0; round < totalRounds - 1; round++) {
            /* For each match in that round */
            for (int match = 0; match < matchesPerRound - 1; match++) {
                int homeIndex = (round + match) % (teams - 1);
                int awayIndex = (teams - 1 - match + round) % (teams - 1);

                /* Last team stays in the same place while others rotate around it */
                if (match == 0) {
                    awayIndex = (teams - 1);
                }

                /* New match using indexes for the home and away team */
                Match nM = new Match(this, teamList.get(homeIndex + 1), teamList.get(awayIndex + 1), round + 1);

                /* Allocate match in this round */
                rounds[round + 1][match + 1] = nM;
            }
        }

        /* Interleave so that home and away matches are fairly and evenly dispersed */

        Match[][] interleaved = new Match[totalRounds][];

        int even = 0;
        int odd = teams / 2;

        for (int p = 0; p < rounds.length - 1; p++) {
            if (p % 2 == 0) {
                even++;
                interleaved[p + 1] = rounds[even];
            } else {
                odd++;
                interleaved[p + 1] = rounds[odd];
            }
        }

        rounds = interleaved;

        /* Last team cannot be away for every game so flip them to home on odd rounds */

        for (int round = 0; round < rounds.length - 1; round++) {
            if (round % 2 == 1) {
                rounds[round + 1][1] = flipMatch(rounds[round + 1][1]);
            }
        }

        /* Fixtures generated; simulate and display the matches */
        simulateSeason(rounds);
    }

    public void updateResults(String msg) {

    }

    public Team updateLeagueTable(int round) {
        return null;
    }

    public void simulateSeason(Match[][] rounds) {
        /* Simulation engine for matches */
        MatchEngine ME = MatchEngine.getInstance();

        /* Track the leader of the league table for each round */
        Team leader = updateLeagueTable(0);

        /* First half of the season */
        for (int r = 1; r < rounds.length; r++) {
            updateResults("==== Round " + r);

            for (int m = 1; m < rounds[r].length; m++) {
                Match cM = rounds[r][m];
                ME.startGame(true, cM.getHomeTeam(), cM.getAwayTeam());
            }

            leader = updateLeagueTable(r);
            System.out.println("\n" + leader.getName() + " is leading the table!");
        }

        /* Second half of the season: mirror of first-half fixtures */
        int rC = rounds.length;
        for (int sR = 1; sR < rounds.length; sR++) {
            rC = rounds.length + sR;
            updateResults("==== Round " + rC);

            /* For each match in that round */
            for (int sM = 1; sM < rounds[sR].length; sM++) {
                Match scM = flipMatch(rounds[sR][sM]);
                ME.startGame(true, scM.getHomeTeam(), scM.getAwayTeam());
            }

            leader = updateLeagueTable(rC);
            System.out.println("\n" + leader.getName() + " is leading the table!");
        }

        System.out.println("\n ==== End of the League Season!");
        updateResults(leader.getName() + " are CHAMPIONS OF " + this.name.toUpperCase() + "!");
    }
}