import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DecimalPipe, PercentPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatBadgeModule } from '@angular/material/badge';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { DashboardService } from '../../services/dashboard.service';
import { DashboardData } from '../../models/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    DecimalPipe,
    MatCardModule,
    MatIconModule,
    MatTableModule,
    MatBadgeModule,
    MatProgressBarModule,
    MatChipsModule,
    MatDividerModule,
    MatTooltipModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  dashboard: DashboardData | null = null;
  loading = true;
  error = '';

  lowStockColumns = ['name', 'current', 'min', 'unit', 'supplier'];
  supplierColumns = ['rank', 'name', 'spent'];
  priceColumns = ['product', 'previous', 'current', 'variation', 'supplier'];

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.dashboardService.getDashboard().subscribe({
      next: data => {
        this.dashboard = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Erro ao carregar dashboard. Verifique se o servidor está online.';
        this.loading = false;
      }
    });
  }

  getVariationClass(v: number): string {
    if (v > 0) return 'variation-up';
    if (v < 0) return 'variation-down';
    return 'variation-neutral';
  }

  getVariationIcon(v: number): string {
    if (v > 0) return 'trending_up';
    if (v < 0) return 'trending_down';
    return 'trending_flat';
  }

  getSupplierBarWidth(spent: number): number {
    if (!this.dashboard?.topSuppliers?.length) return 0;
    const max = this.dashboard.topSuppliers[0].totalSpent;
    return max > 0 ? (spent / max) * 100 : 0;
  }
}
