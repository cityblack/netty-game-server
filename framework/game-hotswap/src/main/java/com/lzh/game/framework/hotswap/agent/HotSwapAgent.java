package com.lzh.game.framework.hotswap.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author zehong.l
 * @since 2024-08-02 14:50
 **/
public class HotSwapAgent {

    public static void agentmain(String args, Instrumentation instrumentation) {

    }

    public static File createAgentLib(String path) throws IOException {
        File jar = new File(path);
        checkParent(jar);
        return jar;
    }

    private static void checkParent(File file) throws IOException {
        var parent = Paths.get(file.getParent());
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    public static void main(String[] args) throws IOException {

//        Files.createFile(Paths.get("/Users/jsonp/Desktop/2/1.txt"));
        var file = new File("/Users/jsonp/Desktop/2/1.txt");
//        var parent = file.getParentFile().getAbsolutePath();
        var path = Paths.get(file.getParent());
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        Files.createFile(Paths.get("/Users/jsonp/Desktop/2/1.txt"));
    }
}
