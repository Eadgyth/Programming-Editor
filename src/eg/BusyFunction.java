package eg;

/**
 * The interface intended to run an action during which a wait
 * cursor is displayed
 */
@FunctionalInterface
public interface BusyFunction {
   
   /**
    * Runs the action
    */
   public void run();
}
