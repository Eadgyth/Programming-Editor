package eg.utils;

import java.io.File;
import java.awt.Toolkit;
import java.util.Locale;

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
    * True if the Java version is higher than 10, false otherwise */
   public static final boolean IS_JAVA_10_OR_HIGHER;
   /**
    * True if the Java version is 13 or higher, false otherwise */
   public static final boolean IS_JAVA_13_OR_HIGHER;
    /**
    * True if the Java version is 14 or higher, false otherwise */
   public static final boolean IS_JAVA_14_OR_HIGHER;
   /**
    * The locale language before locale is set to US in main method */
   public static final String LOCALE_LANG;
   /**
    * The locale country before locale is set to US in main method */
   public static final String LOCALE_COUNTRY;
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
      LOCALE_LANG = Locale.getDefault().getLanguage();
      LOCALE_COUNTRY = Locale.getDefault().getCountry();
      String userHome = System.getProperty("user.home");
      EADGYTH_DATA_DIR = userHome + File.separator + ".eadgyth";
      JAVA_VERSION = System.getProperty("java.version");
      int major = major(JAVA_VERSION);
      IS_JAVA_9_OR_HIGHER = major >= 9;
      IS_JAVA_10_OR_HIGHER = major >= 10;
      IS_JAVA_13_OR_HIGHER = major >= 13;
      IS_JAVA_14_OR_HIGHER = major >= 14;
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
   
   private static int major(String version) {
      int major;
      if (version.startsWith("1.")) {
         major = 8;
      } else {
         major = Integer.parseInt(version.split("[.\\-+]")[0]);
      }
      return major;
   }
      
}
