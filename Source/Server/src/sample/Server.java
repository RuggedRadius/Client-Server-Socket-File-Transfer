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




        // TO DO fancy 'waiting for' screen here
        // ...





        // Get socket from client
        socket = serverSocket.accept();

        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void sendFile(File file) throws IOException
    {
        sendingFile = true;
        controller.log("Sending file: " + file.getName());

        // Create File transfer for table
        FileTransfer newTransfer = new FileTransfer(file);
        newTransfer.status = "Sending";
        controller.filesOutgoing.add(newTransfer);

        // Accept connection
        if (socket == null)
        {
            socket = serverSocket.accept();
        }

        // Get file contents as byte array
        byte[] fileContent = Files.readAllBytes(file.toPath());

        // Write file name
        out.writeUTF(file.getName());

        // Write length of the file
        out.writeInt(fileContent.length);

        // Write file contents
        out.write(fileContent);

        newTransfer.progress = 100.0;
        newTransfer.status = "Complete";

        controller.log("File sent successfully.");
        sendingFile = false;
    }

//    public void receiveFile() throws IOException {
//
//        if (!receivingFile)
//        {
//            // Begin receiving file
//            receivingFile = true;
//
//            // Accept connection
//            socket = serverSocket.accept();
//            controller.log("Awaiting incoming file...");
//
//
//
//
//            // ** FILE SENT HERE **
//
//
//
//
//
//            // Get input stream
//            in = new DataInputStream(socket.getInputStream());
//
//            // Get name of incoming file
//            String fileName = in.readUTF();
//            controller.log("File: " + fileName);
//
//            // Get length of incoming file
//            int length = in.readInt();
//            controller.log("Length: " + length);
//
//            // Read file contents
//            if (length > 0)
//            {
//                // read the message
//                byte[] fileContent = new byte[length];
//                in.readFully(fileContent, 0, fileContent.length);
//
//                // Write bytes to file
//                FileOutputStream fos = new FileOutputStream(fileName);
//                fos.write(fileContent);
//            }
//            else
//                {
//                JOptionPane.showMessageDialog(
//                        null,
//                        "File contents had a length of 0.",
//                        "Suspect file transfer error",
//                        JOptionPane.WARNING_MESSAGE
//                );
//            }
//
//            // Create File transfer for table
//            File receivedFile = new File(fileName);
//            FileTransfer newTransfer = new FileTransfer(receivedFile);
//            newTransfer.status = "Completed";
//            controller.filesIncoming.add(newTransfer);
//
//            // Complete receiving file
//            controller.log("File received successfully.");
//            receivingFile = false;
//        }
//    }
}
