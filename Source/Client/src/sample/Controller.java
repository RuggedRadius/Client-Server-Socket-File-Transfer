package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class Controller
{
    public TextArea txtLog;

    public TableView tblIncoming;
    public TableView tblOutgoing;

    public ObservableList<FileTransfer> filesIncoming;
    public ObservableList<FileTransfer> filesOutgoing;

    private Client client;

    public void initialize() {

        log("Initialising client...");

        initIncomingTable();
        initOutgoingTable();

        initClient();
    }

    public void log(String message) {
        StringBuilder fieldContent = new StringBuilder(txtLog.getText());
        fieldContent.append(message+"\n");
        txtLog.setText(fieldContent.toString());
    }

    private void initClient() {
        try
        {
            // Create client
            String host = "127.0.0.1";
            int port = 1235;
            client = new Client(host, port);
            client.controller = this;

            // Create listening loop on separate thread
            System.out.println("Starting receiving loop...");
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    while (true)
                    {
                        try
                        {
                            if (!client.receivingFile) {
                                client.receiveFile();
                            }
                            Thread.sleep(100);
                        }
                        catch (IOException | InterruptedException e)
                        {
                            System.err.println("Error attempting to receive file");
                            e.printStackTrace();
                        }
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
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
