package sample;

import javafx.collections.FXCollections;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
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


    // Constructor
    public Server(int port, Controller _controller) throws IOException {
        // Assign controller
        controller = _controller;

        // Determine socket and port
        currentPort = port;
        serverSocket = new ServerSocket(currentPort);

        outgoingFiles = new LinkedList<>();
    }

    private void getSocket() throws IOException {
        System.out.println("Waiting for client to accept server socket.");

//        Thread waitThread = launchClientWaitThread();

        // Wait for client to accept socket
        socket = serverSocket.accept();

        System.out.println("\nClient accepted server socket.");

        // Stop waiting thread
//        waitThread.interrupt();

        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
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
                        if (socket == null) {
                            getSocket();
                        }

                        // send data to the client (if any)
                        if (outgoingFiles.size() > 0) {

                            File fileToSend = outgoingFiles.poll();

                            if (fileToSend != null) {
                                System.out.println("Sending file: " + fileToSend.getName());
                                sendFile(fileToSend);
                            }
                        }

                        // Do a little sleep
                        Thread.sleep(250);
                    }
                }
                catch (Exception ex)
                {
                    System.out.println("Error attempting to receive file: " + ex.getCause());
                    System.err.println("Error attempting to receive file: " + ex.getCause());
                    ex.printStackTrace();
                }
            }
        };
        Thread sendLoop = new Thread(sendLoopRunnable);
        sendLoop.setName("Send Loop Thread");
        sendLoop.start();

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
                catch (Exception ex)
                {
                    System.out.println("Error attempting to receive file: " + ex.getCause());
                    System.err.println("Error attempting to receive file: " + ex.getCause());
                    ex.printStackTrace();
                }
            }
        };
        Thread receiveLoopThread = new Thread(receiveLoopRunnable);
        receiveLoopThread.setName("Receive Loop Thread");
        receiveLoopThread.start();
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
        // Write length of the file
        // Write file contents
        out.writeUTF(file.getName());
        out.writeInt(fileContent.length);
        out.write(fileContent);

        System.out.println("File "+file.getName()+" sent successfully.");
        sendingFile = false;
    }

    // Receive Methods
    public void receiveFile() {
        receivingFile = true;
        if (socket == null) {
            receivingFile = false;
            return;
        }

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
}
