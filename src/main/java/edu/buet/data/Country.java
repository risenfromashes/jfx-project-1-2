package edu.buet.data;

public class Country {
    private final int id;
    private final String name;
    private final String altName;
    private final String imageUrl;
    public Country(int id, String name, String altName, String url) {
        this.id = id;
        this.name = name;
        this.altName = altName;
        this.imageUrl = url;
    }
    public Country(String line){
        var props = line.strip().split(";");
        this.id = Integer.parseInt(props[0].strip());
        this.name = props[1].strip();
        this.altName = props[2].strip();
        this.imageUrl = props[3].strip();
    }
    final public int getId(){
        return id;
    }
    final public String getName(){
        return name;
    }
    final public String getAltName(){
        return altName;
    }
    final public String getImageUrl(){
        return imageUrl;
    }
}
