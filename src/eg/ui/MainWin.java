package eg.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import javax.swing.border.EmptyBorder;

//--Eadgyth--//
import eg.Constants;
import eg.TabbedFiles;
import eg.CurrentProject;
import eg.Edit;
import eg.EditAreaFormat;
import eg.ui.menu.*;
import eg.ui.filetree.FileTree;
import eg.console.ConsolePanel;
import eg.plugin.PluginStarter;

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
   private final JLabel showProjectLb = new JLabel();

   private final Menu menu = new Menu();
   private final Toolbar toolbar = new Toolbar();
   private final TabbedPane tabPane = new TabbedPane();
   private final FileTree fileTree = new FileTree();
   private final ConsolePanel console = new ConsolePanel();
   private final FunctionPanel functPnl = new FunctionPanel();

   private JSplitPane splitHorAll;
   private JSplitPane splitHor;
   private JSplitPane splitVert;
   private int dividerLocVert = 0;
   private int dividerLocHorAll = 0;
   private int dividerLocHor = 0;

   public MainWin() {
      initFrame();
      registerAct();
   }

   /**
    * Sets this JFrame visible
    */
   public void makeVisible() {
      frame.setVisible(true);
   }

   /**
    * Gets this Menu
    *
    * @return  this {@link Menu}
    */
   public Menu menu() {
      return menu;
   }
   
   /**
    * Gets this toolbar
    *
    * @return  this {@link Toolbar}
    */
   public Toolbar toolbar() {
      return toolbar;
   }

   /**
    * Gets this TabbedPane
    *
    * @return  this {@link TabbedPane}
    */
   public TabbedPane tabPane() {
      return tabPane;
   }

   /**
    * Gets this FileTree
    *
    * @return  this {@link FileTree}
    */
   public FileTree fileTree() {
      return fileTree;
   }

   /**
    * Gets this ConsolePanel
    *
    * @return  this {@link ConsolePanel}
    */
    public ConsolePanel console() {
       return console;
    }

    /**
     * Gets this FunctionPanel
     *
     * @return  this {@link FunctionPanel}
     */
    public FunctionPanel functionPanel() {
       return functPnl;
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
    * Displays text in the title bar
    *
    * @param title  the text that is displayed in the title bar
    */
   public void displayFrameTitle(String title) {
      frame.setTitle(title);
   }

   /**
    * Shows the name of a project in the status bar
    *
    * @param projectName  the name of the project
    */
   public void showProjectInfo(String projectName) {
      showProjectLb.setText("Project: " + projectName);
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
    * Shows or hides the console
    *
    * @param b  true to show, false to hide the console panel
    */
   public void showConsole(boolean b) {
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

   /**
    * Shows or hides the file view
    *
    * @param b  true to show, false to hide the fileview panel
    */
   public void showFileView(boolean b) {
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

   /**
    * Shows or hides the function panel
    *
    * @param b  true to show, false to hide the function panel
    */
   public void showFunctionPnl(boolean b) {
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

   /**
    * Adds a {@code WindowListener} to this JFrame
    *
    * @param wl  the {@code WindowListener}
    */
   public void winListen(WindowListener wl) {
      frame.addWindowListener(wl);
   }

   /**
    * Registers handlers for file actions
    *
    * @param tf  the reference to {@link TabbedFiles}
    */
   public void registerFileAct(TabbedFiles tf) {
      menu.fileMenu().registerAct(tf);
      toolbar.registerFileAct(tf);
      fileTree.addObserver(tf);
   }

   /**
    * Registers handlers for edit actions
    *
    * @param ed  the reference to {@link Edit}
    * @param tf  the reference to {@link CurrentProject}
    */
   public void registerEditAct(Edit ed, TabbedFiles tf) {
      toolbar.registerEditAct(ed);
      menu.editMenu().registerAct(ed, tf);
   }

   /**
    * Registers handler for plugin actions
    *
    * @param plugSt  the reference to {@link PluginStarter}
    */
   public void registerPlugAct(PluginStarter plugSt) {
      menu.pluginMenu().startPlugin(plugSt, menu.viewMenu());
   }

   /**
    * Registers handlers for project actions
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

   private void initFrame() {
      initSplitPane();
      initStatusbar();
      frame.setJMenuBar(menu.menubar());
      frame.add(splitHorAll, BorderLayout.CENTER);
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.setIconImage(IconFiles.EADGYTH_ICON.getImage());
      frame.setLocation(175, 25);
      frame.setSize(900, 650);
   }

   private void initSplitPane() {
      splitHor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
           null, tabPane.tabbedPane());
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
      showProjectLb.setFont(Constants.VERDANA_PLAIN_11);
      statusBar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
      statusBar.add(showProjectLb);
      showProjectLb.setText("Project: not set");
   }

   private void registerAct() {
      ViewMenu vm = menu.viewMenu();
      vm.consoleItmAct(e -> showConsole(vm.isConsoleItmSelected()));
      vm.fileViewItmAct(e -> showFileView(vm.isFileViewItmSelected()));
      vm.functionItmAct(e -> showFunctionPnl(vm.isFunctionItmSelected()));
      fileTree.closeAct(e -> vm.doUnselectFileViewAct());
      console.closeAct(e -> vm.doConsoleItmAct(false));
      functPnl.closeAct(e -> vm.doFunctionItmAct(false));
   }
}
