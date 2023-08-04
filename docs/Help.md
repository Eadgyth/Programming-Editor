<h2>Help</h2>
<a id="top"></a>
<h3>Content</h3>
<nav>
<ul>
   <li><a href="#Requirements">Requirements</a></li>
   <li><a href="#Projects">Setup to run source code</a></li>
   <li><a href="#Categories">Project categories</a></li>
   <li><a href="#Console">Using the console</a></li>
</ul>
</nav>
<hr>
<h2 id="Requirements">Requirements</h2>
<p>
The editor requires Java 8 or higher. To compile your own Java code by the
built-in compile function Eadgyth must be run with the runtime environment
(JRE) contained in a Java Development Kit (JDK) because it uses a compiler
API contained in the JDK. As of Java 11 JREs are only available in conjunction
with a JDK anyway.
<p>
It is further important to note that the built-in commands to run code from
within the editor basically work in the same way as if commands are run from
the command line or terminal. This means, that the computer must be set up
accordingly:<br><b>The executables of a language must be installed and the
paths to them set permanently as PATH system (environment) variables.</b>
<p>
See also
<a href="https://github.com/Eadgyth/Programming-Editor/blob/master/README.md">
README</a>
<hr>
<h2 id="Projects">Setup to run source code</h2>
<h4>Setting a project</h4>
<ol>
<li>Open a file from or save a new file to the directory that is the intended
    root directory of the project. The file may also be saved in a subdirectory
    or a subdirectory path contained in the project directory. Assigning a
    project only requires that a (any) file that is contained in the project
    directory is open or in the selected tab if multiple files are open. The
    project directory is the working directory where commands (built-in or
    custom) are executed.</li>
<li>Open the project settings by selecting 'Settings for...' in the 'Project'
    menu and choose the suitable <a href="#Categories">category</a> to open
    the project settings.
    </li>
<li>The input options to set a project depend on the category. The required
    entries are the name of the project directory and, except for the
    categories 'HTML' and 'Custom commands', the name of the "main" source
    file. The name of a separate subdirectory containing source files inside
    the project (a source directory) would have to be entered in the
    corresponding text field below the field for the project directory. Click
    ok. If the input was correct, that is, the specified source file is found
    in the project directory, the project is set "active" and the project
    files can be viewed in the 'Project explorer' which is opened from the
    'View' menu.</li>
</ol>
<h4>Retrieving projects</h4>
<ul>
<li>The project that was active when the program has been closed the last time
    is re-loaded and set active when a file of the project is opened (or a new
    file is saved to the project) and no other project is already active.</li>
<li>If 'Save ProjConfig file in the project to retrieve settings' is selected
    in the project settings, the parameters for the project are stored in the
    project folder. This .properties file can be deleted at any time by
    unselecting the option to save it.<br>
    The ProjConfig file marks a project as an "Eadgyth project". This means that
    a project is loaded even if it was not active lastly or other projects are
    already active. If another project is already active, it is asked to change
    to the newly loaded project.</li>
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
      <td>Compile and run Java code; specifiy libraries;
          create an executable jar file</td>
   </tr>
   <tr>
      <td><a href="#CSharpProject">C#</a></td>
      <td>Compile and run C# code; specify DLLs used as
         libraries or also create a DLL</td>
   </tr>
   <tr>
      <td><a href="#PerlProject">Perl</a></td>
      <td>Run a Perl script; check syntax without running</td>
   </tr>
   <tr>
      <td><a href="#PythonProject">Python</a></td>
      <td>Run a Python script</td>
   </tr>
   <tr>
      <td><a href="#RProject">R</a></td>
      <td>Run an R script</td>
   </tr>
   <tr>
      <td><a href="#HtmlProject">HTML</a></td>
      <td>View HTML code in the default browser</td>
   </tr>
   <tr>
      <td><a href="#CustomCmd">Custom commands</a></td>
      <td>Specify own system commands, e.g. to run scripts/batch files
      (run, compile, build)</td>
   </tr>
