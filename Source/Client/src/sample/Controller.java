package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class Controller
{
    // FX Controls
    public Label lblStatus;
    public TextField txtIp1;
    public TextField txtIp2;
    public TextField txtIp3;
    public TextField txtIp4;
    public TextField txtPortNumber;
    public TableView tblIncoming;
    public TableView tblOutgoing;
    public Button btnJoinServer;
    public Button btnLeaveServer;

    // Attributes
    public ObservableList<FileTransfer> filesIncoming;
    public ObservableList<FileTransfer> filesOutgoing;
    private Client client;

    // General Methods
    public void initialize() {
        initIncomingTable();
        initOutgoingTable();
        lblStatus.setText("Client not connected.");
    }
    private String getIpAddress() {
        return txtIp1.getText() + "." + txtIp2.getText() + "." +txtIp3.getText() + "." +txtIp4.getText() + "";
    }
    private void initIncomingTable() {

        // Set table data
        filesIncoming = FXCollections.observableArrayList();
        tblIncoming.setItems(filesIncoming);

        // Create columns
        TableColumn<String, FileTransfer> colFileName = new TableColumn("File Name");
        colFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        colFileName.setPrefWidth(500);

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
        colFileName.setPrefWidth(250);

        TableColumn<String, FileTransfer> colProgress = new TableColumn("Progress");
        colProgress.setCellValueFactory(new PropertyValueFactory<>("progress"));

        TableColumn<String, FileTransfer> colStatus = new TableColumn("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add column to TableView
        tblOutgoing.getColumns().addAll(colFileName, colStatus, colProgress);
    }
    @FXML public void exitApplication() {
        System.exit(0);
    }
    @FXML public void joinServer() {
        // Create client
        int port = Integer.parseInt(txtPortNumber.getText());
        lblStatus.setText("Initialising client...");
        client = new Client(getIpAddress(), port, this);
        lblStatus.setText("Running client...");
        client.startClient();
        lblStatus.setText("Client running");

        btnJoinServer.setDisable(true);
        btnLeaveServer.setDisable(false);
    }
    @FXML public void leaveServer() {
        client.endConnection();

        btnJoinServer.setDisable(false);
        btnLeaveServer.setDisable(true);
    }

    // File Handling
    public File getLocalFile() {
        FileChooser mrChoosey = new FileChooser();
        return  mrChoosey.showOpenDialog(null);
    }
    public List<File> getLocalFiles() {
        FileChooser mrChoosey = new FileChooser();
        return  mrChoosey.showOpenMultipleDialog(null);
    }

    // User Actions
    public void sendFile(File file) {
        if (client != null)
        {
            // Create File transfer for table
            FileTransfer newTransfer = new FileTransfer(file);
            newTransfer.status = "Completed";
            newTransfer.progress = 100.0;
            filesOutgoing.add(newTransfer);

            // Add to outgoing queue for processing
            client.outgoingFiles.add(file);

            // Log to user
            lblStatus.setText("File " + file.getName() + " added to outgoing queue.");
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
    @FXML public void sendSingleFile() {
        File fileToSend = getLocalFile();
        if (fileToSend != null) {
            sendFile(fileToSend);
        }
    }
    @FXML public void sendMultipleFiles() {
        List<File> filesToSend = getLocalFiles();
        if (filesToSend != null && filesToSend.size() > 0) {
            for (File file : filesToSend) {
                sendFile(file);
            }
        }
    }
}
