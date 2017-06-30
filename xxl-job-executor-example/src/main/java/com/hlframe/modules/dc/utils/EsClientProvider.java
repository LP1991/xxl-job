/********************** 版权声明 *************************
 * 文件名: EsClientProvider.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月10日 下午1:30:30
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.plugin.deletebyquery.DeleteByQueryPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;

/** 
 * @类名: com.hlframe.modules.dc.utils.EsClientProvider.java 
 * @职责说明: Es客户端连接Provider
 * @创建者: peijd
 * @创建时间: 2016年11月10日 下午1:30:30
 */
public class EsClientProvider {

	private static final Logger logger = LoggerFactory.getLogger(EsClientProvider.class);
	
	private TransportClient client = null;		//连接客户端
    private volatile boolean inited = false;	//是否初始化
    
    public TransportClient get() {
        return this.client;
    }
 
    @PreDestroy
    public synchronized void close() {
        if (this.client != null) {
            this.client.close();
        }
    }
    
    /**
     * @方法名称: init 
     * @实现功能: 初始化client
     * @create by peijd at 2016年11月10日 下午2:34:02
     */
    @PostConstruct
    public synchronized void init() {
//        if (!inited) {
        try {
        	//初始化配置信息
            Settings settings = Settings.builder()
            		.put("client.transport.sniff",true)	// 客户端嗅探整个集群的状态，把集群中其它机器的ip地址自动添加到客户端中，并且自动发现新加入集群的机器
            		.put("client", true)
            		.put("cluster.name",DcPropertyUtils.getProperty("elasticSearch.cluster", "elasticsearch"))	//集群名称
                		.build();
                TransportClient client = TransportClient.builder().settings(settings).addPlugin(DeleteByQueryPlugin.class).build();
                this.client = client;
 
                //设置客户端地址
            String[] addresses = DcPropertyUtils.getProperty("elasticSearch.address", "10.1.70.200:9300").split(",");
            for (String address : addresses) {
                String[] hostAndPort = address.split(":");
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostAndPort[0]), Integer.valueOf(hostAndPort[1])));
            }
            this.inited = true;
        } catch (UnknownHostException e) {
        	logger.error(String.format("init search client err:=>msg:[%s]", e.getMessage()), e);
            if (client != null) {
                this.client.close();
            }
        }
//        }
    }
    
    public boolean inited(){
    	return inited;
    }
}
