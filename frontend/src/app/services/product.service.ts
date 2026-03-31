import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/models';

const API = 'http://localhost:8080/api/products';

@Injectable({ providedIn: 'root' })
export class ProductService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(API);
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${API}/${id}`);
  }

  getBySupplierId(supplierId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${API}/supplier/${supplierId}`);
  }

  getLowStock(): Observable<Product[]> {
    return this.http.get<Product[]>(`${API}/low-stock`);
  }

  create(product: Product): Observable<Product> {
    return this.http.post<Product>(API, product);
  }

  update(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${API}/${id}`, product);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/${id}`);
  }
}
