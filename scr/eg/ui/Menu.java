package eg.ui;

import java.io.File;
import java.io.IOException;

import java.awt.Toolkit;
import java.awt.Font;
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.border.LineBorder;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.Box;

import java.util.List;

//--Eadgyth--//
import eg.javatools.SearchFiles;
import eg.Preferences;

/**
 * The main menu
 */
public class Menu {

   private final static String[] LANGUAGES = {
      "Plain text", "Java", "HTML"
   };

   private JMenuBar menuMain = new JMenuBar();

   /* File */   
   private JMenu     file             = new JMenu("File");
   private JMenuItem newFileItm       = new JMenuItem("New" );
   private JMenuItem open             = new JMenuItem("Open", IconFiles.openIcon);
   private JMenuItem close            = new JMenuItem("Close", IconFiles.closeIcon);
   private JMenuItem save             = new JMenuItem("Save", IconFiles.saveIcon);
   private JMenuItem saveAll          = new JMenuItem("Save all");
   private JMenuItem saveAs           = new JMenuItem("Save as ...");
   private JMenuItem exit             = new JMenuItem("Exit");

   /* Edit */
   private JMenu     edit             = new JMenu("Edit");
   private JMenuItem undo             = new JMenuItem("Undo", IconFiles.undoIcon);
   private JMenuItem redo             = new JMenuItem("Redo", IconFiles.redoIcon);
   private JMenuItem selectAll        = new JMenuItem("Select all");
   private JMenuItem copy             = new JMenuItem("Copy");
   private JMenuItem paste            = new JMenuItem("Paste");
   private JMenuItem indent           = new JMenuItem("Indent selection more ",
                                                IconFiles.indentIcon);
   private JMenuItem outdent          = new JMenuItem("Indent selection less",
                                                IconFiles.outdentIcon);
   private JMenuItem changeIndent     = new JMenuItem("Indent/outdent length");
   private JMenuItem clearSpaces      = new JMenuItem("Clear spaces");
   private JMenu     language         = new JMenu("Language in new tabs");
   private JCheckBoxMenuItem[]
                     selectLanguage   = new JCheckBoxMenuItem[LANGUAGES.length];
   /* Format */
   private JMenu     format           = new JMenu("Format");
   private JMenuItem font             = new JMenuItem("Font ...");

   /* View */ 
   private JMenu     view             = new JMenu("View");
   private JCheckBoxMenuItem
                     showConsoleItm   = new JCheckBoxMenuItem("Console");
   private JCheckBoxMenuItem
                     fileViewItm      = new JCheckBoxMenuItem("Project explorer");
   private JCheckBoxMenuItem
                     functionItm      = new JCheckBoxMenuItem("Function panel");
   private JMenuItem openViewSettings = new JMenuItem("Other...");

   /* project methods */
   private JMenu     extra            = new JMenu("Project");
   private JMenuItem compile          = new JMenuItem("Save all and compile",
                                               IconFiles.compileIcon);
   private JMenuItem run              = new JMenuItem("Run", IconFiles.runIcon);
   private JMenuItem build            = new JMenuItem("Build");
   private JMenuItem openJavaSetWin   = new JMenuItem("Project settings");
   
   /* plugins */
   private JMenu     plugMenu         = new JMenu("Plugins");
   private JMenu     allPlugsMenu     = new JMenu("Add in function panel");
   private JMenuItem[] selectPlugItm  = null;

   /* Help */ 
   private JMenu     question         = new JMenu("?");
   private JMenuItem about            = new JMenuItem("About Eadgyth");
   private JMenuItem showHelp         = new JMenuItem("Show help");

   private final Preferences prefs = new Preferences();

   public Menu() { 
      menuMain.setOpaque(false);
      menuMain.setBorder(null);

      assembleMenu();
      implActions();
      shortCuts();
      fileViewItm.setEnabled(false);
   }

   JMenuBar getMenuBar() {
      return menuMain;
   }

   //
   //-- add action listeners
   //

   // for file menu
   public void newFileAct(ActionListener al) {
      newFileItm.addActionListener(al);
   }

