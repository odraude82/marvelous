export interface Supplier {
  id?: number;
  name: string;
  cnpjCpf?: string;
  contact?: string;
  productCategory?: string;
  createdAt?: string;
  productCount?: number;
}

export interface Product {
  id?: number;
  name: string;
  costPrice: number;
  stockQuantity: number;
  unitOfMeasure: string;
  minStockAlert?: number;
  supplierId?: number;
  supplierName?: string;
  createdAt?: string;
  totalValue?: number;
  lowStock?: boolean;
}

export interface Purchase {
  id?: number;
  productId: number;
  productName?: string;
  supplierId: number;
  supplierName?: string;
  quantity: number;
  unitPrice: number;
  totalAmount?: number;
  purchaseDate?: string;
  notes?: string;
  createdAt?: string;
}

export interface DashboardData {
  totalStockValue: number;
  totalProducts: number;
  totalSuppliers: number;
  lowStockCount: number;
  lowStockItems: LowStockItem[];
  topSuppliers: SupplierSpending[];
  recentPriceVariations: PriceVariation[];
}

export interface LowStockItem {
  productId: number;
  productName: string;
  currentStock: number;
  minStock: number;
  unitOfMeasure: string;
  supplierName: string;
}

export interface SupplierSpending {
  supplierId: number;
  supplierName: string;
  totalSpent: number;
}

export interface PriceVariation {
  productId: number;
  productName: string;
  previousPrice: number;
  currentPrice: number;
  variationPercent: number;
  supplierName: string;
}
