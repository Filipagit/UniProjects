import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultarProvaComponent } from './consultar-prova.component';

describe('ConsultarProvaComponent', () => {
  let component: ConsultarProvaComponent;
  let fixture: ComponentFixture<ConsultarProvaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultarProvaComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ConsultarProvaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
