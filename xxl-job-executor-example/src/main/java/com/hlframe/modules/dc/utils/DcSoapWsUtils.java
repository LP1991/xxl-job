/********************** 版权声明 *************************
 * 文件: DcSoapWsUtils.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年04月18日 14:36
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import com.hlframe.common.utils.DateUtils;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntfSrc;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import javax.xml.namespace.QName;
import java.util.LinkedList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * com.hlframe.modules.dc.utils.DcSoapWsUtils
 *  Soap webService 接口 工具类
 *
 * @author peijd
 * @create 2017-04-18 14:36
 **/
public class DcSoapWsUtils {
    private final static Logger logger = getLogger(DcSoapWsUtils.class);

    /**
     * @方法名称: getSoapWsData
     * @实现功能: 根据配置信息 获取Soap Webservice 数据
     * @params  [param]
     * @return  java.lang.String
     * @create by peijd at 2017/4/18 15:24
     */
    public static String getSoapWsData(DcJobTransIntfSrc param) throws Exception {
        Assert.hasText(param.getWsMethod());    //ws 调用方法

        //      2. use the JaxWsDynamicClientFactory class to connect the webservice
//        这个是用cxf 客户端访问cxf部署的webservice服务
        //千万记住，访问cxf的webservice必须加上namespace ,否则通不过
        //现在又另外一个问题，传递过去的参数服务端接收不到
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        //url为调用webService的wsdl地址
        Client client = dcf.createClient(param.getWsPath());
        String nameSpace = param.getWsNamespace();
        if (StringUtils.isBlank(nameSpace)){
            //默认获取目标命名空间
            Endpoint endpoint = client.getEndpoint();
            logger.info("--->"+endpoint.getService().getName().getNamespaceURI());
            nameSpace = endpoint.getService().getName().getNamespaceURI();
        }
        //namespace是命名空间，methodName是方法名
        QName name=new QName(nameSpace, param.getWsMethod());
//        构建参数  参数类型解析, 默认为string类型  int:10&string:abc&date:2017-04-25  暂不考虑复杂对象类型...
        List<Object> params = new LinkedList<Object>();
        if (DcStringUtils.isNotBlank(param.getParams())){
            String fieldType = null;
            for(String str: param.getParams().split("&")){
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
        return objects[0].toString();
    }
}
