/**
* PSEditWindow.java - singleton
*   PSEditWindow lets a user set the number of recruits for each 
*   squad/platoon through given a list of text fields.
* 
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.window;

import com.coltonstack.mgcviewer.window.panel.PSDataPanel;
import com.coltonstack.mgcviewer.psinfo.*;
import com.coltonstack.mgcviewer.recruit.RecruitDatabase;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EditPSWindow extends JDialog {
    
    private JButton[] cpAllBtns;
    private JTextField[][] iptTxtFlds;
    public JLabel totRecLbl;
        
    private static EditPSWindow editPsWindow;
    
    // Get singleton object of EditPSWindow
    public static EditPSWindow getInstance() {
        if (editPsWindow == null)
            editPsWindow = new EditPSWindow(MainWindow.getInstance(), "소대, 생활관 설정", true);
        return editPsWindow;
    }
    
    // Construct window for editing platoon and squad information 
    private EditPSWindow(JFrame f, String s, boolean b) {
        super(f, s, b);
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(new Dimension(500, 650));
        this.setLayout(new BorderLayout());
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        this.add(addCntrPnl(), BorderLayout.CENTER);
        this.add(addBtmPnl(), BorderLayout.SOUTH);
    }
    
    // Create center panel that displays a list of textfields; 
    //      each represents number of recruits in each squad
    private JPanel addCntrPnl() {
        JPanel cPanel = new JPanel();
        cPanel.setLayout(new GridLayout(1, PSDatabase.NUM_P));
        
        cpAllBtns = new JButton[PSDatabase.NUM_P];
        iptTxtFlds = new JTextField[PSDatabase.NUM_P][];
        
        JLabel[] platoonLbls = new JLabel[PSDatabase.NUM_P];
        JPanel[] topPnls = new JPanel[PSDatabase.NUM_P];
        JPanel[] topPnlsWrappers = new JPanel[PSDatabase.NUM_P];
        JPanel[][] tfPnls = new JPanel[PSDatabase.NUM_P][];
        
        for (int i = 0; i < PSDatabase.NUM_P; i++) {
            
            topPnls[i] = new JPanel();
            topPnls[i].setLayout(new BoxLayout(topPnls[i], BoxLayout.Y_AXIS));
            
            cpAllBtns[i] = new JButton("통일");
            cpAllBtns[i].addActionListener(new CpAllBtnHndlr());
            topPnls[i].add(cpAllBtns[i]);
            
            platoonLbls[i] = new JLabel(PSDatabase.SEQ_P[i] + "p");
            topPnls[i].add(platoonLbls[i]);
            
            tfPnls[i] = new JPanel[PSDatabase.NUM_S[i]];
            iptTxtFlds[i] = new JTextField[PSDatabase.NUM_S[i]];
            for (int j = 0; j < PSDatabase.NUM_S[i]; j++) {
                tfPnls[i][j] = new JPanel();
                iptTxtFlds[i][j] = new JTextField(8);
                iptTxtFlds[i][j].setText(PSDatabase.numRecruitsPS[i][j] + "");
                tfPnls[i][j].add(iptTxtFlds[i][j]);
                topPnls[i].add(tfPnls[i][j]);
            }
            
            topPnlsWrappers[i] = new JPanel();
            topPnlsWrappers[i].add(topPnls[i]);
            cPanel.add(topPnlsWrappers[i]);
        }
        return cPanel;
    }
    
    // Create bottom panel that displays update button & clear button
    private JPanel addBtmPnl() {
        JPanel bPanel = new JPanel();
        
        totRecLbl = new JLabel("총 장정 수: " + RecruitDatabase.getNumRecruits());
        
        JButton updateButton = new JButton("저장");
        updateButton.addActionListener(new UpdtBtnHndlr());
        
        JButton clearButton = new JButton("초기화");
        clearButton.addActionListener(new ClrBtnHndlr());
        
        bPanel.add(totRecLbl);
        bPanel.add(updateButton);
        bPanel.add(clearButton);
        
        return bPanel;
    }
    
    // Clear all text fields is EditPSWindow
    public void clrTxtFlds() {
        for (int i = 0; i < PSDatabase.NUM_P; i++) {
            for (int j = 0; j < PSDatabase.NUM_S[i]; j++) 
                iptTxtFlds[i][j].setText("");
        }
    }
    
    // Pop window to set p/s information
    public void pop() {
        editPsWindow.totRecLbl.setText("총 장정 수: " + RecruitDatabase.getNumRecruits());
        editPsWindow.setVisible(true);
    }
    
    /******************************** Handler Classes From Here ******************************************/
    // HANDLER: Save p/s information in the textfields into psinfo.txt & PSDatabase
    private class UpdtBtnHndlr implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            int sum = 0;
            PSDatabase psDB = PSDatabase.getInstance();
            
            // For each empty textfields, set text with "0"
            for (int i = 0; i < PSDatabase.NUM_P; i++) {
                for (int j = 0; j < PSDatabase.NUM_S[i]; j++) {
                    if (iptTxtFlds[i][j].getText().equals(""))
                        iptTxtFlds[i][j].setText("0");
                    sum += Integer.parseInt(iptTxtFlds[i][j].getText());
                }
            }
            
            // Proceed savings iff "the number of recruits" is the SAME with 
            //      "the sum of all inputs in textfields"
            if (RecruitDatabase.getNumRecruits() == sum) {
                
                String str;
                boolean invalid = false;
                
                Outer: {
                    for (int i = 0; i < PSDatabase.NUM_P; i++) {
                        for (int j = 0; j < PSDatabase.NUM_S[i]; j++) {
                            str = iptTxtFlds[i][j].getText();

                            // Check if input is only number where 0 < n < 99
                            //      Break loop & pop up error message otherwise
                            if (str.matches("[0-9]{1,2}"))
                                PSDatabase.numRecruitsPS[i][j] = Integer.parseInt(str);
                            else {
                                invalid = true;
                                break Outer;
                            }
                        }
                    }
                    psDB.savePSData();
                    psDB.initPSData();
                    psDB.setPSString();
                    PSDataPanel.psTxtArea.setText(psDB.getPSString());
                    
                    RecruitDatabase recruitDB = RecruitDatabase.getInstance();
                    recruitDB.updtRecruitPS();
                    psDB.setPSDataSet(true);
                    EditPSWindow.getInstance().setVisible(false);
                    
                    JOptionPane.showMessageDialog(null, "생활관 편성 저장 완료");
                }
                if (invalid)
                    JOptionPane.showMessageDialog(null, "잘못된 입력");
            } 
            // If "the number of recruits" is NOT SAME with "the sum of all inputs in textfields",
            //      pop up error message
            else {
                String title = "오류 발생";
                String message = "mgc.txt의 장정 수와 편성표의 장정 수가 맞지 않습니다.\n"
                                + "실제 장정 수: " + RecruitDatabase.getNumRecruits() + "\n" 
                                + "상위 장정 수 합계: " + sum + "\n"
                                + "그래도 저장 하시겠습니까?";
                int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    psDB.setPSDataSet(false);
                    EditPSWindow.getInstance().setVisible(false);
                    PSDataPanel.psTxtArea.setText("생활관을 편성하지 않았습니다.");
                    
                    JOptionPane.showMessageDialog(null, "생활관 편성 저장 완료. 소대, 생활관 정보가 나오지 않습니다.");
                }
            }
        }
    }
    
    // HANDLER: Clear all the textfields in EditPSWindow
    private class ClrBtnHndlr implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            clrTxtFlds();
        }
    }
    
    // HANDLER: Copy input of the top-most textfield & 
    //      Paste it into the rest of textfields of the SAME platoon
    private class CpAllBtnHndlr implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            String copyStr;
            for (int i = 0; i < PSDatabase.NUM_P; i++) {
                if (event.getSource() == cpAllBtns[i]) {
                    copyStr = iptTxtFlds[i][0].getText();
                    for (int j = 0; j < PSDatabase.NUM_S[i]; j++) {
                        iptTxtFlds[i][j].setText(copyStr);
                    }
                    break;
                }
            }
        }
    }
}
