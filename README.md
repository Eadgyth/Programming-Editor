<p>Eadgyth is a simple source code text editor, written in Java. It features an easy setting
up of coding projects and switching between a number of set projects
<p>
The coding language is currently Java but the program is made to integrate other languages
or types of projects. To illustrate this draft implementations for writing in Perl and HTML
are included.
<p>Features of the program can be viewed at
<a href="https://eadgyth.github.io/Programming-Editor/">Help and program documentation</a>.
<p>
REQUIREMENTS FOR TESTING<br>
Running the program requires Java 8. For compiling Java code from within the program installing
the JDK 8 is required. Further, it must be ensured that the program is run using the JRE contained
in the JDK, not the public JRE. It also can be run using Java 9, but with some problems with regard
to the graphical interface (as seen on Windows 10).
<p>
The program is started by clicking the jar-file 'Eadgyth.jar". This file  must be found in the
same folder as the prefs.properties file and the Resources folder, just like found in the
'EadgythProgram' folder in this repository.<p>
<p>
DOCUMENTATION<br>
A guide how to configure a project and an overview of the code of the program can be found at<br>
<a href="https://eadgyth.github.io/Programming-Editor/">
   Help and program documentation</a>.
<p>
LIMITATIONS<br>
While the limitations are certainly countless some functions that are already present are still
embryonic. Some are:
<ul>
<li>Running an interactive program that asks for input through a command-line is not guaranteed
   to work in the console area of the program. Interactive programs in Java seem to work fine but,
   for example, an interactive Perl script does not unless the autoflushing of its STDOUT is enabled
   in the script itself.</li>
<li>The built in packing of a jar file for a java application bundles only .class files.</li>
<li>The printing to a printer is rudimentary and is rather the blueprint for a printing function.
    The page size is not controlled.</li>
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
LICENSE: MIT, see LICENSE<br>
