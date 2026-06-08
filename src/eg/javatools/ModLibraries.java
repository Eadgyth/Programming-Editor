package eg.javatools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Stores configured directories containing modular libraries that
 * can be added to the module path
 */
public class ModLibraries {

   private static final String F_SEP = File.separator;

   private final List<String> mods = new ArrayList<>();
   private final List<String> modsAbs = new ArrayList<>();
   private final StringBuilder notFound = new StringBuilder();

   private String joined = "";
   private String joinedAbs = "";
   private String errMsg = "";

   /**
    * Configures the directories containing modular libraries.
    *
    * @param modLibs  the list of directories containing (putative)
    * modular libararies (relative to <code>projectDir</code> or absolute)
    * @param projectDir  the project directory
    */
   public void configure(List<String> modLibs, String projectDir) {
      notFound.setLength(0);
      mods.clear();
      modsAbs.clear();
      if (!modLibs.isEmpty()) {
         for (String s : modLibs) {
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
      joined = joined(mods); // abs or relative, as given
      joinedAbs = joined(modsAbs);
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
    * Returns a string in which the module directories are joined
    * with commas.
    *
    * @return  the joined directories (relative or absolute, as given);
    * the empty string of none are given
    */
   public String joined() {
      return joined;
   }
   
   
   /**
    * Returns a string in which the absolute module directories are
    * joined with commas.
    *
    * @return  the joined absolute directories; the empty string of
    * none are given
    */
   public String joinedAbs() {
      return joinedAbs;
   }

   //
   //--private--/
   //
   
   private String joined(List<String> l) {
      List<String> distinct = new ArrayList<>(new HashSet<>(l));
      return String.join(File.pathSeparator, distinct);
   }
}
