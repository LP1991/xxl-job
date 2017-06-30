package com.hlframe.modules.dc.schedule.service;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.utils.DateUtils;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.schedule.dao.DcObjeMonitorDao;
import com.hlframe.modules.dc.schedule.entity.DcObjeMonitor;
import com.hlframe.modules.dc.schedule.service.task.DcTaskService;
import com.hlframe.modules.dc.utils.DcCommonUtils;
import com.hlframe.modules.dc.utils.Des;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/15.
 */
@Service
@Transactional(readOnly = true)
public  class DcFtpMonitorService extends CrudService<DcObjeMonitorDao,DcObjeMonitor> implements DcTaskService {
    @Autowired
    private DcObjeMonitorDao dcObjeMonitorDao;


    @Transactional(readOnly = false)
    public DcDataResult runTask(String user) {

        StringBuffer resultSql = new StringBuffer(64);
        resultSql.append("select count(0) as count ,a.job_id as jobid, a.ftp_ip as ip, a.port as port,a.account as account,a.password as password,a.pathname as pathname,b.jobname AS jobname,DATE_FORMAT(b.UPDATE_DATE,'%H-%m-%s') as date from dc_job_transfile_hdfs a LEFT JOIN dc_job_transfile b on(a.job_id=b.id)WHERE b.del_flag='0'group by jobname");
        List<Map<String, Object>> resullist = DcCommonUtils.queryDataBySql(resultSql.toString());
        for (Map<String, Object> index : resullist) {
            Socket socket = new Socket();
            try {
                String ip = index.get("ip").toString();
                String account = index.get("account").toString();
                String password = index.get("password").toString();
                //  System.out.println(index.get("pathname").toString());
                socket.connect(new InetSocketAddress(ip, Integer.parseInt(index.get("port").toString())));
                FTPClient ftpClient = new FTPClient();
                //
                ftpClient.setDefaultPort(Integer.parseInt(index.get("port").toString()));
                ftpClient.connect(index.get("ip").toString());
                ftpClient.setControlEncoding("utf-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.login(account, Des.strDec(password));
                ftpClient.enterLocalActiveMode();
                String pathname = index.get("pathname").toString();//路径规则 根目录不需要/
                boolean flag = false;
                if (StringUtils.isNoneEmpty(pathname)) {
                    pathname = pathname.replaceAll("ftp://" + index.get("ip").toString() + "" + index.get("port").toString() + "/", "");
                    if (pathname.startsWith("/")) {
                        pathname = pathname.replaceFirst("/", "./");
                    }
                    pathname = new String(pathname.getBytes("utf-8"), "iso-8859-1");
                    flag = ftpClient.changeWorkingDirectory(pathname);
                }
                if (flag) {
                    DcObjeMonitor dcObjeMonitor = new DcObjeMonitor();
                    dcObjeMonitor.setType("ftp");
                    dcObjeMonitor.setRetum("是");
                    dcObjeMonitor.setTid(index.get("jobid").toString());
                    dcObjeMonitor.preInsert();
                    dcObjeMonitorDao.insert(dcObjeMonitor);

                } else {
                    DcObjeMonitor dcObjeMonitor = new DcObjeMonitor();
                    dcObjeMonitor.setType("ftp");
                    dcObjeMonitor.setRetum("否");
                    dcObjeMonitor.setTid(index.get("jobid").toString());
                    dcObjeMonitor.setAbnorma("ftp目录[" + pathname + "]不存在！");
                    dcObjeMonitor.preInsert();

                    dcObjeMonitorDao.insert(dcObjeMonitor);
                }
            } catch (Exception e) {
                DcObjeMonitor dcObjeMonitor = new DcObjeMonitor();
                dcObjeMonitor.setType("ftp");
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
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        DcDataResult result = new DcDataResult();
        result.setRst_flag(true);
        result.setRst_std_msg("测试任务:TestTaskService"+ DateUtils.getDateTime());
        System.out.println("---->测试任务:"+result.getRst_std_msg());
        return result;

    }

}
