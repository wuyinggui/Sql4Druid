package com.autohome.mobile.druidservice.dao;

import com.autohome.mobile.druidservice.entity.AppInfoLog;
import com.autohome.mobile.druidservice.exception.AnnotationException;

import java.util.List;
import java.util.Map;


public interface AppInfoLogDao {
    public List<Map<String,Object>> getApiList(AppInfoLog appInfoLog) throws NoSuchMethodException, AnnotationException, IllegalAccessException, NoSuchFieldException;

    List<Map<String, Object>> getSampleData(AppInfoLog appInfoLog) throws NoSuchMethodException, IllegalAccessException, AnnotationException, NoSuchFieldException;
}
