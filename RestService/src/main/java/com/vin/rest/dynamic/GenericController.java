package com.vin.rest.dynamic;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.vin.processor.VinRestProcessor;
import com.vin.rest.exception.GlobalExceptionHandler;
import com.vin.rest.exception.RecordNotFoundException;
import com.vin.rest.exception.ServiceNotFoundException;
import com.vin.rest.model.EmployeeEntity;
import com.vin.rest.repository.EmployeeRepositaryImpl;
import com.vin.rest.service.EmployeeService;
import com.vin.validation.ParamMapValidator;
import com.vin.validation.ParamsValidator;
import com.vin.validation.VinMap;
import javax.servlet.http.HttpServletResponse;
import vin.rest.common.Constant;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
@Component
@Validated
@Controller
public class GenericController {

	static Logger log = Logger.getLogger(GenericController.class.getName());
	@Autowired
	@LazyInit
	EmployeeService service;
	@Autowired
	EmployeeRepositaryImpl employeeRepositaryImpl;
	@Autowired
	ParamsValidator paramsValidator;
	
	@Autowired
	private Environment env;
	@Autowired
	Validator validator;
	@Autowired
	VinRestProcessor vinRestProcessor;
	
	private String controllerPath = null;

	public String getControllerPath() {
		return controllerPath;
	}

	public void setControllerPath(String controllerPath) {
		this.controllerPath = controllerPath;
	}

