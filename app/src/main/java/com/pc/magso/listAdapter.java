package com.pc.magso;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;

import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by admin_2 on 2017/2/9.
 */

public class listAdapter extends BaseAdapter {
    private Context mContext;
    private List<MagBean> mList = null;

    public listAdapter(Context mContext, List<MagBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public MagBean getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem, null);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tvSize = (TextView) convertView.findViewById(R.id.tv_size);
            viewHolder.tvCount = (TextView) convertView.findViewById(R.id.tv_count);
            viewHolder.tvPopular = (TextView) convertView.findViewById(R.id.tv_popular);
            viewHolder.tvCopy = (ButtonFlat) convertView.findViewById(R.id.tv_copy);
            viewHolder.tvDownload = (ButtonFlat) convertView.findViewById(R.id.tv_download);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvName.setText(mList.get(position).getName());
        viewHolder.tvDate.setText(mList.get(position).getDate());
        viewHolder.tvSize.setText(mList.get(position).getSize());
        viewHolder.tvCount.setText(mList.get(position).getCount());
        viewHolder.tvPopular.setText(mList.get(position).getPopular());
        viewHolder.tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
                cm.setText(mList.get(position).getMagnet());
                Toast.makeText(mContext, "复制成功", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mList.get(position).getMagnet()));
                intent.addCategory("android.intent.category.DEFAULT");
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView tvName;
        private TextView tvDate;
        private TextView tvSize;
        private TextView tvCount;
        private TextView tvPopular;
        private ButtonFlat tvCopy;
        private ButtonFlat tvDownload;
    }
}
