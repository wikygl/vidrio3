/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.ViewGroup
 *  android.widget.LinearLayout
 *  android.widget.TextView
 *  androidx.viewbinding.ViewBinding
 *  androidx.viewbinding.ViewBindings
 *  java.lang.NullPointerException
 *  java.lang.Object
 */
package Jose.jose.databinding;

import Jose.jose.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;

public final class ItemArchiveBinding
implements ViewBinding {
    public final LinearLayout layoutItemArchive;
    private final LinearLayout rootView;
    public final TextView textViewContent;

    private ItemArchiveBinding(LinearLayout linearLayout, LinearLayout linearLayout2, TextView textView) {
        this.rootView = linearLayout;
        this.layoutItemArchive = linearLayout2;
        this.textViewContent = textView;
    }

    public static ItemArchiveBinding bind(View view) {
        LinearLayout linearLayout = (LinearLayout)view;
        int n = R.id.textViewContent;
        TextView textView = (TextView)ViewBindings.findChildViewById((View)view, (int)n);
        if (textView != null) {
            return new ItemArchiveBinding((LinearLayout)view, linearLayout, textView);
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(n)));
    }

    public static ItemArchiveBinding inflate(LayoutInflater layoutInflater) {
        return ItemArchiveBinding.inflate(layoutInflater, null, false);
    }

    public static ItemArchiveBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean bl) {
        layoutInflater = layoutInflater.inflate(R.layout.item_archive, viewGroup, false);
        if (bl) {
            viewGroup.addView((View)layoutInflater);
        }
        return ItemArchiveBinding.bind((View)layoutInflater);
    }

    public LinearLayout getRoot() {
        return this.rootView;
    }
}

