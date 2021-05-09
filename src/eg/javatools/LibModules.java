package eg.javatools;

import java.io.File;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Stores configured libraries that can be added the module path
 */
public class LibModules {

   private static final String F_SEP = File.separator;

   private final List<String> mods = new ArrayList<>();
   private final List<String> modsAbs = new ArrayList<>();
   private final StringBuilder notFound = new StringBuilder();

   private String joinedParents = "";
   private String joinedAbsParents = "";
   private String joinedNames = "";
   private String errMsg = "";

   /**
    * Configures the library modules
    *
    * @param modules  the list of paths of (putative) library modules
    * which may be relative to the project directory or absolute
    * @param projectDir  the project directory
    */
   public void configure(List<String> modules, String projectDir) {
      notFound.setLength(0);
      mods.clear();
      modsAbs.clear();
      if (!modules.isEmpty()) {
         for (String s : modules) {
            File f = new File(s);
            if (f.exists() && f.isAbsolute()) {
               modsAbs.add(s);
               mods.add(s);
            }
            else {
               String absInProj = projectDir + F_SEP + s;
               f = new File(absInProj);
               if (f.exists()) {
                  mods.add(s);
                  modsAbs.add(absInProj);
               }
               else {
                  notFound.append("\n").append(s);
               }
            }
         }
      }
      joinedParents = joinedParents(mods, projectDir);
      joinedAbsParents = joinedParents(modsAbs, projectDir);
      joinedNames = joinedNames(modsAbs);
      errMsg = notFound.length() == 0 ? "" :
            "The following modules cannot be found:" + notFound.toString();
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
    * Returns a string in which the parent directories of modules are
    * joined with the system's path separator and duplicates removed.
    *
    * @return  the joined parent directories; the empty string if none
    * are given
    */
   public String joinedParents() {
      return joinedParents;
   }

   /**
    * Returns a string in which the parent directories of modules
    * are joined with the system's path separator, duplicates removed
    * and relative paths converted to absolute paths
    *
    * @return  the joined parent directories; the empty string none
    * are given
    */
   public String joinedParentsAbs() {
      return joinedAbsParents;
   }

   /**
    * Returns a string in which the last names of modules are joined
    * with commas.
    * <p>In the case the module is a jar file the extension and, if
    * present, the version number are removed. Hyphens are replaced
    * with dots.
    * <p>The removal of a version number takes place on the assumption
    * that it's separated by a hyphen and contains only digits and
    * dots.
    *
    * @return  the joined names; the empty string of none are given
    */
   public String joinedNames() {
      return joinedNames;
   }

   //
   //--private--/
   //

   private String joinedParents(List<String> l, String projDir) {
      List<String> parents = new ArrayList<>();
      if (l.isEmpty()) {
         return "";
      }
      else {
         HashSet<String> checkDupl = new HashSet<>();
         for (String s : l) {
            String parent = new File(s).getParent();
            if (!parent.equals(projDir)) {
               if (!checkDupl.contains(parent)) {
                  checkDupl.add(parent);
               }
               else {
                  continue;
               }
               parents.add(parent);
            }
         }
      }
      return String.join(File.pathSeparator, parents);
   }

   private String joinedNames(List<String> l) {
      List<String> names = new ArrayList<>();
      if (l.isEmpty()) {
         return "";
      }
      else {
         for (String s : l) {
            File f = new File(s);
            String name = f.getName();
            if (name.endsWith(".jar")) {
               names.add(moduleNameOfJar(name).replace("-", "."));
            }
            else {
               names.add(name);
            }
         }
      }
      return String.join(",", names);
   }

   private String moduleNameOfJar(String name) {
      int extPos = name.lastIndexOf(".");
      int lastHyphen = name.lastIndexOf("-");
      boolean hasVersion = false;
      if (lastHyphen != -1) {
         char[] c = name.substring(lastHyphen, extPos).toCharArray();
         int i;
         if (c.length > 1 && Character.isDigit(c[1])) {
            for (i = 2; i < c.length && (c[i] == '.' || Character.isDigit(c[i])); i++);
            hasVersion = i == c.length;
         }
      }
      return hasVersion ? name.substring(0, lastHyphen) : name.substring(0, extPos);
   }
}
