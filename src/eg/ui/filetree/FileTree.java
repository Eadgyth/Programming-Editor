package eg.ui.filetree;

import java.awt.Component;
import java.awt.EventQueue;

import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

//--Eadgyth--/
import eg.FileOpener;
import eg.utils.Dialogs;
import eg.utils.FileUtils;

/**
 * The display of a project's file system in a <code>JTree</code>.
 */
public class FileTree {

   private final TreePanel treePnl;
   private final FileOpener opener;
   private final PopupMenu popupFile = new PopupMenu(PopupMenu.FILE_OPT);
   private final PopupMenu popupDir  = new PopupMenu(PopupMenu.FOLDER_OPT);
   private final HashSet<String> checkDupl = new HashSet<>();

   private JTree tree = null;
   private DefaultTreeModel model;

   private String projRoot = "";
   private String deletableDir = null;
   private List<TreePath> expandedNodes = null;
   private HashMap<String, List<TreePath>> prevExpandedNodes = new HashMap<>();
   private File selectedFile = null;
   private DefaultMutableTreeNode selectedNode = null;

   /**
    * @param treePnl  the TreePanel
    * @param opener  the FileOpener
    */
   public FileTree(TreePanel treePnl, FileOpener opener) {
      this.treePnl = treePnl;
      this.opener = opener;
      setActions();
   }

   /**
    * Sets the project's root directory and displays the file system at
    * this root
    *
    * @param projectRoot  the project's root directory
    */
   public void setProjectTree(String projectRoot) {
      if (projectRoot == null) {
         throw new IllegalArgumentException("projectRoot is null");
      }
      if (projRoot.equals(projectRoot) || projectRoot.isEmpty()) {
         return;
      }
      if (tree != null) {
         setExpandedNodeList();
         prevExpandedNodes.put(projRoot, expandedNodes);
      }
      if (prevExpandedNodes.containsKey(projectRoot)) {
         expandedNodes = prevExpandedNodes.get(projectRoot);
      }
      projRoot = projectRoot;
      setNewTree();
   }

   /**
    * Sets the directory that may be deleted by this popup menu
    * although it is not empty. Other non-empty folders are
    * protected against deletion. The specified directory is
    * relative to the project root but does not have to exist
    * initially. Any absolute directory path is ignored.
    *
    * @param dir  the directory. Null or the empty string to not
    * set a deletable directory
    */
   public void setDeletableDir(String dir) {
      if (projRoot.isEmpty()) {
         throw new IllegalStateException("No project root has been set");
      }
      if (dir == null || dir.isEmpty() || new File(dir).isAbsolute()) {
         deletableDir = null;
         return;
      }
      File f = new File(projRoot + File.separator + dir);
      while (f.getParent() != null) {
         if (f.getParent().equals(projRoot)) {
            deletableDir = f.getPath();
            break;
         }
         f = f.getParentFile();
      }
   }

   /**
    * Updates the tree to display any possible changes
    */
   public void updateTree() {
      if (projRoot.isEmpty() || tree == null) {
         return;
      }
      setExpandedNodeList();
      setNewTree();
   }

   /**
    * Returns this currently shown project root
    *
    * @return  the root
    */
   public String currentRoot() {
      return projRoot;
   }

   //
   //--private--/
   //

   private void setNewTree() {
      EventQueue.invokeLater(() -> {
         checkDupl.clear();
         File rootFile = new File(projRoot);
         setModel(rootFile);
         setTree();
      });
   }

   private void setModel(File f) {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode(f);
      model = new DefaultTreeModel(root);
      getFiles(root, f);
   }

   private void setTree() {
      if (tree == null) {
         tree = new JTree(model);
         tree.addMouseListener(mouseListener);
         treePnl.setTree(tree);
         tree.addTreeExpansionListener(expansionListner);
      }
      else {
         tree.setModel(model);
         expand();
      }
   }

   private void getFiles(DefaultMutableTreeNode node, File f) {
      File[] fList = f.listFiles();
      if (fList != null) {
         File[] fListSorted = sortedFiles(fList);
         for (File fs : fListSorted) {
            if (!checkDupl.contains(fs.getAbsolutePath())) {
               checkDupl.add(fs.getAbsolutePath());
            }
            else {
               continue;
            }
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(fs);
            node.add(child);
            if (fs.isDirectory() && node.getLevel() == 0) {
               getFiles(child, fs);
            }
         }
      }
   }

   private File[] sortedFiles(File[] toSort) {
      List<File> all = new ArrayList<>();
      List<File> files = new ArrayList<>();
      for (File f : toSort) {
         if (f.isDirectory()) {
             all.add(f);
         } else {
             files.add(f);
         }
      }
      all.addAll(files);
      return all.toArray(new File[toSort.length]);
   }

   private void openFile() {
      opener.open(selectedFile);
      tree.clearSelection();
   }

