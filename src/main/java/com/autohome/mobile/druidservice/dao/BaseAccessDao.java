package com.autohome.mobile.druidservice.dao;

import com.autohome.mobile.druidservice.exception.AnnotationException;
import com.autohome.mobile.druidservice.util.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BaseAccessDao<T> implements BaseDaoInterface{
    @Autowired
    private BaseDao baseDao;
    public List<Map<String,Object>> handleSearch(T t) throws NoSuchMethodException, NoSuchFieldException, AnnotationException, IllegalAccessException {
        return  baseDao.handleSearch(this,Thread.currentThread().getStackTrace()[2].getMethodName(),t);
    }
}
