import java.util.Random;
import java.io.IOException;
import java.util.PriorityQueue;

/**
 * **Producer and Consumer Asynchronous channel**
 * -To stop program execution press ENTER at any point
 * 
 * Assumptions: 
 * -The MIX process doesn't need to be explicitly enforced because multiple threads running at the same time allows 
 *  for non determinism on who performs at a given time by default.
 * -There are many producers but their IDs need not be tracked because they are producing for all consumers
 * -There is an upper bound on the size of the queue
 * -In the producer class message IDs are chosen at random and then inserted in the buffer
 * -In the consumer class the consumer who receives the next message is chosen randomly. This is done because
 * the producer produces infinite messages for each consumer and to show this I allowed the buffer to be filled and 
 * empty continuously instead of iterating through consumer
 * 
 */


/**
 * @author Corie
 * Main Testing Class 
 */
public class Question_1{
	public static int P;//Number of producers
	public static int C;//Number of consumers
	public static int numMsgs;//Number of Messages
	public static Thread[] ProdIDs;//Producer IDs range
	public static Thread[] ConsIDs;//Consumer IDs range
	public static int currentCons;//current consumer checking the queue
	public static String[] MsgIDs;//Message IDs range 
	public static int numMessages;////Message Values
	public static Buffer bufferObj = new Buffer();//Used as monitor between the Producer and Consumer classes 
	

	public static void main(String args[]) throws InterruptedException{
		P=2;//Number of Producers
		C=5;//Number of Consumers
		numMessages= 5;//Number of messages 
		ProdIDs= new Thread[P];//Producer IDs created
		ConsIDs= new Thread[C];//Consumer IDs created
		MsgIDs = new String[numMessages];//Message IDs created
		
		for (int i = 0; i < numMessages; i++) {//messages with values up to numMessages
			MsgIDs[i]="Message: "+ i;//Messages created 
		}
		for (int i = 0; i < P; i++) {//Producer threads created and started
			ProdIDs[i] = new Thread(new Producer());
			ProdIDs[i].start();
		}
		for (int i = 0; i < C; i++) {//Consumer threads created and started
			ConsIDs[i] = new Thread(new Consumer());
			ConsIDs[i].start();
		}		
	}
}


/**
 * 
 * @author Corie
 *Buffer class acts as a monitor. Both Producer and Consumer are synced to this class
 */
class Buffer{
	public static PriorityQueue<String> buffer;
	public static int B; //buffer size
	
	public Buffer(){//Buffer class constructor
		B=5;//Buffer has B slots. Each slot can hold a message with a value between 0 and numMessages
		buffer = new PriorityQueue<String>(B);//Buffer/Queue created of capacity B
	}
	
	public static boolean isEmpty(){//Checks if buffer is empty. If buffer is empty then the consumer waits on the producer.
		if(buffer.size()==0) return true;//buffer empty
		else return false;//buffer not empty
	}
	
	public static boolean isFull(){//Checks if the buffer is full. If buffer is full then the producer shouldn't add more produce to avoid overflow.
		if(buffer.size()==B) return true;//buffer full
		else return false;//buffer not full
	}
} 


/**
 * 
 * @author Corie
 * Producer class sends messages 
 */
class Producer implements Runnable{
	@Override
	public void run() {//Threads will run indefinitely until ENTER key is pressed
		try {
			while (System.in.available() == 0){
				synchronized (Question_1.bufferObj){//producer locked to the buffer
					Random random = new Random();
					int randProd=random.nextInt(Question_1.P);//random producer
					int randID=random.nextInt(Question_1.numMessages);//producer generates random message ID
					while(Buffer.isFull()){//If buffer filled then the producer must wait to avoid overflowing the queue
						try {
							System.out.println("Buffer filled Producer waiting");
							Question_1.bufferObj.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}//else if buffer isn't full then
					Buffer.buffer.offer(Question_1.MsgIDs[randID]);//inserts random messageID into the queue (FIFO)
					System.out.println("Producer "+randProd+" produced message: "+ randID);
					Question_1.bufferObj.notify();//wakes up the consumer thread if it is sleeping after the buffer has new content
				}	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


/**
 * 
 * @author Corie
 * Consumer class receives messages 
 */
class Consumer implements Runnable{
	@Override
	public void run() {
		try {
			while(System.in.available() == 0){//Threads will run indefinitely until ENTER key is pressed
				synchronized (Question_1.bufferObj){//consumer locked to the buffer
					Random rand = new Random();
					int randCons = rand.nextInt(Question_1.C);//chooses a random consumer to check queue
					while(Buffer.isEmpty()){//If Buffer is empty and there is nothing to consume then the consumer waits
						try {
							System.out.println("Buffer empty Consumer waiting");
							Question_1.bufferObj.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}//else if the buffer isn't empty and there is produce then consume 
					boolean checkID=Buffer.buffer.remove("Message: "+ randCons);//Consumer checks for a message on the queue with its respective ID
					if(checkID){//if true this means that the consumer has found a message addressed to it i.e. IDs match. The message is then removed from the queue
						System.out.println("Consumer "+randCons+" consumed message: "+ randCons);
					}
					Question_1.bufferObj.notify();//if the buffer/queue is filled and the producer was waiting for space to be made available then wake the producer up
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}





