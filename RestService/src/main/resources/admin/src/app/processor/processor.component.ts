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
	selector: 'app-processor',
	templateUrl: './processor.component.html',
	styleUrls: ['./processor.component.css']
})
export class ProcessorComponent implements OnInit, AfterViewInit {
	//displayedColumns: string[];// = ['position', 'name', 'weight', 'symbol'];
	displayedColumns: string[];//=["id","uid","serviceid","attrid","name","classname","paramclassname"];
	model = {};
	dataSource: any;
	data: any;
	@ViewChild(MatPaginator) paginator: MatPaginator;
	@ViewChild(MatSort) sort: MatSort;
	 @ViewChild(MatTableDataSource,{static:true}) table: MatTableDataSource<any>;

	  ProcessorForm: FormGroup;
    loading = false;
    submitted = false;
	ngAfterViewInit() {
		this.dataSource.paginator = this.paginator;
		this.dataSource.sort = this.sort;
		
	}
	constructor( public dialog: MatDialog, public formBuilder: FormBuilder,  public authService: AuthService) {
		 this.ProcessorForm= this.formBuilder.group({
      email: ['', Validators.required],
      password: ['', Validators.required]
    })
		
		let map = new Map<string,string>();
		let uid=localStorage.getItem('uid');
		map.set('uid',uid);
		this.authService.getdata('vinprocessor',map).subscribe((res) => {
			this.data =res;
		 
		console.log(this.displayedColumns);
        this.displayedColumns = [];

		for (var key in this.data[0]) {

			this.displayedColumns.push(key);
		}
		this.displayedColumns.push('action');
		this.dataSource = new MatTableDataSource<any>(this.data );
		this.columnClick('uid');
		this.columnClick('serviceid');
		this.columnClick('attrid');
		// this.data[0].name='vinoth';
    });
		 
		 
		 
		
	}
	
	openDialog(action,obj) {
    obj.action = action;
    const dialogRef = this.dialog.open(DialogBoxComponent, {
      width: '250px',
      data:obj
    });

    dialogRef.afterClosed().subscribe(result => {
      if(result.event == 'Add'){
        this.addRowData(result.data);
      }else if(result.event == 'Update'){
        this.updateRowData(result.data);
      }else if(result.event == 'Delete'){
        this.deleteRowData(result.data);
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

get f() { return this.ProcessorForm.controls; }

ProcessorSubmit(){
	
	
}

 addRowData(row_obj){
    var d = new Date();
    this.dataSource.push({
      id:d.getTime(),
      name:row_obj.name
    });
    this.table._renderChangesSubscription;
    
  }
  updateRowData(row_obj){
    this.dataSource = this.dataSource.filter((value,key)=>{
      if(value.id == row_obj.id){
        value.name = row_obj.name;
      }
      return true;
    });
  }
  deleteRowData(row_obj){
    this.dataSource = this.dataSource.filter((value,key)=>{
      return value.id != row_obj.id;
    });
  }

}


