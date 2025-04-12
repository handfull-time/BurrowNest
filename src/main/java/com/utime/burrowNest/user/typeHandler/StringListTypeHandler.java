package com.utime.burrowNest.user.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StringListTypeHandler extends BaseTypeHandler<List<String>> {

	final static ObjectMapper mapper = new ObjectMapper();
	
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        
    	String jsonArray;
    	if( parameter == null ) {
    		jsonArray = "[]";
    	}else {
    		try {
				jsonArray = mapper.writeValueAsString(parameter);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				jsonArray = "[]";
			}
    	}
    	
        ps.setString(i, jsonArray);
    }
    
    
    private List<String> getArray(String array) throws SQLException{
    	if (array == null) {
			return new ArrayList<>();
        }
    	
        try {
			return mapper.readValue(array,  new TypeReference<List<String>>() {});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.getArray( rs.getString(columnName) );
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.getArray( rs.getString(columnIndex) );
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    	return this.getArray( cs.getString(columnIndex) );
    }
}