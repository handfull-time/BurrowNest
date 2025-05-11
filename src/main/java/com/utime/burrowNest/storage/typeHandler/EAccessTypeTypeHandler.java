package com.utime.burrowNest.storage.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.utime.burrowNest.storage.vo.EAccessType;

/**
 * MyBatis TypeHandler to map between EAccessType enum.
 */
public class EAccessTypeTypeHandler extends BaseTypeHandler<EAccessType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EAccessType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getBit());
    }

    @Override
    public EAccessType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return EAccessType.getEAccessType(rs.getInt(columnName));
    }

    @Override
    public EAccessType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return EAccessType.getEAccessType(rs.getInt(columnIndex));
    }

    @Override
    public EAccessType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return EAccessType.getEAccessType(cs.getInt(columnIndex));
    }

}
