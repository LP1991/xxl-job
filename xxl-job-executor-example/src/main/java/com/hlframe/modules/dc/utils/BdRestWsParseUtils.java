/********************** 版权声明 *************************
 * 文件: BdJsonUtils.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年05月22日 14:32
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import com.hlframe.common.service.ServiceException;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntfSrc;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;

/**
 * com.hlframe.modules.dc.utils.BdRestWsParseUtils
 * 博地项目个性化 实现rest接口数据解析, 分页调用,个性化读取设置,产品中不具有通用性, 故单独实现
 *
 * @author peijd
 * @create 2017-05-22 14:32
 **/
public class BdRestWsParseUtils {

    private final static Logger logger = LoggerFactory.getLogger(BdRestWsParseUtils.class);

    /**
     * @方法名称: getBdWifiWsData
     * @实现功能: 获取博地wifi系统 rest接口数据 数据返回结果需个性化处理
     * @param param
     * @return  java.lang.String
     * @create by peijd at 2017/5/22 19:41
     */
    public static String getBdWifiWsData(DcJobTransIntfSrc param) {
        Assert.hasLength(param.getRestUrl());
        Assert.hasText(param.getRestType());

        String restWsData = null;
        try{
            URI url = new URI(param.getRestUrl());
            //初始化客户端连接
            Client client = Client.create();
            WebResource resource = client.resource(url);

            //目前只考虑post/get调用方式
            if(DcJobTransIntfSrc.RESTFUL_TYPE_GET.equals(param.getRestType())){	//get方式 暂不考虑分页,项目中碰到实际数据再说
                return resource.get(String.class);

            }else if(DcJobTransIntfSrc.RESTFUL_TYPE_POST.equals(param.getRestType())){	//post方式 博地项目中存在数据分页情况
                //构建form参数
                MultivaluedMapImpl params = new MultivaluedMapImpl();
                if (DcStringUtils.isNotBlank(param.getParams())){
                    for(String str: param.getParams().split("&")){
                        if(str.indexOf("=")>0){
                            //根据=号进行解析
                            params.add(str.split("=")[0], str.split("=")[1]);
                        }
                    }
                }
                //rest 默认调用方式
                String contectType = DcStringUtils.isNotBlank(param.getRestContentType())?param.getRestContentType():MediaType.APPLICATION_FORM_URLENCODED;
                restWsData = resource.type(contectType).post(String.class, params);
                //验证分页参数, 持续读取分页数据
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(restWsData);
                Assert.isTrue("0".equals(node.get("code").toString()), node.get("description").toString());

                if(node.has("data")){
                    return node.get("data").toString();
                }
            }

        }catch(Exception e){
            logger.error("-->getRestfulWsData: ", e);
            throw new ServiceException("接口调用异常!");
        }
        return  restWsData;
    }