</table>
</nav>
<h4 id="JavaProject">Java</h4>
<ul>
<li>To run a Java program first compile the source file(s) by selecting 'Save
    and compile' in the 'Project' menu (or clicking the corresponding button
    in the toolbar). If the compilation was successful the Java program is
    started by clicking 'Run' in the 'Project' menu or the corresponding button
    in the toolbar.</li>
<li>If the Editor is run on Java 9 or higher a Java program may be run on the
    module path by specifying a module name in the 'Source' tab in the project
    settings. Running a module in Eadgyth requires that the module directory
    is the source directory. Multi-module mode is not built in.</li>
<li>A separate destination directory for compiled class files inside the
    project directory can be specified in the 'Compile/build' tab in the
    project settings (like 'bin' or 'out'). This directory is created when
    Java files are compiled. It can be deleted in the project explorer for a
    renewed compilation.</li>
<li>An executable Java archive (jar) may be created by selecting 'Create
    executable jar' in the 'Project' menu. A name for the jar file can be
    specified in the 'Compile/build' tab in the project settings. If the text
    box for the name is left empty, the name of the project directory is used.
    The jar is saved in the project directory unless a pathname is specified to
    save it to another location.</li>
<li>Files other than Java files can be included in a compilation (and a jar).
    Extensions of included files are specified with the beginning period in the
    input field 'Extensions of included non-Java files' in the 'Compile/build
    tab.</li>
<li>Libraries may be specified in the tab 'Libraries' in the project settings.
    These are added to classpath and can be jar files or folders containing
    class files. Classpath can be a path relative to the project directory
    (e.g. 'libs/some.jar' given it exists in the project directory) but also an
    absolute path. When the program is packaged in a jar the paths (relative
    or absolute) are added to the CLASS-PATH header in the manifest file as
    specified.</li>
<li>If the Editor is run on Java 9 or higher Library modules may be specified
    in the 'Library modules' tab. A library module may be given as an absolute
    path or as a path relative to the project directory.</li>
</ul>
<h4 id="CSharpProject">C#</h4>
<ul>
<li>First, it is necessary to set the path to the C# compiler as a system
    variable. In Windows the path to csc.exe is like:
   'C:\Windows\Microsoft.NET\Framework64\v[?]\'.
    For Linux/Mac it is assumed that Mono is installed to compile and run C#
    and the paths to Mono and the Mono mcs compiler are set permanently as
    well.</li>
<li>Compile the source file(s) by selecting 'Save and compile' in the 'Project'
    menu (or clicking the corresponding button in the toolbar). If the
    compilation was successful the program is started by selecting 'Run' in the
    'Project' menu or the corresponding button in the toolbar.</li>
<li>The default name of a generated executable file is the name of the source
    file specified in the project settings. An alternative name (or a pathname)
    for an output file can be entered in the 'Compile/build' tab in the
    project settings.</li>
<li>A separate destination directory for a compiled file inside the project
    directory can be specified in the 'Compile/build' tab (like 'bin' or 'out').
    This folder is created when .cs files are compiled.</li>
<li>A DLL may be created by specifying the corresponding compiler option
    '-t:library' (or '-target:library') in the 'Compile/build' tab. It is also
    possible to enter a pathname for the output file so that the DLL is
    created in another project that uses or tries it.</li>
<li>DLLs to be used by a C# program are specified in the 'Libraries' tab. The
    referenced DLLs must be found in the same directory as the .exe that uses
    them.</li>
</ul>
<h4 id="PerlProject">Perl</h4>
<ul>
<li>A Perl script is started by selecting 'Save and run' (or by clicking
    the corresponding button in the toolbar).</li>
<li>The Perl script may be checked for correct syntax without running it by
    by selecting 'Save and compile' (this starts the script with the -c
    option).</li>
<li>NOTE: For correct output of a Perl program to the console window it may be
    necessary to turn of buffering of the standard output in the script (e.g.
    by adding $| = 1 at the beginning of the script).</li>
