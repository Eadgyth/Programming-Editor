package eg.javatools;

import java.io.File;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;

//--Eadgyth--/
import eg.utils.FileUtils;

/**
 * Stores configured libraries that can be added to the classpath
 */
public class Libraries {

   private static final String F_SEP = File.separator;

   private final List<String> libs = new ArrayList<>();
   private final List<String> libsAbs = new ArrayList<>();
   private final List<String> libsForJar = new ArrayList<>();
   private final StringBuilder notFound = new StringBuilder();

   private String joined = "";
   private String joinedAbs = "";

   private String errMsg = "";

   /**
    * Configures the libraries
    *
    * @param libraries  the list of paths of (putative) libraries
    * which may be relative to the project directory or absolute
    * @param projectDir  the project directory
    */
   public void configure(List<String> libraries, String projectDir) {
      notFound.setLength(0);
      libs.clear();
      libsAbs.clear();
      libsForJar.clear();
      if (!libraries.isEmpty()) {
         for (String s : libraries) {
            File f = new File(s);
            if (f.exists() && f.isAbsolute()) {
               libsAbs.add(s);
               libs.add(s);
               addAbsForJar(f);
            }
            else {
               String absInProj = projectDir + F_SEP + s;
               f = new File(absInProj);
               if (f.exists()) {
                  libs.add(s);
                  libsAbs.add(absInProj);
                  addRelForJar(f, s);
               }
               else {
                  notFound.append("\n").append(s);
               }
            }
         }
      }
      joined = joinedLibs(libs);
      joinedAbs = joinedLibs(libsAbs);
      errMsg = notFound.length() == 0 ? "" :
            "The following libraries cannot be found:" + notFound.toString();
   }

   /**
    * Returns the message that indicates invalid entries
    *
    * @return  the message; the empty string if no invalid entries
    * are present
    */
   public String errorMessage() {
      return errMsg;
   }

   /**
    * Returns a string in which the libraries are joined with the
    * system's path separator
    *
    * @return  the joined libraries; the empty string if none are
    * given
    */
   public String joined() {
      return joined;
   }

   /**
    * Returns a string in which the libraries are joined with the
    * system's path separator and relative paths converted to
    * absolute paths.
    *
    * @return  the joined libraries; the empty string if none are
    * given
    */
   public String joinedAbs() {
      return joinedAbs;
   }

   /**
    * Returns the list that contains the libraries formatted for the
    * classpath entry in a manifest file. Relative and absolute paths
    * remain unchanged
    *
    * @return  the list; the empty list if no libraries are given
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

   private void addRelForJar(File f, String path) {
      String s = path.replace(F_SEP, "/");
      if (f.isFile()) {
         libsForJar.add(s);
      }
      else {
         if (s.endsWith("/")) {
            libsForJar.add(s);
         }
         else {
            libsForJar.add(s + "/");
         }
      }
   }

   private void addAbsForJar(File f) {
      try {
         libsForJar.add(f.toURI().toURL().toString());
      }
      catch (MalformedURLException e) {
         FileUtils.log(e);
      }
   }
}
