package Driver;

import Timer.TimerAction;
import pojo.AckFrame;
import pojo.DataFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

public class ReceiverWorker implements TimerAction {

    private int currentTime;
    private int waitingFor;
    private int ackDecisionPointer;
    private Queue<DataFrame> senderQueue;
    private List<AckFrame> receiverFrames;
    private List<AckFrame> receivedFrames;
    private List<Integer> ackDecisions;


    public ReceiverWorker(int currentTime, Queue<DataFrame> receiverQueue, List<AckFrame> receiverFrames, List<Integer> ackDecisions) {
        this.currentTime = currentTime;
        this.senderQueue = receiverQueue;
        this.receiverFrames = receiverFrames;
        this.ackDecisions = ackDecisions;
        this.receivedFrames = new ArrayList<>();
        this.waitingFor = 0;
        this.ackDecisionPointer = 0;

    }

    @Override
    public void action() {

      receiveFrame();
      currentTime++;

    }

    public void receiveFrame(){
        try{
            DataFrame sentFrame = senderQueue.remove();
            if (sentFrame.getSeqNo() == waitingFor) {
                waitingFor++;

            }
            sendAck(sentFrame);
        }catch (NoSuchElementException e){

        }
    }

    public void sendAck(DataFrame sentFrame){
        AckFrame frame = new AckFrame();
        if (ackDecisions.get(ackDecisionPointer) == 1) {
            frame.setAckNo(waitingFor);
            frame.setTime(currentTime  +3);
            frame.setWindowStart(waitingFor);
            frame.setWindowEnd(waitingFor+6);
            System.out.println("time " + currentTime + ": receiver got frame " + sentFrame.getSeqNo() + ", transmitting ACK" + waitingFor
                    + ", good transmission");
            receiverFrames.add(frame);
//            receivedFrames.add(frame);
        } else {
            frame.setAckFlag(false);
            frame.setAckNo(waitingFor);
            frame.setTime(currentTime  +3);
            frame.setWindowStart(waitingFor);
            frame.setWindowEnd(waitingFor+6);
            System.out.println("time " + currentTime + ": receiver got frame " + sentFrame.getSeqNo() + ", transmitting ACK" + waitingFor
                    + ", bad transmission");
            receivedFrames.add(frame);
        }
        ackDecisionPointer++;
   }
}
