package com.hlframe.modules.dc.schedule.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.common.utils.DateUtils;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.metadata.entity.DcDataSource;
import com.hlframe.modules.dc.schedule.dao.DcObjeMonitorDao;
import com.hlframe.modules.dc.schedule.entity.DcObjeMonitor;
import com.hlframe.modules.dc.schedule.service.task.DcTaskService;
import com.hlframe.modules.dc.utils.DcCommonUtils;
import com.hlframe.modules.dc.utils.Des;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/19.
 */
@Service
@Transactional(readOnly = true)
public class DcSoureceMonitorService  extends CrudService<DcObjeMonitorDao,DcObjeMonitor> implements DcTaskService{
    @Autowired
    private DcObjeMonitorDao dcObjeMonitorDao;
    @Transactional(readOnly = false)
    public DcDataResult runTask(String rest) {
        DcObjeMonitor dcObjeMonitor = new DcObjeMonitor();
        StringBuffer resultSql = new StringBuffer(64);
        resultSql.append("select COUNT(0) as count, a.id as id ,a.server_Type AS serverType,a.server_IP AS serverIP,a.server_Port AS serverPort,a.conn_Name AS connName,a.server_Url AS serverUrl,a.driver_Class AS driverClass,a.server_User AS serverUser, a.server_Pswd AS serverPswd, a.server_Name AS serverName,a.update_date AS updateDate from dc_data_source a WHERE a.del_flag='0'  group by updateDate");
        List<Map<String, Object>> resullist = DcCommonUtils.queryDataBySql(resultSql.toString());
        int count = 0;
        for (Map<String, Object> index : resullist) {
            String dbType = index.get("serverType").toString();
            String driverClass = index.get("driverClass").toString();
            String serverUrl = index.get("serverUrl").toString();
            String counts = index.get("count").toString();
            count += Integer.parseInt(counts);
            try {
            // 数据库驱动 如果为空则默认装载驱动
            if (StringUtils.isEmpty(driverClass)) {
                driverClass = DcDataSource.dbDriverMap.get(dbType);
            }
            StringBuilder connUrl = new StringBuilder(64);
            connUrl.append(DcDataSource.dbConnUrlMap.get(dbType));
            if ("dc_oracle".equalsIgnoreCase(dbType)) {
                connUrl.append(index.get("serverIP").toString()).append(":");
                connUrl.append(index.get("serverPort").toString()).append(":");
                connUrl.append(index.get("serverName").toString());

                // mysql jdbc:mysql://<host>:<port>/<database_name>
            } else if ("dc_mysql".equalsIgnoreCase(dbType)) {
                connUrl.append(index.get("serverIP").toString()).append(":");
                connUrl.append(index.get("serverPort").toString()).append("/");
                connUrl.append(index.get("serverName").toString());

                // db2 jdbc:db2://<host>:<port>/<database_name>
            } else if ("dc_db2".equalsIgnoreCase(dbType)) {
                connUrl.append(index.get("serverIP").toString()).append(":");
                connUrl.append(index.get("serverPort").toString()).append("/");
                connUrl.append(index.get("serverName").toString());

                // sqlserver
                // jdbc:sqlserver://<host>:<port>;databaseName=<database_name>
            } else if ("dc_sqlserver".equalsIgnoreCase(dbType)) {
                connUrl.append(index.get("serverIP").toString()).append(":");
                connUrl.append(index.get("serverPort").toString()).append(";");
                connUrl.append("databaseName=").append(index.get("serverName").toString());

                // postgresql jdbc:postgresql://<host>:<port>/<database_name>
            } else if ("dc_postgresql".equalsIgnoreCase(dbType)) {
                connUrl.append(index.get("serverIP").toString()).append(":");
                connUrl.append(index.get("serverPort").toString()).append("/");
                connUrl.append(index.get("serverName").toString());

                // impala 数据转换任务
                // jdbc:impala://<host>:<port>/<database_name>
            } else if ("dc_impala".equalsIgnoreCase(dbType)) {
                connUrl.append(index.get("serverIP").toString()).append(":");
                connUrl.append(index.get("serverPort").toString()).append("/");
                connUrl.append(index.get("serverName").toString());

                // hive 数据转换任务
                // jdbc:hive2://<host>:<port>/<database_name>
            } else if ("dc_hive".equalsIgnoreCase(dbType)) {
                connUrl.append(index.get("serverIP").toString()).append(":");
                connUrl.append(index.get("serverPort").toString()).append("/");
                connUrl.append(StringUtils.isNotBlank(index.get("serverName").toString()) ? index.get("serverName").toString() : "default");
            } else {
                connUrl.setLength(0);
            }

            if (connUrl.length() > 0) {
                serverUrl = connUrl.toString();
            }
            if (StringUtils.isBlank(driverClass)) {
                driverClass = DcDataSource.dbDriverMap.get(dbType);
            }
          //  Assert.hasText(driverClass, "未配置数据库连接驱动!");
                Class.forName(driverClass);
                Connection conn = DriverManager.getConnection(serverUrl, index.get("serverUser").toString(), Des.strDec(index.get("serverPswd").toString()));
                dcObjeMonitor.setType("DB数据源");
                dcObjeMonitor.setRetum("是");
                dcObjeMonitor.setTid(index.get("id").toString());
                dcObjeMonitor.preInsert();
                dcObjeMonitorDao.insert(dcObjeMonitor);
            } catch (Exception e) {
                dcObjeMonitor.setType("DB数据源");
                dcObjeMonitor.setRetum("否");
                dcObjeMonitor.setTid(index.get("id").toString());
                dcObjeMonitor.preInsert();
                if(null!=e.getMessage()){
                    if(e.getMessage().length()>200){
                        dcObjeMonitor.setAbnorma(e.getMessage().substring(0,190)+"...");
                    }else{
                        dcObjeMonitor.setAbnorma(e.getMessage());
                    }
                }
                dcObjeMonitorDao.insert(dcObjeMonitor);
                dcObjeMonitor.setAbnorma(null);

            }

        }
        DcDataResult result = new DcDataResult();
        result.setRst_flag(true);
        result.setRst_std_msg("测试任务:TestTaskService" + DateUtils.getDateTime());
        System.out.println("---->测试任务:" + result.getRst_std_msg());

        return result;
    }

}
