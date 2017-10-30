/**
* ViewDataPanel.java - singleton
*   ViewDataPanel is composed of a number of input text fields that are used
*   to receive inputs from a user.
*
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.window.panel;

import com.coltonstack.mgcviewer.psinfo.PSDatabase;
import com.coltonstack.mgcviewer.runnable.function.FindLines;
import com.coltonstack.mgcviewer.runnable.function.FindRecruits;
import com.coltonstack.mgcviewer.runnable.function.FunctionData;
import com.coltonstack.mgcviewer.recruit.*;
import com.coltonstack.mgcviewer.window.MainWindow;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import java.util.List;

public final class ViewDataPanel extends JPanel {
    
    public static final int NUM_COLUMNS = 3;
    public static final int NUM_TEXT_AREAS = 20;
    public static final int TOT_NUM_TEXT_FIELDS = NUM_TEXT_AREAS * NUM_COLUMNS;
    public static final int MAX_LABELS = RecruitDatabase.getNumRecruits();
    
    private static JTextField[] iptTxtFlds;
    public static JLabel[][] txtFldLbls;
    
    public static ViewDataPanel viewDataPanel;
    
    // Get singleton object of ViewDataPanel
    public static ViewDataPanel getInstance() {
        if (viewDataPanel == null)
            viewDataPanel = new ViewDataPanel();
        return viewDataPanel;
    }
    
    // Construct data-view panel of MainWindow 
    private ViewDataPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        addChldPnls();
    }
    
    // Create child panels for ViewDataPanel
    public void addChldPnls() {
        JPanel[] childPnls = new JPanel[NUM_COLUMNS];
        JPanel[] childPnlsWrapper = new JPanel[NUM_COLUMNS];
        
        JPanel[] tflbPnlsWrappers = new JPanel[TOT_NUM_TEXT_FIELDS];
        JPanel[] tfPnls = new JPanel[TOT_NUM_TEXT_FIELDS];
        JPanel[] lbPnls = new JPanel[TOT_NUM_TEXT_FIELDS];
        
        iptTxtFlds = new JTextField[TOT_NUM_TEXT_FIELDS];
        txtFldLbls = new JLabel[TOT_NUM_TEXT_FIELDS][MAX_LABELS];
                
        iptTxtFldsHndlr handler = new iptTxtFldsHndlr();
        
        int count = 0;
        
        // 1.For each child panel inside ViewDataPanel,
        for (int i = 0; i < NUM_COLUMNS; i++) {
            childPnls[i] = new JPanel();
            childPnls[i].setLayout(new BoxLayout(childPnls[i], BoxLayout.Y_AXIS));
            
            // 2.Allocate fixed number of textfields
            for (int j = 0; j < NUM_TEXT_AREAS; j++) {
                tflbPnlsWrappers[count] = new JPanel();
                tflbPnlsWrappers[count].setLayout(new BorderLayout());
                
                iptTxtFlds[count] = new JTextField(15);
                iptTxtFlds[count].addActionListener(handler);
                
                tfPnls[count] = new JPanel();
                tfPnls[count].add(iptTxtFlds[count]);
                
                lbPnls[count] = new JPanel();
                lbPnls[count].setLayout(new BoxLayout(lbPnls[count], BoxLayout.Y_AXIS));
                
                // 3.Place empty labels below the textfields
                for (int k = 0; k < MAX_LABELS; k++) {
                    txtFldLbls[count][k] = new JLabel("");
                    txtFldLbls[count][k].setAlignmentX(Component.CENTER_ALIGNMENT); 
                    lbPnls[count].add(txtFldLbls[count][k]);
                }
                
                tflbPnlsWrappers[count].add(tfPnls[count], BorderLayout.NORTH);
                tflbPnlsWrappers[count].add(lbPnls[count], BorderLayout.CENTER);
                
                childPnls[i].add(tflbPnlsWrappers[count]);
                count++;
            }
            
            childPnlsWrapper[i] = new JPanel();
            childPnlsWrapper[i].add(childPnls[i]);
            this.add(childPnlsWrapper[i]);
        } 
    }
    
    // Clear all unempty textfields & labels
    public static void clrViewDataPnl() {
        for (int i = 0; i < TOT_NUM_TEXT_FIELDS; i++) {
            if (!iptTxtFlds[i].getText().isEmpty()) {
                iptTxtFlds[i].setText("");
                clrSnglTxtFldLbls(i);
            }
        }
    }    
    
    // Clear all labels of a single textfield
    private static void clrSnglTxtFldLbls(int idx) {
        for (int j = 0; j < MAX_LABELS; j++) {
            if (!txtFldLbls[idx][j].getText().isEmpty()) {
                txtFldLbls[idx][j].setText("");
                txtFldLbls[idx][j].setForeground(Color.BLACK);
            }
        }
    }
    
    // Update labels based on List of Recruit OR String
    private void updtSnglLblPnl(List<?> list, int idx) {
        RecruitDatabase recruitDB = RecruitDatabase.getInstance();
        PSDatabase psDB = PSDatabase.getInstance();
        Object tempObj;
        Recruit tempRct;
        
        // List is List<Object> if it is retrieved from FindLines
        // List is List<Recruit> if it is retrieved from FindRecruits
        if (list.isEmpty()) {
            txtFldLbls[idx][0].setForeground(Color.DARK_GRAY);
            txtFldLbls[idx][0].setText("결과없음");
        } else {
            for (int i = 0; i < list.size(); i++) {
                tempObj = list.get(i);
                
                if (tempObj != null) {
                    if (tempObj instanceof Recruit) {
                        tempRct = (Recruit)tempObj;
                    } else {
                        String s = ((String) tempObj).split(" ")[0];
                        if (s.equals("﻿1")) s = "1";
                        tempRct = recruitDB.getRecruitByNumber(Integer.parseInt(s));
                    }
                    txtFldLbls[idx][i].setText(tempRct.toString());
                    if (psDB.getPSDataSet())
                        txtFldLbls[idx][i].setForeground(tempRct.getColor(tempRct.getPlatoon()));
                } else {
                    txtFldLbls[idx][0].setForeground(Color.DARK_GRAY);
                    txtFldLbls[idx][0].setText("결과없음");
                    break;
                }
            }
        }
    }
    
    /******************************** Handler Classes From Here ******************************************/
    /**============================================================================**/
    // - With filled out the textfields, this handler is invoked with enter pressed 
    // 1. Clean up textField labels
    // 2. Looks through ALL textFields to find FILLED textFields
    // 3. If empty, skip. If filled, check "WHERE TO LOOK UP" to retrieve result:
    //      (If findObject is true, look up List<Recruit>. Otherwise, look up mgc.txt)
    // 3-1. Make threads for each filled textfield & give tasks to the threads
    // 3-2a. The threads will return List of Future that contains List<Recruit> (if findObject == true)
    // 3-2b. The threads will return List of Future that contains List<String> (if findObject == false)
    // 3-3. With the List<Recruit> or List<String> retrieved, update textField labels
    /**============================================================================**/
    // HANDLER: Open window that lets user edit p/s data
    private class iptTxtFldsHndlr implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            FunctionData findRecruits = FindRecruits.getInstance();
            FunctionData findLines = FindLines.getInstance();
            List<Future<List<?>>> resObj = new ArrayList<>();
            //List<?> resO = new ArrayList<>();
            
            ExecutorService service = Executors.newCachedThreadPool();
            
            long startT;
            long endT;
            
            //startT = System.currentTimeMillis();                                              // Single-thread
            // Check all input textFields if not empty in multi-threaded way
            try {
                for (int i = 0; i < TOT_NUM_TEXT_FIELDS; i++) {
                    final String in = iptTxtFlds[i].getText();
                    clrSnglTxtFldLbls(i);
                    
                    // If a textfield is filled,
                    if (!in.isEmpty()) {
                        if (MainWindow.seekObjs) {   /* Retrieve from RecruitDatabse */
                            resObj.add(service.submit((Callable)() -> findRecruits.run(in)));
                            //resO = findLines.run(in);                                         // Single-thread
                        }
                        else {                       /* Retrieve from mgc.txt */
                            resObj.add(service.submit((Callable)() -> findLines.run(in)));
                            //resO = findLines.run(in);                                         // Single-thread
                        }
                    }
                }
                // All Future objs are in 'resObj'. Use these to set display results.
                int idx = 0;
                for (int i = 0; i < TOT_NUM_TEXT_FIELDS; i++) {
                    if (!iptTxtFlds[i].getText().isEmpty()) {
                        final List<?> result = resObj.get(idx++).get();
                        final int index = i;
                        service.submit(() -> updtSnglLblPnl(result, index));
                        
                        //updtSnglLblPnl(resO, i);                                              // Single-thread
                    }
                }
                
            } catch (InterruptedException | ExecutionException ex) {
                /* Handle exception */
            } finally {
                if (service != null)
                    service.shutdown();
                /*endT = System.currentTimeMillis();
                System.out.println("StartT: " + startT);
                System.out.println("EndT: " + endT);
                System.out.println("Elapsed: " + (endT - startT));*/                            // Single-thread
            }
        }
    }
    
    // HANDLER: Clear any textFields and labels that are not empty
    public static class ClrBtnHndlr implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            clrViewDataPnl();
        }
    }
}