package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Match {
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

    int homeAttack = 0;
    int homeMidfield = 0;
    int homeDefence = 0;
    int homeOVR = 0;

    int awayAttack = 0;
    int awayMidfield = 0;
    int awayDefence = 0;
    int awayOVR = 0;

    int HAttempts = 0;
    int AAttempts = 0;

    /* Is the match currently playing? */
    public boolean isMatchPlaying() {
        return this.isPlaying;
    }

    /* Clear all events */
    public void clearEvents() {
        events.clear();
    }

    /* Reset score */
    public void resetScore() {
        this.HScore = 0;
        this.AScore = 0;
    }

    /* New Event */
    public void newEvent(int minsPassed, String gameState, boolean showClock, int half) {
        String clockMsg = minsPassed + "'";
        String event = null;

        /* Present stoppage time on clock */
        if (minsPassed > 45 && half == 1) {
            clockMsg = minsPassed + "+" + (minsPassed - 45);
        } else if (minsPassed > 90 && half == 2) {
            clockMsg = minsPassed + "+" + (minsPassed - 90);
        } else if (minsPassed > 105 && half == 3) {
            clockMsg = minsPassed + "+" + (minsPassed - 105);
        } else if (minsPassed > 120 && half == 4) {
            clockMsg = minsPassed + "+" + (minsPassed - 120);
        }

        /* Create a new event */
        /*-- Home team scored --*/
        if (gameState.equals("HGoal")) {
            /*  */
            /*-- Away team scored --*/
        } else if (gameState.equals("AGoal")) {
            /*  */
            /*-- Start / End of half */
        } else if (gameState.equals("HT") ||
                gameState.equals("FT") ||
                gameState.equals("Kickoff")) {
            /*  */
            /*-- Resumed match --*/
        } else {
            /* */
        }
    }

    /* Clamps the value to be within specified number range */
    public int clampV(int value, int min, int max) {
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

            int hP = RNG.nextInt(100) + 1;
			if (hP <= hLimit * 0.85) {
				/* Home goal */
				return "HGoal";
            } else if (hP >= hLimit * 1.15) {
				/* Posession is lost from home team */
				return "noPos";
            } else {
				/* Chance could lead to a home set-piece... */
				int heC = RNG.nextInt(100) + 1;
				if (heC <= 40) {
                    /* Home team has free-kick... */
					return "HFreekick";
                } else if (heC > 40 && heC <= 80) {
                    /* Home team has a corner... */
					return "HCorner";
                } else
                    /* Home team has a penalty... */
					return "HPenalty";
            }
        } else {
            /* Away team has a chance... */
            int aLimit = (int) Math.floor((awayAttack / (homeDefence + awayAttack)) * 100);

            int aP = RNG.nextInt(100) + 1;
			if (aP <= aLimit * 0.85) {
				/* Away goal */
				return "AGoal";
            } else if (aP >= aLimit * 1.15) {
				/* Posession is lost from away team */
				return "noPos";
            } else {
				/* Chance could lead to an away set-piece... */
				int aeC = RNG.nextInt(100) + 1;
				if (aeC <= 40) {
                    /* Away team has free-kick... */
					return "AFreekick";
                } else if (aeC > 40 && aeC <= 80) {
                    /* Away team has a corner... */
					return "ACorner";
                } else
                    /* Away team has a penalty... */
					return "APenalty";
            }
        }
    }

    /**
     * Determines if the set-piece will be converted
     * 15% chance of scoring from corner,
     * 35% chance of scoring from free-kick,
     * 75% chance of penalty being scored,
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
            corner = "HCorner";
            cornerfailed = "HCornerFailed";
            freekick = "HFreekick";
            freekickfailed = "HFreekickMiss";
            penaltymiss = "HPenaltyMiss";
        } else {
            /* Away-team is doing a set-piece */
            goal = "AGoal";
            corner = "ACorner";
            cornerfailed = "ACornerFailed";
            freekick = "AFreekick";
            freekickfailed = "AFreekickMiss";
            penaltymiss = "APenaltyMiss";
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
            int hLimit = (int) Math.floor(((homeMidfield + homeOVR) / (homeMidfield + awayMidfield + homeOVR + awayOVR)) * 100);

            int hPb = RNG.nextInt(100) + 1;
            if (hPb <= hLimit * 0.95) {
                return "HChanceCreated";
            } else {
                return "noPos";
            }
        } else {
            /* Away team trying to build up an attack... */
            int aLimit = (int) Math.floor(((awayMidfield + awayOVR) / (homeMidfield + awayMidfield + homeOVR + awayOVR)) * 100);

            int aPb = RNG.nextInt(100) + 1;
            if (aPb <= aLimit * 0.95) {
                return "AChanceCreated";
            } else {
                return "noPos";
            }
        }
    }

    public String gainPossession() {
        /* Home midfield better than aray midfield */
        if (homeMidfield >= awayMidfield) {
            int hLimit = (int) Math.floor(((homeMidfield) / (homeMidfield + awayMidfield)) * 100);

            int hPG = RNG.nextInt(100) + 1;
            if (hPG <= hLimit * 0.85) {
                return "hPos";
            } else if (hPG >= hLimit * 1.15) {
                return "aPos";
            } else {
                return "noPos";
            }
        } else {
            int aLimit = (int) Math.floor(((awayMidfield) / (homeMidfield + awayMidfield)) * 100);

            int aPG = RNG.nextInt(100) + 1;
            if (aPG <= aLimit * 0.85) {
                return "aPos";
            } else if (aPG >= aLimit * 1.15) {
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
            HAttempts++;

            String HOutcome = createChance("H");
            matchState = HOutcome;
            if (HOutcome.equals("HGoal")) {
                /* Home chance scored */
                addScore("H");
            } else if (HOutcome.equals("HFreekick") ||
            HOutcome.equals("HCorner") ||
            HOutcome.equals("HPenalty")) {
                /* Home chance leads to set-piece */
                clock++;
                String spOutcome = createSetPiece("H", matchState);
				matchState = spOutcome;
                
                if (spOutcome.equals("HGoal")) {
                    /* Home scores from set-piece */
                    addScore("H");
                } else {
                    /* Miss set-piece */
                }
            } else {
                /* Home chance failed */
                matchState = "HChance Missed";
            }
        } else if (matchState.equals("Away Chance")) {
            /* New chance for away team */
            AAttempts++;

            String AOutcome = createChance("A");
            matchState = AOutcome;
            if (AOutcome.equals("AGoal")) {
                /* Away chance scored */
                addScore("A");
            } else if (AOutcome.equals("AFreekick") ||
            AOutcome.equals("ACorner") ||
            AOutcome.equals("APenalty")) {
                /* Away chance leads to set-piece */
                clock++;
                String spOutcome = createSetPiece("A", matchState);
				matchState = spOutcome;
                
                if (spOutcome.equals("HGoal")) {
                    /* Away scores from set-piece */
                    addScore("A");
                } else {
                    /* Miss set-piece */
                }
            } else {
                /* Away chance failed */
                matchState = "AChance Missed";
            }
        }


        if (beginAction()) {
            if (gainPossession().equals("hPos")) {
                /* Home team gained posession */
                clock++;
                matchState = "Home Posession";

                /* Home team able to build up an attack with possession */
                if (createBuildup("H").equals("HChanceCreated")) {
                    matchState = "Home Chance";
                    HAttempts++;
                }
            } else if (gainPossession().equals("aPos")) {
                /* Away team gained posession */
                clock++;
                matchState = "Away Posession";

                /* Away team able to build up an attack with possession */
                if (createBuildup("A").equals("AChanceCreated")) {
                    matchState = "Away Chance";
                    AAttempts++;
                }
            } else {
                /* No action occuring at this minute */
                matchState = "No action";
            }
        } else {
            /* No action occuring at this minute */
            matchState = "No action";
        } 
    }



    public void startGame(boolean isQuickRun, Team homeT, Team awayT) {
        /*  */
        quickRun = isQuickRun;
        this.clearEvents();
        isPlaying = true;
        this.resetScore();

        /*-- Team Variables --*/
        homeTeam = homeT;
        awayTeam = awayT;
        
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
            this.clampV(homeOVR, 0, 95);

            awayOVR = (int) Math.floor(awayOVR - (15 * (awayOVR / homeOVR)));
            this.clampV(awayOVR, 0, 95);
        } else if (awayOVR > homeOVR) {
            awayOVR = (int) Math.floor(homeOVR + (10 * (homeOVR / awayOVR)));
            /* Clamp away overall boost advantage */
            this.clampV(homeOVR, 0, 95);

            homeOVR = (int) Math.floor(homeOVR - (10 * (homeOVR / awayOVR)));
            this.clampV(awayOVR, 0, 95);
        }

        /*-- Attack vs. opponent defence comparisons 
        The calculation of probabilities which will determine whether a team's chance 
        will be convrted
        --*/

        if (homeAttack > awayDefence) {
            /* Home attack advantage */
            homeAttack = (int) Math.floor(homeAttack + (15 * (awayDefence / homeAttack)));
            this.clampV(homeAttack, 0, 95);

            awayDefence = (int) Math.floor(awayDefence - (15 * (awayDefence / homeAttack)));
            this.clampV(awayDefence, 0, 95);
        } else if (awayDefence > homeAttack) {
            /* Away defence advantage */
            awayDefence = (int) Math.floor(awayDefence + (20 * (homeAttack / awayDefence)));
            this.clampV(awayDefence, 0, 95);

            homeAttack = (int) Math.floor(homeAttack - (10 * (homeAttack / awayDefence)));
            this.clampV(homeAttack, 0, 95);
        }

        if (awayAttack > homeDefence) {
            /* Away attack advantage */
            awayAttack = (int) Math.floor(awayAttack + (10 * (homeDefence / awayAttack)));
            this.clampV(awayAttack, 0, 95);

            homeDefence = (int) Math.floor(homeDefence - (10 * (homeDefence / awayAttack)));
            this.clampV(homeDefence, 0, 95);
        } else if (homeDefence > awayAttack) {
            /* Home defence advantage */
            homeDefence = (int) Math.floor(homeDefence + (25 * (awayAttack / homeDefence)));
            this.clampV(homeDefence, 0, 95);

            awayAttack = (int) Math.floor(awayAttack - (15 * (awayAttack / homeDefence)));
            this.clampV(awayAttack, 0, 95);
        }

        clock = 0;

        matchState = "Kick Off";
        int fhST = RNG.nextInt(3);
        
        /* Begin first half */
        while (clock < (45 + fhST)) {
            clock++;
            matchProgress();
        };

        /* Begin second half */
        clock = 45;
        matchState = "Half time";
        int shST = RNG.nextInt(6);
        
        while (clock < (90 + shST)) {
            matchProgress();
        }

        /* End of second half */
        clock = 90;
        half = 3;
        matchState = "FT";
        /* Code for extra time will come later... */
    }
}
