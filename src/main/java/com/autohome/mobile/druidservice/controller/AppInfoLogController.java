package com.autohome.mobile.druidservice.controller;

import com.autohome.mobile.druidservice.entity.AppInfoLog;
import com.autohome.mobile.druidservice.service.AppInfoLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AppInfoLogController {
    @Autowired
    private AppInfoLogService appInfoLogService;
    @GetMapping("/appinfo/list")
    public Map<String,Object> getInfoList(AppInfoLog appInfoLog){
        Map<String,Object> result = new HashMap<>();
        try {
            List<Map<String,Object>> infoLogList = appInfoLogService.getApiList(appInfoLog);
            result.put("success",true);
            result.put("data",infoLogList);
        }catch (Exception e){
            e.printStackTrace();
            result.put("success",false);
            result.put("errorMsg",e.getMessage());
        }
        return result;
    }
    @GetMapping("/appinfo/sample")
    public Map<String,Object> getSampleData(AppInfoLog appInfoLog){
        Map<String,Object> result = new HashMap<>();
        try {
            List<Map<String,Object>> infoLogList = appInfoLogService.getSampleData(appInfoLog);
            result.put("success",true);
            result.put("data",infoLogList);
        }catch (Exception e){
            e.printStackTrace();
            result.put("success",false);
            result.put("errorMsg",e.getMessage());
        }
        return result;
    }
}
