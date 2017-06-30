/********************** 版权声明 *************************
 * 文件名: DcDataSourceService.java
 * 包名: com.hlframe.modules.dc.metadata.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月7日 下午1:44:42
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.metadata.dao.DcDataSourceDao;
import com.hlframe.modules.dc.metadata.entity.DcDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 
 * @类名: com.hlframe.modules.dc.metadata.service.DcDataSourceService.java 
 * @职责说明: TODO
 * @创建者: peijd
 * @创建时间: 2016年11月7日 下午1:44:42
 */
@Service
@Transactional(readOnly = true)
public class DcDataSourceService extends CrudService<DcDataSourceDao, DcDataSource> {
	
	
	/**
	 * @实现功能: 数据权限过滤
	 * @create by yuzh at 2016年12月15日 上午11:30:29
	 */
	public Page<DcDataSource> findPage(Page<DcDataSource> page, DcDataSource dcDataSource) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		dcDataSource.getSqlMap().put("dsf", dataScopeFilter(dcDataSource.getCurrentUser(),"o","u"));
		// 设置分页参数
		dcDataSource.setPage(page);
		// 执行分页查询
		page.setList(dao.findList(dcDataSource));
		return super.findPage(page, dcDataSource);
	}
	
	
	/**
	 * @方法名称: initDBConnInfo 
	 * @实现功能: 初始化数据库连接信息, 目前先支持5种, 碰到其他数据源再增加
	 * @param dcDataSource
	 * @create by peijd at 2016年11月8日 上午11:00:19
	 */
	public void initDBConnInfo(DcDataSource dcDataSource) {
		// 数据库类型
		String dbType = dcDataSource.getServerType();

		// 数据库驱动 如果为空则默认装载驱动
		if (StringUtils.isEmpty(dcDataSource.getDriverClass())) {
			dcDataSource.setDriverClass(DcDataSource.dbDriverMap.get(dbType));
		}

		// 数据库连接字符串 如果为空则按默认驱动连接构建 jdbc:oracle:thin:@10.1.20.86:1521:orcl
		// if (StringUtils.isBlank(connect.getConnString())) {
		StringBuilder connUrl = new StringBuilder(64);
		connUrl.append(DcDataSource.dbConnUrlMap.get(dbType));
		// oracle jdbc:oracle:thin:@10.1.20.86:1521:orcl
		if ("dc_oracle".equalsIgnoreCase(dbType)) {
			connUrl.append(dcDataSource.getServerIP()).append(":");
			connUrl.append(dcDataSource.getServerPort()).append(":");
			connUrl.append(dcDataSource.getServerName());

			// mysql jdbc:mysql://<host>:<port>/<database_name>
		} else if ("dc_mysql".equalsIgnoreCase(dbType)) {
			connUrl.append(dcDataSource.getServerIP()).append(":");
			connUrl.append(dcDataSource.getServerPort()).append("/");
			connUrl.append(dcDataSource.getServerName());

			// db2 jdbc:db2://<host>:<port>/<database_name>
		} else if ("dc_db2".equalsIgnoreCase(dbType)) {
			connUrl.append(dcDataSource.getServerIP()).append(":");
			connUrl.append(dcDataSource.getServerPort()).append("/");
			connUrl.append(dcDataSource.getServerName());

			// sqlserver
			// jdbc:sqlserver://<host>:<port>;databaseName=<database_name>
		} else if ("dc_sqlserver".equalsIgnoreCase(dbType)) {
			connUrl.append(dcDataSource.getServerIP()).append(":");
			connUrl.append(dcDataSource.getServerPort()).append(";");
			connUrl.append("databaseName=").append(dcDataSource.getServerName());

			// postgresql jdbc:postgresql://<host>:<port>/<database_name>
		} else if ("dc_postgresql".equalsIgnoreCase(dbType)) {
			connUrl.append(dcDataSource.getServerIP()).append(":");
			connUrl.append(dcDataSource.getServerPort()).append("/");
			connUrl.append(dcDataSource.getServerName());

			// impala 数据转换任务
			// jdbc:impala://<host>:<port>/<database_name>
		} else if ("dc_impala".equalsIgnoreCase(dbType)) {
			connUrl.append(dcDataSource.getServerIP()).append(":");
			connUrl.append(dcDataSource.getServerPort()).append("/");
			connUrl.append(dcDataSource.getServerName());
			
			// hive 数据转换任务
			// jdbc:hive2://<host>:<port>/<database_name>
		} else if ("dc_hive".equalsIgnoreCase(dbType)) {
			connUrl.append(dcDataSource.getServerIP()).append(":");
			connUrl.append(dcDataSource.getServerPort()).append("/");
			connUrl.append(StringUtils.isNotBlank(dcDataSource.getServerName())?dcDataSource.getServerName():"default");
		} else {
			connUrl.setLength(0);
		}

		if (connUrl.length() > 0) {
			dcDataSource.setServerUrl(connUrl.toString());
		}
	}
	
	/**
	 * @方法名称: buildSelectList 
	 * @实现功能: 构建数据源选择下拉列表
	 * @param param	过滤条件
	 * @return
	 * @create by peijd at 2016年12月3日 下午2:04:13
	 */
	public List<DcDataSource> buildSelectList(DcDataSource param){
		List<DcDataSource> dataSrcList = findList(param);
		StringBuilder comment = new StringBuilder(64);
		for(DcDataSource dataSrc: dataSrcList){
			comment.append("(").append(dataSrc.getServerType()).append(")").append(dataSrc.getConnName());
			dataSrc.setConnName(comment.toString());
			comment.setLength(0);
		}
		return dataSrcList;
	}
	public DcDataSource getlist(String sid){
		return dao.getlist(sid);
	}
}
