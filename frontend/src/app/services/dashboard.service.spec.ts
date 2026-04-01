import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { DashboardService } from './dashboard.service';
import { DashboardData } from '../models/models';

describe('DashboardService', () => {
  let service: DashboardService;
  let httpMock: HttpTestingController;

  const API = 'http://localhost:8080/api/dashboard';

  const mockDashboard: DashboardData = {
    totalStockValue: 1500.0,
    totalProducts: 10,
    totalSuppliers: 3,
    lowStockCount: 2,
    lowStockItems: [
      {
        productId: 3,
        productName: 'Low Product',
        currentStock: 1,
        minStock: 5,
        unitOfMeasure: 'kg',
        supplierName: 'Vendor',
      },
    ],
    topSuppliers: [
      {
        supplierId: 10,
        supplierName: 'Top Vendor',
        totalSpent: 500.0,
      },
    ],
    recentPriceVariations: [
      {
        productId: 1,
        productName: 'Widget',
        previousPrice: 10.0,
        currentPrice: 12.0,
        variationPercent: 20.0,
        supplierName: 'Supplier A',
      },
    ],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DashboardService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(DashboardService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getDashboard() sends GET to correct URL and returns dashboard data', () => {
    service.getDashboard().subscribe(data => {
      expect(data.totalProducts).toBe(10);
      expect(data.totalSuppliers).toBe(3);
      expect(data.totalStockValue).toBe(1500.0);
      expect(data.lowStockCount).toBe(2);
      expect(data.lowStockItems).toHaveLength(1);
      expect(data.topSuppliers).toHaveLength(1);
      expect(data.recentPriceVariations).toHaveLength(1);
    });

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('GET');
    req.flush(mockDashboard);
  });

  it('getDashboard() returns correctly structured low stock items', () => {
    service.getDashboard().subscribe(data => {
      const item = data.lowStockItems[0];
      expect(item.productId).toBe(3);
      expect(item.productName).toBe('Low Product');
      expect(item.currentStock).toBe(1);
      expect(item.minStock).toBe(5);
      expect(item.supplierName).toBe('Vendor');
    });

    const req = httpMock.expectOne(API);
    req.flush(mockDashboard);
  });

  it('getDashboard() returns correctly structured top suppliers', () => {
    service.getDashboard().subscribe(data => {
      const supplier = data.topSuppliers[0];
      expect(supplier.supplierId).toBe(10);
      expect(supplier.supplierName).toBe('Top Vendor');
      expect(supplier.totalSpent).toBe(500.0);
    });

    const req = httpMock.expectOne(API);
    req.flush(mockDashboard);
  });

  it('getDashboard() returns correctly structured price variations', () => {
    service.getDashboard().subscribe(data => {
      const variation = data.recentPriceVariations[0];
      expect(variation.productId).toBe(1);
      expect(variation.previousPrice).toBe(10.0);
      expect(variation.currentPrice).toBe(12.0);
      expect(variation.variationPercent).toBe(20.0);
    });

    const req = httpMock.expectOne(API);
    req.flush(mockDashboard);
  });

  it('getDashboard() handles empty dashboard response', () => {
    const emptyDashboard: DashboardData = {
      totalStockValue: 0,
      totalProducts: 0,
      totalSuppliers: 0,
      lowStockCount: 0,
      lowStockItems: [],
      topSuppliers: [],
      recentPriceVariations: [],
    };

    service.getDashboard().subscribe(data => {
      expect(data.totalProducts).toBe(0);
      expect(data.lowStockItems).toHaveLength(0);
      expect(data.topSuppliers).toHaveLength(0);
    });

    const req = httpMock.expectOne(API);
    req.flush(emptyDashboard);
  });
});
