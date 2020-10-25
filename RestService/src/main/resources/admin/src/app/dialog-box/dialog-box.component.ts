import { Component, Inject, Optional } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup,Validators  } from "@angular/forms";
export interface UsersData {
  name: string;
  id: number;
}

@Component({
  selector: 'app-dialog-box',
  templateUrl: './dialog-box.component.html',
  styleUrls: ['./dialog-box.component.css']
})
export class DialogBoxComponent   {

submitted = false;
   action:string;
  local_data:any;
  componentName: string;
  UIForm: FormGroup;
primaryKey:any;
  formInput={};
 formData={};
  constructor( public formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<DialogBoxComponent>,
    //@Optional() is used to prevent error if no data is passed
    @Optional() @Inject(MAT_DIALOG_DATA) public data: Map<String,Object>) {
    console.log(data);
    this.local_data = {...data};
    this.action = this.local_data.action;
    this.componentName=  this.local_data.componentName;
    this.primaryKey=this.local_data.primaryKey;
    delete this.local_data.action;
    delete this.local_data.componentName;
    delete this.local_data.primaryKey;
   for (var key in this.local_data) {
	if(!(key==this.primaryKey))
	{
		this.formInput[key]= ['', Validators.required];
		}
		else {
		this.formInput[key]= ['', Validators.nullValidator];	
		}
       this.formData[key]=data[key];
}

this.UIForm= this.formBuilder.group(this.formInput);
this.UIForm.setValue( this.formData);
this.UIForm.get(this.primaryKey).disable();
  }

  doAction(){
	this.submitted = true;
	if(this.action=='Update'||this.action=='Delete')
	{
		this.UIForm.get(this.primaryKey).enable();
	}
	if (!this.UIForm.invalid) {
    this.dialogRef.close({event:this.action,data:this.UIForm.value});
}
  }

  closeDialog(){
    this.dialogRef.close({event:'Cancel'});
  }
get f() { return this.UIForm.controls; }
}
