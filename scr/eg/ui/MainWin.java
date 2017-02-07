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
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import javax.swing.border.EmptyBorder;

//--Eadgyth--//
import eg.Constants;
import eg.Preferences;


/**
 * The main window
 */
public class MainWin {

   private final static Cursor BUSY_CURSOR
         = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR );
   private final static Cursor DEF_CURSOR = Cursor.getDefaultCursor();

   private final JFrame frame = new JFrame();
   private final JPanel allComponents = new JPanel(new BorderLayout());
   private final JPanel statusBar = new JPanel();
   private final JLabel showProjectLb = new JLabel();

   private final JPanel functionPnl = new JPanel(new BorderLayout());
   private final JPanel functTitlePnl = new JPanel();
   private final JLabel functTitleLb = new JLabel(" No function selected");
   private final JButton closeFunctBt = new JButton(IconFiles.CLOSE_ICON);
   
   private final JMenuBar menubar;
   private final JToolBar toolbar;
   private final JTabbedPane tabbedPane;
   private final JPanel fileViewPnl;
   private final JPanel consolePnl;

   private final Preferences prefs = new Preferences();

   private JSplitPane splitHorAll;
   private JSplitPane splitHor;
   private JSplitPane splitVert;
   private int dividerLocVert = 0;
   private int dividerLocHorAll = 0;
   private int dividerLocHor = 0;

   public MainWin(JMenuBar menubar, JToolBar toolbar, JTabbedPane tabbedPane,
         JPanel fileViewPnl, JPanel consolePnl) {
      this.menubar = menubar;
      this.toolbar = toolbar;
      this.tabbedPane = tabbedPane;
      this.fileViewPnl = fileViewPnl;
      this.consolePnl = consolePnl;

      prefs.readPrefs();
      initSplitPane();
      initStatusbar();
      initAllComponents();
      initFunctionPnl();
      initFrame();
      showProjectLb.setText("Project: not set");
   }

   /**
    * Sets this JFrame visible
    */
   public void makeVisible() {
      frame.setVisible(true);
   } 

   /**
    * Sets a busy or default cursor
    * @param isBusy  true to set the wait cursor, false to set
    * the default cursor
    */
   public void setBusyCursor(boolean isBusy) {
      if (isBusy) {
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
    * Displays text in this frame title (i.e., the file)
    * @param title  the text is displayed in the title bar of this frame
    */
   public void displayFrameTitle(String title) {
      frame.setTitle(title);
   }
   
   /**
    * Shows the name of a project in the status bar
    * @param projectName  the name of the project
    */
   public void showProjectInfo(String projectName) {
      showProjectLb.setText("Project: " + projectName);
   }
   
   /**
    * Adds a component to this 'function panel' which is added to
    * the right of this split area.
    * <p>
    * The 'function panel' has a border layout in whose center the
    * specified component is added. The specified title is shown in a panel
    * at the north.
    * @param c  the Component that is added to the right of this plit window
    * @param title  the title for the function
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
    * Shows or hides the toolbar
    * @param isShowToolbar  true to show the toolbar
    */
   public void showToolbar(boolean isShowToolbar) {
      if (isShowToolbar) {
         allComponents.add(toolbar, BorderLayout.NORTH);
         prefs.storePrefs("toolbar", "show");
      }
      else {
         allComponents.remove(toolbar);
         prefs.storePrefs("toolbar", "hide");
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
         prefs.storePrefs("statusbar", "show");
      }
      else {
         allComponents.remove(statusBar);
         prefs.storePrefs("statusbar", "hide");
      }
      allComponents.revalidate();
   }

   /**
    * Shows the console panel
    */
   public void showConsole() {
      splitVert.setDividerSize(6);
      splitVert.setRightComponent(consolePnl);
      if (dividerLocVert == 0) {
         dividerLocVert = (int)(frame.getHeight() * 0.65);
      }
      splitVert.setDividerLocation(dividerLocVert);
   }

   /**
    * Hides the console panel
    */
   public void hideConsole() {
      dividerLocVert = splitVert.getDividerLocation();
      splitVert.setDividerSize(0);
      splitVert.setRightComponent(null);
   }

   /**
    * Shows the file explorer panel
    */
   public void showFileView() {
      splitHor.setDividerSize(6);
      splitHor.setLeftComponent(fileViewPnl);
      if (dividerLocHor == 0) {
         dividerLocHor = (int)(frame.getWidth() * 0.22);
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
   }

   /**
    * Hides the function panel
    */
   public void hideFunctionPnl() {
      dividerLocHorAll = splitHorAll.getDividerLocation();
      splitHorAll.setDividerSize(0);
      splitHorAll.setRightComponent(null);
   }
   
   //
   //--Listeners
   //

   /**
    * Adds a {@code WindowListener} to this JFrame
    * @param wl  the {@code WindowListener}
    */
   public void winListen(WindowListener wl) {
      frame.addWindowListener(wl);
   }
   
   /**
    * Add an {@code ActionListener} to the close button for
    * this function panel
    * @param al  the {@code ActionListener}
    */
   public void closeFunctPnlAct(ActionListener al) {
      closeFunctBt.addActionListener(al);
   }
   
   //
   //--private methods
   //

   private void initFrame() {
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.setContentPane(allComponents);
      frame.setIconImage(IconFiles.EADGYTH_ICON.getImage());
      frame.setLocation(175, 25);
      frame.setSize(900, 650);
   }

   private void initAllComponents() {
      prefs.readPrefs();
      if ("show".equals(prefs.getProperty("toolbar"))) {
         allComponents.add(toolbar, BorderLayout.NORTH);
      }
      allComponents.add(splitHorAll, BorderLayout.CENTER);
      if ("show".equals(prefs.getProperty("statusbar"))) {
         allComponents.add(statusBar, BorderLayout.SOUTH);
      }
      frame.setJMenuBar(menubar);
   }      

   private void initSplitPane() {      
      splitHor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
           null, tabbedPane);
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
   }

   private void initFunctionPnl() {
      functTitlePnl.setLayout(new BoxLayout(functTitlePnl, BoxLayout.LINE_AXIS));
      functTitlePnl.add(functTitleLb);
      functTitleLb.setFont(Constants.SANSSERIF_PLAIN_12);
      functionPnl.setBorder(Constants.BORDER);
      closeFunctBt.setBorder(new EmptyBorder(3, 5, 3, 5));
      closeFunctBt.setContentAreaFilled(false);
      closeFunctBt.setToolTipText("Close function area");
      closeFunctBt.setFocusable(false);
      functTitlePnl.add(Box.createHorizontalGlue());
      functTitlePnl.add(closeFunctBt);
      functionPnl.add(functTitlePnl, BorderLayout.NORTH);
   }  
}
