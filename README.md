<p>Eadgyth is a simple source code text editor with a built-in possibility to compile java
code and to run code written in Java, Perl and R for testing.
<p>
A list of the features of the program and documentation can be found under
<a href="https://eadgyth.github.io/Programming-Editor/">Help and program documentation</a>.
<p>
The first screenshot shows a Java project that has a simple but typical directory structure and
that is run in the program. The lower screenshot shows the simple editor view.
<p>
<img src="docs/images/Windows10SystemLAF.png" width="600"/>
<img src="docs/images/SimpleEditorView.png" width="400"/>
<br>
<p>
REQUIREMENTS FOR TESTING<br>
Running the program requires Java 8. For compiling Java code from within the program installing
the JDK 8 is required. Further, it must be ensured that the program is run using the JRE contained
in the JDK, not the public JRE. It also can be run using Java 9, but with some problems with regard
to the graphical interface (as seen on Windows 10).
<p>
The program is started by clicking the jar-file 'Eadgyth.jar". This file must be found in the
same folder as the prefs.properties file just like found in the 'EadgythProgram' folder in this
repository.
<p>
LIMITATIONS<br>
Among the countless limitations at the present stage some need mention:
<ul>
<li>Running an interactive program that asks for input through a command-line is not guaranteed
    to work in the console area of the program. Interactive programs in Java seem to work fine but,
    for example, an interactive Perl script does not unless the autoflushing of Perl's STDOUT is
    enabled in the script itself.</li>
<li>The compilation of java files and the creation of an executable jar file cannot include
    exteral libraries if the corresponding pre-defined options in the menu or the toolbar are
    selected.</li>
<li>The printing to a printer is rudimentary and is rather the blueprint for a printing function.
    The font size relative to the page size is not controlled.</li>
</ul>
<p>
IDEAS FOR FURTHER DEVELOPEMENT<br>
<ul>
<li>To develop different types of projects (for coding or other). A type of project is defined by
   the interface 'ProjectActions' in the 'projects' package.</li>
<li>To develop "Edit Tools" that can do specialized work with text files. An edit tool implements
   'AddableEditTool' in the 'edittools' package. It's graphical view can be included in the main 
   window and it has access to the file in the selected tab (the interface replaces the plugin
   interface in previous commits).</li>
</ul>
<p>
ACKLOWLEDGEMENT<br>
The program uses icons from
<a href="https://github.com/Distrotech/tango-icon-theme">Tango Desktop Project</a>.
<p>
LICENSE: MIT, see LICENSE<br>
