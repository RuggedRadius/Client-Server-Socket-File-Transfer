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
    // Attributes
    public ObservableList<FileTransfer> filesIncoming;
    public ObservableList<FileTransfer> filesOutgoing;
    public Server server;

    // FX Controls
    public TableView tblIncoming;
    public TableView tblOutgoing;
    public Label lblStatus;
    public TextField txtPortNumber;
    public Button btnStartServer;
    public Button btnStopServer;


    // General methods
    public boolean initialize() {
        initIncomingTable();
        initOutgoingTable();

        lblStatus.setText("Server not running.");
        return true;
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
    @FXML public boolean startServer() {
        try
        {
            // Create server
            int port = Integer.parseInt(txtPortNumber.getText());
            server = new Server(port, this);

            // Launch server
            lblStatus.setText("Launching server...");
            server.initServer();
            server.startThreads();
            lblStatus.setText("Sever running");

            btnStopServer.setDisable(false);
            btnStartServer.setDisable(true);

            return true;
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(
                    null,
                    "Can't launch server; " + ex.getMessage(),
                    "Error launching Server",
                    JOptionPane.ERROR_MESSAGE
            );
            System.err.println(ex.getMessage());
            ex.printStackTrace();

            return false;
        }
    }
    @FXML public void stopServer() {
        if (server != null)
        {
            // Shut down server
            server.shutDownServer();

            System.out.println("Server stopped.");
            lblStatus.setText("Server not running.");

            // Toggle buttons
            btnStopServer.setDisable(true);
            btnStartServer.setDisable(false);
        }
        else
        {
            // Show error
            System.err.println("Error: Cant shut down server; server is null.");
            JOptionPane.showMessageDialog(
                    null,
                    "Can't shut down server; server is null.",
                    "Error shutting down Server",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // File handling
    public File getLocalFile() {

        FileChooser mrChoosey = new FileChooser();
        return  mrChoosey.showOpenDialog(null);
    }
    public List<File> getLocalFiles() {
        FileChooser mrChoosey = new FileChooser();
        return  mrChoosey.showOpenMultipleDialog(null);
    }

    // User actions
    @FXML public void sendSingleFile() {
        File fileToSend = getLocalFile();
        if (fileToSend != null) {
            sendFile(fileToSend);
        }
    }
    @FXML public void sendMultipleFiles() {
        List<File> filesToSend = getLocalFiles();
        if (filesToSend != null && filesToSend.size() > 0) {
            for (File file : filesToSend)
                sendFile(file);
        }
    }
    public void sendFile(File file) {

        FileTransfer newTransfer = null;

        try
        {
            // Create File transfer for table
            newTransfer = new FileTransfer(file);
            newTransfer.status = "Completed";
            newTransfer.progress = 100.0;
            filesOutgoing.add(newTransfer);


            // Add to outgoing queue for processing
            server.outgoingFiles.add(file);

            // Log to user
            System.out.println("File " + file.getName() + " added to outgoing queue.");
        }
        catch (Exception ex)
        {
            if (newTransfer != null)
            {
                newTransfer.status = "Failed";
                newTransfer.progress = 0.0;
            }

            System.out.println("Error: No server found!");
            JOptionPane.showMessageDialog(
                    null,
                    "No server found.",
                    "Server Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }



}
