package com.wyg.druidservice.service;

import com.wyg.druidservice.dao.AppInfoLogDao;
import com.wyg.druidservice.entity.AppInfoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AppInfoLogService{
    @Autowired
    private AppInfoLogDao appInfoLogDao;


    public List<Map<String,Object>> getApiList(AppInfoLog appInfoLog) throws Exception {
        return appInfoLogDao.getApiList(appInfoLog);
    }

    public List<Map<String, Object>> dateHistogramData(AppInfoLog appInfoLog) throws Exception {
        return appInfoLogDao.dateHistogramData(appInfoLog);
    }
}
