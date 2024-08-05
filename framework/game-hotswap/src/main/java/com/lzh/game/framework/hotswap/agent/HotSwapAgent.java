package com.lzh.game.framework.hotswap.agent;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * @author zehong.l
 * @since 2024-08-02 14:50
 **/
public class HotSwapAgent implements ApplicationContextAware {

    public static void agentmain(String args, Instrumentation instrumentation) {
        try {
            Class.forName("com.lzh.game.framework.hotswap.HotSwapBean");
            System.out.println("run??");

        } catch (Exception e) {
            throw new RuntimeException(e);
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
