import { Component, Inject, Optional } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators, FormControl, AbstractControl, AsyncValidatorFn, ValidationErrors } from "@angular/forms";
import { AppConstants } from "../app-constants";
import { DOCUMENT } from '@angular/common';
import { HttpClient, HttpHeaders, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { AuthService } from "../auth/auth-service.service";
export interface UsersData {
	name: string;
	id: number;
}

@Component({
	selector: 'app-dialog-box',
	templateUrl: './dialog-box.component.html',
	styleUrls: ['./dialog-box.component.css']
})
export class DialogBoxComponent {
	serviceName:string;
	method:string;
	attrb_data = {};
	getAllsingleForm: FormGroup;
	getsingleForm: FormGroup;
	delsingleForm: FormGroup;
	
	multiPostForm : FormGroup;
	multiPutForm : FormGroup;
	multiGetAllForm : FormGroup;
	multiGetForm : FormGroup;
	multiDeleteForm : FormGroup;
	getAllmultiForm: FormGroup;
	multigetsingleForm:FormGroup;
	delMultisingleForm:FormGroup;
	inputMap={};
	
	submitted = false;
	action: string;
	local_data: any;
	componentName: string;
	UIForm: FormGroup;
	AddRecUIForm: FormGroup;
	message: string;
	primaryKey: any;
	formInput = {};
	formData = {};
	geturl: string;
	getAllurl: string;
	posturl: string;
	puturl: string;
	deleteurl: string;
	inputDisabled: boolean = true;
	executebuttondis: string = 'none';
	getsingleinputDisabled: boolean = true;
	getsingleexecutebuttondis: string = 'none';
	getAllsingleinputDisabled: boolean = true;
	getAllsingleexecutebuttondis: string = 'none';
	puteexecutebuttondis: string = 'none';
	postexecutebuttondis: string = 'none';
	delsingleexecutebuttondis: string = 'none';
	sampleJson: string = '';
	valueJson = {};
	valueJsonStr: string = '';
	myAppName: string;
	prefixurl: string = '';

	putisLoading: boolean = false;
	putcode: string = '';
	putbody: string = '';
	putResLoading: boolean = false;
	putDbuserName: string;
	putDbpassword: string;

	postisLoading: boolean = false;
	postcode: string = '';
	postbody: string = '';
	postResLoading: boolean = false;
	postDbuserName: string;
	postDbpassword: string;

	getisLoading: boolean = false;
	getcode: string = '';
	getbody: string = '';
	getResLoading: boolean = false;
	getdisurl: string;
	getDbuserName: string;
	getDbpassword: string;

	getsingleid: string;

	getAllisLoading: boolean = false;
	getAllcode: string = '';
	getAllbody: string = '';
	getAllResLoading: boolean = false;
	getAlldisurl: string = '';
	getAllDbuserName: string;
	getAllDbpassword: string;

	deleteisLoading: boolean = false;
	deletecode: string = '';
	deletebody: string = '';
	deleteResLoading: boolean = false;
	deldisurl: string;
	delAllDbuserName: string;
	delAllDbpassword: string;


	//addREc
	formaddRecInput = {};
	formaddRecData = {};


   // multi data validate
    multigeturl: string;
	multigetAllurl: string;
	multiposturl: string;
	multiputurl: string;
	multideleteurl: string;
	multiinputDisabled: boolean = true;
	multiexecutebuttondis: string = 'none';
	multigetsingleinputDisabled: boolean = true;
	multigetsingleexecutebuttondis: string = 'none';
	multigetAllsingleinputDisabled: boolean = true;
	multigetAllsingleexecutebuttondis: string = 'none';
	multiputeexecutebuttondis: string = 'none';
	multipostexecutebuttondis: string = 'none';
	multidelsingleexecutebuttondis: string = 'none';
	multisampleJson = {};
	multivalueJson = {};
	multivalueJsonStr = {};
	multidatasource = {};

	multiputisLoading: boolean = false;
	multiputcode: string = '';
	multiputbody: string = '';
	multiputResLoading: boolean = false;
	multiputDbuserName: string;
	multiputDbpassword: string;
	multiputdisurl: string;

	multipostisLoading: boolean = false;
	multipostcode: string = '';
	multipostbody: string = '';
	multipostResLoading: boolean = false;
	multipostDbuserName: string;
	multipostDbpassword: string;
	multipostdisurl: string;

	multigetisLoading: boolean = false;
	multigetcode: string = '';
	multigetbody: string = '';
	multigetResLoading: boolean = false;
	multigetdisurl: string;
	multigetDbuserName: string;
	multigetDbpassword: string;

	multigetsingleid: string;

	multigetAllisLoading: boolean = false;
	multigetAllcode: string = '';
	multigetAllbody: string = '';
	multigetAllResLoading: boolean = false;
	multigetAlldisurl: string = '';
	multigetAllDbuserName: string;
	multigetAllDbpassword: string;

	multideleteisLoading: boolean = false;
	multideletecode: string = '';
	multideletebody: string = '';
	multideleteResLoading: boolean = false;
	multideldisurl: string;
	multidelAllDbuserName: string;
	multidelAllDbpassword: string;

	constructor( public authService: AuthService,public formBuilder: FormBuilder,
		public dialogRef: MatDialogRef<DialogBoxComponent>,
		//@Optional() is used to prevent error if no data is passed
		@Optional() @Inject(MAT_DIALOG_DATA) public data: Map<String, Object>, @Inject(DOCUMENT) private document: Document, private httpClient: HttpClient) {


		this.getsingleForm = this.formBuilder.group({
			getsingleid: ['', Validators.required]

		});
		this.multigetsingleForm = this.formBuilder.group({
			getsingleid: ['', Validators.required]

		});
		this.delsingleForm = this.formBuilder.group({
			delsingleid: ['', Validators.required]

		});
		this.delMultisingleForm = this.formBuilder.group({
			delsingleid: ['', Validators.required]

		});
		this.prefixurl = document.location.protocol + '//' + document.location.hostname;
		if (document.location.port != null) {
			this.prefixurl = this.prefixurl + ':' + document.location.port
		}
		this.local_data = { ...data };
		this.action = this.local_data.action;
		this.componentName = this.local_data.componentName;
		this.primaryKey = this.local_data.primaryKey;
		this.message = this.local_data.message;
		if (this.local_data.multidata) {
			this.serviceName=this.local_data.serviceName;
			this.method=this.local_data.method;
			for (var val of this.local_data.multidata[0].service[0]['service attr']) {
				console.log("attr map:" + val);
				console.log(val);

				if (val['attrismandatory'] == 'yes' || val['attrismandatory'] == 'true' || val['attrismandatory'] == '1' || val['attrismandatory'] == 'YES' || val['attrismandatory'] == 'TRUE' || val['attrismandatory'] == 'True') {
					if(this.method==val['attrvalmethods']||val['attrvalmethods']=='ALL')
					{this.formaddRecInput[val['attrname']] = ['', Validators.required];}
					else {this.formaddRecInput[val['attrname']] = ['', Validators.nullValidator];}
				} else {
					this.formaddRecInput[val['attrname']] = ['', Validators.nullValidator];
				}


			}

			console.log(this.formaddRecInput);
			this.AddRecUIForm = this.formBuilder.group(this.formaddRecInput);
			for (var val of this.local_data.multidata[0].service[0]['service attr']) {
				if (val['attrcusvalidation'] == 'yes' || val['attrcusvalidation'] == 'true' || val['attrcusvalidation'] == '1' || val['attrcusvalidation'] == 'YES' || val['attrcusvalidation'] == 'TRUE' || val['attrcusvalidation'] == 'True') {
					this.AddRecUIForm.controls[val['attrname']].setAsyncValidators([this.isCustomValidator(val['attrname'], val['attrvalidatorname'])]);
				}
			}

		}
		if (this.local_data.props) {
			//this.valueJson=this.valueJson+'{\n';
			
			if(this.local_data.attribMap){
				this.sampleJson = this.sampleJson + '{\n';
			for (var val of this.local_data.attribMap) {
				if ((val.attrismandatory == 'yes') || (val.attrismandatory == 'true') || val.attrismandatory == '1') {
					this.attrb_data[val.attrname] = ['', Validators.required];
				} else {
					this.attrb_data[val.attrname] = ['', Validators.nullValidator];
				}



				if (val.coltype == 'varchar' || val.coltype.includes('varchar')) {
					this.valueJson[val.attrname] = val.attrname;
					//this.valueJson=this.valueJson+val.attrname+':    "'+val.attrname+'",\n';
					this.sampleJson = this.sampleJson + val.attrname + ':     String,\n';
				} else if (val.coltype == 'int' || val.coltype.includes('int')) {
					this.valueJson[val.attrname] = 0;
					//this.valueJson=this.valueJson+val.attrname+': 0,\n';
					this.sampleJson = this.sampleJson + val.attrname + ':     Number,\n';
				}
				else {
					this.sampleJson = this.sampleJson + val.attrname + ':     ' + val.coltype + ',\n';
					//this.valueJson=this.valueJson+val.attrname+':   "'+val.attrname+'",\n';
					this.valueJson[val.attrname] = val.attrname;
				}
			}
			this.getAllsingleForm = this.formBuilder.group(this.attrb_data);
			this.valueJsonStr = JSON.stringify(this.valueJson, undefined, 4);
			// this.valueJson=this.removeLastComma(this.valueJson)+'\n}';
			this.sampleJson = this.removeLastComma(this.sampleJson) + '\n}';
			}
			this.myAppName = this.local_data.props.get(AppConstants.appName);

			this.getAllurl = '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.service + '/' + this.local_data.props.get(AppConstants.getAllData);
			this.geturl = '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.service + '/' + this.local_data.props.get(AppConstants.getOnedata) + '/{id}';
			this.posturl = '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.service + '/' + this.local_data.props.get(AppConstants.createData);
			this.puturl = '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.service + '/' + this.local_data.props.get(AppConstants.updateData);
			this.deleteurl = '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.service + '/' + this.local_data.props.get(AppConstants.delete) + '/{id}';
			
			// multi service urls
			this.multigetAllurl = '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.props.get(AppConstants.multiService)+'/' +this.local_data.service + '/' + this.local_data.props.get(AppConstants.getMultipleAllData);
			this.multigeturl = '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.props.get(AppConstants.multiService)+'/' +this.local_data.service + '/' + this.local_data.props.get(AppConstants.getMultipleOnedata) + '/{id}';
			this.multiposturl = '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.props.get(AppConstants.multiService)+'/' +this.local_data.service + '/' + this.local_data.props.get(AppConstants.createMultipleData);
			this.multiputurl = '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.props.get(AppConstants.multiService)+'/' +this.local_data.service + '/' + this.local_data.props.get(AppConstants.updateMultipleData);
			this.multideleteurl = '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.props.get(AppConstants.multiService)+'/' +this.local_data.service + '/' + this.local_data.props.get(AppConstants.deleteMultiple) + '/{id}';
		
		}
		delete this.local_data.action;
		delete this.local_data.componentName;
		delete this.local_data.primaryKey;
		for (var key in this.local_data) {
			if ((key == this.primaryKey)) {
				this.formInput[key] = ['', Validators.required];
			}
			else {
				this.formInput[key] = ['', Validators.nullValidator];
			}
			this.formData[key] = data[key];
		}

		this.UIForm = this.formBuilder.group(this.formInput);
		this.UIForm.setValue(this.formData);
		if (this.primaryKey != null) { this.UIForm.get(this.primaryKey).disable(); }
		
		if(this.action=='Multi Service')
		{
		this.multivalueJsonStr = JSON.stringify(this.local_data['ExampleJsonFormat'], undefined, 4);
	    this.multisampleJson =  JSON.stringify(this.local_data['sampleJson'], undefined, 4);	
	    this.multidatasource=this.local_data['datasource'];
    let multiPostFormControls={};

	
     for (var key in this.multidatasource) {
      multiPostFormControls['user:'+ this.multidatasource[key]]=['', Validators.nullValidator];
      multiPostFormControls['pass:'+ this.multidatasource[key]]=['', Validators.nullValidator];

       }
      this. multiPostForm=this.formBuilder.group(multiPostFormControls);
      this. multiPutForm=this.formBuilder.group(multiPostFormControls);
      this. multiGetAllForm=this.formBuilder.group(multiPostFormControls);
	  this.multiGetForm=this.formBuilder.group(multiPostFormControls);
	  this.multiDeleteForm=this.formBuilder.group(multiPostFormControls);
      this.inputMap=this.local_data['inputMap'];
var getAllmultiFormInput={};
		for( key in this.inputMap)
		{
			getAllmultiFormInput[this.inputMap[key]]=['', Validators.nullValidator];
		}
     this.getAllmultiForm=this.formBuilder.group(getAllmultiFormInput);
    //this.getAllmultiForm = this.formBuilder.group(this.inputMap);
		}
		
	}

	doAction() {
		this.submitted = true;
		if (this.action == 'AddRec') {
			console.log('this.AddRecUIForm.invalid :' + this.AddRecUIForm.invalid);
			if (!this.AddRecUIForm.invalid) {
				this.dialogRef.close({ event: this.action, data: this.AddRecUIForm.value });
			}
		}
		if (this.action == 'Update' || this.action == 'Delete') {
			this.UIForm.get(this.primaryKey).enable();
		}
		if (this.action != 'AddRec') {
			if (!this.UIForm.invalid) {
				this.dialogRef.close({ event: this.action, data: this.UIForm.value });
			}
		}
		if (this.action == 'Multi Service') {
			 console.log(this.multiPostForm.value);
				this.dialogRef.close({ event: this.action, data: this.UIForm.value });
		 
		}
		
	}

	closeDialog() {
		this.dialogRef.close({ event: 'Cancel' });
	}
	get f() { return this.UIForm.controls; }
	get h() { return this.getAllsingleForm.controls; }
	get g() {

		return this.getsingleForm.controls;
	}
	get d() {

		return this.delsingleForm.controls;
	}
	get a() {
		return this.AddRecUIForm.controls;
	}
get i() { return this.getAllmultiForm.controls; }
get j() { return this.multigetsingleForm.controls; }
get k() { return this.delMultisingleForm.controls;}
	tryClick(type: string) {
		console.log(type);
		var spans = document.getElementById(type);
		console.log(spans.getElementsByTagName('span')[0].innerText);
		if (spans.getElementsByTagName('span')[0].innerText == 'try it out') {
			spans.getElementsByTagName('span')[0].innerText = 'cancel';
			console.log(type.includes('delete'));
			console.log(document.getElementById('deleteid'));
			if (type.includes('delete')) {
				this.inputDisabled = false;
				this.delsingleexecutebuttondis = 'inline';

			} else if (type.includes('getsingletry')) {

				this.getsingleinputDisabled = false;
				this.getsingleexecutebuttondis = 'inline';
			} else if (type.includes('getAlltry')) {

				this.getAllsingleinputDisabled = false;
				this.getAllsingleexecutebuttondis = 'inline';
			} else if (type.includes('puttry')) {
				this.puteexecutebuttondis = 'inline';
			} else if (type.includes('posttry')) {
				this.postexecutebuttondis = 'inline';
			}
			else if (type.includes('deletetry')) {


				this.delsingleexecutebuttondis = 'inline';
			}
			else if (type.includes('multipostry')) {


				this.multipostexecutebuttondis  = 'inline';
			}
			else if (type.includes('multiputry')) {
				this.multiputeexecutebuttondis = 'inline';
			}
			else if (type.includes('multigetsintry')) {
				this.multigetsingleexecutebuttondis = 'inline';
			}
			else if (type.includes('multigetAltry')) {
				this.multigetAllsingleexecutebuttondis = 'inline';
			}
			else if (type.includes('multideltry')) {
				this.multidelsingleexecutebuttondis = 'inline';
			}
			
		} else {
			spans.getElementsByTagName('span')[0].innerText = 'try it out';
			if (type.includes('delete')) {

				this.inputDisabled = true;
				this.executebuttondis = 'none';
				this.delsingleexecutebuttondis = 'none';
				this.deleteResLoading = false;
			} else if (type.includes('getsingletry')) {

				this.getsingleinputDisabled = true;
				this.getsingleexecutebuttondis = 'none';
				this.getResLoading = false;
			}
			else if (type.includes('getAlltry')) {

				this.getAllsingleinputDisabled = true;
				this.getAllsingleexecutebuttondis = 'none';
				this.getAllResLoading = false;
			}
			else if (type.includes('puttry')) {

				this.putResLoading = false;
				this.puteexecutebuttondis = 'none';
			}
			else if (type.includes('posttry')) {

				this.postResLoading = false;
				this.postexecutebuttondis = 'none';
			}
			else if (type.includes('deletetry')) {

				this.deleteResLoading = false;
				this.delsingleexecutebuttondis = 'none';
			}
			else if (type.includes('multipostry')) {

				this.multipostResLoading = false;
				this.multipostexecutebuttondis = 'none';
			}
			else if (type.includes('multiputry')) {

				this.multiputResLoading = false;
				this.multiputeexecutebuttondis = 'none';
			}
			else if (type.includes('multigetsintry')) {

				this.multigetResLoading = false;
				this.multigetsingleexecutebuttondis = 'none';
			}
			else if (type.includes('multigetAltry')) {

				this.multigetAllisLoading = false;
				this.multigetAllsingleexecutebuttondis = 'none';
			}
			else if (type.includes('multideltry')) {

				this.multideleteResLoading= false;
				this.multidelsingleexecutebuttondis = 'none';
			}
			
		}
	}

	calApi(method: string, service: string, path: string, datasourceid: string) {
		if (method == 'PUT') {
			this.putisLoading = true;

			const headers = new HttpHeaders()
				.set("Content-Type", "application/json")
				.set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
				.set("apikey", localStorage.getItem('access_token'))
				.set("passToken", btoa(this.putDbuserName + ':' + this.putDbpassword));
			var data = document.getElementById("putText").innerHTML;
			console.log('data:' + JSON.stringify(this.valueJsonStr));
			const promise = this.httpClient.put(document.location.protocol + '//' + document.location.hostname + ':8080/' + this.myAppName + '/' + localStorage.getItem('access_token') + '/' + datasourceid + '/' + service + '/' + this.local_data.props.get(AppConstants.updateData), this.valueJsonStr, { headers: headers }).toPromise();

			promise.then((data) => {
				console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
				this.putResLoading = true;
				this.putisLoading = false;
				this.putbody = JSON.stringify(data, undefined, 4);
				this.putcode = '200';

			}).catch((error) => {
				console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
				this.putResLoading = true;
				this.putbody = JSON.stringify(error, undefined, 4);
				this.putcode = error.status;
				this.putisLoading = false;

			});
		}
		else if (method == 'POST') {
			this.postisLoading = true;
			const headers = new HttpHeaders()
				.set("Content-Type", "application/json")
				.set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
				.set("apikey", localStorage.getItem('access_token'))
				.set("passToken", btoa(this.postDbuserName + ':' + this.postDbpassword));
			var data = document.getElementById("putText").innerHTML;
			const promise = this.httpClient.post(document.location.protocol + '//' + document.location.hostname + ':8080/' + this.myAppName + '/' + localStorage.getItem('access_token') + '/' + datasourceid + '/' + service + '/' + this.local_data.props.get(AppConstants.createData), this.valueJsonStr, { headers: headers }).toPromise();

			promise.then((data) => {
				console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
				this.postResLoading = true;
				this.postisLoading = false;
				this.postbody = JSON.stringify(data, undefined, 4);
				this.postcode = '200';

			}).catch((error) => {
				console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
				this.postResLoading = true;
				this.postbody = JSON.stringify(error, undefined, 4);
				this.postcode = error.status;
				this.postisLoading = false;

			});
		} else if (method == 'GET') {
			if (!this.getsingleForm.valid) {
				this.validateAllFormFields(this.getsingleForm);
				return;
			}
			this.getisLoading = true;
			const headers = new HttpHeaders()
				.set("Content-Type", "application/json")
				.set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
				.set("apikey", localStorage.getItem('access_token'))
				.set("passToken", btoa(this.getDbuserName + ':' + this.getDbpassword));
			const control = this.getsingleForm.get('getsingleid').value;
			var url = document.location.protocol + '//' + document.location.hostname + ':8080/' + this.myAppName + '/' + localStorage.getItem('access_token') + '/' + datasourceid + '/' + service + '/' + this.local_data.props.get(AppConstants.getOnedata) + '/' + control;
			this.getdisurl = url;
			const promise = this.httpClient.get(url, { headers: headers }).toPromise();

			promise.then((data) => {
				console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
				this.getResLoading = true;
				this.getisLoading = false;
				this.getbody = JSON.stringify(data, undefined, 4);
				this.getcode = '200';

			}).catch((error) => {
				console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
				this.getResLoading = true;
				this.getbody = JSON.stringify(error, undefined, 4);
				this.getcode = error.status;
				this.getisLoading = false;

			});
		} else if (method == 'GETALL') {
			this.getAllisLoading = true;
			const headers = new HttpHeaders()
				.set("Content-Type", "application/json")
				.set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
				.set("apikey", localStorage.getItem('access_token'))
				.set("passToken", btoa(this.getAllDbuserName + ':' + this.getAllDbpassword));
			console.log(this.getAllsingleForm.value);
			let paramStr = "?";
			for (var key in this.getAllsingleForm.value) {
				console.log(key + ' - ' + this.getAllsingleForm.value[key]);
				if (this.getAllsingleForm.value[key] != null && this.getAllsingleForm.value[key] != '') {
					paramStr = paramStr + key + '=' + this.getAllsingleForm.value[key] + '&';
				}
			}
			this.getAlldisurl = document.location.protocol + '//' + document.location.hostname + ':8080/' + this.myAppName + '/' + localStorage.getItem('access_token') + '/' + datasourceid + '/' + service + '/' + this.local_data.props.get(AppConstants.getAllData);
			const promise = this.httpClient.get(document.location.protocol + '//' + document.location.hostname + ':8080/' + this.myAppName + '/' + localStorage.getItem('access_token') + '/' + datasourceid + '/' + service + '/' + this.local_data.props.get(AppConstants.getAllData) + paramStr, { headers: headers }).toPromise();

			promise.then((data) => {
				console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
				this.getAllResLoading = true;
				this.getAllisLoading = false;
				this.getAllbody = JSON.stringify(data, undefined, 4);
				this.getAllcode = '200';

			}).catch((error) => {
				console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
				this.getAllResLoading = true;
				this.getAllbody = JSON.stringify(error, undefined, 4);
				this.getAllcode = error.status;
				this.getAllisLoading = false;

			});
		}
		else if (method == 'DELETE') {

			if (!this.delsingleForm.valid) {
				this.validateAllFormFields(this.delsingleForm);
				return;
			}

			this.deleteisLoading = true;
			const headers = new HttpHeaders()
				.set("Content-Type", "application/json")
				.set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
				.set("apikey", localStorage.getItem('access_token'))
				.set("passToken", btoa(this.delAllDbuserName + ':' + this.delAllDbpassword));
			const control = this.delsingleForm.get('delsingleid').value;
			var url = document.location.protocol + '//' + document.location.hostname + ':8080/' + this.myAppName + '/' + localStorage.getItem('access_token') + '/' + datasourceid + '/' + service + '/' + this.local_data.props.get(AppConstants.delete) + '/' + control;
			this.deldisurl = url;
			const promise = this.httpClient.delete(url, { headers: headers }).toPromise();

			promise.then((data) => {
				console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
				this.deleteResLoading = true;
				this.deleteisLoading = false;
				this.deletebody = JSON.stringify(data, undefined, 4);
				this.deletecode = '200';

			}).catch((error) => {
				console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
				this.deleteResLoading = true;
				this.deletebody = JSON.stringify(error, undefined, 4);
				this.deletecode = error.status;
				this.deleteisLoading = false;

			});
		}
		else if (method == 'MULTI_POST') {
			this.multipostisLoading = true;
			
				var poassToken:string='';
				for (var key in this.multidatasource) {
				var datasource=	this.multidatasource[key];
				var userName=this.multiPostForm.value['user:'+datasource];
				var Password=this.multiPostForm.value['pass:'+datasource];
				poassToken=poassToken+datasource+':'+ btoa(userName + ':' + Password)+',';
					}
				
			poassToken = poassToken.substring(0, poassToken.length - 1);
				 
				const headers = new HttpHeaders()
				.set("Content-Type", "application/json")
				.set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
				.set("apikey", localStorage.getItem('access_token'))
				.set("passToken", poassToken);
		var url=	document.location.protocol + '//' + document.location.hostname + ':8080/' + this.myAppName +	 '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.props.get(AppConstants.multiService)+'/' +this.local_data.service + '/' + this.local_data.props.get(AppConstants.createMultipleData);
		this.multipostdisurl=url;
		const promise = this.httpClient.post(url, this.multivalueJsonStr, { headers: headers }).toPromise();
	     promise.then((data) => {
				console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
				this.multipostResLoading = true;
				this.multipostisLoading = false;
				this.multipostbody = JSON.stringify(data, undefined, 4);
				this.multipostcode = '200';

			}).catch((error) => {
				console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
				this.multipostResLoading = true;
				this.multipostbody = JSON.stringify(error, undefined, 4);
				this.multipostcode = error.status;
				this.multipostisLoading = false;

			});
				 
		}
		else if (method == 'MULTI_PUT') {
			this.multiputisLoading = true;
			 
				var poassToken:string='';
				for (var key in this.multidatasource) {
				var datasource=	this.multidatasource[key];
				var userName=this.multiPutForm.value['user:'+datasource];
				var Password=this.multiPutForm.value['pass:'+datasource];
				poassToken=poassToken+datasource+':'+ btoa(userName + ':' + Password)+',';
					}
				
			poassToken = poassToken.substring(0, poassToken.length - 1);
			const headers = new HttpHeaders()
				.set("Content-Type", "application/json")
				.set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
				.set("apikey", localStorage.getItem('access_token'))
				.set("passToken", poassToken);
				var url=	document.location.protocol + '//' + document.location.hostname + ':8080/' + this.myAppName +	 '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.props.get(AppConstants.multiService)+'/' +this.local_data.service + '/' + this.local_data.props.get(AppConstants.updateMultipleData);
		
				this.multiputdisurl=url;
					const promise = this.httpClient.put(url, this.multivalueJsonStr, { headers: headers }).toPromise();

			promise.then((data) => {
				console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
				this.multiputResLoading = true;
				this.multiputisLoading = false;
				this.multiputbody = JSON.stringify(data, undefined, 4);
				this.putcode = '200';

			}).catch((error) => {
				console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
				this.multiputResLoading = true;
				this.multiputbody = JSON.stringify(error, undefined, 4);
				this.multiputcode = error.status;
				this.multiputisLoading = false;

			});
				
				
				
				 
		}
		else if (method == 'MULTI_GET') {
			this.multigetisLoading =true;
			
			var poassToken:string='';
				for (var key in this.multidatasource) {
				var datasource=	this.multidatasource[key];
				var userName=this.multiGetForm.value['user:'+datasource];
				var Password=this.multiGetForm.value['pass:'+datasource];
				poassToken=poassToken+datasource+':'+ btoa(userName + ':' + Password)+',';
					}
				if (!this.multiGetForm.valid) {
				this.validateAllFormFields(this.multiGetForm);
				return;
			}
			poassToken = poassToken.substring(0, poassToken.length - 1);
			const headers = new HttpHeaders()
				.set("Content-Type", "application/json")
				.set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
				.set("apikey", localStorage.getItem('access_token'))
				.set("passToken", poassToken);
				const control = this.multigetsingleForm.get('getsingleid').value;
			var url = document.location.protocol + '//' + document.location.hostname + ':8080/' + this.myAppName +	 '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.props.get(AppConstants.multiService)+'/' +this.local_data.service + '/' + this.local_data.props.get(AppConstants.getMultipleOnedata)+'/'+control;
			 this.multigetdisurl=url;
			const promise = this.httpClient.get(url, { headers: headers }).toPromise();

			promise.then((data) => {
				console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
				this.multigetResLoading = true;
				this.multigetisLoading = false;
				this.multigetbody = JSON.stringify(data, undefined, 4);
				this.multigetcode = '200';

			}).catch((error) => {
				console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
				this.multigetResLoading = true;
				this.multigetbody = JSON.stringify(error, undefined, 4);
				this.multigetcode = error.status;
				this.multigetisLoading = false;

			});
			
			 
		}
		else if (method == 'MULTI_GETALL') {
			this.multigetAllisLoading = true;
			var poassToken:string='';
				for (var key in this.multidatasource) {
				var datasource=	this.multidatasource[key];
				var userName=this.multiGetAllForm.value['user:'+datasource];
				var Password=this.multiGetAllForm.value['pass:'+datasource];
				poassToken=poassToken+datasource+':'+ btoa(userName + ':' + Password)+',';
					}
				
			poassToken = poassToken.substring(0, poassToken.length - 1);
			const headers = new HttpHeaders()
				.set("Content-Type", "application/json")
				.set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
				.set("apikey", localStorage.getItem('access_token'))
				.set("passToken", poassToken);
				
				let paramStr = "?";
			for (var key in this.getAllmultiForm.value) {
				console.log(key + ' - ' + this.getAllmultiForm.value[key]);
				if (this.getAllmultiForm.value[key] != null && this.getAllmultiForm.value[key] != '') {
					paramStr = paramStr + key + '=' + this.getAllmultiForm.value[key] + '&';
				}
			}
				
				var url = document.location.protocol + '//' + document.location.hostname + ':8080/' + this.myAppName +	 '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.props.get(AppConstants.multiService)+'/' +this.local_data.service + '/' + this.local_data.props.get(AppConstants.getMultipleAllData);
			 this.multigetAlldisurl=url;
			const promise = this.httpClient.get(url+paramStr, { headers: headers }).toPromise();

			promise.then((data) => {
				console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
				this.multigetAllResLoading = true;
				this.multigetAllisLoading = false;
				this.multigetAllbody = JSON.stringify(data, undefined, 4);
				this.multigetAllcode = '200';

			}).catch((error) => {
				console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
				this.multigetAllResLoading = true;
				this.multigetAllbody = JSON.stringify(error, undefined, 4);
				this.multigetAllcode = error.status;
				this.multigetAllisLoading = false;

			});
				
			 
		}
		else if (method == 'MULTI_DELETE') {
			this.multideleteisLoading = true;
			var poassToken:string='';
				for (var key in this.multidatasource) {
				var datasource=	this.multidatasource[key];
				var userName=this.multiDeleteForm.value['user:'+datasource];
				var Password=this.multiDeleteForm.value['pass:'+datasource];
				poassToken=poassToken+datasource+':'+ btoa(userName + ':' + Password)+',';
					}
				
			poassToken = poassToken.substring(0, poassToken.length - 1);
			const headers = new HttpHeaders()
				.set("Content-Type", "application/json")
				.set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
				.set("apikey", localStorage.getItem('access_token'))
				.set("passToken", poassToken);
				
			if (!this.delMultisingleForm.valid) {
				this.validateAllFormFields(this.delsingleForm);
				return;
			}
			
			
			const control = this.delMultisingleForm.get('delsingleid').value;
			var url = document.location.protocol + '//' + document.location.hostname + ':8080/' + this.myAppName +	 '/' + localStorage.getItem('access_token') + '/' + this.local_data.dsname + '/' + this.local_data.props.get(AppConstants.multiService)+'/' +this.local_data.service + '/' + this.local_data.props.get(AppConstants.deleteMultiple)+'/'+control;
			this.multideldisurl = url;
			const promise = this.httpClient.delete(url, { headers: headers }).toPromise();

			promise.then((data) => {
				console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
				this.multideleteResLoading = true;
				this.multideleteisLoading = false;
				this.multideletebody = JSON.stringify(data, undefined, 4);
				this.multideletecode = '200';

			}).catch((error) => {
				console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
				this.multideleteResLoading = true;
				this.multideletebody = JSON.stringify(error, undefined, 4);
				this.multideletecode = error.status;
				this.multideleteisLoading = false;

			});
			
			
			 
			
		}

	}

	removeLastComma(strng: string) {
		var n = strng.lastIndexOf(",");
		var a = strng.substring(0, n)
		return a;
	}

	validateAllFormFields(formGroup: FormGroup) {         //{1}
		Object.keys(formGroup.controls).forEach(field => {  //{2}
			const control = formGroup.get(field);             //{3}
			if (control instanceof FormControl) {             //{4}
				control.markAsTouched({ onlySelf: true });
			} else if (control instanceof FormGroup) {        //{5}
				this.validateAllFormFields(control);            //{6}
			}
		});
	}
	isCustomValidator(controlName: string, validatorName: string): AsyncValidatorFn {
		return (control: AbstractControl): Observable<ValidationErrors> => {
			 
			let map = new Map<string, string>();
		map.set('validatorName', validatorName);
		map.set('value',control.value);
		console.log(this.AddRecUIForm.value);
		
		 for (var key in this.AddRecUIForm.value) {
         if(this.AddRecUIForm.value[key]!=null&&this.AddRecUIForm.value[key]!=''){
			map.set(key,this.AddRecUIForm.value[key]);
				}}
		
		this.authService.validateData(this.serviceName, map).subscribe((res) => {
			console.log(res);
			 
			if ((String(res)) == 'true' || (String(res)) == 'TRUE' || (String(res)) == 'True') { 
				control.setErrors(null);
			    return bReturn ? of(null) : of(err);
             }
			else { 
				control.setErrors({ customvalidation: true });	
		 let err: ValidationErrors = { 'invalid': true };
          return bReturn ? of(null) : of(err);
            }
			 
		});
		
			let bReturn: boolean = true;
			let control1 = this.AddRecUIForm.controls[controlName];
			let err: ValidationErrors = { 'invalid': true };
			 control.setErrors(null);
			return bReturn ? of(null) : of(err);
		}
	}
}
