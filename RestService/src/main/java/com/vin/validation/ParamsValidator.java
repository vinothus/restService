package com.vin.validation;

import static com.vin.validation.ParamsValidator.userServiceAttrTableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vin.rest.repository.EmployeeRepositaryImpl;

import vin.rest.common.Constant;
@Component
public class ParamsValidator implements ConstraintValidator<ParamMapValidator,Map<String,String>>{

	
	Logger log = Logger.getLogger(ParamsValidator.class.getName());
	
	public static Map<String,List<Map<String, Object>>> userServiceTableMap= new ConcurrentHashMap<>();
	public static Map<String,List<Map<String, Object>>> userServiceAttrTableMap= new ConcurrentHashMap<>();
	public static Map<String, String> dsidMap=new ConcurrentHashMap<>();
	public static Map<String,List<Map<String, Object>>> userVinValidationAttrMap= new ConcurrentHashMap<>();
	public static Map<String,List<Map<String, Object>>> userApiKeyMap= new ConcurrentHashMap<>();
	public static Map<String, Object> reflecClass=new ConcurrentHashMap<>();
	public static Map<String, String> UserApiMap=new ConcurrentHashMap<>();
	@Autowired
	EmployeeRepositaryImpl employeeRepositaryImpl;
	
	@Autowired
	private Environment env;
	
	@Autowired
	ApplicationContext context;
	
	@Override
	public void initialize(ParamMapValidator constraintAnnotation) {

	}
  
