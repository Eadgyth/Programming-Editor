<h2>Help</h2>
<a id="top"></a>
<nav class="left">
<h3>Content</h3>
<ul>
   <li><a href="#Requirements">Requirements</a><br></li>
   <li><a href="#Projects">Setup to run source code</a>
   <li><a href="#Categories">Project categories</a>
   <li><a href="#Console">Using the console</a></li>
</ul>
</nav>
<hr>
<h2 id="Requirements">Requirements</h2>
<p>See
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
      <td>HTML</td>
      <td><a href="#HtmlProject">View HTML code in the default browser</a></td>
   </tr>
   <tr>
      <td>Custom commands</td>
      <td><a href="#CustomCmd">Specify own system commands (run, compile, build)</td>
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
    is the source directory. Multi-module mode is not built in.
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
   the (error) output of a program that is tested.</p>
<p>An own system command entered in a dialog can be run in the current working
   directory (the project directory) in addition to the commands defined in a
   project (built-in or custom). The dialog is opened by selecting the 'cmd...'
   button in the toolbar of the console panel.</p>
<p>A started process can be forcibly quit by clicking the 'stop' symbol.</p>
<p>NOTE: The console does not emulate the terminal of the operating system!
   However, it is possible (or rather not prohibited) to run the system's terminal
   in the console by running the command that starts it (e.g., cmd.exe in
   Windows). It is important to note that a tested program thereupon started from
   this terminal CANNOT be forcibly quit by clicking the 'stop' symbol as this
   would only end the terminal.</p>
<h4>To run a system command...</h4>
<ol>
<li>Open the console panel by selecting 'Console' in the View menu.</li>
<li>Press the 'write' button and enter a command in the shown dialog window. Click
    ok to run the command. </li>
<li>To run a previous command press the run button button (in the tool bar of the
    console; to change a command open the command dialog again by clicking 'Cmd...'.
</li>
</ol>
<h4>Hints</h4>
<ul>
<li>Using the command to start "THE" terminal window (in Windows this would be
    cmd.exe /c start) presets the working directory for the terminal to the
    project directory. To run a program in the CMD (again Windows) and keep the
    CMD open the command would be cmd /c start cmd /k [command].</li>
<li>The option to enter commands may be used to run a batch file/shell script saved
    in the working directory (in Windows the command would be cmd.exe /c [BatchFile.bat]).
</li>
</ul>
<br>
<p><a href="#top">Back to top</a></p>
<hr>
<p>
  Contact: <a href="mailto:m.bussiek@web.de">m.bussiek@web.de</a><br>
  (<i>A message is welcome if the program does not work as suggested in this help</i>)
</p>

