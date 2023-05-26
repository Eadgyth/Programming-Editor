package eg.ui;

import java.lang.reflect.InvocationTargetException;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;

import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--/
import eg.BackgroundTheme;
import eg.TabbedDocuments;
import eg.Projects;
import eg.Edit;
import eg.Formatter;
import eg.Prefs;
import eg.Languages;
import eg.LanguageChanger;
import eg.FunctionalAction;
import eg.BusyFunction;
import eg.edittools.*;
import eg.ui.menu.MenuBar;
import eg.ui.menu.FormatMenu;
import eg.ui.menu.ViewMenu;
import eg.ui.filetree.TreePanel;
import eg.ui.tabpane.ExtTabbedPane;
import eg.utils.ScreenParams;
import eg.utils.FileUtils;
import eg.projects.ProjectTypes;

/**
 * The main window
 */
public class MainWin {

   private static final int DIVIDER_SIZE = ScreenParams.scaledSize(3);

   private final JFrame frame = new JFrame();

   private final BackgroundTheme theme = BackgroundTheme.givenTheme();
   private final MenuBar menuBar = new MenuBar();
   private final ToolBar toolBar = new ToolBar();
   private final StatusBar statusBar = new StatusBar(theme);
   private final ExtTabbedPane tabPane = UIComponents.tabPane();
   private final TreePanel treePnl = new TreePanel(theme);
   private final ConsolePanel consPnl = new ConsolePanel(theme);
   private final EditToolPanel edToolPnl = new EditToolPanel();
   private final List<AddableEditTool> editTools = new ArrayList<>();
   private final BusyFunction bf;
   private final Prefs prefs = new Prefs();

   private int width;
   private int height;
   private int xLoc;
   private int yLoc;

   private JSplitPane splitHor;
   private JSplitPane splitHorMid;
   private JSplitPane splitVert;
   private int dividerLocHor = ScreenParams.scaledSize(160);
   private int dividerLocVert = 0;

