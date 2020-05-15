package sample;

import javafx.collections.FXCollections;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Queue;

public class Server
{
    public Controller controller;

    public static int currentPort;

    ServerSocket serverSocket = null;
    Socket socket;

    DataOutputStream out;
    DataInputStream in;

    Queue<File> outgoingFiles;

    boolean receivingFile;
    boolean sendingFile;


    public Server(int port, Controller _controller) throws IOException
    {
        // Assign controller
        controller = _controller;

        // Determine socket and port
        currentPort = port;
        serverSocket = new ServerSocket(currentPort);
//
//        socket = serverSocket.accept();
//
//        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
//        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void launchServer()
    {

        controller.log("Launching server...");
















        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    try
                    {
//                serverSocket = new ServerSocket(currentPort);

                        while (true)
                        {
                            System.out.println("Waiting for accept");
                            socket = serverSocket.accept();

                            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));


                            // read data from the client
                            // ..

                            // send data to the client
                            // ..
                        }
                    }
                    catch (Exception ex)
                    {
                        controller.log("Error attempting to receive file: " + ex.getCause());
                        System.err.println("Error attempting to receive file: " + ex.getCause());
                        ex.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void sendFile(File file) throws IOException
    {
        sendingFile = true;
        controller.log("Sending file: " + file.getName());

        // Create File transfer for table
        FileTransfer newTransfer = new FileTransfer(file);
        newTransfer.status = "Sending";
        controller.filesOutgoing.add(newTransfer);

        // Get file contents as byte array
        byte[] fileContent = Files.readAllBytes(file.toPath());

        // Write file name
        // Write length of the file
        // Write file contents
        out.writeUTF(file.getName());
        out.writeInt(fileContent.length);
        out.write(fileContent);

        newTransfer.progress = 100.0;
        newTransfer.status = "Complete";

        controller.log("File sent successfully.");
        sendingFile = false;
    }
}
