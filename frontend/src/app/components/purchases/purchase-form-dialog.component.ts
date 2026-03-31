import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { PurchaseService } from '../../services/purchase.service';
import { Purchase, Product, Supplier } from '../../models/models';

@Component({
  selector: 'app-purchase-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule
  ],
  template: `
    <h2 mat-dialog-title>Registrar Compra</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="dialog-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Produto *</mat-label>
          <mat-select formControlName="productId">
            @for (p of data.products; track p.id) {
              <mat-option [value]="p.id">{{ p.name }}</mat-option>
            }
          </mat-select>
          @if (form.get('productId')?.hasError('required') && form.get('productId')?.touched) {
            <mat-error>Produto é obrigatório</mat-error>
          }
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Fornecedor *</mat-label>
          <mat-select formControlName="supplierId">
            @for (s of data.suppliers; track s.id) {
              <mat-option [value]="s.id">{{ s.name }}</mat-option>
            }
          </mat-select>
          @if (form.get('supplierId')?.hasError('required') && form.get('supplierId')?.touched) {
            <mat-error>Fornecedor é obrigatório</mat-error>
          }
        </mat-form-field>
        <div class="row-2col">
          <mat-form-field appearance="outline">
            <mat-label>Quantidade *</mat-label>
            <input matInput type="number" formControlName="quantity" min="1">
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Preço Unitário (R$) *</mat-label>
            <input matInput type="number" step="0.01" formControlName="unitPrice" min="0.01">
          </mat-form-field>
        </div>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Data da Compra</mat-label>
          <input matInput [matDatepicker]="picker" formControlName="purchaseDate">
          <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
          <mat-datepicker #picker></mat-datepicker>
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Observações</mat-label>
          <textarea matInput formControlName="notes" rows="2"></textarea>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button color="primary" (click)="save()" [disabled]="form.invalid || saving">
        {{ saving ? 'Salvando...' : 'Registrar' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [
    `.dialog-form { display: flex; flex-direction: column; gap: 4px; padding-top: 8px; }
     .full-width { width: 100%; }
     .row-2col { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }`
  ]
})
export class PurchaseFormDialogComponent implements OnInit {
  form!: FormGroup;
  saving = false;

  constructor(
    private fb: FormBuilder,
    private purchaseService: PurchaseService,
    private dialogRef: MatDialogRef<PurchaseFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { products: Product[]; suppliers: Supplier[] },
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      productId: [null, Validators.required],
      supplierId: [null, Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      unitPrice: ['', [Validators.required, Validators.min(0.01)]],
      purchaseDate: [new Date()],
      notes: ['']
    });
  }

  save(): void {
    if (this.form.invalid) return;
    this.saving = true;
    const value = this.form.value;
    const purchase: Purchase = {
      ...value,
      purchaseDate: value.purchaseDate
        ? new Date(value.purchaseDate).toISOString().split('T')[0]
        : null
    };
    this.purchaseService.create(purchase).subscribe({
      next: () => {
        this.snackBar.open('Compra registrada com sucesso!', 'OK', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: () => {
        this.snackBar.open('Erro ao registrar compra.', 'Fechar', { duration: 5000 });
        this.saving = false;
      }
    });
  }
}
