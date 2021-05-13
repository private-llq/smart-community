package com.jsy.community.util.facility;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

interface CLibrary extends Library {
	CLibrary INSTANCE = (CLibrary)Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"),	CLibrary.class);
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 打印函数
	 * @Date 2021/5/11 9:24
	 * @Param [format, args]
	 **/
	void printf(String format, Object... args);
}
