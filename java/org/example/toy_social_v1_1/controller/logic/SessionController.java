package org.example.toy_social_v1_1.controller.logic;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.example.toy_social_v1_1.Main;
import org.example.toy_social_v1_1.domain.entities.FriendRequest;
import org.example.toy_social_v1_1.domain.entities.Friendship;
import org.example.toy_social_v1_1.domain.entities.Message;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.domain.ui.FriendListCell;
import org.example.toy_social_v1_1.domain.ui.MessageListCell;
import org.example.toy_social_v1_1.dto.MessageDTO;
import org.example.toy_social_v1_1.factory.AlertFactory;
import org.example.toy_social_v1_1.service.entity.FriendRequestService;
import org.example.toy_social_v1_1.service.entity.FriendshipService;
import org.example.toy_social_v1_1.service.entity.MessageService;
import org.example.toy_social_v1_1.service.entity.UserService;
import org.example.toy_social_v1_1.service.network.Network;
import org.example.toy_social_v1_1.util.event.EntityChangeEvent;
import org.example.toy_social_v1_1.util.event.EntityChangeEventType;
import org.example.toy_social_v1_1.util.observer.Observer;
import org.example.toy_social_v1_1.util.paging.Page;
import org.example.toy_social_v1_1.util.paging.Pageable;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SessionController implements Observer<EntityChangeEvent<?>> {
    private UserService userService;
    private FriendshipService friendshipService;
    private User currentUser;
    private User chattingUser;
    private Long reply_id = 0L;
    private Network network;
    private FriendRequestService friendRequestService;
    private MessageService messageService;

    //FXML Controls
    //region
    @FXML
    private AnchorPane mainPanel;
    @FXML
    private HBox toolBar;
    @FXML
    private ImageView logoPic;
    @FXML
    private TextField searchBar;
    @FXML
    private Button searchButton;
    @FXML
    private ListView<User> options;
    @FXML
    private Label usernameLabel;
    @FXML
    private Button reqButton;
    @FXML
    private Button profileButton;
    @FXML
    private VBox chatBox;
    @FXML
    private ListView<MessageDTO> chatListView;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendMessageButton;
    @FXML
    private ListView<User> friendsListView;
    //endregion

    private Stage stage;
    private Stage tempStage;

    private ObservableList<User> friends;
    private ObservableList<FriendRequest> friendRequests;
    private SimpleIntegerProperty pendingRequestsCount;
    private ObservableList<MessageDTO> conversation;

    private int pageSize = 2;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label pageLabel;

    public SessionController(Stage stage,
                             User currentUser,
                             Network network,
                             UserService userService,
                             FriendshipService friendshipService,
                             FriendRequestService friendRequestService,
                             MessageService messageService) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.network = network;
        this.userService = userService;

        this.friendshipService = friendshipService;
        this.friendshipService.addObserver(this);

        this.friendRequestService = friendRequestService;
        friendRequestService.addObserver(this);
        this.messageService = messageService;
        messageService.addObserver(this);

        chattingUser = new User("empty", "empty");
        pendingRequestsCount = new SimpleIntegerProperty(0);

        initFriendRequests();
        initFriends();
    }

    @Override
    public void update(EntityChangeEvent<?> event) {
        if(event.getData() instanceof Friendship) {
            if(event.getType().equals(EntityChangeEventType.ADD)) {
                handleAddFriendship((EntityChangeEvent<Friendship>) event);
            }
            // TODO other functionalities in the future
        }
        else if(event.getData() instanceof User) {
            // TODO handle user later
        }
        else if(event.getData() instanceof Message) {
            if(event.getType().equals(EntityChangeEventType.ADD)) {
                handleAddMessage((EntityChangeEvent<Message>) event);
            }
            else {
                //TODO other functionalities in the future
            }

        }
        else if(event.getData() instanceof FriendRequest ||
                event.getOldData() instanceof FriendRequest) {
            if(event.getType().equals(EntityChangeEventType.ADD)) {
                handleAddFriendRequest((EntityChangeEvent<FriendRequest>) event);
            }
            else if(event.getType().equals(EntityChangeEventType.DELETE)) {
                handleDeleteFriendRequest((EntityChangeEvent<FriendRequest>) event);
            }
        }
    }

    private void handleAddFriendRequest(EntityChangeEvent<FriendRequest> event) {
        FriendRequest addedRequest = event.getData();
        if(addedRequest.getUser2().equals(currentUser.getID())) {
            System.out.println(addedRequest + " | " + pendingRequestsCount.get());
            pendingRequestsCount.set(pendingRequestsCount.get() + 1);
        }
    }

    private void handleDeleteFriendRequest(EntityChangeEvent<FriendRequest> event) {
        FriendRequest deletedRequest = event.getOldData();
        if(deletedRequest.getUser2().equals(currentUser.getID())) {
            pendingRequestsCount.set(pendingRequestsCount.get() - 1);
        }
    }

    private void handleAddFriendship(EntityChangeEvent<Friendship> event) {
        // TODO order by most recent
        if(event.getData().getId1().equals(currentUser.getID())) {
            userService.find(event.getData().getId2()).ifPresent(friends::add);
        }
    }

    private void handleAddMessage(EntityChangeEvent<Message> event) {
        if(event.getData().getTo().contains(currentUser) && chatBox.isVisible()) {
            conversation.add(new MessageDTO(event.getData()));
        }
    }

    private void initFriendRequests() {
        List<FriendRequest> pendingRequests = friendRequestService.getPendingRequests(currentUser.getID());
        List<FriendRequest> sentRequests = friendRequestService.getSentRequests(currentUser.getID());
        friendRequests = FXCollections.observableArrayList(Stream.concat(pendingRequests.stream(), sentRequests.stream())
                .sorted(Comparator.comparing(FriendRequest::getSent).reversed())
                .toList());
    }

    private void initFriends() {
        Page<User> pagedFriends = network.findAllUsersFriendshipsOnPage(new Pageable(currentPage, pageSize), currentUser.getID());
        List<User> userFriends = StreamSupport.stream(pagedFriends.getElementsOnPage().spliterator(), false)
                .toList();
        friends = FXCollections.observableArrayList(userFriends);
        totalNumberOfElements = pagedFriends.getTotalNumberOfElements();
    }

    @FXML
    public void initialize() {
        usernameLabel.setText(usernameLabel.getText() + currentUser.getUsername() + "!");

        configureSearchBar();
        configureChatBox();
        configureFriendsList();

        pendingRequestsCount.addListener((_, _, newVal) -> {
            if(newVal.intValue() > 0) {
                reqButton.setText("Pending requests - " + newVal);
            }
            else {
                reqButton.setText("Pending requests");
            }
        });
    }

    //SEARCH USERS ACTIONS
    //TODO separate styleOptions method
    //region
    @FXML
    private void configureSearchBar() {
        setSearchTextChanged();
        setSelectedUserClicked();
    }

    @FXML
    public void setSearchTextChanged() {
        searchBar.textProperty().addListener((_, _, newValue) -> {
            if(!newValue.isEmpty()) {
                options.setItems(FXCollections.observableArrayList(userService.getUsersStartingWith(newValue, currentUser.getUsername())));
                if(!options.getItems().isEmpty()) {
                    options.setVisible(true);
                    options.setPrefHeight(Math.min(2, options.getItems().size()) * 27);
                    styleOptions();
                }
            }
            else {
                options.setVisible(false);
            }
        });
    }

    @FXML
    private void styleOptions() {
        options.setCellFactory(new Callback<>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            options.getItems().remove(item);
                            return;
                        }
                        if (item != null) {
                            setText(item.getUsername());
                            setStyle("-fx-background-color: #00246B;" +
                                     "-fx-text-fill: #A1D6E2;");
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void setSelectedUserClicked() {
        options.setOnMouseClicked(this::mouseClickedOnUser);
    }

    @FXML
    private void mouseClickedOnUser(MouseEvent event) {
        if (event.getClickCount() == 1) {
            User selectedUser = options.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                double clickX = event.getScreenX();
                double clickY = event.getScreenY();
                loadUserDetailView(selectedUser, clickX, clickY);
            }
        }
    }

    @FXML
    private void loadUserDetailView(User selectedUser, double x, double y) {
        tempStage = new Stage();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/user-profile-view.fxml"));
        loader.setControllerFactory(param -> new UserProfileController(tempStage,
                userService,
                network,
                currentUser,
                selectedUser,
                friends,
                friendRequestService,
                friendRequests));

        try {
            Scene scene = new Scene(loader.load(), 300, 100);
            tempStage.setScene(scene);
            tempStage.setTitle("User details");
            tempStage.setX(x);
            tempStage.setY(y);
            tempStage.setResizable(false);
            tempStage.initStyle(StageStyle.UNDECORATED);
            tempStage.setAlwaysOnTop(true);
            tempStage.initOwner(stage);
            tempStage.showAndWait();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    //endregion

    //CHAT ACTIONS
    //region

    private void configureChatBox() {
        chatListView.visibleProperty().addListener((_, _, newValue) -> {
            if(newValue) {
                chatListView.setItems(conversation);
                chatListView.setOnMouseClicked(this::mouseClickedOnMessage);
                chatListView.setCellFactory(_ -> new MessageListCell(
                        currentUser
                ));
                chatListView.setPrefHeight(Math.min(10, conversation.size()) * 25);
                chatListView.scrollTo(conversation.size());
                conversation.addListener((ListChangeListener<MessageDTO>) _ -> {
                    chatListView.setPrefHeight(Math.min(10, conversation.size()) * 25);
                    chatListView.scrollTo(conversation.size());
                });
            }
        });
        chatBox.visibleProperty().addListener((_, _, newValue) -> {
            if(newValue) {
                initConversation();
                chatListView.setVisible(true);
            } else {
                chatListView.setVisible(false);
            }
        });
    }

    @FXML
    private void mouseClickedOnMessage(MouseEvent event) {
        if(event.getClickCount() == 2) {
            MessageDTO message = chatListView.getSelectionModel().getSelectedItem();
            messageField.setText("Replying to: " + message.getText()
                    + " - " + messageField.getText());
            reply_id = message.getId();
        }
    }

    private void initConversation() {
        conversation = FXCollections.observableArrayList(messageService.getConversation(
                currentUser.getID(), chattingUser.getID()
        ));
    }

    @FXML
    public void handleSendMessageReq(ActionEvent event) {
        if(messageField.getText().isEmpty()) {
            //TODO more complex logic
            return;
        }

        Message message;
        if(reply_id == 0) {
            message = new Message(currentUser,
                List.of(chattingUser), messageField.getText(),
                Timestamp.valueOf(LocalDateTime.now()), null);
        } else {
            if(messageField.getText().replaceFirst("Replying to: .+ - ", "").isEmpty()) {
                //TODO more complex logic
                return;
            }
            
            message = new Message(currentUser,
                    List.of(chattingUser),
                    messageField.getText().replaceFirst("Replying to: .+ - ", ""),
                    Timestamp.valueOf(LocalDateTime.now()),
                    messageService.find(reply_id).get());
            reply_id = 0L;
        }

        if(messageService.addMessage(message).isEmpty()) {
            conversation.add(new MessageDTO(message));
        }

        messageField.clear();
    }

    //endregion

    //FRIEND LIST ACTIONS
    //region
    @FXML
    private void configureFriendsList() {
        friendsListView.setItems(friends);
        friendsListView.setCellFactory(_ -> new FriendListCell(friendshipService,
                currentUser));
        friendsListView.setPrefHeight(Math.min(2, friends.size()) * 32);

        friends.addListener((ListChangeListener<User>) _ -> {
            initFriends();
            configureFriendsList();
            friendsListView.setPrefHeight(Math.min(2, friends.size()) * 32);
        });
        configurePagingControls();
        setSelectedFriendClicked();
    }

    private void configurePagingControls() {
        int maxPage = (int) Math.ceil((double) totalNumberOfElements / pageSize) - 1;
        if (maxPage == -1) {
            maxPage = 0;
        }
        if (currentPage > maxPage) {
            currentPage = maxPage;
            initFriends();
        }

        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage + 1) * pageSize >= totalNumberOfElements);
        pageLabel.setText("Page " + (currentPage + 1) + "/" + (maxPage + 1));
    }

    @FXML
    private void setSelectedFriendClicked() {
        friendsListView.setOnMouseClicked(this::mouseClickedOnFriend);
    }

    @FXML
    private void mouseClickedOnFriend(MouseEvent event) {
        if (event.getClickCount() == 1) {
            User selectedFriend = friendsListView.getSelectionModel().getSelectedItem();
            if (selectedFriend != null) {
                chattingUser = selectedFriend;
                double clickX = event.getScreenX();
                double clickY = event.getScreenY();

                loadFriendDetailView(selectedFriend, clickX, clickY);
            }
        }
    }

    @FXML
    private void loadFriendDetailView(User selectedFriend, double x, double y) {
        tempStage = new Stage();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/friend-actions-view.fxml"));
        loader.setControllerFactory(param -> new FriendActionsController(tempStage,
                currentUser, selectedFriend,
                network,
                friends,
                chatBox));

        try {
            Scene scene = new Scene(loader.load(), 200, 140);
            tempStage.setScene(scene);
            tempStage.setTitle("Friend Details");
            tempStage.setResizable(false);
            tempStage.setX(x);
            tempStage.setY(y);
            tempStage.initStyle(StageStyle.UNDECORATED);
            tempStage.setAlwaysOnTop(true);
            tempStage.initOwner(stage);
            tempStage.showAndWait();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    //endregion

    @FXML
    public void handleReqAction(ActionEvent event) {
        initFriendRequests();

        tempStage = new Stage();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/pending-requests-view.fxml"));
        loader.setControllerFactory(param -> new PendingRequestsController(currentUser,
                friendRequestService,
                userService,
                friendshipService,
                network,
                friends,
                friendRequests));

        try {
            Scene scene = new Scene(loader.load(), 400, 200);
            tempStage.setScene(scene);
            tempStage.setTitle("Pending Requests");
            tempStage.setResizable(false);
            //tempStage.initStyle(StageStyle.UNDECORATED);
            tempStage.setAlwaysOnTop(true);
            tempStage.initOwner(stage);
            tempStage.showAndWait();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void handlePreviousAction(ActionEvent event) {
        currentPage--;
        initFriends();
        configureFriendsList();
    }

    @FXML
    public void handleNextAction(ActionEvent event) {
        currentPage++;
        initFriends();
        configureFriendsList();
    }

    @FXML
    public void handleProfileAction(ActionEvent event) {

    }
}
