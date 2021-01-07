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
	selector: 'app-datastore',
	templateUrl: './datastore.component.html',
	styleUrls: ['./datastore.component.css']
})
export class DatastoreComponent implements OnInit, AfterViewInit {
	//displayedColumns: string[];// = ['position', 'name', 'weight', 'symbol'];
	displayedColumns: string[];//=["id","uid","serviceid","attrid","name","classname","paramclassname"];
	componentName: string = 'Data Store Configuration';
	model = {};
	dataSource: any;
	data: any;
	primaryKey: string = 'id';
	isLoading = true;
	serviceName: string = 'datastore';
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

		this.reinit();
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

	openValidateDialog(action, obj) {
		obj.action = action;
		let validation = {};
		validation["action"] = action;
		validation["DataStore Name"] = obj.name;
		validation["UserName"] = '';
		validation["Password"] = '';
		validation["componentName"] = 'Data Store Validation';
		let width = '700px';
		const dialogRef = this.dialog.open(DialogBoxComponent, {
			width: width,
			data: validation
		});

		dialogRef.afterClosed().subscribe(result => {
			if (result != undefined) {
				if (result.event == 'Validate') {
					this.validateRowData(result.data);
				}
			}
		});


	}

	validateRowData(row_obj) {
		this.isLoading = true;
		let map = new Map<string, string>();
		map.set('validatorName','connectionValidator');
		map.set('DataStoreName', row_obj["DataStore Name"]);
		map.set('value',btoa( row_obj.UserName+':'+row_obj.Password));
		map.set('uid', localStorage.getItem('access_token'));
        this.authService.getdata('user',map).subscribe((resusr) => {
		console.log(resusr);
		this.authService.validateDatabyApikey(resusr[0].apikey,  row_obj["DataStore Name"], this.serviceName, map).subscribe((res) => {
			console.log(Boolean(res));
			console.log(res);
			let validation = {};
			console.log('res:'+String(res));
			if ((String(res))=='true'||(String(res))=='TRUE'||(String(res))=='True') { validation["message"] = "Validated success fully and connection test was passed "; }
			else { validation["message"] = "Validated success fully and connection test was Failed"; }
			validation["action"] = "Message";
			let width = '500px';

			const dialogRef = this.dialog.open(DialogBoxComponent, {
				width: width,
				data: validation
			});


			this.isLoading = false;
		},
			err => {
				let validation = {};
				validation["action"] = "Message";
				let width = '500px';
				validation["message"] = "Http Error occur ";
				const dialogRef = this.dialog.open(DialogBoxComponent, {
					width: width,
					data: validation
				});
				this.isLoading = false;
			}

		);
		},
		err => {
			
			}
		);


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


