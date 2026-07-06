export interface TransactionLeg {
  accountCode: number;
  counterpartyAccountCode: number;
  type: string;
  amount: string;
  currency: string;
  description: string;
}

export interface TransactionResponse {
  transactionId: string;
  createdAt: string;
  legs: TransactionLeg[];
}
