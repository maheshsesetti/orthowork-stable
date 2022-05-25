import { ICollection } from 'app/shared/model/collection.model';

export interface IFeature {
  id?: number;
  name?: string;
  mandatory?: boolean;
  collection?: ICollection | null;
}

export const defaultValue: Readonly<IFeature> = {
  mandatory: false,
};
