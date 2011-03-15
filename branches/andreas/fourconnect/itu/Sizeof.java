package andreas.fourconnect.itu;
public class Sizeof
{
	
	long memoryLoad = 0;
	long heap1= 0;
	public Sizeof() {
		
	}
	
	public void init() throws Exception {
		runGC ();
        usedMemory ();
        long heap1 = usedMemory();
	}

    public void getMemUsage() throws Exception
    {
        runGC ();
        long heap2 = usedMemory (); // Take an after heap snapshot:
        
        System.out.println ("'before' heap: " + heap1 +
                            ", 'after' heap: " + heap2);
        System.out.println ("heap delta: " + (heap2 - heap1));
        
    }
    private static void runGC () throws Exception
    {
        // It helps to call Runtime.gc()
        // using several method calls:
        for (int r = 0; r < 4; ++ r) _runGC ();
    }
    private static void _runGC () throws Exception
    {
        long usedMem1 = usedMemory (), usedMem2 = Long.MAX_VALUE;
        for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++ i)
        {
            s_runtime.runFinalization ();
            s_runtime.gc ();
            Thread.currentThread ().yield ();
            
            usedMem2 = usedMem1;
            usedMem1 = usedMemory ();
        }
    }
    private static long usedMemory ()
    {
        return s_runtime.totalMemory () - s_runtime.freeMemory ();
    }
    
    private static final Runtime s_runtime = Runtime.getRuntime ();
} // End of class