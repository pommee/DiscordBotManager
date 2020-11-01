package Controllers;

import Models.Bot;
import Models.MessageHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ManageBots {

    @FXML
    private TextField botSecret, botName;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private void writeBot() {
        if (botSecret.getText().equals("") || botName.getText().equals("")) {
            MessageHandler.createNotification("Error", "Fields can´t be empty");
        } else {
            try {
                ArrayList<Bot> itemsList = (ArrayList<Bot>) getBots();
                itemsList.add(new Bot(botSecret.getText(), botName.getText()));
                System.out.println(itemsList);
                File file = new File("bots.txt");
                file.delete();
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(itemsList); // write MenuArray to
                // ObjectOutputStream
                oos.close();
                MessageHandler.createNotification("Added", "Added " + botName.getText() + " as a new Bot");
                botName.setText("");
                botSecret.setText("");
                cancel();
                // fillTables();
            } catch (Exception ignored) {
            }
        }
    }

    static List<Bot> getBots() {
        ArrayList<Bot> chann = new ArrayList<>();

        try {
            FileInputStream fis = new FileInputStream("bots.txt");
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
