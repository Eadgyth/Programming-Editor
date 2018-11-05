<h2 id="Requirements">Requirements</h2>
<p>See also
   <a href="https://github.com/Eadgyth/Programming-Editor/blob/master/README.md">
   README
   </a>
</p>
<p>For a programming project the computer is set up in the same way as if any text
   editor (like Notepad) is used for coding and the tested program would be run
   from the command-line interface of the OS. This may also mean to set environment
   variables. Presently, programming projects can be defined for Java, R and Perl.
</p>
<p>To compile Java files by the pre-defined compile option (as opposed to using the
   command-line interface of the OS or the option to start system commands in the
   console of the program) it must be made sure that the program is run using the
   Java Runtime environment (JRE) contained in the Java Development Kit (JDK).
</p>

<h2>Working with projects</h2>

<h4>Assign files as project</h4>
<ol>
<li>Open a file from or save a new file to the directory that is the presumed root
    directory of the project. The file may also be saved in a sub-directory path
    contained in that root.</li>
<li>Open the project settings window by selecting 'Assign as project' in the
    Project menu and choosing a project category. A file that belongs to the
    project must be open and also must be in the selected tab if multiple files
    are open. The input options shown in the project settings depend on the project
    category.</li>
<li>Enter the name of the presumed root directory of the project in the text field
    'Name of project root'. The name of the main source file is additionally
    required for Java, Perl and R. Also, specifying the name of a sub-directory
    where source files (or packages) are found may be required in the input field
    'Name of sources directory' and then the main file has to exist in this
    directory or a sub-directory of it. In a Java project, also the destination
    directory for compiled class files can be specified in the input field 'Name of
    executables directory' although this directory does not have to exist
    initially. It is created when the project is compiled by using the pre-defined
    compile option.</li>
<li>Click ok. If entries are correct the project can be viewed in the Project
    explorer which is found in the 'View' menu.</li>
</ol>
<br>

<h4>Adding projects and changing between projects</h4>
<ul>
<li>'Assign as project' in the project menu may be selected to assign an additional
    project. For this a file that is part of the new project must be currently
    open and also in the selected tab if multiple files are open.</li>
<li>Changing between already set projects is done by selecting, opening or saving a
    file that is part of the project to be changed to and then selecting 'Change
    project' or, to additionally view or modify the settings, 'Project settings'
    in the Project menu.</li>
</ul>
<br>

<h4>Retrieving projects</h4>
<ul>
<li>The project that was active when the program was closed the last time is
    retrieved and set active when a file of the project is opened (or a new
    file is saved to the project) and no other project is already active. Also,
    the recent project is shown in the project explorer.</li>
<li>If 'Save ProjConfig file in the project' is selected in the project settings
    the parameters for the project are stored in the project folder. This
    properties file can be deleted at any time by unselecting the option to save
    it.<br>
    A project is marked as such by the presence the ProjConfig file. This means
    that a project is loaded even if it was not active lastly or other projects
    are already active. Loading the project again takes place if a file of the
    project is opened (or a new file is saved to the project). If another project
    is already active, it is asked to change to the newly loaded project.</li>
</ul>

<h2>Testing source code</h2>
<p> A project is run for testing by selecting 'Run' in the Project menu or by
    clicking the run button in the toolbar. Commands other than the pre-defined
    commands may be entered in the console.
    A tested program can be forcibly quit by clicking the 'stop' symbol in the
    tool bar of the console.
</p>
<br>
<h4>Testing Java code and creating a jar file</h4>
<ol>
<li>To run the project for testing compile the source file(s) by selecting 'Save
    all open project files and compile project' in the Project menu or by clicking
    the corresponding button in the toolbar. If multiple files are open, only the
    selected file may be saved before the compilation by selecting 'Save selected
    file and compile project'. Note that files outside of the source root cannot
    be included in a compilation when these preset options to compile a project are
    used.</li>
<li>To include warnings -Xlint compiler options may be specified. The input field
    for the -Xlint option is found in the tab 'Compilation and build' in the
    project settings.</li>
<li>For running the project also arguments (those that would be passed to the
    main method) may be entered in the input field 'Command arguments' which is
    found in the in the tab 'Run' in the project settings.</li>
<li>An executable java archive (jar) may be created by selecting 'Create jar' in
    the project menu. A
    name for the jar file can be specified in the tab 'Compilation and build' in
    the project settings. If no name is specified the name of the main class is
    used.</li>
