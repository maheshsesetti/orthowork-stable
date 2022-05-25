import dayjs from 'dayjs';
import { InvoiceStatus } from 'app/shared/model/enumerations/invoice-status.model';
import { PaymentMethod } from 'app/shared/model/enumerations/payment-method.model';

export interface IInvoice {
  id?: number;
  code?: string;
  date?: string;
  details?: string | null;
  status?: InvoiceStatus;
  paymentMethod?: PaymentMethod;
  paymentDate?: string;
  paymentAmount?: number;
}

export const defaultValue: Readonly<IInvoice> = {};
