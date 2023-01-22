package pojo;

public class DataFrame {

    private int seqNo;
    private int time;
    private int timeOut;
    private boolean decFlag;

    private int windowStart;

    private int windowEnd;

    public DataFrame() {
    }

    public DataFrame(int seqNo, int time, int timeOut, boolean decFlag,int windowStart,int windowEnd) {
        this.seqNo = seqNo;
        this.time = time;
        this.timeOut = timeOut;
        this.decFlag = decFlag;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public boolean isDecFlag() {
        return decFlag;
    }

    public void setDecFlag(boolean decFlag) {
        this.decFlag = decFlag;
    }

    public int getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(int windowStart) {
        this.windowStart = windowStart;
    }

    public int getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(int windowEnd) {
        this.windowEnd = windowEnd;
    }
}
