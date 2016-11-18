package eg.ui;

import java.io.File;

import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;
import javax.swing.Box;

//--Eadgyth--//
import eg.javatools.SearchFiles;
import eg.Preferences;
import eg.TabActions;
import eg.Edit;
import eg.Languages;

/**
 * The main menu
 */
public class Menu {

   private final static String[] LANGUAGES = {
      "Plain text", "Java", "HTML"
   };

   private final JMenuBar menuMain = new JMenuBar();

   /* File */   
   private final JMenu     file             = new JMenu("File");
   private final JMenuItem newFileItm       = new JMenuItem("New" );
   private final JMenuItem open             = new JMenuItem("Open", IconFiles.openIcon);
   private final JMenuItem close            = new JMenuItem("Close", IconFiles.closeIcon);
   private final JMenuItem closeAll         = new JMenuItem("Close all");
   private final JMenuItem save             = new JMenuItem("Save", IconFiles.saveIcon);
   private final JMenuItem saveAll          = new JMenuItem("Save all");
   private final JMenuItem saveAs           = new JMenuItem("Save as ...");
   private final JMenuItem exit             = new JMenuItem("Exit");

   /* Edit */
   private final JMenu     edit             = new JMenu("Edit");
   private final JMenuItem undo             = new JMenuItem("Undo", IconFiles.undoIcon);
   private final JMenuItem redo             = new JMenuItem("Redo", IconFiles.redoIcon);
   private final JMenuItem selectAll        = new JMenuItem("Select all");
   private final JMenuItem copy             = new JMenuItem("Copy");
   private final JMenuItem paste            = new JMenuItem("Paste");
   private final JMenuItem indent           = new JMenuItem("Indent selection more ",
                                                  IconFiles.indentIcon);
   private final JMenuItem outdent          = new JMenuItem("Indent selection less",
                                                  IconFiles.outdentIcon);
   private final JMenuItem changeIndent     = new JMenuItem("Indent/outdent length");
   private final JMenuItem clearSpaces      = new JMenuItem("Clear spaces");
   private final JMenu     language         = new JMenu("Language in new tabs");
   private final JCheckBoxMenuItem[]
                           selectLanguage   = new JCheckBoxMenuItem[LANGUAGES.length];
   /* Format */
   private final JMenu     format           = new JMenu("Format");
   private final JMenuItem font             = new JMenuItem("Font ...");

   /* View */ 
   private final JMenu     view             = new JMenu("View");
   private final JCheckBoxMenuItem
                           showConsoleItm   = new JCheckBoxMenuItem("Console");
   private final JCheckBoxMenuItem
                           fileViewItm      = new JCheckBoxMenuItem("Project explorer");
   private final JCheckBoxMenuItem
                           functionItm      = new JCheckBoxMenuItem("Function panel");
   private final JMenuItem openViewSettings = new JMenuItem("Other...");

   /* project methods */
   private final JMenu     extra            = new JMenu("Project");
   private final JMenuItem compile          = new JMenuItem("Save all and compile",
                                                  IconFiles.compileIcon);
   private final JMenuItem run              = new JMenuItem("Run", IconFiles.runIcon);
   private final JMenuItem build            = new JMenuItem("Build");
   private final JMenuItem projectSetWin    = new JMenuItem("Project settings");
   
   /* plugins */
   private final JMenu     plugMenu         = new JMenu("Plugins");
   private final JMenu     allPlugsMenu     = new JMenu("Add in function panel");
   private JMenuItem[]     selectPlugItm    = null;

   /* Help */ 
   private final JMenu     question         = new JMenu("?");
   private final JMenuItem about            = new JMenuItem("About Eadgyth");
   private final JMenuItem showHelp         = new JMenuItem("Show help");

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

   public void registerTabActions(TabActions ta) {
      newFileItm.addActionListener(e -> ta.newEmptyTab());
      open.addActionListener(e -> ta.openFileByChooser());
      close.addActionListener(e -> ta.tryClose());
      closeAll.addActionListener(e -> ta.tryCloseAll());
      save.addActionListener(e -> ta.saveOrSaveAs());     
      saveAll.addActionListener(e -> ta.saveAll());
      saveAs.addActionListener(e -> ta.saveAs());      
      exit.addActionListener(e -> ta.tryExit());
      projectSetWin.addActionListener(e -> ta.openProjectSetWin());
      compile.addActionListener(e -> ta.saveAndCompile());
      run.addActionListener(e -> ta.runProj());
      build.addActionListener(e -> ta.buildProj());
   }
   
   public void registerEdit(Edit edit) {
      undo.addActionListener(e -> edit.undo());
      redo.addActionListener(e -> edit.redo());
      selectAll.addActionListener(e -> edit.selectAll());
      copy.addActionListener(e -> edit.setClipboard());  
      paste.addActionListener(e -> edit.pasteText());   
      indent.addActionListener(e -> edit.indentSelection());
      outdent.addActionListener(e -> edit.outdentSelection());
      clearSpaces.addActionListener(e -> edit.clearSpaces());
      changeIndent.addActionListener(e -> edit.setNewIndentUnit());
      for (int i = 0; i < selectLanguage.length; i++) {
         selectLanguage[i].addActionListener(e ->
               edit.changeLanguage(getNewLanguage(e)));
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
   
   public void selectPlugAct(ActionListener al) {
      if (selectPlugItm != null) {
         for (int i = 0; i < selectPlugItm.length; i++) {
            selectPlugItm[i].addActionListener(al);
         }
      }
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
      file.add(closeAll);
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
         if (prefs.prop.getProperty("language").equals(eg.Languages.values()[i].toString())) {
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
      fileViewItm.setEnabled(false);
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

      // project
      extra.add(compile);
      compile.setEnabled(false);
      extra.add(run);
      run.setEnabled(false);
      extra.addSeparator();
      extra.add(build);
      build.setEnabled(false);
      extra.addSeparator();
      extra.add(projectSetWin);
      menuMain.add(extra);    
      menuMain.add(Box.createHorizontalStrut(8));

      // question
      question.add(about);
      question.add(showHelp);    
      menuMain.add(question);
   }

   private Languages getNewLanguage(ActionEvent e) {
      Languages lang = null;
      for (int i = 0; i < selectLanguage.length; i++) {
         if (e.getSource() == selectLanguage[i]) {
            lang = Languages.values()[i];
         }
         else selectLanguage[i].setState(false);
      }
      return lang;
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