   public void openAct(ActionListener al) {
      open.addActionListener(al);
   }

   public void saveAct(ActionListener al) {
      save.addActionListener(al);
   }

   public void closeAct(ActionListener al) {
      close.addActionListener(al);
   }

   public void saveAllAct(ActionListener al) {
      saveAll.addActionListener(al);
   }

   public void saveAsAct(ActionListener al) {
      saveAs.addActionListener(al);
   }

   public void exitAct(ActionListener al) {
      exit.addActionListener(al);
   }

   // edit
   public void undoAct(ActionListener al) {
      undo.addActionListener(al);
   }

   public void redoAct(ActionListener al) {
      redo.addActionListener(al);
   }

   public void selectAllAct(ActionListener al) {
      selectAll.addActionListener(al);
   }

   public void copyAct(ActionListener al) {
      copy.addActionListener(al);
   }

   public void pasteAct(ActionListener al) {
      paste.addActionListener(al);
   }

   public void indentAct(ActionListener al) {
      indent.addActionListener(al);
   }

   public void outdentAct(ActionListener al) {
      outdent.addActionListener(al);
   }

   public void clearSpacesAct(ActionListener al) {
      clearSpaces.addActionListener(al);
   }

   public void changeIndentAct(ActionListener al) {
      changeIndent.addActionListener(al);
   }

   public void languageAct(ActionListener al) {
      for (int i = 0; i < selectLanguage.length; i++) {
         selectLanguage[i].addActionListener(al);
      }
   }
   
   public void selectPlugAct(ActionListener al) {
      if (selectPlugItm != null) {
         for (int i = 0; i < selectPlugItm.length; i++) {
            selectPlugItm[i].addActionListener(al);
         }
      }
   }

   // format
   public void fontAct(ActionListener al) {
      font.addActionListener(al);
   }

   // view
   public void showConsoleAct(ActionListener al) {
      showConsoleItm.addActionListener(al);
   }

  public void showFileViewAct(ActionListener al) {
      fileViewItm.addActionListener(al);
   }

   public void showFunctionPnlAct(ActionListener al) {
      functionItm.addActionListener(al);
   }

   public void openViewSettingsAct(ActionListener al) {
      openViewSettings.addActionListener(al);
   }

   // project
   public void compileAct(ActionListener al) {
      compile.addActionListener(al);
   }

   void runAct(ActionListener al) {
      run.addActionListener(al);
   }

   void buildAct(ActionListener al) {
      build.addActionListener(al);
   }

   public void openJavaSetWinAct(ActionListener al) {
      openJavaSetWin.addActionListener(al);
   }

   //
   // --get values
   //

   /**
    * @return  the language selected by the language menu
    */
   public String getNewLanguage(ActionEvent e) {
      String newLanguage = null;

      for (int i = 0; i < selectLanguage.length; i++) {
         if (e.getSource() == selectLanguage[i]) {
            newLanguage = LANGUAGES[i];
         }
         else selectLanguage[i].setState(false);
      }
      return newLanguage;
   }
   
   /**
    * @return  the index of the plugin selected in the menu
    */
   public int getPluginIndex(ActionEvent e) {
      int pluginIndex = 0;
      for (int i = 0; i < selectPlugItm.length; i++) {
         if (e.getSource() == selectPlugItm[i]) {
            pluginIndex = i;
         }
      }
      return pluginIndex;
   }

   /**
    * @return  true if this checkbox menu item for showing the console
    * is selected
    */
   public boolean isConsoleSelected() {
      return showConsoleItm.getState();
   }

   /**
    * @return  true if this checkbox menu item for showing the file explorer
    * is selected
    */
   public boolean isFileViewSelected() {
      return fileViewItm.getState();
   }

   /**
    * @return  true if this checkbox menu item for showing the function panel
    * is selected
    */
   public boolean isFunctionPnlSelected() {
      return functionItm.getState();
   }

   //
   //--change state of items
   //
   public void selectShowConsole(boolean select) {
      showConsoleItm.setState(select);
   }

