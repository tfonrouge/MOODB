package tech.fonrouge.daemon;

import tech.fonrouge.daemon.watch.WatchXmlFiles;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new WatchXmlFiles().processEvents();
    }
}
