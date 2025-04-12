package com.utime.burrowNest.common.util;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FileWatcher implements Runnable {

    private final Path watchDir;
    private final WatchService watcher;
    private volatile boolean running = true;

    public FileWatcher(Path dir) throws IOException {
        this.watchDir = dir;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.watchDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        System.out.println("Watching directory: " + watchDir);
    }

    @Override
    public void run() {
        while (running) {
            WatchKey key;
            try {
                key = watcher.poll(1, TimeUnit.SECONDS);
                
                if (key != null) {
                    List<WatchEvent<?>> events = key.pollEvents();
                    for (WatchEvent<?> event : events) {
                        WatchEvent.Kind<?> kind = event.kind();

                        // Context for directory entry event is the file name
                        @SuppressWarnings("unchecked")
						Path name = ((WatchEvent<Path>) event).context();
                        Path child = watchDir.resolve(name);

                        System.out.printf("%s: %s\n", event.kind().name(), child);

                        // Handle file events based on their kind
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            System.out.println("File created: " + child);
                            // TODO: 파일 생성에 따른 DB 동기화 로직
                        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                            System.out.println("File modified: " + child);
                            // TODO: 파일 수정에 따른 DB 동기화 로직
                        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                            System.out.println("File deleted: " + child);
                            // TODO: 파일 삭제에 따른 DB 동기화 로직
                        }
                    }

                    // Reset the key -- this step is crucial if you want to receive further events.
                    boolean valid = key.reset();
                    if (!valid) {
                        System.out.println("WatchKey is no longer valid.");
                        break;
                    }
                }
            } catch (ClosedWatchServiceException e) {
                System.out.println("WatchService has been closed.");
                break;
            } catch (InterruptedException e) {
	            System.out.println("WatchService has been closed.");
	            break;
	        }
        }
        System.out.println("File watcher for " + watchDir + " stopped.");
    }

    public void stop() {
        running = false;
        try {
            watcher.close(); // Release resources
        } catch (IOException e) {
            System.err.println("Error closing WatchService: " + e.getMessage());
        }
    }

//    public static void main(String[] args) throws IOException, InterruptedException {
//
//        Path directoryToWatch = Paths.get("F:\\WorkData\\Burrow");
//        if (!Files.isDirectory(directoryToWatch)) {
//            System.err.println("Error: " + directoryToWatch + " is not a directory.");
//            return;
//        }
//
//        FileWatcher watcher = new FileWatcher(directoryToWatch);
//        Thread watcherThread = new Thread(watcher);
//        watcherThread.start();
//
//        // Simulate program running for some time
//        Thread.sleep(60000);
//
//        // Stop the watcher
//        watcher.stop();
//        watcherThread.join();
//
//        System.out.println("Program finished.");
//    }
}