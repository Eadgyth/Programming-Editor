package eg.ui.filetree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import javax.swing.border.LineBorder;

import javax.swing.filechooser.FileSystemView;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import java.util.Observable;

//--Eadyth--//
import eg.Constants;
import eg.ui.IconFiles;

import eg.utils.JOptions;
import eg.utils.FileUtils;

/**
 * The display the project's file system in a {@code JTree}
 */
public class FileTree extends Observable {
   
   private static final String F_SEP = File.separator;

   /*
    * The panel that contains the tree and the toobar */
   private final JPanel fileTreePnl  = new JPanel(new BorderLayout());
   /*
    * The panel to which the tree is added and that is added to the scroll pane */
   private final JPanel holdTreePnl  = new JPanel();

   private final JScrollPane scroll  = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

   private final JToolBar toolbar;
   private final JButton upBt        = new JButton(UIManager.getIcon(
                                       "FileChooser.upFolderIcon"));
   private final JButton   renewBt   = new JButton(IconFiles.refreshIcon);
   private final JButton   closeBt   = new JButton(IconFiles.closeIcon);
   private final PopupMenu popupFile = new PopupMenu(PopupMenu.FILE_OPT);
   private final PopupMenu popupDir  = new PopupMenu(PopupMenu.FOLDER_OPT);
   
   private JTree tree = null;
   private DefaultTreeModel model;
   private DefaultMutableTreeNode root;
   private MouseListener ml;

   private String projRoot = "";
   private String pathHelper = "";
   private List<TreePath> expanded = null;
   private String deletableDir = "";

   public FileTree() {
      ml = mouseListener;
      toolbar = createToolbar();
      initTreePanel();
   }
   
   /**
    * Returns the reference to this panel that shows the file tree
    * and the toolbar
    * @return  the {@code JPanel} that shows the file tree and the
    * the toolbar
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
    * Sets the filepath of the folder that can be deleted
    * @param dir  the directory that can be deleted although it is
    * not empty
    */
   public void setDeletableDir(String dir) {
      deletableDir = dir;
   }
   
   /**
    * Creates a new tree at the currently shown root
    */
   public void updateTree() {
      getExpandedNodes();
      setNewTree(pathHelper);
      setExpanded();
      fileTreePnl.repaint();
      fileTreePnl.revalidate();
   }
   
   /**
    * Adds an {@code ActionListener} to this close button
    * @param al  the {@code ActionListener}
    */
   public void closeAct(ActionListener al) {
      closeBt.addActionListener(al);
   }
   
   //
   //--private methods--//
   //
   
   private void setNewTree(String path) {
      if (path.length() > 0) {
         if (tree != null) {
            holdTreePnl.remove(tree);
         }   
         pathHelper = path;
         if (path.equals(projRoot)) {
            upBt.setEnabled(false);
         }
         setTree(path);
      }     
   }
   
