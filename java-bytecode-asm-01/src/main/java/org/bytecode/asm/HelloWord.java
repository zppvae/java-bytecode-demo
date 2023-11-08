package org.bytecode.asm;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class HelloWord extends ClassLoader{

    public static void main(String[] args) throws Exception{
        // 生成二进制字节码
        byte[] bytes = generate();
        // 输出字节码
        outputClazz(bytes);
        // 加载AsmTest
        Class<?> clazz = new HelloWord().defineClass("org.bytecode.asm.AsmTest", bytes, 0, bytes.length);
        // 反射获取 main 方法
        Method main = clazz.getMethod("main", String[].class);
        // 调用 main 方法
        main.invoke(null, new Object[]{new String[]{}});
    }

    private static void outputClazz(byte[] bytes) {
        // 输出类字节码
        FileOutputStream out = null;
        try {
            String pathName = HelloWord.class.getResource("/").getPath() + "AsmTest.class";
            out = new FileOutputStream(new File(pathName));
            out.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 用asm写输出hello world
     * @return
     */
    private static byte[] generate() {
        // 定义类的生成
        ClassWriter classWriter = new ClassWriter(0);
        // 定义对象头；版本号、修饰符、全类名、签名、父类、实现的接口
        classWriter.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "org/bytecode/asm/AsmTest", null, "java/lang/Object", null);
        // 添加方法；修饰符、方法名、描述符、签名、异常
        // ([Ljava/lang/String;)V == void main(String[] args)
        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        // 执行指令；获取静态属性
        // 生成：System.out
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        // 加载常量 load constant
        methodVisitor.visitLdcInsn("Asm Test");
        // 调用方法
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        // 返回
        methodVisitor.visitInsn(Opcodes.RETURN);
        // 设置操作数栈的深度和局部变量的大小
        methodVisitor.visitMaxs(2, 1);
        // 方法结束
        methodVisitor.visitEnd();
        // 类完成
        classWriter.visitEnd();
        // 生成字节数组
        return classWriter.toByteArray();
    }

}
