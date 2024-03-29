package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Loader {
    private static Loader singleton = null;
    private List<League> leagues;
    private List<Team> teams;

    public static synchronized Loader getInstance() {
        if (singleton  == null) {
            singleton = new Loader();
        }
        return singleton;
    }

    public void loadData() {

      if (this.leagues == null) {
        this.leagues = new ArrayList<League>();
      }

      if (this.teams == null) {
        this.teams = new ArrayList<Team>();
      }

      this.leagues.clear();
      this.teams.clear();

      /* Load all leagues and teams */
       loadLeagues();
       loadTeams();

      /* Populating teams into leagues */
      for (League league : this.leagues) {
        int currentLeagueID = league.getID();

        /* For each league, add the teams which have the same league ID */
        for (Team team : this.teams) {
          int currentTeam = team.getLeagueID();
          if (currentTeam == currentLeagueID) {
            league.getTeams().add(team);
          }
        }
      }
    }

    public List<League> getLeagues() {
      return this.leagues;
    }

    public List<Team> getTeams() {
      return this.teams;
    }

    public void loadLeagues() {
        try(Scanner LS = new Scanner(new File("resources/leagues.csv"))) {
            /* Skip the header row */
            LS.nextLine();
            /* Read each league */
            while (LS.hasNextLine()) {
              String line = LS.nextLine();
          
              /* Scan for league's attributes */
              String[] LA = line.split(",");

              int lID = Integer.parseInt(LA[0]);
              String name = LA[1];
              String country = LA[2];
              int division = Integer.parseInt(LA[3]);

              League nL = new League(lID, name, country, division);
              this.leagues.add(nL);
            }
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }

    }

    public void loadTeams() {
        try(Scanner TS = new Scanner(new File("resources/teams.csv"))){
            /* Skip the header row */
            TS.nextLine();
            /* Read each team */
            while (TS.hasNextLine()) {
              String line = TS.nextLine(); 

               /* Scan for team's attributes */
              String[] TA = line.split(",");

              int tID = Integer.parseInt(TA[0]);
              String name = TA[1];
              String abbreviation = TA[2];
              int leagueid = Integer.parseInt(TA[3]);
              String location = TA[4];
              int att = Integer.parseInt(TA[6]);
              int mid = Integer.parseInt(TA[7]);
              int def = Integer.parseInt(TA[8]);

              Team nT = new Team(tID, name, abbreviation, leagueid, location, att, mid, def);
              this.teams.add(nT);
            }
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
    }
}
