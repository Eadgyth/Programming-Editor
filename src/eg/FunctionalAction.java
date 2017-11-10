package eg;

import java.awt.event.ActionListener;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * The Action whose ActionPerformed method uses the ActionListener
 * specified in the constructor
 */
public class FunctionalAction extends AbstractAction {
      
   private final ActionListener al;
   
   /**
    * @param name  the name for the action, null to ignore
    * @param icon  the icon for the control
    * @param al  the <code>ActionListener</code>
    */
   public FunctionalAction(String name, Icon icon, ActionListener al) {

      super(name, icon);
      this.al = al;
   }
   
   @Override
   public void actionPerformed(ActionEvent e) {
      al.actionPerformed(e);
   }
} 
