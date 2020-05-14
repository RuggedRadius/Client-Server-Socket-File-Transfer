package sample;

import javafx.scene.control.Control;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Queue;

public class Client
{
    public Controller controller;
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    String host = "127.0.0.1";
    int currentPort;

    Queue<File> outgoingFiles;

    boolean receivingFile;
    boolean sendingFile;
    boolean initialised;


    public Client(String _host, int port) throws IOException, InterruptedException {
        // Determine IP address, socket and port
        currentPort = port;
        host = _host;

        while (!initialised)
        {


            // Set up waiting display frame
            Pane waitingPane = new Pane();



            try
            {
                socket = new Socket(host, currentPort);
            }
            catch (Exception ex)
            {
                Thread.sleep(100);
                System.out.print(".");
            }
        }



        // Assign streams for comms
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    public void endConnection() throws IOException {

        // Close connection
        out.close();
        in.close();
        socket.close();
    }

    public void receiveFile() throws IOException {

        if (!receivingFile)
        {
            receivingFile = true;
            controller.log("Awaiting incoming file...");

            // Get name of incoming file
            String fileName = in.readUTF();
            controller.log("File: " + fileName);

            // Get length of incoming file
            int length = in.readInt();
            controller.log("Length: " + length);

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

            // Create File transfer for table
            File receivedFile = new File(fileName);
            FileTransfer newTransfer = new FileTransfer(receivedFile);
            newTransfer.status = "Completed";
            newTransfer.progress = 100.0;
            controller.filesIncoming.add(newTransfer);

            controller.log("File received successfully.");
            receivingFile = false;
        }
    }
}
