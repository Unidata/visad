# visad-mcidas-slim

This is a fork of the [visad library](https://github.com/visad/visad) for use with the `cdm-mcidas` artifact from the netCDF-Java project.
The intent is to produce a build-tool consumable (i.e. maven artifact) slim dependency of the `edu.wisc.ssec.mcidas` package that contains what is needed for `the ucar.nc2.iosp.mcidas.AreaServiceProvider` and `ucar.nc2.iosp.mcidas.McIDASGridServiceProvider` IOSPs.
No new development will be done in this repository.
Any fixes will be made upstream and what is here will solely be a reflection, with very minor changes, of the mainline `edu.wisc.ssec.mcidas` classes.
These classes do not rely on any third party dependencies.

The following class was removed from the `edu.wisc.ssec.mcidas` package:
* `GridDirectoryList.java`

The following classes from the `edu.wisc.ssec.mcidas` package were modified:
* `AreaDirectoryList.java`
  Removed check for ADDE server from `readDirectory()` method to remove dependency on the `edu.wisc.ssec.mcidas.adde` package
* `AreaFile.java`
  Removed code related to GetAreaGUI in `AreaFile(String source)` constructor to remove dependency on the `edu.wisc.ssec.mcidas.adde` package
  Removed `@Link` annotation from `AreaFile(URL url)`  constructor javadoc
* `AreaFileFactory.java`
  Removed all throws and catches associated with `AddeURLException` to remove dependency on the `edu.wisc.ssec.mcidas.adde` package
* `AREAnav.java`
   The full VisAD jar is needed to read Area files that use the Radar (RECT) type nav.
   The `makeAreaNav` method was modified to try to use `edu.wisc.ssec.mcidas.LALOnav` via reflection for cases where the full visad jar is available.

This library is made available on the Unidata nexus artifacts server (`https://artifacts.unidata.ucar.edu/repository/unidata-releases/`) as a Maven artifact with the following coordinates:

~~~
groupId: edu.wisc.ssec
artifactId: visad-mcidas-slim
~~~

To avoid namespace clashes, a second version of this artifact is published.
The new artifact is the same as `visad-mcidas-slim`, but the `edu.wisc.ssec.mcidas` package is relocated to `ucar.mcidas`.

~~~
groupId: edu.wisc.ssec
artifactId: visad-mcidas-slim-ucar-ns
~~~

As `visad` itself does not appear to be versioned, this artifact is versioned using the calendar date associated with the build of the artifact and has the form `yyyyMMdd`.

As the case with `visad`, this library is released under the terms of the GNU Library General Public License, Version 2, June 1991.

## Release History

* Release 20200507 :  Based on visad commit [b01355c](https://github.com/visad/visad/commit/b01355c650768ce6459d271df82fd88588c22ead)
* Release 20200507-2 :  Same as release 20200507, but includes a new artifact (`visad-mcidas-slim-ucar-ns`) with the `edu.wisc.ssec.mcidas` package relocated to `ucar.mcidas`.
* Release 20231121 : Fixes leaked resource bug
