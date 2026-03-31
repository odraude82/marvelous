import { Routes } from '@angular/router';
import { LayoutComponent } from './components/layout/layout.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'suppliers',
        loadComponent: () => import('./components/suppliers/suppliers.component').then(m => m.SuppliersComponent)
      },
      {
        path: 'products',
        loadComponent: () => import('./components/products/products.component').then(m => m.ProductsComponent)
      },
      {
        path: 'purchases',
        loadComponent: () => import('./components/purchases/purchases.component').then(m => m.PurchasesComponent)
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
