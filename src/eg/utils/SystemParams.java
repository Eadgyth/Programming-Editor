package eg.utils;

import java.io.File;

/**
 * Static system properties
 */
public class SystemParams {

   /**
    * The OS name */
   private final static String OS_NAME = System.getProperty("os.name");
   /**
    * The user home directory */
   private final static String USER_HOME = System.getProperty("user.home");
   /**
    * True if the OS is Windows, false otherwise */
   public final static boolean IS_WINDOWS = OS_NAME.startsWith("Windows");
   /**
    * The Java version */
   public final static String JAVA_VERSION = System.getProperty("java.version");
   /**
    * True if the Java versino is higher than 8 */
   public final static boolean IS_JAVA_9_OR_HIGHER
         = !JAVA_VERSION.startsWith("1.8");
   /**
    * The line separator */
   public final static String LINE_SEP = System.lineSeparator();
   /**
    * The directory of the '.eadgyth' folder in the user home directory */
   public final static String EADGYTH_DATA_DIR
         = USER_HOME + File.separator + ".eadgyth";
}
