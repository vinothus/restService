import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceattributeComponent } from './serviceattribute.component';

describe('ServiceattributeComponent', () => {
  let component: ServiceattributeComponent;
  let fixture: ComponentFixture<ServiceattributeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServiceattributeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceattributeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
