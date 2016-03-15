/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bunzipper;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import net.lingala.zip4j.model.FileHeader;
import ui.UIScene;
import ui.UIStage;

/**
 *
 * @author Jared
 */
public class HomeController extends UIScene
{
    //customer variables
    Thread worker;
    
    @FXML
    private TextField pathToFile;
    
    @FXML
    private TextField pathToFolder;
    
    @FXML
    private Label status;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private ListView processedFileList;
    
    @FXML
    private void browseFileClicked(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Zipped File");
        File sel = fileChooser.showOpenDialog(this.getUIStage().getStage());
        
        if (sel == null)
        {
            return;
        }
        
        this.pathToFile.setText(sel.getAbsolutePath());
    }
    
    @FXML
    private void browseFolderClicked(ActionEvent event)
    {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select Destination Directory");
        File sel = dirChooser.showDialog(this.getUIStage().getStage());
        
        if (sel == null)
        {
            return;
        }
        
        this.pathToFolder.setText(sel.getPath());
    }
    
    @FXML
    private void startClicked(ActionEvent event)
    {
        if (worker == null || worker.isAlive() == false)
        {
            //start thread
            UnzipperRunnable runner = new UnzipperRunnable(pathToFile.getText(), pathToFolder.getText(), 
            (String message, float progress) -> 
                {
                    this.status.setText(message);
                    this.progressBar.setProgress(progress);
                });
            
            //add files
            List list = runner.getFilesInZip();
            ArrayList<String> al = new ArrayList<>();
            
            for (int i = 0; i < list.size(); ++i)
            {
                String filename = ((FileHeader)list.get(i)).getFileName();
                al.add(filename);
            }
            
            ObservableList oList = FXCollections.observableArrayList(al);

            this.processedFileList.setItems(oList);
            worker = new Thread(runner);
            worker.start();
        }
    }
    
    @FXML
    private void stopClicked(ActionEvent event)
    {
        if (worker != null && worker.isAlive())
            worker.interrupt();
    }
    
    public void onClose(WindowEvent we) 
    {
        if (worker != null && worker.isAlive())
            worker.interrupt();
    }
}
