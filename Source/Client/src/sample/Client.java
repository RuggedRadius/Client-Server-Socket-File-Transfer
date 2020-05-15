package sample;

import javafx.scene.control.Control;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;

public class Client
{
    public Controller controller;
    public Socket socket;
    public DataOutputStream out;
    public DataInputStream in;
    public String host;
    public int currentPort;

    Queue<File> outgoingFiles;

    boolean receivingFile;
    boolean sendingFile;
    boolean initialised;


    public Client(String _host, int port, Controller _controller)
    {
        controller = _controller;

        // Determine IP address, socket and port
        currentPort = port;
        host = _host;
    }

    public void endConnection() throws IOException {

        // Close connection
        out.close();
        in.close();
        socket.close();
    }

    public void receiveFile() throws IOException
    {
        receivingFile = true;
        controller.log("Awaiting incoming file...");

        try (Socket socket = new Socket(host, currentPort)) {

            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            // Get name of incoming file
            // Get length of incoming file
            String fileName = in.readUTF();
            int length = in.readInt();

            controller.log("File: " + fileName);
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

            // Add to outgoing table
            addCompletedRecordToIncoming(fileName);
        }
        catch (Exception ex)
        {
            System.out.println("Server error: " + ex.getMessage());
        }

        controller.log("File received successfully.");
        receivingFile = false;
    }

    private void addCompletedRecordToIncoming(String fileName)
    {
        // Create File transfer for table
        File receivedFile = new File(fileName);
        FileTransfer newTransfer = new FileTransfer(receivedFile);
        newTransfer.status = "Completed";
        newTransfer.progress = 100.0;
        controller.filesIncoming.add(newTransfer);
    }
}
