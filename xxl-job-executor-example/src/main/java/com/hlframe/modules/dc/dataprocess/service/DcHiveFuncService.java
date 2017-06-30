/********************** 版权声明 *************************
 * 文件名: DcHiveFuncService.java
 * 包名: com.hlframe.modules.dc.dataprocess.service
 * 版权:	杭州华量软件  hldc
 * 职责: 
 ********************************************************
 *
 * 创建者：Primo  创建时间：2017/5/24
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.modules.dc.dataprocess.dao.DcHiveFuncDao;
import com.hlframe.modules.dc.dataprocess.entity.DcHiveFunction;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.core.SysConfigOption;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DcHiveFuncService extends CrudService<DcHiveFuncDao, DcHiveFunction> {
    private ConnBean cb = new ConnBean(DcPropertyUtils.getProperty("distcp.client.address"),
            DcPropertyUtils.getProperty("distcp.client.loginUser"), DcPropertyUtils.getProperty("distcp.client.loginPswd"));
    private static SSHExec ssh = null;

    @Autowired
    private DcQueryHiveService dcQueryHiveService;

    @Transactional(readOnly = false)
    public boolean register(String id) {
        DcHiveFunction dcHiveFunction = get(id);
        if (dcHiveFunction == null){
            return false;
        }
        // get ssh instance.
        ssh = SSHExec.getInstance(cb);
        try {
            // 连接到hdfs
            ssh.connect();
            //          build create function script for jdbc
            String script = createFunction4JDBC(dcHiveFunction);
            List<Map<String, Object>> result = dcQueryHiveService.runSql(script,"default");

//          build create function script for command line.
            script = "hive -e \""+script+"\"";
            // set the inteval time 1 second in order to response more quickly.
            SysConfigOption.INTEVAL_TIME_BETWEEN_TASKS =1000L;
            // exe script
            ssh.exec(new ExecCommand(script));
            // if script finished successfully
            if (result.size()>0 && result.get(0).get("result").equals("success")){
                //change the status
                dcHiveFunction.setStatus("2");
                //save
                save(dcHiveFunction);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("register error function id:"+dcHiveFunction.getId(),e);
        }finally{
            //reset the inteval time
            SysConfigOption.INTEVAL_TIME_BETWEEN_TASKS =5000L;
            if(null!=ssh){
                ssh.disconnect();
            }
        }
        return false;
    }

    @Transactional(readOnly = false)
    public boolean unregister(String id) {
        DcHiveFunction dcHiveFunction = get(id);
        if (dcHiveFunction == null){
            return false;
        }
        ssh = SSHExec.getInstance(cb);
        try {
            // 连接到hdfs
            ssh.connect();
//         get drop script
            String script = dropFunction4JDBC(dcHiveFunction);
            List<Map<String,Object>> result = dcQueryHiveService.runSql(script,"default");

            script = "hive -e \""+script+"\"";
            // set the inteval time 1 second in order to response more quickly.
            SysConfigOption.INTEVAL_TIME_BETWEEN_TASKS =1000L;
            ssh.exec(new ExecCommand(script));
// check if the script is executed successfully
            if (result.size()>0 && result.get(0).get("result").equals("success")){
                dcHiveFunction.setStatus("1");
                save(dcHiveFunction);
                return true;
            }
        } catch (Exception e) {
            logger.error("unregister error function id:"+dcHiveFunction.getId(),e);
        }finally{
            //reset inteval time
            SysConfigOption.INTEVAL_TIME_BETWEEN_TASKS =5000L;
            if(null!=ssh){
                ssh.disconnect();
            }
        }
        return false;
    }

    private String getJarFromHdfs(DcHiveFunction dcHiveFunction){
        StringBuilder sb = new StringBuilder();
        // get the jar from hdfs to the folder /tmp
        sb.append("hadoop fs -get ").append(DcPropertyUtils.getProperty("hive.udf.path")).append(dcHiveFunction.getJarName()).append(" /tmp/").append(dcHiveFunction.getJarName());
        return sb.toString();
    }

    private String uploadJar2Hive(DcHiveFunction dcHiveFunction){
        StringBuilder sb = new StringBuilder();
        // upload the jar hive environment from /tmp
        sb.append("hive -e \"add jar /tmp/"+dcHiveFunction.getJarName()+"\"");
        return sb.toString();
    }

    @Deprecated
    private String createFunction(DcHiveFunction dcHiveFunction){
        StringBuilder sb = new StringBuilder();
        // upload the jar hive environment from /tmp  hive.udf.path
//        create function add_tt as 'com.hzhl.udf.TestAddUFD' using jar 'hdfs:///home/jar/jerseyHive.jar'
//         hive -e "CREATE FUNCTION abcd as 'com.hzhl.udf.TestAddUFD' USING JAR 'hdfs:///home/jar/jerseyHive.jar';"
        sb.append("hive -e \"CREATE FUNCTION "+dcHiveFunction.getFuncName()+" as '"+dcHiveFunction.getClassPath()+
                "' USING JAR '"+ DcPropertyUtils.getProperty("hive.udf.path")+dcHiveFunction.getJarName()+"';\"");
        String s = "hive -e \""+createFunction4JDBC(dcHiveFunction)+"\"";
        return s;
    }

    private String createFunction4JDBC(DcHiveFunction dcHiveFunction){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE FUNCTION "+dcHiveFunction.getFuncName()+" as '"+dcHiveFunction.getClassPath()+
                "' USING JAR 'hdfs://"+ DcPropertyUtils.getProperty("hive.udf.path")+dcHiveFunction.getJarName()+"';");
        return sb.toString();
    }

    @Deprecated
    private String dropFunction(DcHiveFunction dcHiveFunction){
        StringBuilder sb = new StringBuilder();
//         hive -e "DROP FUNCTION abcd;"
        sb.append("hive -e \"DROP FUNCTION "+dcHiveFunction.getFuncName()+";\"");
        return sb.toString();
    }

    private String dropFunction4JDBC(DcHiveFunction dcHiveFunction){
        StringBuilder sb = new StringBuilder();
//         hive -e "DROP FUNCTION abcd;"
        sb.append("DROP FUNCTION "+dcHiveFunction.getFuncName()+";");
        return sb.toString();
    }


    private String deleteTempJar(DcHiveFunction dcHiveFunction){
        StringBuilder sb = new StringBuilder();
        // delete the temp file at /tmp
        sb.append("rm -rf /tmp/").append(dcHiveFunction.getJarName());
        return sb.toString();
    }


    public static void main(String[] args) {
        DcHiveFuncService service = new DcHiveFuncService();
        DcHiveFunction dcHiveFunction = new DcHiveFunction();
        dcHiveFunction.setFuncName("add_t");
        dcHiveFunction.setJarName("jerseyHive.jar");
        dcHiveFunction.setClassPath("com.hzhl.udf.TestAddUFD");
        if (dcHiveFunction == null){
            return;
        }
        ssh = SSHExec.getInstance(service.cb);
        try {
            // 连接到hdfs
            ssh.connect();
//          get the jar from hdfs to the folder /tmp
            String script = service.createFunction(dcHiveFunction);
            Result res = ssh.exec(new ExecCommand(script));
            System.out.println(res.error_msg);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(null!=ssh){
                ssh.disconnect();
            }
        }
    }
}
