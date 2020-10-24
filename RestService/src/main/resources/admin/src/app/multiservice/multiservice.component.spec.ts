import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MultiserviceComponent } from './multiservice.component';

describe('MultiserviceComponent', () => {
  let component: MultiserviceComponent;
  let fixture: ComponentFixture<MultiserviceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MultiserviceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MultiserviceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
