import { Component, OnInit } from '@angular/core';
import { AfterViewInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { AuthService } from "../auth/auth-service.service";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatDialog } from '@angular/material/dialog';
import { DialogBoxComponent } from '../dialog-box/dialog-box.component';
import { AppConstants } from "../app-constants";
@Component({
	selector: 'app-multiservice',
	templateUrl: './multiservice.component.html',
	styleUrls: ['./multiservice.component.css']
})
export class MultiserviceComponent implements OnInit, AfterViewInit {
	//displayedColumns: string[];// = ['position', 'name', 'weight', 'symbol'];
	displayedColumns: string[];//=["id","uid","serviceid","attrid","name","classname","paramclassname"];
	componentName: string = 'Multi Service Configuration';
	model = {};
	dataSource: any;
	data: any;
	primaryKey: string = 'id';
	isLoading = true;
	serviceName: string = 'multi service';
	multidata: any;
	props: any;
	@ViewChild(MatPaginator) paginator: MatPaginator;
	@ViewChild(MatSort) sort: MatSort;
	// @ViewChild(MatTableDataSource,{static:true}) table: MatTableDataSource<any>;

	UIForm: FormGroup;
	loading = false;
	submitted = false;
	ngAfterViewInit() {


	}
	constructor(public dialog: MatDialog, public formBuilder: FormBuilder, public authService: AuthService) {
		this.UIForm = this.formBuilder.group({
			email: ['', Validators.required],
			password: ['', Validators.required]
		})

		let map = new Map<string, string>();
		let uid = localStorage.getItem('uid');
		map.set('uid', uid);
		this.authService.getdata(this.serviceName, map).subscribe((res) => {
			this.data = res[this.serviceName];

			console.log(this.displayedColumns);
			this.displayedColumns = [];

			for (var key in this.data[0]) {

				this.displayedColumns.push(key);
			}
			this.displayedColumns.push('action');
			console.log(this.displayedColumns);
			this.dataSource = new MatTableDataSource<any>(this.data);
			//this.columnClick('uid');
			this.columnClick('serviceid');
			//this.columnClick('attrid');
			// this.data[0].name='vinoth';
			this.dataSource.paginator = this.paginator;
			this.dataSource.sort = this.sort;
			this.isLoading = false;
		});


		this.props = this.authService.getPropertyMap();

	}

