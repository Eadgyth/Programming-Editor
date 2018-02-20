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
    * The name of the eadproject file that may be stored in a project folder.
    * The value is <code>eadproject.properties</code>.
    */
   public final static String EAD_PROJ_FILE = "eadproject.properties";

   private final static String PREFS_FILE = "prefs.properties";
   
   private final static String[] PREFS_KEYS = {
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
      "projectRoot",
      "mainProjectFile",
      "namespace",
      "sourceDir",
      "execDir",
      "includedFiles",
      "buildName",
      "sourceExtension"
   };
   
   private final static String[] EAD_CONFIG_KEYS = {
      "mainProjectFile",
      "namespace",
      "sourceDir",
      "execDir",
      "includedFiles",
      "buildName",
      "sourceExtension"
   };

   private Properties prop = null;
   
   /**
    * Creates a <code>Preferences</code> that has read in the
    * preferences in the programs "prefs.properties" file
    *
    * @return a new <code>Preferences</code>
    */
   public static Preferences readProgramPrefs() {
      Preferences progrPrefs = new Preferences();
      progrPrefs.readPrefs();
      return progrPrefs;
   }
   
   /**
    * Creates a <code>Preferences</code> that has not read in
    * any entries in a properties file
    *
    * @return a new <code>Preferences</code>
    */
   public static Preferences prefs() {
      Preferences prefs = new Preferences();
      return prefs;
   }
   
   /**
    * Returns the saved value for the specified property.
    *
    * @param property  the property
    * @return  the value for the specified property. The empty string if the
    * property is missing
    */
   public String getProperty(String property) {
      if (prop == null) {
         throw new IllegalStateException("The property " + property
               + " could not be read in.");
      }
      if (prop.getProperty(property) == null) {
         Dialogs.errorMessage("The preference for \"" + property
               + "\" could not be found.",
               "Error reading preferences file");

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
    * Reads in the properties stored in a eadproject.properties file found
    * in the specified directory
    *
    * @param dir  the directory
    */
   public void readEadproject(String dir) {
      readProperties(dir + "/" + EAD_PROJ_FILE);
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
    * Stores a new value for the specified property in an
    * "eadproject.properties" file that is saved in the specified
    * directory. Creates the file if it does not exist
    *
    * @param propToUpdate  the property
    * @param newValue  the new value for the property
    * @param dir  the directory
    */
   public void storeEadproject(String propToUpdate, String newValue,
         String dir) {
      
      String projectFile = dir + "/" + EAD_PROJ_FILE;
      if (!new File(projectFile).exists()) {
         createFile(projectFile, EAD_CONFIG_KEYS);
      }
 
      readEadproject(dir);      
      String[] allValues = new String[EAD_CONFIG_KEYS.length];
      for (int i = 0; i < allValues.length; i++) {
         allValues[i] = prop.getProperty(EAD_CONFIG_KEYS[i]);
      }
      for (int i = 0; i < EAD_CONFIG_KEYS.length; i++) {
         if (EAD_CONFIG_KEYS[i].equals(propToUpdate)) {
            allValues[i] = newValue;
            break;
         }
      }
      store(projectFile, EAD_CONFIG_KEYS, allValues);
   }
   
   //
   //--private--/
   //
   
   private Preferences() {}
   
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
