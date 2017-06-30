package com.hlframe.modules.dc.schedule.service.task;

import com.hlframe.common.utils.DateUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import com.hlframe.modules.dc.metadata.service.DcObjectMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @类名: UpdateESInfoTask
 * @职责说明: This task will select out the last five minutes' obj and update them to es
 * @创建者: Primo
 * @创建时间: 2017/3/22
 */
@Service
public class UpdateESInfoTask implements DcTaskService{
    @Autowired
    private DcObjectMainService dcObjectMainService;
    private int minute = -6;
    @Override
    public DcDataResult runTask(String taskId) throws Exception {
        DcDataResult result = new DcDataResult();
        try {
            DcObjectMain param = new DcObjectMain();
            Date d = new Date();
            d = DateUtils.addMinutes(d, minute);
            param.setCreateDate(d);
            List<DcObjectMain> ids = dcObjectMainService.findList(param);
            dcObjectMainService.updateData2Es(ids);
            result.setRst_flag(true);
        }catch (Exception e){
            e.printStackTrace();
            result.setRst_flag(false);
        }
        return result;
    }
}
