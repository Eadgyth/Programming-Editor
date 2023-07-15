package eg.projects;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

//--Eadgyth--/
import eg.Prefs;
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.ui.projectsetting.SettingsWindow;

/**
 * The configuration of a project
 */
public abstract class AbstractProject implements Configurable {

   private static final String F_SEP = File.separator;

   /**
    * The object of <code>InputOptionsBuilder</code> which a project
    * uses to build the content of this <code>SettingsWindow</code>
    *
    * @see SettingsWindow.InputOptionsBuilder
    */
   protected final SettingsWindow.InputOptionsBuilder inputOptions;

   //
   // The Prefs object that reads from and writes to the Prefs file in
   // in the '.eadgyth' directory in the user home directory
   private final Prefs prefs = new Prefs();
   //
   // Set in constructor
   private final ProjectTypes projType;
   private final SettingsWindow sw;
   private final String sourceExt;
   private final boolean hasSetSourceFile;
   private final String namespaceSep;
   //
   // Variables that depend on user settings
   private final List<String> libraries = new ArrayList<>();
   private final List<String> libModules = new ArrayList<>();
   private String projectDir = "";
   private String projectName = "";
   private String sourceFileName = "";
   private String relSourceFilePath = "";
   private String module = "";
   private String sourceDir = "";
   private String relSourceDir = "";
   private String namespace = "";
   private String namespaceDir = "";
   private String customRunCmd = "";
   private String customCompileCmd = "";
   private String customBuildCmd = "";
   private String cmdOptions = "";
   private String cmdArgs = "";
   private String execDir = "";
   private String compileOptions = "";
   private String extensions = "";
   private String buildName = "";
   //
   // Variables to control the configuration
   private String dirToTest = null;
   private File sourceFile = null;
   private String absSubSourceDir = "";
   private boolean isInNamespaceDir = false;
   private boolean isNameConflict = false;
   private boolean ignoreNameConflict = false;
   private String prevProjConfigDir = "";
   //
   // The Prefs created to read from and write to a ProjConfig
   // file in a project directory
   private Prefs conf;

   @Override
   public final void setConfiguringAction(Runnable r) {
      sw.okAct(r);
   }

   @Override
   public final void openSettingsWindow(String dir) {
      if (dir == null || dir.isEmpty()) {
         throw new IllegalArgumentException("dir cannot be null or empty");
      }
      dirToTest = dir;
      String chooserDir = projectDir.isEmpty() ? dir : projectDir;
      sw.setDirectory(chooserDir);
      sw.setVisible(true);
   }

   @Override
   public final boolean configure() {
      boolean success = false;
      String root = "";
      if (!sw.projDirInput().isEmpty()) {
         root = rootByName(dirToTest, sw.projDirInput());
      }
      if (root.isEmpty()) {
         Dialogs.warnMessageOnTop(projDirInputWarning());
         if (!projectDir.isEmpty()) {
            sw.displayProjDir(projectName);
         }
      }
      else {
         success = !hasSetSourceFile || configForSourceFile(root, false);
      }
      if (success) {
         projectDir = root;
         projectName = new File(root).getName();
         fetchOptionalSettings();
         if (!isNameConflictWarning()) {
            setCommandParameters();
            sw.setVisible(false);
         }
      }
      return success;
   }

   /**
    * {@inheritDoc}.
    * <p>
    * It is first checked if a 'ProjConfig' file is found in the
    * specified directory or further upward the directory path.
    * If this is not the case it is checked if the directory is
    * identical to or contained in the project directory saved
    * lastly in the 'Prefs' file for the editor settings.
    */
   @Override
   public final boolean retrieve(String dir) {
      boolean success;
      String root = rootByContainedFile(dir, Prefs.PROJ_CONFIG_FILE);
      if (!root.isEmpty()) {
         sw.setSaveProjConfigSelected(true);
         conf = new Prefs(root);
         success = configFromPrefs(root, conf);
      }
      else {
         sw.setSaveProjConfigSelected(false);
         root = prefs.property("ProjectRoot");
         success = !root.isEmpty() && rootByName(dir, root).equals(root)
                && configFromPrefs(root, prefs);
      }
      if (success) {
         ignoreNameConflict = true;
         setCommandParameters();
      }
      return success;
   }

