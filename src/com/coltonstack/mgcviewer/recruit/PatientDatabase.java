/**
* PatientDatabase.java - singleton
*   Due to abscence of database driver, PatientDatabase is a memory-living class
*   that functions as database for patient information.
*
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.recruit;

import com.coltonstack.mgcviewer.psinfo.PSDatabase;
import static com.coltonstack.mgcviewer.psinfo.PSDatabase.*;
import java.io.*;
import java.util.*;

public final class PatientDatabase {
    private List<Integer> patients;
    public static File patientTxtLocation = new File("data" + File.separator + "patients.txt");
    public static File beltTxtLocation = new File("면담자 띠.txt");
    private static PatientDatabase patientDB;
    
    // Get singleton instance of PatientDatabase
    public static PatientDatabase getInstance() {
        if (patientDB == null)
            patientDB = new PatientDatabase();
        return patientDB;
    }
    
    // private constructor - singleton
    private PatientDatabase() {}
    
    // Fetch patient data from patients.txt & Save the data into List of Integer
    public void initPatients() {
        String tempStr;
        patients = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(patientTxtLocation))) {
            while ((tempStr = br.readLine()) != null) 
                patients.add(Integer.parseInt(tempStr));
        } catch (IOException e) { /* Handle exception */ }
    }
    
    // Get a single patient number from List of patient number
    public int getPatient(int idx) {
        return patients.get(idx);
    }
    
    // Clear all patient numbers in the List
    public void clrPatients() {
        patients.clear();
    }
        
    // With String array of recruit numbers given as parameter, synchronize it with
    //      patients[] and patients.txt
    public void savePatients(String[] numbers) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(patientTxtLocation))) {
            if (numbers == null) {
                bw.append("");
                return;
            }
            
            int patient;
            RecruitDatabase recruitDB = RecruitDatabase.getInstance();

            // Unmark previous patients
            patients.stream()
                    .filter(p -> ((p > 0) && (p <= RecruitDatabase.getNumRecruits())))
                    .forEachOrdered(p -> recruitDB.getRecruitByNumber(p).setIsPatient(false));
            patients.clear();

            // Add & mark new patients
            for (String number : numbers) {
                patient = Integer.parseInt(number);
                if ((patient > 0) && (patient <= RecruitDatabase.getNumRecruits())) {
                    patients.add(patient);
                    recruitDB.getRecruitByNumber(patient).setIsPatient(true);
                    bw.append(patient + System.lineSeparator());
                }
            }
        } catch (IOException e) { /* Handle exception */ }
    }
    
    // Get current total number of patients
    public int getPatientsSize() {
        return patients.size();
    }
    
    // Create "면담자 띠.txt" based on current patients[]
    public void makePatientBelt() {
        PSDatabase psInfo = PSDatabase.getInstance();
        String resultLine = "";
        boolean pPrinted = false;
        boolean sPrinted = false;
        boolean printLine = false;
        int ptPlatoon = 0;
        int ptSquad;
        int curPlatoon;
        int curSquad;
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(beltTxtLocation))) {
            int patientIdx = 0;
            for (int i = 0; i < NUM_P; i++) {
                curPlatoon = PSDatabase.idxToPlatoon(i);
                
                for (int j = 0; j < NUM_S[i]; j++){
                    curSquad = PSDatabase.idxToSquad(j, curPlatoon);
                    
                    while (patientIdx < patients.size()) {
                        ptPlatoon = psInfo.getPlatoon(patients.get(patientIdx));
                        ptSquad = psInfo.getSquad(patients.get(patientIdx), ptPlatoon);
                        
                        if (curPlatoon != ptPlatoon)
                            break;
                        if (curSquad != ptSquad)
                            break;
                        if (!pPrinted) {
                            bw.append(curPlatoon + "p:" + System.lineSeparator());
                            pPrinted = true;
                        }
                        if (!sPrinted) {
                            resultLine += curSquad + "s: ";
                            sPrinted = true;
                        }
                        resultLine += patients.get(patientIdx) + ", ";
                        patientIdx++;
                        printLine = true;
                    }
                    
                    if (printLine) {
                        resultLine = resultLine.substring(0, resultLine.length()-2);
                        bw.append(resultLine + System.lineSeparator());
                        resultLine = "";
                        printLine = false;
                    }
                    sPrinted = false;

                    if (curPlatoon != ptPlatoon)
                        break;
                }
                
                if (pPrinted)
                    bw.append(System.lineSeparator());
                pPrinted = false;
            }
        } catch (IOException e) { /* Handle exception */ }
    }
    
    // Get a list of patient numbers vertically with line seperator
    @Override
    public String toString() {
        String result = "";
        result = patients.stream().map((p) -> p + "\n").reduce(result, String::concat);
        return result;
    }
}
