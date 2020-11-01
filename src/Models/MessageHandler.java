package Models;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public abstract class MessageHandler {

    public static void createNotification(String title, String message) {
        Notifications notifications = Notifications.create()
                .title(title)
                .text(message)
                .darkStyle()
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT);
        notifications.show();
    }
}
