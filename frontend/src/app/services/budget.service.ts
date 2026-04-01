import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BudgetGroup, BudgetQuote } from '../models/models';

const API = 'http://localhost:8080/api/budgets';

@Injectable({ providedIn: 'root' })
export class BudgetService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<BudgetGroup[]> {
    return this.http.get<BudgetGroup[]>(API);
  }

  getById(id: number): Observable<BudgetGroup> {
    return this.http.get<BudgetGroup>(`${API}/${id}`);
  }

  create(group: { title: string; description?: string }): Observable<BudgetGroup> {
    return this.http.post<BudgetGroup>(API, group);
  }

  update(id: number, group: { title: string; description?: string }): Observable<BudgetGroup> {
    return this.http.put<BudgetGroup>(`${API}/${id}`, group);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/${id}`);
  }

  addQuote(groupId: number, quote: { supplierName: string; unitPrice: number; quantity?: number; notes?: string }): Observable<BudgetQuote> {
    return this.http.post<BudgetQuote>(`${API}/${groupId}/quotes`, quote);
  }

  deleteQuote(quoteId: number): Observable<void> {
    return this.http.delete<void>(`${API}/quotes/${quoteId}`);
  }
}