   private void setTree(String path) {
      root = new DefaultMutableTreeNode("root", true);
      model = new DefaultTreeModel(root);
      getFiles(root, new File(path));
      initTree(); // includes creating a new tree object
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
              for (File fListSort1 : fListSort) {
                  getFiles(child, fListSort1);
              }
          }
      }
   }
   
   private File[] sortFoldersAndFiles(File[] fList) {
      List<File> allFiles = new ArrayList<>();
      List<File> files    = new ArrayList<>();
      
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
      tree = new JTree(model);
      tree.setRootVisible(false);
      tree.setFont(Constants.VERDANA_PLAIN_11);
      tree.setBorder(new LineBorder(Color.WHITE, 5));
      tree.setCellRenderer(new TreeRenderer());
      tree.setToggleClickCount(0);
      tree.expandRow(0);
      tree.addMouseListener(ml);
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
         enableDelete();
         popupDir.showMenu(c, x, y);
      }
   }
   
   private void enableDelete() {
      File f = getSelectedFile();
      if (isInDeletableDir(f)
             || FileUtils.isFolderEmpty(f)) {
          popupDir.enableDelete(true);
      }
      else {
         popupDir.enableDelete(false);
      }
   }
   
   private void deleteFile() {
      DefaultMutableTreeNode selectedNode = getSelectedNode();
      File f = getSelectedFile();
      int res = JOptions.confirmYesNo(deleteMessage(f));
      if (res == JOptionPane.YES_OPTION) {
         boolean success = f.delete();
         if (success) {
            model.removeNodeFromParent(selectedNode);
         }
         else {
            JOptions.warnMessage("Deleting " + f.getName() + " failed");
         }
      }
   }

   private void deleteFolder() {
      DefaultMutableTreeNode selectedNode = getSelectedNode();
      File f = getSelectedFile();  
      int res = JOptions.confirmYesNo(deleteMessage(f));
      if (res == JOptionPane.YES_OPTION) {
         boolean success;
         if (isInDeletableDir(f)) {         
            success = FileUtils.deleteFolder(f);
         }
         else {
            success = FileUtils.deleteEmptyFolder(f);
         }
         if (success) {
            model.removeNodeFromParent(selectedNode);
         }
         else {
            JOptions.warnMessage("Deleting " + f.getName() + "failed.");
         }
      }
   }
   
   private boolean isInDeletableDir(File file) {
      return file.toString().endsWith(F_SEP + deletableDir)
            || file.toString().contains(F_SEP + deletableDir + F_SEP);
   }      
   
   private String deleteMessage(File f) {
      String message
            = "'" + f.getName() + "' will be permanently deleted!\n"
            + "Continue ?";
      return message;
   }

   private void newFolder() {
      DefaultMutableTreeNode parent = getSelectedNode();
      File f = getSelectedFile();
      String newFolder = JOptions.dialogRes("Enter name of new folder",
            "New folder", "");
      if (newFolder != null) {
         File newDir = new File(f.getPath(), newFolder);
         boolean succes = newDir.mkdirs();
         if (succes) {
            model.insertNodeInto(new DefaultMutableTreeNode(newDir),
               parent, parent.getChildCount());
         }
         else {
            JOptions.warnMessage("Creating " + newDir.getName() + " failed");
         }
      }
   }
   
   private void openFile(String fileStr) {
      boolean isAllowedFile
             = fileStr.endsWith(".java")
            || fileStr.endsWith(".txt")
            || fileStr.endsWith(".properties")
            || fileStr.endsWith(".html")
            || fileStr.endsWith(".pl")
            || fileStr.endsWith(".pm");
      if (isAllowedFile) {
         setChanged();
         notifyObservers(fileStr);
      }
      else {
         JOptions.warnMessageToFront(
                "No function is associated with this file type");
      }
   }
   
   private File getSelectedFile() {
      DefaultMutableTreeNode node =
            (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
      Object nodeInfo = null;
      if (node != null) {
         nodeInfo = node.getUserObject();
      }
      return (File) nodeInfo;
   }
   
   private DefaultMutableTreeNode getSelectedNode() {
      return (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
   }
  
   private DefaultMutableTreeNode searchNode(String nodeStr) {
      DefaultMutableTreeNode node;
      Enumeration e = root.breadthFirstEnumeration();
      while (e.hasMoreElements()) {
         node = (DefaultMutableTreeNode) e.nextElement();
         if (nodeStr.equals(node.getUserObject().toString())) {
            return node;
         }
      }
      return null;
   }
   
   private void getExpandedNodes() {
      expanded = new ArrayList<>();
      for (int i = 0; i < tree.getRowCount(); i++) {
         if (tree.isExpanded(i)) {
            expanded.add(tree.getPathForRow(i));
         }
      }      
   }
   
   private void setExpanded() {
      for (TreePath tp : expanded) {
         for (int i = 0; i < tree.getRowCount(); i++) { 
            if (tp.toString().equals(tree.getPathForRow(i).toString())) {             
               tree.expandRow(i);
            }
         }
      }
   }
   
   private void initTreePanel() {
      holdTreePnl.setLayout(new BorderLayout());
      scroll.setBorder(null);
      scroll.setViewportView(holdTreePnl);
      scroll.getVerticalScrollBar().setUnitIncrement(10);
      fileTreePnl.add(toolbar, BorderLayout.NORTH);
      fileTreePnl.add(scroll, BorderLayout.CENTER);
      fileTreePnl.setBorder(Constants.LOW_ETCHED);
      upBt.addActionListener(e -> folderUp());
      renewBt.addActionListener(e -> updateTree());
      upBt.setEnabled(false);
      popupFile.deleteAct(e -> deleteFile());
      popupDir.newFolderAct(e -> newFolder());
      popupDir.deleteAct(e -> deleteFolder());
   }

   private JToolBar createToolbar() {
      JButton[] bts = new JButton[] {
         upBt, renewBt, closeBt
      };  
      String[] tooltips = new String[] {
         "Folder up",
         "Update tree",
         "Close the project explorer",
      };
      return eg.utils.UiComponents.toolbarLastBtRight(bts, tooltips);
   }
   
   private final MouseListener mouseListener = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
         if (SwingUtilities.isLeftMouseButton(e)) {
            File f;
            if (e.getClickCount() == 2) {
               f = getSelectedFile();
               if (f != null ) {
                  String fStr = f.toString();
                  if (f.isFile()) {
                     openFile(fStr); 
                  }
                  else { // is directory
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
      private final FileSystemView fsv = FileSystemView.getFileSystemView();

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