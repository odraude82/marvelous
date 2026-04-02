import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BudgetService } from '../../services/budget.service';
import { BudgetGroup } from '../../models/models';
import { BudgetGroupFormDialogComponent } from './budget-group-form-dialog.component';
import { BudgetQuoteFormDialogComponent } from './budget-quote-form-dialog.component';

@Component({
  selector: 'app-budgets',
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
    MatExpansionModule,
    MatChipsModule,
    MatTooltipModule
  ],
  templateUrl: './budgets.component.html',
  styleUrl: './budgets.component.scss'
})
export class BudgetsComponent implements OnInit {
  groups: BudgetGroup[] = [];
  loading = false;
  displayedColumns = ['supplier', 'unitPrice', 'quantity', 'notes', 'best', 'actions'];

  constructor(
    private budgetService: BudgetService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.budgetService.getAll().subscribe({
      next: data => { this.groups = data; this.loading = false; },
      error: () => { this.loading = false; this.showError('Erro ao carregar orçamentos.'); }
    });
  }

  openGroupForm(group?: BudgetGroup): void {
    const dialogRef = this.dialog.open(BudgetGroupFormDialogComponent, {
      width: '480px',
      data: { group }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.load();
    });
  }

  openAddQuote(group: BudgetGroup): void {
    const dialogRef = this.dialog.open(BudgetQuoteFormDialogComponent, {
      width: '480px',
      data: { groupId: group.id }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.load();
    });
  }

  deleteGroup(group: BudgetGroup): void {
    if (!confirm(`Deseja excluir a comparação "${group.title}" e todos os seus orçamentos?`)) return;
    this.budgetService.delete(group.id!).subscribe({
      next: () => { this.showSuccess('Comparação excluída!'); this.load(); },
      error: () => this.showError('Erro ao excluir comparação.')
    });
  }

  deleteQuote(quoteId: number): void {
    if (!confirm('Deseja remover este orçamento?')) return;
    this.budgetService.deleteQuote(quoteId).subscribe({
      next: () => { this.showSuccess('Orçamento removido!'); this.load(); },
      error: () => this.showError('Erro ao remover orçamento.')
    });
  }

  isBestQuote(group: BudgetGroup, quoteId?: number): boolean {
    return !!group.bestQuoteId && group.bestQuoteId === quoteId;
  }

  private showSuccess(msg: string): void {
    this.snackBar.open(msg, 'OK', { duration: 3000 });
  }

  private showError(msg: string): void {
    this.snackBar.open(msg, 'Fechar', { duration: 5000 });
  }
}
