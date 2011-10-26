package testenough.fortest;

public abstract class ClassThatWillBeWeaved {

    public static final void method1() {}

    private static void method2() {}

    private void method3(){}

    abstract void method4();

    private static class InnerClass extends  ClassThatWillBeWeaved {

        @Override
        public void method4() {
        }
    }
}
