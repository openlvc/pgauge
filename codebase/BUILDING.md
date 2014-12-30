When trying to compile PGauge, you will need to do the following:

Edit build.properties appropriately to set the path to where your RTI is located.
It's pretty straightfoward to edit, there are enteries for each platform based on
my settings, but you'll have to change them to fit your environment. Rembmer to
change the values if you update Portico!

For C++ Win32 Only: Remember, you'll need your path set up properly for Visual Studio.
You could try and build from the Visual Studio commonad line, or you could just
call the appropriate vcvars32.bat file that is located somewhere inside your VS
installation directory. HOWEVER, if you installed VS into the default location, I have
included helper scripts in the "resources\win32" directory. Just run the one appropriate
for your VS version and it should take care of setting things up for you.
