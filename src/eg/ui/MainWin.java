package eg.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

//--Eadgyth--//
import eg.Constants;
import eg.TabbedFiles;
import eg.CurrentProject;
import eg.Edit;
import eg.EditAreaFormat;
import eg.Preferences;
import eg.Languages;

import eg.ui.menu.MenuBar;
import eg.ui.menu.FormatMenu;
import eg.ui.menu.ViewMenu;
import eg.ui.filetree.FileTree;
import eg.ui.tabpane.ExtTabbedPane;

import eg.console.ConsolePanel;
import eg.plugin.PluginStarter;
import eg.utils.UiComponents;

/**
 * The main window
 */
public class MainWin implements ConsoleOpenable {

   private final static Cursor BUSY_CURSOR
         = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
   private final static Cursor DEF_CURSOR
         = Cursor.getDefaultCursor();

   private final JFrame frame = new JFrame();
   private final JPanel statusBar = new JPanel();
   private final JLabel showProjectLb = new JLabel();

   private final MenuBar menuBar = new MenuBar();
   private final Toolbar toolbar = new Toolbar();
   private final ExtTabbedPane tabPane = UiComponents.extTabbedPane();
   private final FileTree fileTree = new FileTree();
   private final ConsolePanel console = new ConsolePanel();
   private final FunctionPanel functPnl = new FunctionPanel();
   private final Preferences prefs = new Preferences();

   private JSplitPane splitHorAll;
   private JSplitPane splitHor;
   private JSplitPane splitVert;
   private int dividerLocVert = 0;
   private int dividerLocHorAll = 0;
   private int dividerLocHor = 0;

   public MainWin() {
      initFrame();
      setViewActions();
      initShowTabbar();
   }
   
   @Override
   public boolean isConsoleOpen() {
      return menuBar.viewMenu().isConsoleItmSelected();
   }
   
   @Override
   public void openConsole() {
      menuBar.viewMenu().doConsoleItmAct(true);
   }

   /**
    * Sets this JFrame visible
    */
   public void makeVisible() {
      frame.setVisible(true);
   }

   /**
    * Gets this <code>ExtTabbedPane</code>
    *
    * @return  this {@link ExtTabbedPane}
    */
   public ExtTabbedPane tabPane() {
      return tabPane;
   }

   /**
    * Gets this <code>ConsolePanel</code>
    *
    * @return  this {@link ConsolePanel}
    */
    public ConsolePanel console() {
       return console;
    }
    
   /**
    * Gets this <code>FileTree</code>
    *
    * @return  this {@link FileTree}
    */
    public FileTree fileTree() {
       return fileTree;
    }

    /**
     * Gets this <code>FunctionPanel</code>
     *
     * @return  this {@link FunctionPanel}
     */
    public FunctionPanel functionPanel() {
       return functPnl;
    }

   /**
    * Displays text in the title bar
    *
    * @param title  the text that is displayed in the title bar
    */
   public void displayFrameTitle(String title) {
      frame.setTitle(title);
   }
   
   /**
    * Sets the project name in the status bar
    *
    * @param name  the name of the project's root directory 
    */
   public void setProjectName(String name) {
      showProjectLb.setText("Project: " + name); 
   }
   
   /**
    * Sets the build name in the menu item for the build action
    *
    * @param buildName  the name for a build
    */
   public void setBuildName(String buildName) {
      menuBar.projectMenu().setBuildLabel(buildName);
   }
   
   /**
    * Sets the selection state of the menu item for wordwrap
    *
    * @param isSelected   true to select state the menu item
    * to set wordwrap, false to unselect
    */
   public void setWordWrapSelected(boolean isSelected) {
      menuBar.formatMenu().selectWordWrapItm(isSelected);
   }
   
   /**
    * Selects the menu item for the specified language and enables the
    * items for the other languages if <code>enable</code> is true
    *
    * @param lang  the language that has one of the constant values in
    * {@link Languages}
    * @param enable  true to enable non-selected items
    */
   public void setLanguageSelected(Languages lang, boolean enable){
      menuBar.editMenu().selectLanguageItm(lang, enable);
   }
   
   /**
    * Enables/disables undo and redo actions
    *
    * @param enableUndo  if undo action is enabled
    * @param enableRedo  if redo action is enabled
    */
   public void enableUndoRedo(boolean enableUndo, boolean enableRedo) {
      toolbar.enableUndoRedoBts(enableUndo, enableRedo);
      menuBar.editMenu().enableUndoRedoItms(enableUndo, enableRedo);
   }
   