	@Override
	public boolean isValid(Map<String, String> value, ConstraintValidatorContext context) {
        boolean valid=true;
        boolean staticbool=true;
		String serviceName = value.get(Constant.VIN_SERVICE);
		String dataStoreKey = value.get(Constant.VIN_SERVICE_DS);
		String apiKey = value.get(Constant.VIN_SERVICE_APIKEY);
		String method = value.get(Constant.REST_METHOD);
		value.remove(Constant.VIN_SERVICE);
		value.remove(Constant.VIN_SERVICE_DS);
		value.remove(Constant.VIN_SERVICE_APIKEY);
		String dsid = null;
		if(userServiceAttrTableMap.size()==0)
		{
			fillServiceAttrbMap(apiKey);
		}
		if (userApiKeyMap.get(apiKey) == null) {
			List<Map<String, Object>> dbobj = employeeRepositaryImpl.setUserDataStore(apiKey, "system", "none")
					.queryForList("select * from User where apikey = ?  ", new Object[] { apiKey });
			if (dbobj.size() > 0) {
				userApiKeyMap.put(apiKey, dbobj);
			} else {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(" ApiKey  is invalid   ").addConstraintViolation();
				return false;
			}
		}
		if(serviceName!=null&&dataStoreKey!=null&&apiKey!=null) {
		if (dsidMap.get(dataStoreKey + ":" + apiKey) == null) {
			dsid = employeeRepositaryImpl.getdsidFordsName(dataStoreKey);
			dsidMap.put(dataStoreKey + ":" + apiKey, dsid);
		} else {
			dsid = dsidMap.get(dataStoreKey + ":" + apiKey);
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put(Constant.SERVICENAME, serviceName);
		params.put(Constant.DSID, dsid);
		List<Map<String, Object>> cachedServiceObj = userServiceTableMap.get(serviceName + "" + dsid);
		List<Map<String, Object>> resultObj = null;
		JdbcTemplate jdbcTem = null;
		if (cachedServiceObj == null) {
			jdbcTem = employeeRepositaryImpl.setUserDataStore(apiKey, "system", "none");
			try {
				resultObj = jdbcTem.queryForList(
						"select * from Service where dsid= ? and  uid = ( select id   from User where   apikey =  ? ) and serviceName =? ",
						new Object[] { dsid, apiKey, serviceName });
			} catch (Exception e) {
				generateServiceTable();
				resultObj = jdbcTem.queryForList(
						"select * from Service where dsid= ? and  uid = ( select id   from User where   apikey =  ? ) and serviceName =? ",
						new Object[] { dsid, apiKey, serviceName });
			}
			if (resultObj.size() > 0) {
				if(userServiceTableMap.get(serviceName + "" + dsid)==null)
				{
					userServiceTableMap.put(serviceName + "" + dsid, resultObj);
				}else
				{
					userServiceTableMap.get(serviceName + "" + dsid).addAll(resultObj);
				}
			}

		} else {
			resultObj = new ArrayList<Map<String, Object>>();
			resultObj.addAll(cachedServiceObj);
		}
		String service_id = null;
		if (resultObj.size() > 0) {
			for (Iterator iterator = resultObj.iterator(); iterator.hasNext();) {
				Map<String, Object> map = (Map<String, Object>) iterator.next();
				if(serviceName.equalsIgnoreCase((String) (map.get("serviceName"))))
				{
					service_id = String.valueOf(resultObj.get(0).get("id"));
					break;
				}
				
			}
			
		}
if(service_id!=null) {
		params.put(Constant.service_id, service_id);
		List<Map<String, Object>> cachedServicAttreObj = userServiceAttrTableMap.get(serviceName + "" + dsid);
		List<Map<String, Object>> resultObjattr = null;
		if (cachedServicAttreObj == null) {
			if (jdbcTem == null) {
				jdbcTem = employeeRepositaryImpl.setUserDataStore(apiKey, "system", "none");
				try {
					resultObjattr = jdbcTem.queryForList("select * from Service_Attr where service_id =? ",
							new Object[] { service_id });
				} catch (Exception e) {
					generateServiceTable();
					resultObjattr = jdbcTem.queryForList("select * from Service_Attr where service_id =? ",
							new Object[] { service_id });
				}
				if (resultObjattr.size() > 0) {
					if(userServiceAttrTableMap.get(serviceName + "" + dsid)==null)
					{
						userServiceAttrTableMap.put(serviceName + "" + dsid, resultObjattr);
						
					}else
					{
						userServiceAttrTableMap.get(serviceName + "" + dsid) .addAll(resultObjattr);
					}
				}
			} else {
				try {
					resultObjattr = jdbcTem.queryForList("select * from Service_Attr where service_id =? ",
							new Object[] { service_id });

				} catch (Exception e) {
					generateServiceTable();
					resultObjattr = jdbcTem.queryForList("select * from Service_Attr where service_id =? ",
							new Object[] { service_id });
				}
				if (resultObjattr.size() > 0) {

					if(userServiceAttrTableMap.get(serviceName + "" + dsid)==null)
					{
						userServiceAttrTableMap.put(serviceName + "" + dsid, resultObjattr);
						
					}else
					{
						userServiceAttrTableMap.get(serviceName + "" + dsid) .addAll(resultObjattr);
					}
				
				}
			}

		} else {
			resultObjattr = new ArrayList<Map<String, Object>>();
			resultObjattr.addAll(cachedServicAttreObj);
		}

		for (Iterator iterator = resultObjattr.iterator(); iterator.hasNext();) {
			String validationClass;
			String attrminlength;
			String attrmaxlength;
			String attrregxvalidation;
			String attrismandatory;
			String attrname;
			String attrid;
            String paramAttrbvalue="";
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			validationClass = String.valueOf(map.get("attrCusValidation"));
			attrminlength = String.valueOf(map.get("attrminLength"));
			attrmaxlength = String.valueOf(map.get("attrMaxLength"));
			attrregxvalidation = String.valueOf(map.get("attrRegXvalidation"));
			attrismandatory = String.valueOf(map.get("attrIsMandatory"));
			attrname= String.valueOf(map.get("attrName"));
			attrid=String.valueOf(map.get("id"));
			String attrValMethods =String.valueOf(map.get("attrValMethods"));
			if(attrValMethods.toUpperCase().contains(method.toUpperCase())||attrValMethods.equalsIgnoreCase("ALL")) {
			valid=mandatoryValidation(attrname, value,attrismandatory);
			}
			paramAttrbvalue=value.get(attrname);
			if(!valid) {
				staticbool=false;
				valid=true;
				 context.disableDefaultConstraintViolation();
				 context.buildConstraintViolationWithTemplate( attrname + " is a Mandatory Attribute "  ).addConstraintViolation();
		        }
			for (Map.Entry<String, String> entry : value.entrySet()) {
				if(entry.getKey().equalsIgnoreCase(attrname))
				{
					
					if(attrValMethods.toUpperCase().contains(method.toUpperCase())||attrValMethods.equalsIgnoreCase("ALL"))
					{
						valid = doValidation(context, valid, validationClass, attrminlength, attrmaxlength, attrregxvalidation,
						map, entry);}
				if(!valid) {
					staticbool=false;
					valid=true;
				}
				}

			}
			if(attrValMethods.toUpperCase().contains(method.toUpperCase())||attrValMethods.equalsIgnoreCase("ALL")) {
			if(!minLength(paramAttrbvalue, attrminlength))
			{
				 context.disableDefaultConstraintViolation();
				 context.buildConstraintViolationWithTemplate( paramAttrbvalue+ " Should Contain Atleast "+attrminlength+" Characters "  ).addConstraintViolation();
				 valid=false;	
				 staticbool=false;
				 valid=true;
			}}
			if(attrValMethods.toUpperCase().contains(method.toUpperCase())||attrValMethods.equalsIgnoreCase("ALL")) {
			if(validationClass!=null&&(validationClass.equalsIgnoreCase("yes")||validationClass.equalsIgnoreCase("true")||validationClass.equalsIgnoreCase("1"))) {
				if(userVinValidationAttrMap.get(attrid)==null) {
					List<Map<String, Object>> obj=employeeRepositaryImpl.setUserDataStore(apiKey, "system", "none").queryForList("select * from VinValidation where attr_id = ? ", new Object[] {attrid});
					if(obj!=null&&obj.size()>0)
					{
						userVinValidationAttrMap.put(attrid,obj);
					}
				}
				
					List<Map<String, Object>> obj=userVinValidationAttrMap.get(attrid);
					if (obj != null && obj.size() > 0) {
						for (Iterator iterator2 = obj.iterator(); iterator2.hasNext();) {
							Map<String, Object> vinValidationMapmap = (Map<String, Object>) iterator2.next();
							String className=(String) vinValidationMapmap.get("classname");
							if (className != null) {
								valid = doCustomValidation(apiKey, dataStoreKey, serviceName, context, valid, className,
										map, value, paramAttrbvalue, env);
							}
							if (!valid) {
								staticbool = false;
								valid=true;
							}
						}
						
					
				}
			} 
			}
		}
		 if(!staticbool) {
			 context.disableDefaultConstraintViolation();
			 context.buildConstraintViolationWithTemplate( "In valida Parameters"  ).addConstraintViolation();
			 return staticbool;
	        }
}
		}
		
		log.info("Validator :" + value);
		return true;
	}

	private void fillServiceAttrbMap(String apiKey) {
     try {
		JdbcTemplate jdbcTem = employeeRepositaryImpl.setUserDataStore(apiKey, "system", "none");
		String Query="select ser.serviceName as name, ser.dsid as dsid , sa.attrName as attrName , sa.id as id , sa.colName as colName ,sa.attrEnable as attrEnable ,sa.attrBuName as attrBuName ,sa.attrBuIcon as attrBuIcon ,sa.attrCusValidation as attrCusValidation,sa.attrminLength as attrminLength,sa.attrMaxLength as attrMaxLength,sa.attrRegXvalidation as attrRegXvalidation,sa.attrIsMandatory as attrIsMandatory , sa.attrIsProcessor as attrIsProcessor ,sa.attrValMethods as attrValMethods ,sa.attrValidatorName as attrValidatorName ,sa.attrValidatorMsg as attrValidatorMsg , sa.attrRegXvalidationMsg as attrRegXvalidationMsg from Service ser , Service_Attr sa where ser.id=sa.service_id ";
		List<Map<String,Object>> cachedObject=jdbcTem.queryForList(Query);
		for (Iterator iterator = cachedObject.iterator(); iterator.hasNext();) {
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			
			if (userServiceAttrTableMap.get(map.get("name") + "" + map.get("dsid")) == null) {
				List<Map<String, Object>> object = new ArrayList<Map<String, Object>>();
				object.add(map);
				userServiceAttrTableMap.put(map.get("name") + "" + map.get("dsid"), object);

			} else {
				userServiceAttrTableMap.get(map.get("name") + "" + map.get("dsid")).add(map);

			}
			
			
			
		}
     }catch (Exception e)
    {
    		employeeRepositaryImpl.createSysTable(); 
	}
	
		
	}

	public boolean doValidation(ConstraintValidatorContext context, boolean valid, String validationClass,
			String attrminlength, String attrmaxlength, String attrregxvalidation, Map<String, Object> map,
			Map.Entry<String, String> entry) {
		
		if(!maxLength(entry.getValue(), attrmaxlength))
		{
			 context.disableDefaultConstraintViolation();
			 context.buildConstraintViolationWithTemplate( entry.getKey()+ "Should not Exceeding the Limit of "+attrminlength+" Characters "  ).addConstraintViolation();
			 valid=false;
		}
		if(!regexValidation(entry.getValue(), attrregxvalidation))
		{
			 context.disableDefaultConstraintViolation();
			 context.buildConstraintViolationWithTemplate( entry.getKey()+ " not in valid formatt  "  ).addConstraintViolation();
			 valid=false;
		}
		
		
		return valid;
	}

	public void generateServiceTable() {
		employeeRepositaryImpl.createSysTable();
	}

	public boolean doCustomValidation(String apiKey,String datasourceKey,String service,ConstraintValidatorContext context1, boolean valid, String validationClass,
			Map<String, Object> Attrbmap, Map<String, String> mapofVal, String valueFmParam,Environment env) {
		try {
			if(reflecClass.get(validationClass)==null)
			{
				AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
				com.vin.validatior.Validator bean = (com.vin.validatior.Validator) factory.createBean(Class
						.forName(validationClass).newInstance().getClass(), AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
				reflecClass.put(validationClass,bean);
			}
			if(reflecClass.get(validationClass)!=null)
			{
				com.vin.validatior.Validator<String> validator = (com.vin.validatior.Validator) reflecClass.get(validationClass);
				ObjectMapper om = new ObjectMapper();
				boolean valididy = validator.isValid(valueFmParam, apiKey, datasourceKey, service,
						om.writeValueAsString(Attrbmap), om.writeValueAsString(mapofVal),
						om.writeValueAsString(getAllKnownProperties(env)));
				log.info(validationClass);
				if (!valididy) {
					context1.disableDefaultConstraintViolation();
					context1.buildConstraintViolationWithTemplate(validator.getErrorMsg().replace("$", valueFmParam))
							.addConstraintViolation();

					valid = false;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return valid;

	}
	public boolean doCustomValidation(String apiKey,String datasourceKey,String service, boolean valid, String validationClass,
			Map<String, Object> Attrbmap, Map<String, String> mapofVal, String valueFmParam,Environment env) {
		try {
			if(reflecClass.get(validationClass)==null)
			{
				AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
				com.vin.validatior.Validator bean = (com.vin.validatior.Validator) factory.createBean(Class
						.forName(validationClass).newInstance().getClass(), AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
				reflecClass.put(validationClass,bean);
			}
			if(reflecClass.get(validationClass)!=null)
			{
				com.vin.validatior.Validator<String> validator = (com.vin.validatior.Validator) reflecClass.get(validationClass);
				ObjectMapper om = new ObjectMapper();
				boolean valididy = validator.isValid(valueFmParam, apiKey, datasourceKey, service,
						om.writeValueAsString(Attrbmap), om.writeValueAsString(mapofVal),
						om.writeValueAsString(getAllKnownProperties(env)));
				log.info(validationClass);
				if (!valididy) {
					valid = false;
				}else {
					valid=valididy;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return valid;

	}
	public boolean minLength(String value, String minLength) {

		Integer minlen = 0;
		try {
			minlen = Integer.parseInt(minLength);
		} catch (Exception e) {
		}
		if (minlen != 0 && value==null)
		{
			return false;
		}
		if (minlen != 0 && value!=null&&value.length() < minlen) {
			return false;
		}
		return true;
	}

	public boolean maxLength(String value, String maxLength) {

		Integer maxlen = 0;
		try {
			maxlen = Integer.parseInt(maxLength);
		} catch (Exception e) {
		}
		if (maxlen != 0  && value!=null&& value.length() > maxlen) {
			return false;
		}
		
		return true;
	}

	public boolean regexValidation(String value, String regex) {
		if (value != null && !value.equals("null") && regex != null && !regex.equals("null")) {
			return Pattern.matches(regex, value);
		}
		return true;
	}
	public boolean mandatoryValidation(String validKey,Map map, String attrismandatory) {
		if (map != null  && validKey != null && !validKey.equals("null")&&attrismandatory!=null&&!attrismandatory.equals("null")) {
			if(attrismandatory.equalsIgnoreCase("true")||attrismandatory.equalsIgnoreCase("yes")||attrismandatory.equalsIgnoreCase("1"))
			if((map.get(validKey)==null))
			{
				return false;
			}
		}
		return true;
	}
	public static void clearCache()
	{
		userServiceTableMap= new ConcurrentHashMap<>();
		 userServiceAttrTableMap= new ConcurrentHashMap<>();
		 userVinValidationAttrMap= new ConcurrentHashMap<>();
		dsidMap=new ConcurrentHashMap<>();
	}
	
	public static Map<String, Object> getAllKnownProperties(Environment env) {
	    Map<String, Object> rtn = new HashMap<>();
	    if (env instanceof ConfigurableEnvironment) {
	        for (PropertySource<?> propertySource : ((ConfigurableEnvironment) env).getPropertySources()) {
	            if (propertySource instanceof EnumerablePropertySource) {
	                for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
	                    rtn.put(key, propertySource.getProperty(key));
	                }
	            }
	        }
	    }
	    return rtn;
	}
	
	public boolean validateSingleAttr(String service, String apikey,String dataStoreKey, Map<String, String> params, String validatorName, String value)
	{
		boolean validity=false;
		boolean isPresentIncache=false;
		   for (Entry<String, List<Map<String, Object>>> entry : userVinValidationAttrMap.entrySet())  {
	            System.out.println("Key = " + entry.getKey() + 
	                             ", Value = " + entry.getValue()); 
	          for (Iterator<Map<String, Object>> iterator = entry.getValue().iterator(); iterator.hasNext();) {
				Map<String, Object> vinValidationMap = (Map<String, Object>) iterator.next();
			String validationClass=String.valueOf(vinValidationMap.get("classname"));
			String name=String.valueOf(vinValidationMap.get("name"));
			if(name.equalsIgnoreCase(validatorName))	
			{
				isPresentIncache=true;
				validity=	doCustomValidation(apikey,dataStoreKey, service,  validity, validationClass, new HashMap<String, Object>(), params, value, env);
				if(!validity)
				{
					return validity;
				}
			}
				
			}
	            
	    } 
		
		if(!isPresentIncache) {
			List<Map<String, Object>> obj=employeeRepositaryImpl.setUserDataStore("system", "system", "none").queryForList("select * from VinValidation where name = ? ", new Object[] {validatorName});
			if(obj!=null&&obj.size()>0)
			{
				for (Iterator iterator = obj.iterator(); iterator.hasNext();) {
					Map<String, Object> map = (Map<String, Object>) iterator.next();
					String validationClass=String.valueOf(map.get("classname"));
					String name=String.valueOf(map.get("name"));
					String attrbid=String.valueOf(map.get("attr_id"));
					if(name.equalsIgnoreCase(validatorName))	
					{
						if(userVinValidationAttrMap.get(attrbid)==null) {
							
							List<Map<String, Object>> tempList=new ArrayList<>();
							tempList.add(map);
							userVinValidationAttrMap.put(attrbid, tempList);
						}
						isPresentIncache=true;
						validity=	doCustomValidation(apikey,dataStoreKey, service,  validity, validationClass, new HashMap<String, Object>(), params, value, env);
						if(!validity)
						{
							return validity;
						}
					}
				}
				
			}
		}
		return validity;
	}
}

