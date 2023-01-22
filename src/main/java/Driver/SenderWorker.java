package Driver;

import Timer.TimerAction;
import pojo.AckFrame;
import pojo.DataFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

public class SenderWorker implements TimerAction {
    private List<Integer> dataDecisions;
    private List<DataFrame> sentFrames;
    private List<DataFrame> senderFrames;
    private int decisionPointer;
    private final int transmissionDelay = 1;
    private final int propagationDelay = 3;
    private final int WINDOW_SIZE = 7;

    private final int TIMEOUT = 7;
    private int currentTime;
    private int lastSent;
    private int waitingForAck;

    private int windowStart;

    private int windowEnd;

    private boolean retryMode;

    private Queue<AckFrame> receiverQueue;

    private int retryPointer;

    private boolean sendFramesOrRetransmit=true;

    

    public SenderWorker(int currentTime) {
        this.currentTime = currentTime;
        this.dataDecisions = new ArrayList<>();
        this.sentFrames = new ArrayList<>();
        this.senderFrames = new ArrayList<>();
        this.decisionPointer = 0;
        this.lastSent = 0;
        this.waitingForAck = 0;
        this.windowStart = 0;
        this.windowEnd = 0;
        this.retryMode = false;
        this.retryPointer = 0;
    }

    public SenderWorker(List<Integer> dataDecisions, int currentTime) {
        this.dataDecisions = dataDecisions;
        this.currentTime = currentTime;
        this.sentFrames = new ArrayList<>();
        this.senderFrames = new ArrayList<>();
        this.decisionPointer = 0;
        this.lastSent = 0;
        this.waitingForAck = 0;
        this.windowStart = 0;
        this.windowEnd = 0;
        this.retryMode = false;
        this.retryPointer = 0;
    }

    public SenderWorker(List<Integer> dataDecisions, List<DataFrame> senderFrames, int currentTime, Queue<AckFrame> receiverQueue) {
        this.dataDecisions = dataDecisions;
        this.senderFrames = senderFrames;
        this.currentTime = currentTime;
        this.receiverQueue = receiverQueue;
        this.sentFrames = new ArrayList<>();
        this.decisionPointer = 0;
        this.lastSent = 0;
        this.waitingForAck = 0;
        this.windowStart = 0;
        this.windowEnd = 0;
        this.retryMode = false;
        this.retryPointer = 0;
    }

    @Override
    public void action() {

        sendFramesOrRetransmit=true;
        checkForAckFrames();
        retransmitOldFrameOnTimeOut();
        if(sendFramesOrRetransmit)
            sendFrames();
        currentTime++;
    }

    public void sendFrames(){
        if(this.lastSent - this.waitingForAck < this.WINDOW_SIZE) {
            DataFrame frame = new DataFrame();
            frame.setSeqNo(lastSent);
            frame.setTime(currentTime  + propagationDelay + transmissionDelay);
            frame.setTimeOut(currentTime + TIMEOUT + transmissionDelay);
            frame.setWindowStart(waitingForAck);
            frame.setWindowEnd(WINDOW_SIZE + waitingForAck -1);
            if (this.dataDecisions.get(this.decisionPointer) == 1) {
                frame.setDecFlag(true);
                System.out.println("time " + this.currentTime + ": sender window [" + frame.getWindowStart() + ", " + frame.getWindowEnd()
                        + "], transmitting new frame " + frame.getSeqNo() + ", good transmission");
                this.senderFrames.add(frame);
                this.sentFrames.add(frame);
            } else {
                frame.setDecFlag(false);
                System.out.println("time " + this.currentTime + ": sender window [" + frame.getWindowStart() + ", " + frame.getWindowEnd()
                        + "], transmitting new frame " + frame.getSeqNo() + ", bad transmission");
                 this.sentFrames.add(frame);
            }
            lastSent++;
            decisionPointer++;
        }
    }

    public void checkForAckFrames(){
        try{
            AckFrame receivedAckFrame = receiverQueue.remove();
//            if(receivedAckFrame.getAckNo() == waitingForAck+1) {
//            retryMode = false;
            System.out.println("time " + receivedAckFrame.getTime() + ": sender got ACK" + receivedAckFrame.getAckNo() + ", window [" + receivedAckFrame.getWindowStart() + ", " + receivedAckFrame.getWindowEnd() + "]");
            waitingForAck = receivedAckFrame.getAckNo();
            retryPointer = waitingForAck;
//            }else {
//                retryMode = true;
//                System.out.println("time " + receivedAckFrame.getTime() + ": sender got ACK" + receivedAckFrame.getAckNo() + ", window [" + receivedAckFrame.getWindowStart() + ", " + receivedAckFrame.getWindowEnd() + "]");

//            }
        }catch (NoSuchElementException e){
            //Nothing to worry
            if(!retryMode)
               retryPointer = waitingForAck;
        }
    }

    public void retransmitOldFrameOnTimeOut(){
        for(DataFrame frame : sentFrames){
            if(frame.getTimeOut() == currentTime  && frame.getSeqNo() >= waitingForAck){
                if (this.dataDecisions.get(this.decisionPointer) == 1) {
                    frame.setDecFlag(true);
                    frame.setWindowStart(waitingForAck);
                    frame.setWindowEnd(waitingForAck+6);
                    frame.setTime(currentTime  + propagationDelay + transmissionDelay);
                    System.out.println("time " + currentTime + ": sender window [" + frame.getWindowStart() + ", " + frame.getWindowEnd()
                            + "], retransmitting old frame " + frame.getSeqNo() + ", good transmission");
                    frame.setTimeOut(currentTime + TIMEOUT + transmissionDelay);
                    removeFrameFromBuffer(frame.getSeqNo());
                    senderFrames.add(frame);

                } else {
                    frame.setDecFlag(false);
                    frame.setWindowStart(waitingForAck);
                    frame.setWindowEnd(waitingForAck+6);
                    frame.setTime(currentTime + propagationDelay + transmissionDelay);
                    System.out.println("time " + currentTime + ": sender window [" + frame.getWindowStart() + ", " + frame.getWindowEnd()
                            + "], retransmitting old frame " + frame.getSeqNo() + ", bad transmission");
                    frame.setTimeOut(currentTime + TIMEOUT + transmissionDelay);
                    removeFrameFromBuffer(frame.getSeqNo());
                }
                decisionPointer++;
                sendFramesOrRetransmit=false;
            }
        }
    }

