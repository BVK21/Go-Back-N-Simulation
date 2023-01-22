package Driver;

public class Timer {
    private static int currentTime;

    public static int getCurrentTime() {
        return currentTime;
    }

    public static void increaseCurrentTime()
    {
        currentTime++;
    }

}
