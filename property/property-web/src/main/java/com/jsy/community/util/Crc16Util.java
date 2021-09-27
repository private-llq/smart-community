package com.jsy.community.util;

import com.jsy.community.api.PropertyException;

import javax.management.JMException;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * 基于Modbus CRC16的校验算法工具类
 */
public class Crc16Util {

    /**
     * 获取源数据和验证码的组合byte数组
     *
     * @param strings 可变长度的十六进制字符串
     * @return
     */
    public static byte[] getData(ArrayList<String> strings) {
        byte[] data = new byte[]{};
        for (int i = 0; i < strings.size(); i++) {
            int x = Integer.parseInt(strings.get(i), 16);
            byte n = (byte) x;
            byte[] buffer = new byte[data.length + 1];
            byte[] aa = {n};
            System.arraycopy(data, 0, buffer, 0, data.length);
            System.arraycopy(aa, 0, buffer, data.length, aa.length);
            data = buffer;
        }
        return getData(data);
    }

    /**
     * 获取源数据和验证码的组合byte数组
     *
     * @param aa 字节数组
     * @return
     */
    private static byte[] getData(byte[] aa) {
        byte[] bb = getCrc16(aa);
        byte[] cc = new byte[aa.length + bb.length];
        System.arraycopy(aa, 0, cc, 0, aa.length);
        System.arraycopy(bb, 0, cc, aa.length, bb.length);
        return cc;
    }

    /**
     * 获取验证码byte数组，基于Modbus CRC16的校验算法
     */
    private static byte[] getCrc16(byte[] arr_buff) {
        int len = arr_buff.length;

        // 预置 1 个 16 位的寄存器为十六进制FFFF, 称此寄存器为 CRC寄存器。
        int crc = 0xFFFF;
        int i, j;
        for (i = 0; i < len; i++) {
            // 把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (arr_buff[i] & 0xFF));
            for (j = 0; j < 8; j++) {
                // 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位
                if ((crc & 0x0001) > 0) {
                    // 如果移出位为 1, CRC寄存器与多项式A001进行异或
                    crc = crc >> 1;
                    crc = crc ^ 0xA001;
                } else
                    // 如果移出位为 0,再次右移一位
                    crc = crc >> 1;
            }
        }
        return intToBytes(crc);
    }

    /**
     * 将int转换成byte数组，低位在前，高位在后
     * 改变高低位顺序只需调换数组序号
     */
    private static byte[] intToBytes(int value) {
        byte[] src = new byte[2];
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 将字节数组转换成十六进制字符串
     */
    public static String byteTo16String(byte[] data) {
        StringBuffer buffer = new StringBuffer();
        for (byte b : data) {
            buffer.append(byteTo16String(b));
        }
        return buffer.toString();
    }

    /**
     * 将字节转换成十六进制字符串
     * int转byte对照表
     * [128,255],0,[1,128)
     * [-128,-1],0,[1,128)
     */
    public static String byteTo16String(byte b) {
        StringBuffer buffer = new StringBuffer();
        int aa = (int) b;
        if (aa < 0) {
            buffer.append(Integer.toString(aa + 256, 16) + " ");
        } else if (aa == 0) {
            buffer.append("00 ");
        } else if (aa > 0 && aa <= 15) {
            buffer.append("0" + Integer.toString(aa, 16) + " ");
        } else if (aa > 15) {
            buffer.append(Integer.toString(aa, 16) + " ");
        }
        return buffer.toString();
    }


    public static ArrayList crc16(String str) {
        ArrayList strings = new ArrayList<String>();
        for (int i = 0; i < str.length(); i++) {
            String s = "";
            if (i % 2 != 0) {
                s = str.substring(i - 1, i + 1);
                strings.add(s);
            }
        }
        return strings;
    }


    public static String getValue(String value) {
        ArrayList arrayList = crc16(value);
        byte[] dd = Crc16Util.getData(arrayList);
        String str = Crc16Util.byteTo16String(dd).toUpperCase();
        String replace = str.replace(" ", "");
        System.out.println("str" + replace);
        return replace;
    }

    public static String getUltimatelyValue(String str)  {
        String value = null;
        try {
            value = UtilStringTo16GB2312.enUnicode(str);
        } catch (UnsupportedEncodingException e) {
            throw  new PropertyException(500,"机器码异常");
        }
        String value1 = Crc16Util.getValue("0064FFFF621B001500000015000300FF000000000000000800" + value);
        return value1;
    }

    /*数字换行成0000模式*/
    public static String getStandard(Integer value){
        String s = value.toString();
        int m = value.toString().length();
        if (m == 1) {
            s = "000" + s;
        } else if (m == 2) {
            s = "00" + s;
        } else if (m == 3) {
            s = "0" + s;
        } else if (m == 4) {
            s = s;
        }
        return  s;
    }





}