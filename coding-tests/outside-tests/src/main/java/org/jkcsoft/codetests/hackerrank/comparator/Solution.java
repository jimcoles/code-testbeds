package org.jkcsoft.codetests.hackerrank.comparator;

/**
 * @author Jim Coles
 */
import java.util.*;

class Player {
    String name;
    int score;

    Player(String name, int score) {
        this.name = name;
        this.score = score;
    }
}

class Checker implements Comparator<Player> {
    private Comparator<? super Player> compName =
        Comparator.comparing(o -> o.name);
    // complete this method
    private Comparator<Player> compScore =
        Comparator.comparingInt(value -> value.score)
        ;
    {
        compScore = compScore.reversed();
    }
    private Comparator<Player> theComp = compScore.reversed().thenComparing(compName);

    public int compare(Player a, Player b) {
        return theComp.compare(a, b);
    }
}


public class Solution {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();

        Player[] player = new Player[n];
        Checker checker = new Checker();

        for(int i = 0; i < n; i++){
            player[i] = new Player(scan.next(), scan.nextInt());
        }
        scan.close();

        Arrays.sort(player, checker);
        for(int i = 0; i < player.length; i++){
            System.out.printf("%s %s\n", player[i].name, player[i].score);
        }
    }
}