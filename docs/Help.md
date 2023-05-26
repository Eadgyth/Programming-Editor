<h2>Help</h2>
<a id="top"></a>
<h3>Content</h3>
<nav>
<ul>
   <li><a href="#Requirements">Requirements</a><br></li>
   <li><a href="#Projects">Setup to run source code</a></li>
   <li><a href="#Categories">Project categories</a></li>
   <li><a href="#Console">Using the console</a></li>
</ul>
</nav>
<hr>
<h2 id="Requirements">Requirements</h2>
<p>
The editor requires Java 8 or higher. To compile your own Java code by the
built-in compile function Eadgyth must be run with the runtime enverinment
(JRE) contained in a Java Developement Kit (JDK) because it uses a compiler
API within the JDK. As of Java 11 JREs are only available in conjunction
with A JDK anyway.
<p>
Generally, the aim of Eadgyth to provide some flexibility in the settings
of projects, e.g., in terms of directory structure, and the possiblity to
reload project settings after newly starting the editor so that is can be
used to work on real coding projects. However, it is still just a text editor
and does not integrate any tools required to run code. Thus, the builit-in
functions basically work in the same way as if system commands are run from
a command-line. This means, that the computer must be set up in the same way:
The executables for a language must be installed and the path to them set
as environment variables.
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
    or a subdirectory path contained in the project directory. Assigning a project
    only requires that a (any) file that is contained in the project directory
    is open or in the selected tab if multiple files are open. The project
    directory is the working directory where commands (built-in or custom) are
    executed.</li>
<li>Open the project settings by selecting 'Settings for...' in the 'Project'
    menu and choosing a <a href="#Categories">category</a> to open the project
    settings.
    </li>
