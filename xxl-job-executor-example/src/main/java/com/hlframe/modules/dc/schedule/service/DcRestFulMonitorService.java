package com.hlframe.modules.dc.schedule.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.common.utils.DateUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntfSrc;
import com.hlframe.modules.dc.schedule.dao.DcObjeMonitorDao;
import com.hlframe.modules.dc.schedule.entity.DcObjeMonitor;
import com.hlframe.modules.dc.schedule.service.task.DcTaskService;
import com.hlframe.modules.dc.utils.DcCommonUtils;
import com.hlframe.modules.dc.utils.DcStringUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/17.
 */
@Service
@Transactional(readOnly = true)
public class DcRestFulMonitorService extends CrudService<DcObjeMonitorDao,DcObjeMonitor> implements DcTaskService {
    @Autowired
    private DcObjeMonitorDao dcObjeMonitorDao;

    @Transactional(readOnly = false)
    public DcDataResult runTask(String rest) {
        StringBuffer resultSql = new StringBuffer(64);
        resultSql.append("select COUNT(0) as count, a.jobId as jobid,a.rest_url as resturl,a.rest_type AS restType,a.rest_contentType AS restContentType,a.params AS params ,b.UPDATE_DATE AS updateDate,b.job_Type AS 'jobType',b.job_name AS jobname FROM DC_JOB_TransIntf_src a LEFT JOIN  DC_JOB_TransIntf b ON(a.jobid=b.id) WHERE b.del_flag='0' AND b.job_Type ='1'  group by updateDate ");
        List<Map<String, Object>> resullist = DcCommonUtils.queryDataBySql(resultSql.toString());
        int count = 0;
        for (Map<String, Object> index : resullist) {
            String jobname = index.get("jobname").toString();
            String counts = index.get("count").toString();
            count += Integer.parseInt(counts);

            try{
                URI url = new URI(index.get("resturl").toString());
                Client client = Client.create();
                WebResource resource = client.resource(url);

            if(jobname.startsWith("bd_wifi_page_")){
                StringBuilder restWsData = new StringBuilder(512);

                    if(DcJobTransIntfSrc.RESTFUL_TYPE_GET.equals(index.get("restType").toString())){	//get方式 暂不考虑分页,项目中碰到实际数据再说
                        resource.get(String.class);
                    }else if(DcJobTransIntfSrc.RESTFUL_TYPE_POST.equals(index.get("restType").toString())){	//post方式 博地项目中存在数据分页情况
                        //构建form参数
                        MultivaluedMapImpl params = new MultivaluedMapImpl();
                        int startNum = 0, pageSize=20;   //默认起始数据索引, 每页数据量
                        if (DcStringUtils.isNotBlank(index.get("params").toString())){
                            for(String str: index.get("params").toString().split("&")){
                                if(str.indexOf("=")>0){
                                    //根据=号进行解析
                                    params.add(str.split("=")[0], str.split("=")[1]);
                                    if(str.indexOf("pageSize")>-1){
                                        pageSize = Integer.parseInt(str.split("=")[1]);
                                    }
                                    if(str.indexOf("start")>-1){
                                        startNum = Integer.parseInt(str.split("=")[1]);
                                    }
                                }
                            }
                        }
                        String contectType = DcStringUtils.isNotBlank(index.get("restContentType").toString())?index.get("restContentType").toString(): MediaType.APPLICATION_FORM_URLENCODED;
                        String restJson = resource.type(contectType).post(String.class, params);
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode node = mapper.readTree(restJson);
                        Assert.isTrue("0".equals(node.get("code").toString()), node.get("description").toString());
                        if(node.has("data")){
                            JSONArray array = new JSONArray();
                            restWsData.append(node.get("data"));
                        }
                        if(node.has("totalSize")){
                            int totalSize = Integer.parseInt(node.get("totalSize").toString().replaceAll("\"", ""));

                        }
                    }
                DcObjeMonitor dcObjeMonitor = new DcObjeMonitor();
                dcObjeMonitor.setType("RestFul接口");
                dcObjeMonitor.setRetum("是");
                dcObjeMonitor.setTid(index.get("jobid").toString());
                dcObjeMonitor.preInsert();
                dcObjeMonitorDao.insert(dcObjeMonitor);
        }else if (jobname.startsWith("bd_wifi_")){
                String restWsData = null;
                if(DcJobTransIntfSrc.RESTFUL_TYPE_GET.equals(index.get("restType").toString())){	//get方式 暂不考虑分页,项目中碰到实际数据再说
                    resource.get(String.class);

                }else if(DcJobTransIntfSrc.RESTFUL_TYPE_POST.equals(index.get("restType").toString())){	//post方式 博地项目中存在数据分页情况
                    //构建form参数
                    MultivaluedMapImpl params = new MultivaluedMapImpl();
                    if (DcStringUtils.isNotBlank(index.get("params").toString())){
                        for(String str: index.get("params").toString().split("&")){
                            if(str.indexOf("=")>0){
                                //根据=号进行解析
                                params.add(str.split("=")[0], str.split("=")[1]);
                            }
                        }
                    }
                    //rest 默认调用方式
                    String contectType = DcStringUtils.isNotBlank(index.get("restContentType").toString())?index.get("restContentType").toString():MediaType.APPLICATION_FORM_URLENCODED;
                    restWsData = resource.type(contectType).post(String.class, params);
                    //验证分页参数, 持续读取分页数据
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(restWsData);
                    Assert.isTrue("0".equals(node.get("code").toString()), node.get("description").toString());
                    if(node.has("data")){
                        node.get("data").toString();
                    }
                }
                DcObjeMonitor dcObjeMonitor = new DcObjeMonitor();
                dcObjeMonitor.setType("RestFul接口");
                dcObjeMonitor.setRetum("是");
                dcObjeMonitor.setTid(index.get("jobid").toString());
                dcObjeMonitor.preInsert();
                dcObjeMonitorDao.insert(dcObjeMonitor);
            }else if(jobname.startsWith("bd_park_page_")){
                StringBuilder restWsData = new StringBuilder(512);
                if(DcJobTransIntfSrc.RESTFUL_TYPE_GET.equals(index.get("restType").toString())){	//get方式 暂不考虑分页,项目中碰到实际数据再说

                }else if(DcJobTransIntfSrc.RESTFUL_TYPE_POST.equals(index.get("restType").toString())){	//post方式 博地项目中存在数据分页情况
                    //构建form参数
                    MultivaluedMapImpl params = new MultivaluedMapImpl();
                    int startNum = 0, pageSize=20;   //默认起始数据索引, 每页数据量
                    if (DcStringUtils.isNotBlank(index.get("params").toString())){
                        for(String str: index.get("params").toString().split("&")){
                            if(str.indexOf("=")>0){
                                //根据=号进行解析
                                params.add(str.split("=")[0], str.split("=")[1]);
                                if(str.indexOf("pageSize")>-1){
                                    pageSize = Integer.parseInt(str.split("=")[1]);
                                }
                                if(str.indexOf("start")>-1){
                                    startNum = Integer.parseInt(str.split("=")[1]);
                                }
                            }
                        }
                    }
                    //rest 默认调用方式
                    String contectType = DcStringUtils.isNotBlank(index.get("restContentType").toString())?index.get("restContentType").toString(): MediaType.APPLICATION_FORM_URLENCODED;
                    String restJson = resource.type(contectType).post(String.class, params);

                    //验证分页参数, 持续读取分页数据
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(restJson);
                    Assert.isTrue("0".equals(node.get("code").toString()), "接口返回结果异常!");
                    if(node.has("list")){
                        JSONArray array = new JSONArray();
                        restWsData.append(node.get("list"));
                    }

                    //如存在总数 totalSize, 进行分页读取
                    if(node.has("total")){
                        int totalSize = Integer.parseInt(node.get("total").toString().replaceAll("\"", ""));

                    }
                }
                DcObjeMonitor dcObjeMonitor = new DcObjeMonitor();
                dcObjeMonitor.setType("RestFul接口");
                dcObjeMonitor.setRetum("是");
                dcObjeMonitor.setTid(index.get("jobid").toString());
                dcObjeMonitor.preInsert();
                dcObjeMonitorDao.insert(dcObjeMonitor);
            }else if(!jobname.startsWith("bd_wifi_page_")&&!jobname.startsWith("bd_wifi_")&&!jobname.startsWith("bd_park_page_")){
                if(DcJobTransIntfSrc.RESTFUL_TYPE_GET.equals(index.get("restType").toString())){	//get方式 暂不考虑分页,项目中碰到实际数据再说
                    resource.get(String.class);

                }else if(DcJobTransIntfSrc.RESTFUL_TYPE_POST.equals(index.get("restType").toString())){	//post方式 博地项目中存在数据分页情况
                    //构建form参数
                    MultivaluedMapImpl params = new MultivaluedMapImpl();
                    if (DcStringUtils.isNotBlank(index.get("params").toString())){
                        for(String str: index.get("params").toString().split("&")){
                            if(str.indexOf("=")>0){
                                //根据=号进行解析
                                params.add(str.split("=")[0], str.split("=")[1]);
                            }
                        }
                    }
                    String contectType = DcStringUtils.isNotBlank(index.get("restContentType").toString())?index.get("restContentType").toString():MediaType.APPLICATION_FORM_URLENCODED;
                    resource.type(contectType).post(String.class, params);
                }
                DcObjeMonitor dcObjeMonitor = new DcObjeMonitor();
                dcObjeMonitor.setType("RestFul接口");
                dcObjeMonitor.setRetum("是");
                dcObjeMonitor.setTid(index.get("jobid").toString());
                dcObjeMonitor.preInsert();
                dcObjeMonitorDao.insert(dcObjeMonitor);
            }
            }catch(Exception e){
                DcObjeMonitor dcObjeMonitor = new DcObjeMonitor();
                dcObjeMonitor.setType("RestFul接口");
                dcObjeMonitor.setRetum("否");
                dcObjeMonitor.setTid(index.get("jobid").toString());
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
