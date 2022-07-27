package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class League {
    /* General */
    private int ID;
    private String name;
    private String country;
    private int division;


    /* League table and results */
    private List<Team> teams = new ArrayList<Team>();
    private List<Match> matches = new ArrayList<Match>();
    private Comparator<Team> LTSorter = new League.LTSorter();

    /* For inputs */
    private Scanner input = new Scanner(System.in);

    public League(int id, String lN, String lC, int lD) {
        this.ID = id;
        this.name = lN;
        this.country = lC;
        this.division = lD;
    }

    public League(int id, String lN, String lC, int lD, List<Team> teams) {
        this.ID = id;
        this.name = lN;
        this.country = lC;
        this.division = lD;
        this.teams = teams;
    }

    public int getID() {
        return this.ID;
    }

    public String getName() {
        return this.name;
    }

    public String getCountry() {
        return this.country;
    }

    public int getDivision() {
        return this.division;
    }

    public List<Team> getTeams() {
        return this.teams;
    }

    public void setTeams(List<Team> nT) {
        this.teams = nT;
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

    public void generateFixtures() {
        this.clearResults();

        int teamSize = this.teams.size();
        int totalRounds = teamSize - 1;
        int matchesPerRound = teamSize / 2;

        Match[][] rounds = new Match[totalRounds][matchesPerRound];

        /* For each round */
        for (int round = 0; round < totalRounds; round++) {
            /* For each match in that round */
            for (int match = 0; match < matchesPerRound; match++) {
                int homeIndex = (round + match) % (teamSize - 1);
                int awayIndex = (teamSize - 1 - match + round) % (teamSize - 1);

                /* Last team stays in the same place while others rotate around it */
                if (match == 0) {
                    awayIndex = (teamSize - 1);
                }

                /* New match using indexes for the home and away team */
                Match nM = new Match(this, teams.get(homeIndex), teams.get(awayIndex), round + 1);

                /* Allocate match in this round */
                rounds[round][match] = nM;
            }
        }

        /* Interleave so that home and away matches are fairly and evenly dispersed */

        Match[][] interleaved = new Match[totalRounds][];

        int even = 0;
        int odd = (teamSize / 2);
        
        for (int p = 0; p < rounds.length; p++) {
            if (p % 2 == 0) {
                even++;
                interleaved[p] = rounds[even - 1];
            } else {
                odd++;
                interleaved[p] = rounds[odd - 1];
            }
        }

        rounds = interleaved;

        /* Last team cannot be away for every game so flip them to home on odd rounds */

        for (int round = 0; round < rounds.length; round++) {
            if (round % 2 == 1) {
                rounds[round][0] = flipMatch(rounds[round][0]);
            }
        }

        /* Fixtures generated; simulate and display the matches */
        simulateSeason(rounds);
    }

    /* Comparator for sorting teams in league table */
    public class LTSorter implements Comparator<Team> {
        @Override
        public int compare(Team t1, Team t2) {
            /* Priority of comparisons: Compare by points */
            int pts = Integer.compare(t2.getPoints(), t1.getPoints());
            if (pts == 0) {
                /* Compare by goal-difference */
                int GD = Integer.compare(t2.getGD(), t1.getGD());
                if (GD == 0) {
                    /* Compare by goals scored */
                    int GF = Integer.compare(t2.getGF(), t1.getGF());
                    if (GF == 0) {
                        /* Compare by goals conceded */
                        int GA = Integer.compare(t1.getGA(), t2.getGA());
                        if (GA == 0) {
                            /* Compare by current positions */
                            int pos = Integer.compare(t1.getPosition(), t2.getPosition());
                            if (pos == 0) {
                                /* DEFAULT: Alphabetically sort the teams initially */
                                int name = t2.getName().compareTo(t1.getName());
                                return name;
                            }
                            return pos;
                        }
                        return GA;
                    }
                    return GF;
                }
                return GD;
            }
            return pts;
        }
    }

    public Team updateLeagueTable(int round, boolean showTable) {

        /* Sort teams using the league table comparator */
        Collections.sort(teams, LTSorter);

        if (showTable) {
            /* Display league table */

            System.out.printf("\n%-6s%-20s%-6s%-6s%-6s%-6s%-6s%-6s%-6s%-6s\n\n",
                    "POS", "TEAM", "MP", "W", "D", "L", "GF", "GA", "GD", "PTS");

            for (int i = 0; i < teams.size(); i++) {
                System.out.printf("%-6d%-20s%-6d%-6d%-6d%-6d%-6d%-6d%-6d%-6d\n",
                        (i + 1),
                        teams.get(i).getName(),
                        round,
                        teams.get(i).getWins(),
                        teams.get(i).getDraws(),
                        teams.get(i).getLosses(),
                        teams.get(i).getGF(),
                        teams.get(i).getGA(),
                        teams.get(i).getGD(),
                        teams.get(i).getPoints());
            }
        }

        /* Return leader of table after sorted league table */
        return teams.get(0);
    }

    public void inputOptions(String m) {
        String answer = "";
        if (m.equals("SeasonStarted")) {
            System.out.println("\n(a) Next round \n(b) Exit League Season");
            answer = input.nextLine().toLowerCase();
            switch (answer) {
                case "a":
                    break;
                case "b":
                    System.out.println("Main Menu WIP!");
                    break;
                default:
                    System.out.println("Invalid option, type in the letter of one of the options...");
            }
        } else if (m.equals("SeasonFinished")) {
            System.out.println("\n(a) Restart season \n(b) Back to Main Menu");
            answer = input.nextLine().toLowerCase();
            switch (answer) {
                case "a":
                    generateFixtures();
                    break;
                case "b":
                    System.out.println("Main Menu WIP!");
                    break;
                default:
                    System.out.println("Invalid option, type in the letter of one of the options...");
            }
        }
    }

    public void simulateSeason(Match[][] rounds) {
        /* Simulation engine for matches */
        MatchEngine ME = MatchEngine.getInstance();

        /* Track the leader of the league table for each round */
        Team leader = updateLeagueTable(0, false);

        System.out.println("\n" + this.name.toUpperCase() + "\n==== Season started \n ");
        /* First half of the season */
        for (int r = 0; r < rounds.length; r++) {
            System.out.println("\n===== Round " + (r + 1));

            for (int m = 0; m < rounds[r].length; m++) {
                Match cM = rounds[r][m];
                ME.startGame(true, cM);
            }

            inputOptions("SeasonStarted");
            leader = updateLeagueTable(r + 1, false);
        }

        System.out.println("\n ==== Half-way into the season; commencing second-half of season");
        System.out.println("\n" + leader.getName() + " is leading the table!");
        leader = updateLeagueTable((teams.size() - 1), true);
        inputOptions("SeasonStarted");

        /* Second half of the season: mirror of first-half fixtures */
        int rC = rounds.length;
        for (int sR = 0; sR < rounds.length; sR++) {
            rC = rounds.length + sR;
            System.out.println("\n===== Round " + (rC + 1));

            /* For each match in that round */
            for (int sM = 0; sM < rounds[sR].length; sM++) {
                Match scM = flipMatch(rounds[sR][sM]);
                ME.startGame(true, scM);
            }

            inputOptions("SeasonStarted");
            leader = updateLeagueTable(rC + 1, false);
        }

        leader = updateLeagueTable(((teams.size() - 1) * 2), true);
        System.out.println("\n ===== End of the League Season!");
        System.out.println(leader.getName() + " are CHAMPIONS OF " + this.name.toUpperCase() + "!");
        inputOptions("SeasonFinished");
    }
}