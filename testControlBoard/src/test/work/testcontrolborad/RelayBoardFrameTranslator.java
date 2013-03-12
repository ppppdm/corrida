package test.work.testcontrolborad;

public class RelayBoardFrameTranslator {
	
	//Public Constant variables
	public static final int LIGHT0 = 0;
	public static final int LIGHT1 = 1;
	public static final int LIGHT2 = 2;
	public static final int LIGHT3 = 3;
	public static final int LIGHT4 = 4;
	public static final int LIGHT5 = 5;
	public static final int LIGHT6 = 6;
	public static final int LIGHT7 = 7;
	
	public static final int SWITCH0 = 0;
	public static final int SWITCH1 = 1;
	public static final int SWITCH2 = 2;
	public static final int SWITCH3 = 3;
	
	public static final int SWITCH_OPEN = 0;
	public static final int SWITCH_CLOSE = 1;
	
	// Command that for user
	public static final int OPEN_ONE_LIGHT = 0;
	public static final int CLOSE_ONE_LIGHT = 1;
	public static final int OPEN_ALL_LIGHT = 2;
	public static final int CLOSE_ALL_LIGHT = 3;
	public static final int READ_SWITCH_STATUS = 4;
	
	//Private variables
	private static final byte FRAME_START = (byte) 0x55;
	private static final byte CONTROL_START = (byte) 0xaa;
	private static final byte FRAME_END = (byte) 0x16;
	
	private static final byte[] SUPER_ADDRESS = { (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
			(byte) 0xaa };
	
	// functions translate user command to net unicode
	public static byte []translateCommand(int comm)
	{
		byte [] b = {0x1, 0x2};
		
		
		
		return b;
	}
	
	public static int[] translateReturnStatus(byte [] bytes_arr)
	{
		int []rs={1,2};
		return rs;
	}
	private static byte [] encodeCommand()
	{
		byte [] b = {0x1, 0x2};
		return b;
	}
}
