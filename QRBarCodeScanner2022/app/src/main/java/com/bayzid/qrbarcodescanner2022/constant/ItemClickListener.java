package com.bayzid.qrbarcodescanner2022.constant;

import android.view.View;

public interface ItemClickListener<T> {
    void onItemClick(View view, T item, int position);
}
