package Driver;

import pojo.AckFrame;
import pojo.DataFrame;

import java.util.*;

public class GoBackN {
     private final int transmissionDelay = 1;
     private final int propagationDelay = 3;
     private final int WINDOW_SIZE = 7;

     private final int TIMEOUT = 7;
     private Queue<DataFrame> senderQueue;
     private Queue<AckFrame> receiverQueue;
     private List<DataFrame> sentFrames;
     private List<DataFrame> receiverFrames;

     private List<Integer> dataDecisions;

     private List<Integer> ackDecisions;


     public GoBackN() {
     }

     public GoBackN(Queue<DataFrame> senderQueue, Queue<AckFrame> receiverQueue, List<DataFrame> sentFrames, List<DataFrame> receiverFrames) {
          this.senderQueue = senderQueue;
          this.receiverQueue = receiverQueue;
          this.sentFrames = sentFrames;
          this.receiverFrames = receiverFrames;
     }

     public GoBackN(List<Integer> dataDecisions, List<Integer> ackDecisions) {
          this.dataDecisions = dataDecisions;
          this.ackDecisions = ackDecisions;
          this.senderQueue = new LinkedList<>();
          this.receiverQueue = new LinkedList<>();
          this.sentFrames = new ArrayList<>();
          this.receiverFrames = new ArrayList<>();
     }

     public void SenderThread() throws InterruptedException {
          int lastSent = 0;
          int waitingForAck = 0;
          int currentTime = 0;
          int decisionPointer = 0;

          while (true) {
               while (lastSent - waitingForAck < WINDOW_SIZE) {
                    DataFrame frame = new DataFrame();
                    frame.setSeqNo(lastSent);
                    frame.setTime(currentTime + propagationDelay + transmissionDelay);
                    frame.setTimeOut(currentTime + TIMEOUT + transmissionDelay);
                    frame.setWindowStart(waitingForAck);
                    frame.setWindowEnd(WINDOW_SIZE + waitingForAck -1);
                    Thread.sleep(300);

                    if (dataDecisions.get(decisionPointer) == 1) {
                         frame.setDecFlag(true);
                         System.out.println("time " + currentTime + ": sender window [" + frame.getWindowStart() + ", " + frame.getWindowEnd()
                                 + "], transmitting new frame " + frame.getSeqNo() + ", good transmission");
                         senderQueue.add(frame);
                    } else {
                         frame.setDecFlag(false);
                         System.out.println("time " + currentTime + ": sender window [" + frame.getWindowStart() + ", " + frame.getWindowEnd()
                                 + "], transmitting new  frame " + frame.getSeqNo() + ", bad transmission");
                         // senderQueue.add(frame);
                    }

                    sentFrames.add(frame);
                    lastSent++;
                    currentTime++;
                    decisionPointer++;
               }
               try {
                    AckFrame receivedAckFrame = receiverQueue.remove();
                    System.out.println("time " + receivedAckFrame.getTime() + ": sender got ACK" + receivedAckFrame.getAckNo() + ", window [" + receivedAckFrame.getWindowStart() + ", " + receivedAckFrame.getWindowEnd() + "]");
                    waitingForAck = receivedAckFrame.getAckNo();
               } catch (NoSuchElementException e) {
                    for (int i = waitingForAck; i < lastSent; i++) {
                         Thread.sleep(300);
                         int windowEnd = lastSent - 1;
                         if (dataDecisions.get(decisionPointer) == 1) {
                              sentFrames.get(i).setDecFlag(true);
                              sentFrames.get(i).setWindowStart(waitingForAck);
                              sentFrames.get(i).setWindowEnd(windowEnd);
                              sentFrames.get(i).setTime(currentTime + propagationDelay + transmissionDelay);
                              System.out.println("time " + sentFrames.get(i).getTimeOut() + ": sender window [" + sentFrames.get(i).getWindowStart() + ", " + sentFrames.get(i).getWindowEnd()
                                      + "], retransmitting old frame " + sentFrames.get(i).getSeqNo() + ", good transmission");
                              sentFrames.get(i).setTimeOut(currentTime + TIMEOUT + transmissionDelay);
                              senderQueue.add(sentFrames.get(i));
                         } else {
                              sentFrames.get(i).setDecFlag(false);
                              sentFrames.get(i).setWindowStart(waitingForAck);
                              sentFrames.get(i).setWindowEnd(windowEnd);
                              sentFrames.get(i).setTime(currentTime + propagationDelay + transmissionDelay);
                              System.out.println("time " + sentFrames.get(i).getTimeOut() + ": sender window [" + sentFrames.get(i).getWindowStart() + ", " + sentFrames.get(i).getWindowEnd()
                                      + "], retransmitting old frame " + sentFrames.get(i).getSeqNo() + ", bad transmission");
                              sentFrames.get(i).setTimeOut(currentTime + TIMEOUT + transmissionDelay);
                              senderQueue.add(sentFrames.get(i));
                              // senderQueue.add(sentFrames.get(i));
                         }
                         currentTime++;
                         decisionPointer++;
                    }

               }
          }
     }

   public void receiverThread() {
        int waitingFor = 0;
        int ackDecisionPointer = 0;

        while (true) {
             try {
                  AckFrame frame = new AckFrame();
                  DataFrame sentFrame = senderQueue.remove();
                  if (sentFrame.getSeqNo() == waitingFor) {
                       waitingFor++;
                       frame.setWindowStart(sentFrame.getWindowStart() +1);
                       frame.setWindowEnd(sentFrame.getWindowEnd() + 1);

                       receiverFrames.add(sentFrame);
                  }else{
                       frame.setWindowStart(sentFrame.getWindowStart());
                       frame.setWindowEnd(sentFrame.getWindowEnd());
                  }


                  frame.setAckNo(waitingFor);
                  frame.setTime(sentFrame.getTime() + propagationDelay);


                  if (ackDecisions.get(ackDecisionPointer) == 1) {
                       frame.setAckFlag(true);
                       System.out.println("time " + sentFrame.getTime() + ": receiver got frame " + sentFrame.getSeqNo() + ", transmitting ACK" + waitingFor
                               + ", good transmission");
                       receiverQueue.add(frame);
                  } else {
                       frame.setAckFlag(false);
                       System.out.println("time " + sentFrame.getTime() + ": receiver got frame " + sentFrame.getSeqNo() + ", transmitting ACK" + waitingFor
                               + ", bad transmission");
                       //receiverQueue.add(frame);
                  }
             }catch (NoSuchElementException e){
                  continue;
             }
           ackDecisionPointer++;
        }
   }

     public static void main(String[] args) {
          DataFrame[] frames = new DataFrame[10];

     }


}
