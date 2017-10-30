/**
* InitDatabases.java - singleton
*   Initialize patient, recruit, p/s database IN ORDER
*   The ORDER is important since each database is prerequisite for the
*   next initialized class.
*
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.runnable.process;

import com.coltonstack.mgcviewer.psinfo.PSDatabase;
import com.coltonstack.mgcviewer.recruit.PatientDatabase;
import com.coltonstack.mgcviewer.recruit.RecruitDatabase;

public class InitDatabases implements ProcessData {
    
    // It is a memory-loss to have multiple operation objects, so InitDatabases class is a singleton
    private static InitDatabases initDatabases;
    
    private InitDatabases() {}
    
    // Get singleton object of InitDatabases
    public static InitDatabases getInstance() {
        if (initDatabases == null)
            initDatabases = new InitDatabases();
        return initDatabases;
    }
    
    @Override
    public void run(String... input) {
        
        // Mere instantiation
        PatientDatabase patientDB = PatientDatabase.getInstance();
        RecruitDatabase recruitDB = RecruitDatabase.getInstance();
        PSDatabase psDB = PSDatabase.getInstance();
        
        // These database class must be initialized in order of patient, p/s then recruit
        patientDB.initPatients();
        psDB.initPSData();
        recruitDB.initRecruits();
        if (RecruitDatabase.getNumRecruits() == psDB.getTotNumRecruitsPS())
            psDB.setPSDataSet(true);
        else
            psDB.setPSDataSet(false);
    }
}
