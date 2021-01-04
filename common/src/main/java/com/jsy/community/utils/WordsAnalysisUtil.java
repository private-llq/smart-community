package com.jsy.community.utils;

import java.util.Arrays;
import java.util.Iterator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerUtil;
import cn.hutool.extra.tokenizer.Word;

/**
 * @author chq459799974
 * @description 分词器工具
 * @since 2020-12-23 18:02
 **/
public class WordsAnalysisUtil {
	public static String[] spiltText(String text) {
		//自动根据用户引入的分词库的jar来自动选择使用的引擎
		TokenizerEngine engine = TokenizerUtil.createEngine();

		//解析文本
		//text = "这两个方法的区别在于返回值";
		Result result = engine.parse(text);
		//输出：这 两个 方法 的 区别 在于 返回 值
		String resultStr = CollUtil.join((Iterator<Word>)result, " ");
		return resultStr.split(" ");
	}
	
	public static void main(String[] args) {
		System.out.println(Arrays.toString(spiltText("东西很好吃")));
	}
}
