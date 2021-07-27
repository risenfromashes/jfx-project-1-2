package edu.buet;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import edu.buet.data.Club;
import edu.buet.data.Player;
import edu.buet.messages.NotifyTransfer;
import edu.buet.messages.TransferListRequest;
import edu.buet.messages.TransferListResponse;
import javafx.application.Platform;
//import edu.buet.data.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Primary extends VBox {
    private Club club;
    private Image clubLogoImage;

    @FXML
    private Label clubName;
    @FXML
    private Label clubBalance;
    @FXML
    private ImageView clubLogo;
    @FXML
    private Button playersButton;
    @FXML
    private Button transferListButton;
    @FXML
    private Button logoutButton;
    @FXML
    private VBox body;

    private Parent header, search;
    private PlayerListHeaderController headerController;
    private PlayerSearchController searchController;
    private ListView<PlayerEntry> playerListView;
    private ObservableList<PlayerEntry> playerList;
    private ObservableList<PlayerEntry> transferList;

    private BottomBar bottomBar;

    public Primary(Club club) {
        this.club = club;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("primary.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize() throws IOException {
        playerList = FXCollections.<PlayerEntry>observableArrayList();
        transferList = FXCollections.<PlayerEntry>observableArrayList();
        bottomBar = new BottomBar();
        clubLogo.setCache(true);
        clubLogo.setSmooth(false);
        clubLogoImage = new Image(club.getLogoUrl(), true);
        clubLogo.setImage(clubLogoImage);
        clubName.setText(club.getName());
        clubBalance.setText(club.getBalance().getString());
        var headerLoader = new FXMLLoader(App.class.getResource("playerlistheader.fxml"));
        var searchLoader = new FXMLLoader(App.class.getResource("playersearch.fxml"));
        header = headerLoader.load();
        headerController = headerLoader.getController();
        search = searchLoader.load();
        searchController = searchLoader.getController();
        populatePlayers();
        populateTransferPlayers();
        setupHeader();
        setupSearch();
        showHeader(false);
        showPlayers();


        playersButton.onActionProperty().set(e -> {
            showTransfer = false;
            toggleButtonStyles();
            body.getChildren().clear();
            showHeader(false);
            showPlayers();
        });

        transferListButton.onActionProperty().set(e -> {
            showTransfer = true;
            toggleButtonStyles();
            body.getChildren().clear();
            showHeader(false);
            showTransferPlayers();
        });
    }
    private boolean showTransfer = false;
    private void toggleButtonStyles() {
        transferListButton.getStyleClass().clear();
        transferListButton.getStyleClass().add("navbar-btn" + (showTransfer ? "-active" : ""));
        playersButton.getStyleClass().clear();
        playersButton.getStyleClass().add("navbar-btn" + (!showTransfer ? "-active" : ""));
    }

    private void showInfo(PlayerEntry entry, boolean transferOrBuy) {
        body.getChildren().clear();
        var p = new PlayerInfo(club, entry, transferOrBuy);
        p.backButtonClickedProperty().set( ev -> {
            if (ev.getButton() == MouseButton.PRIMARY) {
                showHeader(true);
                if (showTransfer) {
                    showTransferPlayers();
                } else {
                    showPlayers();
                }
            }
        });
        body.getChildren().add(p);
    }

    private void showPlayers() {
        playerListView.setItems(playerList);
        if (!body.getChildren().contains(playerListView)) body.getChildren().add(playerListView);
    }
    private void showTransferPlayers() {
        playerListView.setItems(transferList);
        if (!body.getChildren().contains(playerListView)) body.getChildren().add(playerListView);
    }

    private void showHeader(boolean removeFirst) {
        if (removeFirst) body.getChildren().remove(0);
        body.getChildren().add(0, header);
        headerController.resetCarets();
    }

    private void showSearch(boolean removeFirst) {
        if (removeFirst) body.getChildren().remove(0);
        body.getChildren().add(0, search);
    }

    private void setupHeader() {
        headerController.stateProperty().addListener( (ev, s0, state) -> {
            playerList.sort((p1_, p2_) -> {
                var p1 = p1_.getPlayer();
                var p2 = p2_.getPlayer();
                if (state.sortOrder == 0) return 0;
                int ret  = 0;
                switch (state.sortField) {
                case NUMBER:
                    ret = Integer.compare(p1.getJerseyNumber(), p2.getJerseyNumber());
                    break;
                case NAME:
                    ret = p1.getAltName().compareTo(p2.getAltName());
                    break;
                case COUNTRY:
                    ret = p1.getCountry().getAltName().compareTo(p2.getCountry().getAltName());
                    break;
                case  POSITION:
                    ret = p1.getPosition().getShortPosition().compareTo(p2.getPosition().getShortPosition());
                    break;
                case AGE:
                    ret =  Integer.compare(p1.getAge(), p2.getAge());
                    break;
                case HEIGHT:
                    ret =  Integer.compare(p1.getHeight(), p2.getHeight());
                    break;
                case SALARY:
                    ret =  Float.compare(p1.getWeeklySalary().getNumber(), p2.getWeeklySalary().getNumber());
                    break;
                }
                if (state.sortOrder == 1)
                    return ret;
                else
                    return -ret;
            });
        });
        headerController.searchButtonClickProperty().set( ev -> {
            if (ev.getButton() == MouseButton.PRIMARY) {
                showSearch(true);
            }
        });
    }

    private void setupSearch() {
        searchController.queryProperty().addListener( (ev, o1, o2) -> {
            playerListView.setItems(playerList.filtered( p -> o2.match(p.getPlayer())));
        });
        searchController.backButtonClickedProperty().set( ev -> {
            if (ev.getButton() == MouseButton.PRIMARY) {
                showHeader(true);
            }
        });
    }

    private void populatePlayers() {
        playerListView = new ListView<>(playerList);
        VBox.setVgrow(playerListView, Priority.ALWAYS);
        playerListView.setMaxHeight(Double.MAX_VALUE);
        playerList.addAll(club.getPlayers().stream().map(p -> generateEntry(p, false)).collect(Collectors.toList()));
    }
    private PlayerEntry generateEntry(Player player, boolean transfer) {
        var p = new PlayerEntry(player);
        p.infoButton().onActionProperty().set( e -> {
            showInfo(p, false);
        });
        if (transfer) {
            p.sellOrBuyButton().setText("Buy⯈");
            p.sellOrBuyButton().onActionProperty().set( e -> {
                showInfo(p, true);
            });
        } else {
            if (!player.hasTransfer()) {
                p.sellOrBuyButton().setText("Sell⯈");
                p.sellOrBuyButton().onActionProperty().set( e -> {
                    showInfo(p, true);
                });
            } else {
                p.sellOrBuyButton().setVisible(false);
            }
            p.sellOrBuyButton().onActionProperty().set( e -> {
                showInfo(p, true);
            });
        }
        return p;
    }
    private boolean containsPlayer(Collection<PlayerEntry> entries, Player player) {
        for (var entry : entries)
            if (entry.getPlayer().getId() == player.getId()) return true;
        return false;
    }
    private void removePlayer(Collection<PlayerEntry> entries, Player player) {
        for (var entry : entries) {
            if (entry.getPlayer().getId() == player.getId()) {
                entries.remove(entry);
                break;
            }
        }
    }
    private void populateTransferPlayers()  {
        App.getClient().on(NotifyTransfer.class, (o, s) -> {
            Platform.runLater(()->{
                if (o.op == NotifyTransfer.Op.ADD) {
                    var player = o.get();
                    if (player.getClub().getId() != club.getId()) { //offer
                        if (!containsPlayer(transferList, player)) {
                            transferList.add(generateEntry(player, true));
                        }
                    } else { //buy
                        if (!containsPlayer(playerList, player)) {
                            playerList.add(generateEntry(player, false));
                        }
                        if (containsPlayer(transferList, player)) {
                            removePlayer(transferList, player);
                        }
                    }
                } else if (o.op == NotifyTransfer.Op.REMOVE) {
                    var player = o.get();
                    removePlayer(transferList, player);
                    removePlayer(playerList, player);
                }
                club.getBalance().add(o.balanceDelta);
                clubBalance.setText(club.getBalance().getString());
            });
        });
        App.connect().thenAccept( s -> {
            s.sendNow(new TransferListRequest(), TransferListResponse.class)
            .thenAccept( res -> {
                if (res.success()) {
                    for (var player : res.get()) {
                        if (player.getClub().getId() != club.getId()) {
                            if (!containsPlayer(transferList, player)) {
                                transferList.add(generateEntry(player, true));
                            }
                        }
                    }
                } else {
                    Platform.runLater( ()-> {
                        bottomBar.setMessage(BottomBar.Type.WARNING, "Couldn't get transfer list, " + res.getMessage());
                        bottomBar.show(body);
                    });
                }
            }).exceptionally( e -> {
                Platform.runLater( ()-> {
                    bottomBar.setMessage(BottomBar.Type.WARNING, "Couldn't get transfer list, " + e.getMessage());
                    bottomBar.show(body);
                });
                return null;
            });
        });
    }
}
