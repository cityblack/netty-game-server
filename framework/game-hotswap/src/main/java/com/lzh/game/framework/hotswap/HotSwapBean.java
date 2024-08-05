package com.lzh.game.framework.hotswap;

import com.lzh.game.framework.hotswap.agent.HotSwapAgent;
import com.sun.tools.attach.VirtualMachine;
import javassist.ClassPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.ClassInfo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zehong.l
 * @since 2024-08-02 18:20
 **/
@Slf4j
public class HotSwapBean {

    public volatile byte[] updateData;

    public volatile boolean update;

    private final Map<String, ClassFileInfo> classInfo = new HashMap<>();

    public synchronized List<ClassFileInfo> swap(String hotDir, String agentLib) throws Exception {

        var files = getSwapFiles(hotDir);
        if (files.isEmpty()) {
            log.warn("{} not have swap file", hotDir);
            return Collections.emptyList();
        }

        return null;
    }

    private static Map<String, ClassFileInfo> parseSwapFiles(Map<String, Long> files) {

    }

    private void checkFile(String filePath, long lastModifyTime, Map<String, ClassFileInfo> contain) {
        try {
            log.info("file [{}] start checking", filePath);
            var bytes = Files.readAllBytes(Paths.get(filePath));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readClassName(byte[] bytes) throws Exception {
        var input = new DataInputStream(new ByteArrayInputStream(bytes));
        
    }

    private Map<String, Long> getSwapFiles(String dir) throws IOException {
        var path = Paths.get(dir);
        if (!Files.isDirectory(path)) {
            throw new RuntimeException(dir + ". Hot swap path is not dir.");
        }
        var files = new HashMap<String, Long>();
        try (var paths = Files.walk(path)) {
            paths.filter(e -> !Files.isDirectory(e))
                    .map(Path::toFile)
                    .filter(e -> e.getName().endsWith(".class"))
                    .forEach(e -> files.put(e.getAbsolutePath(), e.lastModified()));
        }
        return files;
    }

    public void starVM(String lib) {
        try {
            File file = HotSwapAgent.createAgentLib(lib);
            var pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            var vm = VirtualMachine.attach(pid);
            vm.loadAgent(file.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HotSwapBean getInstance() {
        return Instance.INSTANCE;
    }

    public static class Instance {
        public static final HotSwapBean INSTANCE = new HotSwapBean();
    }
}
