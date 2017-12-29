package eg;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileWriter;

import eg.utils.FileUtils;
import eg.utils.Dialogs;

/**
 * The preferences stored in and read from .properties files
 */
public class Preferences {
   
   /**
    * The name of the config file that is stored in a project folder.
    * The value is eadconfig.properties.
    */
   public final static String CONFIG_FILE = "eadconfig.properties";

   private final static String PREFS_FILE = "prefs.properties";
   private final static String F_SEP = File.separator;
   
   private final static String[] PREFS_KEYS = {
      "recentProject",
      "recentPath",
      "font",
      "fontSize",
      "indentUnit",
      "LaF",
      "toolbar",
      "lineNumbers",
      "statusbar",
      "language",
      "wordWrap",
      "showTabs",
      "iconSize",
      "recentMain",
      "recentModule",
      "recentSourceDir",
      "recentExecDir",
      "recentIncludedExt",
      "recentBuildName"
   };
   
   private final static String[] CONFIG_KEYS = {
      "recentMain",
      "recentModule",
      "recentSourceDir",
      "recentExecDir",
      "recentIncludedExt",
      "recentBuildName"
   };

   private Properties prop = null;
   
   /**
    * Returns the saved value for the specified property.
    * <p>
    * One of the 'read' methods must be used before: {@link #readPrefs()},
    * {@link #readConfig(String)}. These also must be invoked when a
    * property may have changed during runtime.
    *
    * @param property  the property
    * @return  the value for the specified property. The empty string if the
    * property is missing
    */
   public String getProperty(String property) {
      if (prop == null) {
         throw new IllegalStateException("No properties were read in");
      }
      if (prop.getProperty(property) == null) {
         return "";
      }
      else {
         return prop.getProperty(property);
      }
   }

   /**
    * Reads in the properties stored in the prefs.properties file
    */
   public void readPrefs() {
      readProperties(PREFS_FILE);
   }
   
   /**
    * Reads in the properties stored in a config.properties file found
    * in the specified directory
    *
    * @param dir  the directory
    */
   public void readConfig(String dir) {
      readProperties(dir + F_SEP + CONFIG_FILE);
   }

   /**
    * Stores a new value for the specified property in the prefs.properties
    * file
    * @param propToUpdate  the property
    * @param newValue  the new value for the property to update
    */
   public void storePrefs(String propToUpdate, String newValue) {
      readPrefs();
      String[] allValues = new String[PREFS_KEYS.length];
      for (int i = 0; i < PREFS_KEYS.length; i++) {
         allValues[i] = prop.getProperty(PREFS_KEYS[i]);
      }
      for (int i = 0; i < PREFS_KEYS.length; i++) {
         if (PREFS_KEYS[i].equals(propToUpdate)) {
            allValues[i] = newValue;
            break;
         }
      }
      store(PREFS_FILE, PREFS_KEYS, allValues);
   }

   /**
    * Stores a new value for the specified property in a config.properties
    * file saved in the specified directory. Creates the file if it does not
    * exist
    *
    * @param propToUpdate  the property
    * @param newValue  the new value for the property
    * @param dir  the directory
    */
   public void storeConfig(String propToUpdate, String newValue,
         String dir) {
      
      String configFile = dir + F_SEP + CONFIG_FILE;
      if (!new File(configFile).exists()) {
         createFile(configFile, CONFIG_KEYS);
      }
 
      readConfig(dir);      
      String[] allValues = new String[CONFIG_KEYS.length];
      for (int i = 0; i < allValues.length; i++) {
         allValues[i] = prop.getProperty(CONFIG_KEYS[i]);
      }
      for (int i = 0; i < CONFIG_KEYS.length; i++) {
         if (CONFIG_KEYS[i].equals(propToUpdate)) {
            allValues[i] = newValue;
            break;
         }
      }
      store(configFile, CONFIG_KEYS, allValues);
   }
   
   //
   //--private--/
   //
   
   private void createFile(String file, String[] allKeys) {
      prop = new Properties();
      try (FileWriter writer = new FileWriter(file)) {
         for (String allKey : allKeys) {
              prop.setProperty(allKey, "");
          }         
          prop.store(writer, null);
      }
      catch (IOException e){
         FileUtils.logStack(e);
      }
   }
   
   private void readProperties(String file) {
      try (FileInputStream reader = new FileInputStream(file)) {      
        prop = new Properties();
        prop.load(reader); 
      }
      catch (IOException e) {
         if (PREFS_FILE.equals(file)) {
            createFile(PREFS_FILE, PREFS_KEYS);
            Dialogs.warnMessage(PREFS_FILE + " could not be found"
                  + " and is created without presets.");
         }
      }
   }
   
   private void store(String file, String[] allKeys, String[] allValues) {
      try (FileWriter writer = new FileWriter(file)) {
         for (int i = 0; i < allKeys.length; i++) {
            if (allValues[i] == null) {
               prop.setProperty(allKeys[i], "");
            }
            else {
               prop.setProperty(allKeys[i], allValues[i]);
            }
         }         
         prop.store(writer, null);
      }
      catch (IOException e){
         FileUtils.logStack(e);
      }
   }
}
