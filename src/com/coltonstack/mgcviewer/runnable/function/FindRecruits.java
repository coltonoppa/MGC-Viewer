/**
* FindRecruits.java
*   Based on input given in textfields of mgcViewer, FindRecruits class
*   figure out the input if it means either number, birthdate, name or ps
*   and then it returns List of Recruits.
* 
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.runnable.function;

import com.coltonstack.mgcviewer.recruit.*;
import java.util.*;

public class FindRecruits implements FunctionData {
    
    private static FindRecruits findRecruits;
    
    // It is a memory-loss to have multiple operation objects, so FindRecruits class is a singleton
    private FindRecruits() {}
    
    // Get singleton object of FindRecruits
    public static FindRecruits getInstance() {
        if (findRecruits == null)
            findRecruits = new FindRecruits();
        return findRecruits;
    }
    
    @Override
    public List<Recruit> run(String... input) {
        String in = input[0];
        int inputType;
        
        if (in.matches("[1-9][0-9]{5}"))
            inputType = 0;  // return 0 if input is birthdate
        else if (in.matches("[0-9]{1,4}"))
            inputType = 1;  // return 1 if input is number
        else if (in.matches("[1-5]{1}[p]{1}[1-2]{0,1}[0-9]{1}[s]{1}"))   
            inputType = 2;  // return 2 if input is p/s
        else
            inputType = 9;  // return 9 if input is name
        
        RecruitDatabase recruitDB = RecruitDatabase.getInstance();
        List<Recruit> resultRecruits = new ArrayList<>();
        
        // Retrieve List of Recruits based on type of input
        switch(inputType) {
            case 0:
                resultRecruits = recruitDB.getRecruitsByDob(in);
                break;
            case 1:
                resultRecruits.add(recruitDB.getRecruitByNumber(Integer.parseInt(in)));
                break;
            case 2:
                resultRecruits = recruitDB.getRecruitsByPS(in);
                break;
            case 9:
                resultRecruits = recruitDB.getRecruitsByName(in);
                break;
        }
        return resultRecruits;
    }
}
