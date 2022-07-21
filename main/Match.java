import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Processes of a match (For 90 minutes plus stoppage time):
 * 
 * 1. Will some action happen in this minute?
 * -- (Yes, go step 2, No, next minute go step 1)
 * 
 * 2. Home or Away team will gain possession (battle of the midfields)
 * 
 * 3. Will the team, that gains possession, be able to develop it into a chance?
 * -- (OUTCOME: No, go 3a; Yes go 3b)
 * = 3a. Possession lost, next minute, back to step 1,
 * = 3b. Posession develops into a chance
 * 
 * 4. Chance is created from team, will it lead to goal, posession lost or a set-piece?
 * -- (Battle of the attack vs. opponent's defence)
 * -- (OUTCOME: GOAL, go 4a; Possession LOST, go 4b; Set-piece OCCURS, go 4c)
 * = 4a. Chance converted from team, go to step 5,
 * = 4b. Posession is lost from team, back to step 1,
 * = 4c. Chance leads to a set-piece for team (team receives a corner, free-kick or penalty)
 * Which set-piece will the team receive?
 * -- (OUTCOME: corner, go 4ci; free-kick go 4cii; penalty go 4ciii)
 * == 4ci. Team has a corner, will they score from this corner?
 * -- (OUTCOME: Yes, go step 5; No, next minute back to step 1)
 * == 4cii. Team has a free-kick, will they score from this free-kick?
 * -- (OUTCOME: Yes, go step 5; No, next minute back to step 1)
 * == 4ciii. Team has a penalty, will they score from this penalty?
 * -- (OUTCOME: Yes, go step 5; No, next minute back to step 1)
 * 
 * 5. Team has SCORED, update scoreboard, go next minute and back to step 1!
 */

public class Match {
    /* Match states */
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



    public void startGame(boolean isQuickRun, Team homeT, Team awayT) {
        /*  */
        quickRun = isQuickRun;
        this.clearEvents();
        isPlaying = true;
        this.resetScore();

        String matchState = "";

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
    }
}
