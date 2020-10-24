import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceerrorComponent } from './serviceerror.component';

describe('ServiceerrorComponent', () => {
  let component: ServiceerrorComponent;
  let fixture: ComponentFixture<ServiceerrorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServiceerrorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceerrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
