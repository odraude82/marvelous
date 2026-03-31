import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Supplier } from '../models/models';

const API = 'http://localhost:8080/api/suppliers';

@Injectable({ providedIn: 'root' })
export class SupplierService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Supplier[]> {
    return this.http.get<Supplier[]>(API);
  }

  getById(id: number): Observable<Supplier> {
    return this.http.get<Supplier>(`${API}/${id}`);
  }

  create(supplier: Supplier): Observable<Supplier> {
    return this.http.post<Supplier>(API, supplier);
  }

  update(id: number, supplier: Supplier): Observable<Supplier> {
    return this.http.put<Supplier>(`${API}/${id}`, supplier);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/${id}`);
  }
}
