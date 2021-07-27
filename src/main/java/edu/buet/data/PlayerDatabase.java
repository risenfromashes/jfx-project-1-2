package edu.buet.data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlayerDatabase {
    private static final Charset cs = Charset.forName("UTF-8");
    private Map<Integer, Player> players;
    private Map<Integer, Club> clubs;
    private Map<Integer, Country> countries;
    private Map<Integer, TransferOffer> transfers;
    private PlayerDatabase() {
        players = new ConcurrentHashMap<>();
        countries = new ConcurrentHashMap<>();
        clubs = new ConcurrentHashMap<>();
    }
    public static PlayerDatabase parseFile(String dir) throws IOException {
        var db = new PlayerDatabase();
        var fs = FileSystems.getDefault();
        for (var line : Files.readAllLines(fs.getPath(dir, "countries.txt"), cs)) {
            var c = new Country(line);
            db.countries.put(c.getId(), c);
        }
        for (var line : Files.readAllLines(fs.getPath(dir, "clubs.txt"), cs)) {
            var c = new Club(line);
            db.clubs.put(c.getId(), c);
        }
        for (var line : Files.readAllLines(fs.getPath(dir, "players.txt"), cs)) {
            var p = new Player(line, db.clubs, db.countries);
            var c = p.getClub();
            if (c != null) c.addPlayer(p);
            db.players.put(p.getId(), p);
        }
        try {
            for (var line : Files.readAllLines(fs.getPath(dir, "transfers.txt"), cs)) {
                var t = new TransferOffer(line, db.players);
                db.transfers.put(t.getPlayer().getId(), t);
            }
        } catch (IOException e) { //file may not exist
        }
        return db;
    }
    public synchronized void writeToFile(String dir) throws IOException {
        var fs = FileSystems.getDefault();
        Files.write(fs.getPath(dir, "countries.txt"), players.values().stream().map(c -> c.toString()).collect(Collectors.toList()), cs);
        Files.write(fs.getPath(dir, "clubs.txt"), players.values().stream().map(c -> c.toString()).collect(Collectors.toList()), cs);
        Files.write(fs.getPath(dir, "players.txt"), players.values().stream().map(c -> c.toString()).collect(Collectors.toList()), cs);
        Files.write(fs.getPath(dir, "transfers.txt"), transfers.values().stream().map(c -> c.toString()).collect(Collectors.toList()), cs);
    }
    public synchronized Player getPlayer(int id){
        return players.get(id);
    }
    public synchronized Club getClub(int id) {
        return clubs.get(id);
    }
    public synchronized Collection<Club> getClubs() {
        return clubs.values();
    }
    public synchronized Collection<TransferOffer> getTransfers() {
        return transfers.values();
    }
    public synchronized TransferOffer getTransferOffer(int playerId) {
        return transfers.get(playerId);
    }
    public synchronized boolean hasTransferOffer(int playerId) {
        return getTransferOffer(playerId) != null;
    }
    public TransferOffer createTransferOffer(int playerId, float fee) {
        return new TransferOffer(players.get(playerId), new Currency(fee));
    }
    public synchronized TransferOffer addTransferOffer(TransferOffer offer) {
        return transfers.put(offer.getPlayer().getId(), offer);
    }
    public synchronized TransferOffer removeTransferOffer(int playerId) {
        return transfers.remove(playerId);
    }
}
