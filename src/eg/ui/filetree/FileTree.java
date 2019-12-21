package eg.ui.filetree;

import java.awt.Component;
import java.awt.EventQueue;

import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.io.File;

import java.util.ArrayList;
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

   private JTree tree = null;
   private DefaultTreeModel model;
   private DefaultMutableTreeNode root;

   private String projRoot = "";
   private String currentRoot = "";
   private String deletableDir = null;
   private List<TreePath> expandedNodes = null;
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
      if (!projRoot.equals(projectRoot)) {
         projRoot = projectRoot;
         setNewTree(projRoot);
      }
   }

   /**
    * Sets the directory that may be deleted (by using this popup
    * menu) although it is not empty. Usually, non-empty folders
    * are protected against deletion.
    *
    * @param deletableDirName  the name of the directory that is
    * deletable. Null or the empty string to not set a deletable
    * directory
    */
   public void setDeletableDir(String deletableDirName) {
      if (projRoot.length() == 0) {
         throw new IllegalStateException("No project root has been set");
      }
      if (deletableDirName == null || deletableDirName.isEmpty()) {
         deletableDir = null;
      }
      else {
         deletableDir = projRoot + File.separator + deletableDirName;
      }
   }

   /**
    * Updates the tree at the currently shown root
    */
   public void updateTree() {
      if (currentRoot.isEmpty()) {
         return;
      }
      EventQueue.invokeLater(() -> {
         setExpandedNodeList();
         setNewTree(currentRoot);
      });
   }

   /**
    * Gets this currently shown root which may be a subdirectory
    * of the initial project root
    *
    * @return  the root
    */
   public String currentRoot() {
      return currentRoot;
   }

   //
   //--private--/
   //

   private void setNewTree(String path) {
      if (path.isEmpty()) {
         return;
      }
      currentRoot = path;
      treePnl.enableFolderUpAct(!path.equals(projRoot));
      File rootFile = new File(path);
      if (SwingUtilities.isEventDispatchThread()) {
         TreeSetter ts = new TreeSetter(rootFile);
         ts.execute();
      }
      else {
         setModel(rootFile);
         setTree();
      }
   }

   private class TreeSetter extends SwingWorker {

      File rootFile;

      private TreeSetter(File rootFile) {
         this.rootFile = rootFile;
      }

      @Override
      protected Void doInBackground() {
         setModel(rootFile);
         return null;
      }

      @Override
      protected void done() {
         setTree();
      }
   }

   private void setModel(File f) {
      root = new DefaultMutableTreeNode(f);
      model = new DefaultTreeModel(root);
      getFiles(root, f);
   }

   private void setTree() {
      if (tree == null) {
         tree = new JTree(model);
         tree.addMouseListener(mouseListener);
         treePnl.setTree(tree);
      }
      else {
         tree.setModel(model);
         expand();
      }
   }

   private void getFiles(DefaultMutableTreeNode node, File f) {
      File fList[] = f.listFiles();
      if (fList != null) {
         File fListSorted[] = sortedFiles(fList);
         for (File fs : fListSorted) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(fs);
            node.add(child);
            if (fs.isDirectory()) {
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
      File[] sortedList = all.toArray(new File[toSort.length]);
      return sortedList;
   }

   private void folderDown() {
      setNewTree(selectedFile.toString());
      tree.expandRow(0);
   }

   private void folderUp() {
      if (!projRoot.equals(currentRoot)) {
         String parent = new File(currentRoot).getParent();
         setNewTree(parent);
         tree.expandRow(0);
      }
   }

   private void showMenu(Component c, int x, int y) {
      if (selectedFile != null) {
         if (selectedFile.isFile()) {
            popupFile.showMenu(c, x, y);
         }
         else {
            boolean deletable
                  = isFolderEmpty(selectedFile)
                  || (deletableDir != null
                  && selectedFile.toString().startsWith(deletableDir));

            popupDir.enableDelete(deletable);
            popupDir.showMenu(c, x, y);
         }
      }
   }

   private boolean isFolderEmpty(File dir) {
      File[] content = dir.listFiles();
      return content.length == 0;
   }

   private void openFile() {
      opener.open(selectedFile);
      tree.clearSelection();
   }

   private void deleteFile() {
      if (!FileUtils.isWriteable(selectedFile)) {
         return;
      }
      int res = Dialogs.warnConfirmYesNo(
            selectedFile.getName()
            + " will be permanently deleted!\nContinue?");

      if (res == JOptionPane.YES_OPTION) {
         boolean success;
         if (selectedFile.isFile()) {
            success = selectedFile.delete();
         }
         else {
            success = deleteFolder(selectedFile);
         }
         if (success) {
            if (selectedNode == root) {
               folderUp();
            }
            else {
               model.removeNodeFromParent(selectedNode);
            }
         }
         else {
            Dialogs.errorMessage(
                  "Deleting "
                  + selectedFile.getName()
                  + " failed", null);
         }
      }
      else {
         tree.clearSelection();
      }
   }

   private boolean deleteFolder(File dir) {
      boolean b = true;
      if (dir.isDirectory()) {
         for (File f : dir.listFiles()) {
            b = b && deleteFolder(f);
         }
      }
      return b && dir.delete();
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

   private void setExpandedNodeList() {
      expandedNodes = new ArrayList<>();
      for (int i = 0; i < tree.getRowCount(); i++) {
         if (tree.isExpanded(i)) {
            expandedNodes.add(tree.getPathForRow(i));
         }
      }
   }

   private void expand() {
      expandedNodes.forEach((treePath) -> {
         for (int i = 0; i < tree.getRowCount(); i++) {
            if (treePath.toString().equals(tree.getPathForRow(i).toString())) {
               tree.expandRow(i);
            }
         }
      });
   }

   private void setActions() {
      treePnl.setFolderUpAction(e -> folderUp());
      treePnl.setRenewTreeAction(e -> updateTree());
      popupFile.setOpenAction(e -> openFile());
      popupFile.setDeleteAct(e -> deleteFile());
      popupDir.setNewFolderAct(e -> newFolder());
      popupDir.setDeleteAct(e -> deleteFile());
   }

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
                     folderDown();
                  }
               }
            }
         }
      }
   };
}
