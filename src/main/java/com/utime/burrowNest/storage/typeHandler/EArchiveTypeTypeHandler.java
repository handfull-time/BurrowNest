package com.utime.burrowNest.storage.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.utime.burrowNest.storage.vo.EArchiveType;

/**
 * MyBatis TypeHandler to map between EArchiveType enum and its lowercase string representation in DB.
 */
public class EArchiveTypeTypeHandler extends BaseTypeHandler<EArchiveType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EArchiveType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getArchive()); // "zip", "7z", ...
    }

    @Override
    public EArchiveType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return toEnum(value);
    }

    @Override
    public EArchiveType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return toEnum(value);
    }

    @Override
    public EArchiveType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return toEnum(value);
    }

    private EArchiveType toEnum(String archive) {
        if (archive == null) return null;
        for (EArchiveType type : EArchiveType.values()) {
            if (type.getArchive().equalsIgnoreCase(archive)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown archive type: " + archive);
    }
}
