package com.hlframe.task;

import com.hlframe.common.utils.IdGen;
import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.schedule.entity.DcTaskTime;
import com.hlframe.modules.dc.schedule.service.DcTaskTimeService;
import com.hlframe.modules.dc.task.entity.DcTaskLogRun;
import com.hlframe.modules.dc.task.service.DcTaskLogRunService;

import java.io.*;
import java.util.Date;

public class TaskShell implements ITask {
	/**
	 * 执行外部多个jar包任务，需要打包rar或者zip上传，并指定执行的bat文件名或者shell文件名
	 * @param taskInfo
	 * @return
	 */
	public DcDataResult doTask(final TaskInfo taskInfo) {
		final DcDataResult result = new DcDataResult();
		try {
			Thread syncThread = new Thread(new Runnable() {
				public void run(){
					DcTaskLogRunService run = SpringContextHolder.getBean(DcTaskLogRunService.class);
					DcTaskLogRun runobj = new DcTaskLogRun();
					DcTaskTime dcTaskTime=new DcTaskTime();
					DcTaskTimeService obj = SpringContextHolder.getBean(DcTaskTimeService.class);
					try {
						// 记录任务开始日志
						runobj.setIsNewRecord(true);
						runobj.setId(IdGen.uuid());
						runobj.setTaskid(taskInfo.getTaskid());
						runobj.setRunid(taskInfo.getRunid());
						runobj.setStartdate(new Date());
						runobj.setStatus(DcTaskTime.TASK_STATUS_RUNNING);
						run.save(runobj);

						File dir = new File(taskInfo.getFilePath());
						String exitValue = showAllFiles(dir, taskInfo.getMethodName());
						// 记录任务结束日志
						runobj.setIsNewRecord(false);
						runobj.setEnddate(new Date());
						runobj.setStatus(DcTaskTime.TASK_STATUS_SUCCESS);
						runobj.setRemarks(String.valueOf(exitValue));
						run.save(runobj);

						// 非自动清空下，需要将任务设置为完成状态
						dcTaskTime.setId(taskInfo.getTaskid());
						if (!DcTaskTime.TASK_TRIGGERTYPE_AUTO.equalsIgnoreCase(taskInfo.getExecuteType())) {
							dcTaskTime.setStatus(DcTaskTime.TASK_STATUS_SUCCESS);
						}
						//dcTaskTime.setResult(runresult);
						//obj.updateStatus(dcTaskTime);
						obj.save(dcTaskTime);
						result.setRst_flag(true);
						result.setRst_std_msg(exitValue);
					} catch (Exception e) {
						result.setRst_flag(false);
						result.setRst_err_msg(e.getMessage()+"\n");
						// 记录任务错误日志
						runobj.setIsNewRecord(false);
						runobj.setEnddate(new Date());
						runobj.setStatus(DcTaskTime.TASK_STATUS_ERROR);
						runobj.setRemarks("执行任务[" + taskInfo.getTaskName() + "]失败 : " + e.getMessage());
						run.save(runobj);

						// 非自动清空下，需要将任务设置为完成错误

						dcTaskTime.setId(taskInfo.getTaskid());
						if (!DcTaskTime.TASK_TRIGGERTYPE_AUTO.equalsIgnoreCase(taskInfo.getExecuteType())) {
							dcTaskTime.setStatus(DcTaskTime.TASK_STATUS_ERROR);
						}
						//dcTaskTime.setResult(runresult);
						//obj.updateStatus(dcTaskTime);
						obj.save(dcTaskTime);
					}
				}



				private String showAllFiles(File dir, String fileName) throws Exception {
					String exitValue = "";
					File[] fs = dir.listFiles();
					//如果文件夹下不存在路径
					if(fs==null){
						throw new Exception("文件异常，请检查文件是否存在");
					}
					for (File file : fs) {
						if (file.isDirectory()) {
							try {
								exitValue = showAllFiles(file, fileName);
							} catch (Exception e) {
							}
						} else {
							// 如果发现同名则执行文件
							if (fileName.endsWith("sh") || fileName.endsWith("SH")) {
								exitValue = this.callShell(file);
							} else if (fileName.endsWith("bat") || fileName.endsWith("BAT")) {
								exitValue = this.callBat(file);
							} else {
								throw new Exception("文件类型错误，请检查是否是bat或者sh文件");
							}
							break;
						}
					}
					return exitValue;
				}

				private String callBat(File file) throws Exception {
					int result = -1;
					String str = "";
					try {
						Process pcs = Runtime.getRuntime().exec("cmd /c " + file.getPath());//C:\\TEMP\\A.bat 2>&1
						InputStream in = pcs.getInputStream();
						int c;
						while ((c = in.read()) != -1) {
						}
						InputStream input = pcs.getErrorStream();
						ByteArrayOutputStream  baos = new ByteArrayOutputStream ();
						int i=0;
						while((i=input.read())!=-1){
							baos.write(i);
						}
						str = baos.toString();
						in.close();


						pcs.waitFor();
						result = pcs.exitValue();
					} catch (Exception e) {
						throw new Exception("执行bat文件错误:"+e.getMessage());
					}
					return str;
				}

				private String callShell(File file) throws Exception {
					int result = -1;
					String str = "";
					try {
						Runtime rt = Runtime.getRuntime();
						Process pcs = rt.exec("chmod 777 －R "+file.getPath());
						pcs.waitFor();
						pcs = rt.exec(file.getPath());
						pcs.waitFor();
						BufferedReader br = new BufferedReader(new InputStreamReader(pcs.getInputStream()));
						String line = new String();
						while ((line = br.readLine()) != null) {
						}

						InputStream input = pcs.getErrorStream();
						ByteArrayOutputStream  baos = new ByteArrayOutputStream ();
						int i=0;
						while((i=input.read())!=-1){
							baos.write(i);
						}
						str = baos.toString();

						br.close();
						result = pcs.exitValue();
					} catch (Exception e) {
						throw new Exception("执行sh文件错误:"+e.getMessage());
					}
					return str;
				}

			});
			syncThread.start();
			//同步执行标志, 任务执行完成再返回
			if(taskInfo.getSyncFlag()){
				syncThread.join();
			}
		//	result.setRst_flag(true);
		//	result.setRst_std_msg("任务["+taskInfo.getTaskName()+"]执行成功!");

		} catch (Exception e) {
		//	result.setRst_flag(false);
		//	result.setRst_err_msg("任务["+taskInfo.getTaskName()+"]执行失败! "+e.getMessage());
			logger.error("-->TaskShell["+taskInfo.getTaskName()+"].doTask: ", e);
		}
		return result;
	}
}