	openDialog(action, obj) {
		obj.action = action;
		let width = '700px';
		if (action == 'Delete') {
			width = '400px';
		}
		obj.componentName = this.componentName;
		obj.primaryKey = this.primaryKey;
		const dialogRef = this.dialog.open(DialogBoxComponent, {
			width: width,
			data: obj
		});

		dialogRef.afterClosed().subscribe(result => {
			if (result != undefined) {
				if (result.event == 'Add') {
					this.addRowData(result.data);
				} else if (result.event == 'Update') {
					this.updateRowData(result.data);
				} else if (result.event == 'Delete') {
					this.deleteRowData(result.data);
				}
			}
		});
	}

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.dataSource.filter = filterValue.trim().toLowerCase();
	}
	ngOnInit(): void {

	}

	columnClick(colName: string) {
		const colIndex = this.displayedColumns.findIndex(col => col === colName);

		if (colIndex > 0) {
			// column is currently shown in the table, so we remove it
			this.displayedColumns.splice(colIndex, 1);
		} else {
			// column is not in the table, so we add it
			this.displayedColumns.push(colName);
		}
	}

	get f() { return this.UIForm.controls; }

	ProcessorSubmit() {


	}

	addRowData(row_obj) {
		this.isLoading = true;
		delete row_obj[this.primaryKey];
		const promise = this.authService.addData(this.serviceName, row_obj);
		promise.then((data) => {
			console.log("Promise resolved with: " + JSON.stringify(data));
			this.reinit();

		}).catch((error) => {
			console.log("Promise rejected with " + JSON.stringify(error));
			this.isLoading = false;

		});


	}
	updateRowData(row_obj) {
		this.isLoading = true;
		const promise = this.authService.updateData(this.serviceName, row_obj);
		promise.then((data) => {
			console.log("Promise resolved with: " + JSON.stringify(data));
			this.reinit();

		}).catch((error) => {
			console.log("Promise rejected with " + JSON.stringify(error));
			this.isLoading = false;

		});


	}
	deleteRowData(row_obj) {
		this.isLoading = true;
		const promise = this.authService.deleteUniqueData(this.serviceName, row_obj[this.primaryKey]);
		promise.then((data) => {
			console.log("Promise resolved with: " + JSON.stringify(data));
			this.reinit();
			return data;
		}).catch((error) => {
			console.log("Promise rejected with " + JSON.stringify(error));
			this.isLoading = false;

		});

	}
	reinit() {

		let map = new Map<string, string>();
		let uid = localStorage.getItem('uid');
		map.set('uid', uid);
		this.authService.getdata(this.serviceName, map).subscribe((res) => {
			this.data = res[this.serviceName];

			console.log(this.displayedColumns);
			this.displayedColumns = [];

			for (var key in this.data[0]) {

				this.displayedColumns.push(key);
			}
			this.displayedColumns.push('action');
			this.dataSource = new MatTableDataSource<any>(this.data);
			//this.columnClick('uid');
			this.columnClick('serviceid');
			//this.columnClick('attrid');
			this.dataSource.paginator = this.paginator;
			this.dataSource.sort = this.sort;
			this.isLoading = false;
			// this.data[0].name='vinoth';
		});

	}

	isequalsAction(colName: string) {

		console.log('colName :' + colName);
		console.log('colName :' + (colName == 'action'));
		if (colName == 'action') { return false; }
		else { return true; }
	}


	addRec() {
		let validation = {};
		validation["action"] = 'AddRec';
		validation["service"] = this.componentName;
		validation["componentName"] = this.componentName;
		validation["serviceName"] = this.serviceName;
		validation["method"] = AppConstants.POST_METHOD;
		let width = '700px';
		this.isLoading = true;
		let map = new Map<string, string>();
		map.set('servicename', this.serviceName);
		this.authService.getAllMultidata(this.props.get(AppConstants.systemUser), this.props.get(AppConstants.systemDatasource), this.props.get(AppConstants.multiService), this.props.get(AppConstants.multiservicename), this.props.get(AppConstants.getMultipleAllData), map).subscribe((res) => {
			this.multidata = res;
			validation["multidata"] = this.multidata;
			const dialogRef = this.dialog.open(DialogBoxComponent, {
				width: width,
				data: validation
			});

			dialogRef.afterClosed().subscribe(result => {
				if (result != undefined) {
					if (result.event == 'AddRec') {
						let map = {};
						var data = result.data;
						console.log(result.data);
						for (var key in data) {
							if (data[key] != null && data[key] != '') {
								map[key] = data[key];
							}
						}

						this.addRowData(map);
					}
				}
			});
			this.isLoading = false;
		});


	}

	openValidateDialog(action, obj) {
		this.isLoading = true;
		obj.action = action;
		console.log('action :' + action);
		let validation = {};
		validation["action"] = action;
		//validation["validatorName"]=obj.name;
		validation["ParamValue"] = '';
		validation["componentName"] = this.componentName;
		validation["props"] = this.props;
		validation["service"] = obj.multiservicename;
		validation["dsid"] = obj.priority;
		let width = '850px';
		let map = new Map<string, string>();
		var serviceInCondition = '';
		var dsidInCondition = '';
		let multiservices = [];
		var inputMap = {};
		// aplicable for full load data table not for server side paging
		for (var i = 0; i < this.data.length; i++) {
			if (this.data[i]['multiservicename'] == obj.multiservicename) {

				serviceInCondition = serviceInCondition + this.data[i]['serviceid'];
				serviceInCondition = serviceInCondition + ':';
				dsidInCondition = dsidInCondition + this.data[i]['dsid'] + ':';
				multiservices.push(this.data[i]);


				var params = this.data[i]['relationwithparam'].split(',');
				for (var j = 0; j < params.length; j++) {
					if (params[j].split('.').length == 2) {
						inputMap[params[j].split('.')[0]] = params[j].split('.')[1];
					}
				}
			}

		}
		console.log(inputMap);
		multiservices.sort(function(a, b) { return a['priority'] - b['priority'] });
		 
		serviceInCondition = serviceInCondition.substring(0, serviceInCondition.length - 1);
		dsidInCondition = dsidInCondition.substring(0, dsidInCondition.length - 1);
		map.set(AppConstants.IN_CONDITION + 'id', serviceInCondition);
		var sampleJson = {};
		var ExampleJsonFormat = {};
		var datasource = {};
		let dsmap = new Map<string, string>();
		
		this.authService.getAllMultidata(this.props.get(AppConstants.systemUser), this.props.get(AppConstants.systemDatasource), this.props.get(AppConstants.multiService), this.props.get(AppConstants.multiservicename), this.props.get(AppConstants.getMultipleAllData), map).subscribe((res) => {
			 dsidInCondition='';
		  for (var j = 0; j <res[0]['service'].length; j++) {
			dsidInCondition=dsidInCondition+res[0]['service'][j]['dsid']+':';
		}
		dsidInCondition = dsidInCondition.substring(0, dsidInCondition.length - 1);
		dsmap.set(AppConstants.IN_CONDITION + 'id', dsidInCondition);
			this.authService.getdata('datastore', dsmap).subscribe((dsidres) => {
			console.log(dsidres);
			for(var keys in dsidres['datastore'])	
			{
				 
				datasource[dsidres['datastore'][keys]['id']]=dsidres['datastore'][keys]['name'];
			}
			 
				for (var i = 0; i <multiservices.length; i++) {
					var serviceId=multiservices[i]['serviceid'];
					  for (var j = 0; j <res[0]['service'].length; j++) {
						if(res[0]['service'][j]['id']==serviceId)
						{
							var params = multiservices[i]['relationwithparam'].split(',');	
							var isPrimary=false;
							var serviceName='';
							for (var k = 0; k < params.length; k++) {
								if (params[k].split('.').length == 2) {
									isPrimary=true;
								}
								else if(params[k].split('.').length == 3){
									isPrimary=false;
									serviceName=params[k].split('.')[0];
									break;
								}
								}
								console.log(isPrimary);
								console.log(serviceName);
								if(isPrimary)
								{
									var jsontoEx={};
									var jsontoModelEx={};
								console.log(res[0]['service'][j]['service attr']);
								for(var key in res[0]['service'][j]['service attr']){
									if(res[0]['service'][j]['service attr'][key]['attrname'])
									{
										if(res[0]['service'][j]['service attr'][key]['coltype'].includes('int'))
										{
											jsontoEx[res[0]['service'][j]['service attr'][key]['attrname']]=0;
											jsontoModelEx[res[0]['service'][j]['service attr'][key]['attrname']]='intiger';
											}
										else
										{
										jsontoEx[res[0]['service'][j]['service attr'][key]['attrname']]=res[0]['service'][j]['service attr'][key]['attrname'];
										if(res[0]['service'][j]['service attr'][key]['coltype'].includes('char')){
											jsontoModelEx[res[0]['service'][j]['service attr'][key]['attrname']]='String';
										}
										else{
											jsontoModelEx[res[0]['service'][j]['service attr'][key]['attrname']]=res[0]['service'][j]['service attr'][key]['coltype'];
											}	
										
										}
										
										}
								}
								console.log("jsontoEx");
								console.log(jsontoEx);
								ExampleJsonFormat[res[0]['service'][j]['servicename']]=jsontoEx;
								sampleJson[res[0]['service'][j]['servicename']]=jsontoModelEx;
								}else
								{
									var jsontoEx={};
									var jsontoModelEx={};
									for(var key in res[0]['service'][j]['service attr']){
									if(res[0]['service'][j]['service attr'][key]['attrname'])
									{
										if(res[0]['service'][j]['service attr'][key]['coltype'].includes('int'))
										{
											jsontoEx[res[0]['service'][j]['service attr'][key]['attrname']]=0;
											jsontoModelEx[res[0]['service'][j]['service attr'][key]['attrname']]='Intiger';
											}
										else
										{
										jsontoEx[res[0]['service'][j]['service attr'][key]['attrname']]=res[0]['service'][j]['service attr'][key]['attrname'];
										if(res[0]['service'][j]['service attr'][key]['coltype'].includes('char'))
										{
										jsontoModelEx[res[0]['service'][j]['service attr'][key]['attrname']]='String';}	
										else
										{
											jsontoModelEx[res[0]['service'][j]['service attr'][key]['attrname']]=res[0]['service'][j]['service attr'][key]['coltype'];}	
										
										}
										
										}
								}
									ExampleJsonFormat[serviceName][res[0]['service'][j]['servicename']]=jsontoEx
									sampleJson[serviceName][res[0]['service'][j]['servicename']]=jsontoModelEx;
								}
							
						}
					}
					}
				console.log(ExampleJsonFormat);
				console.log(sampleJson);
					this.isLoading = false;
					validation['ExampleJsonFormat']=ExampleJsonFormat;
					validation['sampleJson']=sampleJson;
					validation['datasource']=datasource;
					validation['inputMap']=inputMap;
					validation['dsname']=datasource[0];
				const dialogRef = this.dialog.open(DialogBoxComponent, {
						width: width,
						data: validation
					});

					dialogRef.afterClosed().subscribe(result => {
						if (result != undefined) {
							if (result.event == 'Validate') {

							}
						}
					});
				
			});

		});
	}
}


