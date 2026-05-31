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

# Design

## Sokoban package

1. Cell enum - the atoms so-to-speak.
2. Grid - the mutable representation "of the level"
3. Level - the immutable representation "of the level", should be guaranteed to be a valid level
4. Game - initializes from a level, keeps track of gamestate changes to himself

## Cell

Initially I tried to implement it as bytes with bitmasking operations but, but ran into some issues.
The second best option seems to be to make it an enum, although it comes with some pain as well.
(see `Cell.withDynamicElement` method)

### Why result-based error handling instead of exceptions?:

When we started the design, we were thinking about we might want to have an editor at one point.
Since in a level editor the most frequent state:

1. Constant exception throwing feels off to me
2. Easy to batch `Level.Violations` together, consumer can read out multiple errors at once.

On the Grid object it might have been a good choice to use exceptions, but since we leaned in this direction already,
it feels like it would be more consistent to stay with the result-based approach here as well.

#### What are sealed interfaces?

The `sealed interface ... permits ...` construct lets us enforce that that _ALL_ implementations
stay at one place. Furthermore, switch expressions on the implementing classes are enforced to be _exhaustive_
by the compiler, so no errors should be able to pass by silently.

### Visitor pattern in `Grid`

`@FunctionalInterface`: It makes it possible for the `forEach(CellVisitor visitor)` method to accept lambda expressions.

## UI

This is the "controller" layer

### Rendering

[openjdk docs](https://docs.oracle.com/en/java/java-components/javafx/25/docs/javafx.graphics/javafx/scene/canvas/GraphicsContext.html#drawImage(javafx.scene.image.Image,double,double))
Key class: javafx/scene/canvas/GraphicsContext.java

#### Getting graphics context
```java
public class MainController {

    @FXML
    private Canvas gameCanvas;
    private GraphicsContext gctx;

    @FXML
    private void initialize() {
        gctx = gameCanvas.getGraphicsContext2D();
    }
}
```

Then you'll be able to draw through the graphicscontext `gctx`.

#### Blitting

```java
public void drawImage(Image img,
 double sx,
 double sy,
 double sw,
 double sh,
 double dx,
 double dy,
 double dw,
 double dh)
```
Draws the specified source rectangle of the given image to the given destination rectangle of the Canvas. A null image value or an image still in progress will be ignored.

This method will be affected by any of the global common or image attributes as specified in the Rendering Attributes Table.

Parameters:
    img - the image to be drawn or null.
    sx - the source rectangle's X coordinate position.
    sy - the source rectangle's Y coordinate position.
    sw - the source rectangle's width.
    sh - the source rectangle's height.
    dx - the destination rectangle's X coordinate position.
    dy - the destination rectangle's Y coordinate position.
    dw - the destination rectangle's width.
    dh - the destination rectangle's height. 


# Potential extensions
## additional Level check: 

Could be implemented with a border flood-fill over non-wall cells:
- if flood reaches Terrain.FLOOR or Terrain.GOAL -> the level is not enclosed
- can detect "VOID" cells (for prettier rendering)
(not sure how much I should enforce it though)
