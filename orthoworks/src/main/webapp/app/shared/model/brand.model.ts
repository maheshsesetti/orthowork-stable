import dayjs from 'dayjs';
import { IBrandCategory } from 'app/shared/model/brand-category.model';
import { BrandStatus } from 'app/shared/model/enumerations/brand-status.model';
import { Size } from 'app/shared/model/enumerations/size.model';

export interface IBrand {
  id?: number;
  title?: string;
  keywords?: string | null;
  description?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  rating?: number | null;
  status?: BrandStatus | null;
  price?: number;
  brandSize?: Size;
  dateAdded?: string | null;
  dateModified?: string | null;
  categories?: IBrandCategory[] | null;
}

export const defaultValue: Readonly<IBrand> = {};
