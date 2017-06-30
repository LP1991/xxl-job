/********************** 版权声明 *************************
 * 文件名: UpdateLinkInfoTask.java
 * 包名: com.hlframe.modules.dc.neo4j.service.task
 * 版权:	杭州华量软件  hldc
 * 职责: this task use for resolving the dc_obj_link table to neo4j db
 ********************************************************
 *
 * 创建者：Primo   创建时间：2017/4/28
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.modules.dc.neo4j.service.task;

import com.hlframe.common.utils.DateUtils;
import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.metadata.entity.DcObjectLink;
import com.hlframe.modules.dc.metadata.service.DcObjectLinkService;
import com.hlframe.modules.dc.neo4j.service.Neo4jService;
import com.hlframe.modules.dc.schedule.service.task.DcTaskService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateLinkInfoTask implements DcTaskService {
    @Override
    public DcDataResult runTask(String taskId) throws Exception {
        DcDataResult result = new DcDataResult();
        DcObjectLinkService service = SpringContextHolder.getBean(DcObjectLinkService.class);
        Neo4jService neo4jService = SpringContextHolder.getBean(Neo4jService.class);
        DcObjectLink param = new DcObjectLink();
        List<DcObjectLink> links = service.findList(param);
        neo4jService.addDataList2Neo4j(links);
        result.setRst_flag(true);
        result.setRst_std_msg("测试任务:TestTaskService"+ DateUtils.getDateTime());
        System.out.println("--->finish runTask[TestInnerTaskService] param("+taskId+"): "+result.getRst_std_msg());
        return result;
    }
}
