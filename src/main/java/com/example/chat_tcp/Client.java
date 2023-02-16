package com.example.chat_tcp;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket){

        try{
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error creando el cliente");
            CloseEverything(socket, bufferedWriter, bufferedReader);
        }

    }

    public void sendMessageToServer(String messageToServer){
        try{
            bufferedWriter.write(messageToServer);
            bufferedWriter.newLine();

            bufferedWriter.flush();

        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error mandando mensaje al cliente");
            CloseEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void recieveMessageFromServer(VBox vBox){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Cualquier cosa dentro de aqui se ejecutará en un hilo diferente
                while(socket.isConnected()){

                    // Primero leemos el mensaje del cliente
                    try {
                        String messageFromServer = bufferedReader.readLine();
                        // Lo añadimos al GUI con el método correspondiente (el que necesitaba de algo especial porque se ejecuta en otro hilo...)
                        ServerController.addLabel(messageFromServer, vBox);

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error recibiendo mensaje del servidor");
                        CloseEverything(socket, bufferedWriter, bufferedReader);
                        break; // Necesario para romper el bucle cuando hay un error
                    }

                }
            }
        }).start();
    }


    public void CloseEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){

        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }

        }catch (IOException e){
            e.printStackTrace();
        }

    }


}
