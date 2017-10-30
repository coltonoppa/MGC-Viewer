/**
* OpenFileChooser.java
*   OpenFileChooser class opens JFileChooser window that saves the
*   location of mgc text file to mgcMetadata.txt.
*
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.window;

import com.coltonstack.mgcviewer.recruit.RecruitDatabase;
import java.io.*;
import javax.swing.JFileChooser;

public class OpenFileChooser {
    
    private static JFileChooser fileChsr;
    
    // Append path of mgc.txt to mgcMetadata.txt using FileChooser
    public static boolean apndMgcPath(File file) {
        String fileLocation = OpenFileChooser.pop();
        
        if (fileLocation != null) {
            RecruitDatabase.setMgcTxtFilePath(fileLocation);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.append(RecruitDatabase.getMgcTxtFilePath());
            } catch (IOException ex) { /* handle exception */ }
            return true;
        } else
            return false;
    }
    
    // Open up FileChooser to retrieve target file
    public static String pop() {
        String fileLctn = null;
        
        if (fileChsr == null)
            fileChsr = new JFileChooser();
        int result = fileChsr.showOpenDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION)
            fileLctn = fileChsr.getSelectedFile().getAbsolutePath();
        
        return fileLctn; 
    }
}
