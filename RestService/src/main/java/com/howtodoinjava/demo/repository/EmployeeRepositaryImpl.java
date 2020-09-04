package com.howtodoinjava.demo.repository;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;

import com.healthmarketscience.sqlbuilder.BaseGrantQuery.Privilege.Type;
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
import com.howtodoinjava.demo.model.EmployeeEntity;

@Repository
public class EmployeeRepositaryImpl{
	@Autowired
	DataSource dataSource;
    @Autowired
	JdbcTemplate jdbcTemplate;
    static DbSchema schemaObj;
    static DbTable table_name;
    static DbColumn column_1, column_2, column_3, column_4;
    static DbSpec specficationObj;
    static Map<String,String> serviceTableMap;
    public static Map<DbTable,List<DbColumn>> tableColumnMap;
    static Map<String,Map<String,String>> serviceAttrbMap;
    public void init()
    {
    
    	//serviceAttrbMap();
    	try {
			tableColumnMap=getMetaDatum() ;
			InitGoldenTables();
			
			serviceTableMap();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
	private void InitGoldenTables() throws Exception {
		List<DbTable> serviceTables=initializeTable();
		boolean serviceTabisPresent=false;
		for (Iterator<DbTable> iterator = serviceTables.iterator(); iterator.hasNext();) {
			DbTable dbTable = iterator.next();
			for
			  (Map.Entry<DbTable,List<DbColumn>> entry : tableColumnMap.entrySet()) {
				DbTable table=  entry.getKey(); 

			  List<DbColumn>	  column=entry.getValue();
			  System.out.println("Key = " + entry.getKey() +
			  ", Value = " + entry.getValue());
			  if(dbTable.getName().equalsIgnoreCase(table.getName()))
				{
				  serviceTabisPresent=true;	
				  System.out.println("Table Already Present: ");
				}
			  }
			
		}
		if(!serviceTabisPresent)
		{
			for (Iterator<DbTable> iterator = serviceTables.iterator(); iterator.hasNext();) {
				DbTable dbTable = iterator.next();
				createDbTable(dbTable);
				
			}
			
		
			 
		}
		tableColumnMap=getMetaDatum() ;
		insertServiceTables();
		
	}
	
	public EmployeeEntity getEmployeeByName(String name)
	{
		//jdbcTemplate = new JdbcTemplate(dataSource);
		
	List<Map<String ,Object>> result=	jdbcTemplate.queryForList("select * from TBL_EMPLOYEES where first_name = ? ", new Object[] { name });
		/*
		 * Session session = this.sessionFactory.getCurrentSession();
		 * Query<EmployeeEntity> query =
		 * session.createQuery("from EmployeeEntity E WHERE E.first_name= :name  ");
		 * query.setParameter("name",name);
		 */
	EmployeeEntity employeeEntity=null;
		for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			 employeeEntity=new EmployeeEntity();
			employeeEntity.setEmail(map.get("email").toString());
			employeeEntity.setFirstName(map.get("first_name").toString());
			employeeEntity.setLastName(map.get("last_name").toString());
			employeeEntity.setId(Long.parseLong(map.get("id").toString()));
		}
		return  employeeEntity;
	}
	public List<EmployeeEntity> getAll()
	{
		//jdbcTemplate = new JdbcTemplate(dataSource);
		List<EmployeeEntity> listEmp=new ArrayList<EmployeeEntity>();	
	List<Map<String ,Object>> result=	jdbcTemplate.queryForList("select * from TBL_EMPLOYEES  ");
		/*
		 * Session session = this.sessionFactory.getCurrentSession();
		 * Query<EmployeeEntity> query =
		 * session.createQuery("from EmployeeEntity E WHERE E.first_name= :name  ");
		 * query.setParameter("name",name);
		 */
	EmployeeEntity employeeEntity=null;
		for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			 employeeEntity=new EmployeeEntity();
			employeeEntity.setEmail(map.get("email").toString());
			employeeEntity.setFirstName(map.get("first_name").toString());
			employeeEntity.setLastName(map.get("last_name").toString());
			employeeEntity.setId(Long.parseLong(map.get("id").toString()));
			listEmp.add(employeeEntity);
		}
		return  listEmp;
	}
	   private static void loadSQLBuilderSchema() {
	        specficationObj = new DbSpec();
	        schemaObj = specficationObj.addDefaultSchema();
	    }
	 
