import { IAddress } from 'app/shared/model/address.model';
import { Gender } from 'app/shared/model/enumerations/gender.model';

export interface ICustomer {
  id?: number;
  firstName?: string;
  lastName?: string;
  gender?: Gender;
  email?: string;
  phone?: string;
  addressLine1?: string;
  addressLine2?: string | null;
  city?: string;
  country?: string;
  addresses?: IAddress[] | null;
}

export const defaultValue: Readonly<ICustomer> = {};
