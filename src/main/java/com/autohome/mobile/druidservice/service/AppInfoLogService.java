package com.autohome.mobile.druidservice.service;

import com.autohome.mobile.druidservice.dao.AppInfoLogDao;
import com.autohome.mobile.druidservice.entity.AppInfoLog;
import com.autohome.mobile.druidservice.exception.AnnotationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AppInfoLogService{
    @Autowired
    private AppInfoLogDao appInfoLogDao;


    public List<Map<String,Object>> getApiList(AppInfoLog appInfoLog) throws NoSuchMethodException, AnnotationException, IllegalAccessException, NoSuchFieldException {

        return appInfoLogDao.getApiList(appInfoLog);
    }

    public List<Map<String, Object>> getSampleData(AppInfoLog appInfoLog) throws NoSuchMethodException, NoSuchFieldException, AnnotationException, IllegalAccessException {
        return appInfoLogDao.getSampleData(appInfoLog);
    }
}
