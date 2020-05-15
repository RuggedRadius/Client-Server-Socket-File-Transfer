package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.File;

public class Controller
{
    public TextArea txtLog;

    public TableView tblIncoming;
    public TableView tblOutgoing;

    public ObservableList<FileTransfer> filesIncoming;
    public ObservableList<FileTransfer> filesOutgoing;

    private Client client;

    public void initialize() {

//        log("Initialising client...");

        initIncomingTable();
        initOutgoingTable();

        initClient();
    }

    public void log(String message) {
//        StringBuilder fieldContent = new StringBuilder(txtLog.getText());
//        fieldContent.append(message+"\n");
//        txtLog.setText(fieldContent.toString());
        System.out.println(message);
    }

    @FXML
    public void TESTSEND() {
        File test = new File("C:\\Users\\Ben\\Documents\\code-rain.jpg");
        sendFile(test);
    }
    @FXML
    public void sendSingleFile() {
//        TESTSEND();
        sendFile(getLocalFile());
    }
    public File getLocalFile() {

        FileChooser mrChoosey = new FileChooser();
        return  mrChoosey.showOpenDialog(null);
    }
    public void sendFile(File file) {

        if (client != null)
        {
            // Create File transfer for table
            FileTransfer newTransfer = new FileTransfer(file);
            newTransfer.status = "Pending";
            newTransfer.progress = 0.0;
            filesOutgoing.add(newTransfer);

            // Add to outgoing queue for processing
            client.outgoingFiles.add(file);

            // Log to user
            log("File " + file.getName() + " added to outgoing queue.");
        }
        else
        {
            System.out.println("Error: No server found!");
            JOptionPane.showMessageDialog(
                    null,
                    "No server found.",
                    "Server Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void initClient() {

        // Create client
        String host = "127.0.0.1";
        int port = 1235;
        log("Initialising client...");
        client = new Client(host, port, this);

        client.startClient();
    }

//    private void receiveFile()
//    {
//        try (Socket socket = new Socket(client.host, client.currentPort))
//        {
//            log("Socket opened");
//            client.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
//
//            log("Receiving file...");
//            client.receiveFile();
//
//            System.out.println("Client finished");
//        }
//        catch (UnknownHostException ex)
//        {
//            System.out.println("Server not found: " + ex.getMessage());
//        }
//        catch (IOException ex)
//        {
//            System.out.println("I/O error: " + ex.getMessage());
//        }
//    }

    private void initIncomingTable() {

        // Set table data
        filesIncoming = FXCollections.observableArrayList();
        tblIncoming.setItems(filesIncoming);

        // Create columns
        TableColumn<String, FileTransfer> colFileName = new TableColumn("File Name");
        colFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));

        TableColumn<String, FileTransfer> colProgress = new TableColumn("Progress");
        colProgress.setCellValueFactory(new PropertyValueFactory<>("progress"));

        TableColumn<String, FileTransfer> colStatus = new TableColumn("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add column to TableView
        tblIncoming.getColumns().addAll(colFileName, colStatus, colProgress);
    }
    private void initOutgoingTable() {

        // Set table data
        filesOutgoing = FXCollections.observableArrayList();
        tblOutgoing.setItems(filesOutgoing);

        // Create columns
        TableColumn<String, FileTransfer> colFileName = new TableColumn("File Name");
        colFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));

        TableColumn<String, FileTransfer> colProgress = new TableColumn("Progress");
        colProgress.setCellValueFactory(new PropertyValueFactory<>("progress"));

        TableColumn<String, FileTransfer> colStatus = new TableColumn("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add column to TableView
        tblOutgoing.getColumns().addAll(colFileName, colStatus, colProgress);
    }

}
