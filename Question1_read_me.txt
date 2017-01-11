The java program models Asynchronous message passing between a producer and consumer
To run:
1) Navigate to the Question_1.java file from the command line
2) Type "javac Question_1.java" and enter
3) Type "java Question_1" and enter

To stop program execution press ENTER at any point


*********************************Assumptions***************************************************************************************************************************************
-The MIX process doesn't need to be explicitly enforced because multiple threads running at the same time allows for non determinism on who performs at a given time by default.
-There are many producers but their IDs need not be tracked because they are producing for all consumers.
-There is an upper bound on the size of the queue
-In the producer class message IDs are chosen at random and then inserted in the buffer/queue.
-In the consumer class the consumer who receives the next message is chosen randomly. This is done because
 the producer produces infinite messages for each consumer and to show this I allowed the buffer to be filled and 
 empty continuously instead of iterating through consumers 0..C in order.



***********************************Classes***************************************************************************************************************************************
public class Question_1
Main testing class. Variables are initialized, Messages are created, Producer threads are created, consumer threads are created.

	public static int P;//Number of producers
	public static int C;//Number of consumers
	public static int numMsgs;//Number of Messages
	public static Thread[] ProdIDs;//Producer IDs range
	public static Thread[] ConsIDs;//Consumer IDs range
	public static int currentCons;//current consumer checking the queue
	public static String[] MsgIDs;//Message IDs range 
	public static int numMessages;//Message Values
	public static Buffer bufferObj = new Buffer();//Used as monitor between the Producer and Consumer classes 

ProdIDs[i] represents producer threads where the i value represents their IDs
ConsIDs[i] represents consumer threads where the i value represents their IDs

Each thread is immediately started after being created

*************************************************************************************************************************************************************************************

class Buffer
Buffer class acts as a monitor. Both Producer and Consumer are synced to this class. The Producer and Consumer threads are not allowed to access the Buffer class and modify its variables at the same time.

public static PriorityQueue<String> buffer;//Priority Queue/Buffer
	public static int B; //buffer size.//Buffer has B slots. Each slot can hold a message with a value between 0 and numMessages.
	public static boolean isEmpty();//Checks if buffer is empty. If buffer is empty then the consumer waits on the producer.
	public static boolean isFull();//Checks if the buffer is full. If buffer is full then the producer shouldn't add more produce to avoid overflow.

*************************************************************************************************************************************************************************************

class Producer
Producer class is a thread which sends messages. This class is synchronized to the Buffer class.
When the thread runs a random message ID is chosen and inserted into the buffer. The Producer class then calls notify() to wake up the consumer class if it was waiting on the queue to be refilled.

*************************************************************************************************************************************************************************************

class Consumer 
Consumer class is a thread which consumes messages addressed to it. A random consumer is chosen to receive a message from the queue/buffer. If the buffer is empty then the Consumer thread waits until the Producer thread replenishes the buffer/queue via wait(). If the buffer is populated then the consumer thread checks for the message addressed to it and if found removes it from the queue.



******************************************Testing*******************************************************************************************************************************************

After code creation several printing statements were created to verify the correctness of the code. Pictures have been attached to show test cases. Producers produce messages with IDs and Consumers consumed messages addressed to them i.e with the same IDs. WHen the buffer is filled the producer stops producing and waits to prevent overflowing the buffer. When the buffer is empty the consumer waits for the producer to replenish the buffer.



