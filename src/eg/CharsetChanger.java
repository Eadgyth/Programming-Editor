package eg;

import java.nio.charset.Charset;

/**
 * The interface to change the charset
*/
@FunctionalInterface
public interface CharsetChanger {

   /**
    * Changes the charset
    *
    * @param charset  the charset
    */
   public void change(Charset charset);
}
