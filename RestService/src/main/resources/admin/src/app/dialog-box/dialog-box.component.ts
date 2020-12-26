import { Component, Inject, Optional } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators ,FormControl} from "@angular/forms";
import { AppConstants } from "../app-constants";
import { DOCUMENT } from '@angular/common';
import { HttpClient, HttpHeaders, HttpErrorResponse,HttpParams } from '@angular/common/http';
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
	attrb_data={};
    getAllsingleForm:FormGroup;
    getsingleForm: FormGroup;
    delsingleForm: FormGroup;
	submitted = false;
	action: string;
	local_data: any;
	componentName: string;
	UIForm: FormGroup;
	message: string;
	primaryKey: any;
	formInput = {};
	formData = {};
	geturl: string;
	getAllurl: string;
	posturl: string;
	puturl: string;
	deleteurl: string;
	inputDisabled:boolean=true;
	executebuttondis:string='none';
	getsingleinputDisabled:boolean=true;
	getsingleexecutebuttondis:string='none';
	getAllsingleinputDisabled:boolean=true;
	getAllsingleexecutebuttondis:string='none';
	puteexecutebuttondis:string='none';
	postexecutebuttondis:string ='none';
	delsingleexecutebuttondis:string ='none';
	sampleJson:string='';
	valueJson={};
	valueJsonStr:string='';
	myAppName:string;
	prefixurl:string='';
	
	putisLoading:boolean=false;
	putcode:string='';
	putbody:string='';
	putResLoading:boolean=false;
	
	postisLoading:boolean=false;
	postcode:string='';
	postbody:string='';
	postResLoading:boolean=false;
	
	getisLoading:boolean=false;
	getcode:string='';
	getbody:string='';
	getResLoading:boolean=false;
	getdisurl:string;
	
	getsingleid:string;
	
	getAllisLoading:boolean=false;
	getAllcode:string='';
	getAllbody:string='';
	getAllResLoading:boolean=false;
	getAlldisurl:string='';
	deleteisLoading:boolean=false;
	deletecode:string='';
	deletebody:string='';
	deleteResLoading:boolean=false;
	deldisurl:string;
	
	constructor(public formBuilder: FormBuilder,
		public dialogRef: MatDialogRef<DialogBoxComponent>,
		//@Optional() is used to prevent error if no data is passed
		@Optional() @Inject(MAT_DIALOG_DATA) public data: Map<String, Object>,@Inject(DOCUMENT)  private  document: Document,private httpClient: HttpClient) {
			
			 
		this.getsingleForm = this.formBuilder.group({
      getsingleid: ['', Validators.required]
     
    });
this.delsingleForm = this.formBuilder.group({
      delsingleid: ['', Validators.required]
     
    });
		 this.prefixurl=document.location.protocol +'//'+ document.location.hostname;
	if(document.location.port!=null)
	{
	 this.prefixurl= this.prefixurl+':'+document.location.port
	}
		this.local_data = { ...data };
		this.action = this.local_data.action;
		this.componentName = this.local_data.componentName;
		this.primaryKey = this.local_data.primaryKey;
		this.message = this.local_data.message;
		 
		if (this.local_data.props) {
			//this.valueJson=this.valueJson+'{\n';
			this.sampleJson=this.sampleJson+'{\n';
			for (var val of  this.local_data.attribMap) {
				if((val.attrismandatory=='yes')||(val.attrismandatory=='true')||val.attrismandatory=='1'){
					this.attrb_data[val.attrname]=['', Validators.required];
				}else{
						this.attrb_data[val.attrname]=['',Validators.nullValidator];
				}
				
				
				
          if(val.coltype=='varchar'||val.coltype.includes('varchar'))
			{
				 this.valueJson[val.attrname]=val.attrname;
				//this.valueJson=this.valueJson+val.attrname+':    "'+val.attrname+'",\n';
				this.sampleJson=this.sampleJson+val.attrname+':     String,\n';
			}else if(val.coltype=='int'||val.coltype.includes('int'))
			{
				 this.valueJson[val.attrname]=0;
			//this.valueJson=this.valueJson+val.attrname+': 0,\n';
				this.sampleJson=this.sampleJson+val.attrname+':     Number,\n';	
			}
			else{
           this.sampleJson=this.sampleJson+val.attrname+':     '+val.coltype+',\n';
			//this.valueJson=this.valueJson+val.attrname+':   "'+val.attrname+'",\n';
			 this.valueJson[val.attrname]=val.attrname;
		    }
             }
		this.getAllsingleForm = this.formBuilder.group(this.attrb_data);
			this.valueJsonStr=JSON.stringify(this.valueJson, undefined, 4);
          // this.valueJson=this.removeLastComma(this.valueJson)+'\n}';
			this.sampleJson=this.removeLastComma(this.sampleJson)+'\n}';
			this.myAppName=this.local_data.props[AppConstants.appName];
			 
			this.getAllurl = '/'+ localStorage.getItem('access_token')+'/'+this.local_data.dsname+'/' + this.local_data.service + '/' + this.local_data.props[AppConstants.getAllData];
			this.geturl = '/'+ localStorage.getItem('access_token')+'/'+this.local_data.dsname+'/' + this.local_data.service + '/' + this.local_data.props[AppConstants.getOnedata] + '/{id}';
			this.posturl = '/'+ localStorage.getItem('access_token')+'/'+this.local_data.dsname+'/' + this.local_data.service + '/' + this.local_data.props[AppConstants.createData];
			this.puturl = '/'+ localStorage.getItem('access_token')+'/'+this.local_data.dsname+'/' + this.local_data.service + '/' + this.local_data.props[AppConstants.updateData];
			this.deleteurl = '/'+ localStorage.getItem('access_token')+'/'+this.local_data.dsname+'/' + this.local_data.service + '/' + this.local_data.props[AppConstants.delete] + '/{id}';
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
	}

	doAction() {
		this.submitted = true;
		if (this.action == 'Update' || this.action == 'Delete') {
			this.UIForm.get(this.primaryKey).enable();
		}
		if (!this.UIForm.invalid) {
			this.dialogRef.close({ event: this.action, data: this.UIForm.value });
		}
	}

	closeDialog() {
		this.dialogRef.close({ event: 'Cancel' });
	}
	get f() { return this.UIForm.controls; }
	get h() { return this.getAllsingleForm.controls; }
	get g() {
		 
		 return this.getsingleForm.controls; }
	get d() {
		 
		 return this.delsingleForm.controls; }

	tryClick(type: string) {
		console.log(type);
		var spans = document.getElementById(type);
		console.log(spans.getElementsByTagName('span')[0].innerText );
		if (spans.getElementsByTagName('span')[0].innerText == 'try it out') {
			spans.getElementsByTagName('span')[0].innerText = 'cancel';
			console.log(type.includes('delete'));
			console.log(document.getElementById('deleteid'));
			if(type.includes('delete'))
			{
				this.inputDisabled=false;
				this.delsingleexecutebuttondis='inline';
				 
			}else if(type.includes('getsingletry'))
			{
				 
				this.getsingleinputDisabled=false;
                this.getsingleexecutebuttondis='inline';
			}else if(type.includes('getAlltry'))
			{
				 
				this.getAllsingleinputDisabled=false;
                this.getAllsingleexecutebuttondis='inline';
			}else if(type.includes('puttry'))
			{ 
                this.puteexecutebuttondis='inline';
			}else if(type.includes('posttry'))
			{  
                this.postexecutebuttondis='inline';
			}
			else if(type.includes('deletetry'))
			{
				 
				 
                this.delsingleexecutebuttondis='inline';
			}
		} else {
			spans.getElementsByTagName('span')[0].innerText = 'try it out';
		if(type.includes('delete'))
			{
				
				this.inputDisabled=true;
				this.executebuttondis='none';
				   this.delsingleexecutebuttondis='none';
	           this.deleteResLoading=false;
			}else if(type.includes('getsingletry'))
			{
				
				this.getsingleinputDisabled=true;
                this.getsingleexecutebuttondis='none';
                this.getResLoading=false;
			}
			else if(type.includes('getAlltry'))
			{
				
				this.getAllsingleinputDisabled=true;
                this.getAllsingleexecutebuttondis='none';
                this.getAllResLoading=false;
			}
			else if(type.includes('puttry'))
			{
				
				 this.putResLoading=false;
                this.puteexecutebuttondis='none';
			}
			else if(type.includes('posttry'))
			{
				
				  this.postResLoading=false;
                this.postexecutebuttondis='none';
			}
			else if(type.includes('deletetry'))
			{
				
				  this.deleteResLoading=false;
                this.delsingleexecutebuttondis='none';
			}
		}
	}
	
	calApi(method:string,service:string,path:string,datasourceid:string)
	{
		if(method=='PUT'){
		this.putisLoading=true;
		
		const headers = new HttpHeaders()
    .set("Content-Type", "application/json")
    .set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
    .set("apikey", localStorage.getItem('access_token'));
   var data=document.getElementById("putText").innerHTML;
console.log('data:'+ JSON.stringify(this.valueJsonStr));
		 const promise = this.httpClient.put(document.location.protocol +'//'+ document.location.hostname+':8080/'+this.myAppName+'/'+localStorage.getItem('access_token')+'/'+datasourceid+'/'+service+'/'+this.local_data.props[AppConstants.updateData], this.valueJsonStr, { headers:headers }).toPromise();	
		
        promise.then((data) => {
			console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
			this.putResLoading=true; 
			this.putisLoading=false;
			this.putbody=JSON.stringify(data, undefined, 4);
			this.putcode='200';

		}).catch((error) => {
			console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
			this.putResLoading=true;
			this.putbody=JSON.stringify(error, undefined, 4);
			this.putcode=error.status;
			this.putisLoading=false;

		});
}
else if(method=='POST'){
		this.postisLoading=true;
		const headers = new HttpHeaders()
    .set("Content-Type", "application/json")
    .set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
    .set("apikey", localStorage.getItem('access_token'));
   var data=document.getElementById("putText").innerHTML;
		 const promise = this.httpClient.post(document.location.protocol +'//'+ document.location.hostname+':8080/'+this.myAppName+'/'+localStorage.getItem('access_token')+'/'+datasourceid+'/'+service+'/'+this.local_data.props[AppConstants.createData], this.valueJsonStr, { headers:headers }).toPromise();	
		
        promise.then((data) => {
			console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
			this.postResLoading=true; 
			this.postisLoading=false;
			this.postbody=JSON.stringify(data, undefined, 4);
			this.postcode='200';

		}).catch((error) => {
			console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
			this.postResLoading=true;
			this.postbody=JSON.stringify(error, undefined, 4);
			this.postcode=error.status;
			this.postisLoading=false;

		});
}else if(method=='GET'){
	if(!this.getsingleForm.valid)
	{
		 this.validateAllFormFields(this.getsingleForm);
		return;
	}
		this.getisLoading=true;
		const headers = new HttpHeaders()
    .set("Content-Type", "application/json")
    .set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
    .set("apikey", localStorage.getItem('access_token'));
     const control = this.getsingleForm.get('getsingleid').value;
         var url=document.location.protocol +'//'+ document.location.hostname+':8080/'+this.myAppName+'/'+localStorage.getItem('access_token')+'/'+datasourceid+'/'+service+'/'+this.local_data.props[AppConstants.getOnedata]+'/'+control;
	this.getdisurl=url;
		 const promise = this.httpClient.get(url,{ headers:headers}).toPromise();	
		
        promise.then((data) => {
			console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
			this.getResLoading=true; 
			this.getisLoading=false;
			this.getbody=JSON.stringify(data, undefined, 4);
			this.getcode='200';

		}).catch((error) => {
			console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
			this.getResLoading=true;
			this.getbody=JSON.stringify(error, undefined, 4);
			this.getcode=error.status;
			this.getisLoading=false;

		});
}else if(method=='GETALL'){
		this.getAllisLoading=true;
		const headers = new HttpHeaders()
    .set("Content-Type", "application/json")
    .set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
    .set("apikey", localStorage.getItem('access_token'));
console.log(	this.getAllsingleForm.value );
let paramStr="?";
for(var key in this.getAllsingleForm.value ){
  console.log(key + ' - ' + this.getAllsingleForm.value[key]);
if(this.getAllsingleForm.value[key]!=null&&this.getAllsingleForm.value[key]!='')
{
	paramStr=paramStr+key+'='+ this.getAllsingleForm.value[key]+'&';
}
}
   this.getAlldisurl=document.location.protocol +'//'+ document.location.hostname+':8080/'+this.myAppName+'/'+localStorage.getItem('access_token')+'/'+datasourceid+'/'+service+'/'+this.local_data.props[AppConstants.getAllData];
		 const promise = this.httpClient.get(document.location.protocol +'//'+ document.location.hostname+':8080/'+this.myAppName+'/'+localStorage.getItem('access_token')+'/'+datasourceid+'/'+service+'/'+this.local_data.props[AppConstants.getAllData]+paramStr,  { headers:headers }).toPromise();	
		
        promise.then((data) => {
			console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
			this.getAllResLoading=true; 
			this.getAllisLoading=false;
			this.getAllbody=JSON.stringify(data, undefined, 4);
			this.getAllcode='200';

		}).catch((error) => {
			console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
			this.getAllResLoading=true;
			this.getAllbody=JSON.stringify(error, undefined, 4);
			this.getAllcode=error.status;
			this.getAllisLoading=false;

		});
}
else if(method=='DELETE'){
	
	if(!this.delsingleForm.valid)
	{
		 this.validateAllFormFields(this.delsingleForm);
		return;
	}
	
		this.deleteisLoading=true;
		const headers = new HttpHeaders()
    .set("Content-Type", "application/json")
    .set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy")
    .set("apikey", localStorage.getItem('access_token'));
   
   const control = this.delsingleForm.get('delsingleid').value;
     var url=document.location.protocol +'//'+ document.location.hostname+':8080/'+this.myAppName+'/'+localStorage.getItem('access_token')+'/'+datasourceid+'/'+service+'/'+this.local_data.props[AppConstants.delete]+'/'+control;
	this.deldisurl=url;
		 const promise = this.httpClient.delete(url,  { headers:headers }).toPromise();	
		
        promise.then((data) => {
			console.log("Promise resolved with: " + JSON.stringify(data, undefined, 4));
			this.deleteResLoading=true; 
			this.deleteisLoading=false;
			this.deletebody=JSON.stringify(data, undefined, 4);
			this.deletecode='200';

		}).catch((error) => {
			console.log("Promise rejected with " + JSON.stringify(error, undefined, 4));
			this.deleteResLoading=true;
			this.deletebody=JSON.stringify(error, undefined, 4);
			this.deletecode=error.status;
			this.deleteisLoading=false;

		});
}
		
	}
	
	 removeLastComma(strng:string){        
    var n=strng.lastIndexOf(",");
    var a=strng.substring(0,n) 
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
}
