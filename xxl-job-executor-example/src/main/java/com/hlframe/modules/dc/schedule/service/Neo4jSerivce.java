package com.hlframe.modules.dc.schedule.service;

import com.hlframe.common.utils.DateUtils;
import com.hlframe.common.utils.IdGen;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.schedule.dao.DcObjeMonitorDao;
import com.hlframe.modules.dc.schedule.entity.DcObjeMonitor;
import com.hlframe.modules.dc.schedule.service.task.DcTaskService;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

//import static com.hlframe.modules.dc.utils.EsClientProvider.logger;

/**
 * Created by Administrator on 2017/6/23.
 */
@Service
@Transactional(readOnly = true)
public class Neo4jSerivce implements DcTaskService {
    public static Client client = Client.create();
    @Autowired
    private DcObjeMonitorDao dcObjeMonitorDao;

    @Transactional(readOnly = false)
    public DcDataResult runTask(String Sql) {
        DcDataResult resul = new DcDataResult();
        DcObjeMonitor dcObjeMonitor = new DcObjeMonitor();
        try {
            //restful 地址
            URI u = new URI(DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J");
            WebResource resource = client.resource(u);

            String result = resource.get(String.class);
            if (result.equals("healthy")) {
                dcObjeMonitor.setType(DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J");
                dcObjeMonitor.setRetum("是");
                dcObjeMonitor.setTid(IdGen.uuid());
                dcObjeMonitor.preInsert();
                dcObjeMonitorDao.insert(dcObjeMonitor);
                resul.setRst_flag(true);
                resul.setRst_std_msg("测试任务:TestTaskService" + DateUtils.getDateTime());
                System.out.println("---->测试任务:" + resul.getRst_std_msg());
            }
        } catch (Exception e) {
            dcObjeMonitor.setType(DcPropertyUtils.getProperty("queryHive.restServer.url"));
            dcObjeMonitor.setRetum("否");
            dcObjeMonitor.setTid(IdGen.uuid());
            if (null != e.getMessage()) {
                if (e.getMessage().length() > 200) {
                    dcObjeMonitor.setAbnorma(e.getMessage().substring(0, 190) + "...");
                } else {
                    dcObjeMonitor.setAbnorma(e.getMessage());
                }
            }
            dcObjeMonitor.preInsert();
            dcObjeMonitorDao.insert(dcObjeMonitor);
//            logger.error("-->createIndex failed.", e);
            System.out.println("---->测试任务:" + resul.getRst_std_msg());
            resul.setRst_flag(false);
            resul.setRst_err_msg(e.getMessage());

        }
        return resul;
    }
}
