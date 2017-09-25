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

   private final Menu menu = new Menu();
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
      registerViewAct();
      
      prefs.readPrefs();
      boolean isShowTabs = "show".equals(prefs.getProperty("showTabs"));
      showTabbar(isShowTabs);
      setShowTabbarSelected(isShowTabs);
   }
   
   @Override
   public boolean isConsoleOpen() {
      return menu.viewMenu().isConsoleItmSelected();
   }
   
   @Override
   public void openConsole() {
      menu.viewMenu().doConsoleItmAct(true);
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
    * Enables/disables undo and redo actions
    *
    * @param enableUndo  if undo action is enabled
    * @param enableRedo  if redo action is enabled
    */
   public void enableUndoRedo(boolean enableUndo, boolean enableRedo) {
      toolbar.enableUndoRedoBts(enableUndo, enableRedo);
      menu.editMenu().enableUndoRedoItms(enableUndo, enableRedo);
   }
   
   /**
    * Enables/disables cut and copy actions
    *
    * @param isEnabled  if cut and copy actions are enabled
    */
   public void enableCutCopy(boolean isEnabled) {
      toolbar.enableCutCopyBts(isEnabled);
      menu.editMenu().enableCutCopyItms(isEnabled);
   }
   
   /**
    * Sets the selection state of the menu item to set wordwrap
    *
    * @param isSelected   true to select state the menu item
    * to set wordwrap, false to unselect
    */
   public void setWordWrapSelected(boolean isSelected) {
      menu.formatMenu().selectWordWrapItm(isSelected);
   }
   
   /**
    * Enabled/disables the action to select if the tabbar is visible
    *
    * @param isEnabled  if the action to select if the tabbar is
    * visible
    */
   public void enableShowHideTabbar(boolean isEnabled) {
      menu.viewMenu().enableTabItm(isEnabled);
   }
   
   /**
    * Sets the selection state of the menu item for selecting if the
    * tabbar is visible
    *
    * @param isSelected  true to select the menu item for selecting if the
    * tabbar is visible, false to unselect
    */
   public void setShowTabbarSelected(boolean isSelected) {
      menu.viewMenu().selectTabsItm(isSelected);
   }
   
  /**
    * Sets the selection state of the menu items for the language
    *
    * @param lang  the language that has one of the constant values in
    * {@link Languages}
    * @param selectable  true to enable non-selected items to be selectable
    */
   public void setLanguagesSelected(Languages lang, boolean selectable){
      menu.editMenu().setLanguagesItms(lang, selectable);
   }
   
   /**
    * Enables to open the fileview
    */
   public void enableOpenFileView() {
      menu.viewMenu().enableFileViewItm();
   }
   
   /**
    * Enables/disables the action to change project
    *
    * @param isEnabled  if changing project is enabled
    */
   public void enableChangeProject(boolean isEnabled) {
      menu.projectMenu().enableChangeProjItm(isEnabled);
      toolbar.enableChangeProjBt(isEnabled);
   }
   
   /**
    * Enables/disables actions for a project
    *
    * @param isCompile  if the compile action is enabled
    * @param isRun  if the run action is enabled
    * @param isBuild  if the build action is enabled
    */
   public void enableProjActions(boolean isCompile, boolean isRun,
         boolean isBuild) {

      menu.projectMenu().enableProjItms(isCompile, isRun, isBuild);
      toolbar.enableProjBts(isCompile, isRun);
   }
 
   /**
    * Sets the build name in the menu item for the build action
    *
    * @param buildName  the name for a build
    */
   public void setBuildName(String buildName) {
      menu.projectMenu().setBuildLabel(buildName);
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
    * Sets a busy or default cursor
    *
    * @param b  true to set the wait cursor, false to set
    * the default cursor
    */
   public void setBusyCursor(boolean b) {
      if (b) {
         Component glassPane = frame.getGlassPane();
         glassPane.setVisible(true);
         glassPane.setCursor(BUSY_CURSOR);
      }
      else {
         Component glassPane = frame.getGlassPane();
         glassPane.setVisible(false);
         glassPane.setCursor(DEF_CURSOR);
      }
   }

   /**
    * Adds a {@code WindowListener} to this JFrame
    *
    * @param wl  the {@code WindowListener}
    */
   public void winListen(WindowListener wl) {
      frame.addWindowListener(wl);
   }

   /**
    * Registers listeners for file actions
    *
    * @param tf  the reference to {@link TabbedFiles}
    */
   public void registerFileAct(TabbedFiles tf) {
      menu.fileMenu().registerAct(tf);
      menu.editMenu().registerChangeLanguageAct(tf);
      toolbar.registerFileAct(tf);
      fileTree.addObserver(tf);
   }

   /**
    * Registers listeners for edit actions
    *
    * @param ed  the reference to {@link Edit}
    */
   public void registerEditTextAct(Edit ed) {
      toolbar.registerEditTextAct(ed);
      menu.editMenu().registerEditTextAct(ed);
   }
   
   /**
    * Adds the listener to open the window for view settings
    *
    * @param al  the <code>ActionListener</code>
    */
   public void openViewSettingWinAct(ActionListener al) {
      menu.viewMenu().openSettingWinItmAct(al);
   }
      
   /**
    * Registers listeners for format actions
    *
    * @param format  the reference to {@link EditAreaFormat}
    */
   public void registerFormatAct(EditAreaFormat format) {
      FormatMenu fm =  menu.formatMenu();
      fm.changeWordWrapAct(e -> format.changeWordWrap(fm.isWordWrapItmSelected()));
      fm.fontAct(e -> format.makeFontSettingWinVisible());
   }

   /**
    * Registers listener for plugin actions
    *
    * @param plugSt  the reference to {@link PluginStarter}
    */
   public void registerPlugAct(PluginStarter plugSt) {
      menu.pluginMenu().startPlugin(plugSt, menu.viewMenu());
   }

   /**
    * Registers listeners for project actions
    *
    * @param cp  the reference to {@link CurrentProject}
    */
   public void registerProjectAct(CurrentProject cp) {
      menu.projectMenu().registerAct(cp);
      toolbar.registerProjectAct(cp);
   }

   //
   //--private methods
   //
   
   private void registerViewAct() {
      ViewMenu vm = menu.viewMenu();
      vm.consoleItmAct(e -> showConsole(vm.isConsoleItmSelected()));
      vm.fileViewItmAct(e -> showFileView(vm.isFileViewItmSelected()));
      vm.functionItmAct(e -> showFunctionPnl(vm.isFunctionItmSelected()));
      vm.tabItmAct(e -> showTabbar(vm.isTabItmSelected()));
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

   private void initFrame() {
      initSplitPane();
      initStatusbar();
      frame.setJMenuBar(menu.menubar());
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
