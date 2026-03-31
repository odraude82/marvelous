import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { PurchaseService } from '../../services/purchase.service';
import { ProductService } from '../../services/product.service';
import { SupplierService } from '../../services/supplier.service';
import { Purchase, Product, Supplier } from '../../models/models';
import { PurchaseFormDialogComponent } from './purchase-form-dialog.component';

@Component({
  selector: 'app-purchases',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    DatePipe,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressBarModule,
    MatTooltipModule
  ],
  templateUrl: './purchases.component.html',
  styleUrl: './purchases.component.scss'
})
export class PurchasesComponent implements OnInit {
  purchases: Purchase[] = [];
  products: Product[] = [];
  suppliers: Supplier[] = [];
  displayedColumns = ['date', 'product', 'supplier', 'qty', 'unitPrice', 'total', 'notes', 'actions'];
  loading = false;

  constructor(
    private purchaseService: PurchaseService,
    private productService: ProductService,
    private supplierService: SupplierService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadMeta();
    this.load();
  }

  load(): void {
    this.loading = true;
    this.purchaseService.getAll().subscribe({
      next: data => { this.purchases = data; this.loading = false; },
      error: () => { this.loading = false; this.showError('Erro ao carregar compras.'); }
    });
  }

  loadMeta(): void {
    this.productService.getAll().subscribe({ next: d => this.products = d });
    this.supplierService.getAll().subscribe({ next: d => this.suppliers = d });
  }

  openForm(): void {
    const dialogRef = this.dialog.open(PurchaseFormDialogComponent, {
      width: '520px',
      data: { products: this.products, suppliers: this.suppliers }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.load();
    });
  }

  delete(purchase: Purchase): void {
    if (!confirm('Deseja excluir este registro de compra?')) return;
    this.purchaseService.delete(purchase.id!).subscribe({
      next: () => { this.showSuccess('Compra excluída!'); this.load(); },
      error: () => this.showError('Erro ao excluir compra.')
    });
  }

  private showSuccess(msg: string): void {
    this.snackBar.open(msg, 'OK', { duration: 3000 });
  }

  private showError(msg: string): void {
    this.snackBar.open(msg, 'Fechar', { duration: 5000 });
  }
}
