package io.github.zoldseges;

import io.github.zoldseges.persistence.LevelLibraryDao;
import io.github.zoldseges.persistence.LevelLibraryEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

//NOTE: acts sort of like a model, so shouldn't live either in persistence nor in controller package.
public class LevelLibrary {
    private final ObservableList<LevelLibraryEntry> entries;
    private final Path path;

    public LevelLibrary(Path path) {
        this.path = path;
        this.entries = FXCollections.observableArrayList();
    }

    public ObservableList<LevelLibraryEntry> getEntries() {
        return this.entries;
    }

    public Path getPath() {
        return this.path;
    }

    private void saveState() throws IOException {
        LevelLibraryDao.save(this.path, this.entries);
    }

    public void loadAndSetEntries() throws IOException {
        if (Files.exists(this.path)) {
            this.entries.setAll(LevelLibraryDao.load(this.path));
        }
    }

    public void addEntry(LevelLibraryEntry entry) throws IOException {
        this.entries.add(entry);
        try {
            this.saveState();
        } catch (IOException e) {
            this.entries.remove(entry);
            throw e;
        }
    }

    public void removeEntry(LevelLibraryEntry entry) throws IOException {
        int index = entries.indexOf(entry);
        if (index > 0) {
            entries.remove(index);
            try {
                this.saveState();
            } catch (IOException e) {
                // removed entry, but couldn't save state. we need to add back to stay in sync.
                entries.add(index, entry);
                throw e;
            }
        }
    }
}
