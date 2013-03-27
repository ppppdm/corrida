package test.work.testcontrolborad;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class RelayBoardFrameTranslator {

	// Public Constant variables
	public static final int LIGHT0 = 0;
	public static final int LIGHT1 = 1;
	public static final int LIGHT2 = 2;
	public static final int LIGHT3 = 3;
	public static final int LIGHT4 = 4;
	public static final int LIGHT5 = 5;
	public static final int LIGHT6 = 6;
	public static final int LIGHT7 = 7;
	public static final int LIGHTMAX = 8;

	public static final int SWITCH0 = 0;
	public static final int SWITCH1 = 1;
	public static final int SWITCH2 = 2;
	public static final int SWITCH3 = 3;

	public static final int SWITCH_IS_OPEN = 0;
	public static final int SWITCH_IS_CLOSE = 1;

	// Command operator that for user
	public static final int OPEN_ONE_LIGHT = 0x0000;
	public static final int CLOSE_ONE_LIGHT = 0x1000;
	public static final int OPEN_ALL_LIGHT = 0x2000;
	public static final int CLOSE_ALL_LIGHT = 0x3000;
	public static final int READ_SWITCH_STATUS = 0x4000;
	public static final int OPEN_GROUP_LIGHT = 0x5000;
	public static final int CLOSE_GROUP_LIGHT = 0x6000;

	// Private variables
	private static final byte FRAME_START = (byte) 0x55;
	private static final byte CONTROL_START = (byte) 0xaa;
	private static final byte FRAME_END = (byte) 0x16;

	private static final byte[] SUPER_ADDRESS = { (byte) 0xaa, (byte) 0xaa,
			(byte) 0xaa, (byte) 0xaa };

	// private variables
	private static final byte CONTROL_CODE_READ_PARAMS = 0x00; // read board
																// parameter
	private static final byte CONTROL_CODE_READ_STATUS = 0x01; // read
																// light/relay
																// status
	private static final byte CONTROL_CODE_WRIT_PARAMS = 0x10; // write board
																// parameter
	private static final byte CONTROL_CODE_WRIT_STATUS = 0x11; // write
																// light/relay
																// status
	private static final byte CONTROL_CODE_READ_SWITCH = 0x22; // read switch
																// status

	/**
	 * for up-level call to formate a command, then can send this command to
	 * service. such as comm =
	 * formateCommand(RelayBoardFrameTransltor.OPEN_ONE_LIGHT,
	 * RelayBoardFrameTransltor.LIGHT0);
	 */
	public static int formateCommand(int operator, int value) {

		return operator + value;
	}
	
	/**
	 * 
	 * @param operator values the value list, value should 0 to 12
	 * @return the formated command value
	 */
	public static int formateCommand(int operator, int ... values)
	{
		
		int operand = 0;
		
		for(int value : values)
		{
			//To determine a range of values between 0 and 12
			if( value >= LIGHT0 && value < LIGHTMAX){
				operand += (int)Math.pow(2, value);
			}
		}
		
		return operator + operand;
	}
	
	public static int formateCommand(int operator, ArrayList<Integer> valuelist){
		int operand = 0;
		
		return operator + operand;
	}
	
	/*
	 * function translate operator and values to byte array
	 */
	public static byte[] translateCommand(int operator, ArrayList<Integer> valuelist)
	{
		byte controlCode = CONTROL_CODE_READ_PARAMS;
		int dataLength = 1;
		byte[] dataCode = new byte[0];
		
		
		switch(operator){
		case OPEN_ONE_LIGHT:
			controlCode = CONTROL_CODE_WRIT_STATUS;
			dataLength = 2;
			dataCode = new byte[dataLength];
			dataCode[0] = (byte)valuelist.get(0).intValue();
			dataCode[1] = 0;
			break;
		case CLOSE_ONE_LIGHT:
			controlCode = CONTROL_CODE_WRIT_STATUS;
			dataLength = 2;
			dataCode = new byte[dataLength];
			dataCode[0] = (byte)(valuelist.get(0).intValue()+0x10);
			dataCode[1] = 0;
			break;
		case OPEN_ALL_LIGHT:
			controlCode = CONTROL_CODE_WRIT_STATUS;
			dataLength = 0xd;
			dataCode = new byte[dataLength];
			for(int i = 0; i < dataLength; i++){
				dataCode[i] = (byte)0xff;
			}
			break;
		case CLOSE_ALL_LIGHT:
			dataLength = 0xd;
			dataCode = new byte[dataLength];
			for(int i = 0; i < dataLength; i++){
				dataCode[i] = (byte)0xff;
			}
			controlCode = CONTROL_CODE_WRIT_STATUS;
			
			break;
		case OPEN_GROUP_LIGHT:
			controlCode = CONTROL_CODE_WRIT_STATUS;
			dataLength = 0xd;
			dataCode = new byte[dataLength];
			for(int i = 0; i < valuelist.size(); i++){
				dataCode[valuelist.get(i)]= (byte)0xff;
			}
			break;
		case CLOSE_GROUP_LIGHT:
			controlCode = CONTROL_CODE_WRIT_STATUS;
			dataLength = 0xd;
			dataCode = new byte[dataLength];
			for(int i = 0; i < valuelist.size(); i++){
				dataCode[valuelist.get(i)]= (byte)0xff;
			}
			break;
		case READ_SWITCH_STATUS:
			break;
		default:
			break;
		}
		return encodeCommand(controlCode, dataLength, dataCode);
	}
	
	// functions translate user command to net unicode
	public static byte[] translateCommand(int command) {
		byte controlCode = CONTROL_CODE_READ_PARAMS;
		int dataLength = 1;
		byte[] dataCode = { 0x01 };

		int operator = command & 0xff00;
		int value = command & 0xff;
		switch (operator) {
		case OPEN_ONE_LIGHT:
			controlCode = CONTROL_CODE_WRIT_STATUS;
			dataLength = 2;
			dataCode = new byte[dataLength];
			dataCode[0] = (byte) (value);
			dataCode[1] = 0;
			break;
		case CLOSE_ONE_LIGHT:
			controlCode = CONTROL_CODE_WRIT_STATUS;
			dataLength = 2;
			dataCode = new byte[dataLength];
			dataCode[0] = (byte) (value + 0x10);
			dataCode[1] = 0;
			break;
		case OPEN_ALL_LIGHT:
			controlCode = CONTROL_CODE_WRIT_STATUS;
			break;
		case CLOSE_ALL_LIGHT:
			controlCode = CONTROL_CODE_WRIT_STATUS;
			break;
		case READ_SWITCH_STATUS:
			break;
		default:
			break;
		}

		return encodeCommand(controlCode, dataLength, dataCode);
	}

	public static int[] translateReturnStatus(byte[] bytes_arr) {
		int[] rs = { 1, 2 };
		return rs;
	}

	private static byte[] encodeCommand(byte ControlCode, int DataCodeLenght,
			byte[] dataCode) {
		int len;
		int indexOfControlCodeEnd;
		byte[] info;
		byte cs;

		len = DataCodeLenght + 10;
		info = new byte[len];
		indexOfControlCodeEnd = 8 + DataCodeLenght - 1;
		cs = 0;

		setBytes(info, 0, 0, FRAME_START);
		setBytesWithArray(info, 1, 4, SUPER_ADDRESS);
		setBytes(info, 5, 5, CONTROL_START);
		setBytes(info, 6, 6, ControlCode);
		setBytes(info, 7, 7, (byte) DataCodeLenght);
		setBytesWithArray(info, 8, indexOfControlCodeEnd, dataCode);

		cs = checksum(info, 0, 8 + DataCodeLenght);
		setBytes(info, indexOfControlCodeEnd + 1, indexOfControlCodeEnd + 1, cs);
		setBytes(info, indexOfControlCodeEnd + 2, indexOfControlCodeEnd + 2,
				FRAME_END);

		// System.out.println(info);
		// printBytesFormatedHex(info);
		return info;
	}

	private static byte checksum(byte[] bytes, int startIndex, int len) {

		int sum = 0;
		// System.out.printf("%x\n", sum);
		for (int i = 0; i < len; i++) {
			sum += bytes[startIndex + i] & 0xff;
			// System.out.printf("%x\n", sum);
		}
		// System.out.printf("%x\n", sum);
		return (byte) sum;
	}

	// index start from zero
	private static byte[] setBytes(byte[] bytes, int fromIndex, int toIndex,
			byte... args) {

		if (toIndex - fromIndex + 1 > args.length) {
			return null;
		}

		for (int i = fromIndex, j = 0; i <= toIndex; i++, j++) {
			bytes[i] = args[j];
		}

		return bytes;
	}

	private static byte[] setBytesWithArray(byte[] bytes, int fromIndex,
			int toIndex, byte[] args) {

		if (toIndex - fromIndex + 1 > args.length) {
			return null;
		}

		for (int i = fromIndex, j = 0; i <= toIndex; i++, j++) {
			bytes[i] = args[j];
		}
		return bytes;
	}
	

}
