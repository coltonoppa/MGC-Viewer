/**
* MGCViewer.java
*   MGCViewer application helps squad leaders in 102nd Replacement Battalion
*   by rapidly retrieving recruit information saved in mgc text file
*   and fastly writes patient belts into a text file.
*
* @author  coltonoppa
* @version 1.41
* @since   2017-04-03
*/
package com.coltonstack.mgcviewer;

import com.coltonstack.mgcviewer.runnable.process.InitDatabases;
import com.coltonstack.mgcviewer.runnable.process.LoadApplication;
import com.coltonstack.mgcviewer.window.MainWindow;
import java.io.File;

public class MGCViewer {
        
    private final String version = "1.41v";
    
    public void run() {
        // 1.Seek and Set path of mgc.txt in mgcMetadata.txt
        LoadApplication loadApp = LoadApplication.getInstance();
        loadApp.run("data" + File.separator + "mgcMetadata.txt");
        
        // 2.Load all data in mgc.txt into memory due to abscence of database
        InitDatabases initData = InitDatabases.getInstance();
        initData.run();
        
        // 3.Open mgcViewer window
        MainWindow mainWindow = MainWindow.getInstance();
        mainWindow.setTitle("MGC 뷰어 " + version);
        mainWindow.setVisible(true);
    }
}
