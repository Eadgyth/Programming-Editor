package eg.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;

//--Eadgyth--/
import eg.Constants;

/**
 * Defines the panel to which a variable <code>Component</code> can
 * be added
 */
public class EditToolPanel {
   
   private final JPanel content = new JPanel(new BorderLayout());
   
   public EditToolPanel() {
      content.setBorder(Constants.GRAY_BORDER);
   }
   
   /**
    * Gets this <code>JPanel</code> which a Component can be added to
    *
    * @return  the JPanel
    */
   public JPanel content() {
      return content;
   }
   
   /**
    * Adds a component. A previously added component is removed
    *
    * @param c  the <code>Component</code>
    */
   public void addComponent(Component c) {
      if (c == null) {
         throw new IllegalArgumentException("The parameter c is null");
      }
      c.setPreferredSize(new Dimension(c.getWidth(), 0));
      BorderLayout layout = (BorderLayout) content.getLayout();
      Component cCenter = layout.getLayoutComponent(BorderLayout.CENTER);
      if (cCenter != null && cCenter != c) {
         content.remove(cCenter);
      }
      if (cCenter != c) {
         content.add(c, BorderLayout.CENTER);
         content.revalidate();
         content.repaint();
      }
   }
}