<li>Files other than .java files and .class files can be included in compilation
    and a jar, resepctively. The extensions of included files are specified in the
    input field 'Extensions of included non-Java files' which is found in the tab
    'Compilation and build' in the project settings. Extensions may be separated
    by spaces, commas or semicolons but must begin with the period. Including
    non-Java files is supported only if the project contains separate directories
    for source files (where also the non-Java files must be found) and for class
    files.</li>
</ol>
<br>

<h4>Testing a Perl script</h4>
<ol>
<li>Save changes to the Perl script before running it.</li>
<li>NOTE: there is a limitation when the script asks for input through the
    command-line. In that case any output from the script expected before the input
    is asked for (like "Enter your first name:", "Enter your last name:") is only
    printed in the console of the program after all inputs were made. However, the
    output would be shown as expected if the autoflushing of Perl's STDOUT is
    turned on in the script itself. A way to do this (for testing purposes) is to
    begin the script with the line "STDOUT->autoflush(1);". The other possibilty
    is, of course, to start the command-line interface of the OS to test the script.
   .</li>
<li>Arguments for the start command may be entered in the input field 'Command
    arguments' which is found in the tab 'Run' in the project settings.
</ol>
<br>

<h4>Testing an R script</h4>
<ol>
<li>Save changes to the R script before running it. Internally the 'Rscript'
    command is used.</li>
<li>Arguments and options for the start command (which internally is the 'Rscript'
    command) may be entered in the input fields 'Command arguments' and 'Command
    options', respectively, which are found in the tab 'Run' in the project
    settings.</li>
</ol>
<br>

<h4>Viewing HTML code in a web browser</h4>
<ol>
<li>Save changes to the html file. The 'run' option shows the HTML file in the
    default web browser. The HTML file to be viewed must be open and also selected
    if multiple files are open.</li>
</ol>

<h2>Using the console</h2>
<p>The console shall show messages after compiling a project (applies to Java) and
   the (error) output of a program that is tested.</p>
<p>In addition, self-chosen system commands entered in a dialog can be run in the
   current working directory (the project directory).</p>
<p>A run process can be forcibly quit by clicking the 'stop' symbol.</p>
<p>NOTE: The console does not emulate the terminal of the operating system!
   However, it is possible (or rather not prohibited) to run the system's terminal
   in the console by running the command that starts the terminal (e.g., cmd.exe in
   Windows). It is important to note that a tested program thereupon started from
   this terminal CANNOT be forcibly quit by clicking the 'stop' symbol.</p>
<h2>To run a system command...</h2>
<ol>
<li>Open the console panel by selecting 'Console' in the View menu.</li>
<li>Press the button 'Cmd...' and enter a command in the shown dialog window. Click
    ok to run the command.</li>
<li>To run a previous command press the run button button (in the tool bar of the
    console; to change a command open the command dialog again by clicking 'Cmd...'.
</li>
</ol>
<h4>Hints</h4>
<ul>
<li>Using the command to start "THE" terminal window (in Windows this would be
    cmd.exe /c start) presets the working directory for the terminal to the
    project directory.</li>
<li>The option to enter commands may be used to run batch files saved in the
    working directory (in Windows the command would be cmd.exe /c aBatchFile.bat).
</li>
</ul>

<h2>Using the project explorer</h2>
<ul>
<li>The project explorer is opened by selecting 'Project explorer' in the View
    menu. It shows the file system of a project once one is assigned or, if given,
    that of the recent project.</li>
<li>The explorer always shows the project that is currently set active. The topmost
    directory that can be navigated to is always the project root.</li>
<li>A folder or file may be deleted after a right click on a folder or file and
    selecting 'Delete'. However, folders that are not empty are protected from
    deletion. The exception is, if present, the folder for executable files. This
    may be deleted for a renewed compilation (applies to Java projects).
</li>
<li>A new folder may be created after a right click on a folder and selecting
   'Create new folder'.
</li>
<li>Changes to the project's file structure made from within the program should be
    updated in the file view. Changes made from outside the program would be updated
    "manually" by clicking the 'refresh' symbol.
</li>
</ul>  
<p>Contact: <a href="mailto:m.bussiek@web.de">m.bussiek@web.de</a><br>
(<i>A message is welcome if the program does not work as suggested in this help</i>)
</p>
    
