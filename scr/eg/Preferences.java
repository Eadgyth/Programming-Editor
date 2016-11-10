package eg;

import java.util.Properties;
import java.io.*;

public class Preferences {

   public Properties prop = null; 

   public void readSettings() { 
      InputStream reader = null;
  
      try {      
        reader = new FileInputStream("settings.properties");
        prop = new Properties();
        prop.load( reader );
      }
      catch ( IOException e ) {
        e.printStackTrace();
      }
      finally {
        try {
           reader.close();
        }
        catch ( Exception e ) { }
      }
   }
   
   public void storeSettings(String newSetting) {
      readSettings();
      
      Writer writer = null;

      try {
         writer = new FileWriter("settings.properties");
         prop.setProperty("LocationOfJDK", newSetting);        
         prop.store(writer, null);
      }
      catch ( IOException e ){
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

   public void readPrefs() { 
      InputStream reader = null;
    
      try {      
        reader = new FileInputStream("prefs.properties");
        prop = new Properties();
        prop.load(reader); 
      }
      catch ( IOException e ){
        e.printStackTrace();
      }
      finally {
         try {
            reader.close();
         } catch ( Exception e ) {
            e.printStackTrace();
         }
      }
   }

   public void storePrefs(String propToUpdate, String newProperty) {
      readPrefs();

      String[] allKeys = { 
            "font",
            "fontSize",
            "recentPath",
            "recentProject",
            "recentModule",
            "recentSourceDir",
            "recentExecDir",
            "recentMain",
            "indentUnit",
            "LaF",
            "toolbar",
            "lineNumbers",
            "statusbar",
            "language",
       };
 
      //search property to update
      int i;
      for (i = 0; i < allKeys.length
            && !allKeys[i].equals(propToUpdate); i++);

      String[] allValues = {
            prop.getProperty("font" ),
            prop.getProperty("fontSize"),
            prop.getProperty("recentPath"),
            prop.getProperty("recentProject"),
            prop.getProperty("recentModule" ),
            prop.getProperty("recentSourceDir" ),
            prop.getProperty("recentExecDir"),
            prop.getProperty("recentMain" ),
            prop.getProperty("indentUnit" ),
            prop.getProperty("LaF" ),
            prop.getProperty("toolbar" ),
            prop.getProperty("lineNumbers" ),
            prop.getProperty("statusbar"),
            prop.getProperty("language")
      };
      allValues[i] = newProperty;

      Writer writer = null;

      try {
         writer = new FileWriter("prefs.properties");
         for ( int j = 0; j < allKeys.length; j++ ) {
            prop.setProperty( allKeys[j], allValues[j] );
         }         
         prop.store( writer, null );
      }
      catch ( IOException e ){
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