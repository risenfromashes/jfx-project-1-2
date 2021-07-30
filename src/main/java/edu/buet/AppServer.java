package edu.buet;

import java.io.IOException;
import java.util.TreeSet;

import edu.buet.data.Player;
import edu.buet.data.PlayerDatabase;
import edu.buet.messages.ClubListRequest;
import edu.buet.messages.ClubListResponse;
import edu.buet.messages.LoginRequest;
import edu.buet.messages.LoginResponse;
import edu.buet.messages.LogoutRequest;
import edu.buet.messages.LogoutResponse;
import edu.buet.messages.NotifyTransfer;
import edu.buet.messages.TransferListRequest;
import edu.buet.messages.TransferListResponse;
import edu.buet.messages.TransferOfferRequest;
import edu.buet.messages.TransferOfferResponse;
import edu.buet.messages.TransferRequest;
import edu.buet.messages.TransferResponse;
import edu.buet.net.Server;

public class AppServer {
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 3001;
        try {
            var db = PlayerDatabase.parseFile("data/");
            var connectedClubs = new TreeSet<Integer>();
            var server = new Server();
            server.onAccept(s -> {
                System.out.println("Client Connected");
            })
            .on(ClubListRequest.class, (o, s) -> {
                System.out.println("listRequest");
                s.send(ClubListResponse.successMessage(db.getClubs()));
            })
            .on(LoginRequest.class, (o, s) -> {
                if (o.get().password.equals("1234") ) {
                    var c = db.getClub(o.get().clubId);
                    if (c != null) {
                        if (s.getAttachment() == null && !connectedClubs.contains(c.getId())) {
                            s.setAttachment(c.getId());
                            connectedClubs.add(c.getId());
                            s.send(LoginResponse.successMessage(c));
                        } else {
                            s.send(LoginResponse.errorMessage("Club already logged in"));
                        }
                    } else {
                        s.send(LoginResponse.errorMessage("Club ID doesn't exist"));
                    }
                } else {
                    s.send(LoginResponse.errorMessage("Incorrect password"));
                }
            })
            .on(TransferListRequest.class, (o, s) -> {
                var attch = s.getAttachment();
                if (attch != null && connectedClubs.contains(attch)) {
                    s.send(TransferListResponse.successMessage(db.getTransfers()));
                } else {
                    s.send(TransferListResponse.errorMessage("Not logged in"));
                }
            })
            .on(TransferOfferRequest.class, (o, s) -> {
                System.out.println("Transfer offer requested");
                var attch = s.getAttachment();
                if (attch != null && connectedClubs.contains(attch)) {
                    if (!db.hasTransferOffer(o.get().playerId)) {
                        var player = db.getPlayer(o.get().playerId);
                        var offer = db.createTransferOffer(o.get().playerId, (Integer)attch, o.get().fee);
                        db.addTransferOffer(offer);
                        s.send(TransferOfferResponse.successMessage());
                        for (var socket : server.getSockets()) {
                            var clubId = socket.getAttachment();
                            if (clubId != null && connectedClubs.contains(clubId)) {
                                //System.out.println("Notifying transfer");
                                if (!clubId.equals(attch)) { // don't add transfer to requesting club
                                    socket.notify(new NotifyTransfer(NotifyTransfer.Op.ADD_TRANSFER, player, 0.f));
                                }
                            }
                        }
                        db.writeToFile("data/");
                    } else {
                        s.send(TransferOfferResponse.errorMessage("Player already has transfer offer"));
                    }
                } else {
                    s.send(TransferOfferResponse.errorMessage("Not logged in"));
                }
            })
            .on(TransferRequest.class, (o, s) -> {
                var attch = s.getAttachment();
                if (attch != null && connectedClubs.contains(attch)) {
                    var playerId = o.get();
                    if (db.hasTransferOffer(playerId)) {
                        var player = db.getPlayer(playerId);
                        var offer = db.removeTransferOffer(playerId);
                        var prevClub = player.getClub();
                        var club = db.getClub((Integer)attch);
                        player.setClub(club);
                        club.addPlayer(player);
                        prevClub.removePlayer(player);
                        prevClub.getBalance().add(offer.getFee());
                        club.getBalance().substract(offer.getFee());
                        s.send(TransferResponse.successMessage());
                        //notify connected sockets
                        for (var socket : server.getSockets()) {
                            var clubId = socket.getAttachment();
                            if (clubId != null && connectedClubs.contains(clubId)) {
                                if (clubId.equals(club.getId())) {
                                    socket.notify(new NotifyTransfer(NotifyTransfer.Op.ADD_PLAYER, player, -offer.getFee().getNumber()));
                                    socket.notify(new NotifyTransfer(NotifyTransfer.Op.REMOVE_TRANSFER, player, 0.f));
                                } else if (clubId.equals(prevClub.getId())) {
                                    socket.notify(new NotifyTransfer(NotifyTransfer.Op.REMOVE_PLAYER, player, offer.getFee().getNumber()));
                                } else {
                                    socket.notify(new NotifyTransfer(NotifyTransfer.Op.REMOVE_TRANSFER, player, 0.f));
                                }
                            }
                        }
                        db.writeToFile("data/");
                    } else {
                        s.send(TransferResponse.errorMessage("Invalid transfer request or player already transferred"));
                    }
                } else {
                    s.send(LogoutResponse.errorMessage("Not logged in"));
                }
            })
            .on(LogoutRequest.class, (o, s) -> {
                var attch = s.getAttachment();
                if (attch != null) {
                    var clubId = (Integer)attch;
                    if (connectedClubs.contains(clubId)) {
                        connectedClubs.remove(clubId);
                        s.setAttachment(null);
                        s.send(LogoutResponse.successMessage());
                    } else {
                        s.send(LogoutResponse.errorMessage("Not logged in"));
                    }
                } else {
                    s.send(LogoutResponse.errorMessage("Not logged in"));
                }
            }).
            onClose(s -> {
                var attch = s.getAttachment();
                if (attch != null) {
                    connectedClubs.remove((Integer)attch);
                    System.out.println("Client Disconnected");
                }
            }).run(port);
        } catch (IOException e) {
            System.out.println("Couldn't create server");
        }
    }
}
