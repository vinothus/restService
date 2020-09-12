package com.vin.rest.repository;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import com.healthmarketscience.sqlbuilder.dbspec.Constraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbConstraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import com.vin.rest.exception.ServiceNotFoundException;
import com.vin.rest.model.EmployeeEntity;
import com.vin.validation.ParamsValidator;
import com.vin.validation.ServiceConstraintViolation;

@Repository
public class EmployeeRepositaryImpl {

	Logger log = Logger.getLogger(EmployeeRepositaryImpl.class.getName());
	@Autowired
	DataSource dataSource;
	@Autowired
	JdbcTemplate jdbcTemplate;
	static DbSchema schemaObj;
	static DbSpec specficationObj;
	public static Map<String, String> serviceTableMap;
	public static Map<DbTable, List<DbColumn>> tableColumnMap;
	static Map<String, Map<String, String>> serviceAttrbMap;
    String serviceNTFEx="Service not found Exception";
	public void init() {

		try {
			tableColumnMap = getMetaDatum();
			initGoldenTables();

			getServiceTableMap();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initGoldenTables() throws Exception {
		List<DbTable> serviceTables = initializeTable();
		boolean serviceTabisPresent = false;
		for (Iterator<DbTable> iterator = serviceTables.iterator(); iterator.hasNext();) {
			DbTable dbTable = iterator.next();
			for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMap.entrySet()) {
				DbTable table = entry.getKey();

				if (dbTable.getName().equalsIgnoreCase(table.getName())) {
					serviceTabisPresent = true;
					log.info("Table Already Present: ");
				}
			}

		}
		if (!serviceTabisPresent) {
			for (Iterator<DbTable> iterator = serviceTables.iterator(); iterator.hasNext();) {
				DbTable dbTable = iterator.next();
				createDbTable(dbTable);

			}

		}
		tableColumnMap = getMetaDatum();
		insertServiceTables();

	}

	public EmployeeEntity getEmployeeByName(String name) {

		List<Map<String, Object>> result = jdbcTemplate
				.queryForList("select * from TBL_EMPLOYEES where first_name = ? ", new Object[] { name });
		 
		EmployeeEntity employeeEntity = null;
		for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			employeeEntity = new EmployeeEntity();
			employeeEntity.setEmail(map.get("email").toString());
			employeeEntity.setFirstName(map.get("first_name").toString());
			employeeEntity.setLastName(map.get("last_name").toString());
			employeeEntity.setId(Long.parseLong(map.get("id").toString()));
		}
		return employeeEntity;
	}