<li>The input options for setting a project depend on the category. The required
    entries are the name of the project directory and the name of the "main"
    source file to run (no name is asked for in the categories 'HTML' and 'Custom
    commands'). If the source file is saved in a separate subdirectory inside the
    project (a source directory) the name of this subdirectory would have to be
    entered in the corresponding text field below the field for the project directory.
    Click ok. If the input was correct the project is set "active" and also the
    project files can be viewed in the 'Project explorer' which is opened from the
    'View' menu.</li>
</ol>
<h4>Retrieving projects</h4>
<ul>
<li>The project that was active when the program was closed the last time is
    retrieved and set active when a file of the project is opened (or a new
    file is saved to the project) and no other project is already active. Also,
    the recent project is shown in the project explorer.</li>
<li>If 'Save ProjConfig file in the project to retrieve settings' is selected
    in the project settings the parameters for the project are stored in the
    project folder. This properties file can be deleted at any time by
    unselecting the option to save it.<br>
    A project is marked as such by the presence the ProjConfig file. This means
    that a project is loaded even if it was not active lastly or other projects
    are already active. Loading the project again takes place if a file of the
    project is opened (or a new file is saved to the project). If another project
    is already active, it is asked to change to the newly loaded project.</li>
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
      <td>Java</td>
      <td><a href="#JavaProject">Compile and run Java code; create an executable
          jar file</a></td>
   </tr>
   <tr>
      <td>Perl</td>
      <td><a href="#PerlProject">Run a Perl script; check syntax without running
      </a></td>
   </tr>
   <tr>
      <td>Python</td>
      <td><a href="#PythonProject">Run a Python script</a></td>
   </tr>
   <tr>
      <td>R</td>
      <td><a href="#RProject">Run an R script</a></td>
   </tr>
   <tr>
      <td>Under work: an experimental C# category</td>
      <td>Goal: Compile and run C# code</td>
   </tr>
   <tr>
      <td>HTML</td>
      <td><a href="#HtmlProject">View HTML code in the default browser</a></td>
   </tr>
   <tr>
      <td>Custom commands</td>
      <td><a href="#CustomCmd">Specify own system commands (run, compile, build)</a></td>
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
<li>A separate destination directory for compiled class files can be specified
    in the 'sources' tab in the project settings. This directory is created in
    the project directory when Java files are compiled and can be deleted in the
    project explorer for a renewed compilation. It is recommended to specify a
    destination directory if Java files/packages are also stored in a separate
    source directory.</li>
<li>An executable java archive (jar) may be created by selecting 'Create
    executable jar' in the 'Project' menu. A name for the jar file can be
    specified in the tab 'Compile/build' in the project settings. The jar is
    saved in the project directory unless a pathname is specified to save it
    to another location.</li>
<li>Files other than Java files can be included in a compilation and a jar if
    separate source and destination directories are present. Extensions of
    included files are specified with the beginning period in the input field
    'Extensions of included non-Java files' in the 'Compile/build
    tab in the project settings.</li>
<li>Libraries may be specified in the tab 'Libraries' in the project settings.
    These are added to classpath and can be jar files or folders containing class
    files. Classpath can be a path relative to the project directory (e.g.
    libs/some.jar given this file exists in the project directory) but also an
    absolute path. When the program is packaged in a jar the paths are added to
    the CLASS-PATH header in the manifest file as specified.</li>
<li>If the Editor is run on Java 9 or higher Library modules may be specified
    in the 'Library modules' tab. A library module may be given as an absolute
    path or as a path relative to the project directory.</li>
</ul>
<h4 id="PerlProject">Perl</h4>
<ul>
<li>A Perl script is started by selecting 'Save and run' (or by clicking
    the corresponding button in the toolbar).</li>
<li>The Perl script may be checked for correct syntax without running it by
    by selecting 'Save and compile' (this starts the script with the -c
    option).</li>
<li>NOTE: For correct output of a Perl program to the console window it may be
    necessary to turn of buffering of the standard output in the script (e.g. by
    adding $| = 1 at the beginning of the script).</li>
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
<li>Any HTML file that is located in the project directory can be viewed. The
    viewed file is the currently open file or the file in the selected tab if
    multiple files are open.</li>
</ul>
<h4 id="CustomCmd">Custom commands</h4>
<ul>
<li>Enter system commands in 'Commands' tab where needed. The UI controls
    'Save and Compile', 'Run' and/or 'Build' are active upon filling-in
    fields labeled accordingly. The intention for this category is to run
    batch files/shell scripts.</li>
</ul>
<p><a href="#top">Back to top</a></p>
<hr>
<h2 id="Console">Using the console</h2>
<p>The console shows messages after compiling a project (applies to Java) and
   the (error) output of a program that is tested. A started program can be
   quit by pressing the 'stop' botton in the toolbar of the console panel.</p>
<p>Also, a system command can be entered in a dialog (by pressing the 'write'
   button). However, commands are taken "as-is" which means they are not
   processed in any way and there is no Eadgyth specific command syntax.
   Therefore, the following hints may help ... </p>
<h4>How to define commands</h4>
<ul>
<li>First, it is important to note that the editor's console does not emulate
   the terminal of the operating system. Running a command just means to start
   a program whose output is displayed (or which may ask for input).</li>
<li>The obove point means that the command could be to run the terminal in the
   editor's console so to speak. In Windows the command to be entered would be
   cmd.exe. Then, for example, it would be possible to type 'dir' in the
   (editor's) console to output the directories of the current working directory.
   However, pressing the 'stop' button would only end the shell but not any
   process started from there. Thus, this option is not recommended to run a
   program to be tested as this may not terminate properly.</li>
<li>The other way is to start a terminal process with a command like cmd.exe /c
   [command] (Windowns) or sh -c [command] (Unix/Mac). In this way it is for
   example possible to run batch or script files in the project directory.</li>
<li>Some commands do not require to invoke a terminal process. However, it is
   required if the command uses terminal syntax.</li>
<li>It may also be useful to open 'THE' terminal, for example to open CMD by
   the command 'cmd.exe /c start' under Windows.</li>
</ul>
<h4>To run a system command...</h4>
<ol>
<li>Open the console panel by selecting 'Console' in the View menu.</li>
<li>Press the 'write' button and enter a command in the shown dialog window.
    Click ok to run the command.</li>
<li>To run a previous command press the run button (in the tool bar of the
    console; to change a command open the dialog again by pressing the 'write'
    button.</li>
</ol>
<p><a href="#top">Back to top</a></p>
<hr>
<p>
  Contact: <a href="mailto:m.bussiek@web.de">m.bussiek@web.de</a><br>
  (<i>A message is welcome if the program does not work as suggested in this help</i>)
</p>

