/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bunzipper;

import java.util.List;
import javafx.application.Platform;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
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
    
    private void notify(String status, float percentDone)
    {
        Platform.runLater(() -> 
        {
            progressHandler.progress(status, percentDone);
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
        
        this.notify("Not Started", 0);
        
        file.setRunInThread(true);
    }
    
    @Override
    public void run()
    {
        try
        {
            file.extractAll(destination);
        }
        catch (ZipException ex)
        {
            System.err.println("Failed to extract files from zip: " + ex.getMessage());
        }
        
        ProgressMonitor mon = file.getProgressMonitor();
        
        while (mon.getState() == ProgressMonitor.STATE_BUSY)
        {
            //notify UI of updates
            this.notify("Extracting", mon.getPercentDone());
            
            //sleep as to not over notify
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException ex)
            {
                //stop
                this.notify("Interrupted", mon.getPercentDone());
                mon.cancelAllTasks();
                return; //get out of loop
            }
        }
        
        //completed
        this.notify("Finished", 100);
    }
}
