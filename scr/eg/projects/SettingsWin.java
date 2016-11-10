package eg.projects;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//--Eadgyth--//
import eg.ui.IconFiles;

/**
 * The window for entering the configuration of a new project
 */
public class SettingsWin {

   private JFrame frame = new JFrame("Eadgyth - Project settings"); 

   // Components for project settings (main, package with main, sources, bin)   
   private JTextField   mainFileTf   = new JTextField();
   private JTextField   moduleTf     = new JTextField("");
   private JTextField   sourcesDirTf = new JTextField();      
   private JTextField   execDirTf    = new JTextField();
   private JTextField   argsTf       = new JTextField("");
   private JTextField   buildNameTf  = new JTextField();

   private JButton okBt     = new JButton("   OK   ");
   private JButton cancelBt = new JButton("Cancel");

   private Font fontPlain = new Font("SansSerif", Font.PLAIN, 12);
   private Font fontBold  = new Font("SansSerif", Font.BOLD, 12);

   private final Dimension dTextField = new Dimension(300, 22);
   
   /**
    * @param buildKind  the name for the kind of build
    */
   public SettingsWin(String fileKind, String moduleKind, boolean useArgs,
         String buildKind) {
      setWindow(fileKind, moduleKind, useArgs, buildKind);
   }
   
   /**
    * Makes this frame visible
    */
   public void makeVisible(boolean isVisible) {
      mainFileTf.requestFocus();
      frame.setVisible(isVisible);
   }      
   
   /**
    * Adds action event handler to this ok button
    */
   public void okAct(ActionListener al) {
      okBt.addActionListener(al);
   }
   
   //
   //-- inputs in text fields
   //
   String mainFileIn() {
      return mainFileTf.getText();
   }
   
   String moduleIn() {
      return moduleTf.getText();
   }
   
   public String sourcesDirIn() {
      return sourcesDirTf.getText();
   }
   
   public String execDirIn() {
      return execDirTf.getText();
   }
   
   public String argsIn() {
      return argsTf.getText();
   }
   
   public String buildNameIn() {
      return buildNameTf.getText();
   }
   
   //
   // display text in text fields
   //
   
   
   public void displayMainFile(String in) {
      mainFileTf.setText(in);
   }
   
   public void displayModule(String in) {
      moduleTf.setText(in);
   }

   /**
    * Shows the name of the directory containin source files
    */
   public void displaySourcesDir(String in) {
      sourcesDirTf.setText(in);
   }
   
   /**
    * Shows the name of the directory containin executable files
    */
   public void displayExecDir(String in) {
      execDirTf.setText(in);
   }

   public void resetArgsTf() {
      argsTf.setText("");
   } 
   
   private JPanel projectPanel(String fileKind, String moduleKind) {
      JPanel proj = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      proj.setPreferredSize(new Dimension(550, 150));
      
      // labels
      JLabel mainClassLb = new JLabel(fileKind + ":");
      mainClassLb.setFont(fontBold);
      mainFileTf.setFont(fontPlain);
      mainFileTf.setPreferredSize(dTextField);
      proj.add(mainClassLb);
      proj.add(mainFileTf);
      
      if (moduleKind != null) {
         JLabel packageLb = new JLabel(moduleKind
               + " containing the " + fileKind.toLowerCase() + ":");
         packageLb.setFont(fontBold);
         JLabel sourcesDirLb = new JLabel("Sources directory:");
         sourcesDirLb.setFont(fontBold);
         JLabel classesDirLb = new JLabel("Executables directory:");
         classesDirLb.setFont(fontBold);
   
         moduleTf.setFont(fontPlain);
         moduleTf.setPreferredSize(dTextField);
         sourcesDirTf.setFont(fontPlain);
         sourcesDirTf.setPreferredSize(dTextField);
         execDirTf.setFont(fontPlain);
         execDirTf.setPreferredSize(dTextField);
   
         proj.add(packageLb);
         proj.add(moduleTf);
         proj.add(sourcesDirLb);
         proj.add(sourcesDirTf);
         proj.add(classesDirLb);
         proj.add(execDirTf);
      }
      
      TitledBorder projTitle = BorderFactory.createTitledBorder
            (new LineBorder(Color.BLACK, 1), "Project");
      proj.setBorder(projTitle);
      
      return proj;
   }
   
   private JPanel startCommandPanel() {
      JPanel start = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
      start.setPreferredSize( new Dimension(550, 70));

      JLabel argsLb = new JLabel("Arguments:");
      argsLb.setFont(fontBold);
      argsTf.setPreferredSize(dTextField);
      argsTf.setFont(fontPlain);
      start.add(argsLb);
      start.add(argsTf);
      
      TitledBorder startTitle = BorderFactory.createTitledBorder
           (new LineBorder(Color.BLACK, 1 ), "Arguments for start command");
      start.setBorder(startTitle);
      
      return start;
   }  

   private JPanel buildNamePanel(String buildKind) {
      JPanel buildPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      buildPnl.setPreferredSize(new Dimension(550, 70));
      
      JLabel buildNameLb = new JLabel("Name for " + buildKind +":");
      buildNameLb.setFont(fontBold);
      buildNameTf.setPreferredSize(dTextField);
      buildPnl.add(buildNameLb);
      buildPnl.add(buildNameTf);
      
      TitledBorder buildTitle = BorderFactory.createTitledBorder
            (new LineBorder(Color.BLACK, 1 ), "Build (" + buildKind + ")");
      buildPnl.setBorder(buildTitle);      
      
      return buildPnl;
   }
   
   private JPanel buttonsPanel() {
      JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      buttons.add(okBt);
      buttons.add(cancelBt);
      frame.getRootPane().setDefaultButton(okBt);
      cancelBt.addActionListener(e -> {
         frame.setVisible(false);
      });
      
      return buttons;
   }
   
   private JPanel combineAll(String fileKind, String moduleKind, boolean useArgs,
         String buildKind) {
      JPanel combineAll = new JPanel(new FlowLayout());
      combineAll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      combineAll.add(projectPanel(fileKind, moduleKind));
      if (useArgs) {
         combineAll.add(startCommandPanel());
      }
      if (buildKind != null) {
         combineAll.add(buildNamePanel(buildKind));
      }
      combineAll.add(buttonsPanel());

      return combineAll;
   }

   private void setWindow(String fileKind, String moduleKind, boolean useArgs,
         String buildKind) {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setContentPane(combineAll(fileKind, moduleKind, useArgs, buildKind));
      frame.setSize(600, 400);
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.eadgythIcon.getImage());
   }
}