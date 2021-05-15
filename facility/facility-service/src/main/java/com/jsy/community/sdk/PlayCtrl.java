/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * HCNetSDK.java
 *
 * Created on 2009-9-14, 19:31:34
 */

/**
 * @author Xubinfeng
 */

package com.jsy.community.sdk;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.win32.StdCallLibrary;


//播放库函数声明,PlayCtrl.dll
public interface PlayCtrl extends StdCallLibrary {
	PlayCtrl INSTANCE = (PlayCtrl) Native.loadLibrary(System.getProperty("user.dir") + "\\facility\\facility-service\\src\\main\\java\\com\\jsy\\community\\sdk\\PlayCtrl.dll",PlayCtrl.class);
	
	public static final int STREAME_REALTIME = 0;
	public static final int STREAME_FILE = 1;
	
	boolean PlayM4_GetPort(NativeLongByReference nPort);
	
	boolean PlayM4_OpenStream(NativeLong nPort, ByteByReference pFileHeadBuf, int nSize, int nBufPoolSize);
	
	boolean PlayM4_InputData(NativeLong nPort, ByteByReference pBuf, int nSize);
	
	boolean PlayM4_CloseStream(NativeLong nPort);
	
	boolean PlayM4_SetStreamOpenMode(NativeLong nPort, int nMode);
	
	boolean PlayM4_Play(NativeLong nPort, HWND hWnd);
	
	boolean PlayM4_Stop(NativeLong nPort);
	
	boolean PlayM4_SetSecretKey(NativeLong nPort, NativeLong lKeyType, String pSecretKey, NativeLong lKeyLen);
}

