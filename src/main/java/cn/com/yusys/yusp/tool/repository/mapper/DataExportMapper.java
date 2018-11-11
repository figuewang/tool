package cn.com.yusys.yusp.tool.repository.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface DataExportMapper {
    List<Map<String,Object>> selectAllData(@Param("tableName") String tableName);
    int selectTotal(@Param("tableName") String tableName);
}