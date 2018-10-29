package eg.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--/
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
import eg.ui.tabpane.ExtTabbedPane;
import eg.utils.UIComponents;
import eg.utils.ScreenParams;
import eg.utils.FileUtils;

/**
 * The main window
 */
public class MainWin {

   public final static Cursor BUSY_CURSOR
         = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

   public final static Cursor DEF_CURSOR
         = Cursor.getDefaultCursor();

   private final static int DIVIDER_SIZE = 6;

   private final JFrame frame = new JFrame();
   private final Component glassPane = frame.getGlassPane();
   private final MenuBar menuBar = new MenuBar();
   private final ToolBar toolBar = new ToolBar();
   private final StatusBar statusBar = new StatusBar();
   private final ExtTabbedPane tabPane = UIComponents.tabPane();
   private final TreePanel treePnl = new TreePanel();
   private final ConsolePanel consPnl = new ConsolePanel();
   private final EditToolPanel edToolPnl = new EditToolPanel();
   private final List<AddableEditTool> editTools = new ArrayList<>();
   private final Prefs prefs = new Prefs();

   private JSplitPane splitHor;
   private JSplitPane splitHorMid;
   private JSplitPane splitVert;
   private int dividerLocHor;
   private int dividerLocHorMid;
   private int dividerLocVert = 0;

