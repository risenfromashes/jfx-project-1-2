package edu.buet.messages;
import java.io.Serializable;
public class ClubEntry implements Serializable {
    public final int id;
    public final String name;
    public final String altName;
    ClubEntry(int id, String name, String altName) {
        this.id = id;
        this.name = name;
        this.altName = altName;
    }
}
