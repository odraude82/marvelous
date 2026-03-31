import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { SupplierService } from '../../services/supplier.service';
import { Supplier } from '../../models/models';

@Component({
  selector: 'app-supplier-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  template: `
    <h2 mat-dialog-title>{{ isEdit ? 'Editar' : 'Novo' }} Fornecedor</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="dialog-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nome *</mat-label>
          <input matInput formControlName="name" placeholder="Nome do fornecedor">
          @if (form.get('name')?.hasError('required') && form.get('name')?.touched) {
            <mat-error>Nome é obrigatório</mat-error>
          }
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>CNPJ/CPF</mat-label>
          <input matInput formControlName="cnpjCpf" placeholder="00.000.000/0000-00">
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Contato</mat-label>
          <input matInput formControlName="contact" placeholder="(00) 0000-0000">
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Categoria de Produtos</mat-label>
          <input matInput formControlName="productCategory" placeholder="Ex: Alimentos, Escritório...">
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button color="primary" (click)="save()" [disabled]="form.invalid || saving">
        {{ saving ? 'Salvando...' : 'Salvar' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`.dialog-form { display: flex; flex-direction: column; gap: 4px; padding-top: 8px; }
            .full-width { width: 100%; }`]
})
export class SupplierFormDialogComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  saving = false;

  constructor(
    private fb: FormBuilder,
    private supplierService: SupplierService,
    private dialogRef: MatDialogRef<SupplierFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Supplier | null,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.isEdit = !!this.data;
    this.form = this.fb.group({
      name: [this.data?.name || '', Validators.required],
      cnpjCpf: [this.data?.cnpjCpf || ''],
      contact: [this.data?.contact || ''],
      productCategory: [this.data?.productCategory || '']
    });
  }

  save(): void {
    if (this.form.invalid) return;
    this.saving = true;
    const supplier: Supplier = this.form.value;
    const obs = this.isEdit
      ? this.supplierService.update(this.data!.id!, supplier)
      : this.supplierService.create(supplier);
    obs.subscribe({
      next: () => {
        this.snackBar.open(this.isEdit ? 'Fornecedor atualizado!' : 'Fornecedor cadastrado!', 'OK', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: () => {
        this.snackBar.open('Erro ao salvar fornecedor.', 'Fechar', { duration: 5000 });
        this.saving = false;
      }
    });
  }
}