   public MainWin() {
      initAddableEditTools();
      initFrame();
      setViewActions();
      initShowTabbar();
      initShowFileView();
      bf = new BusyFunction(frame);
      frame.addComponentListener(compListener);
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
    * @return  the ExtTabbedPane
    */
   public ExtTabbedPane tabPane() {
      return tabPane;
   }

   /**
    * Gets this <code>ConsolePanel</code>
    *
    * @return  the {@link ConsolePanel}
    */
   public ConsolePanel consolePanel() {
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
    * @return  the List of type AddableEditTool
    */
   public List<AddableEditTool> editTools() {
      return editTools;
   }

   /**
    * Gets this <code>BusyFunction</code>
    *
    * @return  the BusyFunction
    */
   public BusyFunction busyFunction() {
      return bf;
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
    * and displays in the status bar if wordwrap is choosen
    *
    * @param b  true if wordwrap is chosen; false otherwise
    */
   public void displayWordWrapState(boolean b) {
      menuBar.formatMenu().selectWordWrapItm(b);
      statusBar.displayWordwrapState(b);
   }

   /**
    * Selects the menu item for the specified language and displays the
    * language in the status bar
    *
    * @param lang  the language
    */
   public void setLanguageSelected(Languages lang) {
      menuBar.languageMenu().selectLanguageItm(lang);
      displayLanguage(lang);
   }

   /**
    * Displays the language in the status bar
    *
    * @param lang   the language
    */
   public void displayLanguage(Languages lang) {
      statusBar.displayLanguage(lang.display());
   }

   /**
    * Enables or disables to save text
    *
    * @param b  true to enable, false to disbable
    */
   public void enableSave(boolean b) {
      toolBar.enableSaveBt(b);
      menuBar.fileMenu().enableSaveItm(b);
   }

   /**
    * Enables or disables to rename a file
    *
    * @param b  true to enable, false to disable
    */
   public void enableRename(boolean b) {
      menuBar.fileMenu().enableRenameItm(b);
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
    * Displays the project name in the status bar
    *
    * @param projName  the name of the project
    */
   public void displayProjectName(String projName) {
      statusBar.displayProjectName(projName);
   }

   /**
    * Enables or disables to open the project settings window
    * of a project that is newly assigned
    *
    * @param b  true to enable, false disable
    * @param projType  the project type whose corresponding
    * menu item is selected; null to unselect all items
    */
   public void enableAssignProject(boolean b, ProjectTypes projType) {
      menuBar.projectMenu().enableAssignProjectMenu(b, projType);
   }

   /**
    * Enables or disables to open the project settings window of
    * an active project
    *
    * @param b  true to enable, false to disable
    */
   public void enableOpenProjectSettings(boolean b) {
      menuBar.projectMenu().enableOpenSetWinItm(b);
   }

   /**
    * Enables or disables to change project
    *
    * @param b  true to enable, false to disable
    */
   public void enableChangeProject(boolean b) {
      menuBar.projectMenu().enableChangeProjItm(b);
      toolBar.enableChangeProjBt(b);
   }

   /**
    * Enables or disables to compile a project
    *
    * @param b  true enable, false to disable
    */
   public void enableCompileProject(boolean b) {
      menuBar.projectMenu().enableCompileItm(b);
      toolBar.enableCompileBt(b);
   }

   /**
    * Enables or disables to run a project
    *
    * @param b  true enable, false to disable
    * @param label  the label for the corresponding menu item
    * and for the tooltip of the corresponding toolbar button
    */
   public void enableRunProject(boolean b, String label) {
      menuBar.projectMenu().enableRunItm(b, label);
      toolBar.enableRunBt(b, label);
   }

   /**
    * Enables or disables to build a project
    *
    * @param b  true enable, false to disable
    * @param label  the label for the corresponding menu item
    */
   public void enableBuildProject(boolean b, String label) {
      menuBar.projectMenu().enableBuildItm(b, label);
   }

   /**
    * Shows or hides the toolbar
    *
    * @param b  true to show, false to hide
    */
   public void showToolbar(boolean b) {
      if (b) {
         frame.add(toolBar.toolBar(), BorderLayout.NORTH);
      }
      else {
         frame.remove(toolBar.toolBar());
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
    * Shows the console
    */
   public void showConsole() {
      if (!menuBar.viewMenu().isConsoleItmSelected()) {
         menuBar.viewMenu().doConsoleItmAct(true);
      }
   }

   /**
    * Sets listeners for file actions
    *
    * @param td  the reference to {@link TabbedDocuments}
    */
   public void setFileActions(TabbedDocuments td) {
      menuBar.fileMenu().setActions(td);
      menuBar.fileMenu().setExitAction(e -> exit(td));
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
    * @param lc  the reference to {@link LanguageChanger}
    */
   public void setEditActions(Edit edit, LanguageChanger lc) {
      toolBar.setEditActions(edit);
      menuBar.editMenu().setEditActions(edit);
      menuBar.languageMenu().setChangeLanguageActions(lc);
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
      fm.setFontAction(e -> format.openSetFontDialog());
      fm.setChangeWordWrapAct(e -> {
         boolean isWordwrap = fm.isWordWrapItmSelected();
         format.enableWordWrap(isWordwrap);
         statusBar.displayWordwrapState(isWordwrap);
      });
   }

   /**
    * Sets listeners for project actions
    *
    * @param p  the reference to {@link Projects}
    */
   public void setProjectActions(Projects p) {
      menuBar.projectMenu().setActions(p);
      toolBar.setProjectActions(p);
      frame.addWindowListener(new WindowAdapter() {

         @Override
         public void windowActivated(WindowEvent e) {
            if (e.getOppositeWindow() == null) {
               p.updateFileTree();
            }
         }
      });
   }

   //
   //--private--/
   //

   private void setViewActions() {
      ViewMenu vm = menuBar.viewMenu();
      vm.setConsoleItmAction(e -> showConsole(vm.isConsoleItmSelected()));
      vm.setFileViewItmAction(e -> showFileView(vm.isFileViewItmSelected()));
      vm.setTabItmAction(e -> tabPane.showTabbar(vm.isTabItmSelected()));
      treePnl.setClosingAct(new FunctionalAction(
            "", IconFiles.CLOSE_ICON, e -> vm.doUnselectFileViewAct()));

      consPnl.setClosingAct(new FunctionalAction(
            "", IconFiles.CLOSE_ICON, e -> vm.doConsoleItmAct(false)));
   }

   private void setEditToolSelectionActions(int i) {
      ActionListener closeAct = e -> {
         showEditToolPnl(false, 0);
         menuBar.editMenu().unselectEditToolItmAt(i);
      };
      editTools.get(i).addClosingAction(new FunctionalAction(
            "", IconFiles.CLOSE_ICON, closeAct));

      Runnable select = () -> selectEditTool(i);
      ActionListener addAct = e -> bf.execute(select);
      menuBar.editMenu().setEditToolsActionsAt(addAct, i);
   }

   private void selectEditTool(int i) {
      if (menuBar.editMenu().isEditToolItmSelected(i)) {
         AddableEditTool tool = editTools.get(i);
         edToolPnl.addComponent(tool.content());
         splitHorMid.setResizeWeight(tool.resize() ? 0 : 1);
         showEditToolPnl(true, tool.width());
         menuBar.editMenu().unselectEditToolItmExcept(i);
      }
      else {
         showEditToolPnl(false, 0);
         menuBar.editMenu().unselectEditToolItmAt(i);
      }
   }

   private void showEditToolPnl(boolean b, double width) {
      if (b) {
         splitHorMid.setRightComponent(edToolPnl.content());
         splitHorMid.setDividerSize(DIVIDER_SIZE);
         if (width > 0) {
            int currWidth = splitHorMid.getWidth()
                  - ScreenParams.scaledSize(DIVIDER_SIZE); // approximation

            double loc =  1.0 - width / (double) currWidth;
            splitHorMid.setDividerLocation(loc);
         }
      }
      else {
         splitHorMid.setDividerSize(0);
         splitHorMid.setRightComponent(null);
      }
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
         if (dividerLocHor == 0) {
            dividerLocHor = ScreenParams.scaledSize(150);
         }
         splitHor.setDividerLocation(dividerLocHor);
      }
      else {
         dividerLocHor = splitHor.getDividerLocation();
         splitHor.setDividerSize(0);
         splitHor.setLeftComponent(null);
      }
   }

   private void exit(TabbedDocuments td) {
      if (td.closeAllForExit()) {
         editTools.forEach(AddableEditTool::end);
         ViewMenu vm = menuBar.viewMenu();
         prefs.setYesNoProperty(Prefs.TABBAR_KEY, vm.isTabItmSelected());
         prefs.setYesNoProperty(Prefs.FILE_VIEW_KEY, vm.isFileViewItmSelected());
         prefs.setProperty(Prefs.WIN_WIDTH_KEY, String.valueOf(width));
         prefs.setProperty(Prefs.WIN_HEIGHT_KEY, String.valueOf(height));
         prefs.setProperty(Prefs.WIN_XLOC_KEY, String.valueOf(xLoc));
         prefs.setProperty(Prefs.WIN_YLOC_KEY, String.valueOf(yLoc));
         prefs.setYesNoProperty(Prefs.MULTIPLE_SCREENS_KEY,
               ScreenParams.isMultipleScreens());

         prefs.store();
         System.exit(0);
      }
   }

   private void initAddableEditTools() {
      try {
         for (int i = 0; i < EditTools.values().length; i++) {
            editTools.add((AddableEditTool) Class.forName(
                  "eg.edittools."
                  + EditTools.values()[i].className())
                        .getDeclaredConstructor().newInstance());

            setEditToolSelectionActions(i);
         }
      }
      catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | NoSuchMethodException
            | InvocationTargetException e) {

         FileUtils.log(e);
      }
   }

   private void initFrame() {
      initSplitPane();
      frame.setJMenuBar(menuBar.menuBar());
      frame.add(splitHor, BorderLayout.CENTER);
      frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
      setFrameSizeAndLocation();
   }

   private void initSplitPane() {
      splitHorMid = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, tabPane, null);
      splitHorMid.setDividerSize(0);
      setSplitPaneAppearance(splitHorMid);

      splitVert = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, splitHorMid, null);
      splitVert.setDividerSize(0);
      splitVert.setResizeWeight(1);
      setSplitPaneAppearance(splitVert);

      splitHor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, null, splitVert);
      splitHor.setDividerSize(0);
      setSplitPaneAppearance(splitHor);
   }

   private void setSplitPaneAppearance(JSplitPane jsp) {
      BasicSplitPaneUI bspUI = new BasicSplitPaneUI() {

         @Override
         public BasicSplitPaneDivider createDefaultDivider() {
            return new BasicSplitPaneDivider(this) {
               
               private static final long serialVersionUID = 1L;

               @Override
               public void setBorder(Border b) {
                  // should not set any border
               }

               @Override
               public void paint(Graphics g) {
                  if (theme.isDark()) {
                     g.setColor(theme.lightBackground());
                     g.fillRect(0, 0, getSize().width, getSize().height);
                  }
                  else {
                     super.paint(g);
                  }
               }
            };
         }
      };
      jsp.setUI(bspUI);
      jsp.setBorder(null);
   }

   private void setFrameSizeAndLocation() {
      Dimension screen = ScreenParams.SCREEN_SIZE;
      boolean usePrefs = prefs.existsPrefsFile();
      if (usePrefs) {
         boolean isMulipleScreensPrev = prefs.yesNoProperty(Prefs.MULTIPLE_SCREENS_KEY);
         boolean isMulipleScreens = ScreenParams.isMultipleScreens();
         try {
            int w = Integer.parseInt(prefs.property(Prefs.WIN_WIDTH_KEY));
            int h = Integer.parseInt(prefs.property(Prefs.WIN_HEIGHT_KEY));
            int x = Integer.parseInt(prefs.property(Prefs.WIN_XLOC_KEY));
            int y = Integer.parseInt(prefs.property(Prefs.WIN_YLOC_KEY));
            if (w > screen.width || h > screen.height
                  || (!isMulipleScreens && isMulipleScreensPrev)) {

               usePrefs = false;
            }
            else {
               frame.setSize(w, h);
               frame.setLocation(x, y);
            }
         }
         catch (NumberFormatException e) {
            usePrefs = false;
         }
      }
      if (!usePrefs) {
         frame.setSize(screen.width*4/6, screen.height*4/6);
         frame.setLocation(screen.width/6, screen.height/8);
      }
   }

   private void initShowTabbar() {
      boolean show = prefs.yesNoProperty(Prefs.TABBAR_KEY);
      tabPane.showTabbar(show);
      menuBar.viewMenu().selectTabsItm(show);
   }

   private void initShowFileView() {
      boolean show = prefs.yesNoProperty(Prefs.FILE_VIEW_KEY);
      if (show) {
         showFileView(show);
      }
      menuBar.viewMenu().selectFileViewItm(show);
   }

   private final ComponentListener compListener = new ComponentAdapter() {

      @Override
      public void componentResized(ComponentEvent e) {
         if (frame.getExtendedState() != Frame.MAXIMIZED_BOTH
               && frame.getExtendedState() != Frame.ICONIFIED) {

            Dimension d = frame.getSize();
            width = d.width;
            height = d.height;
         }
      }

      @Override
      public void componentMoved(ComponentEvent e) {
         if (frame.getExtendedState() != Frame.MAXIMIZED_BOTH
               && frame.getExtendedState() != Frame.ICONIFIED) {

            Point p = frame.getLocationOnScreen();
            xLoc = p.x;
            yLoc = p.y;
         }
      }
   };
}
