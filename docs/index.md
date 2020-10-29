I started to make a text editor when I started to learn Java for myself and wished that NotePad
had a compile and a run button. I knew that really versatile code editors and IDEs exist but
using them seemed very complicated for me …

Therefore, a goal is that it is as simple as possible to set up the editor to try out code
by using built-in functions and provide at the same time some flexibility to use it for a real
little coding project. The number of languages for which a ‘run’ function is built-in is
limited so far (Java, Perl, R, Python and HTML) but the code for the editor is somewhat
easily extensible to add support for other languages or even to simply replace the languages
supported in the current version with a single desired one.

The github repository for the program also contains, well ... "releases" (a record of my
attempts to fix bugs and add useful features). Anyway, there an executable jar file of the
program can be found for trying it.

See also <a href="https://github.com/Eadgyth/Programming-Editor/blob/master/README.md">
README</a> for requirements.

<hr>
<h3>How to set up Eadgyth to run code?</h3>
The program is set up for running source code by defining a "project". This is initially
just a working directory which distinguishes "project files" from "non-project files" and
where commands (built-in or self defined) are executed. Setting a project (or changing
to another already set project) requires that any file that is part of the project is
open and also in the selected tab if multiple files are open. This file may also be found
in a sub-directory path relative to the project directory.
<br><br>
Here is an example for a 'Hello World' program in Java. The 'Hello World' program was saved
as HelloWorld.java in the directory named ..\helloworld\scr\hello. 'helloworld' is the project
directory, or the project root. 'scr' is defined as the sources directory which contains the
package 'hello' with the source file. The project settings are opened by selecting
'Settings for...' and choosing the category Java:

<img src="images/opensettings.png" width="600"/>


In the project settings the name of the project directory, the name of the sources directory
and the name of the Java file are entered. Also the name for a distination directory for a
compiled class file (next step) is specified. This directory does not have to exists
initially:

<img src="images/projectsettings.png" width="600"/>


After setting the project it can be viewed in the 'Project explorer'. It is also checked to
save the configuration to be able to reload the project when HelloWorld.java is opened after
newly starting the editor. Then it is tried to compile the program. However, the console
panel that opened shows errors:

<img src="images/compile.png" width="600"/>


After correcting the code a new compilation generates the class file hello\HelloWorld.class in
the 'bin' directory and the program can be run:

<img src="images/run.png" width="600"/>

<hr>
Some more detailed info is found at the
<a href="https://eadgyth.github.io/Programming-Editor/help/help.html">help site</a> which also
accessible from the '?' menu.

