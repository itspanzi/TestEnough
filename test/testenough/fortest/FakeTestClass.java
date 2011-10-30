package testenough.fortest;

import testenough.counter.Track;

public class FakeTestClass {

    public static void callTracker() {
        Track.trackCurrentThread();
    }
}
