package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MatchEngine {

    /* Match engine is a singleton */
    private static MatchEngine singleton = null;

    public static synchronized MatchEngine getInstance() {
        if (singleton == null) {
            singleton = new MatchEngine();
        }
        return singleton;
    }

    /* Input */
    Scanner input = new Scanner(System.in);

    /* Match states */
    String matchState = "";
    boolean quickRun = false;
    boolean isPlaying = true;

    /* Simulation randomiser */
    Random RNG = new Random();

    /* Events */
    List<String> events = new ArrayList<String>();

    /* Scoreboard variables */
    int HScore = 0;
    int AScore = 0;
    int half = 0;
    int clock = 0;

    /* Team Variables */
    Team homeTeam = null;
    Team awayTeam = null;

    double homeAttack = 0;
    double homeMidfield = 0;
    double homeDefence = 0;
    double homeOVR = 0;

    double awayAttack = 0;
    double awayMidfield = 0;
    double awayDefence = 0;
    double awayOVR = 0;

    int HAttempts = 0;
    int AAttempts = 0;

    double homePossession = 0;
    double awayPossession = 0;
    double hPosCount = 0;
    double aPosCount = 0;

    /* Printing messages */
    public void print(String msg) {
        System.out.println(msg);
    }

    /* Is the match currently playing? */
    public boolean isMatchPlaying() {
        return this.isPlaying;
    }

    /* Clear all events */
    public void clearEvents() {
        events.clear();
    }

    /* Calculate possession */
    public void calculatePossession() {
        homePossession = Math.floor((hPosCount / (hPosCount + aPosCount)) * 100);
        awayPossession = 100 - homePossession;
    }

    /* Reset score */
    public void resetScore(Match m) {
        /* Match */
        m.resetStats();
        /* Local */
        this.HScore = 0;
        this.AScore = 0;
        HAttempts = 0;
        AAttempts = 0;

        hPosCount = 0;
        aPosCount = 0;
    }

    /* Increment clock */
    public void incrementClock() {
        if (clock == 45) {
            clock++;
        }
    }

    /* New Event */
    public void newEvent(int minsPassed, String gameState, boolean showClock, int half) {
        String clockMsg = minsPassed + "'";
        String event = null;

        /* Present stoppage time on clock */
        if (minsPassed > 45 && half == 1) {
            clockMsg = 45 + "+" + (minsPassed - 45);
        } else if (minsPassed > 90 && half == 2) {
            clockMsg = 90 + "+" + (minsPassed - 90);
        } else if (minsPassed > 105 && half == 3) {
            clockMsg = 105 + "+" + (minsPassed - 105);
        } else if (minsPassed > 120 && half == 4) {
            clockMsg = 120 + "+" + (minsPassed - 120);
        }

        /* Create a new event */
        /*-- Home team scored --*/
        if (gameState.equals("HGoal")) {
            /*  */
            event = clockMsg + ": GOAL for " + homeTeam.getAbbreviation();
            /*-- Away team scored --*/
        } else if (gameState.equals("AGoal")) {
            /*  */
            event = clockMsg + ": GOAL for " + awayTeam.getAbbreviation();
            /*-- Start / End of half */
        } else if (gameState.equals("HT") ||
                gameState.equals("FT") ||
                gameState.equals("Kickoff")) {
            /*  */
            if (half == 1) {
                event = "======== KICK-OFF --";
            } else if (half == 2) {
                event = "\n======== HALF-TIME: " + homeTeam.getName() +
                        " " + HScore + " - " +
                        AScore + " " + awayTeam.getName() + " -- \n ";
            } else if (half == 3) {
                event = "\n======== FULL-TIME: " + homeTeam.getName() +
                        " " + HScore + " - " +
                        AScore + " " + awayTeam.getName() + " -- \n";
            } else if (half == 4) {
                event = "======== EXTRA-TIME HALF TIME: " + homeTeam.getName() +
                        " " + HScore + " - " +
                        AScore + " " + awayTeam.getName() + " -- ";
            }
            /*-- Resumed match --*/
        } else {
            event = clockMsg + ": " + gameState;
        }

        events.add(event);
        print(event);
    }

    /* Clamps the value to be within specified number range */
    public double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }

    /*-- Functions --*/

    /**
     * Determines if there is going to be action in the minute
     * 60% chance of some action taking place
     * 
     * @return action will happen or not
     */
    public boolean beginAction() {
        int o = RNG.nextInt(200);
        return (o <= 60);
    }

    public String createChance(String side) {
        if (side.equals("H")) {
            /* Home team has a chance... */
            int hLimit = (int) Math.floor((homeAttack / (awayDefence + homeAttack)) * 100);
            /* Bounds: probability falls in either upper bound, lower bound or the "middle" (nothing happens) */
            double hLBound = this.clamp(hLimit * 0.75, 5, 90);
            double hUBound = this.clamp(hLimit * 1.25, hLimit, 95);

            int hP = RNG.nextInt(100) + 1;
            /* Print test 
            System.out.println("\nhLimit hLBound hUBound hP");
            System.out.println(hLimit + " " + hLBound + " " + hUBound + " " + hP); */
 
            if (hP <= hLBound) {
                /* Home goal */
                return "HGoal";
            } else if (hP >= hUBound) {
                /* Posession is lost from home team */
                return "noPos";
            } else {
                /* Chance could lead to a home set-piece... */
                int heC = RNG.nextInt(100) + 1;
                if (heC <= 35) {
                    /* Home team has free-kick... */
                    return "Home Freekick";
                } else if (heC > 35 && heC <= 80) {
                    /* Home team has a corner... */
                    return "Home Corner";
                } else
                    /* Home team has a penalty... */
                    return "Home Penalty";
            }
        } else {
            /* Away team has a chance... */
            int aLimit = (int) Math.floor((awayAttack / (homeDefence + awayAttack)) * 100);
            /* Bounds: probability falls in either upper bound, lower bound or the "middle" (nothing happens) */
            double aLBound = this.clamp(aLimit * 0.60, 5, 90);
            double aUBound = this.clamp(aLimit * 1.25, aLimit, 95);

            int aP = RNG.nextInt(100) + 1;

            /* Print test 
            System.out.println("\naLimit aLBound aUBound aP");
            System.out.println(aLimit + " " + aLBound + " " + aUBound + " " + aP); */

            if (aP <= aLBound) {
                /* Away goal */
                return "AGoal";
            } else if (aP >= aUBound) {
                /* Posession is lost from away team */
                return "noPos";
            } else {
                /* Chance could lead to an away set-piece... */
                int aeC = RNG.nextInt(100) + 1;
                if (aeC <= 25) {
                    /* Away team has free-kick... */
                    return "Away Freekick";
                } else if (aeC > 25 && aeC <= 95) {
                    /* Away team has a corner... */
                    return "Away Corner";
                } else
                    /* Away team has a penalty... */
                    return "Away Penalty";
            }
        }
    }

    /**
     * Determines if the set-piece will be converted
     * 15% chance of scoring from corner,
     * 35% chance of scoring from free-kick,
     * 75% chance of penalty being scored,
     * 
     * @return outcome of set-piee
     */
    public String createSetPiece(String side, String setpiece) {

        /* Set event message based on which side has the set-piece */
        String goal = "";
        String corner = "";
        String cornerfailed = "";
        String freekick = "";
        String freekickfailed = "";
        String penaltymiss = "";

        /* Home-team is doing a set-piece */
        if (side.equals("H")) {
            goal = "HGoal";
            corner = "Home Corner";
            cornerfailed = "Home Corner Failed";
            freekick = "Home Freekick";
            freekickfailed = "Home Freekick Miss";
            penaltymiss = "Home Penalty Miss";
        } else {
            /* Away-team is doing a set-piece */
            goal = "AGoal";
            corner = "Away Corner";
            cornerfailed = "Away Corner Failed";
            freekick = "Away Freekick";
            freekickfailed = "Away Freekick Miss";
            penaltymiss = "Away Penalty Miss";
        }

        /* Team is doing a set-piece... */
        if (setpiece.equals(corner)) {
            /* Will they score from this corner? */
            int CScored = RNG.nextInt(100) + 1;
            if (CScored <= 15) {
                /* Team scored from a corner! */
                return goal;
            } else {
                /* Team failed to score from corner... */
                return cornerfailed;
            }
        } else if (setpiece.equals(freekick)) {
            /* Will they score from this free-kick? */
            int FScored = RNG.nextInt(100) + 1;
            if (FScored <= 35) {
                /* Team scored from a free-kick! */
                return goal;
            } else {
                /* Team failed to score from free-kick... */
                return freekickfailed;
            }
        } else {
            /* Will they score from this penalty? */
            int PScored = RNG.nextInt(100) + 1;
            if (PScored <= 75) {
                /* Team scored a penalty! */
                return goal;
            } else {
                /* Team missed a penalty... */
                return penaltymiss;
            }
        }
    }

    public String createBuildup(String side) {
        /* Home team trying to build up an attack... */
        if (side.equals("H")) {
            int hLimit = (int) Math
                    .floor(((homeMidfield + homeOVR) / (homeMidfield + awayMidfield + homeOVR + awayOVR)) * 100);

            double hBound = this.clamp(hLimit * 0.95, 5, 90);
            int hPb = RNG.nextInt(100) + 1;

            /* Print test 

            System.out.println("\nhLimit hBound hPb");
            System.out.println(hLimit + " " + hBound + " " + hPb);*/

            if (hPb <= hBound) {
                return "HChanceCreated";
            } else {
                return "noPos";
            }
        } else {
            /* Away team trying to build up an attack... */
            int aLimit = (int) Math
                    .floor(((awayMidfield + awayOVR) / (homeMidfield + awayMidfield + homeOVR + awayOVR)) * 100);

            double aBound = this.clamp(aLimit * 0.70, 5, 90);
            int aPb = RNG.nextInt(100) + 1;

            /* Print test 
            System.out.println("\naLimit aBound aPb");
            System.out.println(aLimit + " " + aBound + " " + aPb); */

            if (aPb <= aBound) {
                return "AChanceCreated";
            } else {
                return "noPos";
            }
        }
    }

    public String gainPossession() {
        /* Home midfield better than away midfield */
        if (homeMidfield >= awayMidfield) {
            int hLimit = (int) Math.floor(((homeMidfield) / (homeMidfield + awayMidfield)) * 100);
            double hLBound = this.clamp(hLimit * 0.85, 5, 90);
            double hUBound = this.clamp(hLimit * 1.25, hLimit, 95);

            int hPG = RNG.nextInt(100) + 1;

            /* Print test
            System.out.println("\nhLimit hLBound hUBound hPG");
            System.out.println(hLimit + " " + hLBound + " " + hUBound + " " + hPG); */

            if (hPG <= hLBound) {
                hPosCount++;
                return "hPos";
            } else if (hPG >= hUBound) {
                aPosCount++;
                return "aPos";
            } else {
                return "noPos";
            }
        } else {
            /* Away midfield is better than home midfield */
            int aLimit = (int) Math.floor(((awayMidfield) / (homeMidfield + awayMidfield)) * 100);
            double aLBound = this.clamp(aLimit * 0.75, 5, 90);
            double aUBound = this.clamp(aLimit * 1.25, aLimit, 95);

            int aPG = RNG.nextInt(100) + 1;

            /* Print test 
            System.out.println("\naLimit aLBound aUBound aPG");
            System.out.println(aLimit + " " + aLBound + " " + aUBound + " " + aPG); */

            if (aPG <= aLBound) {
                aPosCount++;
                return "aPos";
            } else if (aPG >= aUBound) {
                hPosCount++;
                return "hPos";
            } else {
                return "noPos";
            }
        }
    }

    public void addScore(String side) {
        if (side.equals("H")) {
            /* Update home team score and table points */
            HScore++;

            homeTeam.setGF(homeTeam.getGF() + 1);
            homeTeam.setGD(homeTeam.getGD() + 1);

            awayTeam.setGA(awayTeam.getGA() + 1);
            awayTeam.setGD(awayTeam.getGF() - awayTeam.getGA());

        } else {
            /* Update away team score and table points */
            AScore++;

            awayTeam.setGF(awayTeam.getGF() + 1);
            awayTeam.setGD(awayTeam.getGD() + 1);

            homeTeam.setGA(homeTeam.getGA() + 1);
            homeTeam.setGD(homeTeam.getGF() - homeTeam.getGA());
        }
    }

    public void matchProgress() {
        if (matchState.equals("Home Chance")) {
            /* New chance for home team */
            hPosCount++;
            HAttempts++;

            if (!quickRun) {
                newEvent(clock, matchState, false, half);
            }

            String HOutcome = createChance("H");
            matchState = HOutcome;
            if (HOutcome.equals("HGoal")) {
                /* Home chance scored */
                if (!quickRun) {
                    newEvent(clock, matchState, true, half);
                }
                addScore("H");
            } else if (HOutcome.equals("Home Freekick") ||
                    HOutcome.equals("Home Corner") ||
                    HOutcome.equals("Home Penalty")) {
                /* Home chance leads to set-piece */
                hPosCount++;
                HAttempts++;
                clock++;

                if (!quickRun) {
                    newEvent(clock, matchState, false, half);
                }

                String spOutcome = createSetPiece("H", matchState);
                matchState = spOutcome;

                if (spOutcome.equals("HGoal")) {
                    /* Home scores from set-piece */
                    if (!quickRun) {
                        newEvent(clock, matchState, true, half);
                    }
                    addScore("H");
                } else {
                    /* Miss set-piece */
                    if (!quickRun) {
                        newEvent(clock, matchState, true, half);
                    }
                }
            } else {
                /* Home chance failed */
                matchState = "Home Chance Missed";
                if (!quickRun) {
                    newEvent(clock, matchState, true, half);
                }
            }
        } else if (matchState.equals("Away Chance")) {
            /* New chance for away team */
            aPosCount++;
            AAttempts++;

            if (!quickRun) {
                newEvent(clock, matchState, false, half);
            }

            String AOutcome = createChance("A");
            matchState = AOutcome;
            if (AOutcome.equals("AGoal")) {
                /* Away chance scored */
                if (!quickRun) {
                    newEvent(clock, matchState, true, half);
                }
                addScore("A");
            } else if (AOutcome.equals("Away Freekick") ||
                    AOutcome.equals("Away Corner") ||
                    AOutcome.equals("Away Penalty")) {
                /* Away chance leads to set-piece */
                aPosCount++;
                AAttempts++;
                clock++;

                if (!quickRun) {
                    newEvent(clock, matchState, false, half);
                }

                String spOutcome = createSetPiece("A", matchState);
                matchState = spOutcome;

                if (spOutcome.equals("AGoal")) {
                    /* Away scores from set-piece */
                    if (!quickRun) {
                        newEvent(clock, matchState, true, half);
                    }
                    addScore("A");
                } else {
                    /* Miss set-piece */
                    if (!quickRun) {
                        newEvent(clock, matchState, true, half);
                    }
                }
            } else {
                /* Away chance failed */
                matchState = "Away Chance Missed";
                if (!quickRun) {
                    newEvent(clock, matchState, true, half);
                }
            }
        }

        if (beginAction()) {
            if (gainPossession().equals("hPos")) {
                /* Home team gained posession */
                clock++;
                matchState = "Home Possession";
                if (!quickRun) {
                    newEvent(clock, matchState, true, half);
                }

                /* Home team able to build up an attack with possession */
                if (createBuildup("H").equals("HChanceCreated")) {
                    matchState = "Home Chance";
                    HAttempts++;
                }
            } else if (gainPossession().equals("aPos")) {
                /* Away team gained possession */
                clock++;
                matchState = "Away Possession";
                if (!quickRun) {
                    newEvent(clock, matchState, true, half);
                }

                /* Away team able to build up an attack with possession */
                if (createBuildup("A").equals("AChanceCreated")) {
                    matchState = "Away Chance";
                    AAttempts++;
                }
            } else {
                /* No action occuring at this minute */
                matchState = " ";
            }
        } else {
            /* No action occuring at this minute */
            matchState = " ";
        }
    }

    public void startHalf(int half) {
        if (half == 1) {
            /* First half */

            /* Only print detailed match events if the match is not quick ran */
            if (!quickRun) {
                print("\n===== " + homeTeam.getName() + " vs. " + awayTeam.getName());
                System.out.println("\n----- Starting first-half...\n");
                print("\nEvent Highlights: \n");
                newEvent(clock, matchState, true, half);
            }

            int fhST = RNG.nextInt(3);

            /* Begin first half */
            while (clock < (45 + fhST)) {
                clock++;
                matchProgress();
            }
            ;

            /* End of first half */
            clock = 45;
            half = 2;
            matchState = "HT";
            calculatePossession();

            if (!quickRun) {
                newEvent(clock, matchState, true, half);
                printMatchStatistics();
            }
        } else if (half == 2) {
            /* Second half */
            if (!quickRun) {
                System.out.println("\n----- Starting second-half...\n");
            }

            int shST = RNG.nextInt(6);

            matchState = " ";
            while (clock < (90 + shST)) {
                clock++;
                matchProgress();
            }

            /* End of second half */
            clock = 90;
            half = 3;
            matchState = "FT";
            calculatePossession();

            if (!quickRun) {
                newEvent(clock, matchState, true, half);
                printMatchStatistics();
            }
        } else if (half == 3) {
            /* Extra time first half: WIP */
        } else if (half == 4) {
            /* Extra time second half: WIP */
        }
    }

    public void printMatchStatistics() {
        print("-- Possession: " + homeTeam.getAbbreviation() + " " + (int) homePossession + "% - "
                + (int) awayPossession + "% " + awayTeam.getAbbreviation());
        print("-- " + homeTeam.getAbbreviation() + " attempts: " + HAttempts);
        print("-- " + awayTeam.getAbbreviation() + " attempts: " + AAttempts);
    }

    public Match startGame(boolean isQuickRun, Match match) {
        /* Quick run the match or show the match events */
        quickRun = isQuickRun;
        this.clearEvents();
        isPlaying = true;
        this.resetScore(match);

        /*-- Team Variables --*/

        homeTeam = match.getHomeTeam();
        awayTeam = match.getAwayTeam();

        /*-- Retrieve initial ratings for teams --*/
        homeAttack = homeTeam.getAttackRating();
        homeMidfield = homeTeam.getMidfieldRating();
        homeDefence = homeTeam.getDefenceRating();
        homeOVR = homeTeam.getOVR();

        awayAttack = awayTeam.getAttackRating();
        awayMidfield = awayTeam.getMidfieldRating();
        awayDefence = awayTeam.getDefenceRating();
        awayOVR = awayTeam.getOVR();

        /*-- Re-adjustments of ratings for the match based on home-advantage and A vs. D comparisons --*/

        /*-- Advantage factors (considering home-advantage)
        The calculation of probabilities which help boost the home team's advantage 
        in gaining posession --*/
        if (homeOVR > awayOVR) {
            homeOVR = (int) Math.floor(homeOVR + (15 * (awayOVR / homeOVR)));
            /* Clamp home overall boost advantage */
            homeOVR = this.clamp(homeOVR, 0, 95);

            awayOVR = (int) Math.floor(awayOVR - (15 * (awayOVR / homeOVR)));
            awayOVR = this.clamp(awayOVR, 0, 95);
        } else if (awayOVR > homeOVR) {
            awayOVR = (int) Math.floor(homeOVR + (10 * (homeOVR / awayOVR)));
            /* Clamp away overall boost advantage */
            awayOVR = this.clamp(homeOVR, 0, 95);

            homeOVR = (int) Math.floor(homeOVR - (10 * (homeOVR / awayOVR)));
            homeOVR = this.clamp(awayOVR, 0, 95);
        }

        /*-- Attack vs. opponent defence comparisons 
        The calculation of probabilities which will determine whether a team's chance 
        will be convrted
        --*/

        if (homeAttack > awayDefence) {
            /* Home attack advantage */
            homeAttack = (int) Math.floor(homeAttack + (15 * (awayDefence / homeAttack)));
            homeAttack = this.clamp(homeAttack, 0, 95);

            awayDefence = (int) Math.floor(awayDefence - (15 * (awayDefence / homeAttack)));
            awayDefence = this.clamp(awayDefence, 0, 95);
        } else if (awayDefence > homeAttack) {
            /* Away defence advantage */
            awayDefence = (int) Math.floor(awayDefence + (20 * (homeAttack / awayDefence)));
            awayDefence = this.clamp(awayDefence, 0, 95);

            homeAttack = (int) Math.floor(homeAttack - (10 * (homeAttack / awayDefence)));
            homeAttack = this.clamp(homeAttack, 0, 95);
        }

        if (awayAttack > homeDefence) {
            /* Away attack advantage */
            awayAttack = (int) Math.floor(awayAttack + (10 * (homeDefence / awayAttack)));
            awayAttack = this.clamp(awayAttack, 0, 95);

            homeDefence = (int) Math.floor(homeDefence - (10 * (homeDefence / awayAttack)));
            homeDefence = this.clamp(homeDefence, 0, 95);
        } else if (homeDefence > awayAttack) {
            /* Home defence advantage */
            homeDefence = (int) Math.floor(homeDefence + (25 * (awayAttack / homeDefence)));
            homeDefence = this.clamp(homeDefence, 0, 95);

            awayAttack = (int) Math.floor(awayAttack - (15 * (awayAttack / homeDefence)));
            awayAttack = this.clamp(awayAttack, 0, 95);
        }

        clock = 0;
        matchState = "Kick Off";

        /* Begin first half */
        startHalf(1);

        /* Ask user to continue half if game is not quick-ran */
        if (!quickRun) {
            System.out.println("\n(a) Begin second-half \n(b) Exit Match ");
            String continueGame = input.nextLine();
            switch (continueGame) {
                case "a":
                    /* Begin second half */
                    startHalf(2);
                    /* Match complete; return the match results */
                    match.updateScores(quickRun, HScore, AScore, HAttempts, AAttempts);
                    break;
                case "b":
                    /* Quit the match */
                    System.out.println("\n Exiting match...");
                    break;
                default:
                    System.out.println("Invalid input. Select one of the letter options");
            }
        } else {
            /* Match is quick ran: continue to second-half */
            startHalf(2);
            /* Match complete; return the match results */
            match.updateScores(quickRun, HScore, AScore, HAttempts, AAttempts);
        }

        return match;
        /* Code for extra time will come later... */
    }
}
