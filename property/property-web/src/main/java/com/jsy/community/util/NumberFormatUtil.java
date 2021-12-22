package com.jsy.community.util;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author chq459799974
 * @Description
 * @since 2021-07-23 10:42
 **/
public class NumberFormatUtil {
	private final static char[] mChars = "0123456789ABCDEF".toCharArray();
	private final static String mHexStr = "0123456789ABCDEF";
	
	/**
	 * @Title:bytes2HexString
	 * @Description:字节数组转16进制字符串
	 * @param b
	 *            字节数组
	 * @return 16进制字符串
	 * @throws
	 */
	public static String bytes2HexString(byte[] b) {
		StringBuffer result = new StringBuffer();
		String hex;
		for (int i = 0; i < b.length; i++) {
			hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			result.append(hex.toUpperCase());
		}
		return result.toString();
	}
	/**
	 * @Title:hexString2Bytes
	 * @Description:16进制字符串转字节数组
	 * @param src  16进制字符串
	 * @return 字节数组
	 */
	public static byte[] hexString2Bytes(String src) {
		int l = src.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			ret[i] = (byte) Integer
				.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
		}
		return ret;
	}
	
	/**
	 * @Title:char2Byte
	 * @Description:字符转成字节数据char-->integer-->byte
	 * @param src
	 * @return
	 * @throws
	 */
	public static Byte char2Byte(Character src) {
		return Integer.valueOf((int)src).byteValue();
	}
	
	/**
	 * @Title:intToHexString
	 * @Description:10进制数字转成16进制
	 * @param a 转化数据
	 * @param len 占用字节数
	 * @return
	 * @throws
	 */
	public static String intToHexString(int a,int len){
		len<<=1;
		String hexString = Integer.toHexString(a);
		int b = len -hexString.length();
		if(b>0){
			for(int i=0;i<b;i++)  {
				hexString = "0" + hexString;
			}
		}
		return hexString;
	}
	public static String longToHexString(long a,int len){
		len<<=1;
		String hexString = Long.toHexString(a);
		int b = len -hexString.length();
		if(b>0){
			for(int i=0;i<b;i++)  {
				hexString = "0" + hexString;
			}
		}
		return hexString;
	}
	
	
	/**
	 * 将16进制的2个字符串进行异或运算
	 * @param strHex_X
	 * @param strHex_Y
	 * 注意：此方法是针对一个十六进制字符串一字节之间的异或运算，如对十五字节的十六进制字符串异或运算：1312f70f900168d900007df57b4884
	先进行拆分：13 12 f7 0f 90 01 68 d9 00 00 7d f5 7b 48 84
	13 xor 12-->1
	1 xor f7-->f6
	f6 xor 0f-->f9
	....
	62 xor 84-->e6
	即，得到的一字节校验码为：e6
	 * @return
	 */
	public static String xor(String strHex_X,String strHex_Y){
		//将x、y转成二进制形式
		String anotherBinary=Integer.toBinaryString(Integer.valueOf(strHex_X,16));
		String thisBinary=Integer.toBinaryString(Integer.valueOf(strHex_Y,16));
		String result = "";
		//判断是否为8位二进制，否则左补零
		if(anotherBinary.length() != 8){
			for (int i = anotherBinary.length(); i <8; i++) {
				anotherBinary = "0"+anotherBinary;
			}
		}
		if(thisBinary.length() != 8){
			for (int i = thisBinary.length(); i <8; i++) {
				thisBinary = "0"+thisBinary;
			}
		}
		//异或运算
		for(int i=0;i<anotherBinary.length();i++){
			//如果相同位置数相同，则补0，否则补1
			if(thisBinary.charAt(i)==anotherBinary.charAt(i))
				result+="0";
			else{
				result+="1";
			}
		}
		return Integer.toHexString(Integer.parseInt(result, 2));
	}
	
	
	
	/**
	 * @param msg
	 * @return 接收字节数据并转为16进制字符串
	 */
	public static String receiveHexToString(byte[] by) {
		try {
	 			/*io.netty.buffer.WrappedByteBuf buf = (WrappedByteBuf)msg;
	 			ByteBufInputStream is = new ByteBufInputStream(buf);
	 			byte[] by = input2byte(is);*/
			String str = bytes2Str(by);
			str = str.toLowerCase();
			return str;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("接收字节数据并转为16进制字符串异常");
		}
		return null;
	}
	/**
	 *  Convert byte[] to hex string.这里我们可以将byte转换成int
	 * @param src byte[] data
	 * @return hex string
	 */
	public static String bytes2Str(byte[] src){
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	public static int[] receiveHexToInt(byte[] by) {
		try {
			int arr[]=new int[by.length];
			if (by == null || by.length <= 0) {
				return null;
			}
			for (int i = 0; i < by.length; i++) {
				int v = by[i] & 0xFF;
				String tmp= Integer.toHexString(v);
				if (tmp.length() < 2) {
					tmp="0"+tmp;
				}
//		            arr[i]=Integer.parseInt(tmp);
				arr[i]=new BigInteger(tmp,16).intValue();
			}
			return arr;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("接收字节数据并转为16进制字符串异常");
		}
		return null;
	}
	
	/**
	 * "7dd",4,'0'==>"07dd"
	 * @param input 需要补位的字符串
	 * @param size 补位后的最终长度
	 * @param symbol 按symol补充 如'0'
	 * @return
	 * N_TimeCheck中用到了
	 */
	public static String fill(String input, int size, char symbol) {
		while (input.length() < size) {
			input = symbol + input;
		}
		return input;
	}
	
	public static int[] hexToByte(String hex) {
		/**
		 *
		 * 先去掉16进制字符串的空格
		 *
		 */
		hex = hex.replace(" ", "");
		/**
		 *
		 * 字节数组长度为16进制字符串长度的一半
		 *
		 */
		int byteLength = hex.length() / 2;
		int[] bytes = new int[byteLength];
		int m = 0;
		int n = 0;
		for (int i = 0; i < byteLength; i++) {
			m = i * 2 + 1;
			n = m + 1;
			int intHex = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
			bytes[i] = intHex;
		}
		return bytes;
		
	}
	
	@Deprecated
	public static String intToHexString(int n) {
		String retVal="00";
		if(n!=0) {
			retVal=Integer.toHexString(n);
		}
		if(retVal.length()%2!=0) {
			retVal="0"+retVal;
		}
		return retVal;
	}
	@Deprecated
	public static String intToHex(int n) {
		if(n==0) {
			return "00";
		}
		//StringBuffer s = new StringBuffer();
		StringBuilder sb = new StringBuilder(8);
		String a;
		char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		while(n != 0){
			sb = sb.append(b[n%16]);
			n = n/16;
		}
		a = sb.reverse().toString();
		if(a.length()==1) {
			a="0"+a;
		}
		return a;
	}
	@Deprecated
	public static String decimalToHex(int decimal) {
		if(decimal==0) {
			return "00";
		}
		String hex = "";
		while(decimal != 0) {
			int hexValue = decimal % 16;
			hex = toHexChar(hexValue) + hex;
			decimal = decimal / 16;
		}
		return  hex;
	}
	//将0~15的十进制数转换成0~F的十六进制数
	public static char toHexChar(int hexValue) {
		if(hexValue <= 9 && hexValue >= 0)
			return (char)(hexValue + '0');
		else
			return (char)(hexValue - 10 + 'A');
	}
	/**
	 * 字符串转换成十六进制字符串
	 * @param str String 待转换的ASCII字符串
	 * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
	 */
	public static String str2HexStr(String str){
		StringBuilder sb = new StringBuilder();
		byte[] bs = str.getBytes();
		
		for (int i = 0; i < bs.length; i++){
			sb.append(mChars[(bs[i] & 0xFF) >> 4]);
			sb.append(mChars[bs[i] & 0x0F]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}
	
	/**
	 * 十六进制字符串转换成 ASCII字符串
	 * @param str String Byte字符串
	 * @return String 对应的字符串
	 */
	public static String hexStr2Str(String hexStr){
		hexStr = hexStr.toString().trim().replace(" ", "").toUpperCase(Locale.US);
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int iTmp = 0x00;;
		
		for (int i = 0; i < bytes.length; i++){
			iTmp = mHexStr.indexOf(hexs[2 * i]) << 4;
			iTmp |= mHexStr.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (iTmp & 0xFF);
		}
		return new String(bytes);
	}
	
	/**
	 * bytes转换成十六进制字符串
	 * @param b byte[] byte数组
	 * @param iLen int 取前N位处理 N=iLen
	 * @return String 每个Byte值之间空格分隔
	 */
	public static String byte2HexStr(byte[] b, int iLen){
		StringBuilder sb = new StringBuilder();
		for (int n=0; n<iLen; n++){
			sb.append(mChars[(b[n] & 0xFF) >> 4]);
			sb.append(mChars[b[n] & 0x0F]);
			sb.append(' ');
		}
		return sb.toString().trim().toUpperCase(Locale.US);
	}
	
	/**
	 * bytes字符串转换为Byte值
	 * @param src String Byte字符串，每个Byte之间没有分隔符(字符范围:0-9 A-F)
	 * @return byte[]
	 */
	public static byte[] hexStr2Bytes(String src){
		/*对输入值进行规范化整理*/
		src = src.trim().replace(" ", "").toUpperCase(Locale.US);
		//处理值初始化
		int m=0,n=0;
		int iLen=src.length()/2; //计算长度
		byte[] ret = new byte[iLen]; //分配存储空间
		
		for (int i = 0; i < iLen; i++){
			m=i*2+1;
			n=m+1;
			ret[i] = (byte)(Integer.decode("0x"+ src.substring(i*2, m) + src.substring(m,n)) & 0xFF);
		}
		return ret;
	}
	
	/**
	 * String的字符串转换成unicode的String
	 * @param strText String 全角字符串
	 * @return String 每个unicode之间无分隔符
	 * @throws Exception
	 */
	public static String strToUnicode(String strText)
		throws Exception
	{
		char c;
		StringBuilder str = new StringBuilder();
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++){
			c = strText.charAt(i);
			intAsc = (int) c;
			strHex = Integer.toHexString(intAsc);
			if (intAsc > 128)
				str.append("\\u");
			else // 低位在前面补00
				str.append("\\u00");
			str.append(strHex);
		}
		return str.toString();
	}
	
	/**
	 * unicode的String转换成String的字符串
	 * @param hex String 16进制值字符串 （一个unicode为2byte）
	 * @return String 全角字符串
	 * @see CHexConver.unicodeToString("\\u0068\\u0065\\u006c\\u006c\\u006f")
	 */
	public static String unicodeToString(String hex){
		int t = hex.length() / 6;
		int iTmp = 0;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < t; i++){
			String s = hex.substring(i * 6, (i + 1) * 6);
			// 将16进制的string转为int
			iTmp = (Integer.valueOf(s.substring(2, 4), 16) << 8) | Integer.valueOf(s.substring(4), 16);
			// 将int转换为字符
			str.append(new String(Character.toChars(iTmp)));
		}
		return str.toString();
	}
	public static int hexStrToInt(String hex) {
		return new BigInteger(hex,16).intValue();
	}
	public static long hexStrToLong(String hex) {
		return new BigInteger(hex,16).longValue();
	}
	public static String hexStrToStr(String hex) {
		return String.valueOf(hexStrToInt(hex));
	}
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
	public static void main(String args[]) {
//	    	String hex="696910000003004c0138ff6e064242313908260457000000000000000000000000000000000000000001011e012c2f5b9fa803ee00000000000000000000000000000000000000000000";
//	    	byte[] by= {105, 105, 16, 0, 0, 3, 0, 76, 1, 56, -1,  110, 6, 66, 66, 49, 57, 8, 38, 4, 87, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 30, 1, 44, 47, 91, -97, -88, 3, -18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//	    	int k[]=receiveHexToInt(by);
		
		System.out.println(hexStrToInt("01140DF5"));
		System.out.println(hexStrToInt("01140df5"));
		
		System.out.println(new BigInteger("5BA6D8E7",16));
		System.out.println(sdf.format(new BigInteger("5BA6D8E7",16)));
		
	}
}
