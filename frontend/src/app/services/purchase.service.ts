import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Purchase } from '../models/models';

const API = 'http://localhost:8080/api/purchases';

@Injectable({ providedIn: 'root' })
export class PurchaseService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Purchase[]> {
    return this.http.get<Purchase[]>(API);
  }

  getById(id: number): Observable<Purchase> {
    return this.http.get<Purchase>(`${API}/${id}`);
  }

  getByProductId(productId: number): Observable<Purchase[]> {
    return this.http.get<Purchase[]>(`${API}/product/${productId}`);
  }

  create(purchase: Purchase): Observable<Purchase> {
    return this.http.post<Purchase>(API, purchase);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/${id}`);
  }
}
