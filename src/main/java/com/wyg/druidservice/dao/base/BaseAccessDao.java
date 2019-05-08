package com.wyg.druidservice.dao.base;

import com.wyg.druidservice.exception.AnnotationException;
import com.wyg.druidservice.util.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BaseAccessDao<T> implements BaseDaoInterface{
    @Autowired
    private BaseDao baseDao;
    public List<Map<String,Object>> handleSearch(T t) throws NoSuchMethodException, NoSuchFieldException, AnnotationException, IllegalAccessException {
        return  baseDao.handleSearch(this.getClass(),Thread.currentThread().getStackTrace()[2].getMethodName(),t);
    }
}
