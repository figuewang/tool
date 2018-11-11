package cn.com.yusys.yusp.tool.service.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import cn.com.yusys.yusp.tool.domain.dto.ColumnDto;
import cn.com.yusys.yusp.tool.domain.dto.TableDto;
import cn.com.yusys.yusp.tool.service.DataBaseService;
@Service
public class DataBaseServiceImpl implements DataBaseService {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public List<TableDto> getAllTables(Connection con,String schema) throws SQLException {
		DatabaseMetaData detaData = con.getMetaData();
		ResultSet rs = detaData.getTables(con.getCatalog(), schema.toUpperCase(), null, new String[] { "TABLE" });
		List<TableDto> tables = new ArrayList<TableDto>();
		while (rs.next()) {
			TableDto table = new TableDto();
			table.setName(rs.getString("TABLE_NAME"));
			table.setRemarks(rs.getString("REMARKS"));
			tables.add(table);
		}
		return tables;
	}

	@Override
	public TableDto getOneTableInfo(String schema, String tableName, Connection con) throws SQLException {
		DatabaseMetaData detaData = con.getMetaData();
		ResultSet rs = detaData.getColumns(null, schema.toUpperCase(), tableName.toUpperCase(), "%");
		
		TableDto table = new TableDto();
		Map<String,ColumnDto> cols = new HashMap<String,ColumnDto>();
		while (rs.next()) {
			 ColumnDto colT = new ColumnDto();
			 colT.setJdbcType(rs.getInt("DATA_TYPE"));
			 colT.setLength(rs.getInt("COLUMN_SIZE"));
			 colT.setActualColumnName(rs.getString("COLUMN_NAME"));
			 colT.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
			 colT.setScale(rs.getInt("DECIMAL_DIGITS"));
			 colT.setRemarks(rs.getString("REMARKS"));
			 colT.setDefaultValue(rs.getString("COLUMN_DEF"));
			 
			 cols.put(rs.getString("COLUMN_NAME"),colT);
			 
			 table.setName(rs.getString("TABLE_NAME"));
			 table.setRemarks(rs.getString("TABLE_CAT"));
			 table.setSchem(rs.getString("TABLE_SCHEM"));			
		 }
		 rs.close();
		 table.setCols(cols);
		 log.debug("表名："+table.getName()+",列数："+table.getCols().size());
		return table;
	}
	
}
