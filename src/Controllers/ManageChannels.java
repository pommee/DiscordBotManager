package Controllers;

import Models.SceneChanger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import Models.Channel;
import Models.MessageHandler;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ManageChannels {

    @FXML
    private TextField channelId, channelName;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private void writeChannel() {
        if (channelId.getText().equals("") || channelName.getText().equals("")) {
            MessageHandler.createNotification("Error", "Fields can´t be empty");
        } else {
            try {
                ArrayList<Channel> itemsList = (ArrayList<Channel>) getChannels();
                itemsList.add(new Channel(channelId.getText(), channelName.getText()));
                System.out.println(itemsList);
                File file = new File("ids.txt");
                file.delete();
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(itemsList); // write MenuArray to
                // ObjectOutputStream
                oos.close();
                //allChannels = itemsList;
                MessageHandler.createNotification("Added", "Added " + channelName.getText() + " as a new channel");
                channelName.setText("");
                channelId.setText("");
                cancel();
                // fillTables();
            } catch (Exception ignored) {
            }
        }
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

    @FXML
    private void cancel() {
        // get a handle to the stage
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        // do what you have to do
        stage.close();
    }
}
