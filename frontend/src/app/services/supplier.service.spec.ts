import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { SupplierService } from './supplier.service';
import { Supplier } from '../models/models';

describe('SupplierService', () => {
  let service: SupplierService;
  let httpMock: HttpTestingController;

  const API = 'http://localhost:8080/api/suppliers';

  const mockSupplier: Supplier = {
    id: 1,
    name: 'ACME Corp',
    cnpjCpf: '12.345.678/0001-99',
    contact: 'acme@example.com',
    productCategory: 'Electronics',
    productCount: 3,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SupplierService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(SupplierService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAll() sends GET to correct URL and returns suppliers', () => {
    const mockSuppliers: Supplier[] = [mockSupplier, { ...mockSupplier, id: 2, name: 'Beta Ltd' }];

    service.getAll().subscribe(suppliers => {
      expect(suppliers).toHaveLength(2);
      expect(suppliers[0].name).toBe('ACME Corp');
      expect(suppliers[1].name).toBe('Beta Ltd');
    });

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('GET');
    req.flush(mockSuppliers);
  });

  it('getAll() returns empty array when no suppliers', () => {
    service.getAll().subscribe(suppliers => {
      expect(suppliers).toHaveLength(0);
    });

    const req = httpMock.expectOne(API);
    req.flush([]);
  });

  it('getById() sends GET to correct URL with id', () => {
    service.getById(1).subscribe(supplier => {
      expect(supplier.id).toBe(1);
      expect(supplier.name).toBe('ACME Corp');
      expect(supplier.cnpjCpf).toBe('12.345.678/0001-99');
    });

    const req = httpMock.expectOne(`${API}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockSupplier);
  });

  it('create() sends POST with supplier body', () => {
    const newSupplier: Supplier = { name: 'New Supplier', cnpjCpf: '98.765.432/0001-11' };

    service.create(newSupplier).subscribe(supplier => {
      expect(supplier.id).toBe(1);
    });

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newSupplier);
    req.flush(mockSupplier);
  });

  it('update() sends PUT to correct URL with supplier body', () => {
    const updatedSupplier: Supplier = { ...mockSupplier, name: 'Updated Corp' };

    service.update(1, updatedSupplier).subscribe(supplier => {
      expect(supplier.id).toBe(1);
    });

    const req = httpMock.expectOne(`${API}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedSupplier);
    req.flush(mockSupplier);
  });

  it('delete() sends DELETE to correct URL', () => {
    service.delete(1).subscribe(() => {
      // void response - no value to assert
    });

    const req = httpMock.expectOne(`${API}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
