package eg.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSplitPane;

import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

//--Eadgyth--//
import eg.Constants;
import eg.Preferences;

/**
 * The main window with a split view to which areas components can
 * be added
 */
public class MainWin {

   /** The busy cursor */
   public final static Cursor BUSY_CURSOR
         = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR );
   /**  The default cursor */
   public final static Cursor DEF_CURSOR = Cursor.getDefaultCursor();

   private final JFrame frame = new JFrame();
   private final JPanel allComponents = new JPanel(new BorderLayout());
   private final JPanel statusBar = new JPanel();
   private final JLabel showProjectLb = new JLabel();

   private final JPanel functionPnl = new JPanel(new BorderLayout());
   private final JPanel functTitlePnl = new JPanel();
   private final JLabel functTitleLb = new JLabel();

   private final Menu menu;
   private final Toolbar tBar;
   private final Preferences prefs = new Preferences();

   private JSplitPane splitHorAll;
   private JSplitPane splitHor;
   private JSplitPane splitVert;
   private JPanel fileView;
   private JPanel consoleView;
   private int dividerLocVert = 0;
   private int dividerLocHorAll = 0;
   private int dividerLocHor = 0;

   /**
    * @param menu  a reference to {@link Menu}
    * @param tBar  a reference to {@link Toolbar}
    */
   public MainWin(Menu menu, Toolbar tBar) {
      this.menu = menu;
      this.tBar = tBar;

      prefs.readPrefs();
      initSplitPane();
      initStatusbar();
      initAllComponents();
      initFunctionPnl();
      showHideAct();
      initFrame();
      showProjectLb.setText("Project root: not set");
   }
   
   /**
    * Adds a component at the upper right of the left split area
    * which is intended for the text area
    * @param c  the Component that is added
    */
   public void addTextArea(Component c) {
      splitHor.setRightComponent(c);
   }
   
   /**
    * Adds a JPanel at the upper left of the left split area
    * that is intended for the file view
    * @param pnl  the JPanel that is added
    */
   public void addFileView(JPanel pnl) {
      fileView = pnl;
   }
   
   /**
    * Adds a JPanel at the bottom of the left split area
    * that is intended for the console view
    * @param pnl  the JPanel that is added
    */
   public void addConsoleView(JPanel pnl) {
      consoleView = pnl;
   }
   
   /**
    * Adds a component to this 'function panel' which is at the right 
    * split area. This panel is intended for plugins.
    * @param c  the Component that is added
    * @param title  the title (filename) for the tab
    */
   public void addToFunctionPanel(Component c, String title) {
      BorderLayout layout = (BorderLayout) functionPnl.getLayout();
      Component cCenter = layout.getLayoutComponent(BorderLayout.CENTER);
      if (cCenter != null) {
         functionPnl.remove(cCenter);
      }
      functTitleLb.setText(" " + title);
      if (c != null) {
         functionPnl.add(c, BorderLayout.CENTER);
      }
      functionPnl.revalidate();
      functionPnl.repaint();
   }

   /**
    * Sets this JFrame visible
    */
   public void makeVisible() {
      frame.setVisible(true);
   } 

   /**
    * Sets a busy or default cursor
    * @param cursor  the cursor that has the value {@link #BUSY_CURSOR}
    * or {@link #DEF_CURSOR}
    */
   public void setCursor(Cursor cursor) {
      frame.setCursor(cursor);
   }

   /**
    * Displays text in this frame title (i.e., the file)
    */
   public void displayFrameTitle(String title) {
      frame.setTitle(title);
   }

   /**
    * @return  if the console was opened
    */
   public boolean isConsoleSelected() {
      return menu.isConsoleSelected();
   }

   /**
    * Enables/Disables selected menu items and toolbar buttons for
    * project
    */
   public void allowProjectEvents(boolean isCompile, boolean isRun,
         boolean isBuild) {
      menu.enableExtra(isCompile, isRun, isBuild);
      tBar.enableExtraBts(isCompile, isRun);
   }
   
   public void showProjectInfo(String project) {
      showProjectLb.setText("Project root: " + project);
      menu.enableFileViewItm(true);
   }

   /**
    * Shows or hides the toolbar
    * @param isShowToolbar  true to show the toolbar
    */
   public void showToolbar(boolean isShowToolbar) {
      if (isShowToolbar) {
         allComponents.add(tBar.toolbar(), BorderLayout.NORTH);
         prefs.storePrefs("toolbar", Constants.SHOW);
      }
      else {
         allComponents.remove(tBar.toolbar());
         prefs.storePrefs("toolbar", Constants.HIDE);
      }
      allComponents.revalidate();
   }

   /**
    * Shows or hides the status bar
    * @param isShowStatusbar  true to show the status bar
    */
   public void showStatusbar(boolean isShowStatusbar) {
      if (isShowStatusbar) {
         allComponents.add(statusBar, BorderLayout.SOUTH);
         prefs.storePrefs("statusbar", Constants.SHOW);
      }
      else {
         allComponents.remove(statusBar);
         prefs.storePrefs("statusbar", Constants.HIDE);
      }
      allComponents.revalidate();
   }

   /**
    * Shows the console panel
    */
   public void showConsole() {
      splitVert.setDividerSize(6);
      splitVert.setRightComponent(consoleView);
      if (dividerLocVert == 0) {
         dividerLocVert = (int)(frame.getHeight() * 0.65);
      }
      splitVert.setDividerLocation(dividerLocVert);
      menu.selectShowConsole(true); 
   }

   /**
    * Hides the console panel
    */
   public void hideConsole() {
      dividerLocVert = splitVert.getDividerLocation();
      splitVert.setDividerSize(0);
      splitVert.setRightComponent(null);
      menu.selectShowConsole(false);
   }

   /**
    * Shows the file explorer panel
    */
   public void showFileView() {
      splitHor.setDividerSize(6);
      splitHor.setLeftComponent(fileView);
      if (dividerLocHor == 0) {
         dividerLocHor = (int)(frame.getWidth() * 0.2);
      }
      splitHor.setDividerLocation(dividerLocHor);
   }

   /**
    * Hides the file explorer panel
    */
   public void hideFileView() {
      dividerLocHor = splitHor.getDividerLocation();
      splitHor.setDividerSize(0);
      splitHor.setLeftComponent(null);
      menu.selectShowFileView(false);
   }

   /**
    * Shows the function panel
    */
   public void showFunctionPnl() {
      splitHorAll.setDividerSize(6);
      splitHorAll.setRightComponent(functionPnl);
      if (dividerLocHorAll == 0) {
         dividerLocHorAll = (int)(frame.getWidth() * 0.7);
      }
      splitHorAll.setDividerLocation(dividerLocHorAll);
      menu.selectFunctionPnl(true);
   }

   /**
    * Hides the function panel
    */
   public void hideFunctionPnl() {
      dividerLocHorAll = splitHorAll.getDividerLocation();
      splitHorAll.setDividerSize(0);
      splitHorAll.setRightComponent(null);
      menu.selectFunctionPnl(false);
   }
   
   //
   //--Listeners
   //

   /**
    * Adds a window listener to this JFrame (to defined a window
    * closing handler)
    */
   public void winListen(WindowListener wl) {
      frame.addWindowListener(wl);
   }
   
   //
   //--private methods
   //

   private void initFrame() {
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.setContentPane(allComponents);
      frame.setLocation(175, 25);
      frame.pack();
      frame.setSize(900, 650);
      frame.setIconImage(IconFiles.eadgythIcon.getImage());
   }

   private void initAllComponents() {
      prefs.readPrefs();
      if (Constants.SHOW.equals(prefs.prop.getProperty("toolbar"))) {
         allComponents.add(tBar.toolbar(), BorderLayout.NORTH);
      }
      allComponents.add(splitHorAll, BorderLayout.CENTER);
      if (Constants.SHOW.equals(prefs.prop.getProperty("statusbar"))) {
         allComponents.add(statusBar, BorderLayout.SOUTH);
      }
      frame.setJMenuBar(menu.getMenuBar());
   }

   private void initSplitPane() {      
      splitHor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
           null, null);
      splitHor.setDividerSize(0);
      splitHor.setBorder(null);
      splitVert = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
           splitHor, null);
      splitVert.setDividerSize(0);
      splitVert.setBorder(null);
      splitHorAll = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
            splitVert, null);
      splitHorAll.setDividerSize(0);
      splitHorAll.setBorder(null/*new MatteBorder(1, 0, 0, 0, Constants.BORDER_GRAY)*/);
   }

   private void initStatusbar() {
      showProjectLb.setFont(Constants.VERDANA_PLAIN_11);
      statusBar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
      statusBar.setPreferredSize(new Dimension(0, 20));
      statusBar.add(showProjectLb);
   }

   private void initFunctionPnl() {
      functTitlePnl.setLayout(new BoxLayout(functTitlePnl, BoxLayout.LINE_AXIS));
      functTitlePnl.add(functTitleLb);
      functionPnl.setBorder(new LineBorder(Constants.BORDER_GRAY));
      functTitleLb.setFont(Constants.SANSSERIF_PLAIN_12);
      functTitleLb.setText(" No function selected");
      JButton closeBt = new JButton(IconFiles.closeIcon);
      closeBt.setBorder(new EmptyBorder(3, 5, 3, 5));
      closeBt.setContentAreaFilled(false);
      closeBt.setToolTipText("Close function area");
      closeBt.setFocusable(false);
      closeBt.addActionListener(e -> {
         hideFunctionPnl();
         menu.selectFunctionPnl(false);
      });
      functTitlePnl.add(Box.createHorizontalGlue());
      functTitlePnl.add(closeBt);
      functionPnl.add(functTitlePnl, BorderLayout.NORTH);
   }

   private void showHideAct() {
      menu.showConsoleAct(e -> showHideConsole());
      menu.showFileViewAct(e -> showHideFileView());
      menu.showFunctionPnlAct(e -> showHideFunctionPnl());
   }

   private void showHideConsole() {
      if (menu.isConsoleSelected()) {
         showConsole();
      }
      else {
         hideConsole();
      }
   }

   private void showHideFileView() {
      if (menu.isFileViewSelected()) {
         showFileView();
      }
      else {
         hideFileView();
      }
   }

   private void showHideFunctionPnl() {
      if (menu.isFunctionPnlSelected()) {
         showFunctionPnl();
      }
      else {
         hideFunctionPnl();
      }
   }
}