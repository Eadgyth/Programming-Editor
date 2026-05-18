<h3>Eadgyth Programming Editor</h3>
<p>
A text and code editor written in Java. It features an easy project setup to compile
and run code from within the editor with support for Java, C#, Python and R.
The editor is suited for learning, quick prototyping, or programming projects
for personal use.
<p>
The editor can be tried out by downloading the executable jar file in a
<a href="https://github.com/Eadgyth/Programming-Editor/releases">release</a>.
It is run with:

```
java -jar Eadgyth.jar
```

<p>
The progam can be built in Netbeans or Eclipse (or with a build of Eadgyth itself)
using the src directory in the repo. The name of the main file is eg.Eadgyth.java.

<h4>REQUIREMENTS</h4>
<ul>
<li>JDK 8 or higher to run and build the editor.</li>
<li>To compile Java code by the built-in compile function, the editor must be
run with the JRE contained in a JDK (not an issue with Java 11+).</li>
<li>As of v1.2.1, the editor requires UTF-8 encoding of source files to ensure
reliable output when running code (an option to convert files to UTF-8 is
available). Previous versions assumed the system or JVM default encoding.</li>
<li>:information_source: The executable paths for Python, R, and .NET must be
included in your system PATH. To compile and run C# code, the editor internally
calls the dotnet command, which requires an .NET SDK to be installed. A minimalistic
.csproj file is generated upon compilation.</li>
</ul>

<h4>LIMITATIONS</h4>
<ul>
<li>Syntax highlighting is simple (keywords, sigils, tags, string literals, line
and block comments). While generally performant, opening or closing multiline string
literals may cause temporary delays. At the code level, the syntax highlighting is
not meant as an example of how it should be done.</li>
<li>The built-in console may appear frozen when a process buffers its output. This
potentially affects custom commands. This issue can often be resolved by enabling
auto-flushing via a command-line switch or script command if the language supports
it.</li>
</ul>
<h4>CREDITS & ACKNOWLEDGEMENTS</h4>
<ul>
<li>William Gilreath for his advicea and comments</li>
<li>The program uses icons from
<a href="https://github.com/Distrotech/tango-icon-theme">Tango Desktop Project</a>.</li>
<li>The dark-blue background uses the
<a href="https://github.com/dracula/dracula-theme">Dracula Color Palette</a> and 
other the dark backgrounds have learned from it.</li>
</ul>
<h4>LICENSE</h4>
<p>
MIT, see LICENSE<br>
<p>
<h4>SCREENSHOT</h4>
<p>
<img src="docs/images/DarkBlueBackground.png" width="600"/><br><br>
