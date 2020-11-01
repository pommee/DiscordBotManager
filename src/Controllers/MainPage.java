package Controllers;

import Models.Bot;
import Models.Channel;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import reactor.core.publisher.Mono;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainPage implements Initializable {

    ArrayList<Channel> allChannels = new ArrayList<>();
    ArrayList<Bot> allBots = new ArrayList<>();
    Long chaId;
    String botSecretKey;
    GatewayDiscordClient client;
    @FXML
    private TextArea customMessage;
    @FXML
    private TextField thumbNail, botKey, channelID;
    @FXML
    private Label loggedInStatus, messageSenderName;
    @FXML
    private ImageView imagePreview, botImage;
    @FXML
    private ComboBox selectChannel, selectBot;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            try {
                allChannels = (ArrayList<Channel>) getChannels();
                allBots = (ArrayList<Bot>) getBots();
                fillTables();
            } catch (NullPointerException ignored) {
            }
        }).start();
    }

    static List<Channel> getChannels() {
        ArrayList<Channel> chann = new ArrayList<>();

        try {
            FileInputStream fis = new FileInputStream("ids.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);

            chann = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException ignored) {
        }
        return chann;
    }

    static List<Bot> getBots() {
        ArrayList<Bot> bot = new ArrayList<>();

        try {
            FileInputStream fis = new FileInputStream("bots.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);

            bot = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException ignored) {
        }
        return bot;
    }

    @FXML
    private void fillTables() {
        for (Channel channel : allChannels) {
            String channelName = channel.getName();
            if (!selectChannel.getItems().contains(channelName)) {
                selectChannel.getItems().add(channelName);
            }
        }
        for (Bot bot : allBots) {
            String botName = bot.getName();
            if (!selectBot.getItems().contains(botName)) {
                selectBot.getItems().add(botName);
            }
        }
    }

    @FXML
    private void fillBotKey() {
        if (!selectBot.getValue().equals("")) {
            for (Bot bot : allBots) {
                if (selectBot.getValue().equals(bot.getName())) {
                    botKey.setText(bot.getKey());
                }
            }
        }
    }

    @FXML
    private void fillChannelId() {
        if (!selectChannel.getValue().equals("")) {
            for (Channel cha : allChannels) {
                if (selectChannel.getValue().equals(cha.getName())) {
                    channelID.setText(cha.getId());
                }
            }
        }
    }

    @FXML
    private void login() {
        new Thread(() -> {
            try {
                for (Bot bot : allBots) {
                    if (selectBot.getValue().equals(bot.getName())) {
                        setBotSecretKey(bot.getKey());
                    }
                }
                client = DiscordClientBuilder.create(getBotSecretKey()).build().login().block();

                client.getEventDispatcher().on(ReadyEvent.class)
                        .subscribe(event -> {
                            User self = event.getSelf();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    loggedInStatus.setTextFill(Color.web("Green"));
                                    loggedInStatus.setText(self.getUsername() + "#" + self.getDiscriminator());
                                    botImage.setImage(new Image(self.getAvatarUrl()));
                                    messageSenderName.setText(self.getUsername());
                                    messageSenderName.setOpacity(100);
                                }
                            });
                        });
                client.onDisconnect().block();
            } catch (NumberFormatException e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loggedInStatus.setText("Failed to login");
                    }
                });
            }
        }).start();
    }

    @FXML
    private void sendMessage() {
        String message = customMessage.getText();
        if (message.length() >= 2000) {
            Models.MessageHandler.createNotification("Error", "Messages cant be over 2000 characters!");
        } else {
            String image = thumbNail.getText();
            for (Channel cha : allChannels) {
                if (cha.getName().equals(selectChannel.getValue())) {
                    setChaId(Long.parseLong(cha.getId()));
                }
            }
            TextChannel channel = (TextChannel) client.getChannelById(Snowflake.of(getChaId())).block();
            Mono<Message> mess = channel.createMessage(messageSpec -> {
                messageSpec.setEmbed(embedSpec -> embedSpec.setDescription(message).setThumbnail(image));
            });
            mess.subscribe();
        }
    }

    @FXML
    private void manageChannels() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/Views/ManageChannels.fxml"));
            /*
             * if "fx:controller" is not set in fxml
             * fxmlLoader.setController(NewWindowController);
             */
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ignored) {
        }
    }

    @FXML
    private void manageBots() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/Views/ManageBots.fxml"));
            /*
             * if "fx:controller" is not set in fxml
             * fxmlLoader.setController(NewWindowController);
             */
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ignored) {
        }
    }

    @FXML
    private void setThumbNail() {
        new Thread(() -> {
            imagePreview.setImage(new Image(thumbNail.getText()));
        }).start();
    }

    public Long getChaId() {
        return chaId;
    }

    public void setChaId(Long chaId) {
        this.chaId = chaId;
    }

    public String getBotSecretKey() {
        return botSecretKey;
    }

    public void setBotSecretKey(String botSecretKey) {
        this.botSecretKey = botSecretKey;
    }
}
