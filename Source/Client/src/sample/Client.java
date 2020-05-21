package sample;

import javafx.scene.control.Control;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Queue;

/*

 * @startuml

 * car --|> wheel

 * @enduml

 */

public class Client
{
    // Attributes
    public Controller controller;
    public Socket socket;
    public DataOutputStream out;
    public DataInputStream in;
    public String host;
    public int currentPort;

    Queue<File> outgoingFiles;

    boolean receivingFile;
    boolean sendingFile;
    public boolean running;

    // Constructor
    public Client(String _host, int port, Controller _controller) {

        // Assign attributes
        controller = _controller;
        currentPort = port;
        host = _host;

        // Init list
        outgoingFiles = new LinkedList<>();

        // Make sure socket is assigned
        while (socket == null)
        {
            try
            {
                socket = new Socket(host, currentPort);

            }
            catch (IOException e)
            {
                System.err.println("Couldn't create socket, ensure server is running.");
                System.err.println("Retrying in 1s...");

                try
                {
                    Thread.sleep(1000);
                }
                catch (Exception ex) {}
            }
        }

        try
        {
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("Error creating output stream from socket.");
        }
    }


    public void startClient() {
        running = true;
        launchSendingLoop();
        launchReceivingLoop();
    }

    // Connection Methods
    public void endConnection() {
        // Close connection
        try
        {
            running = false;
            out.close();
            in.close();
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // Send Methods
    public void launchSendingLoop() {
        System.out.println("Launching client sending loop...");
        Runnable sendLoopRunnable = new Runnable() {
            @Override
            public void run() {
                try
                {
                    while (running)
                    {
                        // send data to the client (if any)
                        if (outgoingFiles.size() > 0) {

                            File fileToSend = outgoingFiles.element();

                            if (fileToSend != null) {
                                System.out.println("Sending file: " + fileToSend.getName());
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
                    System.err.println("Error attempting to receive file: " + ex.getCause());
                    ex.printStackTrace();
                }
            }
        };
        Thread sendLoop = new Thread(sendLoopRunnable);
        sendLoop.setName("Send Loop Thread");
        sendLoop.start();
    }
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
    public void launchReceivingLoop() {
        System.out.println("Launching client receiver loop...");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (running)
                {
                    receiveFile();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void receiveFile() {

        try
        {
            if (socket == null) {
                System.out.println("No socket found.");
                return;
            }

            receivingFile = true;
            System.out.println("Waiting to receive file...");

            // Get name and length of incoming file
            String fileName = in.readUTF();
            System.out.println("File: " + fileName);

            int length = in.readInt();
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
            else {
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
        catch (SocketException e)
        {
            System.err.println("Connection to host lost!");
            System.out.println("Leaving server...");
            controller.leaveServer();
        }
        catch (Exception ex)
        {
            System.out.println("Server error: " + ex.getMessage());
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
