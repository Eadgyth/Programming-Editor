package eg.utils;

import java.io.File;
import java.awt.Toolkit;

/**
 * Static system properties
 */
public class SystemParams {
   /**
    * True if the OS is Windows, false otherwise */
   public static final boolean IS_WINDOWS;
   /**
    * The Java version */
   public static final String JAVA_VERSION;
   /**
    * True if the Java version is higher than 8, false otherwise */
   public static final boolean IS_JAVA_9_OR_HIGHER;
   /**
    * True if the Java version is 13 or higher, false otherwise */
   public static final boolean IS_JAVA_13_OR_HIGHER;
   /**
    * The modifier mask for menu shortcuts */
   public static final int MODIFIER_MASK;
   /**
    * The path to the '.eadgyth' directory in the user home
    * directory */
   public static final String EADGYTH_DATA_DIR;

   static {
      String os = System.getProperty("os.name").toLowerCase();
      IS_WINDOWS = os.contains("win");
      String userHome = System.getProperty("user.home");
      EADGYTH_DATA_DIR = userHome + File.separator + ".eadgyth";
      JAVA_VERSION = System.getProperty("java.version");
      IS_JAVA_9_OR_HIGHER = !JAVA_VERSION.startsWith("1.8");
      IS_JAVA_13_OR_HIGHER = IS_JAVA_9_OR_HIGHER
            && "13".compareTo(JAVA_VERSION) <= 0;
      //
      // up to Java 9:
      MODIFIER_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      //
      // as of Java 10:
      //MODIFIER_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
   }

   /**
    * Returns if the Eadgyth data directory '.eadgyth' exists
    * in the user home directory
    *
    * @return  true if the directory exists, false otherwise
    * @see #EADGYTH_DATA_DIR
    */
   public static boolean existsEadgythDataDir() {
      return new File(EADGYTH_DATA_DIR).exists();
   }

   //
   //--private--/
   //

   private SystemParams() {}
}
