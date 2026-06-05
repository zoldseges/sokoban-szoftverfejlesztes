package io.github.zoldseges.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.zoldseges.sokoban.core.Grid;
import io.github.zoldseges.sokoban.core.Level;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class LevelLibraryDao {
    private LevelLibraryDao() {}

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    //TODO: I'd prefer to store String xsb instead of a list,
    //      but storing endline symbol might cause problems in portability
    //NOTE: List<String> might be more future-proof
    record StoredLevel(String name, List<String> gridStringList) {};
    record LevelLibraryFile(List<StoredLevel> levels) {};

    //TODO: anti-feature: invalid json level entries are just silently skipped.
    public static List<LevelLibraryEntry> load(Path path) throws IOException {
        LevelLibraryFile mappedLevelLibrary = OBJECT_MAPPER.readValue(path.toFile(), LevelLibraryFile.class);
        List<LevelLibraryEntry> result = new ArrayList<>();
        if (mappedLevelLibrary.levels() != null) {
            for (StoredLevel storedLevel : mappedLevelLibrary.levels()) {
                String xsbString = String.join("\n", storedLevel.gridStringList());
                if (Xsb.gridFrom(xsbString) instanceof Grid.Result.Ok okGridResult) {
                    if (Level.from(okGridResult.grid()) instanceof Level.Result.Ok okLevelResult) {
                        LevelLibraryEntry entry = new LevelLibraryEntry(
                                storedLevel.name(), okLevelResult.level()
                        );
                        result.add(entry);
                    }
                }
            }
        }
        return result;
    }

    public static void save(Path path, List<LevelLibraryEntry> entries) throws IOException {
        List<StoredLevel> storedLevels = new ArrayList<>();
        for (LevelLibraryEntry entry : entries) {
            storedLevels.add(new StoredLevel(
                entry.name(), Xsb.strListFrom(entry.level().copyGrid()))
            );
        }
        Path parentPath = path.toAbsolutePath().getParent();
        //NOTE: path can be root, so parent is null
        if (parentPath != null) Files.createDirectories(parentPath);
        OBJECT_MAPPER.writer().writeValue(path.toFile(), new LevelLibraryFile(storedLevels));
    }
}
