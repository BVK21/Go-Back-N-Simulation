package pojo;

public class AckFrame {
     private int ackNo;
     private int time;

     private boolean ackFlag;

     private boolean isReceived;

     private int windowStart;

     private int windowEnd;


     public AckFrame() {
     }

     public AckFrame(int ackNo, int time, boolean sentFlag,boolean isReceived,int windowStart,int windowEnd) {
          this.ackNo = ackNo;
          this.time = time;
          this.isReceived = isReceived;
          this.ackFlag = sentFlag;
          this.windowStart = windowStart;
          this.windowEnd = windowEnd;
     }

     public int getAckNo() {
          return ackNo;
     }

     public void setAckNo(int ackNo) {
          this.ackNo = ackNo;
     }

     public int getTime() {
          return time;
     }

     public void setTime(int time) {
          this.time = time;
     }



     public boolean isAckFlag() {
          return ackFlag;
     }

     public void setAckFlag(boolean ackFlag) {
          this.ackFlag = ackFlag;
     }

     public boolean isReceived() { return isReceived; }

     public void setReceived(boolean received) {
          isReceived = received;
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
