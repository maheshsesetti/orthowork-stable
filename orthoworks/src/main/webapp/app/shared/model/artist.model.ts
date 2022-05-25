import { Gender } from 'app/shared/model/enumerations/gender.model';

export interface IArtist {
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
}

export const defaultValue: Readonly<IArtist> = {};
