import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultarProvasComponent } from './consultar-provas.component';

describe('ConsultarProvasComponent', () => {
  let component: ConsultarProvasComponent;
  let fixture: ComponentFixture<ConsultarProvasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultarProvasComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ConsultarProvasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