</ul>
<h4 id="PythonProject">Python</h4>
<ul>
<li>A Python script is started by selecting 'Save and run' (or by clicking
    the corresponding button in the toolbar).</li>
<li>The script is internally run by the 'python [script_name.py] command and
    additionally uses the -u command-line switch to disable output buffering.</li>
</ul>
<h4 id="RProject">R</h4>
<ul>
<li>An R script is started by selecting 'Save and run' (or by clicking the
    corresponding button in the toolbar).</li>
<li>The script is internally run by the Rscript [scriptname.R] command.</li>
</ul>
<h4 id="HtmlProject">HTML</h4>
<ul>
<li>An HTML file is viewed in the default browser by selecting 'Save and run'
    (or by clicking the corresponding button in the toolbar).</li>
<li>Any HTML file that is in the project directory can be viewed. The
    viewed file is the currently open file or the file in the selected tab if
    multiple files are open.</li>
</ul>
<h4 id="CustomCmd">Custom commands</h4>
<ul>
<li>Enter system commands in 'Commands' tab where needed. The UI controls
    'Save and Compile', 'Run' and/or 'Build' are active upon filling-in
    fields labeled accordingly. The intention for this category is to run
    own batch/script files.</li>
</ul>
<p><a href="#top">Back to top</a></p>
<hr>
<h2 id="Console">Using the console</h2>
<p>The console shows messages after compiling a project (applies to Java and
   C#) and the (error) output of a program that is tested. A started program
   can be quit by pressing the 'stop' button in the toolbar of the console.</p>
<p>Also, a system command can be entered in a dialog which is opened by pressing
   the 'write' button in the toolbar of the console. However, commands are
   taken "as-is" which means that they are not processed in any way and there
   is no Eadgyth-specific command syntax. Also, the editor's console does not
   emulate the command line/terminal of the operating system. Running a
   command just means to start a process whose output is displayed and which
   may ask for input that can be typed in. Therefore, the following hints may
   help ...</p>
<h4>How to define commands</h4>
<ul>
<li>To explain the concept: the command could indeed
    be to run the shell in the editor's console. In Windows the command to be
    entered in the dialog would be cmd.exe. Then, the console would, with quite
    some restrictions, behave like the command line. However, pressing the
    'stop' button would only end the shell but may not end any process started
    from there. Thus, this option is not recommended to run a program to be
    tested as this may not terminate properly.</li>
<li>The other way is to start a shell process with a command like cmd.exe /c
    [command] (Windows) or /bin/bash -c [command] (Unix/Mac). This starts a
    process from the shell that can be terminated by clicking the 'stop'
    button. In this way it is possible to run batch or script files in the
    project directory.</li>
<li>Some commands do not require to start a process via a shell process
    (basically, those which invoke an executable, like 'java [aJavaProgram]).
    However, it is required if the command uses shell syntax or it can be
    required if no full file path is given: for example, if the file aFile.bat
    saved in the project directory is to be executed, both the commands
    'abs\path\to\aFile.bat' and 'cmd /c aFile.bat' would work whereas just
    typing 'aFile.bat' won't.</li>
<li>It may also be useful to open THE command line or terminal of the OS. In
    Windows, for example, CMD is opened by the command 'cmd.exe /c start'.
    Then, CMD is opened with the current working directory (the project
    directory) preset.</li>
</ul>
<h4>To run a system command...</h4>
<ol>
<li>Open the console panel by selecting 'Console' in the View menu.</li>
<li>Press the 'write' button and enter a command in the shown dialog window.
    Click ok to run the command.</li>
<li>To run a previous command press the run button (in the tool bar of the
    console; to change a command open the dialog again by pressing the 'write'
    button.</li>
<li>A commands that contains spaces should be enclosed in quote marks.</li>
</ol>
<p><a href="#top">Back to top</a></p>
<hr>
<p>
  Contact: <a href="mailto:m.bussiek@web.de">m.bussiek@web.de</a><br>
  (<i>A message is welcome if the program does not work as suggested in this help</i>)
</p>
