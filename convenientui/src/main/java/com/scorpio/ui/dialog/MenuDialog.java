package com.scorpio.ui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scorpio.ui.R;
import com.scorpio.ui.util.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Frey-Lin
 */

public class MenuDialog extends BaseDialog {

    private List<MenuItem> menuItems = new ArrayList<>();

    private OnMenuItemClickListener mItemClickListener;

    protected MenuDialog(@NonNull Context context) {
        super(context);
    }

    protected MenuDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MenuDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setOnItemClickListener(OnMenuItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setMenuItems(List<MenuItem> items) {
        this.menuItems = items;
    }

    public static class Builder {
        private MenuDialog dialog;
        private Context mContext;
        private List<MenuItem> menuItems = new ArrayList<>();
        private OnMenuItemClickListener listener;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder items(List<MenuItem> items) {
            this.menuItems = items;
            return this;
        }

        public Builder addItem(int id, String text) {
            MenuItem menuItem = new MenuItem(id, text);
            menuItems.add(menuItem);
            return this;
        }

        public Builder onItemClick(OnMenuItemClickListener listener) {
            this.listener = listener;
            return this;
        }

        public MenuDialog create() {
            dialog = new MenuDialog(mContext, R.style.ActionSheetDialogStyle);
            dialog.setCanceledOnTouchOutside(false);
            int diaWidth = DeviceInfo.getScreenWidth(mContext);
            initDialogView(dialog);
            dialog.setSize((int) (diaWidth * 0.9f), WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            dialog.setOnItemClickListener(listener);
            dialog.setMenuItems(menuItems);
            return dialog;
        }

        private void initDialogView(final MenuDialog dialog) {
            View diaView = LayoutInflater.from(mContext).inflate(R.layout.menu_dialog, null);
            Button cancelBtn = diaView.findViewById(R.id.mCancelBtn);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            ListView listView = diaView.findViewById(R.id.mMenuItem);
            listView.setAdapter(new MenuAdapter(mContext));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MenuItem item = menuItems.get(position);
                    if (listener != null)
                        listener.OnItemClick(item);
                }
            });
            dialog.setContentView(diaView);
        }

        public class MenuAdapter extends BaseAdapter {

            private Context context;

            public MenuAdapter(Context context) {
                this.context = context;
            }

            @Override
            public int getCount() {
                return menuItems.size();
            }

            @Override
            public Object getItem(int position) {
                return menuItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return menuItems.get(position).id;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                MyViewHolder holder = null;
                if (convertView != null) {
                    holder = (MyViewHolder) convertView.getTag();
                } else {
                    holder = new MyViewHolder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false);
                    convertView.setTag(holder);
                }
                holder.item = convertView.findViewById(R.id.mItem);
                holder.item.setText(menuItems.get(position).text);
                return convertView;
            }

            public class MyViewHolder {
                TextView item;
            }
        }

    }

    public static class MenuItem {
        public String text;
        public int id;

        public MenuItem(int id, String text) {
            this.id = id;
            this.text = text;
        }
    }

    public interface OnMenuItemClickListener {
        void OnItemClick(MenuItem menuItem);
    }

}
