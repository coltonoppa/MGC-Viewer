/**
* PatientInfoWindow.java - singleton
*   PatientInfoWindow lets a user set a list of patients and 
*   view new list of patients in PatientDatabase through a single 
*   textarea given in the window.
* 
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.window;

import com.coltonstack.mgcviewer.recruit.PatientDatabase;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EditPatientWindow extends JDialog {
    
    private JLabel numPatientsLbl;
    private JTextArea patientTxtarea;
    
    private PatientDatabase patientDB;
    
    private static EditPatientWindow editPatientWindow;
    
    // Get singleton object of EditPatientWindow
    public static EditPatientWindow getInstance() {
        if (editPatientWindow == null)
            editPatientWindow = new EditPatientWindow(MainWindow.getInstance(), "면담자 설정", true);
        return editPatientWindow;
    }
    
    // Construct window for editing a list of patient information
    private EditPatientWindow(JFrame f, String s, boolean b) {
        super(f, s, b);
        
        this.setSize(new Dimension(500, 650));
        this.setLayout(new BorderLayout());
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                
        this.add(addCntrPnl(), BorderLayout.CENTER);
        this.add(addBtmPnl(), BorderLayout.SOUTH);
    }
    
    // Update textarea to get filled with the most recent list of patients
    public void updtTxtarea() {
        patientDB = PatientDatabase.getInstance();
        patientTxtarea.setText(patientDB.toString());
    }
    
    // Create bottom panel that displays a textarea for a list of patients
    private JPanel addCntrPnl() {
        
        JLabel numPatients = new JLabel("<html>면담자는 번호순으로 입력!</html>");
        numPatientsLbl = new JLabel("총 면담자: " + PatientDatabase.getInstance().getPatientsSize());
        
        patientTxtarea = new JTextArea(32, 30);
        JScrollPane scroll = new JScrollPane (patientTxtarea, 
           JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel cPanel = new JPanel();
        cPanel.add(scroll);
        cPanel.add(numPatients);
        cPanel.add(numPatientsLbl);
        
        return cPanel;
    }
    
    // Create bottom panel that have update button & clear button
    private JPanel addBtmPnl() {
        JPanel bPanel = new JPanel();
        
        JButton updateButton = new JButton("저장");
        updateButton.addActionListener(new UpdtBtnHndlr());
        
        JButton clearButton = new JButton("초기화");
        clearButton.addActionListener(new ClrBtnHndlr());
        
        bPanel.add(updateButton);
        bPanel.add(clearButton);
        
        return bPanel;
    }
    
    // Pop window to set patient information
    public void pop() {
        editPatientWindow.updtTxtarea();
        editPatientWindow.setVisible(true);
    }
    
    /******************************** Handler Classes From Here ******************************************/
    // HANDLER: Retrieve written list of patients from textarea & save it into patients.txt & PatientDatabase
    private class UpdtBtnHndlr implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            patientDB = PatientDatabase.getInstance();
            patientDB.savePatients(patientTxtarea.getText().split("\n"));
            numPatientsLbl.setText("총 면담자: " + patientDB.getPatientsSize());
            JOptionPane.showMessageDialog(null, "면담자 리스트 저장 완료");
        }
    }
    
    // HANDLER: Clear the textarea
    private class ClrBtnHndlr implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            patientTxtarea.setText("");
        }
    }
}
