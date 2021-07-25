package edu.buet.data;

public class Country {
    private final int id;
    private final String name;
    private final String altName;
    private final String flagUrl;
    public Country(int id, String name, String altName, String flagUrl) {
        this.id = id;
        this.name = name;
        this.altName = altName;
        this.flagUrl = flagUrl;
    }
    public Country(String line){
        var props = line.strip().split(";");
        this.id = Integer.parseInt(props[0].strip());
        this.name = props[1].strip();
        this.altName = props[2].strip();
        this.flagUrl = props[3].strip();
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
    final public String getFlagUrl(){
        return flagUrl;
    }
    public String toString() {
//id;name;alt-name;image-url;
        var sb = new StringBuilder();
        sb.append(id);
        sb.append(';');
        sb.append(name);
        sb.append(';');
        sb.append(altName);
        sb.append(';');
        sb.append(flagUrl);
        sb.append(';');
        return sb.toString();
    }
}
