package main;

public class Main {
    public static void main(String[] args) {
        Team AT = new Team(1, "Red FC", "RED", 1, 84, 81, 78);
        Team BT = new Team(2, "Blue FC", "BLU", 1, 85, 80, 79);

        Match M1 = new Match(true, AT, BT);
        Match M2 = new Match(true, BT, AT);
        Match M3 = new Match(true, AT, BT);
        Match M4 = new Match(true, BT, AT);
        Match M5 = new Match(true, AT, BT);
        Match M6 = new Match(true, BT, AT);
        Match M7 = new Match(true, AT, BT);
        Match M8 = new Match(true, BT, AT);

    }
}
