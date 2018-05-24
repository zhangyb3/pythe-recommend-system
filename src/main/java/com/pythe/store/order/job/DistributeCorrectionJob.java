package com.pythe.store.order.job;

import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.pythe.common.utils.HttpClientUtil;




public class DistributeCorrectionJob extends QuartzJobBean {

	

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		
//		HttpClientUtil.doGet("https://check.pythe.cn/pythe-teacher-task/rest/correction/task/distribute");
		
	}

}
