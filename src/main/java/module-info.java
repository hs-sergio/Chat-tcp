module com.example.chat_tcp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.chat_tcp to javafx.fxml;
    exports com.example.chat_tcp;
}