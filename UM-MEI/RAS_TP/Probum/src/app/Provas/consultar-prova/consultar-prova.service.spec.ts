import { TestBed } from '@angular/core/testing';

import { ConsultarProvaService } from './consultar-prova.service';

describe('ConsultarProvaService', () => {
  let service: ConsultarProvaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConsultarProvaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
