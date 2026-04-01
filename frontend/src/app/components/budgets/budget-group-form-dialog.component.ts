import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BudgetService } from '../../services/budget.service';
import { BudgetGroup } from '../../models/models';

@Component({
  selector: 'app-budget-group-form-dialog',
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
    <h2 mat-dialog-title>{{ data?.group ? 'Editar Comparação' : 'Nova Comparação de Orçamentos' }}</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="dialog-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Título *</mat-label>
          <input matInput formControlName="title" placeholder="Ex: Compra de Papel A4">
          @if (form.get('title')?.hasError('required') && form.get('title')?.touched) {
            <mat-error>Título é obrigatório</mat-error>
          }
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Descrição</mat-label>
          <textarea matInput formControlName="description" rows="2" placeholder="Detalhes do item a ser orçado"></textarea>
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
  styles: [
    `.dialog-form { display: flex; flex-direction: column; gap: 4px; padding-top: 8px; }
     .full-width { width: 100%; }`
  ]
})
export class BudgetGroupFormDialogComponent implements OnInit {
  form!: FormGroup;
  saving = false;

  constructor(
    private fb: FormBuilder,
    private budgetService: BudgetService,
    private dialogRef: MatDialogRef<BudgetGroupFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { group?: BudgetGroup } | null,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      title: [this.data?.group?.title ?? '', Validators.required],
      description: [this.data?.group?.description ?? '']
    });
  }

  save(): void {
    if (this.form.invalid) return;
    this.saving = true;
    const value = this.form.value;
    const action = this.data?.group?.id
      ? this.budgetService.update(this.data.group.id, value)
      : this.budgetService.create(value);

    action.subscribe({
      next: () => {
        this.snackBar.open('Comparação salva com sucesso!', 'OK', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: () => {
        this.snackBar.open('Erro ao salvar comparação.', 'Fechar', { duration: 5000 });
        this.saving = false;
      }
    });
  }
}
