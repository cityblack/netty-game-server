package com.lzh.game.framework.hotswap;

import com.lzh.game.framework.hotswap.handler.AbstractFixHandler;
import com.lzh.game.framework.hotswap.handler.FixHandler;
import jakarta.annotation.Resource;
import javassist.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;

/**
 * @author zehong.l
 * @since 2024-08-05 15:07
 **/
@SpringBootTest(classes = AppTest.class)
@SpringBootApplication
class HotSwapServiceTest {

    @Resource
    private HotSwapService hotSwapService;

    @Test
    void execute() throws NotFoundException, IOException, CannotCompileException {
        var target = this.getClass().getClassLoader().getResource("").getPath() + "hot_swap";
        createFile(target);
        hotSwapService.execute(new String[]{target});
    }

    void createFile(String target) throws IOException, NotFoundException, CannotCompileException {
        var path = Paths.get(target);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        var pool = ClassPool.getDefault();
        var calendar = Calendar.getInstance();
        var year = calendar.get(Calendar.YEAR);
        var month = calendar.get(Calendar.MONTH);
        var day = calendar.get(Calendar.DAY_OF_MONTH);
        var hour = calendar.get(Calendar.HOUR_OF_DAY);
        var name = "HotSwapTest" + year + month + day + hour;
        var cc = pool.makeClass(name);
        cc.setInterfaces(new CtClass[]{pool.getCtClass(FixHandler.class.getName())});
        cc.setSuperclass(pool.getCtClass(AbstractFixHandler.class.getName()));
        CtMethod method = CtNewMethod.make("public void doFix() { System.out.println(\"hello world!\"); }", cc);
        cc.addMethod(method);

        CtMethod idMethod = CtMethod.make("public String getId() { return \"" + name + "\"; }", cc);
        cc.addMethod(idMethod);
        cc.writeFile(target);
    }
}