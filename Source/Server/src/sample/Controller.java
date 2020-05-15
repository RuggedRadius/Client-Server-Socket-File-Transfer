package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;

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
    public TextArea txtLog;


    public void initialize() {

        log("Initialising server...");

        initIncomingTable();
        initOutgoingTable();
        initServer();
    }

    public void log(String message) {
//        // Log message to text area
//        StringBuilder fieldContent = new StringBuilder(txtLog.getText());
//        fieldContent.append(message+"\n");
//        txtLog.setText(fieldContent.toString());
//        lblStatus.setText(message);
        System.out.println(message);
    }

    private void initServer() {
        try
        {
            // Create server
            int port = 1235;
            server = new Server(port, this);

            // Launch server
            server.launchServer();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
//    private void createListeningLoop()
//    {
//        System.out.println("Creating listening loop...");
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                while (true)
//                {
//                    try
//                    {
//                        server.receiveFile();
//                        Thread.sleep(100);
//                    }
//                    catch (IOException | InterruptedException e)
//                    {
//                        System.err.println("Error attempting to receiving");
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        Thread thread = new Thread(runnable);
//        thread.start();
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


    @FXML
    public void TESTSEND()
    {
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

        if (server != null)
        {
            // Create File transfer for table
            FileTransfer newTransfer = new FileTransfer(file);
            newTransfer.status = "Pending";
            newTransfer.progress = 0.0;
            filesOutgoing.add(newTransfer);

            // Add to outgoing queue for processing
            server.outgoingFiles.add(file);

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

}
