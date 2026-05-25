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

## configuration

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
