import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { SupplierService } from '../../services/supplier.service';
import { Supplier } from '../../models/models';
import { SupplierFormDialogComponent } from './supplier-form-dialog.component';

@Component({
  selector: 'app-suppliers',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
    MatProgressBarModule,
    MatTooltipModule
  ],
  templateUrl: './suppliers.component.html',
  styleUrl: './suppliers.component.scss'
})
export class SuppliersComponent implements OnInit {
  suppliers: Supplier[] = [];
  displayedColumns = ['name', 'cnpjCpf', 'contact', 'productCategory', 'productCount', 'actions'];
  loading = false;

  constructor(
    private supplierService: SupplierService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.supplierService.getAll().subscribe({
      next: data => { this.suppliers = data; this.loading = false; },
      error: () => { this.loading = false; this.showError('Erro ao carregar fornecedores.'); }
    });
  }

  openForm(supplier?: Supplier): void {
    const dialogRef = this.dialog.open(SupplierFormDialogComponent, {
      width: '500px',
      data: supplier ? { ...supplier } : null
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.load();
    });
  }

  delete(supplier: Supplier): void {
    if (!confirm(`Deseja excluir o fornecedor "${supplier.name}"?`)) return;
    this.supplierService.delete(supplier.id!).subscribe({
      next: () => { this.showSuccess('Fornecedor excluído!'); this.load(); },
      error: () => this.showError('Erro ao excluir fornecedor.')
    });
  }

  private showSuccess(msg: string): void {
    this.snackBar.open(msg, 'OK', { duration: 3000, panelClass: ['snack-success'] });
  }

  private showError(msg: string): void {
    this.snackBar.open(msg, 'Fechar', { duration: 5000, panelClass: ['snack-error'] });
  }
}