	public List<EmployeeEntity> getAll() {
		List<EmployeeEntity> listEmp = new ArrayList<EmployeeEntity>();
		List<Map<String, Object>> result = jdbcTemplate.queryForList("select * from TBL_EMPLOYEES  ");
		 
		EmployeeEntity employeeEntity = null;
		for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			employeeEntity = new EmployeeEntity();
			employeeEntity.setEmail(map.get("email").toString());
			employeeEntity.setFirstName(map.get("first_name").toString());
			employeeEntity.setLastName(map.get("last_name").toString());
			employeeEntity.setId(Long.parseLong(map.get("id").toString()));
			listEmp.add(employeeEntity);
		}
		return listEmp;
	}

	private  void loadSQLBuilderSchema() {
		specficationObj = new DbSpec();
		
		schemaObj = specficationObj.addDefaultSchema();
	}

	public List<Map<String, Object>> getServiceDataByName(String name) throws Exception {
		 
		return getDataForParams(name, new HashMap<>());
	}

	public Map<DbTable, List<DbColumn>> getMetaDatum()  {
		loadSQLBuilderSchema();
		Map<DbTable, List<DbColumn>> metaDatum = new HashMap<>();
		DatabaseMetaData md;
		ResultSet rs ;
		try {
			md = dataSource.getConnection().getMetaData();
			if(dataSource.getConnection().getMetaData().getURL().contains("mysql"))
			{
			List<Map<String,Object>>	tableresult=jdbcTemplate.queryForList("show tables");
				for (Iterator iterator = tableresult.iterator(); iterator.hasNext();) {
					Map<String, Object> map = (Map<String, Object>) iterator.next();
					System.out.println(map);
					for (Entry<String, Object> entry : map.entrySet())  
					{   System.out.println("Key = " + entry.getKey() + 
			                             ", Value = " + entry.getValue()); 
					DbTable tableNameT = schemaObj.addTable(entry.getValue().toString());
					List<DbColumn> listArray = new ArrayList<>();
					List rsmdList = (List) jdbcTemplate.query("desc "+entry.getValue(),new ResultSetExtractor() {
				        @Override
				        public ResultSetMetaData extractData(ResultSet rs) throws SQLException, DataAccessException {
				            ResultSetMetaData rsmd = rs.getMetaData();
				            return rsmd;
				        }
				     });

				    ResultSetMetaData rsmd = (ResultSetMetaData) rsmdList.get(0);
				    int numberOfColumns = rsmd.getColumnCount();
				    for (int i = 1; i < numberOfColumns + 1; i++) {
				        String columnName = rsmd.getColumnName(i);
				        String tableName = rsmd.getTableName(i);
				        rsmd.getColumnType(numberOfColumns);
				       // rsmd.gett
				        System.out.println(columnName);
				        System.out.println(tableName);
				      }
					List<Map<String,Object>>	colums=jdbcTemplate.queryForList("desc "+entry.getValue());
					for (Iterator iterator2 = colums.iterator(); iterator2.hasNext();) {
						Map<String, Object> coluMaps = (Map<String, Object>) iterator2.next();
						coluMaps.get("Field");
						coluMaps.get("Type");
						coluMaps.get("Key");
						DbColumn column1 = tableNameT.addColumn((String)coluMaps.get("Field"),
								getSQLType((String)coluMaps.get("Type")), getLength((String)coluMaps.get("Type")));
						if(coluMaps.get("Key").equals("PRI"))
							{
							column1.primaryKey();
							}
						listArray.add(column1);
						for (Entry<String, Object> entrys : coluMaps.entrySet()) 
						{
							
							 System.out.println("Column Key = " + entrys.getKey() +  ", column Value = " + entrys.getValue()); 
							 
							 
						}
					}
					metaDatum.put(tableNameT, listArray);
			    } 
				} 	 
			} 
			else {	 
			//dataSource.getConnection().setCatalog("cameldb");
		    rs = md.getTables(null, null, "%", new String[] { "TABLE",
				"VIEW"/*
						 * , "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"
						 */ });
		while (rs.next()) {

			DbTable tableNameT = schemaObj.addTable(rs.getString("TABLE_NAME"));
			String tableName = rs.getString("TABLE_NAME");
			log.info(tableName);
			SelectQuery selectQuery = new SelectQuery();
			List<DbColumn> listArray = new ArrayList<>();
			ResultSet rs1 = md.getColumns(null, null, rs.getString("TABLE_NAME"), null);
			ResultSet rs2 = md.getPrimaryKeys(null, null, rs.getString("TABLE_NAME"));
			String primaryKey = "";
			while (rs2.next()) {
				primaryKey = rs2.getString("COLUMN_NAME");
			}

			while (rs1.next()) {
				DbColumn column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
						Integer.parseInt(rs1.getString("DATA_TYPE")), Integer.parseInt(rs1.getString("COLUMN_SIZE")));

				if (rs1.getString("COLUMN_NAME").equals(primaryKey)) {
					column1.primaryKey();
				}
				listArray.add(column1);
				log.info(rs1.getString("DATA_TYPE"));
				log.info(rs1.getString("COLUMN_NAME"));
				log.info(rs1.getString("COLUMN_SIZE"));

			}
			log.info(selectQuery.addAllTableColumns(tableNameT).validate().toString());
			metaDatum.put(tableNameT, listArray);
			
		}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return metaDatum;
	}

	public void createDbTable(DbTable tableName) {
		log.info("\n=======Creating '" + tableName.getName() + "' In The Database=======\n");
		loadSQLBuilderSchema();
		try {

			String createTableQuery = new CreateTableQuery(tableName, true).validate().toString();
			log.info("\nGenerated Sql Query?= " + createTableQuery + "\n");
			jdbcTemplate.execute(createTableQuery);
		} catch (Exception sqlException) {
			sqlException.printStackTrace();
		}
		log.info("\n=======The '" + tableName.getName() + "' Successfully Created In The Database=======\n");
	}

	public List<DbTable> initializeTable() {

		loadSQLBuilderSchema();
		DbColumn id;
		DbColumn tableName;
		DbColumn  serviceName;
		DbColumn attrid ;
		DbColumn  serid;
		DbColumn  attrName;
		DbColumn  colName;
		
		DbTable tableService = schemaObj.addTable("Service");
		DbTable tableServiceAttr = schemaObj.addTable("Service_Attr");

		id = tableService.addColumn("id", Types.INTEGER, 10);
		id.primaryKey();
		tableName = tableService.addColumn("tableName", Types.VARCHAR, 100);
		serviceName = tableService.addColumn("serviceName", Types.VARCHAR, 100);
		attrid = tableServiceAttr.addColumn("id", Types.INTEGER, 10);
		attrid.primaryKey();
		serid = tableServiceAttr.addColumn("service_id", Types.INTEGER, 10);
		attrName = tableServiceAttr.addColumn("attrName", Types.VARCHAR, 100);
		colName = tableServiceAttr.addColumn("colName", Types.VARCHAR, 100);
		List<DbTable> initialTable = new ArrayList<DbTable>();
		initialTable.add(tableService);
		initialTable.add(tableServiceAttr);
		return initialTable;
	}

	public Map<String, Object> insertData(String service, Map<String, String> params) throws Exception {
		loadSQLBuilderSchema();
		String tableName = serviceTableMap.get(service);
		InsertQuery insertQuery;
		String primaryKey = null;
		boolean isUpdate = false;
		Map<String, String> attribParamMap = serviceAttrbMap.get(service);
		for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMap.entrySet()) {
			DbTable table = entry.getKey();
			if (table.getName().equalsIgnoreCase(tableName)) {
				table = schemaObj.addTable(table.getName());
				insertQuery = new InsertQuery(table);
				List<DbColumn> column = entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn = iterator.next();
					List<DbConstraint> dbConstr = dbColumn.getConstraints();
					boolean isPrimaryKey = false;
					for (Iterator<DbConstraint> iterator2 = dbConstr.iterator(); iterator2.hasNext();) {
						DbConstraint dbConstraint = iterator2.next();
						if (dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString())) {

							if ((params.get(attribParamMap.get(dbColumn.getName())) != null)) {
								isPrimaryKey = true;
								primaryKey = params.get(attribParamMap.get(dbColumn.getName()));
								if (getData(service, primaryKey).size() > 0) {
									isUpdate = true;
								}
							} else {

								primaryKey = findMax(tableName);
								params.put(dbColumn.getName(), primaryKey);

							}
						}
					}

					if (params.get(attribParamMap.get(dbColumn.getName())) != null) {
						insertQuery.addColumn(dbColumn, params.get(attribParamMap.get(dbColumn.getName())));
					}
				}

				log.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
				log.info(insertQuery.toString());
				if (primaryKey != null) {

					if (!isUpdate) {
						jdbcTemplate.execute(insertQuery.toString());
					} else {
						updateData(service, params);
					}

				} else {

					throw new Exception("Primary key not found");
				}
				break;
			}

		}
		return getData(service, primaryKey);

	}

	public Map<String, Object> updateData(String service, Map<String, String> params) throws Exception {

		String tableName = serviceTableMap.get(service);
		if (tableName == null) {
			throw new ServiceNotFoundException(serviceNTFEx);
		}
		UpdateQuery updateQuery = new UpdateQuery(tableName);
		int isUpdate = 0;
		String primaryKey = "";
		Map<String, String> attribParamMap = serviceAttrbMap.get(service);
		for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMap.entrySet()) {
			DbTable table = entry.getKey();
			if (table.getName().equalsIgnoreCase(tableName)) {
				table = schemaObj.addTable(table.getName());
				updateQuery = new UpdateQuery(table);
				List<DbColumn> column = entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn = iterator.next();
					List<DbConstraint> dbConstr = dbColumn.getConstraints();
					boolean isPrimaryKey = false;
					for (Iterator<DbConstraint> iterator2 = dbConstr.iterator(); iterator2.hasNext();) {
						DbConstraint dbConstraint = iterator2.next();
						if (dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString())) {
							isPrimaryKey = true;
							if ((params.get(attribParamMap.get(dbColumn.getName())) != null))
								updateQuery.addCondition(BinaryCondition.equalTo(dbColumn,
										params.get(attribParamMap.get(dbColumn.getName()))));
							primaryKey = params.get(attribParamMap.get(dbColumn.getName()));
						}
					}
					if ((params.get(attribParamMap.get(dbColumn.getName())) != null))
						updateQuery.addSetClause(dbColumn, params.get(attribParamMap.get(dbColumn.getName())));

				}

				log.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
				// params.get(attribParamMap.get(dbColumn.getName()))
				log.info(updateQuery.toString());
				try {
					isUpdate = jdbcTemplate.update(updateQuery.toString());
				} catch (Exception e) {
				}
				break;
			}
		}
		if (isUpdate > 0) {

			return getDataForParams(service, params).get(0);
		} else {
			throw new Exception("update Failed");
		}

	}

	public Map<String, Object> getData(String serviceName, String primaryKeyValue) throws Exception {

		SelectQuery selectQuery = new SelectQuery();
		String tableName = serviceTableMap.get(serviceName);
		if (tableName == null) {
			throw new ServiceNotFoundException(serviceNTFEx);
		}
		Map<String, String> attribParamMap = serviceAttrbMap.get(serviceName);
		for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMap.entrySet()) {
			DbTable table = entry.getKey();
			if (table.getName().equalsIgnoreCase(tableName)) {
				List<DbColumn> column = entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn = iterator.next();
					List<DbConstraint> dbConstr = dbColumn.getConstraints();
					for (Iterator<DbConstraint> iterator2 = dbConstr.iterator(); iterator2.hasNext();) {
						DbConstraint dbConstraint = iterator2.next();
						if (dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString())) {
							selectQuery.addCondition(BinaryCondition.equalTo(dbColumn, primaryKeyValue));

						}
					}
					selectQuery.addAliasedColumn(dbColumn, "\"" + attribParamMap.get(dbColumn.getName()) + "\"");
				}
				break;
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = jdbcTemplate.queryForMap(selectQuery.validate().toString());

		} catch (Exception e) {

			log.info(e.getMessage());
		}
		return result;
	}

	public Map<String, Object> deleteData(String serviceName, String primaryKeyValue) throws Exception {

		DeleteQuery deleteQuery = null;
		String tableName = serviceTableMap.get(serviceName);
		if (tableName == null) {
			throw new ServiceNotFoundException(serviceNTFEx);
		}
		for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMap.entrySet()) {
			DbTable table = entry.getKey();
			if (table.getName().equalsIgnoreCase(tableName)) {
				deleteQuery = new DeleteQuery(table);
				List<DbColumn> column = entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn = iterator.next();
					List<DbConstraint> dbConstr = dbColumn.getConstraints();
					for (Iterator<DbConstraint> iterator2 = dbConstr.iterator(); iterator2.hasNext();) {
						DbConstraint dbConstraint = iterator2.next();
						if (dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString())) {
							deleteQuery.addCondition(BinaryCondition.equalTo(dbColumn, primaryKeyValue));

						}
					}
				}
				log.info(deleteQuery.validate().toString());
				break;
			}
		}
		log.info(deleteQuery.validate().toString());
		Map<String, Object> retValue = getData(serviceName, primaryKeyValue);
		int result = 0;
		try {
			result = jdbcTemplate.update(deleteQuery.validate().toString());
		} catch (Exception e) {
		}
		if (result > 0) {
			return retValue;
		} else {
			throw new Exception("Deletion Failed ");
		}

	}

	public List<Map<String, Object>> getDataForParams(String serviceName, Map<String, String> params)    {

		SelectQuery selectQuery = new SelectQuery();
		String tableName = serviceTableMap.get(serviceName);
		if (tableName == null) {
			Set<ConstraintViolation<HashMap>> constraintViolation =new HashSet<ConstraintViolation<HashMap>>();
			Map errorMessages=new HashMap<String,String>();
			ConstraintViolation<HashMap> cv=new ServiceConstraintViolation<String,String>("Service Not Found "," / "+serviceName); 
			constraintViolation.add(cv);
			throw new ConstraintViolationException(constraintViolation);
		}
		Map<String, String> attribParamMap = serviceAttrbMap.get(serviceName);
		for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMap.entrySet()) {
			DbTable table = entry.getKey();
			if (table.getName().equalsIgnoreCase(tableName)) {
				List<DbColumn> column = entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn = iterator.next();
					if (attribParamMap.containsKey(dbColumn.getName())
							&& params.get(attribParamMap.get(dbColumn.getName())) != null) {
						selectQuery.addCondition(
								BinaryCondition.equalTo(dbColumn, params.get(attribParamMap.get(dbColumn.getName()))));
					}
					selectQuery.addAliasedColumn(dbColumn, "\"" + attribParamMap.get(dbColumn.getName()) + "\"");
				}
				log.info(selectQuery.validate().toString());
				break;
			}
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			result = jdbcTemplate.queryForList(selectQuery.validate().toString());

		} catch (Exception e) {

			log.info(e.getMessage());
		}

		return result;
	}

	public void getServiceTableMap() {

		serviceTableMap = new HashMap<>();
		serviceAttrbMap = new HashMap<>();
		List<Map<String, Object>> serviceDatum = jdbcTemplate
				.queryForList("select id, tableName, serviceName from Service");
		for (Iterator<Map<String, Object>> iterator = serviceDatum.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			serviceTableMap.put((String) map.get("serviceName"), (String) map.get("tableName"));
			serviceAttrbMap(Integer.toString((int) map.get("id")), (String) map.get("serviceName"));

		}
		 
	}

	public void serviceAttrbMap(String id, String serviceName) {

		Map<String, String> studentAttrbMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		List<Map<String, Object>> serviceDatum = jdbcTemplate
				.queryForList("select colName, attrName from Service_Attr where service_id = '" + id + "'");
		Map<String, String> AttrbMap;
		AttrbMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		for (Iterator<Map<String, Object>> iterator = serviceDatum.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();

			AttrbMap.put((String) map.get("colName".toUpperCase()), (String) map.get("attrName".toUpperCase()));
			serviceAttrbMap.put(serviceName, AttrbMap);
		}
		 
	}

	private void insertServiceTables() throws Exception {

		for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMap.entrySet()) {
			DbTable table = entry.getKey();
			String tableName = table.getName();

			String serviceInsertQuery = "INSERT INTO   Service (id ,tableName , serviceName )   values( (SELECT MAX( id )+1 FROM Service ser) , '"
					+ tableName + "', '" + tableName.toLowerCase().replace("_", " ") + "'  )";
			String serviceid = getServiceID(tableName);
			String maxRec = findMax("Service");
			if (maxRec != null && serviceid == null) {
				jdbcTemplate.execute(serviceInsertQuery);
				serviceid = getServiceID(tableName);
			} else if (maxRec == null) {
				serviceInsertQuery = "INSERT INTO   Service (id ,tableName , serviceName )   values( 0, '" + tableName
						+ "', '" + tableName.toLowerCase().replace("_", " ") + "'  )";
				jdbcTemplate.execute(serviceInsertQuery);
				serviceid = getServiceID(tableName);
			}

			List<DbColumn> column = entry.getValue();
			for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
				DbColumn dbColumn = iterator.next();
				String serviceAttrid = getServiceAttrID(serviceid, dbColumn.getName());
				String maxRecAttr = findMax("Service_Attr");
				if (maxRecAttr != null && serviceAttrid == null) {
					String serviceAttrQuery = " INSERT INTO Service_Attr (id, service_id , attrName, colName) values ((SELECT MAX( id )+1 FROM Service_Attr serA) ,'"
							+ serviceid + "','" + dbColumn.getName().toLowerCase().replace("_", " ") + "','"
							+ dbColumn.getName() + "') ";
					jdbcTemplate.execute(serviceAttrQuery);
				} else if (maxRecAttr == null) {

					{
						String serviceAttrQuery = " INSERT INTO Service_Attr (id, service_id , attrName, colName) values (0 ,'"
								+ serviceid + "','" + dbColumn.getName().toLowerCase().replace("_", " ") + "','"
								+ dbColumn.getName() + "') ";
						jdbcTemplate.execute(serviceAttrQuery);
					}

				}
			}
		}

	}

	private String getServiceID(String tableName) {

		String selectQuery = " select id from Service where tableName = '" + tableName + "'";
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			data = jdbcTemplate.queryForList(selectQuery);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		String serID = null;
		if (data != null)
			if (data.size() != 0) {
				if (data.get(0).get("ID") != null) {
					serID = Integer.toString((int) data.get(0).get("id"));
				}
			}
		return serID;
	}

	private String getServiceAttrID(String serviceId, String attributeName) {

		String selectQuery = " select id  from Service_Attr where service_id  = '" + serviceId + "' and colName ='"
				+ attributeName + "'";
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			data = jdbcTemplate.queryForList(selectQuery);
		} catch (Exception e) {
			log.info(e.getMessage());
		}

		String attrID = null;
		if (data != null)
			if (!data.isEmpty() ) {
				if (data.get(0).get("ID") != null) {
					attrID = Integer.toString((int) data.get(0).get("id"));
				}
			}
		return attrID;
	}

	private String findMax(String table) {
		String query = "SELECT MAX( id )+1 as id FROM " + table;
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = jdbcTemplate.queryForMap(query);
		} catch (Exception e) {
			log.info(e.getMessage());
		}

		String attrID = null;
		if (data.get("ID") != null) {
			try {attrID = Integer.toString((int) data.get("ID"));
			}catch(Exception e) {
				attrID = Long.toString((Long) data.get("ID"));	
			}
		}
		return attrID;

	}
	public int getSQLType(String mysqlDataType)
	{
		if(mysqlDataType.contains("int"))
		{
			return  Types.INTEGER;
		}
		
		return 0;
	}
	public int getLength(String mysqlDataType)
	{
		char[] lengthData=mysqlDataType.toCharArray();
		String returnStr="";
		for (int i = 0; i < lengthData.length; i++) {
			char chardata= lengthData[i];
			if (chardata >= '0' && chardata <= '9')
			{
				returnStr=returnStr+chardata;
			}
		}
		return Integer.parseInt(returnStr);
	}
	public static String getSqlTypeName(int type) {
	    switch (type) {
	    case Types.BIT:
	        return "BIT";
	    case Types.TINYINT:
	        return "TINYINT";
	    case Types.SMALLINT:
	        return "SMALLINT";
	    case Types.INTEGER:
	        return "INTEGER";
	    case Types.BIGINT:
	        return "BIGINT";
	    case Types.FLOAT:
	        return "FLOAT";
	    case Types.REAL:
	        return "REAL";
	    case Types.DOUBLE:
	        return "DOUBLE";
	    case Types.NUMERIC:
	        return "NUMERIC";
	    case Types.DECIMAL:
	        return "DECIMAL";
	    case Types.CHAR:
	        return "CHAR";
	    case Types.VARCHAR:
	        return "VARCHAR";
	    case Types.LONGVARCHAR:
	        return "LONGVARCHAR";
	    case Types.DATE:
	        return "DATE";
	    case Types.TIME:
	        return "TIME";
	    case Types.TIMESTAMP:
	        return "TIMESTAMP";
	    case Types.BINARY:
	        return "BINARY";
	    case Types.VARBINARY:
	        return "VARBINARY";
	    case Types.LONGVARBINARY:
	        return "LONGVARBINARY";
	    case Types.NULL:
	        return "NULL";
	    case Types.OTHER:
	        return "OTHER";
	    case Types.JAVA_OBJECT:
	        return "JAVA_OBJECT";
	    case Types.DISTINCT:
	        return "DISTINCT";
	    case Types.STRUCT:
	        return "STRUCT";
	    case Types.ARRAY:
	        return "ARRAY";
	    case Types.BLOB:
	        return "BLOB";
	    case Types.CLOB:
	        return "CLOB";
	    case Types.REF:
	        return "REF";
	    case Types.DATALINK:
	        return "DATALINK";
	    case Types.BOOLEAN:
	        return "BOOLEAN";
	    case Types.ROWID:
	        return "ROWID";
	    case Types.NCHAR:
	        return "NCHAR";
	    case Types.NVARCHAR:
	        return "NVARCHAR";
	    case Types.LONGNVARCHAR:
	        return "LONGNVARCHAR";
	    case Types.NCLOB:
	        return "NCLOB";
	    case Types.SQLXML:
	        return "SQLXML";
	    }

	    return "?";
	}
}
