I started to make a text editor when I started to learn Java for myself and wished that NotePad
had a compile and a run button. I knew that really versatile code editors and IDEs exist but
using them seemed very complicated to me …

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
<h4> Example for a 'Hello World' program in Python</h4>
Here is an example for a 'Hello World' program in Python. The script was saved as hello_world.py
in the directory helloworld. The project settings are opened by selecting 'Settings for...' and
choosing the category Python:

<img src="images/opensettingsPy.png" width="500"/>

In the project settings the name of the project directory, or the project root
(helloworld), and the name of the script are entered:

<img src="images/projectsettingsPy.png" width="500"/>


Then, after clicking OK, the script is run by selecting 'Save and run' in the toolbar or
in the 'Project' menu. This opens the console panel which shows the output:

<img src="images/runPy.png" width="500"/>
<br><br>
<hr>
<h4> Example for a Java program</h4>
The next example is a 'Hello World' program in Java, but now it is pretended that a Java
project with a typical directory structure is run. The 'Hello World' program was saved as
HelloWorld.java in the directory named ..\helloworld\scr\hello. 'helloworld' is the project
directory. 'scr' is defined as the source root directory within the project and 'hello' is
a package that contains the source file. It is declared at the top of the code that the
source file is part of this package. The project settings are opened by choosing the
category Java:

<img src="images/opensettings.png" width="500"/>


In the project settings for Java the name of the project directory, the name of the sources
directory and the name of the Java file are entered. Note, that structuring a project in this
way is not required to run a Java program in the editor. HelloWorld.java could have been
saved in the directory helloworld and the input field for the source directory left blank.
Finally, the name for a distination directory for a compiled class file (next step) is
specified. This directory does not have to exists initially:

<img src="images/projectsettings.png" width="500"/>


A set project may be viewed in the 'Project explorer'. It is also checked to save the
project configuration to be able to reload the project when HelloWorld.java is opened after
newly starting the editor. Then it is tried to compile the program. However, the console
panel that opened shows an error:

<img src="images/compile.png" width="600"/>


After correcting the code a new compilation generates the class file hello\HelloWorld.class in
the 'bin' directory and the program can be run:

<img src="images/run.png" width="720"/>

<hr>
Some more detailed info is found at the
<a href="https://eadgyth.github.io/Programming-Editor/help/help.html">help site</a> which also
accessible from the '?' menu.

