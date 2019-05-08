package com.wyg.druidservice.dao;

import com.wyg.druidservice.annotation.sql.Select;
import com.wyg.druidservice.dao.base.BaseDaoInterface;
import com.wyg.druidservice.entity.AppInfoLog;

import java.util.List;
import java.util.Map;


public interface AppInfoLogDao extends BaseDaoInterface {

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
    public List<Map<String,Object>> getApiList(AppInfoLog appInfoLog) throws Exception;

//    List<Map<String, Object>> dateHistogramData(AppInfoLog appInfoLog) throws Exception;
}
