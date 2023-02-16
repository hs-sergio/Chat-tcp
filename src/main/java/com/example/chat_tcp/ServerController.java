package com.example.chat_tcp;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {  // Implements Initializable.. esto nos permite hacer override de nuestro metodo initialize.
    @FXML
    private Button btn_send;
    @FXML
    private TextField txt_send;
    @FXML
    private VBox vbox_messages;
    @FXML
    private ScrollPane sp_main;


    // Vamos a necesitar Server
    private Server server;

    @Override
    public void initialize(URL location, ResourceBundle resources) { // Dentro de este método podemos trabajar con los widgets del fxml (del GUI)

        try{
            server = new Server(new ServerSocket(5555));
        }catch (IOException e){
            e.printStackTrace();
        }

        // Este método permitira que baje automáticamente cuando lleguen mensajes, si no estuviese tendríamos que bajar nosotros manualmente
        vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sp_main.setVvalue((Double) newValue);
            }
        });

        // Este método se ejecuta en otro hilo. Recibe el mensaje y lo añade al vbox de mensajes recibidos
        server.recieveMessageFromClient(vbox_messages);


        btn_send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String messageToSend = txt_send.getText(); // Recoge el valor del textField con nuestro mensaje
                if(!messageToSend.isEmpty()){
                    // Si el mensaje no está vacio mandamos el mensaje añadimos nuestro mensaje al vbox de mensajes y lo enviamos
                    HBox hbox = new HBox();
                    hbox.setAlignment(Pos.CENTER_RIGHT); // Que aparezca nuestro mensaje a la derecha
                    hbox.setPadding(new Insets(5,5,5,10));

                    Text text = new Text(messageToSend);
                    TextFlow textFlow = new TextFlow(text); // Nos permite estilizar textos y aparte si el texto es muy largo nos permite saltar la linea

                    textFlow.setStyle("-fx-color: rgb(0,0,0); " +
                            "-fx-background-color: rgb(142,41,237);" +
                            " -fx-background-radius: 20px");
                    textFlow.setPadding(new Insets(5,10,5,10));
                    text.setFill(Color.color(0.934, 0.945, 0.996));

                    hbox.getChildren().add(textFlow);
                    vbox_messages.getChildren().add(hbox);

                    // Hasta aqui solamente hemos colocado NUESTROS mensajes en NUESTRA GUI

                    // Ahora queremos enviarlos a otra persona

                    server.sendMessageToClient(messageToSend);

                    // Borramos el mensaje de nuestro txtField
                    txt_send.clear();

                }
            }
        });

    }

    // Es estático y queremos que pertenezca a nuestra clase controlador
    // Este es el método que nos va a permitir añadir los mensajes del Cliente a la GUI del servidor
    public static void addLabel(String messageFromClient, VBox vbox){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT); // Sus mensjes a la IZQUIERDA
        hBox.setPadding(new Insets(5,5,5,10));

        Text text = new Text(messageFromClient);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233,233,235);" +
                " -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5,10,5,10));
        hBox.getChildren().add(textFlow);

        // IMPORTANTE. No se puede actualizar la GUI desde otro hilo, y en este caso, cuando nos mandan un mensaje lo hacen desde
        // otro hilo, por ello tenemos que utilizar esto

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox.getChildren().add(hBox);
            }
        });
    }

}