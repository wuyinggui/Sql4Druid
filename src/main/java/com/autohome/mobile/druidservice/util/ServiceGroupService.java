package com.autohome.mobile.druidservice.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


/**
* @author yangchengwei
* @version 创建时间：2019年4月2日 下午4:44:11
* 类说明
*/
@Component
public class ServiceGroupService {
	private static Logger logger = LoggerFactory.getLogger(ServiceGroupService.class);

	private Map<String, JSONObject> groupMap = new HashMap<String, JSONObject>();

	@Value("${ip.servicegroup.url}")
	private String servicegroupUrl;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 初始化业务线配置
	 */
	@Scheduled(cron = "0 */1 * * * *")
	@Async
	public void initServiceGroupMap() {
		try {
			// cdnTerritoryUrl
			String result = restTemplate.getForObject(servicegroupUrl,String.class);

			JSONObject group_obj = JSONObject.parseObject(result);
			if (group_obj != null) {
				JSONArray groupList = group_obj.getJSONArray("data");
				if (groupList.size() > 0) {
					groupMap.clear();
					for (Object object : groupList) {
						JSONObject obj = JSONObject.parseObject(object.toString());
						groupMap.put(obj.getString("code"), obj);
					}
				}
			}

			// 更新到 ip_cdnMap

			logger.info("groupMap长度 {} ", groupMap.keySet().size());
			logger.info("groupMap {} ", JSON.toJSONString(groupMap));

		} catch (Exception e) {
			logger.error("TraceLogService_inidIPCdnMap 方法异常,异常信息:{}", e);
		}
	}

	public List<String> getPluginName(String groupCode, Integer platform) {
		List<String> pluginList = new ArrayList<String>();
		if (groupMap.isEmpty()) {
			initServiceGroupMap();
		}
		JSONArray plugins=new JSONArray();
		if (groupMap.size() > 0) {
			JSONObject groupItem = groupMap.get(groupCode);
			if(groupItem!=null){
				if (platform != null) {
					
					if (platform==1) {
						plugins=groupItem.getJSONArray("ios");
					}
					if (platform==2) {
						 plugins=groupItem.getJSONArray("android");
					}
					if (platform==3) {
						 plugins=groupItem.getJSONArray("rn");
					}
					
					
					
				}else{
					if(groupItem.getJSONArray("ios")!=null){
						plugins.addAll(groupItem.getJSONArray("ios"));
					}
					if(groupItem.getJSONArray("android")!=null){
						plugins.addAll(groupItem.getJSONArray("android"));
					}
					if(groupItem.getJSONArray("rn")!=null){
						plugins.addAll(groupItem.getJSONArray("rn"));
					}
				}
			}
	

		}
		if(plugins.size()>0){
			for (Object object : plugins) {
				pluginList.add((String)object);
			}
		}
		logger.info("getPluginName {},{},{} ", groupCode,platform,JSON.toJSONString(pluginList));
		return pluginList;
	}

}
