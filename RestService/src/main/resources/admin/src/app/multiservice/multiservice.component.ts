import { Component, OnInit } from '@angular/core';
import { AfterViewInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { AuthService } from "../auth/auth-service.service";
import { FormBuilder, FormGroup,Validators  } from "@angular/forms";
@Component({
  selector: 'app-multiservice',
  templateUrl: './multiservice.component.html',
  styleUrls: ['./multiservice.component.css']
})
export class MultiserviceComponent implements OnInit , AfterViewInit {
	//displayedColumns: string[];// = ['position', 'name', 'weight', 'symbol'];
	displayedColumns: string[];//=["id","uid","serviceid","attrid","name","classname","paramclassname"];
	model = {};
	dataSource: any;
	data: any;
	@ViewChild(MatPaginator) paginator: MatPaginator;
	@ViewChild(MatSort) sort: MatSort;
	  UIForm: FormGroup;
    loading = false;
    submitted = false;
	ngAfterViewInit() {
		this.dataSource.paginator = this.paginator;
		this.dataSource.sort = this.sort;
		
	}
	constructor(  public formBuilder: FormBuilder,  public authService: AuthService) {
		 this.UIForm= this.formBuilder.group({
      email: ['', Validators.required],
      password: ['', Validators.required]
    })
		
		let map = new Map<string,string>();
		let uid=localStorage.getItem('uid');
		map.set('uid',uid);
		this.authService.getdata('multi service',map).subscribe((res) => {
			this.data =res;
		 
		console.log(this.displayedColumns);
        this.displayedColumns = [];

		for (var key in this.data[0]) {

			this.displayedColumns.push(key);
		}
		this.dataSource = new MatTableDataSource<any>(this.data );
		//this.columnClick('uid');
		//this.columnClick('serviceid');
		//this.columnClick('attrid');
		// this.data[0].name='vinoth';
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

FormSubmit(){
	
	
}

}