   public MainWin() {
      createAddableEditTools();
      initFrame();
      setViewActions();
      dividerLocHor =  (int)(frame.getWidth() * 0.25);
      initShowTabbar();
      initShowFileView();
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
    * @return  the {@link ConsolePanel}
    */
   public ConsolePanel consolePnl() {
      return consPnl;
   }

   /**
    * Gets this <code>TreePanel</code>
    *
    * @return  the {@link TreePanel}
    */
   public TreePanel treePanel() {
      return treePnl;
   }

   /**
    * Gets this List of <code>AddableEditTool</code>
    *
    * @return  the List of type {@link AddableEditTool}
    */
   public List<AddableEditTool> editTools() {
      return editTools;
   }

   /**
    * Gets this <code>ProjectStateUpdate</code>
    *
    * @return  the {@link ProjectStateUpdate}
    */
   public ProjectStateUpdate projectUpdate() {
      return projUpdate;
   }

   /**
    * Gets this <code>ConsoleOpenable</code>
    *
    * @return  the {@link ConsoleOpenable}
    */
   public ConsoleOpenable consoleOpener() {
      return consOpener;
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
    * Displays the line and column number of the cursor position in
    * the status bar
    *
    * @param lineNr  the line number
    * @param colNr  the column number
    */
   public void displayCursorPosition(int lineNr, int colNr) {
      statusBar.displayCursorPosition(lineNr, colNr);
   }

   /**
    * Selects or unselects the menu item for setting wordwrap
    * and diplays in the status bar if wordwrap is switched on
    *
    * @param b  true if wordwrap is switched on, false otherwise
    */
   public void displayWordWrapState(boolean b) {
      menuBar.formatMenu().selectWordWrapItm(b);
      statusBar.displayWordwrapState(b);
   }

   /**
    * Displays the language in the status bar and selects the menu item
    * for the specified language
    *
    * @param lang  the language
    * @param b  true to enable, false to disable the menu items for the
    * other languages
    */
   public void displayLanguage(Languages lang, boolean b) {
      menuBar.editMenu().selectLanguageItm(lang, b);
      displayLanguage(lang);
   }

   /**
    * Displays the language in the status bar
    *
    * @param lang   the language which is a constant in {@link Languages}
    */
   public void displayLanguage(Languages lang) {
      statusBar.displayLanguage(lang.display());
   }

   /**
    * Enables or disables to save text
    *
    * @param b  true to enable, false to disable
    */
   public void enableSave(boolean b) {
      toolBar.enableSaveBt(b);
      menuBar.fileMenu().enableSaveItm(b);
   }

   /**
    * Enables or disables undo/redo. The specified booleans each are
    * true to enable, false to disable
    *
    * @param isUndo  the boolean for undo
    * @param isRedo  the boolean for redo
    */
   public void enableUndoRedo(boolean isUndo, boolean isRedo) {
      toolBar.enableUndoRedoBts(isUndo, isRedo);
      menuBar.editMenu().enableUndoRedoItms(isUndo, isRedo);
   }

   /**
    * Enables or disables to cut and copy text
    *
    * @param b  true to enable, false to disable
    */
   public void enableCutCopy(boolean b) {
      toolBar.enableCutCopyBts(b);
      menuBar.editMenu().enableCutCopyItms(b);
   }

   /**
    * Enables or disables to hide the tabbar
    *
    * @param b  true to enable, false to disable
    */
   public void enableHideTabbar(boolean b) {
      menuBar.viewMenu().enableTabItm(b);
   }

   /**
    * Shows or hides the toolbar
    *
    * @param b  true to show, false to hide
    */
   public void showToolbar(boolean b) {
      if (b) {
         frame.add(toolBar.content(), BorderLayout.NORTH);
      }
      else {
         frame.remove(toolBar.content());
      }
      frame.revalidate();
   }

  /**
    * Shows or hides the statusbar
    *
    * @param b  true to show, false to hide
    */
   public void showStatusbar(boolean b) {
      if (b) {
         frame.add(statusBar.content(), BorderLayout.SOUTH);
      }
      else {
         frame.remove(statusBar.content());
      }
      frame.revalidate();
   }

   /**
    * Sets the busy cursor
    */
   public void setBusyCursor() {
      glassPane.setVisible(true);
      glassPane.setCursor(BUSY_CURSOR);
   }

   /**
    * Sets the default cursor
    */
   public void setDefaultCursor() {
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
      toolBar.setFileActions(td);
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
      BusyActionListener clearSpaces = new BusyActionListener(
            frame, e -> edit.clearTrailingSpaces());

      toolBar.setEditActions(edit);
      menuBar.editMenu().setEditActions(edit, clearSpaces);
   }

   /**
    * Sets the listener for actions to open the window for view
    * settings
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
         statusBar.displayWordwrapState(isWordwrap);
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
      toolBar.setProjectActions(p);
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

   private void setEditToolsActions(AddableEditTool tool, int i) {
      JButton closeBt = new JButton();
      closeBt.setAction(new FunctionalAction("", IconFiles.CLOSE_ICON,
            e -> showEditToolPnl(false, 0)));

      tool.addClosingButton(closeBt);
      ActionListener addEditTool = (ActionEvent e) -> {
         EventQueue.invokeLater(() -> {
            if (edToolPnl.addComponent(tool.content())) {
               splitHorMid.setResizeWeight(tool.resize() ? 0 : 1);
               showEditToolPnl(true, tool.width());
            }
            else {
               showEditToolPnl(true, -1);
            }
         });
      };
      menuBar.editMenu().setEditToolsActions(addEditTool, i);
   }

   private void showConsole(boolean b) {
      if (b) {
         splitVert.setDividerSize(DIVIDER_SIZE);
         splitVert.setBottomComponent(consPnl.content());
         if (dividerLocVert == 0) {
            dividerLocVert = (int)(frame.getHeight() * 0.55);
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
         splitHor.setDividerSize(DIVIDER_SIZE);
         splitHor.setLeftComponent(treePnl.content());
         splitHor.setDividerLocation(dividerLocHor);
      }
      else {
         dividerLocHor = splitHor.getDividerLocation();
         splitHor.setDividerSize(0);
         splitHor.setLeftComponent(null);
      }
   }

   private void showEditToolPnl(boolean b, double width) {
      if (b) {
         splitHorMid.setRightComponent(edToolPnl.content());
         splitHorMid.setDividerSize(DIVIDER_SIZE);
         if (width > 0) {
            double loc =  (1.0 - width / (double) splitHorMid.getWidth());
            splitHorMid.setDividerLocation(loc);
         }
         if (width == -1) {
            splitHorMid.setDividerLocation(dividerLocHorMid);
         }
      }
      else {
         dividerLocHorMid = splitHorMid.getDividerLocation();
         splitHorMid.setDividerSize(0);
         splitHorMid.setRightComponent(null);
      }
   }

   private final ProjectStateUpdate projUpdate = new ProjectStateUpdate() {

      @Override
      public void enableProjectActions(boolean isCompile, boolean isRun,
            boolean isBuild) {

         menuBar.projectMenu().enableProjectActionsItms(isCompile, isRun, isBuild);
         toolBar.enableProjectActionsBts(isCompile, isRun);
      }

      @Override
      public void setBuildLabel(String label) {
         menuBar.projectMenu().setBuildLabel(label);
      }

      @Override
      public void enableOpenSettingsWin(boolean b) {
         menuBar.projectMenu().enableOpenSetWinItm(b);
      }

      @Override
      public void enableChangeProject(boolean b) {
         menuBar.projectMenu().enableChangeProjItm(b);
         toolBar.enableChangeProjBt(b);
      }

      @Override
      public void displayProjectName(String projName, String projType) {
         statusBar.displayProjectName(projName, projType);
      }
   };

   private final ConsoleOpenable consOpener = new ConsoleOpenable() {

      @Override
      public boolean isConsoleOpen() {
         return menuBar.viewMenu().isConsoleItmSelected();
      }

      @Override
      public void openConsole() {
         menuBar.viewMenu().doConsoleItmAct(true);
      }
   };

   private class BusyActionListener implements ActionListener {

      private final ActionListener al;

      BusyActionListener(JFrame f, ActionListener al) {
         this.al = al;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         try {
            setBusyCursor();
            EventQueue.invokeLater(() -> al.actionPerformed(e));
         }
         finally {
            EventQueue.invokeLater(() -> setDefaultCursor());
         }
      }
   }

   private void exit(TabbedDocuments td) {
      editTools.forEach((t) -> {
         t.end();
      });
      ViewMenu vm = menuBar.viewMenu();
      String state = vm.isTabItmSelected() ? "show" : "hide";
      prefs.setProperty("Tabbar", state);
      state = vm.isFileViewItmSelected() ? "show" : "hide";
      prefs.setProperty("FileView", state);
      if (td.closeForExit()) {
         prefs.store();
         System.exit(0);
      }
   }

   private void initFrame() {
      initSplitPane();
      frame.setJMenuBar(menuBar.menuBar());
      frame.add(splitHor, BorderLayout.CENTER);
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
      frame.setLocation(5, 5);
      Dimension screen = ScreenParams.SCREEN_SIZE;
      frame.setSize(screen.width - screen.width/3, screen.height - screen.height/4);
   }

   private void initSplitPane() {
      splitHorMid = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, tabPane, null);
      splitHorMid.setDividerSize(0);
      splitHorMid.setBorder(null);
      splitVert = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, splitHorMid, null);
      splitVert.setDividerSize(0);
      splitVert.setBorder(null);
      splitHor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, null, splitVert);
      splitHor.setDividerSize(0);
      splitHor.setBorder(null);
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
            editTools.add((AddableEditTool) Class.forName(
                  "eg.edittools."
                  + EditTools.values()[i].className()).newInstance());

            setEditToolsActions(editTools.get(i), i);
         }
      }
      catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException e) {

         FileUtils.log(e);
      }
   }
}
