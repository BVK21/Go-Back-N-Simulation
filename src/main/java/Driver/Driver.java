package Driver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Driver {

    private static final String dataDecisionsFilePath =  "/Users/bhim/IdeaProjects/Go-Back-N-Simulation/src/main/resources/DATA_GOOD_2";
    private static final String ackDecisionFilePath = "/Users/bhim/IdeaProjects/Go-Back-N-Simulation/src/main/resources/ACK_GOOD_2";

    public static List<Integer> getDecisions(String filepath) throws Exception {
        if (filepath == null || filepath.isEmpty() )
            throw new Exception("Invalid File Paths");
        List<Integer> dataGood = new ArrayList<>();

        try(BufferedReader dataDec = new BufferedReader(new FileReader(filepath))) {

            String ackDecString;
            while ((ackDecString = dataDec.readLine()) != null ) {
                ackDecString = ackDecString.trim();
                if (ackDecString.isEmpty())
                    continue;
                dataGood.add(Integer.parseInt(ackDecString));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return dataGood;

    }

    public static void main(String[] args) throws Exception {
        List<Integer> dataDec = getDecisions(dataDecisionsFilePath) ;
        List<Integer> ackDec = getDecisions(ackDecisionFilePath);
        QueueManager queue = new QueueManager();
        ReceiverWorker receiver = new ReceiverWorker(0,queue.getSenderQueue(),queue.getReceiverQueueBuffer(),ackDec);
        SenderWorker sender = new SenderWorker(dataDec,queue.getSenderQueueBuffer(),0, queue.getReceiverQueue());
        System.out.println("time 0: sender got ACK0, window [0, 0]");
        int i = 0;
        while(i <= 500){


            queue.action();
            receiver.action();
            try {
                sender.action();
            }catch (Exception e){
                e.printStackTrace();
            }



            i++;
        }



    }

}
