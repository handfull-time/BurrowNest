package com.utime.burrowNest.common.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.utime.burrowNest.common.vo.BurrowDefine;

@Deprecated
public class BooleanTypeHandler extends BaseTypeHandler<Boolean> {
	
	private static final String YES = BurrowDefine.YES;
	private static final String NO = BurrowDefine.NO;

	@Override
	public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return convertStringToBooelan(rs.getString(columnName));
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return convertStringToBooelan(rs.getString(columnIndex));
	}

	@Override
	public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return convertStringToBooelan(cs.getString(columnIndex));
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
		if( parameter != null ) {
			boolean bFlag = parameter.booleanValue();
			ps.setString(i, bFlag ? YES : NO);	
		}		
	}
	
	private Boolean convertStringToBooelan(String strValue) throws SQLException {
		if (YES.equalsIgnoreCase(strValue)) {
			return Boolean.valueOf(true);
		} else if (NO.equalsIgnoreCase(strValue)) {
			return Boolean.valueOf(false);
		} else {
			throw new SQLException("Unexpected value " + strValue + " found where " + YES + " or " + NO + " was expected.");
		}
	}
}