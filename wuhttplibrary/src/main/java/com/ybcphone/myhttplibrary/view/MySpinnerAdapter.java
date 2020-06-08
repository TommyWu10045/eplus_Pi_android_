package com.ybcphone.myhttplibrary.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ybcphone.myhttplibrary.R;

import java.util.List;


/**
 * Created by tommymac on 2018/2/27.
 */


public class MySpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    private Context context;
    private List<String> list;
    public Object tagObject =null;

    public MySpinnerAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.layout_spinner_view, null);
        TextView tvgetView = (TextView) convertView.findViewById(R.id.tvgetView);
        tvgetView.setText(getItem(position).toString());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.layout_spinner_dropview, null);
        TextView tvdropdowview = (TextView) convertView.findViewById(R.id.tvgetdropdownview);
        tvdropdowview.setText(getItem(position).toString());
        return convertView;
    }
}