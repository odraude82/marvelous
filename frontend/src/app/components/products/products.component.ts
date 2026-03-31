import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ProductService } from '../../services/product.service';
import { SupplierService } from '../../services/supplier.service';
import { Product, Supplier } from '../../models/models';
import { ProductFormDialogComponent } from './product-form-dialog.component';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressBarModule,
    MatChipsModule,
    MatTooltipModule
  ],
  templateUrl: './products.component.html',
  styleUrl: './products.component.scss'
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  suppliers: Supplier[] = [];
  displayedColumns = ['name', 'supplier', 'costPrice', 'stock', 'unit', 'totalValue', 'status', 'actions'];
  loading = false;

  constructor(
    private productService: ProductService,
    private supplierService: SupplierService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadSuppliers();
    this.load();
  }

  load(): void {
    this.loading = true;
    this.productService.getAll().subscribe({
      next: data => { this.products = data; this.loading = false; },
      error: () => { this.loading = false; this.showError('Erro ao carregar produtos.'); }
    });
  }

  loadSuppliers(): void {
    this.supplierService.getAll().subscribe({
      next: data => this.suppliers = data
    });
  }

  openForm(product?: Product): void {
    const dialogRef = this.dialog.open(ProductFormDialogComponent, {
      width: '500px',
      data: { product: product ? { ...product } : null, suppliers: this.suppliers }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.load();
    });
  }

  delete(product: Product): void {
    if (!confirm(`Deseja excluir o produto "${product.name}"?`)) return;
    this.productService.delete(product.id!).subscribe({
      next: () => { this.showSuccess('Produto excluído!'); this.load(); },
      error: () => this.showError('Erro ao excluir produto.')
    });
  }

  private showSuccess(msg: string): void {
    this.snackBar.open(msg, 'OK', { duration: 3000 });
  }

  private showError(msg: string): void {
    this.snackBar.open(msg, 'Fechar', { duration: 5000 });
  }
}