   @Override
   public final ProjectTypes projectType() {
      return projType;
   }

   @Override
   public final boolean hasSetSourceFile() {
      return hasSetSourceFile;
   }

   @Override
   public final boolean isInProject(String dir) {
      return rootByName(dir, projectDir).equals(projectDir);
   }

   @Override
   public final String projectDir() {
      return projectDir;
   }

   @Override
   public final String projectName() {
      return projectName;
   }

   @Override
   public final String executableDir() {
      return execDir;
   }

   @Override
   public final void storeConfiguration() {
      if (projectDir.isEmpty()) {
         throw new IllegalStateException("The project is not configured");
      }
      if (hasSetSourceFile() && !sourceFile.exists()) {
         return;
      }
      store(prefs);
      if (sw.isSaveToProjConfig()) {
         conf = new Prefs(projectDir);
         store(conf);
      }
      else {
         deleteCurrentProjConfigFile();
      }
      if (!prevProjConfigDir.isEmpty() && !prevProjConfigDir.equals(projectDir)) {
         deleteProjConfigFile(prevProjConfigDir);
      }
      prevProjConfigDir = projectDir;
   }

   /**
    * @param projType  the project type
    * @param sourceExt  the extension of the source file (a script
    * file or file with a main entry) without the dot. Null means
    * that the project does not use a set source file.
    * @param namespaceSep  the separator for a namespace. Null to
    * not take into account any namespace. Is also ignored if
    * sourceExt is null.
    *
    * @see #namespace()
    * @see #namespaceDir()
    */
   protected AbstractProject(ProjectTypes projType, String sourceExt,
         String namespaceSep) {

      this.projType = projType;
      this.sourceExt = sourceExt != null ? "." + sourceExt : null;
      this.namespaceSep = sourceExt != null ? namespaceSep : null;
      hasSetSourceFile = sourceExt != null;
      sw = new SettingsWindow(projType.display());
      inputOptions = sw.inputOptionsBuilder();
      sw.setCancelAct(e -> undoSettings());
      sw.setDefaultCloseAct(defaultClosing);
   }

   /**
    * Sets command parameters if the project is successfully
    * configured after the entries in the settings window are
    * confirmed or if a configuration is retrieved from the Prefs
    * file or a ProjConfig file
    */
   protected abstract void setCommandParameters();

   /**
    * Returns the name of the set source file without extension.
    * <p>
    * The returned name is as entered in the settings window which
    * therefore could also be a relative pathname. However, if a
    * namespace separator is passed to the constructor the
    * returned name is only the last name even if a qualified name
    * has been entered. The namespace can be obtained by
    * {@link #namespace()} or {@link #namespaceDir()} where needed.
    *
    * @return  the name, the empty string if the project doesn't
    * use a set source file
    */
   protected String sourceFileName() {
      return sourceFileName;
   }

   /**
    * Returns the extension of the set source file with the
    * beginning period
    *
    * @return  the extension
    */
   protected String sourceExtension() {
      return sourceExt;
   }

   /**
    * Returns the pathname of the set source file relative to
    * the project directory (with file extension). The pathname
    * may include a directory namespace relative to the source
    * directory if a namespace separator is specified in the
    * constructor and the file is found in a subdirectory (path)
    * inside the source root.
    *
    * @see #namespaceDir()
    * @return  the relative pathname, the empty string if the
    * project doesn't use a set source file
    */
   protected String relativeSourceFile() {
      return relSourceFilePath;
   }

   /**
    * Returns if the set source file can be located based on the last
    * successful configuration or a reconfiguration and, if not, asks
    * in a dialog to open the project settings
    *
    * @return  true if the file can be located; false otherwise
    */
   protected boolean locateSourceFile() {
      boolean locate = sourceFile.exists();
      if (!locate) {
         ignoreNameConflict = false;
         locate = configForSourceFile(projectDir, true);
         if (locate) {
            store(prefs);
            if (sw.isSaveToProjConfig()) {
               conf = new Prefs(projectDir);
               store(conf);
            }
         }
      }
      return locate;
   }

   /**
    * Returns the namespace with the given namespace separator.
    * <p>
    * The namespace may be derived from a qualified name that is
    * entered in the project settings and is otherwise defined as
    * in {@link #namespaceDir()}.
    *
    * @return  the namespace; the empty string if none is given
    * or if no namespace separator is set in the constructor
    */
   protected String namespace() {
      return namespace;
   }

