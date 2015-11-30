package com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.navigation_drawer;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bitdubai.android_fermat_ccp_wallet_bitcoin.R;
import com.bitdubai.fermat_android_api.ui.adapters.FermatAdapter;
import com.bitdubai.fermat_ccp_api.layer.module.intra_user.exceptions.CantGetActiveLoginIdentityException;
import com.bitdubai.fermat_ccp_api.layer.module.intra_user.interfaces.IntraUserLoginIdentity;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.utils.FragmentsCommons;

/**
 * Created by mati on 2015.11.24..
 */
public class NavigationViewPainter implements com.bitdubai.fermat_android_api.engine.NavigationViewPainter {

    private final IntraUserLoginIdentity intraUserLoginIdentity;
    private Activity activity;

    public NavigationViewPainter(Activity activity, IntraUserLoginIdentity intraUserLoginIdentity) {
        this.activity = activity;
        this.intraUserLoginIdentity = intraUserLoginIdentity;
    }

    @Override
    public View addNavigationViewHeader() {
        try {
            return FragmentsCommons.setUpHeaderScreen(activity.getLayoutInflater(), activity, intraUserLoginIdentity);
        } catch (CantGetActiveLoginIdentityException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FermatAdapter addNavigationViewAdapter() {
        try {
            NavigationViewAdapter navigationViewAdapter = new NavigationViewAdapter(activity);
            //setNavigationDrawer(navigationViewAdapter);
            return navigationViewAdapter;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ViewGroup addNavigationViewBodyContainer(LayoutInflater layoutInflater,ViewGroup base) {
        RelativeLayout relativeLayout = (RelativeLayout) layoutInflater.inflate(R.layout.navigation_view_bottom,base,true);
        //base.setLayoutParams(new RelativeLayout.LayoutParams(activity,));
        return relativeLayout;
    }

    @Override
    public Drawable addBodyBackground() {
        return ContextCompat.getDrawable(activity.getApplicationContext(),R.drawable.bg_drawer_body);
    }

    @Override
    public int addBodyBackgroundColor() {
        return 0;
    }
}
