package eg.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSplitPane;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--/
import eg.Constants;
import eg.TabbedDocuments;
import eg.Projects;
import eg.Edit;
import eg.Formatter;
import eg.Prefs;
import eg.Languages;
import eg.FunctionalAction;
import eg.edittools.*;
import eg.ui.menu.MenuBar;
import eg.ui.menu.FormatMenu;
import eg.ui.menu.ViewMenu;
import eg.ui.filetree.TreePanel;
import eg.ui.filetree.FileTree;
import eg.ui.tabpane.ExtTabbedPane;
import eg.utils.UIComponents;
import eg.utils.ScreenParams;
import eg.utils.FileUtils;

/**
 * The main window
 */
public class MainWin {

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
   private final ExtTabbedPane tabPane = UIComponents.scolledUnfocusableTabPane();
   private final TreePanel treePnl = new TreePanel();
   private final FileTree fileTree;
   private final ConsolePanel consPnl = new ConsolePanel();
   private final EditToolPanel edToolPnl = new EditToolPanel();
   private final List<AddableEditTool> editTools = new ArrayList<>();
   private final Prefs prefs = new Prefs();
   private final ProjectControlsUpdate projControlsUpdate;

   private JSplitPane splitHorAll;
   private JSplitPane splitHor;
   private JSplitPane splitVert;
   private int dividerLocHor;
   private int dividerLocVert = 0;