	public List<Map<String ,Object>> getServiceDataByName(String name) throws Exception
	{
		/*
		 * loadSQLBuilderSchema(); //jdbcTemplate = new JdbcTemplate(dataSource);
		 * table_name = schemaObj.addTable(name); column_1 = table_name.addColumn("id",
		 * Types.INTEGER, 10); column_2 = table_name.addColumn("first_name",
		 * Types.VARCHAR, 250); column_3 = table_name.addColumn("last_name",
		 * Types.VARCHAR, 250); column_4 = table_name.addColumn("email", Types.VARCHAR,
		 * 250); String query= new
		 * SelectQuery().addColumns(column_1).addColumns(column_2).addColumns(column_3).
		 * validate().toString(); System.out.println(query); List<Map<String ,Object>>
		 * result= jdbcTemplate.queryForList("select * from "+name);
		 */
		return getDataForParams(name, new HashMap<String, String>()) ;
	}
	public Map<DbTable,List<DbColumn>> getMetaDatum() throws Exception
	{
		 loadSQLBuilderSchema();
		Map<DbTable,List<DbColumn>> metaDatum=new HashMap<DbTable,List<DbColumn>>();
		DatabaseMetaData md = dataSource.getConnection().getMetaData();
		ResultSet rs = md.getTables(null, null, "%", new String[] { "TABLE",
				"VIEW"/*
						 * , "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"
						 */});
	  while (rs.next()) {
		  
		   DbTable table_name = schemaObj.addTable(rs.getString("TABLE_NAME"));
		   String  tableName=rs.getString("TABLE_NAME");
	       System.out.println(tableName);
	       String query;
	       SelectQuery selectQuery= new SelectQuery();
	       List<DbColumn> listArray=new ArrayList<DbColumn>();
		ResultSet rs1 = md.getColumns(null, null, rs.getString("TABLE_NAME"), null);
		ResultSet rs2=md.getPrimaryKeys(null, null,  rs.getString("TABLE_NAME"));
		String primaryKey = "";
		while(rs2.next())
		{
			primaryKey=	rs2.getString("COLUMN_NAME");
		}
		
		while(rs1.next())
		{
			 DbColumn column_1 =table_name.addColumn(rs1.getString("COLUMN_NAME"),Integer.parseInt(rs1.getString("DATA_TYPE")),Integer.parseInt(rs1.getString("COLUMN_SIZE")));
			
			 if(rs1.getString("COLUMN_NAME").equals(primaryKey))
			 {
				 column_1.primaryKey(); 
			 }
			 listArray.add(column_1);
			 System.out.println(rs1.getString("DATA_TYPE"));
			System.out.println(rs1.getString("COLUMN_NAME"));
			System.out.println(rs1.getString("COLUMN_SIZE"));
			 
		}
		System.out.println(selectQuery.addAllTableColumns(table_name).validate().toString());
		metaDatum.put(table_name, listArray);
	  }
		
		return metaDatum;
	}
	
	
	  public  void createDbTable(DbTable table_name) {
			System.out.println("\n=======Creating '" +table_name.getName() + "' In The Database=======\n");
			 loadSQLBuilderSchema();
	        try {
	            // Specifying Table Name
	           // table_name = schemaObj.addTable(TABLE_NAME);
	 
	            // Specifying Column Names For The Table
				/*
				 * column_1 = table_name.addColumn(COLUMN_ONE, Types.INTEGER, 10); column_2 =
				 * table_name.addColumn(COLUMN_TWO, Types.VARCHAR, 100); column_3 =
				 * table_name.addColumn(COLUMN_THREE, Types.INTEGER, 200);
				 */
	 
	            String createTableQuery = new CreateTableQuery(table_name, true).validate().toString();
	            System.out.println("\nGenerated Sql Query?= "+ createTableQuery + "\n");
	            jdbcTemplate.execute(createTableQuery);
	        } catch(Exception sqlException) {
	            sqlException.printStackTrace();
	        }
	        System.out.println("\n=======The '" + table_name.getName() + "' Successfully Created In The Database=======\n");
	    }
	public List<DbTable> initializeTable()
	{
		
		loadSQLBuilderSchema();
		//jdbcTemplate = new JdbcTemplate(dataSource);
		DbColumn id, tableName, serviceName,attrid,serid, attrName,colName;
		DbTable table_service = schemaObj.addTable("Service");
		DbTable table_service_attr = schemaObj.addTable("Service_Attr");
		
		id=table_service.addColumn("id", Types.INTEGER, 10);
		id.primaryKey();
		DbConstraint constraint= new DbConstraint(id, "tabUnique", Constraint.Type.UNIQUE);
		//id.addConstraint(constraint);
		tableName=table_service.addColumn("tableName", Types.VARCHAR, 100);
		//tableName.addConstraint(constraint);
		serviceName=table_service.addColumn("serviceName", Types.VARCHAR, 100);
		attrid=table_service_attr.addColumn("id", Types.INTEGER, 10);
		attrid.primaryKey();
		serid=table_service_attr.addColumn("service_id", Types.INTEGER, 10);
		attrName=table_service_attr.addColumn("attrName", Types.VARCHAR, 100);
		colName=table_service_attr.addColumn("colName", Types.VARCHAR, 100);
		 List<DbTable> initialTable=new ArrayList<DbTable>();
		 initialTable.add(table_service);
		 initialTable.add(table_service_attr);
		return initialTable;
	}
	
