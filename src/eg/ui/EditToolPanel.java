package eg.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;

//--Eadgyth--/
import eg.Constants;

/**
 * Defines the panel to which a variable <code>Component</code> can
 * be added. The component is added in the center of a JPanel with
 * a BorderLayout. The JPanel has a {@link Constants#GRAY_BORDER}. 
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
    * Adds a component. A previously added component is replaced
    *
    * @param c  the Component
    * @return  true if the component was added, false if the same
    * component has already been added
    */
   public boolean addComponent(Component c) {
      if (c == null) {
         throw new IllegalArgumentException("c is null");
      }
      BorderLayout layout = (BorderLayout) content.getLayout();
      Component cPrev = layout.getLayoutComponent(BorderLayout.CENTER);
      if  (cPrev != c) {
         if (cPrev != null) {
            content.remove(cPrev);
         }
         content.add(c, BorderLayout.CENTER);
         content.revalidate();
         content.repaint();
         return true;
      }
      else {
         return false;
      }
   }
}
