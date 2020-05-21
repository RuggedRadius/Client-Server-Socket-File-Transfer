package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import org.junit.Test;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.*;

/*

 * @startuml

 * car --|> wheel

 * @enduml

 */

public class Server
{
    public Controller controller;
    public static int currentPort;
    public ServerSocket serverSocket = null;
    public Socket socket;
    public DataOutputStream out;
    public DataInputStream in;
    public ArrayDeque<File> outgoingFiles;
    public boolean receivingFile;
    public boolean sendingFile;
    public Thread receiveLoopThread;
    public Thread sendLoopThread;

    // Constructor
    public Server(int port, Controller _controller) throws IOException {
        // Assign controller
        controller = _controller;

        // Determine socket and port
        currentPort = port;
        serverSocket = new ServerSocket(currentPort);

//        outgoingFiles = new LinkedList<>();
        outgoingFiles = new ArrayDeque<File>();
    }

    // General methods
    public boolean initServer() {

        System.out.println("Launching server...");
        Runnable sendLoopRunnable = new Runnable() {
            @Override
            public void run() {
                try
                {
                    while (true)
                    {
                        // Make sure socket is assigned
                        if (socket == null && serverSocket != null) {
                            getSocket();
                        }

                        // send data to the client (if any)
                        if (outgoingFiles.size() > 0) {

                            File fileToSend = outgoingFiles.peek();
                            if (fileToSend != null && !sendingFile)
                            {
                                sendFile(fileToSend);
                                outgoingFiles.remove();
                            }
                        }

                        // Do a little sleep
                        Thread.sleep(250);
                    }
                }
                catch (Exception ex)
                {
                    System.err.println("Error attempting to receive file: " + ex.getMessage() + " " + ex.getCause());
                }
            }
        };
        sendLoopThread = new Thread(sendLoopRunnable);
        sendLoopThread.setName("Send Loop Thread");

        Runnable receiveLoopRunnable = new Runnable() {
            @Override
            public void run() {
                try
                {
                    while (true)
                    {
                        // Receive file
                        receiveFile();

                        // Do a little sleep
                        Thread.sleep(250);
                    }
                }
                catch (InterruptedException ex)
                {

                }
                catch (Exception ex)
                {
                    System.err.println("Error attempting to receive file: " + ex.getCause());
                    ex.printStackTrace();
                }
            }
        };
        receiveLoopThread = new Thread(receiveLoopRunnable);
        receiveLoopThread.setName("Receive Loop Thread");

        return true;
    }
    public boolean startThreads() {
        sendLoopThread.start();
        receiveLoopThread.start();
        return true;
    }
    public boolean stopThreads() {
        sendLoopThread.interrupt();
        receiveLoopThread.interrupt();
        return true;
    }
    public void shutDownServer() {
        try
        {
            serverSocket.close();
            serverSocket = null;

            stopThreads();
        }
        catch (Exception ex)
        {
            System.err.println("Error shutting server down: " + ex.getMessage() + " " + ex.getCause());
//            ex.printStackTrace();
        }
    }
    private void getSocket() throws IOException {
        System.out.println("Waiting for client to accept server socket.");

        // Wait for client to accept socket
        if (serverSocket != null) {
            socket = serverSocket.accept();
        }
        else
        {
            System.err.println("ServerSocket is null, still attempting to get socket from server.");
        }

        System.out.println("\nClient accepted server socket.");

        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }
    public static boolean testerMethod() {
        return true;
    }

    // Send Methods
    public void sendFile(File file) throws IOException {

        if (file == null)
            return;

        sendingFile = true;
        System.out.println("Sending file: " + file.getName());

        // Get file contents as byte array
        byte[] fileContent = Files.readAllBytes(file.toPath());

        // Write file name
        System.out.println("Sending file name...");
        out.writeUTF(file.getName());

        // Write length of the file
        System.out.println("Sending file length...");
        out.writeInt(fileContent.length);

        // Write file contents
        System.out.println("Sending file contents...");
        out.write(fileContent);

        // Flush buffers
        out.flush();

        System.out.println("File "+file.getName()+" sent successfully.");
        sendingFile = false;
    }

    // Receive Methods
    public void receiveFile() {
        if (socket == null || !socket.isConnected()) {
            return;
        }

        receivingFile = true;

        try
        {
            System.out.println("Waiting to receive file...");

            // Get name of incoming file
            String fileName = in.readUTF();
            System.out.println("File: " + fileName);

            // Get length of incoming file
            int length = in.readInt();
            System.out.println("Length: " + length);

            // Get contents of file
            if (length > 0)
            {
                // read the message
                byte[] fileContent = new byte[length];
                in.readFully(fileContent, 0, fileContent.length);

                // Write bytes to file
                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write(fileContent);

                // Close file output
                fos.close();
            }

            // Add to outgoing table
            addCompletedRecordToIncoming(fileName);
        }
        catch (SocketException e) {
            System.err.println("Connection lost! " + e.getLocalizedMessage());

            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    controller.stopServer();

                }
            });
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}

        receivingFile = false;
    }
    private void addCompletedRecordToIncoming(String fileName) {
        // Create File transfer for table
        File receivedFile = new File(fileName);
        FileTransfer newTransfer = new FileTransfer(receivedFile);
        newTransfer.status = "Completed";
        newTransfer.progress = 100.0;
        controller.filesIncoming.add(newTransfer);
    }
}
