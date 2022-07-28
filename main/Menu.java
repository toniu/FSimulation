package main;

import java.util.Scanner;
import java.util.List;

public class Menu {
    private static Menu singleton = null;
    private boolean isRunning;
    private Scanner scanner = new Scanner(System.in);
    private Scanner intScanner = new Scanner(System.in);
    MatchEngine ME = MatchEngine.getInstance();
    Loader ML = Loader.getInstance();

    public static synchronized Menu getInstance() {
        if (singleton == null) {
            singleton = new Menu();
        }
        return singleton;
    }

    public void startMenu() {
        this.isRunning = true;
        ML.loadData();
        while (this.isRunning) {
            mainMenu();
        }
    }

    public int selectLeague() {
        List<League> leagues = ML.getLeagues();

        boolean leagueSelected = false;
        int leagueIndex = 0;
        /* League select validation */
        while (!leagueSelected) {
            System.out.println("\n==== Select League (Press 0 to exit): \n");

            int maxLeagueNum = 0;
            for (int i = 0; i < leagues.size(); i++) {
                if (leagues.get(i).getTeams().size() != 0) {
                    maxLeagueNum++;
                    System.out.println("(" + (i + 1) + ") " + leagues.get(i).getName());
                }
            }

            leagueIndex = intScanner.nextInt();
            if (leagueIndex > 0 && leagueIndex <= maxLeagueNum) {
                /* Valid input */
                leagueSelected = true;
            } else if (leagueIndex == 0) {
                /* Exit team selection and escape loop */
                leagueSelected = true;
            } else {
                System.out.println("\nInvalid input: select one of the number options");
            }
        }
        return leagueIndex;
    }

    public int selectTeam(int leagueID) {
        League selectedLeague = ML.getLeagues().get(leagueID);
        List<Team> teams = selectedLeague.getTeams();

        boolean teamSelected = false;
        int teamIndex = 0;
        /* Team select validation */

        while (!teamSelected) {
            System.out.println(
                    "\n==== Select Team from " + selectedLeague.getName().toUpperCase() + "\n (Press 0 to exit): \n");
            for (int i = 0; i < teams.size(); i++) {
                System.out.println("(" + (i + 1) + ") " + teams.get(i).getName());
            }

            teamIndex = intScanner.nextInt();
            if (teamIndex > 0 && teamIndex <= teams.size()) {
                /* Valid input */
                teamSelected = true;
            } else if (teamIndex == 0) {
                /* Exit team selection and escape loop */
                teamSelected = true;
            } else {
                System.out.println("\nInvalid input: select one of the number options");
            }
        }
        return teamIndex;
    }

    public void simulateFriendly() {
        /* Select Teams */
        League hL = null;
        League aL = null;
        Team hT = null;
        Team aT = null;
        String country = "";

        /* Home League select validation */
        System.out.println("\n===== Select Home Team ");
        System.out.println("----- Selecting League: ");
        int homeLeagueIndex = selectLeague();
        if (homeLeagueIndex != 0) {
            /* User is selecting from 1...n but arrays begin with index 0 */
            homeLeagueIndex--;
            /* Find league of home team using index */
            hL = ML.getLeagues().get(homeLeagueIndex);
            /* Find location and country */
            country = hL.getCountry();

            System.out.println("\n----- Selecting Team: ");
            int homeTeamIndex = selectTeam(homeLeagueIndex);
            if (homeTeamIndex != 0) {
                /* User is selecting from 1...n but arrays begin with index 0 */
                homeTeamIndex--;

                /* Find team using index selected */
                hT = hL.getTeams().get(homeTeamIndex);
            } else {
                /* Return to main menu */
                return;
            }
        } else {
            /* Return to main menu */
            return;
        }

        /* Away League select validation */
        System.out.println("\n===== Select Away Team ");
        System.out.println("----- Selecting League: ");
        int awayLeagueIndex = selectLeague();
        if (awayLeagueIndex != 0) {
            /* User is selecting from 1...n but arrays begin with index 0 */
            awayLeagueIndex--;
            /* Find league of home team using index */
            aL = ML.getLeagues().get(awayLeagueIndex);

            System.out.println("\n----- Selecting Team: ");
            int awayTeamindex = selectTeam(awayLeagueIndex);
            if (awayTeamindex != 0) {
                /* User is selecting from 1...n but arrays begin with index 0 */
                awayTeamindex--;

                /* Find team using index selected */
                aT = aL.getTeams().get(awayTeamindex);
            } else {
                /* Return to main menu */
                return;
            }
        } else {
            /* Return to main menu */
            return;
        }

        /* If both home and away team have been selected, simulate friendly */
        if (hT != null && aT != null) {
            String begin = "";
            boolean askToStart = false;

            while (!askToStart) {
                System.out.println("\n===== Friendly Match: " + hT.getName() + " vs. " + aT.getName() + "\nLocation: "
                        + hT.getLocation() + ", " + country);
                System.out.println("\n(a) Kick-off \n(b) Back to Main Menu");
                begin = scanner.nextLine();
                switch (begin) {
                    case "a":
                        askToStart = true;
                        boolean friendlyMode = true;
                        Match friendly = null;
                        /* User is now in the friendlies game-mode */
                        while (friendlyMode) {
                            /* Start simulation of friendly */
                            if (friendly == null) {
                                friendly = new Match(hT, aT);
                            }

                            ME.startGame(false, friendly);

                            /* The match has finished */
                            if (friendly.hasCompleted()) {
                                boolean askUser = false;

                                while (!askUser) {
                                    System.out.println(
                                            "\n(a) Rematch \n(b) Select different teams \n(c) Return to main menu");
                                    /*
                                     * User can choose to rematch, select different teams OR return back to main
                                     * menu
                                     */
                                    String continueMatch = scanner.nextLine();
                                    switch (continueMatch) {
                                        case "a":
                                            askUser = true;
                                            System.out.println("\n===== Friendly re-match...");
                                            break;
                                        case "b":
                                            askUser = true;
                                            friendlyMode = false;
                                            System.out.println("\n===== Selecting different teams...");
                                            simulateFriendly();
                                            break;
                                        case "c":
                                            askUser = true;
                                            friendlyMode = false;
                                            System.out.println("\n===== Exiting match...");
                                            break;
                                        default:
                                            System.out.println("\nInvalid input. Select one of the letter options");
                                    }
                                }
                            } else {
                                friendlyMode = false;
                            }
                        }
                        break;
                    case "b":
                        askToStart = true;
                        System.out.println("\n===== Exiting match...");
                        break;
                    default:
                        System.out.println("\nInvalid input. Select one of the letter options");
                }
            }
        }
    }

