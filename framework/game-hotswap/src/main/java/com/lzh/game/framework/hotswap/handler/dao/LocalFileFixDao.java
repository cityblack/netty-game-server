package com.lzh.game.framework.hotswap.handler.dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author zehong.l
 * @since 2024-10-08 14:44
 **/
public class LocalFileFixDao implements FixDao {

    private static final String END = ".hst";

    private String path;

    public LocalFileFixDao(String path) {
        init(path);
    }

    private void init(String path) {
        var p = Paths.get(path);
        if (!Files.exists(p)) {
            try {
                Files.createDirectories(p);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.path = p.toFile().getAbsolutePath();
    }

    @Override
    public List<String> loadAllUsedId() {
        try (Stream<Path> files = Files.walk(Path.of(path))) {
            return files.filter(e -> !Files.isDirectory(e))
                    .map(Path::toFile)
                    .map(File::getName)
                    .map(String::strip)
                    .filter(e -> e.endsWith(END))
                    .map(e -> e.substring(11, e.length() - END.length()))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveFix(String id) {
        try {
            var time = createTime();
            var target = path + File.separatorChar + time + "_" + id + END;
            Files.createFile(Paths.get(target));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // YYYY MM DD HH -> len: 10
    private String createTime() {
        var calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH), day = calendar.get(Calendar.DAY_OF_MONTH), hour = calendar.get(Calendar.HOUR_OF_DAY);
        return year + tenZero(month) + tenZero(day) + tenZero(hour);
    }

    private static String tenZero(int value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }
}
