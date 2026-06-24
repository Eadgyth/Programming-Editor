<h2>Help</h2>
<a id="top"></a>
<h3>Content</h3>
<nav>
<ul>
   <li><a href="#Requirements">Requirements</a></li>
   <li><a href="#Projects">Setup to run source code</a></li>
   <li><a href="#Categories">Project categories</a></li>
   <li><a href="#Console">Console</a></li>
</ul>
</nav>
<hr>
<h2 id="Requirements">Requirements</h2>
<p>
The editor requires Java 8 or higher. To compile Java code by the built-in compile function, Eadgyth must be run with the runtime environment (JRE) contained in a Java Development Kit (JDK).
<p>
It is further important that the built-in commands to run C#, Python, and R assume that the paths to the executables are set as PATH system (environment) variables. For the C# project category, the editor assumes a .NET SDK installation.
<p>
Running code (and comiling) using the built-in function prefers or requires UTF-8 encoded files for reliable output (as of vs1.2.1). The editor can open files with legacy encodings using a non-UTF-8 system encoding as fallback if the system encoding is not UTF-8. A fallback may also be selected in the File menu before opening a file with a legacy encoding. A file can be converted to UTF-8 and also be reverted to the original encoding as long as it remains open in File menu.
<hr>
<h2 id="Projects">Setup to run source code</h2>
<h4>Setting a project</h4>
<ol>
<li>Open a file from or save a new file to the directory that is the intended root directory of the project. The project directory is the working directory where commands (built-in or custom) will be executed.
</li>
<li>Open the project settings by selecting 'Settings for...' in the 'Project' menu and choose the <a href="#Categories">category</a> to open the project settings.
</li>
<li>The input options depend on the project category. Initially, the required entry is the name of the project directory where the project will be created.
</li>
</ol>
<h4>Retrieving projects</h4>
<ul>
<li>The project that was active when the Editor has been closed the last time is re-loaded and set active if a file of the project is opened (or a new file is saved to the project directory).
</li>
<li>To retrieve a previous project independently of the project that had been active lastly "Save ProjConfig file in the project to retrieve settings" can be selected in the project settings. This stores the parameters for the project in the project directory and marks it as an "Eadgyth project". Unselecting the option deletes the ProjConfig file.
</li>
</ul>
<h4>Switching between projects</h4>
<ul>
<li>Multiple projects can be created within a session. Selecting a file that belongs to a project other than the currently active one will disable running (or compiling, where applicable) that project. The editor may prompt you to switch projects (e.g., when opening a file belonging to a project defined in a 'ProjConfig.properties' file), or you can use 'Change Project' to set it active.
</li>
</ul>
<br>
<p><a href="#top">Back to top</a></p>
<hr>
<h2 id="Categories">Project categories</h2>
<nav>
<table>
   <tr>
      <th>Category</th>
      <th>Supported actions</th>
   </tr>
   <tr>
      <td><a href="#JavaProject">Java</a></td>
      <td>Compile and run Java code.<br>
         Distinguishes between a classical, single-, or multi-module mode program; Specify libraries (selectable for classpath or module path); create an executable jar file (for non-modular programs)</td>
   </tr>
   <tr>
      <td><a href="#JavaSourceFileModeProject">Java source-file mode</a></td>
      <td>Run Java code without the compilation step in source-file mode (according to Java specification für Java 11 and 22).<br>
         Recognize <code>module-info.java</code> to run as a modular program (for Java 22 or higher); specify libraries (see above);.</td>
   </tr>
   <tr>
      <td><a href="#CSharpProject">C#</a></td>
      <td>Compile and run C# code using .NET SDK.<br>
         Supports single projects only.</td>
   </tr>
   <tr>
      <td><a href="#PythonProject">Python</a></td>
      <td>Run a Python script.</td>
   </tr>
   <tr>
      <td><a href="#RProject">R</a></td>
      <td>Run an R script.</td>
   </tr>
   <tr>
      <td><a href="#HtmlProject">HTML</a></td>
      <td>View and test HTML, CSS and JavaScript code in the default browser.</td>
   </tr>
   <tr>
      <td><a href="#CustomCmd">Custom commands</a></td>
      <td>Specify custom system commands, e.g. to run scripts/batch files</td>
   </tr>
