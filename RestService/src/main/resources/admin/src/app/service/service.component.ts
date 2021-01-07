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
	selector: 'app-service',
	templateUrl: './service.component.html',
	styleUrls: ['./service.component.css']
})
export class ServiceComponent implements OnInit, AfterViewInit {
	//displayedColumns: string[];// = ['position', 'name', 'weight', 'symbol'];
	displayedColumns: string[];//=["id","uid","serviceid","attrid","name","classname","paramclassname"];
	componentName: string = 'Multi Service Configuration';
	model = {};
	dataSource: any;
	data: any;
	primaryKey: string = 'id';
	isLoading = true;
	serviceName: string = 'service';
	props: any;
	multidata: any;
	@ViewChild(MatPaginator) paginator: MatPaginator;
	@ViewChild(MatSort) sort: MatSort;
	// @ViewChild(MatTableDataSource,{static:true}) table: MatTableDataSource<any>;

	UIForm: FormGroup;
	loading = false;
	submitted = false;
	ngAfterViewInit() {


	}
	constructor(public dialog: MatDialog, public formBuilder: FormBuilder, public authService: AuthService) {

		this.reinit();
		let map = new Map<string, string>();
		map.set('processorClassName', 'yekeulav');
		console.log("AppConstants.propertyMap.size:"+AppConstants.propertyMap.size);
		this.props=this.authService.getPropertyMap();
		
		console.log("AppConstants.propertyMap.size:"+AppConstants.propertyMap.size);
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
		//map.set('uid',uid);
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
			//this.columnClick('serviceid');
			//this.columnClick('attrid');
			this.dataSource.paginator = this.paginator;
			this.dataSource.sort = this.sort;
			this.isLoading = false;
			// this.data[0].name='vinoth';
		});

	}
	isequalsAction(colName: string) {

		//console.log('colName :'+colName);
		//console.log('colName :'+(colName=='action'));
		if (colName == 'action') { return false; }
		else { return true; }
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
		validation["service"] = obj.servicename;
		validation["dsid"] = obj.dsid;
		let width = '850px';

		let map = new Map<string, string>();

		map.set('serviceid', obj.id);
		this.authService.getdata('service attr', map).subscribe((attributeData) => {
			var dskeyName = AppConstants.dsipMap.get(obj.dsid);
			validation["attribMap"] = attributeData['service attr'];
			if (dskeyName == null) {
				map.set('id', obj.dsid);
				this.authService.getdata('datastore', map).subscribe((dsidres) => {
					validation["dsname"] = dsidres['datastore'][0].name;
					console.log(dsidres['datastore'][0].name);
					AppConstants.dsipMap.set(obj.dsid, dsidres['datastore'][0].name);
					this.isLoading = false;
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

				}, err => {
					this.isLoading = false;
				});
			} else {
				validation["dsname"] = dskeyName;
				this.isLoading = false;
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

			}

		}, err => {
			this.isLoading = false;
		});

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
		map.set('servicename',this.serviceName);
		this.authService.getAllMultidata(this.props.get(AppConstants.systemUser), this.props.get(AppConstants.systemDatasource),  this.props.get(AppConstants.multiService), this.props.get(AppConstants.multiservicename), this.props.get(AppConstants.getMultipleAllData), map).subscribe((res) => {
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
						var data=result.data;
						console.log(result.data);
						for (var key in data) {
         if(data[key]!=null&&data[key]!=''){
			 map[key]=data[key];
				}}
		
						 this.addRowData(map);
					}
				}
			});
		this.isLoading = false;
		});


	}


}


