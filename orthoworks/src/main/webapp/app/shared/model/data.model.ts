import { ITransaction } from 'app/shared/model/transaction.model';

export interface IData {
  id?: number;
  name?: string;
  fileContentType?: string | null;
  file?: string | null;
  transaction?: ITransaction | null;
}

export const defaultValue: Readonly<IData> = {};
