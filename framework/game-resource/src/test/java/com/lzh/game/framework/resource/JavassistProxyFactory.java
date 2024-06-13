/*
 * Copyright (C) 2013, 2014 Brett Wooldridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzh.game.framework.resource;

import javassist.*;
import javassist.bytecode.ClassFile;

import java.lang.reflect.Array;
import java.util.*;


public final class JavassistProxyFactory
{
   private static ClassPool classPool;
   private static String genDirectory = "";

   public static void main(String... args) throws Exception {
      classPool = new ClassPool();
//      classPool.importPackage("java.sql");
      classPool.appendClassPath(new LoaderClassPath(JavassistProxyFactory.class.getClassLoader()));


      // Cast is not needed for these
      String methodBody = "{ Object value = storage.getOrThrow(key);return ((com.lzh.game.resource.StorageInstance)value).getValue(); }";
      generateProxyClass(StorageInstance.class, ConfigValueResource.class.getName(), methodBody);

   }

   /**
    *  Generate Javassist Proxy Classes
    */
   private static <T> void generateProxyClass(Class<T> primaryInterface, String superClassName, String methodBody) throws Exception
   {
      String newClassName = superClassName.replaceAll("(.+)\\.(\\w+)", "$1.Hikari$2");

      CtClass superCt = classPool.getCtClass(superClassName);
      CtClass targetCt = classPool.makeClass(newClassName, superCt);
      targetCt.setModifiers(Modifier.setPublic(Modifier.FINAL));

      System.out.println("Generating " + newClassName);


      CtClass storageType = classPool.getCtClass(Storage.class.getName());

      CtField storageField = new CtField(storageType, "storage", targetCt);
      targetCt.addField(storageField);

      CtClass keyClass = classPool.getCtClass(String.class.getName());
      CtField keyField = new CtField(keyClass, "key", targetCt);
      targetCt.addField(keyField);

      CtConstructor constructor = new CtConstructor(new CtClass[]{storageType,keyClass}, targetCt);
      String constructorBody = "{ this.storage = $1; this.key = $2; }";
      constructor.setBody(constructorBody);
      targetCt.addConstructor(constructor);

      // Make a set of method signatures we inherit implementation for, so we don't generate delegates for these
      Set<String> superSigs = new HashSet<>();
      for (CtMethod method : superCt.getMethods()) {
         if ((method.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
            superSigs.add(method.getName() + method.getSignature());
         }
      }

      Set<String> methods = new HashSet<>();
      for (Class<?> intf : getAllInterfaces(primaryInterface)) {
         CtClass intfCt = classPool.getCtClass(intf.getName());
         targetCt.addInterface(intfCt);
         for (CtMethod intfMethod : intfCt.getDeclaredMethods()) {
            final String signature = intfMethod.getName() + intfMethod.getSignature();

            // don't generate delegates for methods we override
            if (superSigs.contains(signature)) {
               continue;
            }

            // Ignore already added methods that come from other interfaces
            if (methods.contains(signature)) {
               continue;
            }

            // Track what methods we've added
            methods.add(signature);

            // Clone the method we want to inject into
            CtMethod method = CtNewMethod.copy(intfMethod, targetCt, null);
            if (method.getName().equals("getValue")) {
               String modifiedBody = methodBody;
               method.setBody(modifiedBody);
            }

            targetCt.addMethod(method);
         }
      }

      targetCt.getClassFile().setMajorVersion(ClassFile.JAVA_8);
      targetCt.writeFile(genDirectory + "target/classes");
   }


   private static boolean isDefaultMethod(Class<?> intf, CtMethod intfMethod) throws Exception
   {
      List<Class<?>> paramTypes = new ArrayList<>();

      for (CtClass pt : intfMethod.getParameterTypes()) {
         paramTypes.add(toJavaClass(pt));
      }

      return intf.getDeclaredMethod(intfMethod.getName(), paramTypes.toArray(new Class[0])).toString().contains("default ");
   }

   private static Set<Class<?>> getAllInterfaces(Class<?> clazz)
   {
      Set<Class<?>> interfaces = new LinkedHashSet<>();
      for (Class<?> intf : clazz.getInterfaces()) {
         if (intf.getInterfaces().length > 0) {
            interfaces.addAll(getAllInterfaces(intf));
         }
         interfaces.add(intf);
      }
      if (clazz.getSuperclass() != null) {
         interfaces.addAll(getAllInterfaces(clazz.getSuperclass()));
      }

      if (clazz.isInterface()) {
         interfaces.add(clazz);
      }

      return interfaces;
   }

   private static Class<?> toJavaClass(CtClass cls) throws Exception
   {
      if (cls.getName().endsWith("[]")) {
         return Array.newInstance(toJavaClass(cls.getName().replace("[]", "")), 0).getClass();
      }
      else {
         return toJavaClass(cls.getName());
      }
   }

   private static Class<?> toJavaClass(String cn) throws Exception
   {
      switch (cn) {
      case "int":
         return int.class;
      case "long":
         return long.class;
      case "short":
         return short.class;
      case "byte":
         return byte.class;
      case "float":
         return float.class;
      case "double":
         return double.class;
      case "boolean":
         return boolean.class;
      case "char":
         return char.class;
      case "void":
         return void.class;
      default:
         return Class.forName(cn);
      }
   }
}