   /**
    * Returns the directory namespace with the system dependent
    * file separator.
    * <p>
    * The directory namespace can be a directory (path) that contains
    * the specified source file inside the {@link #sourceDir()}. It
    * is derived from a file search or the entry of a qualified
    * filename in the settings window.
    * <p>
    * If the namespace separator passed to the constructor is the
    * file separator, any qualified filename must match the file
    * location. Otherwise, the source file may exist directly in the
    * source directory although a qualified name is entered.
    *
    * @return  the directory namespace; the empty string if none is
    * given or if no namespace separator is set in the constructor
    */
   protected String namespaceDir() {
      return namespaceDir;
   }

   /**
    * Returns the source directory which equals the project directory
    * if no separate source directory inside the project is given.
    *
    * @return  the directory
    */
   protected String sourceDir() {
      return sourceDir;
   }

   /**
    * Returns the source directory relative to the project
    * directory
    *
    * @return  the directory; the empty string if none is given
    */
   protected String relativeSourceDir() {
      return relSourceDir;
   }

   /**
    * Returns the name for a module
    *
    * @return  the name; the empty string of none is given
    */
   protected String module() {
      return module;
   }

   /**
    * Returns the list of libraries
    *
    * @return  the list; the empty list if no library is given
    */
   protected List<String> libraries() {
      return libraries;
   }

   /**
    * Returns the list of library modules
    *
    * @return  the list; the empty list if no module is given
    */
   protected List<String> libModules() {
      return libModules;
   }

   /**
    * Returns command options
    *
    * @return  the options; the empty string if none given
    */
   protected String cmdOptions() {
      return cmdOptions;
   }

   /**
    * Returns command arguments
    *
    * @return  the arguments; the empty string if none is given
    */
   protected String cmdArgs() {
      return cmdArgs;
   }

   /**
    * Returns compiler options
    *
    * @return  the options; the empty string if none is given
    */
   protected String compileOptions() {
      return compileOptions;
   }

   /**
    * Returns the array of file extensions that are included in a
    * file search
    *
    * @return  the array; the empty array if no extension is
    * given
    */
   protected String[] fileExtensions() {
      return extensions.isEmpty() ? new String[0] : extensions.split(",");
   }

   /**
    * Returns the name for a build.
    * @deprecated  call {@link buildName(boolean)} instead
    *
    * @return  the name which is the default name
    * ([projectName]Project) if no build name is entered in the
    * settings
    */
   @Deprecated
   protected String buildName() {
      if (buildName.isEmpty()) {
         return projectName + "Project";
      }
      return buildName;
   }

   /**
    * Returns the name for a build
    *
    * @param useDef  true to return the default name
    * ('[projectName]') if no build name is entered in the
    * settings, false to return the empty string for this case
    * @return  the name; the empty string or the default name
    * as indicated
    */
   protected String buildName(boolean useDef) {
      if (buildName.isEmpty() && useDef) {
         return projectName;
      }
      return buildName;
   }

   /**
    * Returns the custom run command
    *
    * @return  the command; the empty string if none is given
    */
   protected String customRunCmd() {
      return customRunCmd;
   }

   /**
    * Returns the custom compile command
    *
    * @return  the command; the empty string if none is given
    */
   protected String customCompileCmd() {
      return customCompileCmd;
   }

   /**
    * Returns the custom build command
    *
    * @return  the command; the empty string if none is given
    */
   protected String customBuildCmd() {
      return customBuildCmd;
   }

   //
   //--private--/
   //

   private String rootByName(String dir, String rootName) {
      File f = new File(rootName);
      if (f.isAbsolute() && f.exists() && f.isDirectory()) {
         rootName = f.getName();
      }
      File root = new File(dir);
      while (root != null) {
         if (rootName.equals(root.getName()) && root.exists()) {
            return root.getPath();
         }
         root = root.getParentFile();
      }
      return "";
   }

   private String rootByContainedFile(String dir, String file) {
      File root = new File(dir);
      while (root != null) {
         if (new File(root, file).exists()) {
            return root.getPath();
         }
         root = root.getParentFile();
      }
      return "";
   }

