import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProductService } from '../../services/product.service';
import { Product, Supplier } from '../../models/models';

@Component({
  selector: 'app-product-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  template: `
    <h2 mat-dialog-title>{{ isEdit ? 'Editar' : 'Novo' }} Produto</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="dialog-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nome *</mat-label>
          <input matInput formControlName="name" placeholder="Nome do produto">
          @if (form.get('name')?.hasError('required') && form.get('name')?.touched) {
            <mat-error>Nome é obrigatório</mat-error>
          }
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Fornecedor</mat-label>
          <mat-select formControlName="supplierId">
            <mat-option [value]="null">— Nenhum —</mat-option>
            @for (s of data.suppliers; track s.id) {
              <mat-option [value]="s.id">{{ s.name }}</mat-option>
            }
          </mat-select>
        </mat-form-field>
        <div class="row-2col">
          <mat-form-field appearance="outline">
            <mat-label>Preço de Custo (R$) *</mat-label>
            <input matInput type="number" step="0.01" formControlName="costPrice">
            @if (form.get('costPrice')?.hasError('required') && form.get('costPrice')?.touched) {
              <mat-error>Obrigatório</mat-error>
            }
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Qtd. em Estoque *</mat-label>
            <input matInput type="number" formControlName="stockQuantity">
          </mat-form-field>
        </div>
        <div class="row-2col">
          <mat-form-field appearance="outline">
            <mat-label>Unidade de Medida *</mat-label>
            <input matInput formControlName="unitOfMeasure" placeholder="Ex: Kg, Un, Cx">
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Alerta Estoque Mínimo</mat-label>
            <input matInput type="number" formControlName="minStockAlert">
          </mat-form-field>
        </div>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button color="primary" (click)="save()" [disabled]="form.invalid || saving">
        {{ saving ? 'Salvando...' : 'Salvar' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [
    `.dialog-form { display: flex; flex-direction: column; gap: 4px; padding-top: 8px; }
     .full-width { width: 100%; }
     .row-2col { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }`
  ]
})
export class ProductFormDialogComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  saving = false;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private dialogRef: MatDialogRef<ProductFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { product: Product | null; suppliers: Supplier[] },
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const p = this.data.product;
    this.isEdit = !!p;
    this.form = this.fb.group({
      name: [p?.name || '', Validators.required],
      supplierId: [p?.supplierId || null],
      costPrice: [p?.costPrice || '', [Validators.required, Validators.min(0.01)]],
      stockQuantity: [p?.stockQuantity ?? 0, [Validators.required, Validators.min(0)]],
      unitOfMeasure: [p?.unitOfMeasure || '', Validators.required],
      minStockAlert: [p?.minStockAlert ?? 5]
    });
  }

  save(): void {
    if (this.form.invalid) return;
    this.saving = true;
    const product: Product = this.form.value;
    const obs = this.isEdit
      ? this.productService.update(this.data.product!.id!, product)
      : this.productService.create(product);
    obs.subscribe({
      next: () => {
        this.snackBar.open(this.isEdit ? 'Produto atualizado!' : 'Produto cadastrado!', 'OK', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: () => {
        this.snackBar.open('Erro ao salvar produto.', 'Fechar', { duration: 5000 });
        this.saving = false;
      }
    });
  }
}