    public void simulateLeague() {
        List<League> leagues = ML.getLeagues();
        League selectedLeague = null;
        /* League select validation */
        System.out.println("\n===== Select League: ");
        int leagueIndex = selectLeague();
        if (leagueIndex != 0) {
            /* User is selecting from 1...n but arrays begin with index 0 */
            leagueIndex--;
            selectedLeague = leagues.get(leagueIndex);

        } else {
            /* Return to main menu */
            return;
        }

        /* If league has been selected, simulate season */
        if (selectedLeague != null) {
            String begin = "";
            boolean askToStart = false;
            boolean leagueMode = false;
            System.out.println("\n===== League simulation of: " + selectedLeague.getName() + "\nCountry: "
                    + selectedLeague.getCountry() + "\nDivision: " + selectedLeague.getDivision());

            while (!askToStart) {
                System.out.println("\n(a) Kick-off the season \n(b) Back to Main Menu");
                begin = scanner.nextLine();
                switch (begin) {
                    case "a":
                        askToStart = true;
                        leagueMode = true;

                        while (leagueMode) {
                            boolean askUser = false;
                            /* Start simulation of league */
                            selectedLeague.generateFixtures();

                            /* Only ask user for restarting the league once the season has completed */
                            if (selectedLeague.isSeasonCompleted()) {
                                while (!askUser) {
                                    System.out.println(
                                            "\n(a) Restart season \n(b) Select different league \n(c) Back to Main Menu");
                                    String answer = scanner.nextLine().toLowerCase();
                                    switch (answer) {
                                        case "a":
                                            askUser = true;
                                            System.out.println("\n===== Restarting league season...");
                                            break;
                                        case "b":
                                            askUser = true;
                                            leagueMode = false;
                                            System.out.println("\n===== Selecting different league...");
                                            simulateLeague();
                                            break;
                                        case "c":
                                            askUser = true;
                                            leagueMode = false;
                                            System.out.println("\n===== Exiting simulation of league...");
                                            break;
                                        default:
                                            System.out.println("Invalid option, type in the letter of one of the options...");
                                    }
                                }
                            } else {
                                /* User wanted to quit simulation of league while season was incomplete */
                                askToStart = true;
                                leagueMode = false;
                            }
                        }

                        break;
                    case "b":
                        askToStart = true;
                        System.out.println("\n===== Exiting simulation of league...");
                        break;
                    default:
                        System.out.println("Invalid input. Select one of the letter options");
                }
            }
        }
    }

    public void mainMenu() {
        String menuOption = "";

        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("FSimulation MAIN MENU ");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("\n(a) Simulate friendly ");
        System.out.println("(b) Simulate league ");
        System.out.println("(c) Exit Simulation ");
        System.out.println("\n----------------------------------------------------------------------");

        menuOption = scanner.nextLine();
        switch (menuOption) {
            case "a":
                simulateFriendly();
                break;
            case "b":
                simulateLeague();
                break;
            case "c":
                System.out.println("\n===== Exiting simulation...");
                this.isRunning = false;
                break;
            default:
                System.out.println("\nInvalid menu option. Select one of the letter options listed!");
        }
    }

    public Scanner getScanner() {
        return this.scanner;
    }

}
