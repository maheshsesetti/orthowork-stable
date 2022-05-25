import dayjs from 'dayjs';
import { IData } from 'app/shared/model/data.model';
import { ICollection } from 'app/shared/model/collection.model';
import { IOutput } from 'app/shared/model/output.model';
import { TransactionStatus } from 'app/shared/model/enumerations/transaction-status.model';

export interface ITransaction {
  id?: number;
  title?: string;
  status?: TransactionStatus | null;
  date?: string;
  data?: IData[] | null;
  collection?: ICollection | null;
  result?: IOutput | null;
}

export const defaultValue: Readonly<ITransaction> = {};