   private void delete() {
      if (!FileUtils.isWriteable(selectedFile)) {
         return;
      }
      int res = Dialogs.warnConfirmYesNo(
            selectedFile.getName()
            + " will be permanently deleted!\nContinue?");

      if (res == JOptionPane.YES_OPTION) {
         try {
            if (selectedFile.isFile()) {
               Files.delete(selectedFile.toPath());
            }
            else {
               deleteFolder(selectedFile);
            }
            model.removeNodeFromParent(selectedNode);
         }
         catch (IOException e) {
            FileUtils.log(e);
         }
      }
      else {
         tree.clearSelection();
      }
   }

   private void deleteFolder(File dir) throws IOException {
      if (dir.isDirectory()) {
         File[] list = dir.listFiles();
         if (list != null) {
            for (File f : list) {
               deleteFolder(f);
            }
         }
      }
      Files.delete(dir.toPath());
   }

   private void newFolder() {
      String newFolder = Dialogs.textFieldInput(
            "Enter a name for the new folder", "New folder", "");

      if (newFolder == null || newFolder.isEmpty()) {
         tree.clearSelection();
         return;
      }
      File newDir = new File(selectedFile.getPath(), newFolder);
      if (newDir.exists()) {
         Dialogs.errorMessage(
               newDir.getName()
               + " already exists.",
               null);

          tree.clearSelection();
          return;
      }
      boolean succes = newDir.mkdirs();
      if (succes) {
         DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newDir);
         model.insertNodeInto(newNode, selectedNode, 0);
         tree.expandPath(tree.getSelectionPath());
         tree.clearSelection();
      }
      else {
         Dialogs.errorMessage(
               "Creating "
               + newDir.getName()
               + " failed.",
               null);
      }
   }

   private void setExpandedNodeList() {
      expandedNodes = new ArrayList<>();
      for (int i = 0; i < tree.getRowCount(); i++) {
         if (tree.isExpanded(i)) {
            expandedNodes.add(tree.getPathForRow(i));
         }
      }
   }

   private void expand() {
      expandedNodes.forEach(treePath -> {
         for (int i = 0; i < tree.getRowCount(); i++) {
            if (treePath.toString().equals(tree.getPathForRow(i).toString())) {
               tree.expandRow(i);
            }
         }
      });
   }

   private void setActions() {
      popupFile.setOpenAction(e -> openFile());
      popupFile.setDeleteAct(e -> delete());
      popupDir.setNewFolderAct(e -> newFolder());
      popupDir.setDeleteAct(e -> delete());
   }

   private final TreeExpansionListener expansionListner
         = new TreeExpansionListener() {

      @Override
      public void treeExpanded(TreeExpansionEvent event) {
         TreePath expPath = event.getPath();
         DefaultMutableTreeNode node =
               (DefaultMutableTreeNode) expPath.getLastPathComponent();

         int n = model.getChildCount(node);
         for (int i = 0; i < n; i++) {
            DefaultMutableTreeNode childNode
                  = (DefaultMutableTreeNode) model.getChild(node, i);

            Object nodeInfo = childNode.getUserObject();
            File f = (File) nodeInfo;
            if (f.isDirectory() && childNode.isLeaf()) {
               File[] fList = f.listFiles();
               if (fList != null) {
                  File[] fListSorted = sortedFiles(fList);
                  for (File fs : fListSorted) {
                     DefaultMutableTreeNode grandchild = new DefaultMutableTreeNode(fs);
                     childNode.add(grandchild);
                  }
               }
            }
         }
      }

      @Override
      public void treeCollapsed(TreeExpansionEvent event) {
    	   // not used
      }
   };

   private final MouseListener mouseListener = new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
         int row = tree.getRowForLocation(e.getX(), e.getY());
         if (SwingUtilities.isRightMouseButton(e)) {
            tree.setSelectionRow(row);
            setSelection();
            showMenu(e.getComponent(), e.getX(), e.getY());
         }
         else if (SwingUtilities.isLeftMouseButton(e)) {
            if (row == -1) {
               tree.clearSelection();
            }
            if (e.getClickCount() == 2) {
               setSelection();
               if (selectedFile != null) {
                  if (selectedFile.isFile()) {
                     openFile();
                  }
                  else {
                     if (tree.isCollapsed(row)) {
                        tree.expandRow(row);
                     }
                     else {
                        tree.collapseRow(row);
                     }

                  }
               }
            }
         }
      }

      private void setSelection() {
         selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
         if (selectedNode != null) {
            Object nodeInfo = selectedNode.getUserObject();
            selectedFile = (File) nodeInfo;
         }
         else {
            selectedFile = null;
         }
      }

      private void showMenu(Component c, int x, int y) {
         if (selectedFile != null) {
            if (selectedFile.isFile()) {
               popupFile.showMenu(c, x, y);
            }
            else {
               popupDir.enableDelete(isFolderDeletable());
               popupDir.showMenu(c, x, y);
            }
         }
      }

      private boolean isFolderDeletable() {
         File[] content = selectedFile.listFiles();
         if (content == null || selectedFile.compareTo(new File(projRoot)) == 0) {
            return false;
         }
         else {
    	      return content.length == 0
                || (deletableDir != null
                && selectedFile.toString().startsWith(deletableDir));
         }
      }
   };
}
