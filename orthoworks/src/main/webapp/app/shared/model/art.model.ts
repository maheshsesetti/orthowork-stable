import { ICollection } from 'app/shared/model/collection.model';
import { AssetType } from 'app/shared/model/enumerations/asset-type.model';
import { Type } from 'app/shared/model/enumerations/type.model';

export interface IArt {
  id?: number;
  name?: string;
  handle?: string;
  assetType?: AssetType;
  type?: Type | null;
  collections?: ICollection[] | null;
}

export const defaultValue: Readonly<IArt> = {};
