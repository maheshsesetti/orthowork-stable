import dayjs from 'dayjs';
import { ITransaction } from 'app/shared/model/transaction.model';

export interface IOutput {
  id?: number;
  date?: string;
  result?: string;
  transaction?: ITransaction | null;
}

export const defaultValue: Readonly<IOutput> = {};
