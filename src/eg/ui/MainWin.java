package eg.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSplitPane;

//--Eadgyth--/
import eg.Constants;
import eg.TabbedFiles;
import eg.CurrentProject;
import eg.Edit;
import eg.EditAreaFormat;
import eg.Preferences;
import eg.Languages;
import eg.FunctionalAction;
import eg.edittools.AddableEditTool;
import eg.ui.menu.MenuBar;
import eg.ui.menu.FormatMenu;
import eg.ui.menu.ViewMenu;
import eg.ui.filetree.FileTree;
import eg.ui.tabpane.ExtTabbedPane;
import eg.console.ConsolePanel;
import eg.utils.UiComponents;
import java.awt.event.ActionEvent;

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
   private final JLabel projectLb = new JLabel();
   private final JLabel languageLb = new JLabel();
   private final JLabel cursorPosLb = new JLabel();
   private final JLabel wordwrapLb = new JLabel();

   private final MenuBar menuBar = new MenuBar();
   private final Toolbar toolbar = new Toolbar();
   private final ExtTabbedPane tabPane = UiComponents.extTabbedPane();
   private final FileTree fileTree = new FileTree();
   private final ConsolePanel console = new ConsolePanel();
   private final ToolPanel toolPnl = new ToolPanel();
   private final Preferences prefs = new Preferences();

   private JSplitPane splitHorAll;
   private JSplitPane splitHor;
   private JSplitPane splitVert;
   private int dividerLocVert = 0;
   private int dividerLocHorAll = 0;
   private int dividerLocHor = 0;
   private final String wordwrapOn = "";

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
    * Displays text in the title bar
    *
    * @param title  the title
    */
   public void displayFrameTitle(String title) {
      frame.setTitle(title);
   }
   
   /**
    * Displays the file type, i.e. the display value of the set
    * language, in the status bar
    *
    * @param lang   the language
    */
   public void displayFileType(Languages lang) {
      languageLb.setText("File type: " + lang.display());
   }
   
   /**
    * Displays the project name in the status bar
    *
    * @param projName  the name
    */
   public void displayProjectName(String projName) {
      projectLb.setText("Active project: " + projName); 
   }
   
   /**
    * Displays the line and column number of the cursor position in
    * the status bar
    *
    * @param lineNr  the line number
    * @param colNr  the column number
    */
   public void displayLineAndColNr(int lineNr, int colNr) {
      cursorPosLb.setText("Line " + lineNr + "  Col " + colNr);
   }
   
   /**
    * Labels the menu item for building actions
    *
    * @param label  the label
    */
   public void setBuildLabel(String label) {
      menuBar.projectMenu().setBuildLabel(label);
   }
   
   /**
    * Sets the selection state of the menu item for setting wordwrap
    * actions
    *
    * @param b  true to select, false to unselect the item
    */
   public void setWordWrapSelected(boolean b) {
      menuBar.formatMenu().selectWordWrapItm(b);
      setWordwrapInStatusBar(b);
   }
   
   /**
    * Selects the menu item for the specified language and displays
    * the file type in the status bar
    *
    * @param lang  the language
    * @param b  if the non-selected items are set enabled
    */
   public void setLanguageSelected(Languages lang, boolean b){
      menuBar.editMenu().selectLanguageItm(lang, b);
      displayFileType(lang); 
   }
   
   /**
    * Enables or disables controls for undoing and redoing actions. The
    * booleans indicate if the controls for the respective action are set
    * enabled or disabled
    *
    * @param isUndo  the boolean
    * @param isRedo  the boolean
    */
   public void enableUndoRedo(boolean isUndo, boolean isRedo) {
      toolbar.enableUndoRedoBts(isUndo, isRedo);
      menuBar.editMenu().enableUndoRedoItms(isUndo, isRedo);
   }
   
   /**
    * Enables or disables controls for cutting and copying actions
    *
    * @param b  true to enable, false to disable the controls
    */
   public void enableCutCopy(boolean b) {
      toolbar.enableCutCopyBts(b);
      menuBar.editMenu().enableCutCopyItms(b);
   }
   
   /**
    * Enables or disables the munu item for actions to set the visiblity
    * of the tabbar
    *
    * @param b  true to enable, false to disable the item
    */
   public void enableShowTabbar(boolean b) {
      menuBar.viewMenu().enableTabItm(b);
   }
   
   /**
    * Enables the menu item for actions to open the fileview panel
    */
   public void enableOpenFileView() {
      menuBar.viewMenu().enableFileViewItm();
   }
   
   /**
    * Enables or disables the controls for actions to change project
    *
    * @param b  true to enable, false to disable the controls
    */
   public void enableChangeProject(boolean b) {
      menuBar.projectMenu().enableChangeProjItm(b);
      toolbar.enableChangeProjBt(b);
   }
   
   /**
    * Enables or disables the controls for actions to compile, run and
    * build a project. The booleans indicate if the respective controls are
    * set enabled or disabled.
    *
    * @param isCompile  the boolean
    * @param isRun  the boolean
    * @param isBuild  the boolean
    */
   public void enableSrcCodeActions(boolean isCompile, boolean isRun,
         boolean isBuild) {

      menuBar.projectMenu().enableSrcCodeActionItms(isCompile, isRun,
            isBuild);
      toolbar.enableSrcCodeActionBts(isCompile, isRun);
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
    * @param edit the reference to {@link Edit}
    */
   public void setEditTextActions(Edit edit) {
      toolbar.setEditTextActions(edit);
      menuBar.editMenu().setEditTextActions(edit);
   }
   
   /**
    * Sets the listener for opening the ith edit tool of the tools
    * in <code>EditTools</code>
    *
    * @param tool  the tool
    * @param i  the index
    * @see eg.edittools.EditTools
    */
   public void setEditToolsActions(AddableEditTool tool, int i) {
      JButton closeBt = new JButton();
      closeBt.setAction(new FunctionalAction("", IconFiles.CLOSE_ICON,
            e -> showToolPnl(false)));
      tool.createToolPanel(closeBt);

      winListen(new WindowAdapter() {

         @Override
         public void windowClosing(WindowEvent we) {
            tool.end();
         }
      });

      menuBar.editMenu().setEditToolsActions(
            e -> {
               toolPnl.addComponent(tool.toolComponent());
               showToolPnl(true);
            }, i);               
   }    
   
   /**
    * Sets the listener for actions to opens the window for view
    * settings
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
      fm.setChangeWordWrapAct((ActionEvent e) -> {
         boolean isWordwrap = fm.isWordWrapItmSelected();
         format.changeWordWrap(isWordwrap);
         setWordwrapInStatusBar(isWordwrap);
      });
      fm.setFontAction(e -> format.makeFontSettingWinVisible());
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
   //--private methods--/
   //
   
   private void setViewActions() {
      ViewMenu vm = menuBar.viewMenu();
      vm.setConsoleItmAction(e -> showConsole(vm.isConsoleItmSelected()));
      vm.setFileViewItmAction(e -> showFileView(vm.isFileViewItmSelected()));
      vm.setTabItmAction(e -> showTabbar(vm.isTabItmSelected()));
      fileTree.closeAct(e -> vm.doUnselectFileViewAct());
      console.closeAct(e -> vm.doConsoleItmAct(false));
      toolPnl.closeAct(e -> showToolPnl(false));
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
   
   public void showToolPnl(boolean b) {
      if (b) {
         splitHorAll.setDividerSize(6);
         splitHorAll.setRightComponent(toolPnl.panel());
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
   
   private void setWordwrapInStatusBar(boolean isWordwrap) {
      if (isWordwrap) {
         cursorPosLb.setForeground(Constants.GRAY);
         wordwrapLb.setText("Word-wrap ");
      }
      else {
         cursorPosLb.setForeground(Color.BLACK);
         wordwrapLb.setText("");
      }
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
      splitVert.setResizeWeight(0);
      splitVert.setBorder(null);
      splitHorAll = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
            splitVert, null);
      splitHorAll.setResizeWeight(0);
      splitHorAll.setDividerSize(0);
      splitHorAll.setBorder(null);
   }

   private void initStatusbar() {
      int lbHeight = 15;
      Dimension width5   = UiComponents.scaledDimension(5, lbHeight);
      Dimension width20  = UiComponents.scaledDimension(20, lbHeight);
      Dimension width100 = UiComponents.scaledDimension(100, lbHeight);
      Dimension width150 = UiComponents.scaledDimension(150, lbHeight);
      Dimension width200 = UiComponents.scaledDimension(200, lbHeight);
      JLabel[] lbArr = { languageLb, projectLb, cursorPosLb, wordwrapLb };
      setLbFont(lbArr);
      setLbWidth(languageLb, width100);
      setLbWidth(projectLb, width200);
      setLbWidth(cursorPosLb, width150);
      statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.LINE_AXIS));
      statusBar.add(Box.createRigidArea(width5));
      statusBar.add(languageLb);
      statusBar.add(Box.createRigidArea(width20));
      statusBar.add(projectLb);
      statusBar.add(Box.createRigidArea(width20));
      statusBar.add(wordwrapLb);
      statusBar.add(Box.createRigidArea(width5));
      statusBar.add(cursorPosLb);
      displayProjectName("none");
   }
   
   private void setLbFont(JLabel[] lb) {
      for (JLabel l : lb) {
         l.setFont(Constants.VERDANA_PLAIN_8);
      }
   }
   
   private void setLbWidth(JLabel lb, Dimension dim) {
      lb.setPreferredSize(dim);
      lb.setMinimumSize(dim);
      lb.setMaximumSize(dim);
   }
}
