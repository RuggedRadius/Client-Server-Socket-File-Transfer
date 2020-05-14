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
import java.sql.ResultSet;

public class Controller
{
    public TextArea txtLog;

    // FX Controls
    public TableView tblIncoming;
    public TableView tblOutgoing;
    public Label lblStatus;

    public ObservableList<FileTransfer> filesIncoming;
    public ObservableList<FileTransfer> filesOutgoing;

    public Server server;

    // Attributes
    public void initialize() {

        log("Initialising server...");

        initIncomingTable();
        initOutgoingTable();
        initServer();




    }

    public void log(String message) {
        // Log message to text area
        StringBuilder fieldContent = new StringBuilder(txtLog.getText());
        fieldContent.append(message+"\n");
        txtLog.setText(fieldContent.toString());
        lblStatus.setText(message);
    }

    private void initServer() {
        try
        {
            // Create server
            int port = 1235;
            server = new Server(port, this);

            // Create listening loop on separate thread
//            createListeningLoop();
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


    public void TESTSEND()
    {
        File test = new File("C:\\Users\\Ben\\Documents\\code-rain.jpg");
        sendFile(test);
    }

    @FXML
    public void sendSingleFile()
    {
        TESTSEND();
//        sendFile(getLocalFile());
    }
    public File getLocalFile() {

        FileChooser mrChoosey = new FileChooser();
        return  mrChoosey.showOpenDialog(null);
    }
    public void sendFile(File file) {

        if (server != null)
        {
            try
            {
                // Send the file
                server.sendFile(file);
            }
            catch (IOException e)
            {
                // Show error
                JOptionPane.showMessageDialog(
                        null,
                        "An error occured while sending the file.",
                        "Error sending file.",
                        JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        }
        else
        {
            JOptionPane.showMessageDialog(
                    null,
                    "No server found.",
                    "Server Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
