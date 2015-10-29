package com.niesen.cporm.adaptor;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.niesen.cpo.lib.model.loader.CPOrmCursorAdaptor;
import com.niesen.cporm.model.domain.User;


public class ExampleCursorAdaptor extends CPOrmCursorAdaptor<User, ExampleCursorAdaptor.ViewHolder> {


    public ExampleCursorAdaptor(Context context) {

        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void setViewInformation(ViewHolder viewHolder, User information) {

        viewHolder.textView.setText(information.getUserName());
    }

    protected static class ViewHolder {

        final TextView textView;

        ViewHolder(View view) {

            textView = (TextView) view.findViewById(android.R.id.text1);
        }
    }

}
