/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bunzipper;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.UnzipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;

/**
 *
 * @author Jared
 */
public class UnzipperRunnable implements Runnable
{
    private String sourceFile;
    private String destination;
    private ZipFile file;
    private UnzipperProgress progressHandler;
    
    private void notify(String status, float percentDone, String fileName, Boolean newFile)
    {
        Platform.runLater(() -> 
        {
            progressHandler.progress(status, percentDone, fileName, newFile);
        });
    }
    
    public List getFilesInZip()
    {
        List headers = null;
        
        try
        {
            headers = file.getFileHeaders();
        }
        catch (ZipException ex)
        {
            System.err.println("Couldn't load file headers");
        }
        
        return headers;
    }
    
    public UnzipperRunnable(String sourceFile, String destination, UnzipperProgress progressHandler)
    {
        this.sourceFile = sourceFile;
        this.destination = destination;
        this.progressHandler = progressHandler;
        
        try
        {
           file = new ZipFile(this.sourceFile);
        }
        catch(ZipException ex)
        {
            System.err.println("Failed to open file: " + ex.getMessage());
        }
        
        this.notify("Not Started", 0, "", false);
    }
    
    @Override
    public void run()
    {
        ArrayList<FileHeader> headers;
        
        try
        {
            headers = new ArrayList(file.getFileHeaders());
        }
        catch (ZipException ex)
        {
            System.err.println("Failed to get File Headers");
            return;
        }
        
        int numFiles = headers.size();
        
        for (int fileIndex = 0; fileIndex < numFiles; ++fileIndex)
        {
            float percentDone = (float)fileIndex / numFiles;
            
            //notify UI of updates
            this.notify("Extracting", percentDone, headers.get(fileIndex).getFileName(), true);

            try
            {
                file.extractFile(headers.get(fileIndex), destination);
            }
            catch (ZipException ex)
            {
                System.err.println("Failed to extract files from zip: " + ex.getMessage());
            }
            
            //Check if interrupted and cancel tasks if so
            if (Thread.interrupted())
            {
                this.notify("Interrupted", percentDone, "", false);
                return;
            }
        }
        
        //completed
        this.notify("Finished", 1, "", false);
    }
}