	public  Map<String ,Object> insertData(String service,Map<String ,String> params) throws Exception
	{
		loadSQLBuilderSchema();
	String tableName=	serviceTableMap.get(service);
	InsertQuery insertQuery;
	String primaryKey=null;
	Map<String,String> attribParamMap=serviceAttrbMap.get(service);
	 for (Map.Entry<DbTable,List<DbColumn>> entry : tableColumnMap.entrySet())  {
		 DbTable table= entry.getKey();
		if( table.getName().equalsIgnoreCase(tableName))
		 {
			table = schemaObj.addTable(table.getName());
			insertQuery=new InsertQuery(table);
			List<DbColumn> column=entry.getValue();
			for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
				DbColumn dbColumn =  iterator.next();
				List<DbConstraint> dbConstr= dbColumn.getConstraints();
				boolean isPrimaryKey=false;
				for (Iterator<DbConstraint> iterator2 = dbConstr.iterator(); iterator2.hasNext();) {
					DbConstraint dbConstraint = iterator2.next();
					if(dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString()))
					{
						isPrimaryKey=true;
					
						if( (params.get(attribParamMap.get(dbColumn.getName())) != null) )
						primaryKey=params.get(attribParamMap.get(dbColumn.getName()));
					}
				}
				
				if (params.get(attribParamMap.get(dbColumn.getName())) != null) {
					insertQuery.addColumn(dbColumn, params.get(attribParamMap.get(dbColumn.getName())));
				}
			}
			
            System.out.println("Key = " + entry.getKey() + 
                             ", Value = " + entry.getValue()); 
            System.out.println(insertQuery.toString());
            if(primaryKey!=null)
            {
            	jdbcTemplate.execute(insertQuery.toString());
            	}else {
            		
            		throw new Exception("Primary key not found");
            	}
            break;
		 }
		
		
    }
	 return getData(service, primaryKey); 
		
	}
	public Map<String ,Object> updateData(String service,Map<String ,String> params) throws Exception
	{
		
		String tableName=	serviceTableMap.get(service);
		UpdateQuery updateQuery= new UpdateQuery(tableName);
		int isUpdate = 0;
		String primaryKey="";
		Map<String,String> attribParamMap=serviceAttrbMap.get(service);
		 for (Map.Entry<DbTable,List<DbColumn>> entry : tableColumnMap.entrySet())  {
			 DbTable table= entry.getKey();
			if( table.getName().equalsIgnoreCase(tableName))
			 {
				table = schemaObj.addTable(table.getName());
				updateQuery=new UpdateQuery(table);
				List<DbColumn> column=entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn =  iterator.next();
					List<DbConstraint> dbConstr= dbColumn.getConstraints();
					boolean isPrimaryKey=false;
					for (Iterator<DbConstraint> iterator2 = dbConstr.iterator(); iterator2.hasNext();) {
						DbConstraint dbConstraint = iterator2.next();
						if(dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString()))
						{
							isPrimaryKey=true;
							if( (params.get(attribParamMap.get(dbColumn.getName())) != null) )
							updateQuery.addCondition(BinaryCondition.equalTo(dbColumn, params.get(attribParamMap.get(dbColumn.getName()))));
							primaryKey=params.get(attribParamMap.get(dbColumn.getName()));
						}
					}
					if( (params.get(attribParamMap.get(dbColumn.getName())) != null) )
						updateQuery.addSetClause(dbColumn,  params.get(attribParamMap.get(dbColumn.getName())));
					
				}
				
	            System.out.println("Key = " + entry.getKey() + 
	                             ", Value = " + entry.getValue()); 
	            //params.get(attribParamMap.get(dbColumn.getName()))
	            System.out.println(updateQuery.toString());
	            isUpdate= jdbcTemplate.update(updateQuery.toString());
	            break;
			 }
	    } 
			if(isUpdate>0)
			{
				
				return getDataForParams(service, params).get(0);
			}else 
			{
				throw new Exception("update Failed");
			}
			 
		
	}
	
	
	public  Map<String ,Object> getData(String serviceName, String primaryKeyValue)
	{
		 
		 SelectQuery selectQuery= new SelectQuery();
		 String tableName=	serviceTableMap.get(serviceName);
		 Map<String,String> attribParamMap=serviceAttrbMap.get(serviceName);
		 for (Map.Entry<DbTable,List<DbColumn>> entry : tableColumnMap.entrySet())  {
			 DbTable table= entry.getKey();
			if( table.getName().equalsIgnoreCase(tableName))
			 {
				List<DbColumn> column=entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn =  iterator.next();
					List<DbConstraint> dbConstr= dbColumn.getConstraints();
					for (Iterator<DbConstraint> iterator2 = dbConstr.iterator(); iterator2.hasNext();) {
						DbConstraint dbConstraint = iterator2.next();
						if(dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString()))
						{
							selectQuery.addCondition(BinaryCondition.equalTo(dbColumn, primaryKeyValue));
							
						}
					}
						selectQuery.addAliasedColumn(dbColumn,"\""+attribParamMap.get(dbColumn.getName())+"\"");
				}
				System.out.println(selectQuery.validate().toString());
			break;
			 }
			}
		 Map<String ,Object> result=	jdbcTemplate.queryForMap(selectQuery.validate().toString());
		return result;
	}
	
	
	public  Map<String ,Object> deleteData(String serviceName, String primaryKeyValue) throws Exception
	{
		 
		 DeleteQuery deleteQuery=null;
		 Map<String ,Object> deletingVal;
		 String tableName=	serviceTableMap.get(serviceName);
		 Map<String,String> attribParamMap=serviceAttrbMap.get(serviceName);
		 for (Map.Entry<DbTable,List<DbColumn>> entry : tableColumnMap.entrySet())  {
			 DbTable table= entry.getKey();
			if( table.getName().equalsIgnoreCase(tableName))
			 {
				deleteQuery= new DeleteQuery(table);
				List<DbColumn> column=entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn =  iterator.next();
					List<DbConstraint> dbConstr= dbColumn.getConstraints();
					for (Iterator<DbConstraint> iterator2 = dbConstr.iterator(); iterator2.hasNext();) {
						DbConstraint dbConstraint = iterator2.next();
						if(dbConstraint.getType().toString().equals(Constraint.Type.PRIMARY_KEY.toString()))
						{
							deleteQuery.addCondition(BinaryCondition.equalTo(dbColumn, primaryKeyValue));
							
						}
					}
						//selectQuery.addAliasedColumn(dbColumn,"\""+attribParamMap.get(dbColumn.getName())+"\"");
				}
				System.out.println(deleteQuery.validate().toString());
			break;
			 }
			}
		 System.out.println(deleteQuery.validate().toString());
		 Map<String ,Object> retValue= getData(serviceName, primaryKeyValue);
		 int result=	jdbcTemplate.update(deleteQuery.validate().toString());
		 if(result>0)
		 {
			 return retValue;
		 }else
		 {
			 throw new Exception("Deletion Failed ");
		 }
		 
	}
	
	public List<Map<String ,Object>> getDataForParams(String serviceName,Map<String,String> params) throws Exception
	{
		 
		 SelectQuery selectQuery= new SelectQuery();
		 String tableName=	serviceTableMap.get(serviceName);
		 if(tableName==null)
		 {
			 throw new Exception("Service not found Exception");
		 }
		 Map<String,String> attribParamMap=serviceAttrbMap.get(serviceName);
		 for (Map.Entry<DbTable,List<DbColumn>> entry : tableColumnMap.entrySet())  {
			 DbTable table= entry.getKey();
			if( table.getName().equalsIgnoreCase(tableName))
			 {
				List<DbColumn> column=entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn =  iterator.next();
				  if(attribParamMap.containsKey(dbColumn.getName())&&params.get(attribParamMap.get(dbColumn.getName()))!=null)
							{
							 selectQuery.addCondition(BinaryCondition.equalTo(dbColumn, params.get(attribParamMap.get(dbColumn.getName()))));
							 }
					 selectQuery.addAliasedColumn(dbColumn,"\""+attribParamMap.get(dbColumn.getName())+"\"");
				}
				System.out.println(selectQuery.validate().toString());
			break;
			 }
			}
		 List<Map<String ,Object>>  result=	jdbcTemplate.queryForList(selectQuery.validate().toString());
		return result;
	}
	
	public void serviceTableMap()
	{
		
		serviceTableMap=new HashMap<String , String>();
		serviceAttrbMap=new HashMap<String ,Map<String,String>>();
		List<Map<String,Object>> serviceDatum= jdbcTemplate.queryForList("select id, tableName, serviceName from Service"); 
		for (Iterator<Map<String, Object>> iterator = serviceDatum.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			serviceTableMap.put((String)map.get("serviceName"),(String)  map.get("tableName"));
			serviceAttrbMap(Integer.toString((int)map.get("id")),(String)map.get("serviceName"));
			
		}
		//serviceTableMap.put("employee", "TBL_EMPLOYEES");
		//serviceTableMap.put("student", "TBL_STUDENT");
	}
	public void serviceAttrbMap(String id,String serviceName)
	{
		
		Map<String,String> studentAttrbMap=new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);
		List<Map<String,Object>> serviceDatum= jdbcTemplate.queryForList("select colName, attrName from Service_Attr where service_id = '"+id+"'"); 
		Map<String,String> AttrbMap;
		 AttrbMap=new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);
		for (Iterator<Map<String, Object>> iterator = serviceDatum.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			
			AttrbMap.put((String)map.get("colName".toUpperCase()), (String)map.get("attrName".toUpperCase()));
			serviceAttrbMap.put(serviceName,AttrbMap);
		}
		/*
		 * studentAttrbMap.put("id", "id"); studentAttrbMap.put("first_name",
		 * "First Name"); studentAttrbMap.put("last_name", "Last Name");
		 * studentAttrbMap.put("email", "Email Id"); serviceAttrbMap.put("student",
		 * studentAttrbMap); Map<String,String> emptAttrbMap=new
		 * TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER); emptAttrbMap.put("id",
		 * "id"); emptAttrbMap.put("first_name", "First Name");
		 * emptAttrbMap.put("last_name", "Last Name"); emptAttrbMap.put("email",
		 * "Email Id"); serviceAttrbMap.put("employee", emptAttrbMap);
		 */
	}
 
	private void insertServiceTables() throws Exception {

		
		 for (Map.Entry<DbTable,List<DbColumn>> entry : tableColumnMap.entrySet())  {
			 DbTable table= entry.getKey();
			 String tableName=table.getName();
			  
				 
			 String serviceInsertQuery="INSERT INTO   Service (id ,tableName , serviceName )   values( (SELECT MAX( id )+1 FROM Service ser) , '"+tableName+"', '"+tableName.toLowerCase().replace("_", " ")+"'  )";
			 String serviceid=getServiceID(tableName);
			String maxRec= FindMax("Service");
			 if(maxRec!=null&&serviceid==null) 
			 {
				 jdbcTemplate.execute(serviceInsertQuery); 
				 serviceid=getServiceID(tableName);
			 }else if(maxRec==null)
			 {
				 serviceInsertQuery="INSERT INTO   Service (id ,tableName , serviceName )   values( 0, '"+tableName+"', '"+tableName.toLowerCase().replace("_", " ")+"'  )";
				 jdbcTemplate.execute(serviceInsertQuery); 
				 serviceid=getServiceID(tableName);	 
			 }
			
			 List<DbColumn> column=entry.getValue();
				for (Iterator<DbColumn> iterator = column.iterator(); iterator.hasNext();) {
					DbColumn dbColumn = iterator.next();
					String serviceAttrid=getServiceAttrID(serviceid,dbColumn.getName());
					String maxRecAttr= FindMax("Service_Attr");
					if (maxRecAttr!=null) {
						String serviceAttrQuery = " INSERT INTO Service_Attr (id, service_id , attrName, colName) values ((SELECT MAX( id )+1 FROM Service_Attr serA) ,'"+ serviceid + "','" + dbColumn.getName().toLowerCase().replace("_"," ") + "','" + dbColumn.getName() +"') ";
						jdbcTemplate.execute(serviceAttrQuery);
					}else
					{
						
						 {
								String serviceAttrQuery = " INSERT INTO Service_Attr (id, service_id , attrName, colName) values (0 ,'"	+ serviceid + "','" + dbColumn.getName().toLowerCase().replace("_"," ") + "','" + dbColumn.getName() +"') ";
								jdbcTemplate.execute(serviceAttrQuery);
							}
						
					}
				}	
		 }
		 
	 
	}
	private String getServiceID(String tableName) {

		String selectQuery=" select id from Service where tableName = '"+tableName+"'";
		List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
		try {
			data=	jdbcTemplate.queryForList(selectQuery);
		}catch(Exception e) {
			System.out.println(e.getMessage());
			}
		String serID = null;
		if(data!=null)
		if(data.size()!=0)
		{
			if(data.get(0).get("ID")!=null)
			{
			serID=Integer.toString((int)data.get(0).get("id"));
			}
		}
		return serID;
	}
	private String getServiceAttrID(String serviceId,String attributeName) {

		String selectQuery=" select id  from Service_Attr where service_id  = '"+serviceId+"' and attrName ='"+attributeName+"'";
		List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
		try { data=	jdbcTemplate.queryForList(selectQuery);
		}catch(Exception e)
		{System.out.println(e.getMessage());}
		
		String attrID = null;
		if(data!=null)
		if(data.size()!=0)
		{
			if(data.get(0).get("ID")!=null)
			{
				attrID=Integer.toString((int)data.get(0).get("id"));
			}
		}
		return attrID;
	}
	private String FindMax(String table)
	{
		String Query="SELECT MAX( id )+1 as id FROM "+table;
		Map<String,Object> data=new HashMap<String,Object>();;
try { 
	data=	jdbcTemplate.queryForMap(Query);}catch(Exception e) {System.out.println(e.getMessage());}
		
		String attrID = null;
		if(data.get("ID")!=null)
			{
			attrID=Integer.toString((int)data.get("ID"));
			}
		return attrID;
		
	}
}
