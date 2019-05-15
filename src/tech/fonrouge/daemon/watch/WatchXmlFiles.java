package tech.fonrouge.daemon.watch;

import com.sun.nio.file.SensitivityWatchEventModifier;
import tech.fonrouge.daemon.build.BuildMOODB;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class WatchXmlFiles {
    private static int counter = 0;
    private final WatchService watchService;
    private final Map<WatchKey, Path> watchKeyPathMap;
    private final PathMatcher matcher;
    private boolean trace;
    private BuildMOODB buildMOODB;

    public WatchXmlFiles() throws IOException {

        Path dir = Paths.get(".");
        watchService = FileSystems.getDefault().newWatchService();
        matcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.xml");
        watchKeyPathMap = new HashMap<>();
        buildMOODB = new BuildMOODB();

        System.out.format("Scanning %s ...\n", dir);
        registerAll(dir);
        System.out.println("Done.");
        trace = true;
    }

    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY}, SensitivityWatchEventModifier.HIGH);
        //WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);
        if (trace) {
            Path prev = watchKeyPathMap.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s", prev, dir);
                }
            }
        }
        watchKeyPathMap.put(key, dir);
    }

    private void registerAll(Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void processEvents() {
        for (; ; ) {

            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                return;
            }

            Path dir = watchKeyPathMap.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized.");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                if (matcher.matches(child.getFileName())) {
                    System.out.format("%d %s: %s\n", ++counter, event.kind().name(), child);
                    buildMOODB.buildClasses(child);
                }

                if (kind == ENTRY_CREATE) {
                    if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                        try {
                            registerAll(child);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                watchKeyPathMap.remove(key);
                if (watchKeyPathMap.isEmpty()) {
                    break;
                }
            }
        }

    }
}
