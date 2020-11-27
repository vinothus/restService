package com.vin.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.vin.rest.repository.EmployeeRepositaryImpl;

import vin.rest.common.Constant;
@Component
public class ParamsValidator implements ConstraintValidator<ParamMapValidator,Map<String,String>>{

	
	Logger log = Logger.getLogger(ParamsValidator.class.getName());
	
	public static Map<String,List<Map<String, Object>>> userServiceTableMap= new ConcurrentHashMap<>();
	public static Map<String,List<Map<String, Object>>> userServiceAttrTableMap= new ConcurrentHashMap<>();
	public static Map<String, String> dsidMap=new ConcurrentHashMap<>();
	@Autowired
	EmployeeRepositaryImpl employeeRepositaryImpl;
	
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
		value.remove(Constant.VIN_SERVICE);
		value.remove(Constant.VIN_SERVICE_DS);
		value.remove(Constant.VIN_SERVICE_APIKEY);
		String dsid = null;
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
            String paramAttrbvalue="";
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			validationClass = String.valueOf(map.get("attrCusValidation"));
			attrminlength = String.valueOf(map.get("attrminLength"));
			attrmaxlength = String.valueOf(map.get("attrMaxLength"));
			attrregxvalidation = String.valueOf(map.get("attrRegXvalidation"));
			attrismandatory = String.valueOf(map.get("attrIsMandatory"));
			attrname= String.valueOf(map.get("attrName"));
			valid=mandatoryValidation(attrname, value,attrismandatory);
			paramAttrbvalue=value.get(attrname);
			if(!valid) {
				staticbool=false;
				 context.disableDefaultConstraintViolation();
				 context.buildConstraintViolationWithTemplate( attrname + " is a Mandatory Attribute "  ).addConstraintViolation();
		        }
			for (Map.Entry<String, String> entry : value.entrySet()) {
				if(entry.getKey().equalsIgnoreCase(attrname))
				{valid = doValidation(context, valid, validationClass, attrminlength, attrmaxlength, attrregxvalidation,
						map, entry);
				if(!valid) {
					staticbool=false;
				}
				}

			}
			if(!minLength(paramAttrbvalue, attrminlength))
			{
				 context.disableDefaultConstraintViolation();
				 context.buildConstraintViolationWithTemplate( paramAttrbvalue+ " Should Contain Atleast "+attrminlength+" Characters "  ).addConstraintViolation();
				 valid=false;	
				 staticbool=false;
			}
			//valid=doCustomValidation(context, valid, validationClass, map, value,paramAttrbvalue);
			//if(!valid) {
			//	staticbool=false;
			//}
		}
		 if(!staticbool) {
			 context.disableDefaultConstraintViolation();
			 context.buildConstraintViolationWithTemplate( "In valida Parameters"  ).addConstraintViolation();
			 return staticbool;
	        }
}
		}
		if(valid&&serviceName.equals("service attr")&&dataStoreKey.equalsIgnoreCase("system"))
		{
			clearCache();
		}
		log.info("Validator :" + value);
		return true;
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

	public boolean doCustomValidation(ConstraintValidatorContext context, boolean valid, String validationClass,
			 Map<String, Object> Attrbmap,
			Map<String, String> mapofVal,String valueFmParam)
	{
		
		String attrcusvalidation =(String) Attrbmap.get("attrcusvalidation");

			try {
				if (validationClass != null) {
					if(attrcusvalidation.equalsIgnoreCase("yes")||attrcusvalidation.equalsIgnoreCase("true")||attrcusvalidation.equalsIgnoreCase("1")) {
					com.vin.validatior.Validator<String> validator = (com.vin.validatior.Validator) Class
							.forName(validationClass).newInstance();
					boolean valididy = validator.isValid(valueFmParam);
					log.info(validationClass);
					if (!valididy) {
						 context.disableDefaultConstraintViolation();
						 context.buildConstraintViolationWithTemplate( validator.getErrorMsg().replace("$",valueFmParam)  ).addConstraintViolation();
						 
						 valid=false;
					}
				}}
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
		dsidMap=new ConcurrentHashMap<>();
	}
}

