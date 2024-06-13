package com.lzh.game.framework.logs;

import com.lzh.game.framework.logs.anno.LogFacade;
import com.lzh.game.framework.utils.ClassScannerUtils;
import javassist.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zehong.l
 * @since 2023-05-31 15:42
 **/
@Slf4j
public class LogHandler {

    private static final String LOG_PACK_NAME = LoggerUtils.class.getPackageName();
    private static final String LOG_BUILD_CLASS = LOG_PACK_NAME + ".LoggerUtils.LogBuild";

    private static final Map<Class<?>, Object> INVOKE_OBJ = new HashMap<>();

    private static final DefaultParameterNameDiscoverer DISCOVERER = new DefaultParameterNameDiscoverer();

    private static final Function<Class<?>, String> BUILD_NEW_CLASS_NAME = (clazz) -> clazz.getName() + "impl$$$";

    public static void init(String scanPath) {
        log.info("{}. Loading log config..", scanPath);
        long startTime = System.currentTimeMillis();
        var list = ClassScannerUtils.scanPackage(scanPath, e -> e.isAnnotationPresent(LogFacade.class));
        for (Class<?> clazz : list) {
            registerLog(clazz);
        }
        log.info("Loaded log config. time:{}", System.currentTimeMillis() - startTime);
    }

    public static void init() {
        LogHandler.init("com.lzh.game");
    }

    public static <T> T getLog(Class<T> clazz) {
        Object invoke = INVOKE_OBJ.get(clazz);
        if (Objects.isNull(invoke)) {
            throw new RuntimeException("No register the " + clazz.getSimpleName() + " log.");
        }
        return (T) invoke;
    }

    public static void registerLog(Class<?> logClass) {
        if (!logClass.isInterface()) {
            throw new RuntimeException("Log handler is not interface");
        }
        try {
            Object object = buildObject(logClass);
            INVOKE_OBJ.put(logClass, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object buildObject(Class<?> clazz) throws NotFoundException, CannotCompileException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> build = buildClass(clazz);
        return build.getConstructor().newInstance();
    }

    private static Class<?> buildClass(Class<?> clazz) throws NotFoundException, CannotCompileException, ClassNotFoundException {

        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(LogHandler.class));
        String className = BUILD_NEW_CLASS_NAME.apply(clazz);
        CtClass ctClass = pool.makeClass(className);

        CtClass interfaceClass = pool.getCtClass(clazz.getName());
        ctClass.addInterface(interfaceClass);

//        CtMethod[] methods = interfaceClass.getDeclaredMethods();
        Set<String> methodNames = new HashSet<>();
        for (Method sourceMethod : clazz.getDeclaredMethods()) {
            String methodName = sourceMethod.getName();
            if (methodNames.contains(methodName)) {
                throw new RuntimeException("Log method %s not unique.".formatted(methodName));
            }
            CtMethod method = pool.getMethod(clazz.getName(), methodName);
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (!Objects.equals("void", method.getReturnType().getName())) {
                throw new RuntimeException("Log method return type is not void.");
            }
            if (!method.hasAnnotation(LogMethod.class)) {
                throw new RuntimeException("Log method not has @LogMethod.");
            }
            if (method.getParameterTypes().length == 0) {
                throw new RuntimeException("Log method not has params.");
            }
            methodNames.add(method.getName());
            String[] paramNames = parseParamNames(sourceMethod);
            String fieldName = methodName + "$$Param_";
            String fieldBody = "private static String[] %s = new String[]{%s};".formatted(fieldName, Stream.of(paramNames).collect(Collectors.joining("\",\"", "\"", "\"")));
            CtField field = CtField.make(fieldBody, ctClass);
            ctClass.addField(field);
            CtMethod cm = new CtMethod(method.getReturnType(), methodName, method.getParameterTypes(), ctClass);
            cm.setBody(buildMethodBody(fieldName, method));

            ctClass.addMethod(cm);
        }
        return ctClass.toClass();
    }

    private static String buildMethodBody(String fieldName, CtMethod method) throws ClassNotFoundException, NotFoundException, CannotCompileException {
        CtClass[] paramTypes = method.getParameterTypes();
        int paramLen = paramTypes.length;

        LogMethod logMethod = (LogMethod) method.getAnnotation(LogMethod.class);

        StringBuilder paramBuild = new StringBuilder("new Object[]{");
        for (int i = 0; i < paramLen; i++) {
            paramBuild.append(paramConvert(i + 1, paramTypes[i]));
            if (i != paramLen - 1) {
                paramBuild.append(",");
            }
        }
        paramBuild.append("}");
//        LoggerUtils.LogBuild.class.getPackageName()
        String methodBody = """
                {
                 %s build =
                 %s.LoggerUtils.of("%s", %d).addParam(%s, %s);
                """.formatted(LOG_BUILD_CLASS, LOG_PACK_NAME, logMethod.logFile(), logMethod.logReason(), fieldName, paramBuild.toString());

        methodBody += " build.log();";
        return methodBody + "}";
    }

    private static String paramConvert(int index, CtClass type) throws CannotCompileException {
        String clazzName = type.getName();
        String param = "$" + index;
        if (Objects.equals("int", clazzName)) {
            return "Integer.valueOf(%s)".formatted(param);
        } else if (Objects.equals("float", clazzName)) {
            return "Float.valueOf(%s)".formatted(param);
        } else if (Objects.equals("double", clazzName)) {
            return "Double.valueOf(%s)".formatted(param);
        } else if (Objects.equals("long", clazzName)) {
            return "Long.valueOf(%s)".formatted(param);
        } else if (Objects.equals("char", clazzName)) {
            return "String.valueOf(%s)".formatted(param);
        } else if (Objects.equals("boolean", clazzName)) {
            return "Boolean.valueOf(%s)".formatted(param);
        }
        return param;
    }

    private static String[] parseParamNames(Method method) {
        return DISCOVERER.getParameterNames(method);
    }


    public static void main(String[] args) throws NoSuchMethodException {

//        LogHandler.init();
//        LogHandler.getLog(LogTest.class).logTest(2444);
//        LoggerUtils.of(LogFile.AGREEMENT, LogReason.CONSOLE).addParam(new String[]{"xx"}, 30).log();
    }
}
