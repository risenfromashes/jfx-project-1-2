package edu.buet.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Position implements Serializable{
    public static final int FORWARD = 1 << 0; public static final int MIDFIELDER = 1 << 1;
    public static final int DEFENDER = 1 << 2;
    public static final int GOALKEEPER = 1 << 3;

    private final int primaryPositions;
    private final String  positionShort;
    private final List<String> positionFull;
    public Position(String posStr) {
        this.positionShort = posStr.strip();
        this.positionFull = new LinkedList<>();
        var parts = positionShort.split(" ");
        var posFlags = 0;
        for (var part : parts) {
            part = part.strip();
            if (part.length() == 0) continue;
            if (part.equals("ST")) {
                positionFull.add("Striker");
                posFlags |= FORWARD;
            } else if (part.equals("SW")) {
                positionFull.add("Sweeper");
                posFlags |= DEFENDER;
            } else if (part.equals("GK")) {
                positionFull.add("Goalkeeper");
                posFlags |= GOALKEEPER;
            } else {
                var sb = new StringBuffer();
                if (part.charAt(0) == 'L')
                    sb.append("Left ");
                else if (part.charAt(0) == 'R')
                    sb.append("Right ");
                switch (part.charAt(part.length() - 1)) {
                case 'M':
                    posFlags |= MIDFIELDER;
                    break;
                case 'B':
                    posFlags |= DEFENDER;
                    break;
                default:
                    posFlags |= FORWARD;
                    break;
                }
                if (part.lastIndexOf("CM") > -1) {
                    sb.append("Central Midfielder");
                } else if (part.lastIndexOf("WB") > -1) {
                    sb.append("Wing-back");
                } else if (part.lastIndexOf("DM") > -1) {
                    sb.append("Defensive Midfielder");
                } else if (part.lastIndexOf("CB") > -1) {
                    sb.append("Centre-back");
                } else if (part.lastIndexOf("AM") > -1) {
                    sb.append("Attacking Midfielder");
                } else {
                    assert(part.length() == 2);
                    if (part.charAt(1) == 'F') {
                        sb.append("Forward");
                    } else if (part.charAt(1) == 'M') {
                        sb.append("Midfielder");
                    } else if (part.charAt(1) == 'B') {
                        sb.append("Back");
                    } else if (part.charAt(1) == 'W') {
                        sb.append("Winger");
                    } else if (part.charAt(1) == 'S') {
                        sb.append("Striker");
                    }
                }
                positionFull.add(sb.toString());
            }
        }
        this.primaryPositions = posFlags;
    }
    public boolean checkPosition(int pos){
        return (pos & primaryPositions) != 0;
    }
    public List<String> getFullPosition(){
        return positionFull;
    }
    public String getShortPosition(){
        return positionShort;
    }
}