    public void removeFrameFromBuffer( int seqNo){
        List<Integer> deleteIndex = new ArrayList<>();
        for(int i = 0 ; i < senderFrames.size(); i++){
            if(senderFrames.get(i).getSeqNo() == seqNo){
                deleteIndex.add(i);
            }
        }

        for(Integer i : deleteIndex){
            senderFrames.remove((int)i);
        }
    }

    public void checkAndAddIntoSenderFrames(int seqNo , int updatedTime , int windowStart , int windowEnd ,int newTimeOut,DataFrame oldFrame){
        boolean found = false;
        for(DataFrame frame : senderFrames){
            if(frame.getSeqNo() == seqNo){
                frame.setTime(updatedTime);
                frame.setWindowStart(windowStart);
                frame.setWindowEnd(windowEnd);
                frame.setTimeOut(newTimeOut);
                found = true;
            }
        }

        if(!found){
            senderFrames.add(oldFrame);
        }
    }
    public void  retransmitOldFrame(){

        if(retryMode && retryPointer < lastSent ){
            if (this.dataDecisions.get(this.decisionPointer) == 1) {
                sentFrames.get(retryPointer).setDecFlag(true);
                sentFrames.get(retryPointer).setWindowStart(waitingForAck);
                sentFrames.get(retryPointer).setWindowEnd(waitingForAck+6);
                sentFrames.get(retryPointer).setTime(currentTime  + propagationDelay + transmissionDelay);
                System.out.println("time " + currentTime + ": sender window [" + sentFrames.get(retryPointer).getWindowStart() + ", " + sentFrames.get(retryPointer).getWindowEnd()
                        + "], retransmitting old frame " + sentFrames.get(retryPointer).getSeqNo() + ", good transmission");
                sentFrames.get(retryPointer).setTimeOut(currentTime + TIMEOUT + transmissionDelay);
                removeFrameFromBuffer(senderFrames.get(retryPointer).getSeqNo());
                senderFrames.add(sentFrames.get(retryPointer));
            } else {
                sentFrames.get(retryPointer).setDecFlag(false);
                sentFrames.get(retryPointer).setWindowStart(waitingForAck);
                sentFrames.get(retryPointer).setWindowEnd(waitingForAck+6);
                sentFrames.get(retryPointer).setTime(currentTime + propagationDelay + transmissionDelay);
                System.out.println("time " + currentTime + ": sender window [" + sentFrames.get(retryPointer).getWindowStart() + ", " + sentFrames.get(retryPointer).getWindowEnd()
                        + "], retransmitting old frame " + sentFrames.get(retryPointer).getSeqNo() + ", bad transmission");
                sentFrames.get(retryPointer).setTimeOut(currentTime + TIMEOUT + transmissionDelay);
                removeFrameFromBuffer(senderFrames.get(retryPointer).getSeqNo());
            }
            retryPointer++;
            decisionPointer++;
        } else if(retryMode){
            retryPointer=waitingForAck;
        }
    }

    public boolean checkForTimeOut(){
        for(DataFrame frame : sentFrames){
            if(frame.getTimeOut() == currentTime){
                return true;
            }
        }
        return false;
    }


    public List<Integer> getDataDecisions() {
        return dataDecisions;
    }

    public void setDataDecisions(List<Integer> dataDecisions) {
        this.dataDecisions = dataDecisions;
    }

    public List<DataFrame> getSentFrames() {
        return sentFrames;
    }

    public void setSentFrames(List<DataFrame> sentFrames) {
        this.sentFrames = sentFrames;
    }

    public List<DataFrame> getSenderFrames() {
        return senderFrames;
    }

    public void setSenderFrames(List<DataFrame> senderFrames) {
        this.senderFrames = senderFrames;
    }

    public int getDecisionPointer() {
        return decisionPointer;
    }

    public void setDecisionPointer(int decisionPointer) {
        this.decisionPointer = decisionPointer;
    }

    public int getTransmissionDelay() {
        return transmissionDelay;
    }

    public int getPropagationDelay() {
        return propagationDelay;
    }

    public int getWINDOW_SIZE() {
        return WINDOW_SIZE;
    }

    public int getTIMEOUT() {
        return TIMEOUT;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public int getLastSent() {
        return lastSent;
    }

    public void setLastSent(int lastSent) {
        this.lastSent = lastSent;
    }

    public int getWaitingForAck() {
        return waitingForAck;
    }

    public void setWaitingForAck(int waitingForAck) {
        this.waitingForAck = waitingForAck;
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

    public boolean isRetryMode() {
        return retryMode;
    }

    public void setRetryMode(boolean retryMode) {
        this.retryMode = retryMode;
    }

    public Queue<AckFrame> getReceiverQueue() {
        return receiverQueue;
    }

    public void setReceiverQueue(Queue<AckFrame> receiverQueue) {
        this.receiverQueue = receiverQueue;
    }

    public int getRetryPointer() {
        return retryPointer;
    }

    public void setRetryPointer(int retryPointer) {
        this.retryPointer = retryPointer;
    }
}
