package com.bitdubai.sub_app.intra_user_community.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bitdubai.fermat_android_api.ui.adapters.FermatAdapter;
import com.bitdubai.fermat_android_api.ui.util.FermatAnimationsUtils;
import com.bitdubai.fermat_api.layer.actor_connection.common.enums.ConnectionState;
import com.bitdubai.fermat_ccp_api.layer.module.intra_user.interfaces.IntraUserInformation;
import com.bitdubai.sub_app.intra_user_community.R;
import com.bitdubai.sub_app.intra_user_community.holders.AppWorldHolder;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class AppListAdapter extends FermatAdapter<IntraUserInformation, AppWorldHolder> {


    public AppListAdapter(Context context) {
        super(context);
    }

    public AppListAdapter(Context context, List<IntraUserInformation> dataSet) {
        super(context, dataSet);
    }

    @Override
    protected AppWorldHolder createHolder(View itemView, int type) {
        return new AppWorldHolder(itemView);
    }

    @Override
    protected int getCardViewResource() {
        return R.layout.row_connections_world;
    }

    @Override
    protected void bindHolder(AppWorldHolder holder, IntraUserInformation data, int position) {
        holder.connectionState.setVisibility(View.GONE);
        ConnectionState connectionState = data.getConnectionState();
        switch (connectionState) {
            case CONNECTED:
                if (holder.connectionState.getVisibility() == View.GONE)
                    holder.connectionState.setVisibility(View.VISIBLE);
                break;
            case BLOCKED_LOCALLY:
                break;
            case BLOCKED_REMOTELY:
                break;
            case CANCELLED_LOCALLY:
                break;
            case CANCELLED_REMOTELY:
                if (holder.connectionState.getVisibility() == View.GONE){
                    holder.connectionState.setImageResource(R.drawable.icon_contact_no_conect);
                    holder.connectionState.setVisibility(View.VISIBLE);
                }
                break;
            case NO_CONNECTED:
                break;
            case DENIED_LOCALLY:
                break;
            case DENIED_REMOTELY:
                if (holder.connectionState.getVisibility() == View.GONE){
                    holder.connectionState.setImageResource(R.drawable.icon_contact_no_conect);
                    holder.connectionState.setVisibility(View.VISIBLE);
                }
                break;
            case DISCONNECTED_LOCALLY:
                break;
            case DISCONNECTED_REMOTELY:
                if (holder.connectionState.getVisibility() == View.GONE){
                    holder.connectionState.setImageResource(R.drawable.icon_contact_no_conect);
                    holder.connectionState.setVisibility(View.VISIBLE);
                }
                break;
            case ERROR:
                break;
            case INTRA_USER_NOT_FOUND:
                break;
            case PENDING_LOCALLY_ACCEPTANCE:
                break;
            case PENDING_REMOTELY_ACCEPTANCE:
                if (holder.connectionState.getVisibility() == View.GONE){
                    holder.connectionState.setImageResource(R.drawable.icon_contact_standby);
                    holder.connectionState.setVisibility(View.VISIBLE);
                }
                break;
            default:
                if (holder.connectionState.getVisibility() == View.VISIBLE)
                    holder.connectionState.setVisibility(View.GONE);
                break;
        }
        holder.row_connection_state.setText(data.getState());
        if(data.getState().equals("Offline"))
            holder.row_connection_state.setTextColor(Color.RED);
        else
            holder.row_connection_state.setTextColor(Color.WHITE);
        holder.name.setText(data.getName());
        byte[] profileImage = data.getProfileImage();
        if (profileImage != null && profileImage.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(profileImage, 0, profileImage.length);
            bitmap = Bitmap.createScaledBitmap(bitmap, 480, 480, true);
            holder.thumbnail.setImageBitmap(bitmap);
        }else{
            holder.thumbnail.setVisibility(View.GONE);
            new ProgressTask(holder.progressBar,holder.thumbnail).execute();
        }

    }

    private class ProgressTask extends AsyncTask<Void,Void,Void> {

        WeakReference<ProgressBar> weakReference;
        WeakReference<ImageView> imageViewWeakReference;

        public ProgressTask(ProgressBar progressBar,ImageView imageView) {
            weakReference = new WeakReference<ProgressBar>(progressBar);
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected void onPreExecute(){
            weakReference.get().setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                TimeUnit.SECONDS.sleep(7);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(weakReference.get()!=null) {
                weakReference.get().setVisibility(View.GONE);
                weakReference.clear();
            }
            if(imageViewWeakReference.get()!=null) {
                FermatAnimationsUtils.showEmpty(context,true,imageViewWeakReference.get());
            }
        }
    }

    public int getSize() {
        if (dataSet != null)
            return dataSet.size();
        return 0;
    }
}