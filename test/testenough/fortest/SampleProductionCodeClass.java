package testenough.fortest;

public class SampleProductionCodeClass {

    public void sampleMethod() {
        testenough.counter.Track.trackCurrentThread();
    }

    public void anotherMethod() {
        testenough.counter.Track.trackCurrentThread();
    }

    public void nest() {
        FakeTestClass.callTracker();
    }

    public static class AnInnerClass {

        public void innerMethod() {
            testenough.counter.Track.trackCurrentThread();
        }
    }
}
