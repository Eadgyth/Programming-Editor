<img src="images/EadgythIcon.png" width="50"/>
<p>Eadgyth is a simple source code text editor, written in Java. It features an easy setting
up of coding projects and switching between a number of set projects
<p>
The motivation is to have an editor that helps coding in may ways but that is at the
same time simple to work with as much as possible. It may be especially suited to write
and test applications for home or lerning requirements.
<p>
The coding language is currently Java but the program is made to integrate other languages
or types of projects. To illustrate this draft implementations for writing in Perl and HTML
are included.
<p>
FEATURES<br>
<ul>
   <li>Showing files in tabs</li>
   <li>Undo / redo</li>
   <li>Block-wise indentation</li>
   <li>A basic file explorer for projects</i>
   <li>An "exchange editor" (the panel at the right in the image below) to edit text in a
   separate view and to facilitate the exchange of text within a file or between files</li>
   <li>A basic syntax highlighting for java, perl, html, javascript and CSS.</li>
   <li>A basic auto-indentation</li>
   <li>The setting of projects without creating any extra data files outside the program's
   own folder (a properties file may optionally be saved in a project's folder though). Projects
   are retrieved after newly starting the program and a number of projects can be defined
   and set active during the program's runtime. A project is assigned to the program when a file
   of that project is opened (or newly saved) and is in the selected tab. This file may be any
   file (that is not necessarily a main source file) that is found in the working directory
   of the project or a subdirectory from that.</li>
   <li>Compiling and testing Java code and bundling a Java program in a jar file; testing
   a Perl script; viewing Html code in the default web browser</li>
   <li>A basic (interactive) console to view the output (error messages) during compiling
   and testing a program (also allows running self-chosen system commands)</li>
</ul>
(Under Windows 10, setting the system look and feel)<br>
<img src="images/Windows10SystemLAF.png" width="550"/>
<p>
<img src="images/SimpleEditorView.png" width="400"/>
<p>
REQUIREMENTS FOR TESTING<br>
Running the program requires Java 8. For compiling Java code installing the JDK 8 is required
and it must be ensured that the program is run using the JRE contained in the JDK, not the
public JRE. It also can be run using Java 9, but with some problems with regard to the graphical
interface (as seen on Windows 10).
<p>
The program must be found in the same folder as the prefs.properties file and the Resources
folder, just like found in the 'EadgythProgram' folder in this repository.<p>
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
<li>The syntax coloring is basic. It's also still incomplete for Html. Many html tags and attributes
    are not colored.</li>
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
