package com.vin.processor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Logger;
import java.sql.*;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vin.rest.repository.EmployeeRepositaryImpl;

@Component
public class DateProvider implements ProcessParam {

    @Override
	public String doPreProcess(String... value) throws JsonParseException, JsonMappingException, IOException {
		 String paramoValue=value[0];
		 String apiKey=value[1];
		 String datasourceKey=value[2];
		 String serviceName=value[3];
		 String attrbMap=value[4];
		 String mapofVal=value[5];
         String env=value[6]; 
          String date=new Date(new java.util.Date().getTime()).toString();
    return date;
        }
@Override
	public String doPostProcess(String... value) throws JsonParseException, JsonMappingException, IOException {
		 String paramoValue=value[0];
		 String apiKey=value[1];
		 String datasourceKey=value[2];
		 String serviceName=value[3];
		 String attrbMap=value[4];
		 String mapofVal=value[5];
         String env=value[6];
        String time=new java.sql.Time(new java.util.Date().getTime()).toString();
    return time;
}

}
