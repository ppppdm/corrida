package test.work.testcontrolborad;

public class RelayBoardFrameTranslator {
	
	//Public Constant variables
	public static int LIGHT0 = 0;
	public static int LIGHT1 = 1;
	public static int LIGHT2 = 2;
	public static int LIGHT3 = 3;
	public static int LIGHT4 = 4;
	public static int LIGHT5 = 5;
	public static int LIGHT6 = 6;
	public static int LIGHT7 = 7;
	
	public static int SWITCH0 = 0;
	public static int SWITCH1 = 1;
	public static int SWITCH2 = 2;
	public static int SWITCH3 = 3;
	
	public static int SWITCH_OPEN = 0;
	public static int SWITCH_CLOSE = 1;
	
	// Command that for user
	public static int OPEN_ONE_LIGHT = 0;
	public static int CLOSE_ONE_LIGHT = 1;
	public static int OPEN_ALL_LIGHT = 2;
	public static int CLOSE_ALL_LIGHT = 3;
	public static int READ_SWITCH_STATUS = 4;
	
	//Private variables
	private static byte FRAME_START = (byte) 0x55;
	private static byte CONTROL_START = (byte) 0xaa;
	private static byte FRAME_END = (byte) 0x16;
	
	private static byte[] SUPER_ADDRESS = { (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
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
