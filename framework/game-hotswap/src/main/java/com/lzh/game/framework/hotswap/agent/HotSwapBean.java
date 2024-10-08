package com.lzh.game.framework.hotswap.agent;

import com.sun.tools.attach.VirtualMachine;
import javassist.CannotCompileException;
import javassist.bytecode.ClassFile;
import javassist.util.proxy.DefineClassHelper;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author zehong.l
 * @since 2024-08-02 18:20
 **/
@Slf4j
public class HotSwapBean {

    public volatile byte[] updateData;

    public volatile Throwable error;

    private final Map<String, ClassFileInfo> CLASS_INFO = new HashMap<>();

    public List<ClassFileInfo> swap(String[] hotDir, String agentLib) {
        try {
            var files = getSwapFiles(hotDir);
            if (files.isEmpty()) {
                log.warn("{} not have swap file", Arrays.toString(hotDir));
                return Collections.emptyList();
            }
            var able = parseSwapFiles(files);
            if (able.isEmpty()) {
                log.warn("not have changed file.");
                return Collections.emptyList();
            }
            loadNewClass(able);
            var list = new ArrayList<>(able.values());
            updateData = HotSwapUtils.classToBytes(list);
            starVM(agentLib);
            if (Objects.nonNull(error)) {
                log.error("Hot swap fail. ", error);
                return Collections.emptyList();
            }
            CLASS_INFO.putAll(able);
            return list;
        } catch (Exception e) {
            log.error("Hot swap fail. ", e);
            throw new RuntimeException(e);
        }
    }

    private void loadNewClass(Map<String, ClassFileInfo> files) throws CannotCompileException {
        var loader = Thread.currentThread().getContextClassLoader();
        for (Map.Entry<String, ClassFileInfo> entry : files.entrySet()) {
            String className = entry.getKey();
            try {
                loader.loadClass(className);
            } catch (Exception e) {
                DefineClassHelper.toClass(className, null, loader, null, entry.getValue().getData());
            }
        }
    }

    private Map<String, ClassFileInfo> parseSwapFiles(Map<String, Long> files) {
        var result = new HashMap<String, ClassFileInfo>();
        for (Map.Entry<String, Long> entry : files.entrySet()) {
            checkFile(entry.getKey(), entry.getValue(), result);
        }
        return result;
    }

    private void checkFile(String filePath, long lastModifyTime, Map<String, ClassFileInfo> contain) {
        try {
            log.info("file [{}] start checking", filePath);
            var path = Paths.get(filePath);
            var bytes = Files.readAllBytes(path);
            var className = getClassName(bytes);
            var newInfo = new ClassFileInfo(filePath, className, bytes, lastModifyTime);
            var old = CLASS_INFO.get(className);
            if (Objects.nonNull(old)) {
                if (old.getLastModifyTime() == newInfo.getLastModifyTime()
                        || Objects.equals(old.getMd5(), newInfo.getMd5())) {
                    log.info("Ignore [{}] update. cause one is not change", className);
                    return;
                }
            }
            contain.put(className, newInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getClassName(byte[] bytes) throws IOException {
        var clz = new ClassFile(new DataInputStream(new ByteArrayInputStream(bytes)));
        return clz.getName();
    }

    private Map<String, Long> getSwapFiles(String[] dirs) throws IOException {
        var files = new HashMap<String, Long>();
        for (String dir : dirs) {
            var path = Paths.get(dir);
            if (!Files.isDirectory(path)) {
                throw new RuntimeException(dir + ". Hot swap path is not dir.");
            }
            try (var paths = Files.walk(path)) {
                paths.filter(e -> !Files.isDirectory(e))
                        .map(Path::toFile)
                        .filter(e -> e.getName().endsWith(".class"))
                        .forEach(e -> files.put(e.getAbsolutePath(), e.lastModified()));
            }
        }
        return files;
    }

    private void starVM(String lib) {
        try {
            var path = libAbsolutePath(lib);
            var pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            var vm = VirtualMachine.attach(pid);
            vm.loadAgent(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String libAbsolutePath(String lib) throws IOException {
        var path = Paths.get(lib);
        if (!Files.exists(path)) {
            log.info("Agent lib not exists. Regeneration lib file to: {}", lib);
            File file = HotSwapAgent.createAgentLib(lib);
            return file.getAbsolutePath();
        }
        return path.toFile().getAbsolutePath();
    }

    public static HotSwapBean getInstance() {
        return Instance.INSTANCE;
    }

    public static class Instance {
        public static final HotSwapBean INSTANCE = new HotSwapBean();
    }

    private HotSwapBean() {
    }
}