   public void selectShowFileView(boolean select) {
      fileViewItm.setState(select);
   }

   public void selectFunctionPnl(boolean select) {
      functionItm.setState(select);
   }

   /**
    * Enables to open the file explorer
    * @param isEnabled  true to enable to open the file explorer 
    */
   public void enableFileViewItm(boolean isEnabled) {
      fileViewItm.setEnabled(isEnabled);
   }

   void enableExtra(boolean isCompile, boolean isRun, boolean isBuild) {
      compile.setEnabled(isCompile);
      run.setEnabled(isRun);
      build.setEnabled(isBuild);      
   }

   //
   // -- private
   //

   private void implActions() {
      about.addActionListener(e -> new InfoWin());      
      showHelp.addActionListener(e -> new Help());
   }

   private void assembleMenu() {

      // file
      file.add(newFileItm);
      file.add(open);
      file.add(close);
      file.addSeparator();
      file.add(save);
      file.add(saveAll);
      file.add(saveAs);
      file.addSeparator();
      file.add(exit);
      menuMain.add(file);
      menuMain.add(Box.createHorizontalStrut(5));

      // edit
      edit.add(undo);
      edit.add(redo);
      edit.addSeparator();
      edit.add(selectAll);
      edit.add(copy);
      edit.add(paste );
      edit.addSeparator();
      edit.add(indent);
      edit.add(outdent);
      edit.add(changeIndent);
      edit.add(clearSpaces);
      edit.addSeparator();
      prefs.readPrefs();
      for (int i = 0; i < selectLanguage.length; i++) {
         selectLanguage[i] = new JCheckBoxMenuItem(LANGUAGES[i]);
         if (prefs.prop.getProperty("language").equals(LANGUAGES[i])) {
            selectLanguage[i].setState(true);
         }
      }
      edit.add(language);
      for (int i = 0; i < selectLanguage.length; i++) {
         language.add(selectLanguage[i]);
      }
      menuMain.add(edit);
      menuMain.add(Box.createHorizontalStrut(5));

      // format
      format.add(font);
      menuMain.add(format);
      menuMain.add(Box.createHorizontalStrut(5));

      // view
      view.add(showConsoleItm);
      view.add(fileViewItm);
      view.add(functionItm);
      view.addSeparator();
      view.add(openViewSettings);
      menuMain.add(view);    
      menuMain.add(Box.createHorizontalStrut(5));
      
      // plugins
      plugMenu.add(allPlugsMenu);
      File[] plugJars = null;
      plugJars = new SearchFiles().filteredFilesToArr("./Plugins", ".jar");
      if (plugJars != null) {
         selectPlugItm = new JMenuItem[plugJars.length];
         for (int i = 0; i < plugJars.length; i++) {
            selectPlugItm[i] = new JMenuItem(plugJars[i].getName());
            allPlugsMenu.add(selectPlugItm[i]);
         }
      }
      menuMain.add(plugMenu);

      // extra
      extra.add(compile);
      extra.add(run);
      extra.addSeparator();
      extra.add(build);
      extra.addSeparator();
      extra.add(openJavaSetWin);
      menuMain.add(extra);    
      menuMain.add(Box.createHorizontalStrut(8));

      // question
      question.add(about);
      question.add(showHelp);    
      menuMain.add(question);
   }

   private void shortCuts() {

      // shortcutKeyMask determines that action would need pressing CTRL 
      copy.setAccelerator(KeyStroke.getKeyStroke('C',
         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      paste.setAccelerator(KeyStroke.getKeyStroke('V',
         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      open.setAccelerator(KeyStroke.getKeyStroke("control O"));
      newFileItm.setAccelerator(KeyStroke.getKeyStroke("control N"));
      save.setAccelerator(KeyStroke.getKeyStroke("control S"));
      indent.setAccelerator(KeyStroke.getKeyStroke("control R"));
      outdent.setAccelerator(KeyStroke.getKeyStroke("control L"));
      compile.setAccelerator(KeyStroke.getKeyStroke("control K"));
      run.setAccelerator(KeyStroke.getKeyStroke("control E"));
   }
}