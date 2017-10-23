package eg.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;

import java.util.List;
import java.util.ArrayList;

import java.util.jar.JarInputStream;
import java.util.jar.JarEntry;

//--Eadgyth--//
import eg.javatools.SearchFiles;

/**
 * Static methods that load plugins provided as jar files.
 * <p>
 * Following http://www.java-blog-buch.de/d-plugin-entwicklung-in-java
 * (many thanks)
 */
class PluginLoader {
   
   static List<Pluggable> loadPlugins(File plugDir) throws IOException { 
      File[] plugJars = new SearchFiles().filteredFilesToArr(plugDir.toString(), ".jar");
      ClassLoader cl = new URLClassLoader(PluginLoader.fileArrayToURLArray(plugJars));
      List<Class<Pluggable>> plugClasses = PluginLoader.extractClassesFromJARs(plugJars, cl);
      return PluginLoader.createPluggableObjects(plugClasses);
   }
   
   private static URL[] fileArrayToURLArray(File[] files) throws MalformedURLException {
      URL[] urls = new URL[files.length];
      for (int i = 0; i < files.length; i++) {
         urls[i] = files[i].toURI().toURL();
      }
      return urls;
   }
   
   private static List<Class<Pluggable>> extractClassesFromJARs(File[] jars, ClassLoader cl)
         throws IOException {
      List<Class<Pluggable>> classes = new ArrayList<>();
      for (File jar : jars) {
         classes.addAll(PluginLoader.extractClassesFromJAR(jar, cl));
      }
      return classes;
   }
   
   @SuppressWarnings("unchecked")
   private static List<Class<Pluggable>> extractClassesFromJAR(File jar, ClassLoader cl)
         throws IOException {

      List<Class<Pluggable>> classes = new ArrayList<>();
      JarInputStream jaris = new JarInputStream(new FileInputStream(jar));
      JarEntry ent = null;
      while ((ent = jaris.getNextJarEntry()) != null) {
         if (ent.getName().toLowerCase().endsWith(".class")) {
            try {
               Class<?> cls = cl.loadClass(ent.getName().substring(
                    0, ent.getName().length() - 6).replace('/', '.'));
               if (PluginLoader.isPluggableClass(cls)) {
                  classes.add((Class<Pluggable>)cls);
               }
            }
            catch (ClassNotFoundException e) {
               eg.utils.FileUtils.logStack(e);
            }
        }
     }
     jaris.close();
     return classes;
   }
   
   private static boolean isPluggableClass(Class<?> cls) {
      for (Class<?> i : cls.getInterfaces()) {
         if (i.equals(Pluggable.class)) {
            return true;
         }
      }
      return false;
   }
   
   private static List<Pluggable> createPluggableObjects(List<Class<Pluggable>> pluggables) { 
      List<Pluggable> plugs = new ArrayList<>(pluggables.size());
      for (Class<Pluggable> plug : pluggables) {
         try {
            plugs.add(plug.newInstance());
         }
         catch (InstantiationException | IllegalAccessException e) {
            eg.utils.FileUtils.logStack(e);
         }
      }
      return plugs;
   }
}
