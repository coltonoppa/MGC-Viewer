/**
* MainWindow.java - singleton
*   MainWindow class combines all the panels that need to be displayed,
*   which include EastPanel object, WestPanel object, MenuBar and BottomBar.
*   
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.window;

import com.coltonstack.mgcviewer.psinfo.PSDatabase;
import com.coltonstack.mgcviewer.recruit.PatientDatabase;
import com.coltonstack.mgcviewer.recruit.RecruitDatabase;
import com.coltonstack.mgcviewer.window.panel.ViewDataPanel;
import com.coltonstack.mgcviewer.window.panel.PSDataPanel;
import com.coltonstack.mgcviewer.window.panel.ViewDataPanel.ClrBtnHndlr;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

public class MainWindow extends JFrame {
    
    public static boolean seekObjs = true;
    private JRadioButton inText;
    private JRadioButton inObject;
    
    private EditPatientWindow patientInfoWindow;
    
    private static MainWindow mainWindow;
    
    
    // Get singleton object of MainWindow
    public static MainWindow getInstance() {
        if (mainWindow == null)
            mainWindow = new MainWindow();
        return mainWindow;
    }
    
    // Construct main window for mgcViewer
    private MainWindow() {
        
        addMenuBar();
        
        this.setLayout(new BorderLayout());
        this.add(PSDataPanel.getInstance(), BorderLayout.EAST);
        this.add(addCntrPnl(), BorderLayout.CENTER);
                
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 700);
        this.setResizable(false);
        
        // Pop up main window at the center of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }
    
    // Create central panel that have ViewDataPanel & searchOptionPanel
    private JPanel addCntrPnl() {
        JPanel cPanel = new JPanel();
        JPanel searchOptionPanel = addSrchOptPnl();
        
        ViewDataPanel viewDataPanel = ViewDataPanel.getInstance();
        JScrollPane scrollFrame = new JScrollPane(viewDataPanel);
        viewDataPanel.setAutoscrolls(true);
        
        cPanel.setLayout(new BorderLayout());
        cPanel.add(scrollFrame, BorderLayout.CENTER);
        cPanel.add(searchOptionPanel, BorderLayout.SOUTH);
        
        return cPanel;
    }
    
    // Create search option panel which goes into CentralPanel
    private JPanel addSrchOptPnl() {
        JPanel westBottomBar = new JPanel();
        ButtonGroup bGroup = new ButtonGroup();
        
        inText = new JRadioButton("mgc.txt에서 찾기", false);
        inObject = new JRadioButton("장정 정보로 찾기", true);
        
        bGroup.add(inText);
        bGroup.add(inObject);
        
        westBottomBar.add(inText);
        westBottomBar.add(inObject);
        
        inText.addItemListener(new SrchOptHndlr());
        inObject.addItemListener(new SrchOptHndlr());
        
        JButton clearButton = new JButton("초기화");
        JButton patientInfoButton = new JButton("면담자 설정");
        
        clearButton.addActionListener(new ClrBtnHndlr());
        patientInfoButton.addActionListener(new PatientInfoBtnHndlr());
        
        westBottomBar.add(clearButton);
        westBottomBar.add(patientInfoButton);
        
        return westBottomBar;
    }
    
    // Create menu bar:
    //  1.Browse: open FileChooser to locate & open new mgc.txt
    //  2.Exit: exit this application
    private void addMenuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("파일(F)");
        file.setMnemonic(KeyEvent.VK_F);

        JMenuItem eMenuItem = new JMenuItem("종료(E)");
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("프로그램 종료");
        eMenuItem.addActionListener(e -> System.exit(0));
        
        JMenuItem bMenuItem = new JMenuItem("열기(B)");
        bMenuItem.setMnemonic(KeyEvent.VK_B);
        bMenuItem.setToolTipText("mgc.txt 열기");
        bMenuItem.addActionListener(new OpenNewMGCFileHandler());

        file.add(bMenuItem);
        file.add(eMenuItem);
        
        JMenu help = new JMenu("도움말(H)");
        help.setMnemonic(KeyEvent.VK_H);

        JMenuItem hMenuItem = new JMenuItem("사용법(U)");
        hMenuItem.setMnemonic(KeyEvent.VK_U);
        hMenuItem.setToolTipText("MGC 뷰어 사용법");
        hMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(null, helpStr));
        
        JMenuItem aMenuItem = new JMenuItem("정보(I)");
        aMenuItem.setMnemonic(KeyEvent.VK_I);
        aMenuItem.setToolTipText("MGC 뷰어 정보");
        aMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(null, aboutStr));
        
        help.add(hMenuItem);
        help.add(aMenuItem);
        
        menubar.add(file);
        menubar.add(help);

        setJMenuBar(menubar);
    }
    
    /******************************** Handler Classes From Here ******************************************/
    // HANDLER: Using radio buttons, choose where to seek for inputs; in text file or in objects
    private class SrchOptHndlr implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {
            if (inText.isSelected())
                seekObjs = false;
            else if (inObject.isSelected())
                seekObjs = true;
        }
    }
    
    // HANDLER: Open patient information window
    private class PatientInfoBtnHndlr implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            patientInfoWindow = EditPatientWindow.getInstance();
            patientInfoWindow.pop();
        }
    }
    
    // HANDLER: Open new mgc.txt file
    private class OpenNewMGCFileHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (OpenFileChooser.apndMgcPath(new File("mgcMetadata.txt"))) {
                ViewDataPanel.clrViewDataPnl();
                PSDataPanel.currentMGC.setText("현재 파일: " + RecruitDatabase.mgcTxtFilePath.getName());
                
                // Empty p/s data
                PSDatabase psDB = PSDatabase.getInstance();
                psDB.clrPSData();
                psDB.savePSData();
                psDB.initPSData();
                psDB.setPSDataSet(false);
                
                // Empty patients data
                PatientDatabase patientDB = PatientDatabase.getInstance();
                patientDB.clrPatients();
                patientDB.savePatients(null);
                patientDB.initPatients();
                
                // Load new recruit data
                RecruitDatabase recruitDB = RecruitDatabase.getInstance();
                recruitDB.initRecruits();
                
                // Open EditPSWindow
                PSDataPanel.psTxtArea.setText("생활관을 편성하지 않았습니다.");
                EditPSWindow psEditWindow = EditPSWindow.getInstance();
                psEditWindow.clrTxtFlds();
                psEditWindow.setVisible(true);
            }
        }
    }
    
    private final String aboutStr = "             = MGC 뷰어 version별 정보 = \n\n" 
                                    +"1.0v: 장정 번호를 통한 정보 검색 기능\n"
                                    + "1.1v: 장정 이름, 생년월일을 통한 정보 검색 기능, 면담자 설정 추가\n"
                                    + "1.2v: 소대&생활관을 통한 정보 검색 기능, 소대&생활관 정보 설정 및 출력 기능 추가\n"
                                    + "1.3v: mgc.txt를 소스로한 검색 기능 추가, 입력 필드 갯수 확대\n"
                                    + "1.4v: 멀티 스레딩을 통한 검색 기능 추가\n\n"
                                    + "제작자: 102보충대대 경비중대 4소대 분대장 민주홍";
    
    private final String helpStr = "                = MGC 뷰어 사용법 = \n\n"
                                    + "1. 프로그램을 사용하기 전, 분류계에서 받은 mgc.txt를 로드합니다.\n"
                                    + "2. 생활관 편성표를 입력합니다.\n"
                                    + "   생활관 편성표를 입력하지 않을시에는 소대&생활관 정보가 나오지 않습니다.\n"
                                    + "3. 장정 정보와 소대&생활관으로 검색한다면 \"장정 정보로 찾기\"에 체크를\n"
                                    + "   mgc.txt에 매칭되는 문자열을 찾는다면 \"mgc.txt에서 찾기\"에 체크를 합니다.\n"
                                    + "4-1. 장정 정보와 소대&생활관으로 검색한다면 입력란에 장정 번호, 이름, 생년월일 또는 소대&생활관을 입력합니다.\n"
                                    + "     예) 493, 민주홍, 911115, 4p12s 등\n"
                                    + "4-2. mgc.txt에서 검색한다면 입력란에 찾고자하는 문자열을 입력합니다.\n"
                                    + "     예) 1115, 주홍, 234, 박 등\n"
                                    + "5. 입력을 마친 뒤에는 Enter키를 눌러서 결과를 확인합니다.\n\n"
                                    + "* 초기화 버튼 클릭시 입력란과 출력된 정보가 사라집니다.\n"
                                    + "* 면담자 설정 버튼을 통해 면담자 번호를 입력할 수 있습니다.\n"
                                    + "  면담자 번호는 \"번호순\"으로 입력해야 합니다.\n"
                                    + "  (단순히 의무대에서 보내온 면담자 리스트를 복사하면 문제 없습니다.)\n"
                                    + "* 면담자 설정이 완료되면 \"면담자 띠 생성\" 버튼을 통해서 MGC 뷰어가 있는 디렉토리에 면담자 띠를 생성할 수 있습니다.\n"
                                    + "\n";
}
