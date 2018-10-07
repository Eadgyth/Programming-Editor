package eg;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileWriter;

//--Eadgyth--/
import eg.utils.FileUtils;
import eg.utils.Dialogs;

/**
 * The reading from and writing to the Prefs file in the program folder and
 * a ProjectConfig file in a project folder
 */
public class Prefs {

   /**
    * The name of the ProjConfig file that may be stored in a project
    */
   public final static String PROJ_CONFIG_FILE = "ProjConfig.properties";

   private final static String PREFS_FILE = "Prefs.properties";
   private final static Properties PREF_PROP = new Properties();

   private final Properties projConfigProp = new Properties();
   private final Properties prop;

   private String file;

  /**
   * Creates a <code>Prefs</code> that reads from and writes to the
   * Prefs file in the program folder. Every new <code>Prefs</code>
   * object accesses the same set of properties. The properties
   * are not loaded from the Prefs file.
   */
   public Prefs() {
      prop = PREF_PROP;
      file = PREFS_FILE;
   }

   /**
    * Creates a <code>Prefs</code> that reads from and writes to
    * a ProjConfig file in the specified project directory. The
    * properties are loaded if the file exists. Every new
    * <code>Prefs</code> object accesses an own set of properties.
    *
    * @param projectDir  the directory
    */
   public Prefs(String projectDir) {
      prop = projConfigProp;
      file = projectDir + "/" + PROJ_CONFIG_FILE;
      if (new File(file).exists()) {
         load();
      }
   }

   /**
    * Sets a new value for the property that corresponds to the
    * the specified key.
    *
    * @param key  the property key
    * @param value  the new value
    */
   public void setProperty(String key, String value) {
      prop.setProperty(key, value);
   }

   /**
    * Returns the value of the property that corresponds to the specified
    * key
    *
    * @param key  the property key
    * @return  the value or the empty string if the property coud not be
    * found
    */
   public String getProperty(String key) {
      String res = prop.getProperty(key);
      if (res == null) {
         return "";
      }
      else {
         return res;
      }
   }

   /**
    * Loads the properties from file
    */
   public void load() {
      try (FileInputStream reader = new FileInputStream(file)) {
         prop.load(reader);
      }
      catch (IOException e) {
         if (prop == PREF_PROP) {
            Dialogs.warnMessage(
                  " The Prefs file could not be found."
                  + " The editor is started without presets.");
         }
      }
   }

   /**
    * Writes the properties to file
    */
   public void store() {
      try (FileWriter writer = new FileWriter(file)) {
         prop.store(writer, null);
      }
      catch (IOException | NullPointerException | ClassCastException e){
         FileUtils.log(e);
      }
   }
}
