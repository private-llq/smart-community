package com.jsy.community.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Hashtable;

/**
 * @author DKS
 * @description 生成二维码工具类
 * @since 2021/8/16  14:49
 **/
public class QRCodeGenerator {
	
	public static void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
		Path path = FileSystems.getDefault().getPath(filePath);
//		Path file = new File(filePath).toPath();
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
	}
	
	public static byte[] generateQRCode(String text, int width, int height) throws Exception {
		String format = "png";
		Hashtable hints = new Hashtable();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
		File outputFile = new File("new.png");
		MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile);
		return toByteArray(new File("new.png"));
	}



	public static byte[] toByteArray(File imageFile) throws Exception {
		BufferedImage img = ImageIO.read(imageFile);
		ByteArrayOutputStream buf = new ByteArrayOutputStream((int) imageFile.length());
		try {
			ImageIO.write(img, "jpg", buf);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return buf.toByteArray();
	}
	
	public static void main(String[] args) throws Exception {
		String text = "https://home.firefoxchina.cn/";
//		String text = "https://192.168.12.188:8081/?id=1";
		/*
		 * 图片的宽度和高度
		 */
		int width = 100;
		int height = 100;
		String path="E:\\3.jpg";

		// 图片的格式
		String format = "png";
		QRCodeGenerator.generateQRCodeImage(text,width,height,path);
		System.out.println("ok");



//
//		Hashtable hints = new Hashtable();
//		// 定义字符集编码格式
//		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//		// 最终生成 参数列表 （1.内容 2.格式 3.宽度 4.高度 5.二维码参数）
//		BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
//		File outputFile = new File("new.png");

//		new MockMultipartFile().enc
//		MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile);
//		byte[] b = toByteArray(new File("new.png"));
//		MinioUtils.uploadDeposit(b, BusinessConst.DEPOSIT_QR_CODE);

	}
}