   private boolean configForSourceFile(String root, boolean reconfig) {
      setSourceDir(root);
      setSourceFile();
      setRelSourceFilePath();
      sourceFile = new File(root, relSourceFilePath);
      if (!sourceFile.exists()) {
         if (reconfig) {
            if (0 == Dialogs.warnConfirmYesNo(fileInputWarning(true))) {
               openSettingsWindow(projectDir);
            }
         }
         else {
            Dialogs.warnMessageOnTop(fileInputWarning(false));
         }
         return false;
      }
      return true;
   }

   private void setSourceDir(String root) {
      relSourceDir = sw.sourcesDirInput().replace("/", F_SEP);
      sourceDir = root;
      if (!relSourceDir.isEmpty()) {
         File dir = new File(root, relSourceDir);
         if (dir.exists() && dir.isDirectory()) {
            sourceDir = dir.getPath();
         }
         else {
            findSourceDir(root, relSourceDir);
            if (sourceDir.length() > root.length()) {
               relSourceDir = sourceDir.substring(root.length() + 1);
            }
         }
      }
   }

   private void findSourceDir(String root, String sourceDirInput) {
      File fRoot = new File(root);
      File[] files = fRoot.listFiles();
      if (files != null) {
         for (File f : files) {
            if (f.isDirectory()) {
               if (f.getName().equals(sourceDirInput)) {
                  sourceDir = f.getPath();
                  break;
               }
               else {
                  findSourceDir(f.getPath(), sourceDirInput);
               }
            }
         }
      }
   }

   private void setSourceFile() {
      isNameConflict = false;
      parseFilenameInput();
      absSubSourceDir = "";
      findSubSourceDir(sourceDir, sourceFileName + sourceExt);
      if (namespaceSep != null) {
         boolean forceGivenNamespace = !namespaceDir.isEmpty()
               && namespaceSep.equals(F_SEP);

         boolean isSubSourceDir = !absSubSourceDir.isEmpty()
               && absSubSourceDir.length() > sourceDir.length();

         isInNamespaceDir = isInGivenNamespaceDir() || forceGivenNamespace
               || isSubSourceDir;

         if (isInNamespaceDir && namespaceDir.isEmpty()) {
            namespaceDir = absSubSourceDir.substring(sourceDir.length() + 1);
            namespace = namespaceDir.replace(F_SEP, namespaceSep);
         }
      }
   }

   private void parseFilenameInput() {
      namespace = "";
      namespaceDir = "";
      String formatted = sw.filenameInput().replace("\\", "/");
      if (formatted.endsWith(sourceExt)) {
         int extPos = formatted.length() - sourceExt.length();
         formatted = formatted.substring(0, extPos);
      }
      if (namespaceSep != null) {
         formatted = formatted.replace(namespaceSep, "/");
         int lastSepPos = formatted.lastIndexOf('/', formatted.length() - 1);
         if (lastSepPos != -1) {
            namespaceDir = formatted.replace("/", F_SEP).substring(0, lastSepPos);
            namespace = namespaceDir.replace(F_SEP, namespaceSep);
            sourceFileName = formatted.substring(lastSepPos + 1);
         }
         else {
            sourceFileName = formatted;
         }
      }
      else {
         sourceFileName = formatted.replace("/", F_SEP);
      }
   }

   private void findSubSourceDir(String root, String name) {
      File[] files = new File(root).listFiles();
      if (files == null) {
    	   return;
      }
      List<File> dirs = new ArrayList<>(10);
      for (File f : files) {
         if (f.isFile()) {
            if (f.getName().equals(name)) {
               if (absSubSourceDir.isEmpty()) {
                  absSubSourceDir = f.getParent();
               }
               else {
                  isNameConflict = true;
               }
            }
         }
         else {
            dirs.add(f);
         }
      }
      if (!dirs.isEmpty()) {
         dirs.forEach((File f) -> findSubSourceDir(f.getPath(), name));
      }
   }

   private boolean isNameConflictWarning() {
      if (isNameConflict && !ignoreNameConflict) {
         Dialogs.infoMessage(nameConflictMsg(), "Multiple files");
         ignoreNameConflict = true;
         return true;
      }
      return false;
   }

