package com.hlframe.modules.dc.task;

import com.hlframe.common.utils.DateUtils;
import com.hlframe.modules.dc.common.service.DcCommonService;
import com.hlframe.modules.dc.datasearch.entity.DcSearchContent;
import com.hlframe.modules.dc.datasearch.service.DcSearchContentService;
import com.hlframe.modules.dc.metadata.entity.DcObjectCataRef;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import com.hlframe.modules.dc.metadata.service.DcObjectCataRefService;
import com.hlframe.modules.dc.metadata.service.DcObjectMainService;
import com.hlframe.modules.dc.utils.DcEsUtil;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * @类名: Test
 * @职责说明: 阿萨德
 * @创建者: Primo
 * @创建时间: 2017/3/8
 */
public class Test {
    private static ApplicationContext applicationContext;
    public static void upload() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext("/applicationcontext-job.xml","/spring-context-dc.xml");
        DcCommonService
                service = applicationContext.getBean(DcCommonService.class);
        service.initLoadDataToEs();
//        applicationContext.c
//        service.runTask(null);
//        int t = service.getFolders4es(new DcObjectMain()).size();
//        System.out.print(t);
//        service.configObjLinkByProcess("00fcf152021b469281d1761d825cfa65","18336d59a2834760b36c2bcfef5ae56b,18ffe222afdd46c6a4901567f49808d5,1d99ce22cd394be1835a195474b38d1e,wasd","1fef159218ab4c4caf1d7fffb2d0cd77");
//        service.configObjLinkByProcess("1950e58510d344a7a3e9a85ec5a7c3c5","2ec27a04f0214f19af561d0d98d182a4,2f0e647a2f164e628aba3f9ac5e40d54","2f854d31e7da424987689f90a819452f");

//        DataProcessEntity entity = new DataProcessEntity("68fab6b77c7341ef85424d425262dc6d","woshinidaye","source");
//        service.deleteObj(entity);
//        service.deleteAllBySrcId("18ffe222afdd46c6a4901567f49808d5");
//       String s = service.createIndex();
//       System.out.println(s);
//        es(service);
//        time();
    }


    public static Date changeTimeZone(Date date, TimeZone oldZone, TimeZone newZone) {
        Date dateTmp = null;
        if (date != null) {
            int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
            dateTmp = new Date(date.getTime() - timeOffset);
        }
        return dateTmp;
    }
    public static  void time(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
        simpleDateFormat.setTimeZone(utcZone);
        String s = simpleDateFormat.format(new Date());
        System.out.println(s);
    }


    public static void es(DcCommonService service) throws Exception {
        Client client = DcEsUtil.getEsClient();
        String id = "0530d916650745acbe403a0e3b676b43";
        String type = "file";
        String index = "dc_metadata";
        service.dataUpsert2Es(client,id, DcObjectMain.OBJ_TYPE_FILE,index);
    }

    public static void search(){
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String oneYearBefore = df.format(DateUtils.addYears(date, -1));
        String sixMonthsBefore = df.format(DateUtils.addMonths(date, -6));
        String threeMonthsBefore = df.format(DateUtils.addMonths(date, -3));
        String oneMonthsBefore = df.format(DateUtils.addMonths(date, -1));
        String oneWeekBefore = df.format(DateUtils.addWeeks(date, -1));
        String now = df.format(date);
        String tomorrow = df.format(DateUtils.addDays(date, 1));
        String id="d0080810b07a495fae6b239fc5e703f3";
        try {
            String index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
            Client client = DcEsUtil.getEsClient();
            QueryBuilder timeqb = null;
            timeqb = QueryBuilders.rangeQuery("createDate").gte(convertToDate("oneYear")).lte(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            AggregationBuilder catagoryAgg = AggregationBuilders
                    .nested("myAgg").path("cata").subAggregation(AggregationBuilders
                            .terms("test")
                            .field("cata.cata_id"));
//            boolQuery()
//                .must(matchQuery("obj1.name", "blue"))
//                    .must(rangeQuery("obj1.count").gt(5)),
            timeqb = QueryBuilders.nestedQuery("s_cata",boolQuery().filter(matchQuery("s_cata.cata_id", id)).filter(matchQuery("s_cata.cata_id", "f9f9b645f7414d818b3a227bf1ba68b6")));
            timeqb = QueryBuilders.nestedQuery("s_cata",matchQuery("s_cata.cata_id", id));
            timeqb = QueryBuilders.queryStringQuery("*");
            SearchRequest request = new SearchRequest(index)
                    .source(new SearchSourceBuilder().query(timeqb));
            AggregationBuilder aggregation =
                    AggregationBuilders
                            .nested("agg").path("s_cata")
                            .subAggregation(
                                    AggregationBuilders
                                            .terms("name").field("s_cata.cata_id").size(100)
                            );
            QueryBuilder builder = QueryBuilders.boolQuery().must(QueryBuilders.nestedQuery("s_cata",matchQuery("s_cata.cata_id", "711911fca51748b0a53c627791d5b11f")))
            .must(QueryBuilders.nestedQuery("s_cata",matchQuery("s_cata.cata_id", "1d1c386b73ac4f26b8fdc08772aa6039")));

            SearchResponse response = client.prepareSearch(index).setTypes("table","2","3").setQuery(timeqb).setPostFilter(builder)
                    .addAggregation(aggregation).setFrom(0)
                    .setSize(1000).execute().actionGet();
            for (SearchHit sh : response.getHits().getHits()) {
//                System.out.println(sh.getSource().get("myAgg").getClass().getName());
            }
            System.out.println("totle:"+response.getHits().getTotalHits());
            Nested  agg = response.getAggregations().get("agg");
            System.out.println(agg.getDocCount());
            Terms name = agg.getAggregations().get("name");
//            Map<String,Object> metadata = response.getHits();
//            System.out.println("metaData<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//            Iterator<String> it = metadata.keySet()
//                    .iterator();
//            while(it.hasNext()){
//                String key = it.next();
//                System.out.println("key:"+key+",  value:"+metadata.get(key));
//            }
//            System.out.println("metaData<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            for (Terms.Bucket bucket : name.getBuckets()) {
                System.out.println(""+bucket.getKey()+": "+bucket.getDocCount());
//                ReverseNested resellerToProduct = bucket.getAggregations().get("reseller_to_product");
//                resellerToProduct.getDocCount(); // Doc count
            }
//            Nested thirdClass  = response.getAggregations().get("myAgg"); // 业务分类第三层数据
//
//            ValueCount ct = thirdClass.getAggregations().get("295e3a98f87a4e638ad8c9cfef9548ab");
//            long value = ct.getValue();
//            System.out.println("-----------------------------------------"+value+"--------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void alise(Client client) {
        IndicesAliasesRequestBuilder indicesAliasesRequestBuilder = client.admin().indices().prepareAliases();
    }
    public static void initRefTable(){
        applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
        DcSearchContentService dcSearchContentService = applicationContext.getBean(DcSearchContentService.class);
        DcObjectCataRefService dcObjectCataRefService = applicationContext.getBean(DcObjectCataRefService.class);
        List<DcSearchContent> unmarks = dcSearchContentService.getUnmark();
        DcObjectMainService dcObjectMainService = applicationContext.getBean(DcObjectMainService.class);
        List<DcObjectMain> list = dcObjectMainService.findList(new DcObjectMain());
        for(DcObjectMain obj : list){
            for (DcSearchContent ref: unmarks){
                DcObjectCataRef f = new DcObjectCataRef();
                f.setCataId(ref.getId());
                f.setObjId(obj.getId());
                dcObjectCataRefService.save(f);
            }
        }
    }

    public static void testBatchDelete(){
        applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
        DcSearchContentService dcSearchContentService = applicationContext.getBean(DcSearchContentService.class);
        DcObjectCataRefService dcObjectCataRefService = applicationContext.getBean(DcObjectCataRefService.class);
        List<DcSearchContent> unmarks = dcSearchContentService.getUnmark();
        DcObjectMainService dcObjectMainService = applicationContext.getBean(DcObjectMainService.class);
        String[] ids = new String[]{"45fe77e9c3d84fc8b643e75f17b3d102","2d44641fded64c55a8fdbe6eb669d642"};
        try {
            dcObjectMainService.deleteByIds(ids,"table");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
//        2cee2fda26b14620b4de32981fadf277
        String index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
//        search();
//        initRefTable();
        try {
            upload();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            createMapping(index,"table");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            creataIndex(index);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        testBatchDelete();
    }
    /**
     * @方法名称: creataIndex
     * @实现功能: 创建索引库
     * @param indexName  索引名称
     * @create by peijd at 2016年11月5日 上午9:15:52
     */
    public static void creataIndex( String indexName) throws Exception {
        Client client = DcEsUtil.getEsClient();
        Assert.hasText(indexName);
        // 检查索引是否存在
        if (checkIndex(client, indexName)) {
            return;
        }

        // 创建索引 方法1
        CreateIndexResponse cIndexResponse = client.admin().indices().prepareCreate(indexName).execute().actionGet();
        // 方法2
        // CreateIndexResponse cIndexResponse = client.admin().indices().create(new CreateIndexRequest("pjd")).actionGet();

    }

    /**
     * @方法名称: deleteIndex
     * @实现功能: 删除索引
     * @param client
     * @param indexName	索引名称
     * @create by peijd at 2016年11月5日 上午9:49:23
     */
    public static void deleteIndex( String indexName) throws Exception {
        Client client = DcEsUtil.getEsClient();
        Assert.hasText(indexName);
        // 检查索引是否存在
        if (checkIndex(client, indexName)) {
            //删除索引
            DeleteIndexResponse deleteResponse = client.admin().indices().prepareDelete(indexName).execute().actionGet();

        }
    }
    /**
     * @方法名称: checkIndex
     * @实现功能: 检查索引是否存在
     * @param client
     * @param indexName  索引名称
     * @return
     * @create by peijd at 2016年11月5日 上午9:13:20
     */
    public static boolean checkIndex(Client client, String indexName) throws Exception {
        IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(indexName);
        IndicesExistsResponse inExistsResponse = client.admin().indices().exists(inExistsRequest).actionGet();
        return inExistsResponse.isExists();
    }
    /**
     * 创建mapping(feid("indexAnalyzer","ik")该字段分词IK索引 ；feid("searchAnalyzer","ik")该字段分词ik查询；具体分词插件请看IK分词插件说明)
     * @param indices 索引名称；
     * @param mappingType 索引类型
     * @throws Exception
     */
    public static void createMapping(String indices,String mappingType)throws Exception{
        Client client = DcEsUtil.getEsClient();
        XContentBuilder builder= XContentFactory.jsonBuilder()
                .startObject()
                .startObject(indices)
                .startObject("properties")
                .startObject("cata").field("cata_id", "String").field("count", "int").field("store", "yes").endObject()
//                .startObject("kw").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
                .endObject()
                .endObject()
                .endObject();
        PutMappingRequest mapping = Requests.putMappingRequest(indices).type(mappingType).source(builder);
        client.admin().indices().putMapping(mapping).actionGet();
        client.close();
    }

    public void date2017_03_07(){
        //        String[] catalogs = new String[3];
//        for(String s : catalogs){
//            System.out.println(s+"1");
//        }
//        String s = null;
//        String a = "";
//        System.out.print(a+s);
//        String s = "cdf51ff99e184f43af69632b6f7c8c03,;11ba3fa13d3d4b3f8bfb0ff3c93c6c97,;e01b3febab57433fbde38c0a735604c5,;622dc1c580d74ab7acd1adc30503b95a,;a87a4b70c9134d4d89a7ae45d1c9a75b,;afa5bbfdb6be475b9ffd15a7778025d4,;295e3a98f87a4e638ad8c9cfef9548ab,;8a615c228a794629975c6ee9f88e5152,;4c1fa7484b2d4b8d8d91b5a00803cfe6,;ae128fda25374820900a8a9156108c25,;313eb7589a1c4bf0b48cdb9160a5726e,;c83a6134c34f433eb64513b63441f81d,;c2afd289cd14486ba3d2289fe187ab59,;2f50bdd4db764e5fbda32c6e10f9a77c,;18b1ca3420584827bcac4fa21d4d1a65,;6f13b6342f71465491031dc74d0195da,;d5c8a6319e5942a79f055bd2873572ad,;185f89cd23874356a1f12bf2c2f6ae36,;744c5840fafb4f8ca30fe3741479b156,;2e526de3d9024299a55c0d8a53f41451,";
//        unpackCatalogData(s);
//        DcCommonService dcCommonService = new DcCommonService();
        String labelSql = "SELECT a.LABEL_NAME as labelName,a.LABEL_DESC as labelDesc,  a.REMARKS as aremarks,"
                + "b.OBJ_CODE as objCode, b.OBJ_NAME as objName, b.OBJ_DESC as objDesc, b.SYSTEM_ID as systemId, "
                + "b.MANAGER_PER as managerPer, b.MANAGER_ORG as managerOrg, b.REMARKS as bremarks, "
                + "b.SORT_NUM as sortNum, b.CREATE_BY as createBy, b.CREATE_DATE as createDate, b.UPDATE_BY as updateBy, "
                + "b.UPDATE_DATE as updateDate  FROM dc_obj_label  a "
                + " LEFT JOIN dc_obj_main  b on b.ID = a.ID WHERE b.DEL_FLAG='0' ";
        String folderSql = "SELECT a.ID AS id,a.OBJ_CODE as objCode,a.OBJ_NAME as objName,a.SYSTEM_ID as systemId,"
                + "a.OBJ_DESC as objDesc,a.MANAGER_PER as managerPer,a.MANAGER_ORG as managerOrg,a.REMARKS as remarks,"
                + "a.SORT_NUM as sortNum,a.CREATE_BY as createBy,a.CREATE_DATE as createDate,a.UPDATE_BY as updateBy,"
                + "a.UPDATE_DATE as updateDate,b.FOLDER_NAME as folderName,"
                + "b.FOLDER_URL as folderUrl,b.IS_STRUCT as isStruct,b.REMARKS as remarks,"
                + "c.FILE_NAME as fileName,c.FILE_BELONG as fileBelong,"
                + "c.FILE_URL as fileUrl,c.IS_STRUCT as isStruct,c.SPLITTER as splitter,"
                + "c.REMARKS as remarks FROM dc_obj_main a "
                + "LEFT JOIN dc_obj_folder b ON b.OBJ_ID = a.ID "
                + "LEFT JOIN dc_obj_file c ON c.OBJ_ID = a.ID "
                + "WHERE a.DEL_FLAG = '0'and a.OBJ_TYPE ='6'";
        String interSql = "SELECT a.INTFC_TYPE as intfcType, a.INTFC_PROTOCAL as intfcProtocal, a.INTFC_NAMESPACE as intfcNamespace,"
                + "a.INTFC_USER as intfcUser, a.INTFC_PSWD as intfcPswd, a.REMARKS as aremarks,b.OBJ_CODE as objCode,"
                + " b.OBJ_NAME as objName,b.OBJ_TYPE as objType,b.OBJ_DESC as objDesc,b.SYSTEM_ID as systemId,"
                + "b.MANAGER_PER as managerPer,b.MANAGER_ORG as managerOrg,b.REMARKS as bremarks,b.SORT_NUM as sortNum,"
                + "b.CREATE_BY as createBy,b.CREATE_DATE as createDate,b.UPDATE_BY as updateBy,b.UPDATE_DATE as updateDate "
                + "FROM dc_obj_interface a, dc_obj_main b WHERE	a.OBJ_ID = b.ID and b.DEL_FLAG='0' ";
        System.out.print(interSql);
    }
    private static void unpackCatalogData(Object catalogsPath) {
        List<List<String>> catalogs = new ArrayList<>();
        String[] catalog = catalogsPath.toString().split(";");
        List<String[]> list = new ArrayList<>();
        for(int i=0;i<catalog.length;i++){
            list.add(catalog[i].split(","));
        }
        for(int i=0;i<3;i++){
            List<String> s = new ArrayList<>();
            for(int j =0;j<list.size();j++){
                String[] cataDetail = list.get(j);
                if(cataDetail.length>i && null != cataDetail[i] && !"".equals(cataDetail[i])){
                    s.add(cataDetail[i]);
                }
            }
            catalogs.add(s);
        }
        for(List<String> l1 : catalogs){
            for(String s1 : l1){
                System.out.print(s1+" ");
            }
            System.out.println("");
        }
    }

    public static String convertToDate(String searchTime){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fromDate = "";
        if (searchTime.equals("oneYear")) {//时间周期选择为最近一年
            fromDate=df.format(DateUtils.addMonths(date, -12));
        } else if (searchTime.equals("halfYear")) {//时间周期选择为最近半年
            fromDate=df.format(DateUtils.addMonths(date, -6));
        } else if (searchTime.equals("threeMonths")) {//时间周期选择为最近三个月
            fromDate=df.format(DateUtils.addMonths(date, -3));
        } else if (searchTime.equals("oneMonth")) {//时间周期选择为最近一个月
            fromDate=df.format(DateUtils.addMonths(date, -1));
        } else if (searchTime.equals("oneWeek")) {//时间周期选择为最近一周
            fromDate = df.format(DateUtils.addDays(date, -7));
        } else if (searchTime.equals("today")) {//时间周期选择为今天
            fromDate = df.format(date);
        }
        return fromDate;
    }
}
