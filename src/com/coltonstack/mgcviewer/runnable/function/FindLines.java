/**
* FindLines.java
*   Unlike FindRecruits class, FindLines retrives all lines in 
*   mgc.txt fileiff they contain user-typed input and then returns
*   List of String, which are the matching lines.
* 
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.runnable.function;

import com.coltonstack.mgcviewer.recruit.*;
import java.io.*;
import java.util.*;

public class FindLines implements FunctionData {
    
    private static FindLines findLines;
    
    // It is a memory-loss to have multiple operation objects, so FindLines class is a singleton
    private FindLines() {}
    
    // Get singleton object of FindLines
    public static FindLines getInstance() {
        if (findLines == null)
            findLines = new FindLines();
        return findLines;
    }
    
    @Override
    public List<String> run(String... input) {
        List<String> resultStr = new ArrayList<>();
    
        File file = RecruitDatabase.mgcTxtFilePath;
        String tempStr;
        String in = input[0];
        
        // Find matching string lines in mgc.txt
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((tempStr = br.readLine()) != null) {
                if (tempStr.contains(in))
                    resultStr.add(tempStr);
            }
        } catch (IOException e) { /* Handle exception */ }
        
        return resultStr;
    }
}
