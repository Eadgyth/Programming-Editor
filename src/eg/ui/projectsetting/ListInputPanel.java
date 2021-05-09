package eg.ui.projectsetting;

import java.io.File;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import javax.swing.border.MatteBorder;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.util.ArrayList;
import java.util.List;

//--Eadgyth--/
import eg.FileChooser;
import eg.ui.UIComponents;
import eg.utils.ScreenParams;

/**
 * Defines a panel with a list of text fields and buttons to modify
 * the list
 */
public class ListInputPanel {

   private static final Dimension DIM_TF = ScreenParams.scaledDimension(350, 14);
   private static final Color SEL_TF_YELLOW = new Color(250, 250, 170);

   private final JPanel content = new JPanel(new BorderLayout());
   private final JScrollPane scroll = new JScrollPane(
         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

   private final JButton addBt    = new JButton("Add");
   private final JButton removeBt = new JButton("Remove");
   private final JButton upBt     = new JButton("Up");
   private final JButton downBt   = new JButton("Down");
   private final JButton browseBt = new JButton("...");
   private final JButton setRelBt = new JButton("Rel.");
   private final JPanel  inScroll = new JPanel(new FlowLayout(FlowLayout.RIGHT));
   private final JPanel  holder   = new JPanel();
   private final List<JTextField> tfList = new ArrayList<>(5);
   private final FileChooser chooser;

   private String projDir;
   private String projDirInput = "";
   private int tfIndex = 0;

   /**
    * Creates a <code>ListInputPanel</code> that initially shows one
    * text field
    *
    * @param label  the label for the panel
    * @param chooser  the FileChooser that is configured to choose
    * files or directories
    */
   public ListInputPanel(String label, FileChooser chooser) {
      this.chooser = chooser;
      initContent(label);
      setActions();
   }

   /**
    * Gets this <code>JPanel</code> that displays this content
    *
    * @return  the JPanel
    */
   public JPanel content() {
      return content;
   }

   /**
    * Sets the directory that is or is contained in the putative
    * project directory or the known project directory
    *
    * @param dir  the directory
    */
   public void setDirectory(String dir) {
      projDir = dir;
   }

   /**
    * Tries to set the project directory by searching the path of
    * the directory set in {@link #setDirectory(String)} upwards
    * for the specified directory name
    *
    * @param projDirInput  the last name of the putative or known
    * directory path
    */
   public void trySetProjectDir(String projDirInput) {
      this.projDirInput = "";
      if (projDirInput.isEmpty()) {
         return;
      }
      File f = new File(projDir);
      while (f != null) {
         if (f.getName().equals(projDirInput)) {
            this.projDirInput = f.getPath();
            break;
         }
         f = f.getParentFile();
      }
   }

   /**
    * Displays the specified list of strings in this list of text fields
    *
    * @param l  the list of strings
    */
   public void displayList(List<String> l) {
      if (l.isEmpty()) {
         return;
      }
      for (int i = 0; i < l.size(); i++) {
         if (i == tfList.size()) {
            addTf(false);
         }
         tfList.get(i).setText(l.get(i));
      }
   }

   /**
    * Assigns the text in this list of text fields to the specified
    * list. Empty text fields are skipped, slashes may be converted
    * to the system's file separator and leading and trailing spaces
    * are removed.
    *
    * @param l  the list
    */
   public void assignListInput(List<String> l) {
      l.clear();
      for (int i = 0; i < tfList.size(); i++) {
         String s = tfList.get(i).getText().trim().replace("/", File.separator);
         if (!s.isEmpty()) {
            l.add(s);
         }
      }
   }

   /**
    * Updates this controls when this content is made visible
    */
   public void updateWhenSetVisible() {
      boolean hasParentFocus = content.getParent().hasFocus();
      if (!tfList.isEmpty()) {
         if (!hasParentFocus) {
            tfList.get(tfIndex).requestFocusInWindow();
         }
         else {
            tfList.get(tfIndex).setBackground(SEL_TF_YELLOW);
         }
         enableButtons();
      }
   }

   //
   //--private--/
   //

   private void addTf(boolean focus) {
      int i = tfList.size();
      tfList.add(new JTextField());
      tfList.get(i).setFont(ScreenParams.scaledFontToPlain(tfList.get(i).getFont(), 8));
      tfList.get(i).setPreferredSize(DIM_TF);
      tfList.get(i).addFocusListener(focusListener);
      tfList.get(i).getDocument().addDocumentListener(docListener);
      holder.add(tfList.get(i));
      holder.revalidate();
      if (focus) {
         tfList.get(i).requestFocusInWindow();
         java.awt.EventQueue.invokeLater(() -> {
            JScrollBar bar = scroll.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
         });
      }
   }

   private void removeTf() {
      if (tfList.size() == 1) {
         tfList.get(0).setText("");
         removeBt.setEnabled(false);
         setRelBt.setEnabled(false);
      }
      else {
         holder.remove(tfList.get(tfIndex));
         tfList.remove(tfIndex);
         holder.revalidate();
         holder.repaint();
         if (!tfList.isEmpty()) {
            tfList.get(tfList.size() - 1).requestFocusInWindow();
         }
      }
   }

   private void moveDown() {
      String selected = tfList.get(tfIndex).getText();
      String next = tfList.get(tfIndex + 1).getText();
      tfList.get(tfIndex).setText(next);
      tfList.get(tfIndex + 1).setText(selected);
      tfList.get(tfIndex + 1).requestFocusInWindow();
   }

   private void moveUp() {
      String selected = tfList.get(tfIndex).getText();
      String prev = tfList.get(tfIndex - 1).getText();
      tfList.get(tfIndex).setText(prev);
      tfList.get(tfIndex - 1).setText(selected);
      tfList.get(tfIndex - 1).requestFocusInWindow();
   }

   private void setPath() {
      File file = chooser.selectedFileOrDirectory();
      if (file != null) {
         String text = file.getPath();
         tfList.get(tfIndex).setText(text);
         tfList.get(tfIndex).requestFocusInWindow();
      }
   }

   private void setRelPath() {
      String toSet = "";
      String abs = tfList.get(tfIndex).getText();
      File fAbs = new File(abs);
      while (fAbs != null) {
         if (fAbs.getPath().equals(projDirInput)) {
            toSet = abs.substring(fAbs.getPath().length() + 1);
            tfList.get(tfIndex).setText(toSet);
            break;
         }
         fAbs = fAbs.getParentFile();
      }
   }

   private void enableButtons() {
      removeBt.setEnabled(tfList.size() > 1
            || (tfList.size() == 1 && !tfList.get(0).getText().isEmpty()));

      upBt.setEnabled(tfIndex > 0);
      downBt.setEnabled(tfIndex < tfList.size() - 1);
      setRelBt.setEnabled(!tfList.get(tfIndex).getText().isEmpty()
               && !projDirInput.isEmpty());
   }

   private void initContent(String label) {
      holder.setLayout(new BoxLayout(holder, BoxLayout.Y_AXIS));
      inScroll.add(holder);
      scroll.setViewportView(inScroll);
      scroll.setBorder(null);
      scroll.getVerticalScrollBar().setUnitIncrement(5);
      content.add(UIComponents.labelPanel(label), BorderLayout.NORTH);
      content.add(scroll, BorderLayout.CENTER);
      content.add(buttonPanel(), BorderLayout.SOUTH);
      content.setPreferredSize(ScreenParams.scaledDimension(0, 100));
      addTf(false);
   }

   private JPanel buttonPanel() {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      pnl.setBorder(new MatteBorder(1, 0, 0, 0, Color.WHITE));
      pnl.add(browseBt);
      pnl.add(setRelBt);
      pnl.add(addBt);
      pnl.add(removeBt);
      pnl.add(upBt);
      pnl.add(downBt);
      return pnl;
   }

   private void setActions() {
      browseBt.addActionListener(e -> setPath());
      browseBt.setToolTipText("Browse");

      setRelBt.addActionListener(e -> setRelPath());
      setRelBt.setEnabled(false);
      setRelBt.setToolTipText("Try to convert to a path relative to project");

      addBt.addActionListener(e -> addTf(true));
      addBt.setToolTipText("Add new field");

      removeBt.addActionListener(e -> removeTf());
      removeBt.setEnabled(false);
      removeBt.setToolTipText(
            "Romove selected field or clear if the field is the only one");

      downBt.addActionListener(e -> moveDown());
      downBt.setEnabled(false);
      downBt.setToolTipText("Move selected field downward");

      upBt.addActionListener(e -> moveUp());
      upBt.setEnabled(false);
      upBt.setToolTipText("Move selected field upward");
   }

   DocumentListener docListener = new DocumentListener() {

      @Override
      public void changedUpdate(DocumentEvent documentEvent) {
    	   // not used
      }

      @Override
      public void insertUpdate(DocumentEvent documentEvent) {
         setRelBt.setEnabled(true);
         removeBt.setEnabled(true);
      }

      @Override
      public void removeUpdate(DocumentEvent documentEvent) {
         boolean enable = tfList.get(tfIndex).getText().length() > 0;
         setRelBt.setEnabled(enable);
         removeBt.setEnabled(enable || tfList.size() > 1);
      }
   };

   private final FocusListener focusListener = new FocusListener() {

      @Override
      public void focusGained(FocusEvent e) {
         tfIndex = tfList.indexOf(e.getComponent());
         tfList.get(tfIndex).setBackground(SEL_TF_YELLOW);
         setWhite();
         enableButtons();
      }

      @Override
      public void focusLost(FocusEvent e) {
         // not used
      }

      private void setWhite() {
         for (int i = 0; i < tfList.size(); i++) {
            if (i != tfIndex) {
               tfList.get(i).setBackground(Color.WHITE);
            }
         }
      }
   };
}