   public MainWin() {
      fileTree = new FileTree(treePnl);
      createAddableEditTools();
      initFrame();
      setViewActions();
      dividerLocHor =  (int)(frame.getWidth() * 0.25);
      initShowTabbar();
      initShowFileView();
      projControlsUpdate = pcu;
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
    public ConsolePanel consolePnl() {
       return consPnl;
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
     * Gets this List of <code>AddableEditTool</code>
     *
     * @return  this List of type {@link AddableEditTool}
     */
    public List<AddableEditTool> editTools() {
       return editTools;
    }

   /**
    * Gets a new <code>ConsoleOpenable</code>
    *
    * @return  this {@link ConsoleOpenable}
    */
   public ConsoleOpenable consoleOpener() {
      return menuBar.viewMenu().consoleOpener();
   }

   /**
    * Gets this <code>ProjectControlsUpdate</code>
    *
    * @return  this {@link ProjectControlsUpdate}
    */
   public ProjectControlsUpdate projControlsUpdate() {
      return projControlsUpdate;
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
    * Displays the current language in the status bar
    *
    * @param lang   the language which is a constant in {@link Languages}
    */
   public void displayLanguage(Languages lang) {
      languageLb.setText("Language: " + lang.display());
   }

   /**
    * Displays the project name and type in the status bar
    *
    * @param projName  the name
    * @param projType  the type
    */
   public void displayProjectName(String projName, String projType) {
      projectLb.setText("Active project: " + projName + " (" + projType + ")");
   }

   /**
    * Displays the line and column number of the cursor position in
    * the status bar
    *
    * @param lineNr  the line number
    * @param colNr  the column number
    */
   public void displayCursorPosition(int lineNr, int colNr) {
      cursorPosLb.setText("Line " + lineNr + "  Col " + colNr);
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
    * the language in the status bar
    *
    * @param lang  the language
    * @param b  if the non-selected items are set enabled
    */
   public void setLanguageSelected(Languages lang, boolean b){
      menuBar.editMenu().selectLanguageItm(lang, b);
      displayLanguage(lang);
   }

   /**
    * Sets the booleans that specify if undoing and redoing actions
    * are enabled (true) or disabled
    *
    * @param isUndo  the boolean value for undo actions
    * @param isRedo  the boolean value for redo actions
    */
   public void enableUndoRedo(boolean isUndo, boolean isRedo) {
      toolbar.enableUndoRedoBts(isUndo, isRedo);
      menuBar.editMenu().enableUndoRedoItms(isUndo, isRedo);
   }

   /**
    * Sets the boolean that specifies if save actions are enabled (true)
    * or disabled
    *
    * @param b  the boolean value
    */
   public void enableSave(boolean b) {
      toolbar.enableSaveBt(b);
      menuBar.fileMenu().enableSaveItm(b);
   }

   /**
    * Sets the boolean that specifies if cutting and copying actions
    * are enabled (true) or disabled
    *
    * @param b  the boolean value
    */
   public void enableCutCopy(boolean b) {
      toolbar.enableCutCopyBts(b);
      menuBar.editMenu().enableCutCopyItms(b);
   }

   /**
    * Sets the boolean that specifies if actions to set the visiblity
    * of the tabbar are enabled (true) or disabled
    *
    * @param b  the boolean value
    */
   public void enableShowTabbar(boolean b) {
      menuBar.viewMenu().enableTabItm(b);
   }

   /**
    * Sets the boolean that specifies if actions to change project are
    * enabled (true) or disabled
    *
    * @param b  the boolean value
    */
   public void enableChangeProject(boolean b) {
      menuBar.projectMenu().enableChangeProjItm(b);
      toolbar.enableChangeProjBt(b);
   }

   /**
    * Sets the boolean that specifies if actions to open a project's
    * settings window are enabled (true) or disabled
    *
    * @param b  the boolean value
    */
   public void enableOpenProjSetWinActions(boolean b) {
      menuBar.projectMenu().enableOpenSetWinItm(b);
   }

   /**
    * Shows or hides the toolbar
    *
    * @param b  the boolean value that is true to show and false to
    * hide the toolbar
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
    * Shows or hides the statusbar
    *
    * @param b  the boolean value that is true to show and false to
    * hide the statusbar
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
    * Sets listeners for file actions
    *
    * @param td  the reference to {@link TabbedDocuments}
    */
   public void setFileActions(TabbedDocuments td) {
      menuBar.fileMenu().setActions(td);
      menuBar.fileMenu().setExitActions(e -> exit(td));
      menuBar.editMenu().setChangeLanguageActions(td);
      toolbar.setFileActions(td);
      fileTree.addObserver(td);

      frame.addWindowListener(new WindowAdapter() {

         @Override
         public void windowClosing(WindowEvent we) {
            exit(td);
         }
      });
   }

   /**
    * Sets listeners for actions to edit text
    *
    * @param edit  the reference to {@link Edit}
    */
   public void setEditActions(Edit edit) {
      toolbar.setEditActions(edit);
      menuBar.editMenu().setEditActions(edit);
   }

   /**
    * Sets the listener for actions to open the window for view
    * preferences
    *
    * @param viewSetWin  the reference to <code>ViewSettingWin</code>
    */
   public void setViewSettingWinAction(ViewSettingWin viewSetWin) {
      menuBar.viewMenu().openSettingWinItmAction(e ->
            viewSetWin.setVisible(true));
   }

   /**
    * Sets listeners for format actions
    *
    * @param format  the reference to {@link Formatter}
    */
   public void setFormatActions(Formatter format) {
      FormatMenu fm =  menuBar.formatMenu();
      fm.setChangeWordWrapAct(e -> {
         boolean isWordwrap = fm.isWordWrapItmSelected();
         format.enableWordWrap(isWordwrap);
         setWordwrapInStatusBar(isWordwrap);
      });
      fm.setFontAction(e -> format.openSetFontDialog());
   }

   /**
    * Sets listeners for project actions
    *
    * @param p  the reference to {@link Projects}
    */
   public void setProjectActions(Projects p) {
      menuBar.projectMenu().setActions(p);
      toolbar.setProjectActions(p);
   }

   //
   //--private--/
   //

   private void setViewActions() {
      ViewMenu vm = menuBar.viewMenu();
      vm.setConsoleItmAction(e -> showConsole(vm.isConsoleItmSelected()));
      vm.setFileViewItmAction(e -> showFileView(vm.isFileViewItmSelected()));
      vm.setTabItmAction(e -> tabPane.showTabbar(vm.isTabItmSelected()));
      treePnl.setCloseAct(e -> vm.doUnselectFileViewAct());
      consPnl.setCloseAct(e -> vm.doConsoleItmAct(false));
   }

   private void showConsole(boolean b) {
      if (b) {
         splitVert.setDividerSize(6);
         splitVert.setBottomComponent(consPnl.content());
         if (dividerLocVert == 0) {
            dividerLocVert = (int)(frame.getHeight() * 0.6);
         }
         splitVert.setDividerLocation(dividerLocVert);
      }
      else {
         dividerLocVert = splitVert.getDividerLocation();
         splitVert.setDividerSize(0);
         splitVert.setBottomComponent(null);
      }
   }

   private void showFileView(boolean b) {
      if (b) {
         splitHorAll.setDividerSize(6);
         splitHorAll.setLeftComponent(treePnl.content());
         splitHorAll.setDividerLocation(dividerLocHor);
      }
      else {
         dividerLocHor = splitHorAll.getDividerLocation();
         splitHorAll.setDividerSize(0);
         splitHorAll.setLeftComponent(null);
      }
   }

   private void showEditToolPnl(boolean b) {
      if (b) {
         splitHor.setDividerSize(6);
         splitHor.setRightComponent(edToolPnl.panel());
      }
      else {
         splitHor.setDividerSize(0);
         splitHor.setRightComponent(null);
      }
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
      boolean show = "show".equals(prefs.getProperty("Tabbar"));
      tabPane.showTabbar(show);
      menuBar.viewMenu().selectTabsItm(show);
   }
   
   private void initShowFileView() {
      boolean show = "show".equals(prefs.getProperty("FileView"));
      if (show) {
         showFileView(show);
      }
      menuBar.viewMenu().selectFileViewItm(show);
   }

   private void createAddableEditTools() {
      try {
         for (int i = 0; i < EditTools.values().length; i++) {
            editTools.add((AddableEditTool) Class.forName("eg.edittools."
                  + EditTools.values()[i].className()).newInstance());

            setEditToolsActions(editTools.get(i), i);
         }
      }
      catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException e) {

         FileUtils.logStack(e);
      }
   }

   private void setEditToolsActions(AddableEditTool tool, int i) {
      JButton closeBt = new JButton();
      closeBt.setAction(new FunctionalAction("", IconFiles.CLOSE_ICON,
            e -> showEditToolPnl(false)));

      tool.addClosingAction(closeBt);
      menuBar.editMenu().setEditToolsActions(
            e -> {
               edToolPnl.addComponent(tool.toolContent());
               showEditToolPnl(true);
            },
            i);
   }

   private final ProjectControlsUpdate pcu = new ProjectControlsUpdate() {

      @Override
      public void enableProjectActions(boolean isCompile, boolean isRun,
            boolean isBuild) {

         menuBar.projectMenu().enableProjectActionsItms(isCompile, isRun, isBuild);
         toolbar.enableProjectActionsBts(isCompile, isRun);
      }

      @Override
      public void setBuildLabel(String label) {
         menuBar.projectMenu().setBuildLabel(label);
      }
   };
   
   private void exit(TabbedDocuments td) {
      editTools.forEach((t) -> {
         t.end();
      });
      ViewMenu vm = menuBar.viewMenu();
      String state = vm.isTabItmSelected() ? "show" : "hide";
      prefs.setProperty("Tabbar", state);
      state = vm.isFileViewItmSelected() ? "show" : "hide";
      prefs.setProperty("FileView", state);
      if (td.isAllClosed()) {
         prefs.store();
         System.exit(0);
      }
   }

   private void initFrame() {
      initSplitPane();
      initStatusbar();
      frame.setJMenuBar(menuBar.menuBar());
      frame.add(splitHorAll, BorderLayout.CENTER);
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
      frame.setLocation(5, 5);
      Dimension screen = ScreenParams.SCREEN_SIZE;
      frame.setSize(screen.width - screen.width/3, screen.height - screen.height/4);
   }

   private void initSplitPane() {
      splitHor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, tabPane, null);
      splitHor.setDividerSize(0);
      splitHor.setBorder(null);
      splitHor.setResizeWeight(1);
      splitVert = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, splitHor, null);
      splitVert.setDividerSize(0);
      splitVert.setResizeWeight(0);
      splitVert.setBorder(null);
      splitHorAll = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, null,
            splitVert);
      splitHorAll.setResizeWeight(0);
      splitHorAll.setDividerSize(0);
      splitHorAll.setBorder(null);
   }

   private void initStatusbar() {
      int lbHeight = 15;
      Dimension width5   = ScreenParams.scaledDimension(5, lbHeight);
      Dimension width20  = ScreenParams.scaledDimension(20, lbHeight);
      Dimension width100 = ScreenParams.scaledDimension(100, lbHeight);
      Dimension width150 = ScreenParams.scaledDimension(150, lbHeight);
      Dimension width200 = ScreenParams.scaledDimension(200, lbHeight);
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
      projectLb.setText("Active project: none");
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
