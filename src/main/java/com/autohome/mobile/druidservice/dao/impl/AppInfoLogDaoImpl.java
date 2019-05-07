package com.autohome.mobile.druidservice.dao.impl;

import com.autohome.mobile.druidservice.annotation.sql.Select;
import com.autohome.mobile.druidservice.dao.AppInfoLogDao;
import com.autohome.mobile.druidservice.dao.BaseAccessDao;
import com.autohome.mobile.druidservice.dao.BaseDaoInterface;
import com.autohome.mobile.druidservice.exception.AnnotationException;
import com.autohome.mobile.druidservice.util.BaseDao;
import com.autohome.mobile.druidservice.entity.AppInfoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AppInfoLogDaoImpl extends BaseAccessDao implements AppInfoLogDao {

    @Select(value = "SELECT api,\n" +
            "sum(\"count_sum\") FILTER(WHERE errorsubtype <> 110013 and ( errorsubtype = 110007 or errorsubtype in (110005,110006,110011,110012,110014,110015,110016,110017,110018,110019,110020,110021,110022))) as errorCount,\n" +
            "sum(\"count_sum\") as allCount   FROM \"app_info_log\"\n" +
            "where errortype <> 110009 and  channel <> 'pvdebug' and retrytype  in(0,-1)\n" +
            "and \"__time\" >= TIME_PARSE('#{begin}','YYYY-MM-dd HH:mm','Asia/Shanghai') and \"__time\" < TIME_PARSE('#{end}','YYYY-MM-dd HH:mm','Asia/Shanghai')\n" +
            "#{dynamic}\n" +
            "group by api\n" +
            "order by errorCount desc limit 10000",
            resultTypes = {String.class,Long.class,Long.class},
            columnLabels = {"api","errorCount","allCount"}
    )
    @Override
    public List<Map<String, Object>> getApiList(AppInfoLog appInfoLog) throws NoSuchMethodException, AnnotationException, IllegalAccessException, NoSuchFieldException {
        return super.handleSearch(appInfoLog);
    }
    @Select("select * from \"app_info_log\" where \"__time\" >= TIME_PARSE('#{begin}','YYYY-MM-dd HH:mm','Asia/Shanghai') and \"__time\" < TIME_PARSE('#{end}','YYYY-MM-dd HH:mm','Asia/Shanghai') #{dynamic} limit 1000")
    @Override
    public List<Map<String, Object>> getSampleData(AppInfoLog appInfoLog) throws NoSuchMethodException, IllegalAccessException, AnnotationException, NoSuchFieldException {
        return super.handleSearch(appInfoLog);
    }
}
