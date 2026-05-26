# initialization

```
mvn archetype:generate
    -DarchetypeGroupId=org.openjfx
    -DarchetypeArtifactId=javafx-archetype-fxml
    -DarchetypeVersion=0.0.6
    -DgroupId=io.github.zoldseges
    -DartifactId=sokoban
    -Dversion=0.0.1
    -Djavafx-version=25
```
`-Djavafx-version=25` seems to be the latest LTS

## mvn configuration

Running the command will output this:
```
Confirm properties configuration:
javafx-version: 25
javafx-maven-plugin-version: 0.0.6
add-debug-configuration: N
groupId: io.github.zoldseges
artifactId: sokoban
version: 0.0.1
package: io.github.zoldseges
```
`add-debug-configuration: N`: It seems the IDE should handle debugging correctly, no need to set it, probably.

## idea configuration

handling the following warning
```
WARNING: A restricted method in java.lang.System has been called
WARNING: java.lang.System::load has been called by com.sun.glass.utils.NativeLibLoader in module javafx.graphics (file:/home/adam/.m2/repository/org/openjfx/javafx-graphics/25/javafx-graphics-25-linux.jar)
WARNING: Use --enable-native-access=javafx.graphics to avoid a warning for callers in this module
WARNING: Restricted methods will be blocked in a future release unless native access is enabled
```
1. add `--enable-native-access=javafx.graphics` VM option
   1. `shift-shift`
   2. `Edit Configurations (for 'App' under 'Applications')`
   3. `'Alt-v' (modify options -> add VM-options)`
   4. `add the option '--enable-native-access=javafx.graphics'`
2. tick `Store as project file` in the top right to make it tracked by VCS
3. run the app, so it will be set under `.idea` actually
