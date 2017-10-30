/**
* PSInfo.java - singleton
*   Due to abscence of database driver, PSInfo is a memory-living class
*   that functions as database for platoon & squad information.
* 
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.psinfo;

import java.io.*;

public final class PSDatabase {
    
    public static final int NUM_P = 5;
    public static final int[] NUM_S = new int[]{10, 10, 15, 9, 8};
    public static final int TOT_S = 52;
    public static final int[] SEQ_P = new int[]{3, 4, 5, 1, 2};
    private static final File FILE = new File("data" + File.separator + "psinfo.txt");
    
    public static int[] eachPlatoonSize;
    private static int[] cummulativePSize;
    private static String psString;
    private boolean psDataSet;
    
    /* This arr contains the number of recruits in each squad */
    public static int[][] numRecruitsPS = new int[NUM_P][];
    
    private static PSDatabase psInfo = new PSDatabase();
    
    
    // private constructor - singleton
    private PSDatabase() {}
    
    // Get singleton instance of RecruitDatabase
    public synchronized static PSDatabase getInstance() {
        if (psInfo == null)
            psInfo = new PSDatabase();
        return psInfo;
    }
    
    // Fetch p/s data from psinfo.txt & Save the data into array of int, numRecruitsPS
    public void initPSData() {
        String tempStr;
        String tempStrArr[];
        int singlePlatoonSize;
        
        for (int i = 0; i < NUM_P; i++) 
            numRecruitsPS[i] = new int[NUM_S[i]];
        eachPlatoonSize = new int[NUM_P];
        cummulativePSize = new int[NUM_P];
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            int platoon = 0;
            while ((tempStr = br.readLine()) != null) {
                singlePlatoonSize = 0;
                tempStrArr = tempStr.split(" ");
                for (int i = 0; i < NUM_S[platoon]; i++){
                    numRecruitsPS[platoon][i] = Integer.parseInt(tempStrArr[i]);
                    singlePlatoonSize += numRecruitsPS[platoon][i];
                }
                eachPlatoonSize[platoon] = singlePlatoonSize;
                platoon++;                    
            }
        } catch (IOException e) { /* Handle exception */ }
        
        setPSString();
    }
    
    // Set each element of array numRecruitPS to 0
    public void clrPSData() {
        for (int i = 0; i < NUM_P; i++) {
            for (int j = 0; j < NUM_S[i]; j++){
                numRecruitsPS[i][j] = 0;
            }
        }
    }
    
    // Save array of p/s info into psinfo.txt
    public void savePSData() {
        String tempStr = "";
        
        try (BufferedWriter br = new BufferedWriter(new FileWriter(FILE))) {
            
            for (int i = 0; i < NUM_P; i++) {
                for (int j = 0; j < NUM_S[i]; j++){
                    tempStr += numRecruitsPS[i][j] + " ";
                }
                br.append(tempStr);
                tempStr = "";
                br.append(System.lineSeparator());
            }
        } catch (IOException e) { /* Handle exception */ }
    }
    
    // Get platoon by number
    public int getPlatoon(int number) {
        int platoon = 0;
        
        cummulativePSize[0] = eachPlatoonSize[0];
        for (int i = 1; i < cummulativePSize.length; i++) 
            cummulativePSize[i] = cummulativePSize[i-1] + eachPlatoonSize[i];
        
        if ((number > 0) && (number <= cummulativePSize[0]))
            platoon = 3;
        else if ((number > cummulativePSize[0]) && (number <= cummulativePSize[1]))
            platoon = 4;
        else if ((number > cummulativePSize[1]) && (number <= cummulativePSize[2]))
            platoon = 5;
        else if ((number > cummulativePSize[2]) && (number <= cummulativePSize[3]))
            platoon = 1;
        else if ((number > cummulativePSize[3]) && (number <= cummulativePSize[4]))
            platoon = 2;
        else
            platoon = -1;
        
        return platoon;
    }
    
    // Get index platoon based on actual platoon
    public static int platoonToIdx(int platoon) {
        switch(platoon) {
            case 3:     return 0;
            case 4:     return 1;
            case 5:     return 2;
            case 1:     return 3;
            case 2:     return 4;
            default:    return -1;
        }
    }
    
    // Get index squad based on actual squad
    public static int squadToIdx(int squad, int platoon) {
        int idx = squad - 1;
        switch (platoon) {
            case 1:     // 4p
                idx -= NUM_S[0];
                break;
            case 4:     // 2p
                idx -= NUM_S[3];
                break;
        }
        return idx;
    }
    
    // Get actual platoon based on index version of it
    public static int idxToPlatoon(int idx) {
        return SEQ_P[idx];
    }
    
    // Get actual squad based on index version of it
    public static int idxToSquad(int idx, int platoon) {
        int squad = idx + 1;
        switch (platoon) {
            case 4:
                squad += NUM_S[0];
                break;
            case 2:
                squad += NUM_S[3];
                break;
        }
        return squad;
    }
    
    // Get squad by recruit number
    public int getSquad(int number, int platoon) {
        int squad = 0;
        int tmp;
        
        switch(platoon) {
            case 3:
                tmp = 0;
                for (int i = 0; i < NUM_S[0]; i++) {
                    if ((number > tmp) && (number <= tmp + numRecruitsPS[0][i])) {
                        squad = i+1;
                        break;
                    }
                    tmp += numRecruitsPS[0][i];
                }
                break;
            case 4:
                tmp = cummulativePSize[0];
                for (int i = 0; i < NUM_S[1]; i++) {
                    if ((number > tmp) && (number <= tmp + numRecruitsPS[1][i])) {
                        squad = i+1+NUM_S[0];
                        break;
                    }
                    tmp += numRecruitsPS[1][i];
                }
                break;
            case 5:
                tmp = cummulativePSize[1];
                for (int i = 0; i < NUM_S[2]; i++) {
                    if ((number > tmp) && (number <= tmp + numRecruitsPS[2][i])) {
                        squad = i+1;
                        break;
                    }
                    tmp += numRecruitsPS[2][i];
                }
                break;
            case 1:
                tmp = cummulativePSize[2];
                for (int i = 0; i < NUM_S[3]; i++) {
                    if ((number > tmp) && (number <= tmp + numRecruitsPS[3][i])) {
                        squad = i+1;
                        break;
                    }
                    tmp += numRecruitsPS[3][i];
                }
                break;
            case 2:
                tmp = cummulativePSize[3];
                for (int i = 0; i < NUM_S[4]; i++) {
                    if ((number > tmp) && (number <= tmp + numRecruitsPS[4][i])) {
                        squad = i+1+NUM_S[3];
                        break;
                    }
                    tmp += numRecruitsPS[4][i];
                }
                break;
        }
        
        return squad;
    }
    
    // Get readable string version of p/s information
    public String getPSString() {
        return psString;
    }
    
    // Stringify p/s info so that the information is much readable to human
    public void setPSString() {
        String result = "";
        int toCompare;
        boolean tilda;
        int tmpSum;
        int totalSum = 0;
        
        for (int i = 0; i < NUM_P; i++) {
            result += SEQ_P[i] + "p: ";
            toCompare = numRecruitsPS[i][0];
            tmpSum = numRecruitsPS[i][0];
            totalSum += tmpSum;
            tilda = true;
            for (int j = 1; j < NUM_S[i]; j++) {
                if (toCompare != numRecruitsPS[i][j]) {
                    result += j + "s x " + numRecruitsPS[i][j-1] + "\n      ";
                    tilda = true;
                    toCompare = numRecruitsPS[i][j];
                    tmpSum += numRecruitsPS[i][j];
                } else  {
                    tmpSum += numRecruitsPS[i][j];
                    if (tilda) {
                        result += j + " ~ ";
                        tilda = false;
                    }
                }
                if (j == NUM_S[i]-1) 
                    result += (j+1) + "s x " + numRecruitsPS[i][j] + "= " + tmpSum + "\n";
                totalSum += numRecruitsPS[i][j];
            }
        }
        psString = result + "Total: " +totalSum + "\n\n";
        result = "";
        
        // Add number range for each platoon
        int recruitsPlatoon = 1;
        for (int i = 0; i < NUM_P; i++) {
            result += SEQ_P[i] + "p: " + recruitsPlatoon + " ~ ";
            for (int j = 0; j < NUM_S[i]; j++) {
                recruitsPlatoon += numRecruitsPS[i][j];
            }
            result += recruitsPlatoon-1 + "\n";
        }
        
        psString += result;
    }
    
    // Validate platoon & squad:
    // True if give platoon & squad indices are valid. False otherwise.
    public static boolean isPSValid(int platoon, int squad) {
        
        switch(platoon) {
            case 0:     // 3p: 0 ~ 9
                if ((squad >= 0) && (squad < NUM_S[0]))
                    return true;
                break;
            case 1:     // 4p: 0 ~ 9
                if ((squad >= 0) && (squad < NUM_S[1]))
                    return true;
                break;
            case 2:     // 5p: 0 ~ 14
                if ((squad >= 0) && (squad < NUM_S[2]))
                    return true;
                break;
            case 3:     // 1p: 0 ~ 8
                if ((squad >= 0) && (squad < NUM_S[3]))
                    return true;
                break;
            case 4:     // 2p: 0 ~ 7
                if ((squad >= 0) && (squad < NUM_S[4]))
                    return true;
                break;
        }
        return false;
    }
    
    // Set psDataSet
    public void setPSDataSet(boolean data) {
        psDataSet = data;
    }
    
    // Get psDataSet
    public boolean getPSDataSet() {
        return psDataSet;
    }
    
    // Get total number of Recruits calculated by this class 
    //      (independant information from RecruitDatabase)
    public int getTotNumRecruitsPS() {
        return cummulativePSize[4];
    }
}
