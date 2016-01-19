package com.bitdubai.reference_wallet.bank_money_wallet.fragments.summary;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_android_api.ui.adapters.FermatAdapter;
import com.bitdubai.fermat_android_api.ui.enums.FermatRefreshTypes;
import com.bitdubai.fermat_android_api.ui.fragments.FermatWalletListFragment;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatListItemListeners;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Wallets;
import com.bitdubai.fermat_bnk_api.all_definition.enums.TransactionType;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet.bank_money.interfaces.BankAccountNumber;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet.bank_money.interfaces.BankMoneyTransactionRecord;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet_module.interfaces.BankMoneyWalletModuleManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedWalletExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.reference_wallet.bank_money_wallet.R;
import com.bitdubai.reference_wallet.bank_money_wallet.common.adapters.AccountListAdapter;
import com.bitdubai.reference_wallet.bank_money_wallet.common.adapters.TransactionListAdapter;
import com.bitdubai.reference_wallet.bank_money_wallet.common.dialogs.CreateTransactionFragmentDialog;
import com.bitdubai.reference_wallet.bank_money_wallet.session.BankMoneyWalletSession;
import com.bitdubai.reference_wallet.bank_money_wallet.util.CommonLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by memo on 08/12/15.
 */
public class AccountDetailFragment extends FermatWalletListFragment<BankMoneyTransactionRecord> implements FermatListItemListeners<BankMoneyTransactionRecord>, DialogInterface.OnDismissListener  {


    private BankMoneyWalletModuleManager moduleManager;
    private ErrorManager errorManager;
    private ArrayList<BankMoneyTransactionRecord> transactionList;
    private String walletPublicKey= "banking_wallet";
    private BankAccountNumber bankAccountNumber;

    com.getbase.floatingactionbutton.FloatingActionsMenu fab;
    CreateTransactionFragmentDialog dialog;

    private View emtyView;

    private FermatTextView bookTextView;
    private FermatTextView availableTextView;
    private FermatTextView balanceText;
    private FermatTextView availableText;
    private FermatTextView bookText;
    private FermatTextView aliasText;
    private FermatTextView accountText;

    private static final String TAG = "AccountListActivityFragment";
    public AccountDetailFragment() {
    }

    public static AccountDetailFragment newInstance() {
        return new AccountDetailFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BankAccountNumber data = (BankAccountNumber)appSession.getData("account_data");
        try {

            moduleManager = ((BankMoneyWalletSession) appSession).getModuleManager();
            errorManager = appSession.getErrorManager();
        } catch (Exception ex) {
            CommonLogger.exception(TAG, ex.getMessage(), ex);
            if (errorManager != null)
                errorManager.reportUnexpectedWalletException(
                        Wallets.BNK_BANKING_WALLET, UnexpectedWalletExceptionSeverity.DISABLES_THIS_FRAGMENT, ex);
        }
        System.out.println("DATA ="+data.getAccount());
        bankAccountNumber = data;
        transactionList = (ArrayList)getMoreDataAsync(FermatRefreshTypes.NEW, 0);
    }

