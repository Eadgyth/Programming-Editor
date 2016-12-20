# Java-Programming-Editor / Eadgyth
A text editor written in Java that helps coding (in Java, Perl, HTML) and that is extensible
by plugins which may be developed to do different types of work with text files.
<p>
REQUIREMENTS FOR TESTING <br>
Running the program requires JAVA 8 (JRE 8). I did not have the opportunity to test
the program on operating systems other than windows Vista, 7 and 10.<p>
The program must be found in the same folder as the two .properties files and the Resources
folder, just like in the 'EadgythProgram' folder in this repository.<p>
Plugins must be stored in the folder 'Plugins' in the same folder as the program and be available
as single jar files.<p>
To compile the program the source files (packages) must be placed in a directory that also
includes the ".properties" files and the 'Resources' folder.<p>
A guide how to configure a project and some other info is found in the Resources
folder (Help.html) and is adressable from the '?' menu. 
<p>
IDEA FOR FURTHER DEVELOPEMENT
1) More types of projects may be added. A type of project is defined by an Interface
'ProjectActions'.
2) Develop plugins to make specialized types of work with text files. Presently, a
plugin would implement the interface Pluggable which cane be implemented to have
access to the text document that is in the selected tab and that can have a graphical
view in the main window.
<p>
DOCUMENTATION <br>
The javadoc of the program is found in this repository (Docs/Ead-JavaDoc).
<p>
LIMITATIONS <br>
1) The Undo/redo is a place holder for a real undo/redo as it simply undoes/redoes single
characters.
<br>
2) The console has a function to run system commands (defined the class 'ProcessStarter'
in the package 'eg.console'). Interactive programs that then require reading from and
writing to the started process by typing in the console are not guaranteed to work.
Interactive programs in Java seem to work fine but a Perl script does not unless the
autoflushing of STDOUT is enabled in the script, for example.
<br>
3) The built in packing of a jar file bundles only .class files.
<br>
<p>
PROBLEMS <br>
4) The rendering of the GUI has gone wrong after the computer was in sleep mode when
the system look and feel was selected.
<br>
5) Removing/replacing chunks of text leads under some condition to the coloring of text 
right above block comments in the block comment color (class eg.document.Coloring).
<br>
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
<img src="Screenshots/Windows10SystemLAF.png" width="600"/>

