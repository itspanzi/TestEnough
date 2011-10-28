package testenough;

import testenough.weaver.BCWeaver;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Does not have a job anymore. We should just move to bcweaver
 */
public class CallTrackingTransformer implements ClassFileTransformer {

    private Configuration configuration;
    private BCWeaver bcWeaver;

    public CallTrackingTransformer(Configuration configuration, BCWeaver bcWeaver) {
        this.configuration = configuration;
        this.bcWeaver = bcWeaver;
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return bcWeaver.weave(className, loader, classfileBuffer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallTrackingTransformer that = (CallTrackingTransformer) o;
        return !(configuration != null ? !configuration.equals(that.configuration) : that.configuration != null);
    }

    @Override
    public int hashCode() {
        return configuration != null ? configuration.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CallTrackingTransformer{" +
                "configuration=" + configuration +
                ", bcWeaver=" + bcWeaver +
                '}';
    }
}
