package com.lzh.game.framework.hotswap.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * @author zehong.l
 * @since 2024-08-02 14:50
 **/
public class HotSwapAgent {

    public static void agentmain(String args, Instrumentation instrumentation) {
        try {
            Class.forName("com.lzh.game.framework.hotswap.agent.HotSwapBean");
            byte[] date = HotSwapBean.getInstance().updateData;
            var classDate = HotSwapUtils.byteToClassInfo(date);
            var defined = new ClassDefinition[classDate.size()];
            int i = 0;
            for (Map.Entry<String, byte[]> entry : classDate.entrySet()) {
                defined[i++] = new ClassDefinition(Class.forName(entry.getKey()), entry.getValue());
            }
            instrumentation.redefineClasses(defined);
            HotSwapBean.getInstance().updateData = null;
        } catch (Exception e) {
            HotSwapBean.getInstance().error = e;
        }
    }

    public static File createAgentLib(String path) throws IOException {
        File jar = new File(path);
        if (jar.exists()) {
            return jar;
        }
        checkParent(jar);
        var manifest = new Manifest();
        var attrs = manifest.getMainAttributes();
        attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attrs.put(new Attributes.Name("Agent-Class"), HotSwapAgent.class.getName());
        attrs.put(new Attributes.Name("Can-Redefine-Classes"), "true");
        attrs.put(new Attributes.Name("Can-Retransform-Classes"), "true");
        try (var output = new JarOutputStream(new FileOutputStream(jar), manifest)) {
            addClassToJar(output, HotSwapAgent.class);
            addClassToJar(output, HotSwapUtils.class);
            output.closeEntry();
        }
        return jar;
    }

    private static void addClassToJar(JarOutputStream outputStream, Class<?> clz) throws IOException {
        var name = clz.getSimpleName();
        var path = clz.getResource(".").getPath() + name + ".class";
        try (var input = new FileInputStream(path)) {
            var bytes = input.readAllBytes();
            var entry = new JarEntry(name + ".class");
            outputStream.putNextEntry(entry);
            outputStream.write(bytes);
            outputStream.flush();
        }
    }

    private static void checkParent(File file) throws IOException {
        var parent = Paths.get(file.getParent());
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

}
