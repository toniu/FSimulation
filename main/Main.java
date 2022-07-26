package main;

public class Main {
    public static void main(String[] args) {
        Team AT = new Team(1, "Red FC", "RED", 1, 84, 81, 78);
        Team BT = new Team(2, "Blue FC", "BLU", 1, 85, 80, 79);

        MatchEngine ME = MatchEngine.getInstance();

        Match M1 = new Match(AT, BT);
        Match M2 = new Match(AT, BT);
        System.out.println(" \n 1ST LEG \n ");
        ME.startGame(false, M1);
        System.out.println(" \n 2ND LEG \n ");
        ME.startGame(false, M2);


    }
}
