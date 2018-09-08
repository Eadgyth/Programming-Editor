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
   
   private final JPanel pnl = new JPanel(new BorderLayout());
   
   public EditToolPanel() {
      pnl.setBorder(Constants.GRAY_BORDER);
   }
   
   /**
    * Gets this <code>JPanel</code> which a Component can be added to
    *
    * @return  the JPanel
    */
   public JPanel panel() {
      return pnl;
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
      BorderLayout layout = (BorderLayout) pnl.getLayout();
      Component cCenter = layout.getLayoutComponent(BorderLayout.CENTER);
      if (cCenter != null && cCenter != c) {
         pnl.remove(cCenter);
      }
      if (cCenter != c) {
         pnl.add(c, BorderLayout.CENTER);
         pnl.revalidate();
         pnl.repaint();
      }
   }
}
   
