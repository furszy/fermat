package com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.adapters;

import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.filters.ChatFilter;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.holders.ChatHolder;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.models.ChatMessage;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.util.ChatMessageComparator;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.util.Utils;
import com.bitdubai.fermat_android_api.ui.adapters.FermatAdapter;
import com.bitdubai.fermat_cht_android_sub_app_chat_bitdubai.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * ChatAdapter
 *
 * @author Jose Cardozo josejcb (josejcb89@gmail.com) on 05/01/15.
 * @version 1.0
 */

public class ChatAdapter extends FermatAdapter<ChatMessage, ChatHolder> implements Filterable {

    ArrayList<ChatMessage> filteredData;
    private String filterString;

    public ChatAdapter(Context context, ArrayList<ChatMessage> chatMessages) {//ChatFactory
        super(context, chatMessages);
    }

    @Override
    protected ChatHolder createHolder(View itemView, int type) {
        return new ChatHolder(itemView);
    }

    @Override
    protected int getCardViewResource() {
        return R.layout.chat_list_item;
    }

    @Override
    public void changeDataSet(List<ChatMessage> dataSet) {

        Collections.sort(dataSet, new ChatMessageComparator());

        super.changeDataSet(dataSet);
    }

    @Override
    protected void bindHolder(ChatHolder holder, ChatMessage data, int position) {
        if (data != null) {
            boolean myMsg = data.getIsme();
            setAlignment(holder, myMsg, data);
            final String copiedMessage = holder.txtMessage.getText().toString();
            holder.content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("simple text", copiedMessage);
                        clipboard.setPrimaryClip(clip);
                    } else {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(copiedMessage);
                    }
                    if (copiedMessage.length() <= 10) {
                        Toast.makeText(context, context.getText(R.string.copy_message_toast)+ " " + copiedMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, context.getText(R.string.copy_message_toast)+ " " + copiedMessage.substring(0, 11) + "...", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }
    }

    public View getView() {
        LayoutInflater vi = LayoutInflater.from(context);
        View convertView = vi.inflate(R.layout.chat_list_item, null);
        return convertView;
    }

    public void add(ChatMessage message) {
        dataSet.add(message);
    }

    private void setAlignment(ChatHolder holder, boolean isMe, ChatMessage data) {
        holder.tickstatusimage.setImageResource(0);
        holder.txtMessage.setText(Utils.avoidingScientificNot(data.getMessage()));
        holder.txtInfo.setText(data.getDate());
        if (isMe) {
            holder.contentWithBG.setBackgroundResource(R.drawable.cht_burble_green);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);

            if(data.getStatus() != null) {
                switch (data.getStatus()) {
                    case SENT:
                        holder.tickstatusimage.setVisibility(View.VISIBLE);
                        holder.tickstatusimage.setImageResource(R.drawable.cht_ticksent);
                        break;
                    case DELIVERED:
                        holder.tickstatusimage.setVisibility(View.VISIBLE);
                        holder.tickstatusimage.setImageResource(R.drawable.cht_tickdelivered);
                        break;
                    case RECEIVED:
                        holder.tickstatusimage.setVisibility(View.VISIBLE);
                        holder.tickstatusimage.setImageResource(R.drawable.cht_tickdelivered);
                        break;
                    case READ:
                        holder.tickstatusimage.setVisibility(View.VISIBLE);
                        holder.tickstatusimage.setImageResource(R.drawable.cht_tickread);
                        break;
                    case CANNOT_SEND:
                        holder.tickstatusimage.setVisibility(View.VISIBLE);
                        holder.tickstatusimage.setImageResource(R.drawable.cht_close);
                        break;
                    default:
                        holder.tickstatusimage.setImageResource(0);
                        holder.tickstatusimage.setVisibility(View.GONE);
                        break;
                }
            }else {
                holder.tickstatusimage.setImageResource(0);
                holder.tickstatusimage.setVisibility(View.GONE);
            }
        } else {
            holder.contentWithBG.setBackgroundResource(R.drawable.cht_burble_white);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);
            //holder.txtInfo.setPadding(20,0,20,7);
            holder.tickstatusimage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (filterString != null)
            return filteredData == null ? 0 : filteredData.size();
        else
            return dataSet == null ? 0 : dataSet.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        if (filterString != null)
            return filteredData != null ? (!filteredData.isEmpty()
                    && position < filteredData.size()) ? filteredData.get(position) : null : null;
        else
            return dataSet != null ? (!dataSet.isEmpty()
                    && position < dataSet.size()) ? dataSet.get(position) : null : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Filter getFilter() {
        return new ChatFilter(dataSet, this);
    }

    public void setFilterString(String filterString) {
        this.filterString = filterString;
    }

    public String getFilterString() {
        return filterString;
    }

}