    /**
     * @方法名称: getBdWifiWsPageData
     * @实现功能: 获取博地WIFI系统 rest接口分页数据, 博地wifi系统接口数据具有分页属性, 需定制化实现 modified by peijd 2017-05-22
     * @param param
     * @return  java.lang.String
     * @create by peijd at 2017/5/22 14:28
     */
    public static String getBdWifiWsPageData(DcJobTransIntfSrc param){
        Assert.hasLength(param.getRestUrl());
        Assert.hasText(param.getRestType());

        StringBuilder restWsData = new StringBuilder(512);
        try{
            URI url = new URI(param.getRestUrl());
            //初始化客户端连接
            Client client = Client.create();
            WebResource resource = client.resource(url);

            //目前只考虑post/get调用方式
            if(DcJobTransIntfSrc.RESTFUL_TYPE_GET.equals(param.getRestType())){	//get方式 暂不考虑分页,项目中碰到实际数据再说
                return resource.get(String.class);

            }else if(DcJobTransIntfSrc.RESTFUL_TYPE_POST.equals(param.getRestType())){	//post方式 博地项目中存在数据分页情况
                //构建form参数
                MultivaluedMapImpl params = new MultivaluedMapImpl();
                int startNum = 0, pageSize=20;   //默认起始数据索引, 每页数据量
                if (DcStringUtils.isNotBlank(param.getParams())){
                    for(String str: param.getParams().split("&")){
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
                String contectType = DcStringUtils.isNotBlank(param.getRestContentType())?param.getRestContentType(): MediaType.APPLICATION_FORM_URLENCODED;
                String restJson = resource.type(contectType).post(String.class, params);

                //验证分页参数, 持续读取分页数据
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(restJson);
                Assert.isTrue("0".equals(node.get("code").toString()), node.get("description").toString());
                if(node.has("data")){
                    JSONArray array = new JSONArray();
                    restWsData.append(node.get("data"));
                }

                //如存在总数 totalSize, 进行分页读取
                if(node.has("totalSize")){
                    int totalSize = Integer.parseInt(node.get("totalSize").toString().replaceAll("\"", ""));
                    appendWifiPageData(restWsData, totalSize, pageSize, startNum+pageSize, params, contectType, resource);
                }
            }

        }catch(Exception e){
            logger.error("-->getBdWifiWsPageData: ", e);
            throw new ServiceException("接口调用异常!");
        }
        return  restWsData.toString();
    }

    /**
     * @方法名称: appendWifiPageData
     * @实现功能: 加载Wifi分页数据
     * @param restWsData    结果数据, 递归
     * @param totalSize     数据总量
     * @param pageSize      每页数量
     * @param startNum      每页起始数
     * @param intfParams    接口参数
     * @param contectType   接口参数类别
     * @param resource      //接口连接方式
     * @return
     * @create by peijd at 2017/5/22 16:38
     */
    private static void appendWifiPageData(StringBuilder restWsData, int totalSize, int pageSize, int startNum,
                                           MultivaluedMapImpl intfParams, String contectType, WebResource resource) throws Exception {
        if(logger.isDebugEnabled()){
            logger.debug("--->start: "+startNum);
            logger.debug("--->pageSize: "+pageSize);
            logger.debug("--->totalSize: "+totalSize);
        }
        //参数重新赋值
        intfParams.putSingle("start",startNum);
        String restJson = resource.type(contectType).post(String.class, intfParams);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(restJson);
        Assert.isTrue("0".equals(node.get("code").toString()), node.get("description").toString());
        if(node.has("data")){
            restWsData.deleteCharAt(restWsData.length()-1).append(",").append(node.get("data").toString().substring(1));
        }
        //如存在总数 totalSize, 进行分页读取
        if(node.has("totalSize") && startNum+pageSize<totalSize){
            appendWifiPageData(restWsData, Integer.parseInt(node.get("totalSize").toString().replaceAll("\"","")), pageSize, startNum+pageSize, intfParams, contectType, resource);
        }

    }

    /**
     * @方法名称: getBdParkWsPageData
     * @实现功能: 获取博地停车场系统WS接口数据
     * @params  [intfSrc]
     * @return  java.lang.String
     * @create by peijd at 2017/5/22 20:02
     */
    public static String getBdParkWsPageData(DcJobTransIntfSrc param) {
        Assert.hasLength(param.getRestUrl());
        Assert.hasText(param.getRestType());

        StringBuilder restWsData = new StringBuilder(512);
        try{
            URI url = new URI(param.getRestUrl());
            //初始化客户端连接
            Client client = Client.create();
            WebResource resource = client.resource(url);

            //目前只考虑post/get调用方式
            if(DcJobTransIntfSrc.RESTFUL_TYPE_GET.equals(param.getRestType())){	//get方式 暂不考虑分页,项目中碰到实际数据再说
                return resource.get(String.class);

            }else if(DcJobTransIntfSrc.RESTFUL_TYPE_POST.equals(param.getRestType())){	//post方式 博地项目中存在数据分页情况
                //构建form参数
                MultivaluedMapImpl params = new MultivaluedMapImpl();
                int startNum = 0, pageSize=20;   //默认起始数据索引, 每页数据量
                if (DcStringUtils.isNotBlank(param.getParams())){
                    for(String str: param.getParams().split("&")){
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
                String contectType = DcStringUtils.isNotBlank(param.getRestContentType())?param.getRestContentType(): MediaType.APPLICATION_FORM_URLENCODED;
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
                    appendParkPageData(restWsData, totalSize, pageSize, startNum+pageSize, params, contectType, resource);
                }
            }

        }catch(Exception e){
            logger.error("-->getBdWifiWsPageData: ", e);
            throw new ServiceException("接口调用异常!");
        }
        return  restWsData.toString();
    }

    /**
     * @方法名称: appendParkPageData
     * @实现功能: 加载停车系统分页数据
     * @param restWsData    结果数据, 递归
     * @param totalSize     数据总量
     * @param pageSize      每页数量
     * @param pageNum      每页起始数
     * @param intfParams    接口参数
     * @param contectType   接口参数类别
     * @param resource      //接口连接方式
     * @return
     * @create by peijd at 2017/5/22 20:14
     */
    private static void appendParkPageData(StringBuilder restWsData, int totalSize, int pageSize, int pageNum,
                                           MultivaluedMapImpl intfParams, String contectType, WebResource resource) throws IOException {
        if(logger.isDebugEnabled()){
            logger.debug("--->pageNum: "+ pageNum);
            logger.debug("--->pageSize: "+pageSize);
            logger.debug("--->totalSize: "+totalSize);
        }
        //参数重新赋值
        intfParams.putSingle("pageNo", pageNum);
        String restJson = resource.type(contectType).post(String.class, intfParams);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(restJson);
        Assert.isTrue("0".equals(node.get("code").toString()), "接口返回结果异常!");
        if(node.has("list")){
            restWsData.deleteCharAt(restWsData.length()-1).append(",").append(node.get("list").toString().substring(1));
        }
        //如存在总数 totalSize, 进行分页读取
        if(node.has("totle") && (pageNum+1)*pageSize<totalSize){
            appendParkPageData(restWsData, Integer.parseInt(node.get("total").toString().replaceAll("\"","")), pageSize, pageNum+1, intfParams, contectType, resource);
        }
    }


}
