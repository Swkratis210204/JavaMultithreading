class Operation implements Runnable {
    private final int ACCOUNT_ID;
    private final int AMOUNT;
    private final Account account;

    Operation(Bank bank, int accountId, int amount) {
        ACCOUNT_ID = accountId;
        AMOUNT = amount;
        account = bank.getAccount(ACCOUNT_ID);
    }

    int getAccountId() {
        return ACCOUNT_ID;
    }

    synchronized public void run() {
        account.adjustBalance(AMOUNT);
    }
}
