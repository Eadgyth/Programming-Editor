package eg.javatools;

import java.io.File;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;

//--Eadgyth--/
import eg.utils.FileUtils;

/**
 * Stores libraries in different configurations
 */
public class Libraries {
   
   private static final String F_SEP = File.separator;

   private final List<String> libs = new ArrayList<>();
   private final List<String> libsAbs = new ArrayList<>();
   private final List<String> libsForJar = new ArrayList<>();
   private final StringBuilder notFound = new StringBuilder();

   private String joined = "";
   private String joinedAbsPath = "";
   private String errMsg = "";

   /**
    * Configures the libraries. Entries may be existing absolute paths
    * or paths that exist in the specified poject directory. Invalid
    * entries can be retrieved by {@link #errorMessage}.
    *
    * @param libraries  the list of (putative) libraries
    * @param projectDir  the directory of the project
    */
   public void configureLibraries(List<String> libraries, String projectDir) {
      notFound.setLength(0);
      libs.clear();
      libsAbs.clear();
      libsForJar.clear();
      if (!libraries.isEmpty()) {
         for (String s : libraries) {
            File f = new File(s);
            if (f.exists() && f.isAbsolute()) {
               libs.add(s);
               libsAbs.add(s);
               try {
                  libsForJar.add(f.toURI().toURL().toString());
               }
               catch (MalformedURLException e) {
                  FileUtils.log(e);
               }
            }
            else {
               String absInProject = projectDir + F_SEP + s;
               f = new File(absInProject);
               if (f.exists()) {
                  libs.add(s);
                  libsAbs.add(absInProject);
                  String forJar = s.replace(F_SEP, "/");
                  if (f.isFile()) {
                     libsForJar.add(forJar);
                  }
                  else {
                     if (forJar.endsWith("/")) {
                        libsForJar.add(forJar);
                     }
                     else {
                        libsForJar.add(forJar + "/");
                     }
                  }
               }
               else {
                  notFound.append("\n").append(s);
               }
            }
         }
      }
      joined = joinedLibs(libs);
      joinedAbsPath = joinedLibs(libsAbs);
      errMsg = notFound.length() == 0 ? "" :
            "The following libraries cannot be found:" + notFound.toString();
   }

   /**
    * Gets the message that contains libraries that could not
    * be found
    *
    * @return  the message, the empty string if all libraries are
    * found
    */
   public String errorMessage() {
      return errMsg;
   }

   /**
    * Gets a string in which the libraries are joined with
    * the system's path separator
    *
    * @return  the joined libraries
    */
   public String joined() {
      return joined;
   }

   /**
    * Gets a string in which the libraries are joined with the
    * system's path separator and libraries that were given as
    * paths relative to the project directory are converted to
    * absolute paths
    *
    * @return  the joined libraries
    */
   public String joinedAbsPaths() {
      return joinedAbsPath;
   }

   /**
    * Gets the list that contains the libraries as absolute paths
    * including those that were given as paths relative to the
    * project directory
    *
    * @return  the list
    */
   public List<String> absPaths() {
      return libsAbs;
   }

   /**
    * Gets the list that contains the libraries formatttted for
    * the classpath entry in a manifest file. Absolute paths are
    * converted to URLs and folders end with a slash.
    *
    * @return  the list
    */
   public List<String> forJar() {
      return libsForJar;
   }

   //
   //--private--/
   //

   private String joinedLibs(List<String> l) {
      if (l.isEmpty()) {
         return "";
      }
      else {
         return String.join(File.pathSeparator, l);
      }
   }
}
