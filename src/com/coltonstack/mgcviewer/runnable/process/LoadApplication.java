/**
* LoadApplication.java - singleton
*   LoadApplication retrieve the location of mgc.txt file from mgcMetadata.txt
*   If the location does not exist or is not invalid anymore, pop up
*   fileChooser to get right location of mgc.txt file
*
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.runnable.process;

import com.coltonstack.mgcviewer.window.OpenFileChooser;
import com.coltonstack.mgcviewer.recruit.RecruitDatabase;
import java.io.*;

public class LoadApplication implements ProcessData {
    
    // It is a memory-loss to have multiple operation objects, so LoadApplication class is a singleton
    private static LoadApplication loadApplication;
    
    private LoadApplication() {}
    
    // Get singleton object of LoadApplication
    public static LoadApplication getInstance() {
        if (loadApplication == null)
            loadApplication = new LoadApplication();
        return loadApplication;
    }
    
    @Override
    public void run(String... input) {
        File file = new File(input[0]);
        String line;
        
        try {
            // if mgcMetadata.txt DNE
            if (!file.exists()) {
                OpenFileChooser.apndMgcPath(file);
            } else {
                // 1. get mgc.txt location
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    
                    // 2-1. if mgc.txt exists
                    if ((line = br.readLine()) != null) {
                        if (new File(line).exists())
                            RecruitDatabase.setMgcTxtFilePath(line);
                        else
                            OpenFileChooser.apndMgcPath(file);
                    }
                    // 2-2. if mgc.txt DNE: Open File Chooser, Save mgc.txt to mgcMetadata.txt
                    else 
                        OpenFileChooser.apndMgcPath(file);
                }
            }
        } catch (IOException ex) { /* handle exception */ }
    }
}