   /**
    * Enables/disables cut and copy actions
    *
    * @param isEnabled  if cut and copy actions are enabled
    */
   public void enableCutCopy(boolean isEnabled) {
      toolbar.enableCutCopyBts(isEnabled);
      menuBar.editMenu().enableCutCopyItms(isEnabled);
   }
   
   /**
    * Enabled/disables the action to make the tabbar visible
    *
    * @param isEnabled  if the action to make the tabbar visible
    * is enabled
    */
   public void enableShowTabbar(boolean isEnabled) {
      menuBar.viewMenu().enableTabItm(isEnabled);
   }
   
   /**
    * Enables to open the fileview panel
    */
   public void enableOpenFileView() {
      menuBar.viewMenu().enableFileViewItm();
   }
   
   /**
    * Enables/disables to change project
    *
    * @param isEnabled  if changing project is enabled
    */
   public void enableChangeProject(boolean isEnabled) {
      menuBar.projectMenu().enableChangeProjItm(isEnabled);
      toolbar.enableChangeProjBt(isEnabled);
   }
   
   /**
    * Enables/disables compiling, running and building a project
    *
    * @param isCompile  if the compile action is enabled
    * @param isRun  if the run action is enabled
    * @param isBuild  if the build action is enabled
    */
   public void enableCompileRunBuild(boolean isCompile, boolean isRun,
         boolean isBuild) {

      menuBar.projectMenu().enableCompileRunBuildItms(isCompile, isRun, isBuild);
      toolbar.enableCompileRunBts(isCompile, isRun);
   }

   /**
    * Shows or hides the toolbar
    *
    * @param b  true to show, false to hide the toolbar
    */
   public void showToolbar(boolean b) {
      if (b) {
         frame.add(toolbar.toolbar(), BorderLayout.NORTH);
      }
      else {
         frame.remove(toolbar.toolbar());
      }
      frame.revalidate();
   }

   /**
    * Shows or hides the status bar
    *
    * @param b  true to show, false to hide the status bar
    */
   public void showStatusbar(boolean b) {
      if (b) {
         frame.add(statusBar, BorderLayout.SOUTH);
      }
      else {
         frame.remove(statusBar);
      }
      frame.revalidate();
   }

   /**
    * Sets the busy cursor
    */
   public void setBusyCursor() {
      Component glassPane = frame.getGlassPane();
      glassPane.setVisible(true);
      glassPane.setCursor(BUSY_CURSOR);
   }
   
   /**
    * Sets the default cursor
    */
   public void setDefaultCursor() {
      Component glassPane = frame.getGlassPane();
      glassPane.setVisible(false);
      glassPane.setCursor(DEF_CURSOR);
   }

   /**
    * Adds a <code>WindowListener</code> to this JFrame
    *
    * @param wl  the <code>WindowListener</code>
    */
   public void winListen(WindowListener wl) {
      frame.addWindowListener(wl);
   }

   /**
    * Sets listeners for file actions
    *
    * @param tf  the reference to {@link TabbedFiles}
    */
   public void setFileActions(TabbedFiles tf) {
      menuBar.fileMenu().setActions(tf);
      menuBar.editMenu().setChangeLanguageAction(tf);
      toolbar.setFileActions(tf);
      fileTree.addObserver(tf);
   }

   /**
    * Sets listeners for actions to edit text
    *
    * @param ed  the reference to {@link Edit}
    */
   public void setEditTextActions(Edit ed) {
      toolbar.setEditTextActions(ed);
      menuBar.editMenu().setEditTextActions(ed);
   }
   
   /**
    * Sets the listener that opens the window for view settings
    *
    * @param viewSetWin  the reference to <code>ViewSettingWin</code>
    */
   public void setViewSettingWinAction(ViewSettingWin viewSetWin) {
      menuBar.viewMenu().openSettingWinItmAction(e ->
            viewSetWin.makeVisible(true));
   }
      
   /**
    * Sets listeners for format actions
    *
    * @param format  the reference to {@link EditAreaFormat}
    */
   public void setFormatActions(EditAreaFormat format) {
      FormatMenu fm =  menuBar.formatMenu();
      fm.setChangeWordWrapAct(e ->
            format.changeWordWrap(fm.isWordWrapItmSelected()));
      fm.setFontAction(e -> format.makeFontSettingWinVisible());
   }

