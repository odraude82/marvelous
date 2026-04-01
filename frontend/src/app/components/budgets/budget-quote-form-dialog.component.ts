import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BudgetService } from '../../services/budget.service';

@Component({
  selector: 'app-budget-quote-form-dialog',
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
    <h2 mat-dialog-title>Adicionar Orçamento de Fornecedor</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="dialog-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Fornecedor *</mat-label>
          <input matInput formControlName="supplierName" placeholder="Nome do fornecedor">
          @if (form.get('supplierName')?.hasError('required') && form.get('supplierName')?.touched) {
            <mat-error>Nome do fornecedor é obrigatório</mat-error>
          }
        </mat-form-field>
        <div class="row-2col">
          <mat-form-field appearance="outline">
            <mat-label>Preço Unitário (R$) *</mat-label>
            <input matInput type="number" step="0.01" formControlName="unitPrice" min="0.01">
            @if (form.get('unitPrice')?.hasError('required') && form.get('unitPrice')?.touched) {
              <mat-error>Preço é obrigatório</mat-error>
            }
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Quantidade</mat-label>
            <input matInput type="number" formControlName="quantity" min="1">
          </mat-form-field>
        </div>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Observações</mat-label>
          <textarea matInput formControlName="notes" rows="2"></textarea>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button color="primary" (click)="save()" [disabled]="form.invalid || saving">
        {{ saving ? 'Salvando...' : 'Adicionar' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [
    `.dialog-form { display: flex; flex-direction: column; gap: 4px; padding-top: 8px; }
     .full-width { width: 100%; }
     .row-2col { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }`
  ]
})
export class BudgetQuoteFormDialogComponent implements OnInit {
  form!: FormGroup;
  saving = false;

  constructor(
    private fb: FormBuilder,
    private budgetService: BudgetService,
    private dialogRef: MatDialogRef<BudgetQuoteFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { groupId: number },
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      supplierName: ['', Validators.required],
      unitPrice: ['', [Validators.required, Validators.min(0.01)]],
      quantity: [null],
      notes: ['']
    });
  }

  save(): void {
    if (this.form.invalid) return;
    this.saving = true;
    this.budgetService.addQuote(this.data.groupId, this.form.value).subscribe({
      next: () => {
        this.snackBar.open('Orçamento adicionado!', 'OK', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: () => {
        this.snackBar.open('Erro ao adicionar orçamento.', 'Fechar', { duration: 5000 });
        this.saving = false;
      }
    });
  }
}
