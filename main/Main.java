package main;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /* Team AT = new Team(1, "Red FC", "RED", 1, "", 84, 81, 78);
        Team BT = new Team(2, "Blue FC", "BLU", 1, "", 85, 80, 79);
        Team CT = new Team(3, "Green FC", "GRE", 1, "", 81, 76, 79);
        Team DT = new Team(4, "Yellow FC", "YEL", 1, "", 80, 81, 77);
        Team ET = new Team(5, "Orange FC", "ORA", 1, "", 78, 78, 79);
        Team FT = new Team(6, "Purple FC", "PUR", 1, "", 81, 76, 75);

        List<Team> teams = new ArrayList<>();
        teams.add(AT);
        teams.add(BT);
        teams.add(CT);
        teams.add(DT);
        teams.add(ET);
        teams.add(FT);

        League L1 = new League(1, "Rainbow League", "England", 1, teams);
        L1.generateFixtures();
        */

        Loader ML = new Loader();
        ML.loadData();

        List<League> leagues = ML.getLeagues();
        League L1 = leagues.get(0);

        L1.generateFixtures();
    }
}
