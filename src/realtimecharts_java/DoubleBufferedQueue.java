package realtimecharts_java;
import java.util.ArrayList;

public class DoubleBufferedQueue<T>
{
    private ArrayList<T> buffer;
    private ArrayList<T> buffer0;
    private ArrayList<T> buffer1;
    private int maxBufferSize;

    public DoubleBufferedQueue(int bufferSize)
    {
    	maxBufferSize = bufferSize;
        buffer0 = buffer = new ArrayList<T>(bufferSize);
        buffer1 = new ArrayList<T>(bufferSize);
    }
    public DoubleBufferedQueue()
    {
    	this(10000);
    }

    //
    // Add an item to the queue. Returns true if successful, false if the buffer is full.
    //
    public synchronized boolean put(T datum)
    {
    	boolean canWrite = buffer.size() < maxBufferSize;
        if (canWrite) buffer.add(datum);
        return canWrite;        
    }

    //
    // Get all the items in the queue.
    //
    public synchronized ArrayList<T> get()
    {
    	ArrayList<T> ret = buffer;
        buffer = (buffer == buffer0) ? buffer1 : buffer0;
        buffer.clear();
        return ret;
    }
}