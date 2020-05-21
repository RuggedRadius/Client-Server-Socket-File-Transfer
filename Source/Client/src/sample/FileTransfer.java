package sample;

import java.io.File;

public class FileTransfer
{
    // Attributes
    public File file;
    public String fileName;
    public double progress;
    public String status;

    // Constructor
    public FileTransfer(File _file)
    {
        file = _file;
        fileName = file.getName();
        progress = 100.0;
        status = "Initiating";
    }

    // Getters
    public File getFile() {
        return file;
    }
    public String getFileName() {
        return fileName;
    }
    public double getProgress() {
        return progress;
    }
    public String getStatus() {
        return status;
    }
}
