package com.utime.burrowNest.storage.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.utime.burrowNest.storage.vo.EOrientation;

/**
 * MyBatis TypeHandler to map between EArchiveType enum and its lowercase string representation in DB.
 */
public class EOrientationTypeHandler extends BaseTypeHandler<EOrientation> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EOrientation parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getDscr()); // "zip", "7z", ...
    }

    @Override
    public EOrientation getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return toEnum(value);
    }

    @Override
    public EOrientation getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return toEnum(value);
    }

    @Override
    public EOrientation getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return toEnum(value);
    }

    private EOrientation toEnum(String orientation) {
        if (orientation == null) return null;
        for (EOrientation type : EOrientation.values()) {
            if (type.getDscr().equalsIgnoreCase(orientation)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown EOrientation type: " + orientation);
    }
}
