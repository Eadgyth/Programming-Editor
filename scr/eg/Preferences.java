package eg;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;

public class Preferences {
   
   private final static String[] PREFS_KEYS = { 
      "recentProject",
      "recentMain",
      "recentModule",
      "recentSourceDir",
      "recentExecDir",
      "recentPath",
      "font",
      "fontSize",
      "indentUnit",
      "LaF",
      "toolbar",
      "lineNumbers",
      "statusbar",
      "language",
      "wordWrap"
   };
   
   private final static String[] CONFIG_KEYS = {
      "recentMain",
      "recentModule",
      "recentSourceDir",
      "recentExecDir",
   };

   private Properties prop = null;
   
   public String getProperty(String search) {
      return prop.getProperty(search);
   }
   
   public void readSettings() {
      readProps("settings.properties");
   }

   public void readPrefs() {
      readProps("prefs.properties");
   }
   
   public void readConfig(String dir) {
      readProps(dir + File.separator + "config.properties");
   }
   
   public void storeSettings(String newSetting) {
      readSettings();
      String[] allKeys = {
         "LocationOfJDK"
      };
      String[] allValues = {
         prop.getProperty("LocationOfJDK")
      };
      allValues[0] = newSetting;
      store("settings.properties", allKeys, allValues);
   }

   public void storePrefs(String propToUpdate, String newProperty) {
      readPrefs();

      String[] allValues = {
         prop.getProperty("recentProject"),
         prop.getProperty("recentMain"),
         prop.getProperty("recentModule"),
         prop.getProperty("recentSourceDir"),
         prop.getProperty("recentExecDir"),
         prop.getProperty("recentPath"),
         prop.getProperty("font"),
         prop.getProperty("fontSize"),
         prop.getProperty("indentUnit"),
         prop.getProperty("LaF"),
         prop.getProperty("toolbar"),
         prop.getProperty("lineNumbers"),
         prop.getProperty("statusbar"),
         prop.getProperty("language"),
         prop.getProperty("wordWrap"),
      };
      int i;
      for (i = 0; i < PREFS_KEYS.length; i++) {
         if (PREFS_KEYS[i].equals(propToUpdate)) {
            allValues[i] = newProperty;
            break;
         }
      }

      store("prefs.properties", PREFS_KEYS, allValues);
   }

   public void storeConfig(String propToUpdate, String newProperty,
         String dir) {
      
      String configFile = dir + File.separator + "config.properties";
      if (!new File(configFile).exists()) {
         createFile(configFile, CONFIG_KEYS);
      }
      readConfig(dir);

      String[] allValues = {
         prop.getProperty("recentMain"),
         prop.getProperty("recentModule"),
         prop.getProperty("recentSourceDir"),
         prop.getProperty("recentExecDir"),
      };

      int i;
      for (i = 0; i < CONFIG_KEYS.length; i++) {
         if (CONFIG_KEYS[i].equals(propToUpdate)) {
            allValues[i] = newProperty;
            break;
         }
      }

      store(configFile, CONFIG_KEYS, allValues);
   }
   
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
        e.printStackTrace();
      }
      finally {
         try {
            writer.close();
         } catch ( Exception e ) {
            e.printStackTrace();
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
      catch ( IOException e ){
        e.printStackTrace();
      }
      finally {
         try {
            if (reader != null) {
               reader.close();
            }
         } catch (IOException e) {
            e.printStackTrace();
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
        e.printStackTrace();
      }
      finally {
         try {
            writer.close();
         } catch ( Exception e ) {
            e.printStackTrace();
         }
      }
   }
}