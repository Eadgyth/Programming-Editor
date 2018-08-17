This is a simple text editor, written in Java, which can also be used for writing and testing
source code. It is intended for smaller coding projects, maybe to develop applications for own
use or for learning purposes. Project categories in the present version are Java (so far worked
out most), Perl, R and HTML. The code of this program is made such that it is possible to
implement other project categories. A goal is that it should be easy to assign files as a coding
project and that several projects, which can be switched between, can be defined in parallel.
<p>
DOCUMENTATION<br>
A list of the features, screenshots, a help site and code documentation can be found under
<a href="https://eadgyth.github.io/Programming-Editor/">here</a>.
<p>
REQUIREMENTS FOR TESTING<br>
An executable jar of the program is found in the folder "JarAndPreferences". The jar should be kept
in the same directory as the prefs.properties file. "JarAndPreferences" can be also downloaded
<a href="https://github.com/Eadgyth/Programming-Editor/releases">here</a>.
<p>
Running (and compiling) the program requires Java 8. Running it using Java 9 (or 10) causes some
problems with regard to the graphical interface (at least under Windows).
<p>
If the program shall be used for compiling Java code by the built-in compile option it must be made 
that the program is run using the JRE contained in the JDK (and not the public JRE).
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
<li>I tested the program on Windows. I would appriciate feedback, especially from somebody who may
    have tried it on other platforms (m.bussiek@web.de).</li>
</ul>
<p>
IDEAS FOR FURTHER DEVELOPEMENT<br>
<ul>
<li>To implement other project categories. In principle, this is not limited to programming
    and coding projects. A type of project is defined by the interface 'ProjectActions' in the
    'projects' package.</li>
<li>To develop "Edit Tools" that can do specialized work with text files. An edit tool implements
    'AddableEditTool' in the 'edittools' package. It's graphical view can be included in the main 
    window and it has access to the file in the selected tab.</li>
</ul>
<p>
ACKLOWLEDGEMENT<br>
The program uses icons from
<a href="https://github.com/Distrotech/tango-icon-theme">Tango Desktop Project</a>.
<p>
LICENSE: MIT, see LICENSE<br>
