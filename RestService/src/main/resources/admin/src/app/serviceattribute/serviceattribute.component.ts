import { Component, OnInit } from '@angular/core';
import { AfterViewInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { AuthService } from "../auth/auth-service.service";
import { FormBuilder, FormGroup,Validators  } from "@angular/forms";
import {MatDialog} from '@angular/material/dialog';
import { DialogBoxComponent } from '../dialog-box/dialog-box.component';

@Component({
  selector: 'app-serviceattribute',
  templateUrl: './serviceattribute.component.html',
  styleUrls: ['./serviceattribute.component.css']
})
export class ServiceattributeComponent implements OnInit , AfterViewInit {
	//displayedColumns: string[];// = ['position', 'name', 'weight', 'symbol'];
	displayedColumns: string[];//=["id","uid","serviceid","attrid","name","classname","paramclassname"];
	componentName: string='Multi Service Configuration';
	model = {};
	dataSource: any;
	data: any;
	primaryKey: string='id';
	isLoading = true;
	serviceName: string ='service attr';
	@ViewChild(MatPaginator) paginator: MatPaginator;
	@ViewChild(MatSort) sort: MatSort;
	// @ViewChild(MatTableDataSource,{static:true}) table: MatTableDataSource<any>;

	  UIForm: FormGroup;
    loading = false;
    submitted = false;
	ngAfterViewInit() {
		
		
	}
	constructor( public dialog: MatDialog, public formBuilder: FormBuilder,  public authService: AuthService) {
		
		this.reinit();
	}
	
	openDialog(action,obj) {
    obj.action = action;
    let width='700px';
	if(action == 'Delete'){
		 width='400px';
		}
    obj.componentName = this.componentName;
    obj.primaryKey=this.primaryKey;
    const dialogRef = this.dialog.open(DialogBoxComponent, {
      width: width,
      data:obj
    });

    dialogRef.afterClosed().subscribe(result => {
	if(result!=undefined)
     { if(result.event == 'Add'){
        this.addRowData(result.data);
      }else if(result.event == 'Update'){
        this.updateRowData(result.data);
      }else if(result.event == 'Delete'){
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

ProcessorSubmit(){
	
	
}

 addRowData(row_obj){
	this.isLoading = true;
	delete row_obj[this.primaryKey];
	const promise =this.authService.addData(this.serviceName,row_obj);
	promise.then((data)=>{
      console.log("Promise resolved with: " + JSON.stringify(data));
      this.reinit();
     
    }).catch((error)=>{
      console.log("Promise rejected with " + JSON.stringify(error));
     this.isLoading = false;
      
    });
    
 
  }
  updateRowData(row_obj){
	this.isLoading = true;
  const promise= this.authService.updateData(this.serviceName,row_obj);
   promise.then((data)=>{
      console.log("Promise resolved with: " + JSON.stringify(data));
      this.reinit();
     
    }).catch((error)=>{
      console.log("Promise rejected with " + JSON.stringify(error));
      this.isLoading = false;
      
    });
	
   
  }
  deleteRowData(row_obj){
	this.isLoading = true;
   const promise=   this.authService.deleteUniqueData(this.serviceName,row_obj[this.primaryKey]);
   promise.then((data)=>{
      console.log("Promise resolved with: " + JSON.stringify(data));
  this.reinit();
     return data;
    }).catch((error)=>{
      console.log("Promise rejected with " + JSON.stringify(error));
      this.isLoading = false;
      
    });
   
  }
reinit()
{
	
	let map = new Map<string,string>();
		let uid=localStorage.getItem('uid');
		map.set('uid',uid);
		this.authService.getdata(this.serviceName,map).subscribe((res) => {
			this.data =res;
		 
		console.log(this.displayedColumns);
        this.displayedColumns = [];

		for (var key in this.data[0]) {

			this.displayedColumns.push(key);
		}
		this.displayedColumns.push('action');
		this.dataSource = new MatTableDataSource<any>(this.data );
		//this.columnClick('uid');
		//this.columnClick('serviceid');
		//this.columnClick('attrid');
		this.dataSource.paginator = this.paginator;
		this.dataSource.sort = this.sort;
		this.isLoading = false;
		// this.data[0].name='vinoth';
    });
	
}
}