	public GenericController getObject() {

		return new GenericController();
	}
	@MethodName(MethodName="addData")
	@CrossOrigin
	public ResponseEntity<Object> addData(@PathVariable("service") String service,
			@RequestBody String params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken,@RequestHeader Map<String,String> headers) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> jsonMap = new HashMap<>();
		jsonMap = mapper.readValue(params, new TypeReference<Map<String, String>>() {
		}); // converts JSON to Map
		jsonMap.put(Constant.REST_METHOD, Constant.POST_METHOD);
		doValidation(service, apiKey, dataStoreKey, jsonMap);
		jsonMap=doPreProcess(service, apiKey, dataStoreKey, jsonMap);
		Map<String, Object> returnData=employeeRepositaryImpl.insertData(service, jsonMap, apiKey, dataStoreKey,passToken);
		returnData=doPostProcess(service, apiKey, dataStoreKey, returnData);
		String requestAccept = headers.get("accept");
		ObjectNode  rootNode=mapper.createObjectNode();
		ObjectNode childListNodes = mapper.createObjectNode();
		for (Entry<String, Object> entry : returnData.entrySet())  
		   {
			   String value = null;
			   if( entry.getValue()!=null)
			   {
				   value=  String.valueOf( entry.getValue());
			   }
			   childListNodes.put(entry.getKey(),value);  
		   }
		if(requestAccept==null) {
			rootNode.set(service, childListNodes);
			return new ResponseEntity<Object>(rootNode, new HttpHeaders(), HttpStatus.OK);
		}
		else if (requestAccept.contains("json")) {
			rootNode.set(service, childListNodes);
			return new ResponseEntity<Object>(rootNode, new HttpHeaders(), HttpStatus.OK);
		} else if (requestAccept.contains("xml")) {
			XmlMapper xmlMapper = new XmlMapper();
			rootNode.set(service.replace(" ","_"), childListNodes);
			String jsonStr = xmlMapper.enable(SerializationFeature.WRAP_ROOT_VALUE).writer().withRootName(service.replace(" ","_") + "s")
					.writeValueAsString(rootNode);
			return new ResponseEntity<Object>(jsonStr, new HttpHeaders(), HttpStatus.OK);
		}
		rootNode.set(service, childListNodes);
	 return new ResponseEntity<Object>(rootNode,
				new HttpHeaders(), HttpStatus.OK); 
	}

	private void doValidation(String service, String apiKey, String dataStoreKey, Map<String, String> jsonMap) {
		jsonMap.put(Constant.VIN_SERVICE, service);
		jsonMap.put(Constant.VIN_SERVICE_DS, dataStoreKey);
		jsonMap.put(Constant.VIN_SERVICE_APIKEY, apiKey);
		Set<ConstraintViolation<HashMap>> constraintViolation = validator
				.validate(new VinMap<String, String>(jsonMap));
		if (!constraintViolation.isEmpty()) {
			throw new ConstraintViolationException(constraintViolation);
		}
	}
	
	
	private Map<String, String> doPreProcess(String service, String apiKey, String dataStoreKey, Map<String, String> jsonMap) {
		
		jsonMap=vinRestProcessor.doPreProcess(jsonMap, apiKey,dataStoreKey,service);
		return jsonMap; 
	}
	
	private Map<String, Object> doPostProcess(String service, String apiKey, String dataStoreKey, Map<String, Object> jsonMap) {
		 
		jsonMap=vinRestProcessor.doPostProcess(jsonMap, apiKey,dataStoreKey,service);
		return jsonMap; 
	}
	
	private Map<String, String> doPostProcessStr(String service, String apiKey, String dataStoreKey, Map<String, String> jsonMap) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		 
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap = mapper.readValue(mapper.writeValueAsString(jsonMap), new TypeReference<Map<String, Object>>() {
		});
		
		
	String paramMapStr = null;
	try {
		paramMapStr = vinRestProcessor.dopostProcessStr(apiKey, dataStoreKey, service,jsonMap.get("processorClassName"),  paramMap, jsonMap.get("value"), env);
	} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e) {
		e.printStackTrace();
	}
	//vinRestProcessor.dopostProcess(paramMap, apiKey,dataStoreKey,service);
	try {	jsonMap = mapper.readValue(paramMapStr, new TypeReference<Map<String, String>>() {
		});}catch(Exception e) {
			jsonMap=new HashMap<>();
			jsonMap.put("processedValue", paramMapStr);
		}
		return jsonMap; 
	}
	
	private Map<String, String> doPreProcessStr(String service, String apiKey, String dataStoreKey, Map<String, String> jsonMap) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		 
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap = mapper.readValue(mapper.writeValueAsString(jsonMap), new TypeReference<Map<String, Object>>() {
		});
		
		
	String paramMapStr = null;
	try {
		Map<String,Object> attrMap=new HashMap();
		paramMapStr = vinRestProcessor.dopreProcessStr(apiKey, dataStoreKey, service,jsonMap.get("processorClassName"),  paramMap, jsonMap.get("value"), env);
	} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e) {
		e.printStackTrace();
	}
	//vinRestProcessor.dopostProcess(paramMap, apiKey,dataStoreKey,service);
	try {	jsonMap = mapper.readValue(paramMapStr, new TypeReference<Map<String, String>>() {
	});}catch(Exception e) {
		jsonMap=new HashMap<>();
		jsonMap.put("processedValue", paramMapStr);
	}
	return jsonMap; 
	}
	
	
	
	@MethodName(MethodName="updateData")
	@CrossOrigin
	public ResponseEntity<Object> updateData(@PathVariable("service") String service,
			@RequestBody String params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken,@RequestHeader Map<String,String> headers) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> jsonMap = new HashMap<>();
		
		jsonMap = mapper.readValue(params, new TypeReference<Map<String, String>>() {
		}); // converts JSON to Map
		jsonMap.put(Constant.REST_METHOD, Constant.PUT_METHOD);
		doValidation(service, apiKey, dataStoreKey, jsonMap);
		jsonMap=doPreProcess(service, apiKey, dataStoreKey, jsonMap);
		Map<String, Object> returnData=employeeRepositaryImpl.updateData(service, jsonMap, apiKey, dataStoreKey,passToken);
		returnData=doPostProcess(service, apiKey, dataStoreKey, returnData);
		String requestAccept = headers.get("accept");
		ObjectNode  rootNode=mapper.createObjectNode();
		ObjectNode childListNodes = mapper.createObjectNode();
		for (Entry<String, Object> entry : returnData.entrySet())  
		   {
			   String value = null;
			   if( entry.getValue()!=null)
			   {
				   value=  String.valueOf( entry.getValue());
			   }
			   childListNodes.put(entry.getKey(),value);  
		   }
		if(requestAccept==null) {
			rootNode.set(service, childListNodes);
			return new ResponseEntity<Object>(rootNode, new HttpHeaders(), HttpStatus.OK);
		}
		if (requestAccept.contains("json")) {
			rootNode.set(service, childListNodes);
			return new ResponseEntity<Object>(rootNode, new HttpHeaders(), HttpStatus.OK);
		} else if (requestAccept.contains("xml")) {
			XmlMapper xmlMapper = new XmlMapper();
			rootNode.set(service.replace(" ","_"), childListNodes);
			String jsonStr = xmlMapper.enable(SerializationFeature.WRAP_ROOT_VALUE).writer().withRootName(service.replace(" ","_") + "s")
					.writeValueAsString(rootNode);
			return new ResponseEntity<Object>(jsonStr, new HttpHeaders(), HttpStatus.OK);
		}
		rootNode.set(service, childListNodes);
	 return new ResponseEntity<Object>(rootNode,
				new HttpHeaders(), HttpStatus.OK);
	}
	
	@MethodName(MethodName="getDatum")
	@CrossOrigin
	public ResponseEntity<Object> getDatum(@PathVariable("service") String service,
			@RequestParam   Map<String, String> params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken,@RequestHeader Map<String,String> headers) throws Exception    {
		params.put(Constant.REST_METHOD, Constant.GET_ALL_METHOD);
		System.out.println(headers);
		doValidation(service, apiKey, dataStoreKey, params);
		params=doPreProcess(service, apiKey, dataStoreKey, params);
		List<Map<String, Object>> returnObj=employeeRepositaryImpl.getDataForParams(service, params, apiKey, dataStoreKey,passToken);
		for (Iterator iterator = returnObj.iterator(); iterator.hasNext();) {
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			doPostProcess(service, apiKey, dataStoreKey, map);
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		ObjectNode  rootNode=mapper.createObjectNode();
		ArrayNode childListNodes = mapper.createArrayNode();
	for (Iterator<Map<String, Object>> iterator = returnObj.iterator(); iterator.hasNext();) {
		Map<String, Object> map = (Map<String, Object>) iterator.next();
		ObjectNode childNodes = mapper.createObjectNode();
		
		   for (Entry<String, Object> entry : map.entrySet())  
		   {
			   String value = null;
			   if( entry.getValue()!=null)
			   {
				   value=  String.valueOf( entry.getValue());
			   }
			   childNodes.put(entry.getKey(),value);  
		   }
		   childListNodes.add(childNodes);
	}
	
	

	String requestAccept = headers.get("accept");
	if (requestAccept==null)
	{
		rootNode.putArray(service).addAll( childListNodes);
	return new ResponseEntity<Object>(rootNode, new HttpHeaders(), HttpStatus.OK);
	}
	else if (requestAccept.contains("json")) {
		rootNode.putArray(service).addAll( childListNodes);
		return new ResponseEntity<Object>(rootNode, new HttpHeaders(), HttpStatus.OK);
	} else if (requestAccept.contains("xml")) {
		XmlMapper xmlMapper = new XmlMapper();
		rootNode.putArray(service.replace(" ","_")).addAll( childListNodes);
		String jsonStr = xmlMapper.enable(SerializationFeature.WRAP_ROOT_VALUE).writer().withRootName(service.replace(" ","_") + "s")
				.writeValueAsString(rootNode);
		return new ResponseEntity<Object>(jsonStr, new HttpHeaders(), HttpStatus.OK);
	}
	rootNode.putArray(service).addAll( childListNodes);
 return new ResponseEntity<Object>(rootNode,
			new HttpHeaders(), HttpStatus.OK); 
		
	}
	@MethodName(MethodName="getData")
	@CrossOrigin
	public ResponseEntity< Object> getData(@PathVariable("service") String service,
			@PathVariable("uniquekey") @Valid @NotNull String uniquekey,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken,@RequestHeader Map<String,String> headers) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put(Constant.UNIQUEKEY, uniquekey);
		params.put(Constant.REST_METHOD, Constant.GET_METHOD);
		doValidation(service, apiKey, dataStoreKey, params);
		params=doPreProcess(service, apiKey, dataStoreKey, params);	 
		Map<String, Object> returnData=employeeRepositaryImpl.getData(service, uniquekey, apiKey, dataStoreKey,passToken);
		returnData=doPostProcess(service, apiKey, dataStoreKey, returnData);
		ObjectMapper mapper = new ObjectMapper();
		String requestAccept = headers.get("accept");
		ObjectNode  rootNode=mapper.createObjectNode();
		ObjectNode childListNodes = mapper.createObjectNode();
		for (Entry<String, Object> entry : returnData.entrySet())  
		   {
			   String value = null;
			   if( entry.getValue()!=null)
			   {
				   value=  String.valueOf( entry.getValue());
			   }
			   childListNodes.put(entry.getKey(),value);  
		   }
		if (requestAccept==null) {
			rootNode.set(service, childListNodes);
			return new ResponseEntity<Object>(rootNode, new HttpHeaders(), HttpStatus.OK);
		}
		else if (requestAccept.contains("json")) {
			rootNode.set(service, childListNodes);
			return new ResponseEntity<Object>(rootNode, new HttpHeaders(), HttpStatus.OK);
		} else if (requestAccept.contains("xml")) {
			XmlMapper xmlMapper = new XmlMapper();
			rootNode.set(service.replace(" ","_"), childListNodes);
			String jsonStr = xmlMapper.enable(SerializationFeature.WRAP_ROOT_VALUE).writer().withRootName(service.replace(" ","_") + "s")
					.writeValueAsString(rootNode);
			return new ResponseEntity<Object>(jsonStr, new HttpHeaders(), HttpStatus.OK);
		}
		rootNode.set(service, childListNodes);
	 return new ResponseEntity<Object>(rootNode,
				new HttpHeaders(), HttpStatus.OK);
	}
