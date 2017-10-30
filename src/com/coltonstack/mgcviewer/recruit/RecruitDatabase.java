/**
* RecruitDatabase.java - singleton
*   As abscence of database driver, RecruitDatabase is a memory-living class
*   that functions as database for List of Recruits.
* 
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.recruit;

import com.coltonstack.mgcviewer.psinfo.PSDatabase;
import com.coltonstack.mgcviewer.window.EditPSWindow;
import java.io.*;
import java.util.*;

public final class RecruitDatabase {
    
    private List<Recruit> recruits;
    private static int numRecruits;
    public static File mgcTxtFilePath;
    
    private PatientDatabase patientDB;
    private PSDatabase psDB;
    
    private static RecruitDatabase recruitDB;
    
    // Get singleton instance of RecruitDatabase
    public synchronized static RecruitDatabase getInstance() {
        if (recruitDB == null)
            recruitDB = new RecruitDatabase();
        return recruitDB;
    }
    
    // private constructor - singleton
    private RecruitDatabase() {}
    
    // Fetch recruit data from mgc.txt & Save the data into List of Recruits
    public void initRecruits() {
        String tempStr;
        String tempStrArr[];
        patientDB = PatientDatabase.getInstance();
        psDB = PSDatabase.getInstance();
        
        if (psDB.getPSDataSet()) {
            EditPSWindow editPSWindow = EditPSWindow.getInstance();
            editPSWindow.pop();
        }
        
        recruits = new ArrayList<>();
        recruits.add(new Recruit(0, "", "", 0, 0));
        
        try (BufferedReader br = new BufferedReader(new FileReader(mgcTxtFilePath))) {
            
            int num;
            int platoon;
            int patientIdx = 0;
            PSDatabase psInfo = PSDatabase.getInstance();
            numRecruits = 0;
            
            // Independently set first Recruit since ANSI Character Encoding has ` at first line
            if ((tempStr = br.readLine()) != null) {
                tempStrArr = tempStr.split(" ");
                recruits.add(new Recruit(1, tempStrArr[1], tempStrArr[2], 3, 1));
                if (patientDB.getPatientsSize() > 0) {
                    if (patientDB.getPatient(patientIdx) == 1) {
                        recruits.get(1).setIsPatient(true);
                        patientIdx++;
                    }
                }
                numRecruits++;
            }
                
            // Initialize all recruits in mgc text file
            while ((tempStr = br.readLine()) != null) {
                tempStrArr = tempStr.split(" ");
                num = Integer.parseInt(tempStrArr[0]);
                platoon = psInfo.getPlatoon(num);
                recruits.add(new Recruit(num, tempStrArr[1], tempStrArr[2], platoon, psInfo.getSquad(num, platoon)));
                if (patientIdx < patientDB.getPatientsSize() && patientDB.getPatient(patientIdx) == num) {
                    recruits.get(num).setIsPatient(true);
                    patientIdx++;
                }
                numRecruits++;
            }
        } catch (IOException e) { /* Handle exception */ }
    }
    
    // Set p/s information of recruits
    public void updtRecruitPS() {
        PSDatabase psDB = PSDatabase.getInstance();
        int count  = 1;
        int platoon;
        int squad;
        
        // Initialize all recruits in mgc text file
        while (count <= numRecruits) {
            platoon = psDB.getPlatoon(count);
            squad = psDB.getSquad(count, platoon);
            recruits.get(count).setPlatoon(platoon);
            recruits.get(count).setSquad(squad);
            count++;
        }
    }
    
    // Get the total number of recruits
    public static int getNumRecruits() {
        return numRecruits;
    }
    
    // Get List of Recruits found by name
    public List<Recruit> getRecruitsByName(String name) {
        List<Recruit> results = new ArrayList<>();
        recruits.parallelStream()
                .filter(rc -> rc.getName().equals(name))
                .forEachOrdered(rc -> results.add(rc));
        return results;
    }
    
    // Get List of Recruits found by dob
    public List<Recruit> getRecruitsByDob(String dob) {
        List<Recruit> results = new ArrayList<>();
        recruits.parallelStream()
                .filter(rc -> rc.getDob().equals(dob))
                .forEachOrdered(rc -> results.add(rc));
        return results;
    }
    
    // Get a single Recruit found by number
    public Recruit getRecruitByNumber(int number) {
        if ((number > 0) && (number <= numRecruits))
            return recruits.get(number);
        else
            return null;
    }
    
    // Get List of Recruits in specifically given platoon & squad
    public List<Recruit> getRecruitsByPS(String ps) {
        List<Recruit> results = new ArrayList<>();
        String tmp = ps.split("p")[1];
        
        int platoon = Integer.parseInt(ps.charAt(0) + "");
        int squad = Integer.parseInt(tmp.substring(0, tmp.length()-1));
        
        platoon = PSDatabase.platoonToIdx(platoon);
        squad = PSDatabase.squadToIdx(squad, platoon);
        
        if (!PSDatabase.isPSValid(platoon, squad))
            return results;
        
        int start = 0;
        for (int i = 0; i < platoon; i++) 
            start += PSDatabase.eachPlatoonSize[i];
        
        for (int i = 0; i < squad; i++)
            start += PSDatabase.numRecruitsPS[platoon][i];
        start++;
        int end = start + PSDatabase.numRecruitsPS[platoon][squad];
        
        while (start < end) {
            results.add(recruits.get(start));
            start++;
        }
        
        return results;
    }
    
    // Set path of mgc.txt
    public static void setMgcTxtFilePath(String loc) {
        mgcTxtFilePath = new File(loc);
    }
    
    // Get path of mgc.txt
    public static String getMgcTxtFilePath() {
        return mgcTxtFilePath.getAbsolutePath();
    }
}