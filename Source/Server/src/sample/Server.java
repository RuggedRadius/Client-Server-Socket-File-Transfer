package sample;

import javafx.collections.FXCollections;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;

public class Server
{
    public Controller controller;

    public static int currentPort;

    ServerSocket serverSocket = null;
    Socket socket;

    DataOutputStream out;
    DataInputStream in;

//    Queue<File> outgoingFiles;
    ArrayDeque<File> outgoingFiles;

    boolean receivingFile;
    boolean sendingFile;

    Thread receiveLoopThread;
    Thread sendLoopThread;




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

    // Send Methods
    public void sendFile(File file) throws IOException {

        if (file == null) {
            System.err.println("Sending error: File was null.");
            return;
        }

        sendingFile = true;
        System.out.println("Sending file: " + file.getName());

        // Get file contents as byte array
        byte[] fileContent = Files.readAllBytes(file.toPath());

        // Write file name
        // Write length of the file
        // Write file contents
        System.out.println("Sending file name...");
        out.writeUTF(file.getName());
        System.out.println("Sending file length...");
        out.writeInt(fileContent.length);
        System.out.println("Sending file contents...");
        out.write(fileContent);

        System.out.println("File "+file.getName()+" sent successfully.");
        sendingFile = false;
    }

    // Receive Methods
    public void receiveFile() {
        if (socket == null) {
            return;
        }

        receivingFile = true;

        try {
            System.out.println("Waiting to receive file...");
            // Get name of incoming file
            // Get length of incoming file
            String fileName = in.readUTF();
            int length = in.readInt();

            System.out.println("File: " + fileName);
            System.out.println("Length: " + length);

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
            else
            {
                JOptionPane.showMessageDialog(
                        null,
                        "File contents had a length of 0.",
                        "Suspect file transfer error",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            // Add to outgoing table
            addCompletedRecordToIncoming(fileName);
        }
        catch (Exception ex)
        {
//            System.out.println("Server error: " + ex.getMessage());
        }
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

    public void launchServer() {

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
        sendLoopThread.start();

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
        receiveLoopThread.start();
    }

    public void shutDownServer() {
        try
        {
            serverSocket.close();
            serverSocket = null;

            sendLoopThread.interrupt();
            receiveLoopThread.interrupt();



//            in.close();
//            out.close();

//            if (socket != null)
//                socket.close();

        }
        catch (Exception ex)
        {
            System.err.println("Error shutting server down: " + ex.getMessage() + " " + ex.getCause());
//            ex.printStackTrace();
        }
    }
}
