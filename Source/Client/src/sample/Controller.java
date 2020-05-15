package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

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
        StringBuilder fieldContent = new StringBuilder(txtLog.getText());
        fieldContent.append(message+"\n");
        txtLog.setText(fieldContent.toString());
        System.out.println(message);
    }

    private void initClient() {

        // Create client
        String host = "127.0.0.1";
        int port = 1235;
        log("Initialising client...");
        client = new Client(host, port, this);



        log("Launching client receiver loop...");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    receiveFile();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();








    }

    private void receiveFile()
    {
        try (Socket socket = new Socket(client.host, client.currentPort))
        {
            log("Socket opened");
            client.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            log("Receiving file...");
            client.receiveFile();

            System.out.println("Client finished");
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Server not found: " + ex.getMessage());
        }
        catch (IOException ex)
        {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

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
