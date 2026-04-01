import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAnimations } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { DashboardComponent } from './dashboard.component';
import { DashboardService } from '../../services/dashboard.service';
import { DashboardData } from '../../models/models';

const mockDashboard: DashboardData = {
  totalStockValue: 1500.0,
  totalProducts: 10,
  totalSuppliers: 3,
  lowStockCount: 2,
  lowStockItems: [],
  topSuppliers: [
    { supplierId: 1, supplierName: 'Vendor A', totalSpent: 1000 },
    { supplierId: 2, supplierName: 'Vendor B', totalSpent: 400 },
  ],
  recentPriceVariations: [],
};

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let dashboardServiceSpy: { getDashboard: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    dashboardServiceSpy = { getDashboard: vi.fn().mockReturnValue(of(mockDashboard)) };

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideAnimations(),
        { provide: DashboardService, useValue: dashboardServiceSpy },
      ],
    }).compileComponents();

    const fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('loads dashboard data on init', () => {
    expect(dashboardServiceSpy.getDashboard).toHaveBeenCalledTimes(1);
    expect(component.dashboard).toEqual(mockDashboard);
    expect(component.loading).toBe(false);
    expect(component.error).toBe('');
  });

  describe('getVariationClass()', () => {
    it('returns variation-up for positive values', () => {
      expect(component.getVariationClass(5)).toBe('variation-up');
      expect(component.getVariationClass(0.01)).toBe('variation-up');
    });

    it('returns variation-down for negative values', () => {
      expect(component.getVariationClass(-3)).toBe('variation-down');
      expect(component.getVariationClass(-0.01)).toBe('variation-down');
    });

    it('returns variation-neutral for zero', () => {
      expect(component.getVariationClass(0)).toBe('variation-neutral');
    });
  });

  describe('getVariationIcon()', () => {
    it('returns trending_up for positive values', () => {
      expect(component.getVariationIcon(10)).toBe('trending_up');
    });

    it('returns trending_down for negative values', () => {
      expect(component.getVariationIcon(-5)).toBe('trending_down');
    });

    it('returns trending_flat for zero', () => {
      expect(component.getVariationIcon(0)).toBe('trending_flat');
    });
  });

  describe('getSupplierBarWidth()', () => {
    it('returns 100 for the top supplier (max)', () => {
      expect(component.getSupplierBarWidth(1000)).toBe(100);
    });

    it('returns proportional width for other suppliers', () => {
      expect(component.getSupplierBarWidth(400)).toBe(40);
    });

    it('returns 0 when dashboard is null', () => {
      component.dashboard = null;
      expect(component.getSupplierBarWidth(500)).toBe(0);
    });

    it('returns 0 when topSuppliers is empty', () => {
      component.dashboard = { ...mockDashboard, topSuppliers: [] };
      expect(component.getSupplierBarWidth(500)).toBe(0);
    });

    it('returns 0 when max spent is 0', () => {
      component.dashboard = {
        ...mockDashboard,
        topSuppliers: [{ supplierId: 1, supplierName: 'Zero', totalSpent: 0 }],
      };
      expect(component.getSupplierBarWidth(0)).toBe(0);
    });
  });

  it('sets error message on load failure', async () => {
    dashboardServiceSpy.getDashboard.mockReturnValue(throwError(() => new Error('Network error')));

    component.load();

    expect(component.error).toContain('Erro ao carregar dashboard');
    expect(component.loading).toBe(false);
  });

  it('sets loading to true then false during successful load', () => {
    dashboardServiceSpy.getDashboard.mockReturnValue(of(mockDashboard));

    component.load();

    expect(component.loading).toBe(false);
    expect(component.dashboard).toEqual(mockDashboard);
  });
});