   /**
    * Sets the listener for starting a plugin
    *
    * @param plugSt  the reference to {@link PluginStarter}
    */
   public void setPlugAction(PluginStarter plugSt) {
      menuBar.pluginMenu().startPlugin(plugSt, menuBar.viewMenu());
   }

   /**
    * Sets listeners for project actions
    *
    * @param cp  the reference to {@link CurrentProject}
    */
   public void setProjectActions(CurrentProject cp) {
      menuBar.projectMenu().setActions(cp);
      toolbar.setProjectActions(cp);
   }

   //
   //--private methods
   //
   
   private void setViewActions() {
      ViewMenu vm = menuBar.viewMenu();
      vm.setConsoleItmAction(e -> showConsole(vm.isConsoleItmSelected()));
      vm.setFileViewItmAction(e -> showFileView(vm.isFileViewItmSelected()));
      vm.setFunctionItmAction(e -> showFunctionPnl(vm.isFunctionItmSelected()));
      vm.setTabItmAction(e -> showTabbar(vm.isTabItmSelected()));
      fileTree.closeAct(e -> vm.doUnselectFileViewAct());
      console.closeAct(e -> vm.doConsoleItmAct(false));
      functPnl.closeAct(e -> vm.doFunctionItmAct(false));
   }
   
   private void showConsole(boolean b) {
      if (b) {
         splitVert.setDividerSize(6);
         splitVert.setRightComponent(console.consolePnl());
         if (dividerLocVert == 0) {
            dividerLocVert = (int)(frame.getHeight() * 0.65);
         }
         splitVert.setDividerLocation(dividerLocVert);
      }
      else {
         dividerLocVert = splitVert.getDividerLocation();
         splitVert.setDividerSize(0);
         splitVert.setRightComponent(null);
      }
   }
   
   private void showFunctionPnl(boolean b) {
      if (b) {
         splitHorAll.setDividerSize(6);
         splitHorAll.setRightComponent(functPnl.functionPanel());
         if (dividerLocHorAll == 0) {
            dividerLocHorAll = (int)(frame.getWidth() * 0.7);
         }
         splitHorAll.setDividerLocation(dividerLocHorAll);
      }
      else {
         dividerLocHorAll = splitHorAll.getDividerLocation();
         splitHorAll.setDividerSize(0);
         splitHorAll.setRightComponent(null);
      }
   }
   
   private void showFileView(boolean b) {
      if (b) {
         splitHor.setDividerSize(6);
         splitHor.setLeftComponent(fileTree.fileTreePnl());
         if (dividerLocHor == 0) {
            dividerLocHor = (int)(frame.getWidth() * 0.22);
         }
         splitHor.setDividerLocation(dividerLocHor);
      }
      else {
         dividerLocHor = splitHor.getDividerLocation();
         splitHor.setDividerSize(0);
         splitHor.setLeftComponent(null);
      }
   }
   
   private void showTabbar(boolean show) {
      tabPane.showTabbar(show);
      String state = show ? "show" : "hide";
      prefs.storePrefs("showTabs", state);
   }
   
   private void initShowTabbar() {
      prefs.readPrefs();
      boolean show = "show".equals(prefs.getProperty("showTabs"));
      tabPane.showTabbar(show);
      menuBar.viewMenu().selectTabsItm(show);
   }

   private void initFrame() {
      initSplitPane();
      initStatusbar();
      frame.setJMenuBar(menuBar.menuBar());
      frame.add(splitHorAll, BorderLayout.CENTER);
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
      frame.setLocation(5, 5);
      Dimension screen = Constants.SCREEN_SIZE;
      frame.setSize(screen.width - screen.width/3, screen.height - screen.height/4);
   }

   private void initSplitPane() {
      splitHor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
           null, tabPane);
      splitHor.setDividerSize(0);
      splitHor.setBorder(null);
      splitVert = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
           splitHor, null);
      splitVert.setDividerSize(0);
      splitVert.setResizeWeight(1);
      splitVert.setBorder(null);
      splitHorAll = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
            splitVert, null);
      splitHorAll.setResizeWeight(1);
      splitHorAll.setDividerSize(0);
      splitHorAll.setBorder(null);
   }

   private void initStatusbar() {
      showProjectLb.setFont(Constants.VERDANA_PLAIN_8);
      statusBar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
      statusBar.add(showProjectLb);
      showProjectLb.setText("Project: not set");
   }
}
