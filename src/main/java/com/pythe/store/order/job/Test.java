package com.pythe.store.order.job;

import com.alibaba.fastjson.JSONObject;
import com.pythe.common.utils.HttpClientUtil;
import com.pythe.common.utils.JsonUtils;

public class Test {
	public static void main(String[] args) {
        String url = "http://api.CuoBieZi.net/spellcheck/json_check/json_phrase";

        String sentence = "他突然间痛酷";
        JSONObject json = new JSONObject();
        json.put("content",sentence);
        json.put("username","yangxiaoen");
        json.put("biz_type","show");
        json.put("mode","advanced");
        
        String str = HttpClientUtil.doPostJson(url, JsonUtils.objectToJson(json));
        System.out.println(str);
	}
}
