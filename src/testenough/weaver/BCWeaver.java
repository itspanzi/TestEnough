package testenough.weaver;

import javassist.*;
import testenough.Configuration;

import java.io.ByteArrayInputStream;

public class BCWeaver {

    private Configuration configuration;

    public BCWeaver(Configuration configuration) {
        this.configuration = configuration;
    }

    public byte[] weave(String className, ClassLoader loader, byte[] byteCode) {
        if (!configuration.shouldWeave(className)) return null;
        try {
            CtClass ctClass = byteCodeAsClass(loader, copyOf(byteCode));
            for (CtMethod method : ctClass.getDeclaredMethods()) {
                if (shouldEnhance(method)) {
                    try {
                        method.insertAfter(codeToBeInserted(), true);
                    } catch (CannotCompileException e) {
                        System.out.println("Couldn't instrument: " + method + ". Caused by: " + e);
                    }
                }
            }
            return ctClass.toBytecode();
        } catch (Exception e) {
            throw new RuntimeException("Could not instrument", e);
        }
    }

    private byte[] copyOf(byte[] byteCode) {
        byte[] copy = new byte[byteCode.length];
        System.arraycopy(byteCode, 0, copy, 0, byteCode.length);
        return copy;
    }

    private boolean shouldEnhance(CtMethod method) {
        return !method.isEmpty() && !Modifier.isAbstract(method.getModifiers());
    }

    private CtClass byteCodeAsClass(ClassLoader loader, byte[] copy) throws Exception {
        Class<?> aClass = loader.loadClass(classPoolClassName());
        ClassPool aDefault = (ClassPool) aClass.getMethod("getDefault").invoke(null);
        aDefault.appendClassPath(new ClassClassPath(loader.loadClass(sampleClassFromProd())));
        return aDefault.makeClass(new ByteArrayInputStream(copy));
    }

    private String classPoolClassName() {
        //Any reference to the class object in order to get the full qualified name programatically loads the class to be loaded. We want to load it using the given class loader.
        return "javassist.ClassPool";
    }

    private String sampleClassFromProd() {
        return configuration.sampleClassFromProd();
    }

    private String codeToBeInserted() {
        return configuration.codeToBeInserted();
    }

    @Override
    public boolean equals(Object obj) {
        return true;
    }
}
