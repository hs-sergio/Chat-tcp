package com.example.chat_tcp;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    // Crea un objeto socket para comunicarnos con cualquiera que se conecte
    private ServerSocket serverSocket;

    private Socket socket;
    private BufferedReader bufferedReader; // Para leer los mensajes que nos enviarn
    private BufferedWriter bufferedWriter;

    public Server(ServerSocket serverSocket){

        try{
            this.serverSocket = serverSocket;
            this.socket = serverSocket.accept();
            this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream())); // Para recibir lo que nos manden
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error creando el servidor");
            // Si tira el error, hay que cerrar todo
            CloseEverything(socket, bufferedWriter, bufferedReader);


        }

    }

        public void sendMessageToClient(String messageToClient){
            try{
                bufferedWriter.write(messageToClient);
                bufferedWriter.newLine();

                bufferedWriter.flush();

            }catch (IOException e){
                e.printStackTrace();
                System.out.println("Error mandando mensaje al cliente");
                CloseEverything(socket, bufferedWriter, bufferedReader);
            }
        }

        public void recieveMessageFromClient(VBox vbox){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Cualquier cosa dentro de aqui se ejecutará en un hilo diferente
                    while(socket.isConnected()){

                        // Primero leemos el mensaje del cliente
                        try {
                            String messageFromClient = bufferedReader.readLine();
                            // Lo añadimos al GUI con el método correspondiente (el que necesitaba de algo especial porque se ejecuta en otro hilo...)
                            ServerController.addLabel(messageFromClient, vbox);

                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Error recibiendo mensaje del cliente");
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

