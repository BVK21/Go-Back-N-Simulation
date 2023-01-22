package Driver;

import Timer.TimerAction;
import pojo.AckFrame;
import pojo.DataFrame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QueueManager implements TimerAction {

    private Queue<DataFrame> senderQueue ;
    private Queue<AckFrame>  receiverQueue;
    private List<DataFrame> senderQueueBuffer;
    private List<AckFrame> receiverQueueBuffer;



    private int currentTime;

    public QueueManager() {
        this.senderQueue = new LinkedList<>();
        this.receiverQueue = new LinkedList<>();
        this.senderQueueBuffer = new ArrayList<>();
        this.receiverQueueBuffer = new ArrayList<>();
        this.currentTime = 0;
    }

    public QueueManager(int currentTime) {
        this.currentTime = currentTime;
        this.senderQueue = new LinkedList<>();
        this.receiverQueue = new LinkedList<>();
        this.senderQueueBuffer = new ArrayList<>();
        this.receiverQueueBuffer = new ArrayList<>();
    }

    @Override
    public void action() {


        addToReceiverQueue();
        addToSenderQueue();
        currentTime++;
    }

    public void addToReceiverQueue(){
        for(AckFrame frame : receiverQueueBuffer){
            if(frame.getTime() == currentTime ){
                receiverQueue.add(frame);
                break;
            }
        }
    }

    public void addToSenderQueue(){
        for(DataFrame frame : senderQueueBuffer){
            if(frame.getTime() == currentTime ) {
               senderQueue.add(frame);
               break;
            }
        }
    }

    public Queue<DataFrame> getSenderQueue() {
        return senderQueue;
    }

    public void setSenderQueue(Queue<DataFrame> senderQueue) {
        this.senderQueue = senderQueue;
    }

    public Queue<AckFrame> getReceiverQueue() {
        return receiverQueue;
    }

    public void setReceiverQueue(Queue<AckFrame> receiverQueue) {
        this.receiverQueue = receiverQueue;
    }

    public List<DataFrame> getSenderQueueBuffer() {
        return senderQueueBuffer;
    }

    public void setSenderQueueBuffer(List<DataFrame> senderQueueBuffer) {
        this.senderQueueBuffer = senderQueueBuffer;
    }

    public List<AckFrame> getReceiverQueueBuffer() {
        return receiverQueueBuffer;
    }

    public void setReceiverQueueBuffer(List<AckFrame> receiverQueueBuffer) {
        this.receiverQueueBuffer = receiverQueueBuffer;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }
}
