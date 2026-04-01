import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ProductService } from './product.service';
import { Product } from '../models/models';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  const API = 'http://localhost:8080/api/products';

  const mockProduct: Product = {
    id: 1,
    name: 'Widget',
    costPrice: 9.99,
    stockQuantity: 100,
    unitOfMeasure: 'un',
    minStockAlert: 5,
    supplierId: 10,
    supplierName: 'ACME Corp',
    lowStock: false,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ProductService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAll() sends GET to correct URL and returns products', () => {
    const mockProducts: Product[] = [mockProduct, { ...mockProduct, id: 2, name: 'Gadget' }];

    service.getAll().subscribe(products => {
      expect(products).toHaveLength(2);
      expect(products[0].name).toBe('Widget');
      expect(products[1].name).toBe('Gadget');
    });

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('GET');
    req.flush(mockProducts);
  });

  it('getById() sends GET to correct URL with id', () => {
    service.getById(1).subscribe(product => {
      expect(product.id).toBe(1);
      expect(product.name).toBe('Widget');
    });

    const req = httpMock.expectOne(`${API}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProduct);
  });

  it('getBySupplierId() sends GET to supplier endpoint', () => {
    service.getBySupplierId(10).subscribe(products => {
      expect(products).toHaveLength(1);
      expect(products[0].supplierId).toBe(10);
    });

    const req = httpMock.expectOne(`${API}/supplier/10`);
    expect(req.request.method).toBe('GET');
    req.flush([mockProduct]);
  });

  it('getLowStock() sends GET to low-stock endpoint', () => {
    const lowStockProduct = { ...mockProduct, lowStock: true, stockQuantity: 2 };

    service.getLowStock().subscribe(products => {
      expect(products[0].lowStock).toBe(true);
    });

    const req = httpMock.expectOne(`${API}/low-stock`);
    expect(req.request.method).toBe('GET');
    req.flush([lowStockProduct]);
  });

  it('create() sends POST with product body', () => {
    const newProduct: Product = { name: 'New', costPrice: 5.0, stockQuantity: 10, unitOfMeasure: 'kg' };

    service.create(newProduct).subscribe(product => {
      expect(product.id).toBe(1);
    });

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newProduct);
    req.flush(mockProduct);
  });

  it('update() sends PUT to correct URL with product body', () => {
    const updatedProduct: Product = { ...mockProduct, name: 'Updated Widget' };

    service.update(1, updatedProduct).subscribe(product => {
      expect(product.name).toBe('Widget');
    });

    const req = httpMock.expectOne(`${API}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedProduct);
    req.flush(mockProduct);
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
