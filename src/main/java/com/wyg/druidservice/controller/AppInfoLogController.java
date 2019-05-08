package com.wyg.druidservice.controller;

import com.wyg.druidservice.entity.AppInfoLog;
import com.wyg.druidservice.service.AppInfoLogService;
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


//    public Map<String,Object> dateHistogramData(AppInfoLog appInfoLog){
//        Map<String,Object> result = new HashMap<>();
//        try {
//            List<Map<String,Object>> infoLogList = appInfoLogService.dateHistogramData(appInfoLog);
//            result.put("success",true);
//            result.put("data",infoLogList);
//        }catch (Exception e){
//            e.printStackTrace();
//            result.put("success",false);
//            result.put("errorMsg",e.getMessage());
//        }
//        return result;
//    }
}
