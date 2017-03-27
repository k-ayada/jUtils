package pub.ayada.genutils.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import pub.ayada.genutils.db.oraObjsPOJO.OBJECTS;
import pub.ayada.genutils.db.oraObjsPOJO.OWNER;
import pub.ayada.genutils.db.oraObjsPOJO.TYPE;

public class OraMeta {

	public static ArrayList<String> get_oraTbales_ArrayList(Connection conn) {		
		ArrayList<String> tables = new ArrayList<String>();
		try {
			ResultSet rs = conn.prepareStatement("SELECT TABLE_NAME FROM ALL_TABLE")
					           .executeQuery();
			while (rs.next()) 			
				tables.add(rs.getString(1));
			
			rs.close();		
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}	
		return tables;
	}
	public static String get_oraTables_JSON(Connection conn) {
		StringBuilder json = new StringBuilder("{ tables : [");
		for (String table : get_oraTbales_ArrayList(conn))
			json.append("\"").append(table).append("\",");
	 
		json.setLength(json.length() - 1);
		json.append("]}");
		return json.toString();
	}

	public static String getAllOraTableColumns(Connection conn, String[] Tables) throws SQLException {

		StringBuilder sql = new StringBuilder(
				"SELECT ALL_TAB_COLUMNS.TABLE_NAME,ALL_TAB_COLUMNS.COLUMN_NAME,DATA_TYPE,");
		sql.append(
				"CASE WHEN DATA_TYPE = 'NUMBER' THEN DATA_PRECISION ELSE DATA_LENGTH END AS DATA_LENGTH,nvl(DATA_SCALE,0),COMMENTS")
				.append(" FROM ALL_TAB_COLUMNS, ALL_COL_COMMENTS WHERE ALL_TAB_COLUMNS.TABLE_NAME = ALL_COL_COMMENTS.TABLE_NAME")
				.append(" AND ALL_TAB_COLUMNS.COLUMN_NAME = ALL_COL_COMMENTS.COLUMN_NAME");

		if (Tables != null && Tables.length > 0) {
			StringBuilder tbls = new StringBuilder(" AND ALL_TAB_COLUMNS.TABLE_NAME in (");

			for (String table : Tables) {
				tbls.append("'").append(table).append("',");
			}
			tbls.setLength(tbls.length() - 1);
			sql.append(tbls.toString()).append(")");
		}
		String table = "";
		HashMap<String, ArrayList<String>> tables = new HashMap<>();
		ResultSet rs = null;
		try {
			rs = conn.prepareStatement(sql.toString()).executeQuery();
			while (rs.next()) {
				if (!table.equals(rs.getString(1))) {
					table = rs.getString(1);
					tables.put(table, new ArrayList<String>());
				}
				StringBuilder coljson = new StringBuilder("{\"name\" : \"");
				coljson.append(rs.getString(2)).append("\", \"datatype\": \"").append(rs.getString(3))
						.append("\", \"length\": \"").append(rs.getInt(4));
				if (rs.getInt(5) > 0)
					coljson.append("(").append((rs.getInt(4) - rs.getInt(5))).append('.').append(rs.getInt(5))
							.append(')');
				coljson.append("\", \"description\": \"").append(rs.getString(6)).append("\"}");
				tables.get(table).add(coljson.toString());
			}
		} catch (SQLException e) {
			System.out.println("SQL fialed : " + sql.toString());
			throw new SQLException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				System.out.println("SQL fialed : " + sql.toString());
				throw new SQLException(e);
			}
		}
		StringBuilder json = new StringBuilder("{ \"data\" : [");
		for (Entry<String, ArrayList<String>> entry : tables.entrySet()) {
			json.append("{\"table\":\"").append(entry.getKey()).append("\", \"columns\": [");
			for (String coljson : entry.getValue()) {
				json.append(coljson).append(",");
			}
			json.setLength(json.length() - 1);
			json.append("]},");
		}
		json.setLength(json.length() - 1);
		json.append("]}");
		return json.toString();

	}

	public static String getOraTables(Connection conn) throws SQLException {

		StringBuilder sql = new StringBuilder("select all_tables.owner, all_tables.table_name, COMMENTS");
		sql.append("  from all_tables, all_tab_comments ")
				.append(" where all_tables.table_name = all_tab_comments.table_name ")
				.append("   and all_tables.owner not in ('DBA','SYS')").append(" order by all_tables.owner");

		ResultSet rs = conn.prepareStatement(sql.toString()).executeQuery();

		String owner = "";
		HashMap<String, ArrayList<String>> owners = new HashMap<>();

		while (rs.next()) {
			if (!owner.equals(rs.getString(1))) {
				owner = rs.getString(1);
				owners.put(owner, new ArrayList<String>());
			}
			StringBuilder tablejson = new StringBuilder("{\"name\" : \"");
			tablejson.append(rs.getString(2)).append("\", \"description\": \"").append(rs.getString(3)).append("\"}");
			owners.get(owner).add(tablejson.toString());
		}
		StringBuilder json = new StringBuilder("{ \"data\" : [");

		for (Entry<String, ArrayList<String>> entry : owners.entrySet()) {
			json.append("{\"owner\":\"").append(entry.getKey()).append("\", \"tables\": [");
			for (String tablejson : entry.getValue()) {
				json.append(tablejson).append(",");
			}
			json.setLength(json.length() - 1);
			json.append("]},");
		}
		json.setLength(json.length() - 1);
		json.append("]}");
		return json.toString();
	}

	public static OBJECTS getOraObjs(Connection conn) throws SQLException {

		StringBuilder sql = new StringBuilder(
				"SELECT OWNER, OBJECT_TYPE, OBJECT_NAME FROM ALL_OBJECTS WHERE OBJECT_TYPE in (");
		sql.append(
				"'TABLE', 'INDEX', 'TIGGER' ,'PROCEDURE' , 'FUNCTION', 'PACKAGE', 'PACKAGE BODY', 'PROCEDURE', 'PROGRAM', 'SYNONYM', 'TYPE', 'VIEW')")
				.append(" ORDER BY 1,2,3");

		ResultSet rs = conn.prepareStatement(sql.toString()).executeQuery();
		String OWNER = "";
		String OBJECT_TYPE = "";

		ArrayList<String> OraOBJECTS = new ArrayList<String>();
		TYPE OraOBJECT_TYPE = null;
		OWNER OraOBJ_OWN = null;
		OBJECTS OraOBJS = new OBJECTS();

		while (rs.next()) {

			if (!OBJECT_TYPE.equals(rs.getString("OBJECT_TYPE"))) {
				if (OraOBJECT_TYPE != null)
					OraOBJ_OWN.addType(OraOBJECT_TYPE);

				OBJECT_TYPE = rs.getString("OBJECT_TYPE");
				OraOBJECTS = new ArrayList<String>();
				OraOBJECT_TYPE = new TYPE(OBJECT_TYPE, OraOBJECTS); // Create
																	// the
			}
			OraOBJECTS.add(rs.getString("OBJECT_NAME"));

			if (!OWNER.equals(rs.getString("OWNER"))) {
				if (OraOBJ_OWN != null) {
					OraOBJS.addOwner(OraOBJ_OWN);
				}
				OWNER = rs.getString("OWNER");
				OraOBJ_OWN = new OWNER(OWNER);
			}
		}
		return OraOBJS;
	}

}
