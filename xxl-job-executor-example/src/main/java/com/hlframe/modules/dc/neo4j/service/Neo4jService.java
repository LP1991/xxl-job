/********************** 版权声明 *************************
 * 文件名: DcJobExportDataService.java
 * 包名: com.hlframe.modules.dc.dataexport.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年2月23日 下午5:03:31
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.neo4j.service;

import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.modules.dc.dataexport.entity.DcJobExportData;
import com.hlframe.modules.dc.dataexport.service.DcJobExportDataService;
import com.hlframe.modules.dc.dataprocess.entity.*;
import com.hlframe.modules.dc.dataprocess.service.*;
import com.hlframe.modules.dc.metadata.entity.DcObjectInterface;
import com.hlframe.modules.dc.metadata.entity.DcObjectLink;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import com.hlframe.modules.dc.metadata.service.DcObjectMainService;
import com.hlframe.modules.dc.neo4j.entity.DataProcessEntity;
import com.hlframe.modules.dc.neo4j.entity.Neo4j;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
public class Neo4jService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private DcObjectMainService dcObjectMainService = null; // obj main
	@Autowired
	private DcJobExportDataService dcJobExportDataService; //exTerport_db  dc_job_exportdata	数据导出
	@Autowired
	private DcDataProcessDesignService dcDataProcessDesignService; //trans_design  dc_data_process_design	转换设计
	@Autowired
	private DcJobTransHdfsService dcJobTransHdfsService; //extract_hdfs dc_job_transhdfs hdfs对象采集
	@Autowired
	private DcJobTransFileService dcJobTransFileService;//extract_file dc_job_transfile 文件对象采集
	@Autowired
	private DcJobTransDataService dcJobTransDataService;//extract_db 	dc_job_transdata  DB数据采集
    @Autowired
    private DcJobTransIntfService dcJobTransIntfService;//extract_intf dc_job_transintf 接口数据采集

    @Deprecated
	public String  queryHiveSql(Neo4j neo4j){
		Client client = Client.create();
		try {
			//restful 地址
			URI u = new URI(DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J/createData");
			WebResource resource = client.resource(u);
			JSONObject data = new JSONObject();
			/*
			 * 除过程描述不是必填字段外其他都为必填字段
			 */
			
				data.put("tableName", neo4j.getTableName() );//表名
				data.put("processId", neo4j.getProcessId() );//过程id
				data.put("processName",neo4j.getProcessName() );//过程名
				data.put("processDes", neo4j.getProcessDes());//过程描述
				data.put("tarName", neo4j.getTarName() );//目标表名
			MultivaluedMapImpl params = new MultivaluedMapImpl();  
			params.add("data", data.toString()); 
			String result= resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
			return result;
		} catch (Exception e) {
			logger.error("-->runScript", e);
			String i = "json error";
			String ser="''";
			return ser;
		}
	}

    /**
     * @name: getObjsAndLinksBySrcId
     * @funciton: query the link information from neo4j by source obj id.
     * @param
     * @return
     * @Create by lp at 2017/4/27 17:09
     * @throws
     */
    public String getObjsAndLinksBySrcId(String objId){
        Client client = Client.create();
        try {
            //restful 地址
            URI u = new URI( DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J/getObjsAndLinksBySrcId");
            WebResource resource = client.resource(u);

            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("data", objId);
            String result= resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
            return result;
        } catch (Exception e) {
            logger.error("-->getObjsAndLinksBySrcId failed. objId :"+ objId, e);
            return "error";
        }
    }

    /**
     * @name: getObjsAndLinksByProcessId
     * @funciton: query the link information from neo4j by obj id.
     * @param
     * @return
     * @Create by lp at 2017/4/27 17:10
     * @throws
     */
    public String getObjsAndLinksById(String objId){
        Client client = Client.create();
        try {
            //restful 地址
            URI u = new URI(DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J/getObjsAndLinksById");
            WebResource resource = client.resource(u);

            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("data", objId);
            String result= resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
            return result;
        } catch (Exception e) {
            logger.error("-->getObjsAndLinksByProcessId failed. objId :"+ objId, e);
            return "data:[],links:[]";
        }
    }

    /**
     * @name: getObjsAndLinksByProcessId
     * @funciton: query the link information from neo4j by process obj id.
     * @param
     * @return
     * @Create by lp at 2017/4/27 17:10
     * @throws
     */
    public String getObjsAndLinksByProcessId(String objId){
        Client client = Client.create();
        try {
            //restful 地址
            URI u = new URI( DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J/getObjsAndLinksByProcessId");
            WebResource resource = client.resource(u);

            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("data", objId);
            String result= resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
            return result;
        } catch (Exception e) {
            logger.error("-->getObjsAndLinksByProcessId failed. objId :"+ objId, e);
            return "error";
        }
    }

    /**
     * @name: getObjsAndLinksByTargetId
     * @funciton: query the link information from neo4j by target obj id.
     * @param
     * @return
     * @Create by lp at 2017/4/27 17:11
     * @throws
     */
    public String getObjsAndLinksByTargetId(String objId){
        Client client = Client.create();
        try {
            //restful 地址
            URI u = new URI(DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J/getObjsAndLinksByTargetId");
            WebResource resource = client.resource(u);

            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("data", objId);
            String result= resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
            return result;
        } catch (Exception e) {
            logger.error("-->getObjsAndLinksByTargetId failed. objId :"+ objId, e);
            return "error";
        }
    }

    /**
     * @name: addLink2Neo4j
     * @funciton: add the link information to neo4j
     * @param
     * @return
     * @Create by lp at 2017/4/27 17:11
     * @throws
     */
	public String addLink2Neo4j(DcObjectLink link){
		Client client = Client.create();

		try {
			//restful 地址
			URI u = new URI( DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J/createData");
			WebResource resource = client.resource(u);

			String jsonParam = link2jsonParam(link);
            if (jsonParam == null){
                logger.info("link2jsonParam failed, link id :"+link.getId());
                return "error";
            }
			MultivaluedMapImpl params = new MultivaluedMapImpl();
			params.add("data", jsonParam);
//			System.out.println("================="+jsonParam);
			String result= resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
			return result;
		} catch (Exception e) {
			logger.error("-->addLink2Neo4j failed. link id:"+link.getId(), e);
			return "error";
		}
	}

    /**
     * @name: addLink2Neo4j
     * @funciton: add the link information to neo4j
     * @param link
     * @return
     * @Create by lp at 2017/4/27 17:11
     * @throws
     */
    public String addLink2Neo4j(DataProcessEntity link){
        Client client = Client.create();
        if (link == null){
            logger.info("link2jsonParam failed, link objId :"+link.getObjId());
            return "error";
        }
        try {
            //restful 地址
            URI u = new URI( DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J/createData");
            WebResource resource = client.resource(u);
            ObjectMapper mapper = new ObjectMapper();
            String jsonParam = mapper.writeValueAsString(link);

            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("data", jsonParam);
//			System.out.println("================="+jsonParam);
            String result= resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
            return result;
        } catch (Exception e) {
            logger.error("-->addLink2Neo4j failed. link objId:"+link.getObjId(), e);
            return "error";
        }
    }

	/**
	 * @name: link2jsonParam
	 * @funciton: translate link obj to json param
	 * @param
	 * @return
	 * @Create by lp at 2017/4/27 17:12
	 * @throws
	 */
    private String link2jsonParam(DcObjectLink link) {
        //源/目标/转换过程 id不可相同. 否则血缘图无法展现  by peijd 2016-06-26
        Assert.isTrue(!link.getSrcObjId().equals(link.getTarObjId()), "源对象和目标对象ID不可相同!" );
        Assert.isTrue(!link.getSrcObjId().equals(link.getProcessId()), "源对象和转换过程ID不可相同!" );
        Assert.isTrue(!link.getTarObjId().equals(link.getProcessId()), "目标对象和转换过程ID不可相同!" );
        try {
            if (link == null) {
                return null;
            }
            if (DcObjectLink.LINK_TYPE_FIELD.equals(link.getLinkType())) {
                return null;
            }
            DcObjectMain source = getObjectMainService().get(link.getSrcObjId());
            DcObjectMain target = getObjectMainService().get(link.getTarObjId());
            DataProcessEntity data_target = new DataProcessEntity(target.getId(), target.getObjName(), DataProcessEntity.NEO4J_LABEL_TARGET, target.getObjDesc());
            DataProcessEntity data_source = new DataProcessEntity(source.getId(), source.getObjName(), DataProcessEntity.NEO4J_LABEL_SOURCE, source.getObjDesc());
            DataProcessEntity data_process = null;
            if (DcObjectLink.DATA_SOURCE_EXTRACT_DB.equals(link.getDataSource())) {
                //extract_db 	dc_job_transdata  DB数据采集
                DcJobTransData process = dcJobTransDataService.get(link.getProcessId());
                data_process = new DataProcessEntity(process.getId(), process.getJobName(), DataProcessEntity.NEO4J_LABEL_PROCESS, process.getJobDesc());
            } else if (DcObjectLink.DATA_SOURCE_EXTRACT_INTF.equals(link.getDataSource())) {
                //如果是数据表发布
                if(DcObjectInterface.INTFC_SRC_TYPE_PUBLISH.equalsIgnoreCase(link.getProcessId())){
                    data_process = new DataProcessEntity(link.getProcessId(), "数据中心发布", DataProcessEntity.NEO4J_LABEL_PROCESS, "数据中心内部发布接口");

                }else{
                    //extract_intf dc_job_transintf 接口数据采集
                    DcJobTransIntf process = dcJobTransIntfService.get(link.getProcessId());
                    data_process = new DataProcessEntity(process.getId(), process.getJobName(), DataProcessEntity.NEO4J_LABEL_PROCESS, process.getJobDesc());
                }
            } else if (DcObjectLink.DATA_SOURCE_EXTRACT_FILE.equals(link.getDataSource())) {
                //    extract_file dc_job_transfile 文件对象采集
                DcJobTransFile process = dcJobTransFileService.get(link.getProcessId());
                data_process = new DataProcessEntity(process.getId(), process.getJobname(), DataProcessEntity.NEO4J_LABEL_PROCESS, process.getDescription());
            } else if (DcObjectLink.DATA_SOURCE_EXTRACT_HDFS.equals(link.getDataSource())) {
                //    extract_hdfs dc_job_transhdfs hdfs对象采集
                DcJobTransHdfs process = dcJobTransHdfsService.get(link.getProcessId());
                data_process = new DataProcessEntity(process.getId(), process.getJobName(), DataProcessEntity.NEO4J_LABEL_PROCESS, process.getJobDesc());
            } else if (DcObjectLink.DATA_SOURCE_TRANSDESIGN.equals(link.getDataSource())) {
                //    trans_design  dc_data_process_design	转换设计
                DcDataProcessDesign process = dcDataProcessDesignService.get(link.getProcessId());
                data_process = new DataProcessEntity(process.getId(), process.getDesignName(), DataProcessEntity.NEO4J_LABEL_PROCESS, process.getDesignDesc());
            } else if (DcObjectLink.DATA_SOURCE_EXPORT_DB.equals(link.getDataSource())) {
                //    export_db  dc_job_exportdata	数据导出
                DcJobExportData process = dcJobExportDataService.get(link.getProcessId());
                data_process = new DataProcessEntity(process.getId(), process.getJobName(), DataProcessEntity.NEO4J_LABEL_PROCESS, process.getJobDesc());
            } else if (DcObjectLink.DATA_SOURCE_TRANSJOB.equals(link.getDataSource())) {
                // Todo can't find the specific serivce for DcObjectLink.DATA_SOURCE_TRANSJOB
                DcJobExportData process = dcJobExportDataService.get(link.getProcessId());
                data_process = new DataProcessEntity(process.getId(), process.getJobName(), DataProcessEntity.NEO4J_LABEL_PROCESS, process.getJobDesc());
            }
            return wrapJson(data_source,data_process,data_target);
        }catch(Exception e){
	        logger.error("link2jsonParam failed, link id:"+link.getId(),e);
        }
        return null;

    }

    private String wrapJson(DataProcessEntity data_source, DataProcessEntity data_process, DataProcessEntity data_target) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            if (data_process == null){
                return mapper.writeValueAsString(data_target);
            }
            List<DataProcessEntity> c1 = new ArrayList<>();
            c1.add(data_target);
            data_process.setSubNodes(c1);

            if (data_source == null){
                return mapper.writeValueAsString(data_process);
            }
            List<DataProcessEntity> c2 = new ArrayList<>();
            c2.add(data_process);

            data_source.setSubNodes(c2);

            return mapper.writeValueAsString(data_source);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("wrapJson failed. source id:"+data_source.getObjId()+", process id:"+data_process.getObjId()+", target id:"+data_target.getObjId(),e);
        }
        return null;
    }

    private String wrapJson(DataProcessEntity entity) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            if (entity == null){
                return null;
            }
            return mapper.writeValueAsString(entity);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("wrapJson failed. source id:"+entity.getObjId(),e);
        }
        return null;
    }

    /**
     * @name: addDataList2Neo4j
     * @funciton: add link list to neo4j database.
     * @param
     * @return
     * @Create by lp at 2017/4/27 17:12
     * @throws
     */
	public String addDataList2Neo4j(List<DcObjectLink> linkList) {
        Client client = Client.create();

        try {
            //restful 地址
            URI u = new URI(DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J/createDataList");
            WebResource resource = client.resource(u);
            JSONArray array = new JSONArray();
            for (DcObjectLink link : linkList){
                try {
                    String jsonParam = link2jsonParam(link);
                    if (jsonParam != null){
                        array.put(new JSONObject(jsonParam));
                    }else {
                        logger.info("link2jsonParam failed, link id :"+link.getId());
                    }
                }catch (Exception e){
                    logger.error("resolve link fail"+link.getId());
                }
            }

            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("data", array.toString());
            String result= resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
            return result;
        } catch (Exception e) {
            logger.error("-->addDataList2Neo4j failed", e);
            return "error";
        }
	}
    /**
     * @name: editData
     * @funciton: updata the obj to neo4j database
     * @param
     * @return
     * @Create by lp at 2017/4/27 17:24
     * @throws
     */
    public String editData(DataProcessEntity entity) {
        if (null == entity){
            return null;
        }
        Client client = Client.create();

        try {
            //restful 地址
            URI u = new URI(DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J/editData");
            WebResource resource = client.resource(u);

            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("data", wrapJson(entity));
            String result= resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
            return result;
        } catch (Exception e) {
            logger.error("-->editData failed. entity id:"+entity.getObjId(), e);
            return "error";
        }
    }

    /**
     * @name: deleteAllBySrcId
     * @funciton: delete all the objs and the relationships which link to the srcObj.(It contains the process and the target if they are exist.)
     * @param
     * @return
     * @Create by lp at 2017/4/27 17:25
     * @throws
     */
    public String deleteAllBySrcId(String objId) {
	    if (null == objId){
	        return null;
        }
        Client client = Client.create();

        try {
            //restful 地址
            URI u = new URI( DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J/deleteAllBySrcId");
            WebResource resource = client.resource(u);

            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("data", objId);
            String result= resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
            return result;
        } catch (Exception e) {
            logger.error("-->deleteAllBySrcId failed. objId:"+objId, e);
            return "error";
        }
    }

    /**
     * @name: deleteObj
     * @funciton: delete the obj with it's relationships
     * @param
     * @return
     * @Create by lp at 2017/4/27 17:26
     * @throws
     */
    public String deleteObj(DataProcessEntity entity) {
        if (null == entity){
            return null;
        }
        Client client = Client.create();

        try {
            //restful 地址
            URI u = new URI(DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J/deleteById");
            WebResource resource = client.resource(u);

            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("data", wrapJson(entity));
            String result= resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
            return result;
        } catch (Exception e) {
            logger.error("-->deleteById failed. objId:"+entity, e);
            return "error";
        }
    }

    /**
     * @name: clearDatabase
     * @funciton: clear the neo4j database. initiated a clear db
     * @param
     * @return
     * @Create by lp at 2017/4/27 17:27
     * @throws
     */
    public String clearDatabase() {
        Client client = Client.create();

        try {
            //restful 地址
            URI u = new URI(DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J/clearDatabase");
            WebResource resource = client.resource(u);

            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("key", DcPropertyUtils.getProperty("jersey.neo4j.key4clear","private"));
            String result= resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
            return result;
        } catch (Exception e) {
            logger.error("-->clearDatabase failed.", e);
            return "error";
        }
    }

    /**
     * @name: createIndex
     * @funciton: create label index for source, process and target.
     * @param
     * @return
     * @Create by lp at 2017/4/27 17:28
     * @throws
     */
    public String createIndex() {
        Client client = Client.create();

        try {
            //restful 地址
            URI u = new URI(DcPropertyUtils.getProperty("jersey.neo4j") + "rest/neo4J/createIndex");
            WebResource resource = client.resource(u);

            String result= resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class);
            return result;
        } catch (Exception e) {
            logger.error("-->createIndex failed.", e);
            
            return "error";
        }
    }

    /**
     * @方法名称: getObjectMainService
     * @实现功能: 获取元数据对象Service
     * @params  []
     * @return  com.hlframe.modules.dc.metadata.service.DcObjectMainService
     * @create by peijd at 2017/5/4 20:43
     */
    private DcObjectMainService getObjectMainService() {
        if(null==dcObjectMainService){
            synchronized (DcObjectMainService.class) {
                if(null==dcObjectMainService){
                    dcObjectMainService = SpringContextHolder.getBean(DcObjectMainService.class);
                }
            }
        }
        return dcObjectMainService;
    }
}
