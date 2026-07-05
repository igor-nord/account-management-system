export interface TransactionSummary {
  transactionId: string;
  type: string;
  amount: string;
  currency: string;
  description: string;
  createdAt: string;
}

export interface HistoryPageResponse {
  items: TransactionSummary[];
  nextCursor: string | null;
}

export interface BalanceSnapshot {
  time: string;
  balance: string;
}

export interface BalanceSeriesResponse {
  points: BalanceSnapshot[];
}
