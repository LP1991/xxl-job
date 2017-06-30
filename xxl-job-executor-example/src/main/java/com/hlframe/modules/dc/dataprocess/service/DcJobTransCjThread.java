/********************** 版权声明 *************************
 * 文件名: DcJobTransCjThread.java
 * 包名: com.hlframe.modules.dc.dataprocess.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：hgw   创建时间：2017年4月24日 上午10:28:19
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.modules.dc.schedule.entity.DcTaskQuery;
import com.hlframe.modules.dc.utils.DcTaskQueryUtils;
import net.neoremind.sshxcute.core.SSHExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.service.DcJobTransCjThread.java 
 * @职责说明: TODO
 * @创建者: hgw
 * @创建时间: 2017年4月24日 上午10:28:19
 */
public class DcJobTransCjThread extends Thread{
	    private SSHExec ssh;

	    
	    private DcTaskQuery dtq;

	    protected Logger logger = LoggerFactory.getLogger(this.getClass());


	    @Override
	    public void run() {
	        try {
	            dtq = DcTaskQueryUtils.getDcTaskQueryYes();
	            ssh = dtq.getSsh();
	            //获取下一个table
	        }catch (Exception e){
	            e.printStackTrace();
	            logger.error("-->extract data from DB: runTask,"+this.getClass().getName(),e);
	        }

	    }
	    


		public SSHExec getSsh() {
			return ssh;
		}

		public void setSsh(SSHExec ssh) {
			this.ssh = ssh;
		}

		public DcTaskQuery getDtq() {
			return dtq;
		}

		public void setDtq(DcTaskQuery dtq) {
			this.dtq = dtq;
		}
	    
		
	    
}