</table>
</nav>
<h4 id="JavaProject">Java</h4>
<ul>
<li><b>Compile and Run: </b>To run a Java program, first compile the source file(s) by selecting 'Save and compile'. If the compilation was successful the Java program can be started by selecting 'Run'.
</li>
<li><b>Modular program: </b>A Java program can be run on the module path (for Java 9 or higher). To activate "module mode", specify the name of the (main) module in the "Source" tab of the project settings. Leaving the module name blank will use the classpath. The editor can handle the following simple program structures: A <code>module-info.java</code> file is saved directly in the source directory (for single-module mode), or <code>module-info.java</code> files are stored in direct subdirectories of the source directory (for multi-module mode). In the latter case, the specified main module name must exactly match the name of the module's subdirectory.</li>
</li>
<li><b>Output directory: </b>A destination directory name for compiled class files can be specified in the 'Compile/build' tab in the project settings (like 'bin', or, for single-module mode also 'bin/my.module'). This directory is created in the project directory on compilation. Unlike other non-empty folders, this directory can be deleted in the editor's Project Explorer to allow recompilation from scratch.
</li>
<li><b>Building a JAR: </b>An executable JAR is created by selecting 'Create executable jar' in the 'Project' menu. A name for the JAR file can be specified in the 'Compile/build' tab in the project settings. If no name is entered, the name of the project directory is used. The JAR is saved in the project directory (unless a path is specified to save it to another location). For modular programs, the creation of a JAR is not yet supported.
</li>
<li><b>Including non-Java Files: </b>To include files other than Java files in a compilation (or JAR), the file extensions are specified including the leading dot in the field 'Extensions of included non-Java files' in the 'Compile/build tab'.
</li>
<li><b>Usage of Libraries: </b>Libraries may be specified in the tab 'Libraries' (Java 8), or in the tabs 'Libraries (classpath)' and 'Libraries (module path)' (Java 9 or higher) in the project settings. Libraries can be given with absolute paths or with paths relative to the project directory (e.g. 'libs/some.jar'). If a Libarary is added via browsing, the path is initially absolute and can be made relative using the 'Rel.'-button, provided the library is located within the project directory.
</li>
</ul>
<h4 id="JavaSourceFileModeProject">Java source-file mode</h4>
<ul>
</li>
<li>Select "Save and run" to run the Java program whose name is entered in the project settings.
</li>
<li>Java "source-file mode" allows running a Java program without the compilation step as described in the Java specification, introduced in Java 11 and extended in Java 22: Prior to Java 22, the program must consist of a single Java file. Since Java 22, multiple source files as well as a modular program in single-module mode are allowed.
</li>
<li>The editor enables selecting source-file mode if it is run with Java 11 or higher. Attempting to run a program with more than one source file on Java prior to 22 would result in compilation errors because the editor itself does not check if multiple files are used on Java below 22. However, the editor runs a program on the module path if a <code>module-info.java</code> file is found in the source root and the Java version is 22 or higher.
</ul>
<h4 id="CSharpProject">C#</h4>
<ul>
<li><b>Compile and Run: </b>First note that 'compile' means compiling sources and building an assembly. Therefore, 'Save and compile' is selected to create a DLL. If the compilation was successful, the program can be started by selecting 'Run'.
</li>
<li><b>Output name: </b>The name of the output file(s) initially corresponds to the name of the project directory. An alternative output name can be specified in the field 'Name for output file' in the 'Compile/build' tab. The currently set output file is the that is run if 'Run' is selected.
</li>
<li><b>Output directory: </b>The editor creates the output directory 'bin' in the project directory. Output files are created directly in this directory (without 'debug' or 'target version' subdirectories). As discribed for Java, the output directory can be deleted in the editor's Project Explorer to allow a new compilation from scratch.
</li>
<li><b>.csproj: </b>The editor generates a new minimal .csproj file for a new project. Its name corresponds to the output name defined above. The .csproj file will not be overwritten on subsequent compilations to preserve any manual changes. Accordingly, any pre-existing .csproj with the same name will not be overwritten. However, the .csproj will be regenerated if it is deleted. Likewise, a new .csproj file will be created if the output name is changed, or differs from any pre-existing .csproj.
</li>
<li><b>Specifying the output type: </b>The output types, 'Library' or 'WinExe', can be specified in the 'Compile/build' tab in the project settings. The default is 'Exe'. The output type is only automatically added to .csproj when a new .csproj file is generated.
</li>
</ul>
<h4 id="PythonProject">Python</h4>
<ul>
<li>A Python script is started by selecting 'Save and run'. The script is run by the <code>python [script_name.py]</code> command and uses the <code>-u</code> command-line option to disable output buffering.
</li>
</ul>
<h4 id="RProject">R</h4>
<ul>
<li>An R script is started by selecting 'Save and run'. The script is run by the <code>Rscript [scriptname.R]</code> command.</li>
</ul>
<h4 id="HtmlProject">HTML</h4>
<ul>
<li>An HTML (or HTM) file in the project directory is viewed in the default browser by selecting 'Save and run'.
</li>
<li>The file that is viewed is the one in the selected tab.
</li>
</ul>
<h4 id="CustomCmd">Custom commands</h4>
<ul>
<li>Enter system commands in 'Commands' tab. The 'Compile', 'Run', and/or 'Build' actions are enabled when the corresponding fields are filled.
</li>
<li>Please also note the remarks about using a system command in the <a href="#Console">Console</a> section below.
</li>
</ul>
<p><a href="#top">Back to top</a></p>
<hr>
<h2 id="Console">Console</h2>
<p>The console shows messages after compiling a project (applies to Java and C#) and the standard and error output of a tested program. A started program can be terminated by pressing the 'stop' button in the console's toolbar.</p>
<p>The console is interactive while a process is running. Any input a program requests can be typed in the console area.</p>
<p>Additionally, a custom system command can be run by pressing the 'Enter and run a system command' button (pencil icon) in the console toolbar.</p>
<p>Note the following editor-specific behavior/limitations:</p>
<ol>
<li>The 'stop'-button only ends the process started by the command. Any sub-processes started from within that process are not affected.
</li>
<li>The command is internally split into arguments at each space. Arguments containing spaces can be grouped by wrapping them in quotes. This parsing is handled by the editor itself, not by a shell, but should behave as expected in most cases.
</li>
<li>The editor reads process in- and output as UTF-8. It may be necessary to add flags or switches to enforce UTF-8 encoding if the system's or JVM's default encoding is not UTF-8.
</li>
<li>The console may appear frozen in cases where a process buffers its output because the standard output is redirected to the editor's console. This may be solved by adding a command option that disables buffering (like -u for Python, see above), if available. If a process does not exit on its own due to the redirected output, a method to explicitly terminate it can be a temporary workaround (like <code>process.exit()</code> for Node.js). Alternatively, a progam can be started by launching the native terminal from within Eadgyth. On Windows, this can be done with <code>cmd /c start cmd /k [MyProgram]</code>; on Linux/macOS the equivalent command depends on the installed terminal emulator (e.g. <code>gnome-terminal -- bash -c '[MyProgram]; exec bash'</code>).
</li>
</ol>
<p><a href="#top">Back to top</a></p>
<hr>
<p>
  Contact: <a href="mailto:m.bussiek@web.de">m.bussiek@web.de</a><br>
  (<i>A message is welcome if the program does not work as suggested in this help</i>)
</p>
