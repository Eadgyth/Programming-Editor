package eg;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;

import eg.utils.FileUtils;

/**
 * The preferences/settings stored in and read from .properties files
 */
public class Preferences {
   
   /**
    * The name of the config file that is stored in a project folder.
    * The value is eadconfig.properties.
    */
   public final static String CONFIG_FILE = "eadconfig.properties";

   private final static String PREFS_FILE = "prefs.properties";
   private final static String SETTINGS_FILE = "settings.properties"; 
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
      "recentMain",
      "recentModule",
      "recentSourceDir",
      "recentExecDir",
      "recentBuildName"
   };
   
   private final static String[] CONFIG_KEYS = {
      "recentMain",
      "recentModule",
      "recentSourceDir",
      "recentExecDir",
      "recentBuildName"
   };

   private Properties prop = null;
   
   /**
    * Returns the saved value for the specified property.
    * <p>
    * One of the read methods must be used before: {@link #readSettings()},
    * {@link #readPrefs()}, {@link #readConfig(String)}
    * @param property  the property whoch a value is searched for
    * @return  the value for the specified property
    */
   public String getProperty(String property) {
      if (prop == null) {
         throw new IllegalStateException("No properties were read in");
      }
      return prop.getProperty(property);
   }
   
   /**
    * Reads in the properties stored in the settings.properties file
    */
   public void readSettings() {
      readProps(SETTINGS_FILE);
   }

   /**
    * Reads in the properties stored in the prefs.properties file
    */
   public void readPrefs() {
      readProps(PREFS_FILE);
   }
   
   /**
    * Reads in the properties stored in the config.properties file
    * @param dir  the directory where the config file is found
    */
   public void readConfig(String dir) {
      readProps(dir + F_SEP + CONFIG_FILE);
   }
   
   /** 
    * Stores a new value for the single property in the
    * settings.proprties file
    * @param newValue  the new value for the property in the
    * settings.properties file
    */
   public void storeSettings(String newValue) {
      readSettings();
      String[] allKeys = {"LocationOfJDK"};
      String[] allValues = { newValue };
      store(SETTINGS_FILE, allKeys, allValues);
   }

   /**
    * Stores a new value for the specified property in the prefs.properties
    * file
    * @param propToUpdate  the property which a new value is assigned to
    * @param newValue  the new value for the property to update
    */
   public void storePrefs(String propToUpdate, String newValue) {
      readPrefs();
      String[] allValues = new String[CONFIG_KEYS.length + PREFS_KEYS.length];
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
    * Stores a new value for the specified property in the config.properties
    * file and creates the file if it does not exist
    * @param propToUpdate  the property which a new value is assigned to
    * @param newValue  the new value for the property to update
    * @param dir  the directory where the eadconfig.properties file is
    * found
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
   //--private methods
   //
   
   private void createFile(String file, String[] allKeys) {
      Writer writer = null;
      prop = new Properties();
      try {
         writer = new FileWriter(file);
          for (String allKey : allKeys) {
              prop.setProperty(allKey, "");
          }         
         prop.store(writer, null);
      }
      catch (IOException e){
         FileUtils.logStack(e);
      }
      finally {
         try {
            if (writer != null) {
               writer.close();
            }
         } catch (IOException e) {
            FileUtils.logStack(e);
         }
      }
   }
   
   private void readProps(String file) { 
      InputStream reader = null;
    
      try {      
        reader = new FileInputStream(file);
        prop = new Properties();
        prop.load(reader); 
      }
      catch (IOException e){
        FileUtils.logStack(e);
      }
      finally {
         try {
            if (reader != null) {
               reader.close();
            }
         } catch (IOException e) {
            FileUtils.logStack(e);
         }
      }
   }
   
   private void store(String file, String[] allKeys, String[] allValues) {
      Writer writer = null;
      try {
         writer = new FileWriter(file);
         for (int j = 0; j < allKeys.length; j++) {
            prop.setProperty(allKeys[j], allValues[j]);
         }         
         prop.store(writer, null);
      }
      catch (IOException e){
        FileUtils.logStack(e);
      }
      finally {
         try {
            if (writer != null) {
               writer.close();
            }
         } catch (IOException e) {
            FileUtils.logStack(e);
         }
      }
   }
}
