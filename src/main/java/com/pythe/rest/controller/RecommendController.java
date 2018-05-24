package com.pythe.rest.controller;

import java.net.URLDecoder;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.params.HttpParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.alibaba.fastjson.JSONObject;
import com.pythe.common.pojo.PytheResult;
import com.pythe.common.utils.ExceptionUtil;
import com.pythe.common.utils.FactoryUtils;
import com.pythe.rest.service.RecommendService;
import com.pythe.rest.service.TestService;

@SessionAttributes({"token","name","password"})
@Controller
public class RecommendController {

	
	
	
	@Autowired 
	private HttpServletRequest httpServletRequest;
	
	@Autowired  
	private HttpSession session;
	

	
	@Autowired
	private RecommendService recommendService;
	
	
	@RequestMapping(value = "/recommend/essay/everyday", method = RequestMethod.POST)
	@ResponseBody
	public PytheResult recommendTest(
//			@RequestHeader(value="test") String test,
			@RequestBody String parameters
			) throws Exception {
		
		
		return recommendService.recommendEssayEveryday(parameters);

		
		
	}
	

}