    @Override
    protected void initViews(View layout) {
        super.initViews(layout);
        this.emtyView =  layout.findViewById(R.id.no_transactions);
        this.fab = (com.getbase.floatingactionbutton.FloatingActionsMenu) layout.findViewById(R.id.bw_fab_multiple_actions);
        this.availableTextView = (FermatTextView) layout.findViewById(R.id.available_balance);
        this.bookTextView = (FermatTextView) layout.findViewById(R.id.book_balance);

        List<BankAccountNumber> tempList= new ArrayList<>();
        tempList.add(bankAccountNumber);


        layout.findViewById(R.id.bw_fab_withdraw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCreateTransactionDialog(TransactionType.DEBIT);
            }
        });

        layout.findViewById(R.id.bw_fab_deposit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCreateTransactionDialog(TransactionType.CREDIT);
            }
        });
        configureToolbar();

        accountText = (FermatTextView) layout.findViewById(R.id.account);
        aliasText =  (FermatTextView) layout.findViewById(R.id.account_alias);
        balanceText = (FermatTextView) layout.findViewById(R.id.balance_text);
        availableText = (FermatTextView) layout.findViewById(R.id.available_text);
        bookText = (FermatTextView) layout.findViewById(R.id.book_text);
        balanceText.setText("Balance");
        bookText.setText("Book");
        availableText.setText("Available");
        updateBalance();
        showOrHideNoTransactionsView(transactionList.isEmpty());
    }

    private void launchCreateTransactionDialog(TransactionType transactionType){
        dialog = new CreateTransactionFragmentDialog(getActivity(), (BankMoneyWalletSession) appSession, getResources(), transactionType,bankAccountNumber.getAccount(),bankAccountNumber.getCurrencyType());
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    private void updateBalance(){

        accountText.setText(bankAccountNumber.getAccount());
        aliasText.setText(bankAccountNumber.getAlias());
        availableTextView.setText(String.valueOf(moduleManager.getBankingWallet().getAvailableBalance(bankAccountNumber.getAccount())) + " " + bankAccountNumber.getCurrencyType().getCode());
        bookTextView.setText(String.valueOf(moduleManager.getBankingWallet().getBookBalance(bankAccountNumber.getAccount())) + " " + bankAccountNumber.getCurrencyType().getCode());
    }

    private void configureToolbar() {
        getToolbar().setBackgroundColor(getResources().getColor(R.color.background_header_navy));
    }

    @Override
    protected boolean hasMenu() {
        return false;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.bw_account_detail_summary;
    }

    @Override
    protected int getSwipeRefreshLayoutId() {
        return R.id.transactions_swipe_refresh;
    }

    @Override
    protected int getRecyclerLayoutId() {
        return R.id.transactions_recycler_view;
    }

    @Override
    protected boolean recyclerHasFixedSize() {
        return false;
    }

    @Override
    public void onPostExecute(Object... result) {
        isRefreshing = false;
        if (isAttached) {
            swipeRefreshLayout.setRefreshing(false);
            if (result != null && result.length > 0) {
                transactionList = (ArrayList) result[0];
                if (adapter != null)
                    adapter.changeDataSet(transactionList);
                showOrHideNoTransactionsView(transactionList.isEmpty());
            }
        }
    }

    private void showOrHideNoTransactionsView(boolean show) {
        if (show) {
            recyclerView.setVisibility(View.GONE);
            emtyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emtyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onErrorOccurred(Exception ex) {
        isRefreshing = false;
        if (isAttached) {
            swipeRefreshLayout.setRefreshing(false);
            //TODO: show error, toast?
        }
    }

    @Override
    public FermatAdapter getAdapter() {
        if(adapter == null){
            adapter = new TransactionListAdapter(getActivity(), transactionList);
            adapter.setFermatListEventListener(this);
        }
        return adapter;
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
        return layoutManager;
    }

    @Override
    public void onItemClickListener(BankMoneyTransactionRecord data, int position) {

    }

    @Override
    public void onLongItemClickListener(BankMoneyTransactionRecord data, int position) {

    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        fab.collapse();
        updateBalance();
        onRefresh();
    }

    @Override
    public List<BankMoneyTransactionRecord> getMoreDataAsync(FermatRefreshTypes refreshType, int pos){
        List<BankMoneyTransactionRecord> data = new ArrayList<>();
        if (moduleManager != null) {
            try {
                data.addAll(moduleManager.getBankingWallet().getTransactions(bankAccountNumber.getAccount()));

            } catch (Exception ex) {
                if (errorManager != null)
                    errorManager.reportUnexpectedWalletException(Wallets.BNK_BANKING_WALLET, UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, ex);
            }
        } else {
            Toast.makeText(getActivity(), "Sorry, an error happened in BalanceSummaryFragment (Module == null)", Toast.LENGTH_SHORT).show();
        }
        return data;

    }
}
