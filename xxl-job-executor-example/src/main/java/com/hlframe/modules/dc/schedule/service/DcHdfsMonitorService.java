package com.hlframe.modules.dc.schedule.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.common.utils.DateUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.schedule.dao.DcObjeMonitorDao;
import com.hlframe.modules.dc.schedule.entity.DcObjeMonitor;
import com.hlframe.modules.dc.schedule.service.task.DcTaskService;
import com.hlframe.modules.dc.utils.DcCommonUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/16.
 */
@Service
@Transactional(readOnly = true)
public class DcHdfsMonitorService extends CrudService<DcObjeMonitorDao,DcObjeMonitor> implements DcTaskService {
    @Autowired
    private DcObjeMonitorDao dcObjeMonitorDao;
    @Transactional(readOnly = false)
    public DcDataResult runTask(String user) {
        StringBuffer resultSql = new StringBuffer(64);
        resultSql.append("select count(0) as count,a.ID as id,a.src_hdfs_address as address, a.UPDATE_DATE as date from dc_job_transhdfs a where  a.DEL_FLAG ='0' group by date");
        List<Map<String, Object>> resultlist = DcCommonUtils.queryDataBySql(resultSql.toString());
        int count = 0;

        for (Map<String, Object> index : resultlist) {
            FileSystem fs =null;
            try{
                String address = index.get("address").toString();
                String counts = index.get("count").toString();
                count += Integer.parseInt(counts);
                Configuration conf = new Configuration();
                conf.set("dfs.socket.timeout","1000");
                conf.set("dfs.datanode.socket.read.timeout","2000");
                conf.set("ipc.client.connect.timeout","20");
                fs = FileSystem.get(new URI("hdfs://" + address), conf, "hdfs");//hdfs配置信息在dc_config中配置
                boolean f = fs.exists(new Path("/"));
                if (f) {
                    DcObjeMonitor dcObjeMonitor = new DcObjeMonitor();
                    dcObjeMonitor.setType("Hdfs");
                    dcObjeMonitor.setRetum("是");
                    dcObjeMonitor.setTid(index.get("id").toString());
                    dcObjeMonitor.preInsert();
                    dcObjeMonitorDao.insert(dcObjeMonitor);
                }
            } catch (Exception e) {
                DcObjeMonitor dcObjeMonitor = new DcObjeMonitor();
                dcObjeMonitor.setType("Hdfs");
                dcObjeMonitor.setRetum("否");
                dcObjeMonitor.setTid(index.get("id").toString());
                if(null!=e.getMessage()){
                    if(e.getMessage().length()>200){
                        dcObjeMonitor.setAbnorma(e.getMessage().substring(0,190)+"...");
                    }else{
                        dcObjeMonitor.setAbnorma(e.getMessage());
                    }
                }
                dcObjeMonitor.preInsert();
                dcObjeMonitorDao.insert(dcObjeMonitor);
                logger.error("-->runTask: ", e);
            }
        }
        DcDataResult result = new DcDataResult();
        result.setRst_flag(true);
        result.setRst_std_msg("测试任务:TestTaskService"+ DateUtils.getDateTime());
        System.out.println("---->测试任务:"+result.getRst_std_msg());
        return result;
    }
}
