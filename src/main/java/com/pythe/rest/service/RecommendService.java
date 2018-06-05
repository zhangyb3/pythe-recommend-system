package com.pythe.rest.service;

import com.pythe.common.pojo.PytheResult;

public interface RecommendService {

	void executeInstantRecommendOnce() throws Exception;

	PytheResult recommendEssayEveryday(String parameters) throws Exception;

	PytheResult recommendEssayEverytime(String parameters) throws Exception;

	
	
}
