package eg.ui.filetree;

import java.awt.Component;

import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

//--Eadgyth--/
import eg.utils.Dialogs;
import eg.utils.FileUtils;

/**
 * The display of a project's file system in a <code>JTree</code>.
 */
public class FileTree extends Observable {

   private final TreePanel treePnl;
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
    * @param treePanel  the reference to {@link TreePanel}
    */
   public FileTree(TreePanel treePanel) {
      treePnl = treePanel;
      setActions();   
   }

   /**
    * Sets the project's root directory and displays the file system at
    * this root if the same root is not aready set
    *
    * @param projectRoot  the project's root directory
    */
   public void setProjectTree(String projectRoot) {
      if (!projRoot.equals(projectRoot)) {
         projRoot = projectRoot;
         setNewTree(projRoot);
         tree.expandRow(0);
      }
   }
   
   /**
    * Sets the directory that may be deleted (by using this popup menu)
    * although it is not empty. Usually, non-empty folders are protected
    * against deletion.
    *
    * @param deletableDirName  the name of the directory that is deletable.
    * Null or the empty string to not set a deletable directory
    */
   public void setDeletableDir(String deletableDirName) {
      if (projRoot.length() == 0) {
         throw new IllegalStateException("No project root has been set");
      }
      if (deletableDirName == null || deletableDirName.length() == 0) {
         deletableDir = null;
      }      
      else {
         deletableDir = projRoot + File.separator + deletableDirName;
      }     
   }

   /**
    * Updates the tree at the currently shown root.
    */
   public void updateTree() {
      setExpandedNodeList();
      setNewTree(currentRoot);
      expand();
   }

   //
   //--private--/
   //

   private void setNewTree(String path) {
      if (path.length() == 0) {
         return;
      }
      currentRoot = path;
      treePnl.enableFolderUpAct(!path.equals(projRoot));
      File rootFile = new File(path);
      root = new DefaultMutableTreeNode(rootFile);
      model = new DefaultTreeModel(root);
      getFiles(root, rootFile);
      if (tree == null) {
         tree = new JTree(model);
         tree.addMouseListener(mouseListener);
         treePnl.setTree(tree);
      }
      else {
         tree.setModel(model);
      }
   }

   private void getFiles(DefaultMutableTreeNode node, File f) {
      File fList[] = f.listFiles();
      if (fList != null) {
         File fListSorted[] = FileUtils.sortedFiles(fList);
         for (File fs : fListSorted) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(fs);
            node.add(child);
            if (fs.isDirectory()) {
               getFiles(child, fs);
            }
         }
      }
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
                  = FileUtils.isFolderEmpty(selectedFile)
                  || (deletableDir != null
                  && selectedFile.toString().startsWith(deletableDir));

            popupDir.enableDelete(deletable);
            popupDir.showMenu(c, x, y);
         }
      }
   }
   
   private void openFile() {
      setChanged();
      notifyObservers(selectedFile);
   }

   private void deleteFile() {
      int res = Dialogs.warnConfirmYesNo(
            selectedFile.getName() + " will be permanently deleted!\nContinue?");
            
      if (res == JOptionPane.YES_OPTION) {
         boolean success;
         if (selectedFile.isFile()) {
            success = selectedFile.delete();
         }
         else {
            success = FileUtils.deleteFolder(selectedFile);
         }
         if (success) {
            System.out.println(tree.getModel().getRoot());
            if (selectedNode == tree.getModel().getRoot()) {
               folderUp();
            }
            else {
               model.removeNodeFromParent(selectedNode);
            }
         }
         else {
            Dialogs.errorMessage("Deleting " + selectedFile.getName()
                  + " failed", null);
         }
      }
   }

   private void newFolder() {
      String newFolder = Dialogs.textFieldInput(
            "Enter a name for the new folder", "New folder", "");

      if (newFolder != null) {
         File newDir = new File(selectedFile.getPath(), newFolder);
         boolean succes = newDir.mkdirs();
         if (succes) {
            model.insertNodeInto(new DefaultMutableTreeNode(newDir),
                  selectedNode, selectedNode.getChildCount());
         }
         else {
            Dialogs.errorMessage("Creating " + newDir.getName() + " failed", null);
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
      popupFile.deleteAct(e -> deleteFile());
      popupDir.newFolderAct(e -> newFolder());
      popupDir.deleteAct(e -> deleteFile());
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
