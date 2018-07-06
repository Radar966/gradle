## New and noteworthy

Here are the new features introduced in this Gradle release.

<!--
IMPORTANT: if this is a patch release, ensure that a prominent link is included in the foreword to all releases of the same minor stream.
Add-->

<!--
### Example new and noteworthy
-->

### Use SNAPSHOT plugin versions with the `plugins {}` block

Starting with this release, it is now possible to use SNAPSHOT plugin versions in the `plugins {}` and `pluginManagement {}` blocks.

### Periodic cache cleanup

Caching has always been one of the strong suits of Gradle. Over time, more and more persistent caches have been added to improve performance and support new features, requiring more and more disk space on build servers and developer workstations. Gradle now addresses one of the most highly voted issues on GitHub and introduces the following cleanup strategies:

- Version-specific cache directories in `GRADLE_USER_HOME/caches/<gradle-version>/` are checked periodically (at most every 24 hours) for whether they are still in use. If not, directories for release versions are deleted after 30 days of inactivity, snapshot versions after 7 days of inactivity. Moreover, the corresponding Gradle distributions at `GRADLE_USER_HOME/wrapper/dists/gradle-<gradle-version>-{bin,all}/` are deleted as well, if present.
- Shared versioned cache directories in `GRADLE_USER_HOME/caches/` (e.g. `jars-*`) are checked periodically (at most every 24 hours) for whether they are still in use. If there's no Gradle version that still uses them, they are deleted.
- Files in shared caches used by the current Gradle version in `GRADLE_USER_HOME/caches/` (e.g. `jars-3` or `modules-2`) are checked periodically (at most every 24 hours) for when they were last accessed. Depending on whether the file can be recreated locally or would have to be downloaded from a remote repository again, it will be deleted after 7 or 30 days of not being accessed, respectively.


## Promoted features

Promoted features are features that were incubating in previous versions of Gradle but are now supported and subject to backwards compatibility.
See the User guide section on the “[Feature Lifecycle](userguide/feature_lifecycle.html)” for more information.

The following are the features that have been promoted in this Gradle release.

<!--
### Example promoted
-->

### Tooling API types and methods

Many types and methods that were previously marked `@Incubating` are now considered stable. 

## Fixed issues

## Deprecations

Features that have become superseded or irrelevant due to the natural evolution of Gradle become *deprecated*, and scheduled to be removed
in the next major Gradle version (Gradle 5.0). See the User guide section on the “[Feature Lifecycle](userguide/feature_lifecycle.html)” for more information.

The following are the newly deprecated items in this Gradle release. If you have concerns about a deprecation, please raise it via the [Gradle Forums](https://discuss.gradle.org).

### Creating instances of JavaPluginConvention

Instances of this class are intended to be created only by the `java-base` plugin and should not be created directly. Creating instances using the constructor of `JavaPluginConvention` will become an error in Gradle 5.0. The class itself is not deprecated and it is still be possible to use the instances created by the `java-base` plugin.

## Potential breaking changes

### Kotlin DSL breakages

- `project.java.sourceSets` is now `project.sourceSets`

## External contributions


We would like to thank the following community members for making contributions to this release of Gradle.

<!--
 - [Some person](https://github.com/some-person) - fixed some issue (gradle/gradle#1234)
-->

We love getting contributions from the Gradle community. For information on contributing, please see [gradle.org/contribute](https://gradle.org/contribute).

- [Björn Kautler](https://github.com/Vampire) - Update Spock version in docs and build init (gradle/gradle#5627)
- [Kyle Moore](https://github.com/DPUkyle) - Use latest Gosu plugin 0.3.10 (gradle/gradle#5855)
- [Mata Saru](https://github.com/matasaru) - Add missing verb into docs (gradle/gradle#5694)
- [Sébastien Deleuze](https://github.com/sdeleuze) - Add support for SNAPSHOT plugin versions in the `plugins {}` block (gradle/gradle#5762)
- [Ben McCann](https://github.com/benmccann) - Decouple Play and Twirl versions (gradle/gradle#2062)

## Known issues

Known issues are problems that were discovered post release that are directly related to changes made in this release.
