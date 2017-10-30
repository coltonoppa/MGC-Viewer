/**
* Recruit.java - half-immutable 
*   Recruit is a class that can save information of a single Recruit object
*   (isPatient can be changed during an execution of the program)
* 
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.recruit;

import com.coltonstack.mgcviewer.psinfo.PSDatabase;
import java.awt.Color;

public final class Recruit {
    
    private final int number;
    private final String name;
    private final String dob;
    private int platoon;
    private int squad;
    private boolean isPatient = false;

    // Constructor of Recruit
    public Recruit(int number, String name, String dob, int platoon, int squad) {
        this.number = number;
        this.name = name;
        this.dob = dob;
        this.platoon = platoon;
        this.squad = squad;
    }
    
    // Get number of Recruit
    public int getNumber() {
        return this.number;
    }

    // Get name of Recruit
    public String getName() {
        return this.name;
    }

    // Get birthdate of Recruit
    public String getDob() {
        return this.dob;
    }
    
    // Get platoon of Recruit
    public int getPlatoon() {
        return this.platoon;
    }
    
    // Get squad of Recruit
    public int getSquad() {
        return this.squad;
    }
    
    // Set platoon of Recruit
    public void setPlatoon(int platoon) {
        this.platoon = platoon;
    }
    
    // Set squad of Recruit
    public void setSquad(int squad) {
        this.squad = squad;
    }
    
    // Get isPatient to see if Recruit is a patient
    public boolean isPatient() {
        return this.isPatient;
    }

    // Set isPatient
    public void setIsPatient(boolean isPatient) {
        this.isPatient = isPatient;
    }
    
    // Get color by platoon
    public Color getColor(int platoon) {
        switch(platoon) {
            case 3:             // Black
                return Color.decode("0x000000");
            case 4:             // Blue
                return Color.decode("0x0039bd");
            case 5:             // Orange
                return Color.decode("0xbd7b00");
            case 1:             // Red
                return Color.decode("0xbd003f");
            case 2:             // Green
                return Color.decode("0x309748");
            default:
                return Color.decode("0x000000");
        }
    }
    
    // Get information about a single Recruit
    //      number, name, birthdate, platoon, squad, isPatient
    @Override
    public String toString() {
        String retVal = this.number + " " + this.name + " " + this.dob + " ";
        
        PSDatabase psDB = PSDatabase.getInstance();
        retVal += psDB.getPSDataSet() ? this.platoon + "p" + this.squad + "s " : " ";
        retVal += this.isPatient?"!":"";
        return retVal;
    }
}