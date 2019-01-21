This is a simple text editor, written in Java, which may be used for writing and
testing source code. A goal is that it should be easy to assign files as a programming
project to use built-in actions to run (or compile/build) a project. Further, several
projects, which can be easily switched between, may be defined in parallel. Project
categories are Java (so far worked out most), Perl, R and HTML in the present version.
A project without a specific category (generic) may be as well defined to run self-chosen
system commands in a semi-interactive console. The code of this program is made such that
it is possible to implement other project categories (a basic support for Python is slowly
under work). The program also provides a basic file explorer.
<p>
DOCUMENTATION<br>
A list of the features, screenshots, a help site and code documentation can be found under
<a href="https://eadgyth.github.io/Programming-Editor/">here</a>.
<p>
REQUIREMENTS FOR TESTING<br>
Running (and compiling) the program requires Java 8 or higher.
<p>
If the program shall be used for compiling Java code by the built-in compile option it must be
made sure that the program is run using the JRE contained in the JDK (and not the public JRE).
<p>
An executable jar of the program together with a Prefs file is found in the folder
"JarAndPreferences". The jar was created after compiling with Java JDK 8.
<p>
LIMITATIONS<br>
Among the countless limitations some need mention:
<ul>
<li>The editor includes a basic console to show output/error messages from a tested program
    or also freely defined system commands. This console is in principle interactive but
    interaction may not work in all cases. Specifically, the output of processes that buffer
    the standard output is not shown interactively but only after the process ended. Therefore,
    buffering would have to be disabled, for example, by specifying a corresponding command option,
    if available for a given language/process.</li>
<li>The compilation of java files and the creation of an executable jar file cannot include
    external libraries if the built-in options for compiling and crating a jar are selected.</li>
<li>I tested the program on Windows. There are some problems with the graphical appearance
    on a high dpi screen. I would be grateful for feedback, especially from somebody who may
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
    window and it has access to methods that define a document in the selected tab.</li>
</ul>
<p>
ACKLOWLEDGEMENT<br>
The program uses icons from
<a href="https://github.com/Distrotech/tango-icon-theme">Tango Desktop Project</a>.
<p>
LICENSE: MIT, see LICENSE<br>