   private void fetchOptionalSettings() {
      if (hasSetSourceFile) {
         sw.assignLibrariesInput(libraries);
         sw.assignLibModulesInput(libModules);
         module = sw.moduleInput();
         cmdOptions = sw.cmdOptionsInput();
         cmdArgs = sw.cmdArgsInput();
         execDir = sw.execDirInput().replace("/", F_SEP);
         compileOptions = sw.compileOptionsInput();
         extensions = sw.fileExtensionsInput();
         buildName = sw.buildNameInput();
      }
      else {
         customRunCmd = sw.customRunCmdInput();
         customCompileCmd = sw.customCompileCmdInput();
         customBuildCmd = sw.customBuildCmdInput();
      }
   }

   private boolean configFromPrefs(String root, Prefs pr) {
      String projTypeToTest = pr.property("ProjectType");
      if (!projTypeToTest.equals(projType.toString())) {
         return false;
      }
      boolean success;
      if (!hasSetSourceFile) {
         File f = new File(root);
         success = f.exists() && f.isDirectory();
      }
      else {
         sourceFileName = pr.property("MainProjectFile");
         relSourceDir = pr.property("SourceDir");
         sourceDir = relSourceDir.isEmpty() ? root : root + F_SEP + relSourceDir;
         if (namespaceSep != null) {
            namespaceDir = pr.property("Namespace");
            namespace = namespaceDir.replace(F_SEP, namespaceSep);
         }
         isInNamespaceDir = isInGivenNamespaceDir();
         setRelSourceFilePath();
         sourceFile = new File(root + F_SEP + relSourceFilePath);
         success = sourceFile.exists() && sourceFile.isFile();
      }
      if (success) {
         projectDir = root;
         projectName = new File(projectDir).getName();
         fetchOptionalPrefs(pr);
         displaySettings();
      }
      return success;
   }

   private void fetchOptionalPrefs(Prefs pr) {
      if (hasSetSourceFile) {
         String libInput = pr.property("Libraries");
         if (!libInput.isEmpty()) {
            String[] libInputArr = libInput.split(File.pathSeparator);
            libraries.addAll(Arrays.asList(libInputArr));
         }
         String modInput = pr.property("LibModules");
         if (!modInput.isEmpty()) {
            String[] modInputArr = modInput.split(File.pathSeparator);
            libModules.addAll(Arrays.asList(modInputArr));
         }
         module = pr.property("Module");
         execDir = pr.property("ExecDir");
         cmdOptions = pr.property("CmdOptions");
         cmdArgs = pr.property("CmdArgs");
         compileOptions = pr.property("CompileOptions");
         extensions = pr.property("IncludedFiles");
         buildName = pr.property("BuildName");
      }
      else {
         customRunCmd = pr.property("RunCommand");
         customCompileCmd = pr.property("CompileCommand");
         customBuildCmd = pr.property("BuildCommand");
      }
   }

   private boolean isInGivenNamespaceDir() {
      if (!namespaceDir.isEmpty()) {
         String pathname = namespaceDir + F_SEP + sourceFileName + sourceExt;
         return new File(sourceDir, pathname).exists();
      }
      return false;
   }

   private void setRelSourceFilePath() {
      StringBuilder sb = new StringBuilder();
      if (!relSourceDir.isEmpty()) {
         sb.append(relSourceDir).append(F_SEP);
      }
      if (isInNamespaceDir) {
         sb.append(namespaceDir).append(F_SEP);
      }
      sb.append(FileUtils.addExtension(sourceFileName, sourceExt));
      relSourceFilePath = sb.toString();
   }

   private void displaySettings() {
      sw.displayProjDir(projectName);
      sw.displayModule(module);
      sw.displaySourcesDir(relSourceDir);
      if (!namespace.isEmpty()) {
         sw.displayFilename(namespace + namespaceSep + sourceFileName);
      }
      else {
         sw.displayFilename(sourceFileName);
      }
      sw.displayLibraries(libraries);
      sw.displayLibModules(libModules);
      sw.displayCmdOptions(cmdOptions);
      sw.displayCmdArgs(cmdArgs);
      sw.displayExecDir(execDir);
      sw.displayCustomRunCmd(customRunCmd);
      sw.displayCustomCompileCmd(customCompileCmd);
      sw.displayCustomBuildCmd(customBuildCmd);
      sw.displayFileExtensions(extensions);
      sw.displayBuildName(buildName);
      sw.displayCompileOptions(compileOptions);
   }

