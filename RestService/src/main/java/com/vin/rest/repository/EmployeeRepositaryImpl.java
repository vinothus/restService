package com.vin.rest.repository;

import java.math.BigDecimal;
import java.sql.Connection;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.vin.rest.dynamic.MultiServiceImpl;
import com.vin.rest.exception.RecordNotFoundException;
import com.vin.rest.exception.ServiceNotFoundException;
import com.vin.rest.model.EmployeeEntity;
import com.vin.validation.ParamsValidator;
import com.vin.validation.ServiceConstraintViolation;

@Component
public class EmployeeRepositaryImpl {

	Logger log = Logger.getLogger(EmployeeRepositaryImpl.class.getName());
	@Autowired
	DataSource dataSource;
	@Autowired
	JdbcTemplate jdbcTemplate;
	static DbSchema schemaObj;
	static DbSpec specficationObj;
	public static Map<String, String> serviceTableMap= new ConcurrentHashMap<>();
	public static Map<DbTable, List<DbColumn>> tableColumnMap= new ConcurrentHashMap<>();
	static Map<String, Map<String, String>> serviceAttrbMap= new ConcurrentHashMap<>();
    String serviceNTFEx="Service not found Exception";
    
	String[] nonscaling = {"NCLOB","BLOB","CLOB","NULL","OTHER","JAVA_OBJECT","ARRAY", "DISTINCT", "STRUCT", "REF", "DATALINK", "ROWID", "SQLXML", "?" };
	List<String> invalidElement = new ArrayList<String>();
	public void init() {

		try {
			//tableColumnMap = getMetaDatum();
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
				if(!isTablePresent(dbTable.getName()))
				{
					createDbTable(dbTable);
					}

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
		Connection conn=null;
		try {
			conn=dataSource.getConnection();
			md = conn.getMetaData();
			if(md.getURL().contains("mysql"))
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
					 
					List<Map<String,Object>>	colums=jdbcTemplate.queryForList("desc "+entry.getValue());
					for (Iterator iterator2 = colums.iterator(); iterator2.hasNext();) {
						Map<String, Object> coluMaps = (Map<String, Object>) iterator2.next();
						coluMaps.get("Field");
						coluMaps.get("Type");
						coluMaps.get("Key");
						 
						DbColumn column1 = tableNameT.addColumn((String)coluMaps.get("Field"),
								getSqlTypeWithoutScal((String)coluMaps.get("Type"))	, getLength((String)coluMaps.get("Type")));
						column1.setDefaultValue(getSqlTypeName((String)coluMaps.get("Type")));
						if(coluMaps.get("Key").equals("PRI"))
							{
							column1.primaryKey();
							}
						listArray.add(column1);
						 
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
			if(tableName.contains("/")||tableName.contains("$"))
			{
				continue;
			}
			SelectQuery selectQuery = new SelectQuery();
			List<DbColumn> listArray = new ArrayList<>();
			ResultSet rs1 = md.getColumns(null, null,tableName, null);
			ResultSet rs2 = md.getPrimaryKeys(null, null, tableName);
			String primaryKey = "";
			while (rs2.next()) {
				primaryKey = rs2.getString("COLUMN_NAME");
			}
			rs2.close();

			while (rs1.next()) {
				DbColumn column1 = null ;
				try {
				 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
						Integer.parseInt(rs1.getString("DATA_TYPE")), Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				}catch(Exception e)
				{
					 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
								4, Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				}

				if (rs1.getString("COLUMN_NAME").equals(primaryKey)) {
					column1.primaryKey();
				}
				listArray.add(column1);
				log.info(rs1.getString("DATA_TYPE"));
				log.info(rs1.getString("COLUMN_NAME"));
				log.info(rs1.getString("COLUMN_SIZE"));

			}
			rs1.close();
			log.info(selectQuery.addAllTableColumns(tableNameT).validate().toString());
			metaDatum.put(tableNameT, listArray);
			
		}
		rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			
			if(conn!=null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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
		}  catch (Exception sqlException) {
			if(sqlException instanceof java.sql.SQLSyntaxErrorException || sqlException instanceof org.springframework.jdbc.BadSqlGrammarException)
			{
				log.info("table creation failed with Syntax Error");
				
				String manualQuery="Create TABLE "+tableName.getName() + " ( ";
				List<DbColumn>  columns=tableName.getColumns();
				for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
					DbColumn dbColumn = (DbColumn) iterator.next();
					List<DbConstraint> dbconstaints=dbColumn.getConstraints();
					boolean isPrimary=false;
					for (Iterator iterator2 = dbconstaints.iterator(); iterator2.hasNext();) {
						DbConstraint dbConstraint = (DbConstraint) iterator2.next();
						if (dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString())) {
							isPrimary= true;
						}
						
					}
					if(dbColumn.getTypeNameSQL().contains("INTEGER"))
					{
						manualQuery=manualQuery+dbColumn.getName()+ " "+dbColumn.getTypeNameSQL();
					}else if(dbColumn.getTypeNameSQL().contains("VARCHAR"))
					{
						manualQuery=manualQuery+dbColumn.getName()+ " "+dbColumn.getTypeNameSQL()+"("+dbColumn.getTypeLength()+")";
					}
					if(isPrimary)
					{
						manualQuery=manualQuery+" PRIMARY KEY";	
					}
					if( iterator.hasNext())
					{
						manualQuery=manualQuery+" , ";	
					}
				}
				manualQuery=manualQuery+" )";
				System.out.println(manualQuery);
				try {
				jdbcTemplate.execute(manualQuery);
				}catch(Exception e)
				{
				log.info(e.getMessage());	
				}
			}
			
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
		DbColumn multiseviceid;
		DbColumn multisevice_id;
		DbColumn multiseviceName;
		DbColumn multisevicePriority;
		DbColumn multiseviceType;
		DbColumn multiseviceRelationwithParam;
		DbColumn sevicePriority;
		
		DbTable tableService = schemaObj.addTable("Service");
		DbTable tableServiceAttr = schemaObj.addTable("Service_Attr");
		DbTable tableMultiService = schemaObj.addTable("Multi_Service");
		id = tableService.addColumn("id", Types.INTEGER, 10);
		id.primaryKey();
		tableName = tableService.addColumn("tableName", Types.VARCHAR, 100);
		serviceName = tableService.addColumn("serviceName", Types.VARCHAR, 100);
		attrid = tableServiceAttr.addColumn("id", Types.INTEGER, 10);
		attrid.primaryKey();
		serid = tableServiceAttr.addColumn("service_id", Types.INTEGER, 10);
		attrName = tableServiceAttr.addColumn("attrName", Types.VARCHAR, 100);
		colName = tableServiceAttr.addColumn("colName", Types.VARCHAR, 100);
		multiseviceid= tableMultiService.addColumn("id", Types.INTEGER, 10);
		multiseviceid.primaryKey();
		multisevice_id= tableMultiService.addColumn("service_id",  Types.VARCHAR, 100);
		multiseviceName= tableMultiService.addColumn("multiservicename", Types.VARCHAR, 100);
		multisevicePriority= tableMultiService.addColumn("priority", Types.INTEGER, 10);
		multiseviceType= tableMultiService.addColumn("type",Types.VARCHAR, 100);
		multiseviceRelationwithParam= tableMultiService.addColumn("relationwithparam", Types.VARCHAR, 100);
		
		List<DbTable> initialTable = new ArrayList<DbTable>();
		initialTable.add(tableService);
		initialTable.add(tableServiceAttr);
		initialTable.add(tableMultiService);
		return initialTable;
	}

	public Map<String, Object> insertData(String service, Map<String, String> params) throws Exception {
		loadSQLBuilderSchema();
		ObjectMapper mapper = new ObjectMapper();
		String tableName = serviceTableMap.get(service);
		int updatedata=0;
		
		setGDValues(service, tableName);
		tableName = serviceTableMap.get(service);
		if (tableName == null) {
			throw new ServiceNotFoundException(serviceNTFEx);
		}
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
								primaryKey =( mapper.writeValueAsString(params.get(attribParamMap.get(dbColumn.getName()))) );
								if (getData(service, primaryKey).size() > 0) {
									isUpdate = true;
								}
							} else {

								primaryKey = findMax(tableName);
								if(primaryKey==null)
								{
									primaryKey="0";
								}
								params.put(attribParamMap.get(dbColumn.getName()), primaryKey);

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
					 updatedata=	jdbcTemplate.update(insertQuery.toString());
					} else {
						updateData(service, params);
					}

				} else {

					throw new Exception("Primary key not found");
				}
				break;
			}

		}
		Map<String, Object>  datatrt=getData(service, primaryKey);
		if(datatrt==null||datatrt.isEmpty())
		{
			if(updatedata==1&&primaryKey.equals("0"))
			{datatrt=	getDataForParams(service, new HashMap<String, String>()).get(0);}
		}
		
		return datatrt;

	}
	private void arrangeGoldenDataForTable(String tableName) {
		loadSQLBuilderSchema();
		setTableColumn(tableName);
		
	}
	private void arrangeGoldenData(String service) {
		loadSQLBuilderSchema();
		if(!isPresentinDB(service))
		{
			String tableName=service.replace(" ", "_");
		setTableColumn(tableName.toUpperCase());
		}
	}

	 
	private boolean isPresentinDB(String service) {

		String selectQuery = " select tableName from Service where serviceName = '" + service + "'";
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		boolean isPresent=false;
		try {
			data = jdbcTemplate.queryForList(selectQuery);
		} catch (Exception e) {
			log.info(e.getMessage());
			return isPresent;
		}
		String tableName = null;
		if (data != null)
			if (data.size() != 0) {
				isPresent=true;
				if (data.get(0).get("tableName") != null) {
					try {
						tableName = (String) data.get(0).get("tableName");
						setTableColumn(tableName.toUpperCase());
					} catch (Exception e) {

					}
				}
			}
		return isPresent;
	}
	public String refreshMataData(String serviceName) throws RecordNotFoundException
	{
		List<Map<String, Object>> serviceDatum = jdbcTemplate
				.queryForList("select id, tableName, serviceName from Service where serviceName= '"+serviceName+"'");
		if(!(serviceDatum.size()>0))
		{
			throw new RecordNotFoundException(serviceName +" not found ;");
		}
		String cacheTn=serviceTableMap.get(serviceName);
		for (Iterator<Map<String, Object>> iterator = serviceDatum.iterator(); iterator.hasNext();) {
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			String dbTn=(String) map.get("tableName");
			if(cacheTn==null)
			{
				serviceTableMap.put(serviceName,dbTn);	
			}
		}
		String id=getID(serviceDatum.get(0),"id");
		serviceAttrbMap(id, serviceName);
		
		return id;
	}
	private String getID(Map<String, Object> data,String key) {
		String id = null;
		if (data.get(key) != null) {
			try {
				id = Integer.toString((int) data.get(key));
			} catch (Exception e) {

				try {
					id = Long.toString((Long) data.get(key));
				} catch (Exception es) {

					BigDecimal datas = (BigDecimal) data.get(key);
					id = datas.toString();

				}
			}
		}
		return id;
	}

	private void setTableColumn(String tableName) {
		boolean isTablePresent=false;
		DatabaseMetaData md;
		DbTable tableNameT;
		List<DbColumn> listArray = new ArrayList<>();
		Connection conn=null;
		boolean isPresent=false;
		for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMap.entrySet()) {
			DbTable table = entry.getKey();
			if (table.getName().equalsIgnoreCase(tableName)) {
				isPresent=true;
			}
		}
		try {
			if(!isPresent) {
			 conn= dataSource.getConnection();
			md = conn.getMetaData();
			if(md.getURL().contains("mysql"))
			{
				
				 tableNameT = schemaObj.addTable(tableName);
				List<DbColumn> listArraycol = new ArrayList<>();
				List<Map<String,Object>>	colums=jdbcTemplate.queryForList("desc "+tableName);
				for (Iterator iterator2 = colums.iterator(); iterator2.hasNext();) {
					 isTablePresent=true;
					Map<String, Object> coluMaps = (Map<String, Object>) iterator2.next();
					coluMaps.get("Field");
					coluMaps.get("Type");
					coluMaps.get("Key");
					 
					 
					DbColumn column1 = tableNameT.addColumn((String)coluMaps.get("Field"),
							getSqlTypeWithoutScal((String)coluMaps.get("Type")), getLength((String)coluMaps.get("Type")));
					//column1.setDefaultValue(getSqlTypeName((String)coluMaps.get("Type")));
					if(coluMaps.get("Key").equals("PRI"))
						{
						column1.primaryKey();
						}
					listArraycol.add(column1);
					 
				}
				if(listArraycol.size()>0)
				{
				tableColumnMap.put(tableNameT, listArraycol);
				}
				
			}else {
				
			ResultSet rs1 = md.getColumns(null, null,tableName.toUpperCase(), null);
			ResultSet rs2 = md.getPrimaryKeys(null, null, tableName.toUpperCase());
			String primaryKey = "";
			 tableNameT = schemaObj.addTable(tableName);
			while (rs2.next()) {
				primaryKey = rs2.getString("COLUMN_NAME");
			}
			rs2.close();

			while (rs1.next()) {
				DbColumn column1 = null ;
				try {
					
				 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
						Integer.parseInt(rs1.getString("DATA_TYPE")), Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				 isTablePresent=true;
				}catch(Exception e)
				{
					 column1 = tableNameT.addColumn(rs1.getString("COLUMN_NAME"),
								4, Integer.parseInt(rs1.getString("COLUMN_SIZE")));
				}

				if (rs1.getString("COLUMN_NAME").equals(primaryKey)) {
					column1.primaryKey();
				}
				listArray.add(column1);
				log.info(rs1.getString("DATA_TYPE"));
				log.info(rs1.getString("COLUMN_NAME"));
				log.info(rs1.getString("COLUMN_SIZE"));

			}
			rs1.close();
			if(listArray.size()>0)
			{
				tableColumnMap.put(tableNameT, listArray);
				}
			}
			}
			 
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
						if(!isTablePresent(dbTable.getName()))
						{
							createDbTable(dbTable);
							}
						tableColumnMap.put(dbTable, dbTable.getColumns());
					}

				}
			 
			setServiceTableMap(tableName);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}finally
		{
			if(conn!=null)
			{
				
				try {
					conn.close();
				} catch (SQLException e) {
					 
					e.printStackTrace();
				}
			}
			
		}
	}

	public Map<String, Object> updateData(String service, Map<String, String> params) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String tableName = serviceTableMap.get(service);
		setGDValues(service, tableName);
		tableName = serviceTableMap.get(service);
		if (tableName == null) {
			throw new ServiceNotFoundException(serviceNTFEx);
		}
		UpdateQuery updateQuery = new UpdateQuery(tableName);
		int isUpdate = 0;
		String primaryKey = "";
		String primaryKeyAttr="";
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
							primaryKey =( mapper.writeValueAsString(params.get(attribParamMap.get(dbColumn.getName()))) );
							primaryKeyAttr=attribParamMap.get(dbColumn.getName());
						}
					}
					if ((params.get(attribParamMap.get(dbColumn.getName())) != null))
						updateQuery.addSetClause(dbColumn, params.get(attribParamMap.get(dbColumn.getName())));

				}

				log.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
				// params.get(attribParamMap.get(dbColumn.getName()))
				log.info(updateQuery.toString());
				try {
					if(!(primaryKey==null||primaryKey.isEmpty()||primaryKey.equalsIgnoreCase("null")))
					{
						isUpdate = jdbcTemplate.update(updateQuery.toString());
					}
				} catch (Exception e) {
				}
				break;
			}
		}
		if (isUpdate > 0) {
		if(tableName.equals("SERVICE_ATTR"))
		{
			System.out.println("select S.id as id, S.tableName as tableName, S.serviceName as serviceName, SA.colName as colName, SA.attrName as attrName  from Service S Service_Attr SA, jdbcTemplate where S.id=  '"+params.get("service id")+"'  and S.id=SA.service_id ");
			List<Map<String, Object>> serviceDatum = jdbcTemplate
					.queryForList("select S.id as id, S.tableName as tableName, S.serviceName as serviceName, SA.colName as colName, SA.attrName as attrName  from Service S, Service_Attr SA  where S.id=  '"+params.get("service id")+"'  and S.id=SA.service_id ");
			
			String serviceName=(String)serviceDatum.get(0).get("serviceName");
			Map<String, String> AttrbMap; 
			AttrbMap=serviceAttrbMap.get(serviceName);
			if(AttrbMap==null)
			{
				AttrbMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
			}
			 
			for (Iterator iterator = serviceDatum.iterator(); iterator.hasNext();) {
				Map<String, Object> map = (Map<String, Object>) iterator.next();
				serviceName=(String)map.get("serviceName");
				AttrbMap.put((String) map.get("colName".toUpperCase()), (String) map.get("attrName".toUpperCase()));
				//serviceAttrbMap.put((String)map.get("serviceName"),AttrbMap);
			}
			if(serviceName!=null)
			{
				serviceAttrbMap.put(serviceName,AttrbMap);
				}
			/*
			 * List<Map<String, Object>> serviceDatum1 = jdbcTemplate
			 * .queryForList("select colName, attrName from Service_Attr where service_id = '"
			 * + params.get("service id") + "'"); Map<String, String> AttrbMap; AttrbMap =
			 * new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER); for
			 * (Iterator<Map<String, Object>> iterator = serviceDatum1.iterator();
			 * iterator.hasNext();) { Map<String, Object> map = iterator.next();
			 * 
			 * AttrbMap.put((String) map.get("colName".toUpperCase()), (String)
			 * map.get("attrName".toUpperCase())); serviceAttrbMap.put((String)
			 * serviceDatum.get(0).get("serviceName"), AttrbMap); }
			 */
				
				//serviceAttrbMap(id,(String) serviceDatum.get(0).get("serviceName"));
		     
		}
		params=new HashMap<String, String>();
		params.put(primaryKeyAttr,primaryKey);
			return getDataForParams(service, params).get(0);
		} else {
			try {
				if(primaryKey==null||primaryKey.isEmpty()||primaryKey.equalsIgnoreCase("null"))
				{
					return insertData(service, params);
				}
			} catch (Exception e) {
				throw new Exception("update Failed");
			}
			if (!(isUpdate > 0)) {
				throw new Exception("update Failed");
			}else
			{
				return getDataForParams(service, params).get(0);
			}
		}

}

	public Map<String, Object> getData(String serviceName, String primaryKeyValue) throws Exception {

		SelectQuery selectQuery = new SelectQuery();
		String tableName = serviceTableMap.get(serviceName);
		setGDValues(serviceName, tableName);
		tableName = serviceTableMap.get(serviceName);
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
					if (attribParamMap.get(dbColumn.getName())!= null)
						{
						if(isValidForSelect(dbColumn.getTypeNameSQL()))
						{
							selectQuery.addAliasedColumn(dbColumn, "\"" + attribParamMap.get(dbColumn.getName()) + "\"");
						}
						}
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
		setGDValues(serviceName, tableName);
		tableName = serviceTableMap.get(serviceName);
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
		for (int i = 0; i < nonscaling.length; i++) {
			invalidElement.add(nonscaling[i]);
		}
		SelectQuery selectQuery = new SelectQuery();
		String tableName = serviceTableMap.get(serviceName);
		setGDValues(serviceName, tableName);
		tableName = serviceTableMap.get(serviceName);
		 
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
				String columnType=	 dbColumn.getTypeNameSQL() ;
				 if(attribParamMap.get(dbColumn.getName())!=null)
				 { 
					 if(isValidForSelect(dbColumn.getTypeNameSQL())) {
					 selectQuery.addAliasedColumn(dbColumn, "\"" + attribParamMap.get(dbColumn.getName()) + "\"");
					 }	 
				 } 
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

	private void setGDValues(String serviceName, String tableName) {
		if (tableName == null) {
			arrangeGoldenData(serviceName);

		} else {
			boolean isPresent = false;
			for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMap.entrySet()) {
				DbTable table = entry.getKey();
				if (table.getName().equalsIgnoreCase(tableName)) {
					isPresent = true;
				}
			}
			if (!isPresent) {
				arrangeGoldenDataForTable(tableName);
			}
		}
	}

	public void getServiceTableMap() {

		 
		List<Map<String, Object>> serviceDatum = jdbcTemplate
				.queryForList("select id, tableName, serviceName from Service");
		for (Iterator<Map<String, Object>> iterator = serviceDatum.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			serviceTableMap.put((String) map.get("serviceName"), (String) map.get("tableName"));
			String id="";
			try{
				id=Integer.toString((int) map.get("id"));
				}catch(Exception e) {
				
				id=(	(BigDecimal) map.get("id")).toString();
			}
			serviceAttrbMap(id, (String) map.get("serviceName"));

		}
		 
	}
	public void setServiceTableMap(String tableName) {

		
		List<Map<String, Object>> serviceDatum = jdbcTemplate
				.queryForList("select id, tableName, serviceName from Service where tableName= '"+tableName+"'");
		boolean isPresent=false;
		for (Iterator<Map<String, Object>> iterator = serviceDatum.iterator(); iterator.hasNext();) {
			isPresent=true;
			Map<String, Object> map = iterator.next();
			 
				serviceTableMap.put((String) map.get("serviceName"), (String) map.get("tableName"));
			 
			String id="";
			try{
				id=Integer.toString((int) map.get("id"));
				}catch(Exception e) {
				
				id=(	(BigDecimal) map.get("id")).toString();
			}
			serviceAttrbMap(id, (String) map.get("serviceName"));

		}
		 if(!isPresent)
		 {
			 
			 try {
				 boolean isPresentTable=false;
					for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMap.entrySet()) {
						DbTable table = entry.getKey();
						if (table.getName().equalsIgnoreCase(tableName)) {
							isPresentTable=true;
						}
					}
				  if(isPresentTable)
				{
					  insertServiceTables(tableName);
				      setServiceTableMap(tableName);
				      insertServiceTables(tableName);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
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
	private void insertServiceTables(String tableName) throws Exception {

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

			for (Map.Entry<DbTable, List<DbColumn>> entry : tableColumnMap.entrySet()) {
				DbTable table = entry.getKey();
				if (table.getName().equalsIgnoreCase(tableName)) {
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
				}}
		

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
					try
					{
						serID = Integer.toString((int) data.get(0).get("id"));
					}catch(Exception e) {
						java.math.BigDecimal setIDs=	(java.math.BigDecimal ) data.get(0).get("id");
						serID=setIDs.toString();
					}
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
					try {attrID = Integer.toString((int) data.get(0).get("id"));}catch(Exception e) {
						
						try{attrID = Long.toString((Long) data.get(0).get("id"));	}catch(Exception es) {
							BigDecimal datas =(BigDecimal) data.get(0).get("id");
							attrID=datas.toString();
							
						}
					}
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
				
				try{attrID = Long.toString((Long) data.get("ID"));	}catch(Exception es) {
					BigDecimal datas =(BigDecimal) data.get("ID");
					attrID=datas.toString();
					
				}
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
		
		int returnInt=0;
		
		try{returnInt=Integer.parseInt(returnStr);}catch(Exception e) {log.info(e.getMessage());}
		
		return returnInt;
	}
	
	public String getSqlTypeWithoutScal(String mysqlDataType)
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
		
		 
		
		 
		
		return mysqlDataType.replace("(", "").replace(")", "").replace(returnStr, "");
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
	
	public boolean isValidForSelect(String type)
	{

		if ("BIT".equalsIgnoreCase(type)) {
			return true;
		}
		if ("INT".equalsIgnoreCase(type)) {
			return true;
		}
		if ("TINYINT".equalsIgnoreCase(type)) {
			return true;
		}

		if ("SMALLINT".equalsIgnoreCase(type)) {
			return true;
		}

		if ("INTEGER".equalsIgnoreCase(type)) {
			return true;
		}

		if ("BIGINT".equalsIgnoreCase(type)) {
			return true;
		}

		if ("FLOAT".equalsIgnoreCase(type)) {
			return true;
		}

		if ("REAL".equalsIgnoreCase(type)) {
			return true;
		}

		if ("DOUBLE".equalsIgnoreCase(type)) {
			return true;
		}

		if ("NUMERIC".equalsIgnoreCase(type)) {
			return true;
		}

		if ("DECIMAL".equalsIgnoreCase(type)) {
			return true;
		}

		if ("CHAR".equalsIgnoreCase(type)) {
			return true;
		}

		if ("VARCHAR".equalsIgnoreCase(type)) {
			return true;
		}

		if ("LONGVARCHAR".equalsIgnoreCase(type)) {
			return true;
		}

		if ("DATE".equalsIgnoreCase(type)) {
			return true;
		}

		if ("TIME".equalsIgnoreCase(type)) {
			return true;
		}

		if ("TIMESTAMP".equalsIgnoreCase(type)) {
			return true;
		}

		if ("BINARY".equalsIgnoreCase(type)) {
			return false;
		}

		if ("VARBINARY".equalsIgnoreCase(type)) {
			return false;
		}

		if ("LONGVARBINARY".equalsIgnoreCase(type)) {
			return false;
		}

		if ("NULL".equalsIgnoreCase(type)) {
			return false;
		}

		if ("OTHER".equalsIgnoreCase(type)) {
			return false;
		}

		if ("JAVA_OBJECT".equalsIgnoreCase(type)) {
			return false;
		}

		if ("DISTINCT".equalsIgnoreCase(type)) {
			return false;
		}

		if ("STRUCT".equalsIgnoreCase(type)) {
			return false;
		}

		if ("ARRAY".equalsIgnoreCase(type)) {
			return false;
		}

		if ("BLOB".equalsIgnoreCase(type)) {
			return false;
		}

		if ("CLOB".equalsIgnoreCase(type)) {
			return false;
		}

		if ("REF".equalsIgnoreCase(type)) {
			return false;
		}

		if ("DATALINK".equalsIgnoreCase(type)) {
			return false;
		}

		if ("BOOLEAN".equalsIgnoreCase(type)) {
			return true;
		}

		if ("ROWID".equalsIgnoreCase(type)) {
			return false;
		}

		if ("NCHAR".equalsIgnoreCase(type)) {
			return true;
		}

		if ("NVARCHAR".equalsIgnoreCase(type)) {
			return true;
		}

		if ("LONGNVARCHAR".equalsIgnoreCase(type)) {
			return true;
		}

		if ("NCLOB".equalsIgnoreCase(type)) {
			return false;
		}

		if ("SQLXML".equalsIgnoreCase(type)) {
			return false;
		}

		return false;
	}
		
	public static int getSqlTypeName(String type) {
	     
	    if("BIT".equalsIgnoreCase(type))
	    	{ return Types.BIT;
	    	}
	        
	    
	    	if("TINYINT".equalsIgnoreCase(type))
	    	{
	    		return Types.TINYINT;
	    	}
	    
	    	if("SMALLINT".equalsIgnoreCase(type))
	    	{
	    		return Types.SMALLINT;
	    	}
	   
	    	if("INTEGER".equalsIgnoreCase(type))
	    	{
	    		 return Types.INTEGER;
	    	}
	    
	    	if("BIGINT".equalsIgnoreCase(type))
	    	{
	    		return Types.BIGINT;
	    	}
	    
	    	if("FLOAT".equalsIgnoreCase(type))
	    	{
	    		return Types.FLOAT;
	    	}
	    
	    	if("REAL".equalsIgnoreCase(type))
	    	{
	    		return Types.REAL;
	    	}
	    
	    	if("DOUBLE".equalsIgnoreCase(type))
	    	{
	    		return Types.DOUBLE;
	    	}
	   
	    	if("NUMERIC".equalsIgnoreCase(type))
	    	{
	    		 return Types.NUMERIC;
	    	}
	  
	    	if("DECIMAL".equalsIgnoreCase(type))
	    	{
	    		  return Types.DECIMAL;
	    	}
	    
	    	if("CHAR".equalsIgnoreCase(type))
	    	{
	    		return Types.CHAR;
	    	}
	   
	    	if("VARCHAR".equalsIgnoreCase(type))
	    	{
	    		 return Types.VARCHAR;
	    	}
	    
	    	if("LONGVARCHAR".equalsIgnoreCase(type))
	    	{
	    		return Types.LONGVARCHAR;
	    	}
	   
	    	if("DATE".equalsIgnoreCase(type))
	    	{
	    		 return Types.DATE;
	    	}
	    
	    	if("TIME".equalsIgnoreCase(type))
	    	{
	    		return Types.TIME;
	    	}
	   
	    	if("TIMESTAMP".equalsIgnoreCase(type))
	    	{
	    		 return Types.TIMESTAMP;
	    	}
	    
	    	if("BINARY".equalsIgnoreCase(type))
	    	{
	    		return Types.BINARY;
	    	}
	   
	    	if("VARBINARY".equalsIgnoreCase(type))
	    	{
	    		 return Types.VARBINARY;
	    	}
	    
	    	if("LONGVARBINARY".equalsIgnoreCase(type))
	    	{
	    		return Types.LONGVARBINARY;
	    	}
	   
	    	if("NULL".equalsIgnoreCase(type))
	    	{
	    		 return Types.NULL;
	    	}
	   
	    	if("OTHER".equalsIgnoreCase(type))
	    	{
	    		 return Types.OTHER;
	    	}
	   
	    	if("JAVA_OBJECT".equalsIgnoreCase(type))
	    	{
	    		 return Types.JAVA_OBJECT;
	    	}
	  
	    	if("DISTINCT".equalsIgnoreCase(type))
	    	{
	    		  return Types.DISTINCT;
	    	}
	   
	    	if("STRUCT".equalsIgnoreCase(type))
	    	{
	    		 return Types.STRUCT;
	    	}
	   
	    	if( "ARRAY".equalsIgnoreCase(type))
	    	{
	    		 return Types.ARRAY;
	    	}
	    
	    	if( "BLOB".equalsIgnoreCase(type))
	    	{
	    		return Types.BLOB;
	    	}
	    
	    	if( "CLOB".equalsIgnoreCase(type))
	    	{
	    		return Types.CLOB;
	    	}
	  
	    	if( "REF".equalsIgnoreCase(type))
	    	{
	    		  return Types.REF;
	    	}
	    
	    	if( "DATALINK".equalsIgnoreCase(type))
	    	{
	    		return Types.DATALINK;
	    	}
	    
	    	if( "BOOLEAN".equalsIgnoreCase(type))
	    	{
	    		return Types.BOOLEAN;
	    	}
	   
	    	if( "ROWID".equalsIgnoreCase(type))
	    	{
	    		 return Types.ROWID;
	    	}
	    
	    	if( "NCHAR".equalsIgnoreCase(type))
	    	{
	    		return Types.NCHAR;
	    	}
	    
	    	if( "NVARCHAR".equalsIgnoreCase(type))
	    	{
	    		return Types.NVARCHAR;
	    	}
	   
	    	if( "LONGNVARCHAR".equalsIgnoreCase(type))
	    	{
	    		 return Types.LONGNVARCHAR;
	    	}
	  
	    	if( "NCLOB".equalsIgnoreCase(type))
	    	{
	    		  return Types.NCLOB;
	    	}
	   
	    	if( "SQLXML".equalsIgnoreCase(type))
	    	{
	    		 return Types.SQLXML;
	    }

	    return 0;
	} 
	
	public String clearCache()
	{
		 serviceTableMap= new ConcurrentHashMap<>();
		 tableColumnMap= new ConcurrentHashMap<>();
		 serviceAttrbMap= new ConcurrentHashMap<>();
		MultiServiceImpl.MultiServiceMap= new ConcurrentHashMap<>();
		log.info( "Cache Cleared");
		return "Cache Cleared";
	}
	
	public boolean isTablePresent(String tableName) {
		List<Map<String, Object>> map = new ArrayList<>();
		try {
			map = jdbcTemplate.queryForList("desc " + tableName);
			if (map == null) {
				return false;
			}
			if (map.size() > 0) {
				log.info("table present");
				return true;
			}
		} catch (Exception e) {
			log.info(e.getMessage());
		}

		return false;
	}
	 
}
