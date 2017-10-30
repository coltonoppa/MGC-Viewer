/**
* EastPanel.java - singleton
*   EastPanel displays information about the p/s allocation of recruits 
*   written in 102RB style:
*       3p: 1 ~ 10s x 21= 210
*       4p: 1 ~ 10s x 22= 220
*       5p: 1 ~ 15s x 22= 330
*       1p: 1 ~ 9s x 22= 198
*       2p: 1 ~ 4s x 20
*           5 ~ 8s x 21= 164
*       Total: 1122
*   Also, this class also contains a button that creates patient belts
*   which help squad leaders by rapidly gathering patients in the morning.
*
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.window.panel;

import com.coltonstack.mgcviewer.psinfo.PSDatabase;
import com.coltonstack.mgcviewer.recruit.PatientDatabase;
import com.coltonstack.mgcviewer.recruit.RecruitDatabase;
import com.coltonstack.mgcviewer.window.EditPSWindow;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class PSDataPanel extends JPanel {
    
    private final JPanel psDsplyPnl;
    private final JPanel psBtnPnl;
    public static JTextArea psTxtArea;
    public static JLabel currentMGC;
    
    private static PSDataPanel psDataPanel;
    
    // Get singleton object of PSDataPanel
    public static PSDataPanel getInstance() {
        if (psDataPanel == null)
            psDataPanel = new PSDataPanel();
        return psDataPanel;
    }
    
    // Construct p/s data panel of MainWindow 
    private PSDataPanel() {
        psDsplyPnl = addPSDsplyPnl();
        psBtnPnl = addPSBtnPnl();
        
        this.setPreferredSize(new Dimension(250,getHeight()));
        this.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, Color.GRAY));
        this.setLayout(new BorderLayout());
        this.add(psDsplyPnl, BorderLayout.CENTER);
        this.add(psBtnPnl, BorderLayout.SOUTH);
    }
        
    // Create CENTER panel that display p/s information
    private JPanel addPSDsplyPnl() {
        JPanel psChildPanel = new JPanel();
        psChildPanel.setLayout(new BorderLayout());
        psChildPanel.add(new JLabel(" - 생활관 편성 - "), BorderLayout.NORTH);
        
        psTxtArea = new JTextArea();
        psTxtArea.setEditable(false);
        psTxtArea.setPreferredSize(new Dimension(200,340));
        psTxtArea.setMargin(new Insets(5, 5, 5, 5));        
        setPSTxtArea();
        
        JScrollPane scroll = new JScrollPane (psTxtArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
              scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        psChildPanel.add(scroll, BorderLayout.CENTER);
        
        JPanel path = new JPanel();
        currentMGC = new JLabel("현재 파일: " + RecruitDatabase.mgcTxtFilePath.getName());
        path.add(currentMGC);
        psChildPanel.add(path, BorderLayout.SOUTH);
        
        JPanel psdPanel = new JPanel();
        psdPanel.add(psChildPanel);
        return psdPanel;
    }
    
    // Create SOUTH panel that contains Edit button & Belt-creation button
    private JPanel addPSBtnPnl() {
        JButton editPSButton = new JButton("설정");
        editPSButton.addActionListener(new setPSBtnHndlr());
        
        JButton createPatientBeltButton = new JButton("면담자 띠 생성");
        createPatientBeltButton.addActionListener(new makePatientBeltBtnHndlr());
        
        JPanel psbPanel = new JPanel();
        psbPanel.add(editPSButton);
        psbPanel.add(createPatientBeltButton);
        
        return psbPanel;
    }
    
    // Set p/s textArea
    public void setPSTxtArea() {
        PSDatabase psDB = PSDatabase.getInstance();
        if (psDB.getPSDataSet())
            psTxtArea.setText(psDB.getPSString());
        else
            psTxtArea.setText("생활관을 편성하지 않았습니다.");
    }
    
    /******************************** Handler Classes From Here ******************************************/
    // HANDLER: Open window that lets user edit p/s data
    private class setPSBtnHndlr implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            EditPSWindow editPSWindow = EditPSWindow.getInstance();
            editPSWindow.pop();
        }
    }
    
    // HANDLER: Create patient belts
    private class makePatientBeltBtnHndlr implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            PatientDatabase patientDB = PatientDatabase.getInstance();
            patientDB.makePatientBelt();
            JOptionPane.showMessageDialog(null, "면담자 띠가 생성되었습니다");
        }
    }
}