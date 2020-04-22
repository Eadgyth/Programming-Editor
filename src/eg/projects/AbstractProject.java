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

   /**
    * The object of <code>InputOptionsBuilder</code> which a project
    * uses to build the content of this <code>SettingsWindow</code>
    *
    * @see SettingsWindow.InputOptionsBuilder
    */
   protected final SettingsWindow.InputOptionsBuilder inputOptions;

   private static final String F_SEP = File.separator;
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
   private String projectDir = "";
   private String projectName = "";
   private String sourceFileName = "";
   private String relSourceFilePath = "";
   private String sourceDir = "";
   private String relSourceDir = "";
   private String namespace = "";
   private String namespaceDir = "";
   private String customCmd = "";
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
   private String absNamespace = "";
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
      String rootName = sw.projDirInput();
      String root = "";
      if (!rootName.isEmpty()) {
         root = rootByName(dirToTest, rootName);
      }
      boolean success = false;
      if (root.isEmpty()) {
         Dialogs.warnMessageOnTop(projDirInputWarning());
         if (!projectDir.isEmpty()) {
            sw.displayProjDir(projectName);
         }
      }
      else {
         success = !hasSetSourceFile || configForSourceFile(root);
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
      boolean success = false;
      String root = rootByContainedFile(dir, Prefs.PROJ_CONFIG_FILE);
      if (!root.isEmpty()) {
         sw.setSaveProjConfigSelected(true);
         conf = new Prefs(root);
         success = configFromPrefs(root, conf);
      }
      else {
         sw.setSaveProjConfigSelected(false);
         root = prefs.property("ProjectRoot");
         if (root != null && dir.startsWith(root)) {
             success = configFromPrefs(root, prefs);
         }
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
      return dir.startsWith(projectDir);
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
      store(prefs);
      if (sw.isSaveToProjConfig()) {
         conf = new Prefs(projectDir);
         store(conf);
      }
      else {
         deleteCurrentProjConfigFile();
      }
      if (!prevProjConfigDir.isEmpty()
            && !prevProjConfigDir.equals(projectDir)) {

         deleteProjConfigFile(prevProjConfigDir);
      }
      prevProjConfigDir = projectDir;
   }

   /**
    * @param projType  the project type
    * @param sourceExt  the extension of the (main) source file
    * to be set in this {@link SettingsWindow}. Null means no
    * configuation for a particular source file
    * @param namespaceSep  the separator for the namespace of the
    * set source file (see {@link #namespace()}). Null to not
    * take into account a namespace. Is ignored if sourceExt is
    * null
    */
   protected AbstractProject(ProjectTypes projType, String sourceExt,
         String namespaceSep) {

      this.projType = projType;
      hasSetSourceFile = sourceExt != null;
      if (hasSetSourceFile) {
         this.sourceExt = "." + sourceExt;
         this.namespaceSep = (namespaceSep != null) ? namespaceSep : null;
      }
      else {
         this.sourceExt = null;
         this.namespaceSep = null;
      }
      sw = new SettingsWindow();
      inputOptions = sw.inputOptionsBuilder();
      sw.setCancelAct(e -> undoSettings());
      sw.setDefaultCloseAct(defaultClosing);
   }

   /**
    * Sets command parameters
    */
   protected abstract void setCommandParameters();

   /**
    * Returns the name of the set source file without extension
    *
    * @return  the name, the empty string if the project doesn't
    * use a set source file
    */
   protected String sourceFileName() {
      return sourceFileName;
   }

   /**
    * Returns the pathname of the set source file relative
    * to the project directory
    *
    * @return  the relative pathname, the empty string if the
    * project doesn't use a set source file
    */
   protected String relativeSourceFile() {
      return relSourceFilePath;
   }

    /**
    * Returns if the set source file can be located based on
    * the last successful configuration and, if not, asks in
    * a dialog to open the project settings
    *
    * @return  true if the file can be located, false otherwise
    */
   protected boolean locateSourceFile() {
      boolean locate = sourceFile.exists();
      if (!locate) {
         ignoreNameConflict = false;
         if (0 == Dialogs.warnConfirmYesNo(
                  fileInputWarning() + "\n\nOpen the project settings?")) {

            openSettingsWindow(projectDir);
         }
      }
      return locate;
   }

   /**
    * Returns the namespace of the set source file with the
    * given namespace separator. Otherwise namespace is
    * defined as in {@link #namespaceDir()}
    *
    * @return  the namespace; the empty string if none is given
    * or if no namespace separator is set in the constructor
    */
   protected String namespace() {
      return namespace;
   }

   /**
    * Returns the namespace of the set source file with the
    * system dependent file separator. Namespace is the path
    * of the source file relative to the source root
    * (see {@link #sourceDir()}). It is derived either from the
    * entry of a qualified filename in the settings window
    * or from a file search if no qualified name is entered.
    *
    * @see  #namespace()
    * @return  the namespace; the empty string if none is given
    * or if no namespace separator is set in the constructor
    */
   protected String namespaceDir() {
      return namespaceDir;
   }

   /**
    * Returns the source directory. It equals the project directory
    * if no separate source directory inside the project is given
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
   public String relativeSourceDir() {
      return relSourceDir;
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
    * Returns the list of libraries
    *
    * @return  the list which is empty if no library is given
    */
   protected List<String> libraries() {
      return libraries;
   }

   /**
    * Returns the custom command
    *
    * @return  the custom command; the empty string if none is given
    */
   protected String customRunCmd() {
      return customCmd;
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
    * Returns the array of extensions of files that can be
    * included in a compilation/build in addition to source
    * files
    *
    * @return  the array; the empty array if no extension is
    * given
    */
   protected String[] fileExtensions() {
      if (extensions.isEmpty()) {
         return new String[0];
      }
      else {
         return extensions.split(",");
      }
   }

   /**
    * Returns the name for a build
    *
    * @return  the name; default is '[projectName]Project'
    */
   protected String buildName() {
      return buildName;
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

   private boolean configForSourceFile(String root) {
      setSourceDir(root);
      setSourceFileLocation();
      setRelSourceFile();
      sourceFile = new File(root + F_SEP + relSourceFilePath);
      if (sourceFile.exists()) {
         return true;
      }
      else {
         Dialogs.warnMessageOnTop(fileInputWarning());
         return false;
      }
   }

   private void setSourceDir(String root) {
      relSourceDir = sw.sourcesDirInput().replace("/", F_SEP);
      sourceDir = root;
      if (!relSourceDir.isEmpty()) {
         File dir = new File(root, relSourceDir);
         if (dir.exists()) {
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

   private void setSourceFileLocation() {
      isNameConflict = false;
      absNamespace = "";
      splitFilenameInput();
      if (namespaceSep != null && namespaceDir.isEmpty()) {
         findNamespace(sourceDir, sourceFileName + sourceExt);
         if (!absNamespace.isEmpty()
               && (absNamespace.length() > sourceDir.length())) {

            namespaceDir = absNamespace.substring(sourceDir.length() + 1);
            namespace = namespaceDir.replace(F_SEP, namespaceSep);
         }
      }
   }

   private void splitFilenameInput() {
      namespace = "";
      namespaceDir = "";
      String input = sw.filenameInput();
      String formatted = input.replace("\\", "/");
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

   private void findNamespace(String root, String name) {
      File[] files = new File(root).listFiles();
      if (files == null) {
    	 return;
      }
      List<File> dirs = new ArrayList<>(10);
      for (File f : files) {
         if (f.isFile()) {
            if (f.getName().equals(name)) {
               if (absNamespace.isEmpty()) {
                  absNamespace = f.getParent();
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
         dirs.forEach((File f) -> findNamespace(f.getPath(), name));
      }
   }

   private boolean isNameConflictWarning() {
      if (isNameConflict && !ignoreNameConflict) {
         Dialogs.warnMessageOnTop(nameConflictMsg());
         ignoreNameConflict = true;
         return true;
      }
      return false;
   }

   private void fetchOptionalSettings() {
      if (hasSetSourceFile) {
         sw.assignLibrariesInput(libraries);
         cmdOptions = sw.cmdOptionsInput();
         cmdArgs = sw.cmdArgsInput();
         execDir = sw.execDirInput().replace("/", F_SEP);
         compileOptions = sw.compileOptionsInput();
         extensions = sw.fileExtensionsInput();
         buildName = sw.buildNameInput();
         if (buildName.isEmpty()) {
            buildName = projectName + "Project";
            sw.displayBuildName(buildName);
         }
      }
      else {
         customCmd = sw.customCmdInput();
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
         if (namespaceSep != null) {
            namespaceDir = pr.property("Namespace");
            namespace = namespaceDir.replace(F_SEP, namespaceSep);
         }
         relSourceDir = pr.property("SourceDir");
         sourceDir = relSourceDir.isEmpty() ? root : root + F_SEP + relSourceDir;
         setRelSourceFile();
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
         execDir = pr.property("ExecDir");
         extensions = pr.property("IncludedFiles");
         buildName = pr.property("BuildName");
      }
      else {
         customCmd = pr.property("Command");
      }
   }

   private void setRelSourceFile() {
      StringBuilder sb = new StringBuilder();
      if (!relSourceDir.isEmpty()) {
         sb.append(relSourceDir).append(F_SEP);
      }
      if (!namespaceDir.isEmpty()) {
         sb.append(namespaceDir).append(F_SEP);
      }
      sb.append(FileUtils.addExtension(sourceFileName, sourceExt));
      relSourceFilePath = sb.toString();
   }

   private void displaySettings() {
      sw.displayProjDir(projectName);
      sw.displaySourcesDir(relSourceDir);
      if (!namespace.isEmpty()) {
         sw.displayFilename(namespace + namespaceSep + sourceFileName);
      }
      else {
         sw.displayFilename(sourceFileName);
      }
      sw.displayLibraries(libraries);
      sw.displayCmdOptions(cmdOptions);
      sw.displayCmdArgs(cmdArgs);
      sw.displayExecDir(execDir);
      sw.displayCustomCmd(customCmd);
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
      pr.setProperty("ExecDir", execDir);
      StringBuilder libs = new StringBuilder();
      libraries.forEach(s -> libs.append(s.replace("/", F_SEP))
    		  .append(File.pathSeparator));

      pr.setProperty("Libraries", libs.toString());
      pr.setProperty("IncludedFiles", extensions);
      pr.setProperty("BuildName", buildName);
      pr.setProperty("ProjectType", projType.toString());
      pr.setProperty("Command", customCmd);
      pr.store();
   }

   private void deleteCurrentProjConfigFile() {
      File configFile = new File(projectDir + F_SEP + Prefs.PROJ_CONFIG_FILE);
      if (configFile.exists()) {
         int res = Dialogs.warnConfirmYesNo(
               "Saving the \'ProjConfig\' file is no more selected.\n"
               + "Remove the file?");

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
      displaySettings();
      sw.setVisible(false);
   }

   private final WindowAdapter defaultClosing = new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent we) {
         undoSettings();
      }
   };

   private String projDirInputWarning() {
      StringBuilder sb = new StringBuilder();
      if (sw.projDirInput().isEmpty()) {
         sb.append("A project directory is not specified.");
      }
      else {
         sb.append(sw.projDirInput())
            .append("\nThe directory cannot be found.");
      }
      if (!projectDir.isEmpty()) {
         sb.append("\n\nThe previous directory '")
            .append(projectName())
            .append("' will be kept.");
      }
      return sb.toString();
   }

   private String fileInputWarning() {
      StringBuilder sb = new StringBuilder();
      if (sourceFileName.isEmpty()) {
         sb.append("The name of a ")
            .append(projType.display())
            .append(" file is not specified");
      }
      else {
         sb.append(sw.filenameInput())
            .append("\nThe ")
            .append(projType.display())
            .append(" file cannot be found");

         if (!relSourceDir.isEmpty()) {
            sb.append(" in the source directory ")
               .append(relSourceDir);
         }
      }
      sb.append(".");
      return sb.toString();
   }

   private String nameConflictMsg() {
      String name = namespace.isEmpty() ?
            "" : namespace + namespaceSep + sourceFileName + sourceExt;

      StringBuilder msg = new StringBuilder();
      msg.append(sourceFileName).append(sourceExt)
            .append("\nMore than one file with this name seems to exist.");

      if (!name.isEmpty()) {
         msg.append("\nThe currently set file is:\n\n").append(name);
      }
      msg.append("\n\nA file can be entered as qualified name or pathname")
            .append(" relative to the source root.");

      return msg.toString();
   }
}
