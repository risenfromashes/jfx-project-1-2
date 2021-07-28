package edu.buet;

import java.io.IOException;

import edu.buet.data.Club;
import edu.buet.data.Currency;
import edu.buet.data.Player;
import edu.buet.data.TransferOffer;
import edu.buet.messages.TransferOfferRequest;
import edu.buet.messages.TransferOfferResponse;
import edu.buet.messages.TransferRequest;
import edu.buet.messages.TransferResponse;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PlayerInfo extends VBox {
    @FXML
    private Label name;
    @FXML
    private Label country;
    @FXML
    private Label age;
    @FXML
    private Label height;
    @FXML
    private Label weight;
    @FXML
    private Label jersyNumber;
    @FXML
    private Label preferredFoot;
    @FXML
    private Label positionLine0;
    @FXML
    private Label positionLine1;
    @FXML
    private Label positionLine2;
    @FXML
    private Label salary;
    @FXML
    private Label value;
    @FXML
    private ImageView imageView;
    @FXML
    private ImageView flagView;
    @FXML
    private Button sellOrConfirmButton;
    @FXML
    private Button backButton;
    @FXML
    private Spinner<Double> feeSpinner;
    @FXML
    private HBox transferInfo;
    @FXML
    private HBox transferBox;
    @FXML
    private Label transferFee;
    @FXML
    private VBox body;

    private Label[] positionLines = new Label[3];

    private Player player;
    private Club club;
    private PlayerEntry entry;

    private BottomBar bottomBar;

    private boolean transferPrompt;

    public PlayerInfo(Club club, PlayerEntry entry, boolean transferPrompt) {
        this.club = club;
        this.transferPrompt = transferPrompt;
        this.entry = entry;
        this.player = entry.getPlayer();
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("playerinfo.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        VBox.setVgrow(this, Priority.ALWAYS);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    ObjectProperty<EventHandler<? super MouseEvent>> backButtonClickedProperty() {
        return backButton.onMouseClickedProperty();
    }

    @FXML
    void initialize() {
        bottomBar = new BottomBar();
        positionLines[0] = positionLine0;
        positionLines[1] = positionLine1;
        positionLines[2] = positionLine2;
        name.setText(player.getName());
        country.setText(player.getCountry().getName());
        age.setText(Integer.toString(player.getAge()));
        height.setText(player.getHeight() + " cm");
        weight.setText(player.getWeight() + " kg");
        jersyNumber.setText(Integer.toString(player.getJerseyNumber()));
        preferredFoot.setText(player.getPreferredFoot().toString());
        var positions = player.getPosition().getFullPosition();
        for (int i = 0; i < 3; i++) {
            if ( i < positions.size()) {
                positionLines[i].setText(positions.get(i));
            } else {
                positionLines[i].setText("");
            }
        }
        name.setText(player.getName());
        imageView.setSmooth(false);
        flagView.setSmooth(false);
        imageView.setCache(true);
        flagView.setCache(true);
        imageView.setImage(entry.getPlayerImage());
        flagView.setImage(entry.getFlagImage());
        if (player.hasTransfer()) {
            transferBox.setVisible(false);
            transferInfo.setVisible(true);
            transferFee.setText(player.getTransfer().getFee().getString());
            if (player.getTransfer().getSellingClubId() == club.getId()) {
                sellOrConfirmButton.setVisible(false);
            } else {
                if (!transferPrompt) {
                    sellOrConfirmButton.setText("Buy");
                } else {
                    sellOrConfirmButton.setText("Confirm");
                }
                sellOrConfirmButton.setVisible(true);
            }
        } else {
            transferInfo.setVisible(false);
            sellOrConfirmButton.setVisible(true);
            if (!transferPrompt) {
                sellOrConfirmButton.setText("Sell");
                transferBox.setVisible(false);
            } else {
                sellOrConfirmButton.setText("Confirm");
                transferBox.setVisible(true);
            }
        }
        sellOrConfirmButton.onActionProperty().set( e -> {
            if (transferPrompt) {
                if (player.hasTransfer()) { //buy
                    if (club.getBalance().getNumber() > player.getTransfer().getFee().getNumber()) {
                        if (player.getTransfer().getSellingClubId() != club.getId()) {
                            App.connect().thenAccept( s -> {
                                s.sendNow(new TransferRequest(player.getId()), TransferResponse.class)
                                .thenAccept( res -> {
                                    Platform.runLater(()->{
                                        if (res.success()) {
                                            transferBox.setVisible(false);
                                            sellOrConfirmButton.setVisible(false);
                                            transferInfo.setVisible(false);
                                        } else {
                                            bottomBar.setMessage(BottomBar.Type.ERROR, res.getMessage());
                                            bottomBar.show(body);
                                        }
                                    });
                                });
                            });
                        }
                    } else {
                        bottomBar.setMessage(BottomBar.Type.WARNING, "Not enough budget");
                        bottomBar.show(body);
                    }
                } else { //sell
                    var fee = (float)(double)feeSpinner.getValue();
                    App.connect().thenAccept( s -> {
                        s.sendNow(new TransferOfferRequest(player.getId(), fee), TransferOfferResponse.class)
                        .thenAccept( res -> {
                            Platform.runLater(()->{
                                if (res.success()) {
                                    var cf = new Currency(fee);
                                    transferBox.setVisible(false);
                                    sellOrConfirmButton.setVisible(false);
                                    transferInfo.setVisible(true);
                                    transferFee.setText(cf.getString());
                                    entry.sellOrBuyButton().setVisible(false);
                                    player.setTransfer(new TransferOffer(player.getId(), club.getId(), cf));
                                } else {
                                    bottomBar.setMessage(BottomBar.Type.ERROR, res.getMessage());
                                    bottomBar.show(body);
                                }
                            });
                        });
                    });
                }
            } else {
                transferPrompt = true;
                sellOrConfirmButton.setText("Confirm");
                if (!player.hasTransfer())
                    transferBox.setVisible(true);
            }
        });
    }
}
