package cn.com.yusys.yusp.tool.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import cn.com.yusys.yusp.tool.domain.dto.TableDto;

public interface DataBaseService {
	List<TableDto> getAllTables(Connection con,String schema) throws SQLException;
	TableDto getOneTableInfo(String schema, String tableName,Connection con) throws SQLException;
}