   private void store(Prefs pr) {
      if (pr == prefs) {
         pr.setProperty("ProjectRoot", projectDir);
      }
      pr.setProperty("MainProjectFile", sourceFileName);
      pr.setProperty("Namespace", namespaceDir);
      pr.setProperty("SourceDir", relSourceDir);
      pr.setProperty("Module", module);
      pr.setProperty("ExecDir", execDir);
      StringBuilder libs = new StringBuilder();
      libraries.forEach(s -> libs.append(s).append(File.pathSeparator));
      pr.setProperty("Libraries", libs.toString());
      StringBuilder mods = new StringBuilder();
      libModules.forEach(s -> mods.append(s).append(File.pathSeparator));
      pr.setProperty("LibModules", mods.toString());
      pr.setProperty("CmdOptions", cmdOptions);
      pr.setProperty("CmdArgs", cmdArgs);
      pr.setProperty("CompileOptions", compileOptions);
      pr.setProperty("IncludedFiles", extensions);
      pr.setProperty("BuildName", buildName);
      pr.setProperty("ProjectType", projType.toString());
      pr.setProperty("RunCommand", customRunCmd);
      pr.setProperty("CompileCommand", customCompileCmd);
      pr.setProperty("BuildCommand", customBuildCmd);
      pr.store();
   }

   private void deleteCurrentProjConfigFile() {
      File configFile = new File(projectDir + F_SEP + Prefs.PROJ_CONFIG_FILE);
      if (configFile.exists()) {
         int res = Dialogs.warnConfirmYesNo(
               "Saving the \'ProjConfig\' file is no more selected.\n"
               + "Remove the file?\n\n");

         if (res == 0) {
            deleteProjConfigFile(projectDir);
         }
         else {
            sw.setSaveProjConfigSelected(true);
            if (conf == null) {
               conf = new Prefs(projectDir);
            }
            store(conf);
         }
      }
   }

   private void deleteProjConfigFile(String dir) {
      File configFile = new File(dir + F_SEP + Prefs.PROJ_CONFIG_FILE);
      if (configFile.exists()) {
         try {
            Files.delete(configFile.toPath());
         }
         catch (IOException e) {
            FileUtils.log(e);
         }
      }
   }

   private void undoSettings() {
      sw.setVisible(false);
      displaySettings();
   }

   private final WindowAdapter defaultClosing = new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent we) {
         displaySettings();
      }
   };

   private String projDirInputWarning() {
      StringBuilder sb = new StringBuilder();
      if (sw.projDirInput().isEmpty()) {
         sb.append("A project directory is not specified.");
      }
      else {
         sb.append(sw.projDirInput())
            .append("\nThe project directory cannot be found.");
      }
      if (!projectDir.isEmpty()) {
         sb.append(" The previous project directory name will be kept.");
      }
      return sb.toString();
   }

   private String fileInputWarning(boolean confirm) {
      StringBuilder sb = new StringBuilder();
      if (sourceFileName.isEmpty()) {
         sb.append("A source file is not specified.");
      }
      else {
         if (isInNamespaceDir) {
            sb.append(namespaceDir).append(F_SEP);
         }
         sb.append(sourceFileName).append(sourceExt)
            .append("\nThe file cannot be found in the specified source root ..")
            .append(F_SEP).append(sw.projDirInput());

         if (!relSourceDir.isEmpty()) {
            sb.append(F_SEP).append(relSourceDir);
         }
         sb.append(".");
      }
      if (confirm) {
         sb.append("\n\nOpen the project settings?");
      }
      return sb.toString();
   }

   private String nameConflictMsg() {
      StringBuilder sb = new StringBuilder();
      sb.append(sourceFileName).append(sourceExt)
         .append("\nMore than one file with this name seems to exist")
         .append(" in the project. The currently set file is:\n..")
         .append(F_SEP).append(sw.projDirInput())
         .append(F_SEP).append(relSourceFilePath);

      if (namespaceSep != null) {
         sb.append("\n\nNOTE: a qualified name or relative pathname may be ")
            .append("entered to select a file.");
      }
      return sb.toString();
   }
}
