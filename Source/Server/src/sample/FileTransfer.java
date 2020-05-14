package sample;

import java.io.File;

public class FileTransfer
{


    public File file;
    public String fileName;
    public double progress;
    public String status;

    public FileTransfer(File _file)
    {
        file = _file;
        fileName = file.getName();
        progress = 0.0;
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
