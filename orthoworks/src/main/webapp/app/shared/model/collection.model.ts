import { IFeature } from 'app/shared/model/feature.model';
import { IArt } from 'app/shared/model/art.model';
import { CollectionType } from 'app/shared/model/enumerations/collection-type.model';
import { AuctionType } from 'app/shared/model/enumerations/auction-type.model';
import { Currency } from 'app/shared/model/enumerations/currency.model';

export interface ICollection {
  id?: number;
  name?: string;
  title?: string;
  count?: number | null;
  collectionType?: CollectionType;
  auctionType?: AuctionType;
  minRange?: number;
  maxRange?: number;
  currency?: Currency;
  owner?: string;
  features?: IFeature[] | null;
  arts?: IArt[] | null;
}

export const defaultValue: Readonly<ICollection> = {};
