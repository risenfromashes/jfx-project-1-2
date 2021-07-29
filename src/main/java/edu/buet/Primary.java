package edu.buet;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import edu.buet.data.Club;
import edu.buet.data.Country;
import edu.buet.data.Currency;
import edu.buet.data.Player;
import edu.buet.messages.LogoutRequest;
import edu.buet.messages.LogoutResponse;
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

    enum NavMenu { PLAYERS, TRANSFERS, COUNTRIES }

    NavMenu currentMenu = NavMenu.PLAYERS;

    private Club club;
    private Image clubLogoImage;

    @FXML
    private Label clubName;
    @FXML
    private Label clubBalance;
    @FXML
    private Label totalSalaryLabel;
    @FXML
    private ImageView clubLogo;
    @FXML
    private Button playersButton;
    @FXML
    private Button transferListButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button countriesButton;
    @FXML
    private VBox body;
    private Parent header, search;
    private PlayerListHeaderController headerController;
    private PlayerSearchController searchController;
    private ListView<PlayerEntry> playerListView;
    private ObservableList<PlayerEntry> playerList;
    private ObservableList<PlayerEntry> transferList;
    private Currency totalSalary;


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

        totalSalary = new Currency(0.f);
        for ( var p : club.getPlayers())
            totalSalary.add(p.getWeeklySalary().getNumber() * 52);
        totalSalaryLabel.setText(totalSalary.getString());

        playersButton.onActionProperty().set(e -> {
            showMenu(NavMenu.PLAYERS);
        });

        transferListButton.onActionProperty().set(e -> {
            showMenu(NavMenu.TRANSFERS);
        });

        countriesButton.onActionProperty().set(e -> {
            showMenu(NavMenu.COUNTRIES);
        });

        logoutButton.onActionProperty().set(e -> {
            App.getClient().clearHandlers();
            App.connect().thenAccept( s -> {
                System.out.println("Logout button pressed");
                s.sendNow(new LogoutRequest(), LogoutResponse.class).thenAccept( res ->{
                    Platform.runLater(()->{
                        try {
                            App.setRoot("loginscreen");
                        } catch (IOException err) {
                            throw new RuntimeException(err);
                        }
                    });
                });
            });
        });

    }
    private void toggleButtonStyles() {
        playersButton.getStyleClass().clear();
        playersButton.getStyleClass().add("navbar-btn" + (currentMenu == NavMenu.PLAYERS  ? "-active" : ""));
        transferListButton.getStyleClass().clear();
        transferListButton.getStyleClass().add("navbar-btn" + (currentMenu == NavMenu.TRANSFERS ? "-active" : ""));
        countriesButton.getStyleClass().clear();
        countriesButton.getStyleClass().add("navbar-btn" + (currentMenu == NavMenu.COUNTRIES ? "-active" : ""));
    }

    private void showMenu(NavMenu menu) {
        this.currentMenu = menu;
        toggleButtonStyles();
        body.getChildren().clear();
        switch (currentMenu) {
        case PLAYERS:
            showHeader(false);
            showPlayers();
            break;
        case TRANSFERS:
            showHeader(false);
            showTransferPlayers();
            break;
        case COUNTRIES:
            populateCountries();
            break;
        }
    }
    private void showMenu() {
        showMenu(currentMenu);
    }

    private void showInfo(PlayerEntry entry, boolean transferOrBuy) {
        body.getChildren().clear();
        var p = new PlayerInfo(club, entry, transferOrBuy);
        p.backButtonClickedProperty().set( ev -> {
            if (ev.getButton() == MouseButton.PRIMARY) {
                showMenu();
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
                playerListView.setItems(playerList);
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
                var player = o.get();
                switch (o.op) {
                case ADD_PLAYER:
                    if (!containsPlayer(playerList, player)) {
                        playerList.add(generateEntry(player, false));
                        totalSalary.add(player.getWeeklySalary().getNumber() * 52);
                        totalSalaryLabel.setText(totalSalary.getString());
                    }
                    break;
                case REMOVE_PLAYER:
                    if (containsPlayer(playerList, player)) {
                        removePlayer(playerList, player);
                        totalSalary.substract(player.getWeeklySalary().getNumber() * 52);
                        totalSalaryLabel.setText(totalSalary.getString());
                    }
                    break;
                case ADD_TRANSFER:
                    if (!containsPlayer(transferList, player)) {
                        transferList.add(generateEntry(player, true));
                    }
                    break;
                case REMOVE_TRANSFER:
                    if (containsPlayer(transferList, player)) {
                        transferList.remove(generateEntry(player, true));
                    }
                    break;
                }
                club.getBalance().add(o.balanceDelta);
                clubBalance.setText(club.getBalance().getString());
            });
        });
        App.connect().thenAccept( s -> {
            s.sendNow(new TransferListRequest(), TransferListResponse.class)
            .thenAccept( res -> {
                if (res.success()) {
                    Platform.runLater( ()-> {
                        for (var player : res.get()) {
                            if (player.getClub().getId() != club.getId()) {
                                if (!containsPlayer(transferList, player)) {
                                    transferList.add(generateEntry(player, true));
                                }
                            }
                        }
                    });
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
    private void populateCountries() {
        var countryList = new ListView<CountryEntry>();
        VBox.setVgrow(countryList, Priority.ALWAYS);
        playerListView.setMaxHeight(Double.MAX_VALUE);
        var m1 = new HashMap<Integer, Integer>();
        var m2 = new HashMap<Integer, Country>();
        playerList.forEach(p -> {
            var c = p.getPlayer().getCountry();
            int id = c.getId();
            int n = m1.get(id) != null ? m1.get(id) : 0;
            m1.put(id, n + 1);
            m2.put(id, c);
        });
        m1.forEach( (i, n) -> {
            countryList.getItems().add(new CountryEntry(m2.get(i), n));
        });
        body.getChildren().add(countryList);
    }
}
