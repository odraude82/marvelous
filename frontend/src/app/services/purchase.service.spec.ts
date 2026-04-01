import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { PurchaseService } from './purchase.service';
import { Purchase } from '../models/models';

describe('PurchaseService', () => {
  let service: PurchaseService;
  let httpMock: HttpTestingController;

  const API = 'http://localhost:8080/api/purchases';

  const mockPurchase: Purchase = {
    id: 100,
    productId: 1,
    productName: 'Widget',
    supplierId: 10,
    supplierName: 'ACME Corp',
    quantity: 20,
    unitPrice: 4.5,
    totalAmount: 90.0,
    purchaseDate: '2024-01-15',
    notes: 'Bulk order',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PurchaseService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(PurchaseService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAll() sends GET to correct URL and returns purchases', () => {
    const mockPurchases: Purchase[] = [mockPurchase, { ...mockPurchase, id: 200, quantity: 5 }];

    service.getAll().subscribe(purchases => {
      expect(purchases).toHaveLength(2);
      expect(purchases[0].id).toBe(100);
      expect(purchases[1].id).toBe(200);
    });

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('GET');
    req.flush(mockPurchases);
  });

  it('getAll() returns empty array when no purchases', () => {
    service.getAll().subscribe(purchases => {
      expect(purchases).toHaveLength(0);
    });

    const req = httpMock.expectOne(API);
    req.flush([]);
  });

  it('getById() sends GET to correct URL with id', () => {
    service.getById(100).subscribe(purchase => {
      expect(purchase.id).toBe(100);
      expect(purchase.productName).toBe('Widget');
      expect(purchase.totalAmount).toBe(90.0);
    });

    const req = httpMock.expectOne(`${API}/100`);
    expect(req.request.method).toBe('GET');
    req.flush(mockPurchase);
  });

  it('getByProductId() sends GET to product endpoint', () => {
    service.getByProductId(1).subscribe(purchases => {
      expect(purchases).toHaveLength(1);
      expect(purchases[0].productId).toBe(1);
    });

    const req = httpMock.expectOne(`${API}/product/1`);
    expect(req.request.method).toBe('GET');
    req.flush([mockPurchase]);
  });

  it('create() sends POST with purchase body', () => {
    const newPurchase: Purchase = { productId: 1, supplierId: 10, quantity: 5, unitPrice: 4.5 };

    service.create(newPurchase).subscribe(purchase => {
      expect(purchase.id).toBe(100);
    });

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newPurchase);
    req.flush(mockPurchase);
  });

  it('delete() sends DELETE to correct URL', () => {
    service.delete(100).subscribe(() => {
      // void response - no value to assert
    });

    const req = httpMock.expectOne(`${API}/100`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
