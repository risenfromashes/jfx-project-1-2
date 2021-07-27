package edu.buet.data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
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
        transfers = new ConcurrentHashMap<>();
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
                var t = new TransferOffer(line);
                db.players.get(t.getPlayerId()).setTransfer(t);
                db.transfers.put(t.getPlayerId(), t);
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
    public synchronized List<Club> getClubs() {
        return clubs.values().stream().collect(Collectors.toList());
    }
    public synchronized List<Player> getTransfers() {
        return transfers.values().stream().map(t -> players.get(t.getPlayerId())).collect(Collectors.toList());
    }
    public synchronized TransferOffer getTransferOffer(int playerId) {
        return transfers.get(playerId);
    }
    public synchronized boolean hasTransferOffer(int playerId) {
        return getTransferOffer(playerId) != null;
    }
    public TransferOffer createTransferOffer(int playerId, int sellingClub, float fee) {
        return new TransferOffer(playerId, sellingClub, new Currency(fee));
    }
    public synchronized TransferOffer addTransferOffer(TransferOffer offer) {
        players.get(offer.getPlayerId()).setTransfer(offer);
        return transfers.put(offer.getPlayerId(), offer);
    }
    public synchronized TransferOffer removeTransferOffer(int playerId) {
        players.get(playerId).setTransfer(null);
        return transfers.remove(playerId);
    }
}
