package testenough.weaver;

import javassist.*;

import java.io.ByteArrayInputStream;

public class BCWeaver {
    public byte[] weave(ClassLoader loader, byte[] byteCode) {
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

    private String codeToBeInserted() {
        return "testenough.counter.Track.trackCurrentThread();";
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
        return "mtrace.spike.Another";
    }

    @Override
    public boolean equals(Object obj) {
        return true;
    }
}
