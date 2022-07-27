package main;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /* Load data of leagues and teams */
        Loader ML = new Loader();
        ML.loadData();

        /* Select first league and test it */
        List<League> leagues = ML.getLeagues();
        League L1 = leagues.get(0);
        League L2 = leagues.get(1);
        League L3 = leagues.get(2);
        League L4 = leagues.get(3);
        League L5 = leagues.get(4);

        L5.generateFixtures();
    }
}