@MethodName(MethodName="delData")
@CrossOrigin
	public ResponseEntity< Object> delData(@PathVariable("service") String service,
			@PathVariable("uniquekey") @Valid @NotNull String uniquekey,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken,@RequestHeader Map<String,String> headers) throws Exception {
	Map<String, String> params = new HashMap<>();	
	params.put(Constant.UNIQUEKEY, uniquekey);
	params.put(Constant.REST_METHOD, Constant.DELETE_METHOD);
	doValidation(service, apiKey, dataStoreKey, params);
	Map<String, Object> returnData=employeeRepositaryImpl.deleteData(service, uniquekey, apiKey, dataStoreKey,passToken);
	returnData=doPostProcess(service, apiKey, dataStoreKey, returnData);
	ObjectMapper mapper = new ObjectMapper();
	String requestAccept = headers.get("accept");
	ObjectNode  rootNode=mapper.createObjectNode();
	ObjectNode childListNodes = mapper.createObjectNode();
	for (Entry<String, Object> entry : returnData.entrySet())  
	   {
		   String value = null;
		   if( entry.getValue()!=null)
		   {
			   value=  String.valueOf( entry.getValue());
		   }
		   childListNodes.put(entry.getKey(),value);  
	   }
	if (requestAccept==null) {
	rootNode.set(service, childListNodes);
	return new ResponseEntity<Object>(rootNode, new HttpHeaders(), HttpStatus.OK);
	}
	else if (requestAccept.contains("json")) {
		rootNode.set(service, childListNodes);
		return new ResponseEntity<Object>(rootNode, new HttpHeaders(), HttpStatus.OK);
	} else if (requestAccept.contains("xml")) {
		XmlMapper xmlMapper = new XmlMapper();
		rootNode.set(service.replace(" ","_"), childListNodes);
		String jsonStr = xmlMapper.enable(SerializationFeature.WRAP_ROOT_VALUE).writer().withRootName(service.replace(" ","_") + "s")
				.writeValueAsString(rootNode);
		return new ResponseEntity<Object>(jsonStr, new HttpHeaders(), HttpStatus.OK);
	}
	rootNode.set(service, childListNodes);
 return new ResponseEntity<Object>(rootNode,
			new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<String>	refreshMataData(@PathVariable("service") String service,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey) throws RecordNotFoundException
	{
		
		return new ResponseEntity<String>(employeeRepositaryImpl.refreshMataData(service,apiKey,dataStoreKey),new HttpHeaders(), HttpStatus.OK)	;
	}
	@MethodName(MethodName="clearCache")
	@CrossOrigin
	public ResponseEntity<String>	 clearCache(@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey)
	{
		
		return new ResponseEntity<String>(employeeRepositaryImpl.clearCache(),new HttpHeaders(), HttpStatus.OK)	;
	}
	
	@MethodName(MethodName="home")
	    public String home() {
	        System.out.println("Going home...");
	        return "index";
	    }

		public static String getMethodName(String Name) {

			Method methods[] = GenericController.class.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				MethodName methodAnno = method.getDeclaredAnnotation(MethodName.class);
				if (methodAnno != null) {
					if (methodAnno.MethodName().equalsIgnoreCase(Name)) {
						return method.getName();
					}
				}
			}

			return null;
		}
		@MethodName(MethodName="initGC")
		@CrossOrigin
		public ResponseEntity<String>	initGC(@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader Map<String,String> headers)
		{
			employeeRepositaryImpl.init();
			return new ResponseEntity<String>("initilized",new HttpHeaders(), HttpStatus.OK)	;
		}
		
		@MethodName(MethodName="validateDatum")
		@CrossOrigin
		public ResponseEntity<String> validateDatum(@PathVariable("service") String service,
				@RequestParam   Map<String, String> params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken) throws Exception {
			doValidation(service, apiKey, dataStoreKey, params);
			return  new ResponseEntity<String>("true" ,new HttpHeaders(), HttpStatus.OK)	;
		}
		@MethodName(MethodName="preProcessData")
		@CrossOrigin
		public ResponseEntity<Map<String, String>> preProcessData(@PathVariable("service") String service,
				@RequestParam   Map<String, String> params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken) throws Exception {
			params=doPreProcessStr(service, apiKey, dataStoreKey, params);
			return  new ResponseEntity<Map<String, String>>(params ,new HttpHeaders(), HttpStatus.OK)	;
		}
		@MethodName(MethodName="postProcessData")
		@CrossOrigin
		public ResponseEntity<Map<String, String>> postProcessData(@PathVariable("service") String service,
				@RequestParam   Map<String, String> params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken) throws Exception {
			params=doPostProcessStr(service, apiKey, dataStoreKey, params);
			return  new ResponseEntity<Map<String, String>>(params ,new HttpHeaders(), HttpStatus.OK)	;
		}
		
		@MethodName(MethodName="validateData")
		@CrossOrigin
		public ResponseEntity<String> validateData(@PathVariable("service") String service,
				@RequestParam   Map<String, String> params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken) throws Exception {
			String validatorName=params.get("validatorName");
			String value=params.get("value");
			boolean validity=paramsValidator.validateSingleAttr(service, apiKey, dataStoreKey, params,validatorName, value);
			return  new ResponseEntity<String>(String.valueOf(validity) ,new HttpHeaders(), HttpStatus.OK)	;
        }
        

        @MethodName(MethodName="getDatumStream")
	@CrossOrigin
	public void getDatumStream(final HttpServletResponse response,@PathVariable("service") String service,
			@RequestParam   Map<String, String> params,@PathVariable("apiKey") String apiKey,@PathVariable("dataStoreKey") String dataStoreKey,@RequestHeader(value="passToken", defaultValue = "none") String passToken,@RequestHeader Map<String,String> headers) throws Exception    {
		params.put(Constant.REST_METHOD, Constant.GET_ALL_METHOD);
		System.out.println(headers);
		doValidation(service, apiKey, dataStoreKey, params);
        params=doPreProcess(service, apiKey, dataStoreKey, params);
        	String requestAccept = headers.get("accept");
		employeeRepositaryImpl.getDataForParamsStream(response,service, params, apiKey, dataStoreKey,passToken,requestAccept);
	 	}
		
}
