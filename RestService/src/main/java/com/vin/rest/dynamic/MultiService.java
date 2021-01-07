package com.vin.rest.dynamic;

import java.util.Comparator;

public class MultiService implements Comparator<MultiService> {

	int id;
	String serviceName;
	String dataSourceKey;
	ServiceType serviceType;
	int priproty;
	String relationwithParam;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public ServiceType getServiceType() {
		return serviceType;
	}
	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}
	public int getPriproty() {
		return priproty;
	}
	public void setPriproty(int priproty) {
		this.priproty = priproty;
	}
	public String getRelationwithParam() {
		return relationwithParam;
	}
	public void setRelationwithParam(String relationwithParam) {
		this.relationwithParam = relationwithParam;
	}
	@Override
	public int compare(MultiService o1, MultiService o2) {
		return o1.getPriproty()-o2.getPriproty();
	}
	public String getDataSourceKey() {
		return dataSourceKey;
	}
	public void setDataSourceKey(String dataSourceKey) {
		this.dataSourceKey = dataSourceKey;
	}
	 
	
	
}
