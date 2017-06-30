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
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/23.
 */
@Service
@Transactional(readOnly = true)
public class dcHiveQueryService implements DcTaskService {
    public static Client client = Client.create();
    protected Logger logger = LoggerFactory.getLogger(getClass());//存储日志
    @Autowired
    private DcObjeMonitorDao dcObjeMonitorDao;
    @Transactional(readOnly = false)
    public DcDataResult runTask(String Sql){
        String sql = "show tables";//构建sql语句
        Assert.hasText(sql);
        DcObjeMonitor dcObjeMonitor = new DcObjeMonitor();
        DcDataResult resul = new DcDataResult();

        try {

            URI u = new URI(DcPropertyUtils.getProperty("queryHive.restServer.url"));

            //URI u = new URI("http://localhost:9050/jerseyHive/rest/dcHiveQuery/executeQuerySql");
            WebResource resource = client.resource(u);
            // 构建form参数
            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("metaSql", sql);
            long startTime= System.currentTimeMillis();
            String result = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
            long endTime= System.currentTimeMillis();
            float excTime=(float)(endTime-startTime)/1000;
            logger.debug("result执行时间："+excTime+"s");
            String data = result.replace("\"[", "[").replace("]\"", " ]").replace("\"{", "{").replace("}\"", "}").replace("\\", "");//对result进行变化
            data = jsonString(data);
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> map = mapper.readValue(data, Map.class);//将data转成Json格式
            List<Map<String, Object>> jsonList = null;
            if("T".equals(map.get("resultFlag"))){
                if(map.get("std_msg")==""){
                    jsonList = new ArrayList<Map<String,Object>>();
                }else{
                    jsonList = (List<Map<String, Object>>) map.get("std_msg");//获取到result里面的数据，强制转化成List<Map<String, String>>格式
                }
            }
            if(jsonList == null || jsonList.size()==0){
                Map<String,Object> dataMap = new HashMap<String,Object>();
                dataMap.put("result", map.get("err_msg"));
            }

            dcObjeMonitor.setType(DcPropertyUtils.getProperty("queryHive.restServer.url"));
            dcObjeMonitor.setRetum("是");
            dcObjeMonitor.setTid(IdGen.uuid());
            dcObjeMonitor.preInsert();
            dcObjeMonitorDao.insert(dcObjeMonitor);
            resul.setRst_flag(true);
            resul.setRst_std_msg("测试任务:TestTaskService"+ DateUtils.getDateTime());
            System.out.println("---->测试任务:"+resul.getRst_std_msg());
        } catch (Exception e) {//uri异常处理
            dcObjeMonitor.setType(DcPropertyUtils.getProperty("queryHive.restServer.url"));
            dcObjeMonitor.setRetum("否");
            dcObjeMonitor.setTid(IdGen.uuid());
            if(null!=e.getMessage()){
                if(e.getMessage().length()>200){
                    dcObjeMonitor.setAbnorma(e.getMessage().substring(0,190)+"...");
                }else{
                    dcObjeMonitor.setAbnorma(e.getMessage());
                }
            }
            dcObjeMonitor.preInsert();
            dcObjeMonitorDao.insert(dcObjeMonitor);
            logger.error("-->executeQuerySql: ", e);
            System.out.println("---->测试任务:"+resul.getRst_std_msg());
            resul.setRst_flag(false);
            resul.setRst_err_msg(e.getMessage());
        }

        return resul;

    }

    private String jsonString(String s){
        char[] temp = s.toCharArray();
        int n = temp.length;
        for(int i =0;i<n;i++){
            if(temp[i]==':'&&temp[i+1]=='"'){
                for(int j =i+2;j<n;j++){
                    if(temp[j]=='"'){
                        if(temp[j+1]!=',' &&  temp[j+1]!='}'){
                            temp[j]='”';
                        }else if(temp[j+1]==',' ||  temp[j+1]=='}'){
                            break ;
                        }
                    }
                }
            }
        }
        return new String(temp);
    }

}
