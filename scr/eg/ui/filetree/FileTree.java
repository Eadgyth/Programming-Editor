package eg.ui.filetree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Component;

import java.awt.event.*;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import javax.swing.border.*;

import javax.swing.border.LineBorder;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import javax.swing.filechooser.FileSystemView;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import java.util.Observable;

//--Eadyth--//
import eg.utils.ShowJOption;
import eg.Constants;
import eg.ui.IconFiles;

/**
 * Contains the file view panel that consists in a panel in which a JTree showing
 * the project's file system can be displayed and a toolbar. 
 */
public class FileTree extends Observable {

   /*
    * The panel that contains the tree and the toobar */
   private final JPanel fileTreePnl  = new JPanel(new BorderLayout());
   /*
    * The panel to which the tree is added and that is added to the scroll pane */
   private final JPanel holdTreePnl  = new JPanel();

   private final JScrollPane scroll  = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);  
   private final JToolBar toolbar    = new JToolBar(JToolBar.HORIZONTAL);

   private final JButton upBt        = new JButton(UIManager.getIcon(
         "FileChooser.upFolderIcon"));
   private final JButton   refreshBt = new JButton(IconFiles.refreshIcon);
   private final JButton   closeBt   = new JButton(IconFiles.closeIcon);
   private final PopupMenu popupFile = new PopupMenu(PopupMenu.FILE_OPT);
   private final PopupMenu popupDir  = new PopupMenu(PopupMenu.FOLDER_OPT);
   
   private JTree tree = null;
   private DefaultTreeModel model;
   private DefaultMutableTreeNode root;
   private MouseListener ml;
   private ArrayList<Integer> expandedRows;
   private String pathHelper = "";
   private String projRoot = "";

   public FileTree() {
      ml = mouseListener;
      initTreePanel();
   }
   
   /**
    * Returns the reference to this panel that contains the file tree
    * and the toolbar
    */
   public JPanel fileTreePnl() {
      return fileTreePnl;
   }
   
   /**
    * Sets the project's root directory and displays the file system at
    * this root
    */
   public void setProjectTree(String projRoot) {
      this.projRoot = projRoot;
      setNewTree(projRoot);
   }
   
   /**
    * Adds a file to the file tree
    */
   public void addFile(String fileToAdd) {
      File f = new File(fileToAdd);
      String parent = f.getParent();
      DefaultMutableTreeNode searchNode = searchNode(parent);
      model.insertNodeInto(new DefaultMutableTreeNode(f),
               searchNode, searchNode.getChildCount());
   }
   
   /**
    * Registers an event handler at the close button of the file
    * tree panel
    */
   public void closeAct(ActionListener al) {
      closeBt.addActionListener(al);
   }
   
   //
   //--private methods--//
   //
   
  private File getSelectedFile() {
      DefaultMutableTreeNode node =
            (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
      Object nodeInfo = null;
      if (node != null) {
         nodeInfo = node.getUserObject();
      }
      return (File) nodeInfo;
   }
   
   private void folderDown(String child) {
      setNewTree(child);
      upBt.setEnabled(true);
   }
   
   private void showMenu(Component c, int x, int y) {
      int row = tree.getRowForLocation(x, y);
      tree.setSelectionRow(row);
      if (!tree.isSelectionEmpty() && getSelectedFile().isFile()) {
         popupFile.showMenu(c, x, y);
      }
      if (!tree.isSelectionEmpty() && getSelectedFile().isDirectory()) {
         popupDir.showMenu(c, x, y);
      }
   }

   private void initTreePanel() {
      holdTreePnl.setLayout(new BorderLayout());
      scroll.setBorder(new MatteBorder(0, 1, 1, 1, Constants.BORDER_GRAY));
      scroll.setViewportView(holdTreePnl);
      initToolbar();
      fileTreePnl.add(toolbar, BorderLayout.NORTH);
      fileTreePnl.add(scroll, BorderLayout.CENTER);
      upBt.addActionListener(e -> folderUp());
      refreshBt.addActionListener(e -> refreshTree());
      upBt.setEnabled(false);
      popupFile.deleteAct(e -> deleteFile());
      popupDir.newFolderAct(e -> newFolder());
   }
   
   private void setNewTree(String path) {
      if (path.length() > 0) {
         if (tree != null) {
            holdTreePnl.remove(tree);
         }   
         setTree(path);
         pathHelper = path;
         if (path.equals(projRoot)) {
            upBt.setEnabled(false);
         }
      }     
   }
   
   private void setTree(String path) {
      root = new DefaultMutableTreeNode("root", true);
      model = new DefaultTreeModel(root);
      getFiles(root, new File(path));
      initTree(); // includes creating a new tree obsect
      holdTreePnl.add(tree);
      fileTreePnl.repaint();
      fileTreePnl.revalidate();
   }
   
   private void getFiles(DefaultMutableTreeNode node, File f) {
      if (f.isFile()) {
          DefaultMutableTreeNode child = new DefaultMutableTreeNode(f);
          node.add(child);
      }
      else {
          DefaultMutableTreeNode child = new DefaultMutableTreeNode(f);
          node.add(child);
          File fList[] = f.listFiles();
          if (fList != null) {
             File fListSort[] = sortFoldersAndFiles(fList);
             for(int i = 0; i < fListSort.length; i++) {
                getFiles(child, fListSort[i]);
             }
          }
      }
   }
   
   private File[] sortFoldersAndFiles(File[] fList) {
      List<File> allFiles = new ArrayList<File>();
      List<File> files    = new ArrayList<File>();
      
      for (int i = 0; i < fList.length; i++ ) {
         if (fList[i].isDirectory()) {
            allFiles.add(fList[i]);
         }
         else {
            files.add(fList[i]);
         }
      }
      allFiles.addAll(files);
      File[] sortedList = allFiles.toArray(new File[fList.length]);
      return sortedList;
   }         

   private void initTree() {
      UIManager.put("Tree.rowHeight", new Integer(20));
      tree = new JTree(model);
      tree.setRootVisible(false);
      tree.setFont(Constants.VERDANA_PLAIN_11);
      tree.setBorder(new LineBorder(Color.WHITE, 5));
      tree.setCellRenderer(new TreeRenderer());
      tree.setToggleClickCount(0);
      tree.expandRow(0);
      tree.addMouseListener(ml);
   }
   
   private void deleteFile() {
      DefaultMutableTreeNode selectedNode = getSelectedNode();
      File f = getSelectedFile();
      int res = ShowJOption.confirmYesNo("Delete " + f.getName() + " ?");
      if (res == JOptionPane.YES_OPTION) {
         boolean success = f.delete();
         if (success) {
            model.removeNodeFromParent(selectedNode);
         }
         else {
            ShowJOption.warnMessage("Deleting " + f.getName() + " failed");
         }
      }
   }
   
   private void newFolder() {
      DefaultMutableTreeNode parent = getSelectedNode();
      File f = getSelectedFile();
      String newFolder = ShowJOption.dialogRes("Enter name of new folder", "New folder", "");
      if (newFolder != null) {
         File newDir = new File(f.toString() + File.separator + newFolder);
         boolean succes = newDir.mkdirs();
         if (succes) {
            model.insertNodeInto(new DefaultMutableTreeNode(newDir),
               parent, parent.getChildCount());
         }
         else {
            ShowJOption.warnMessage("Creating " + newDir.getName() + " failed");
         }
      }
   }
   
   private void folderUp() {
      String parent = new File(pathHelper).getParent();
      String rootParent = new File(projRoot).getParent();
      if (!rootParent.equals(parent)) {
         setNewTree(parent);
      }
      if (projRoot.equals(parent)) {
         upBt.setEnabled(false);
      }
   }
   
   private void refreshTree() {
      setNewTree(pathHelper);
   }
   
   private DefaultMutableTreeNode getSelectedNode() {
      return (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
   }
  
   private DefaultMutableTreeNode searchNode(String nodeStr) {
      DefaultMutableTreeNode node = null;
      Enumeration e = root.breadthFirstEnumeration();
      while (e.hasMoreElements()) {
         node = (DefaultMutableTreeNode) e.nextElement();
         if (nodeStr.equals(node.getUserObject().toString())) {
            return node;
         }
      }
      return null;
   }

   private void initToolbar() {
      JButton[] bts = new JButton[] {
         upBt, refreshBt, closeBt
      };
      for (int i = 0; i < bts.length; i++) {
         if (i == bts.length - 1) {
            toolbar.add(Box.createHorizontalGlue());
         }
         toolbar.add(bts[i]);
         toolbar.setOpaque(false);
         toolbar.setFloatable(false);
         toolbar.setBorder(null);
         bts[i].setBorder(new EmptyBorder(2, 5, 2, 5));
         bts[i].setFocusable(false);
      }
   }
   
   private MouseListener mouseListener = new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
         if (SwingUtilities.isLeftMouseButton(e)) {
            File f = null;
            if (e.getClickCount() == 2) {
               f = getSelectedFile();
               if (f != null ) {
                  String fStr = f.toString();
                  if (f.isFile()) {
                      boolean isAllowedFile
                             = fStr.endsWith(".java")
                            || fStr.endsWith(".txt")
                            || fStr.endsWith(".properties")
                            || fStr.endsWith(".html");
                      if (isAllowedFile) {
                         setChanged();
                         notifyObservers(fStr);
                      }
                      else {
                         ShowJOption.warnMessageToFront(
                                "No function is associated with this file type");
                     }
                  }
                  else {
                     folderDown(fStr);
                  }
               }
            }
         }
         if (SwingUtilities.isRightMouseButton(e)) {
            showMenu(e.getComponent(), e.getX(), e.getY());
        }
      }
   };

   private class TreeRenderer extends DefaultTreeCellRenderer {
      private FileSystemView fsv = FileSystemView.getFileSystemView();

      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
              row, hasFocus);

        if (value instanceof DefaultMutableTreeNode) {
           value = ((DefaultMutableTreeNode) value).getUserObject();
           if (value instanceof File) {
              File file = (File) value;
              if (file.isFile()) {
                 setIcon(fsv.getSystemIcon(file));
                 setText(file.getName());
              } else {
                 setIcon(fsv.getSystemIcon(file));
                 setText(file.getName());
              }
           }
        }
        return this;
      }
   }
}