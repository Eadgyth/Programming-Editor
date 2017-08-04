# Java-Programming-Editor / Eadgyth
A text editor written in Java. The goal is a coding editor suited to write rather simple applications,
may be for home requirements, and to provide a simple but flexible setting up of projects. Coding
language is Java (Perl and HTML included but less elaborated).
<p>
The program is extensible by plugins which have access to the editor field in a selected tab and
by implementing other types of projects using the interface intended for this purpose.
<p>
REQUIREMENTS FOR TESTING AND COMPILATION<br>
Running the program requires JAVA 8 (JRE 8). I did not have the opportunity to test the program
on operating systems other than windows Vista, 7 and 10.<p>
The program must be found in the same folder as the two .properties files and the Resources
folder, just like in the 'EadgythProgram' folder in this repository.<p>
Plugins must be stored in the folder 'Plugins' which must be found in the same folder as the
program. Plugins currently must be available as single jar files.<p>
To compile the program the source files (packages) must be placed in a directory that also
includes the ".properties" files and the 'Resources' folder
<p>
DOCUMENTATION<br>
A guide how to configure a project and some other info is found in
<a href="https://rawgit.com/Eadgyth/Java-Programming-Editor/master/EadgythProgram/Resources/Help.html">Help.html</a>.
This file is also addressable from the 'Help' menu in the program.
<p>
The javadoc is found in this repo and also can be viewed
<a href="https://rawgit.com/Eadgyth/Java-Programming-Editor/master/javadoc/index.html">here</a>.
<p>
LIMITATIONS <br> 
1) Running an interactive (console) program that then requires writing to this process is not
guaranteed to work in the console panel of the program. Interactive programs in Java seem to
work fine but, for example, an interactive Perl script does not unless the autoflushing of its
STDOUT is enabled in the script itself.
<br>
2) The built in packing of a jar file bundles only .class files.
<br>
3) The printing to a printer is rudimentary and the font size is different from
the corresponding font size in other programs (e.g. Editor in Windows). 
<br>
<p>
PROBLEMS <br>
1) The undo/redo may may cause problems in combination with the syntax coloring. Because of this
the undo stack is emptied in some situations to work around problems that would lead to a demage
of the document. Problems may still occur and the undo/redo is by far not worked out well.<br>
2) Unexpected rendering of the UI happened when the system look and feel was selected and the
program was idling for a longer period of time (under Windows 10).<br>
<p>
IDEA FOR FURTHER DEVELOPEMENT<br>
1) To develop types of projects (for coding or other). A type of project is defined by the
interface 'ProjectActions' in the 'projects' package.<br>
2) To develop plugins that can do specialized work with text files. Presently, a plugin would
implement the interface 'Pluggable' in the 'plugins' package. An implementing plugin receives
a reference to EditorAccess class which provides a reference to the text area object (JTextPane)
in the currently selected tab, methods to modify text and a method to add a graphical view in the
main window.
<p>
CONTACT<br>
Malte Bussiek<br>
m.bussiek@web.de<br>
<p>
CONTRIBUTORS<br>
I hope for contributions and critiques.<br>
<p>
LICENSE: MIT, see LICENSE<br>
<p>
SCREENSHOT<br>
Under Windows 10, setting the system look and feel<br>
<img src="Screenshots/Windows10SystemLAF.png" width="650"/>
<p>
Basic editor view<br>
<img src="Screenshots/SimpleEditorView.png" width="500"/>
