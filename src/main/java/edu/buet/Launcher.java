package edu.buet;
public class Launcher {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--server"))
            AppServer.main(args);
        else
            App.main(args);
    }
}
