A tool to trace execution of test case which written by Junit 4 annotations syntax and basic Junit 5 annotations syntax.

It bases on AspectJ and works with AspectJ weaver tools like aspectj-maven-plugin, aspectjweaver, ...

Notes: With compile or post-compile time weaving, it will not trace execution of external libraries' methods.

Output file is ${Project-folder}/logaspect.log