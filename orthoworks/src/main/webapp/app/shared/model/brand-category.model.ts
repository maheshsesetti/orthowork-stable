import dayjs from 'dayjs';
import { IBrand } from 'app/shared/model/brand.model';

export interface IBrandCategory {
  id?: number;
  description?: string;
  sortOrder?: number | null;
  dateAdded?: string | null;
  dateModified?: string | null;
  parent?: IBrandCategory | null;
  brands?: IBrand[] | null;
}

export const defaultValue: Readonly<IBrandCategory> = {};
