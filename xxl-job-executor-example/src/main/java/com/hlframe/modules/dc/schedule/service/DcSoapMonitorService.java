package com.hlframe.modules.dc.schedule.service;

import com.hlframe.common.utils.DateUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.schedule.dao.DcObjeMonitorDao;
import com.hlframe.modules.dc.schedule.entity.DcObjeMonitor;
import com.hlframe.modules.dc.schedule.service.task.DcTaskService;
import com.hlframe.modules.dc.utils.DcCommonUtils;
import com.hlframe.modules.dc.utils.DcStringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by 00052 on 2017/6/17.
 */
@Service
public class DcSoapMonitorService implements DcTaskService {
    @Autowired
    private DcObjeMonitorDao dcObjeMonitorDao;

    @Override
    public DcDataResult runTask(String taskId){
        DcObjeMonitor dcObjeMonitor = null;
        DcDataResult result = new DcDataResult();
        List<Map<String,Object>> obj = new ArrayList<>();
        StringBuffer sqlBuf = new StringBuffer();
        //job_type默认为2soap tar_type现在是默认为mysql
        sqlBuf.append("select a.id as 'id', a.job_name as 'jobName', a.job_desc as 'jobDesc',  a.log_dir as 'logDir', a.STATUS as 'status', a.REMARKS AS 'remarks', a.SORT_NUM as 'sortNum', a.CREATE_BY AS \"createBy.id\", a.CREATE_DATE as \"createDate\", a.UPDATE_BY AS \"updateBy.id\", a.UPDATE_DATE AS \"updateDate\", b.ws_path as wsPath, b.ws_namespace as wsNamespace, b.ws_method as wsMethod, b.rest_url as restUrl, b.rest_type as restType, b.rest_contentType AS restContentType, b.params as params, c.tarName as tarName, c.create_flag as createFlag, c.connId as connId, c.schemaName as schemaName FROM DC_JOB_TransIntf a LEFT JOIN DC_JOB_TransIntf_src b on a.id = b.jobId LEFT JOIN DC_JOB_TransIntf_tar c on a.id = c.jobId LEFT JOIN sys_user u ON u.id = a.create_by LEFT JOIN sys_office o ON o.id = u.office_id where a.job_type='2'");
        obj = DcCommonUtils.queryDataBySql(sqlBuf.toString());
        for(int i =0;i<obj.size();i++){
            String pid =  String.valueOf(obj.get(i).get("id"));
            try{
            JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
            dcObjeMonitor = new DcObjeMonitor();
            //url为调用webService的wsdl地址
            Client client = dcf.createClient(String.valueOf(obj.get(i).get("wsPath")));
            String nameSpace = String.valueOf(obj.get(i).get("wsNamespace"));
            String param =  String.valueOf(obj.get(i).get("params"));
            String wsMethod =  String.valueOf(obj.get(i).get("wsMethod"));
            if (com.hlframe.common.utils.StringUtils.isBlank(nameSpace)){
                //默认获取目标命名空间
                Endpoint endpoint = client.getEndpoint();
                nameSpace = endpoint.getService().getName().getNamespaceURI();
            }
            QName name=new QName(nameSpace, wsMethod);
//        构建参数  参数类型解析, 默认为string类型  int:10&string:abc&date:2017-04-25  暂不考虑复杂对象类型...
            List<Object> params = new LinkedList<Object>();
            if (DcStringUtils.isNotBlank(param)){
                String fieldType = null;
                for(String str:param.split("&")){
                    if(str.indexOf(":")>0){
                        fieldType = str.split(":")[0];
                        //根据:号进行解析
                        if("int".equalsIgnoreCase(fieldType)){      //整数型
                            params.add(Integer.parseInt(str.split(":")[1]));
                        }else if("date".equalsIgnoreCase(fieldType)){   //日期型
                            params.add(DateUtils.parseDate(str.split(":")[1]));
                        }else if("float".equalsIgnoreCase(fieldType)){  //float
                            params.add(Float.parseFloat(str.split(":")[1]));
                        }else if("double".equalsIgnoreCase(fieldType)){ //double
                            params.add(Double.parseDouble(str.split(":")[1]));
                        }else{  //默认 字符串
                            params.add(str.split(":")[1]);
                        }
                    }else{
                        params.add(str);
                    }
                }
            }
            Object[] objects=client.invoke(name, params.toArray());
            if(DcStringUtils.isNotEmpty(objects[0].toString())){
                dcObjeMonitor.preInsert();
                dcObjeMonitor.setTid(pid);
                dcObjeMonitor.setType("soap");
                dcObjeMonitor.setRetum("是");
            }else{
                dcObjeMonitor.preInsert();
                dcObjeMonitor.setTid(pid);
                dcObjeMonitor.setType("soap");
                dcObjeMonitor.setRetum("否");
                dcObjeMonitor.setAbnorma("接口信息不存在");
                }
            dcObjeMonitorDao.insert(dcObjeMonitor);
            }catch(Exception e){
                dcObjeMonitor = new DcObjeMonitor();
                dcObjeMonitor.preInsert();
                dcObjeMonitor.setTid(pid);
                dcObjeMonitor.setType("soap");
                dcObjeMonitor.setRetum("否");
                dcObjeMonitor.setAbnorma(e.getMessage());
                dcObjeMonitorDao.insert(dcObjeMonitor);
            }
        }
        result.setRst_flag(true);
        result.setRst_std_msg("测试任务:Soap连接信息生成");
        return result;
    }

    public static void main(String[] args){
        try {
            new DcSoapMonitorService().runTask("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
