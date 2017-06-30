package com.hlframe.modules.dc.schedule.service.task;

import com.hlframe.common.utils.DateUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.utils.DcEsUtil;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import com.xxl.job.core.biz.model.ReturnT;
import org.elasticsearch.client.Client;
import org.springframework.stereotype.Service;

/**
 * @类名: InitESMappingService
 * @职责说明: use for init es mapping
 * @创建者: Primo
 * @创建时间: 2017/4/11
 */
@Service
public class InitESMappingService implements DcTaskService{

    @Override
    public DcDataResult runTask(String taskId) throws Exception {
        DcDataResult result = new DcDataResult();
        String index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
        Client client = DcEsUtil.getEsClient();
        try {
            DcEsUtil.deleteIndex(client,index);
        }catch (Exception e){
            e.printStackTrace();
        }
        DcEsUtil.creataIndex(client,index);
        DcEsUtil.createCatalogMapping(client,index,"table");
        DcEsUtil.createCatalogMapping(client,index,"file");
        DcEsUtil.createCatalogMapping(client,index,"folder");
        DcEsUtil.createCatalogMapping(client,index,"interface");
        result.setRst_flag(true);
        result.setRst_std_msg("测试任务:InitESMappingService"+ DateUtils.getDateTime());
        return result;
    }

    public static void main(String[] args){
        try {
            new InitESMappingService().runTask("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
