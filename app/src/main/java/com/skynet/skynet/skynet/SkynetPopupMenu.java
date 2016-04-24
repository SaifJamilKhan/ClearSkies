package com.skynet.skynet.skynet;

import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.view.menu.MenuPresenter;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;


/**
 * Created by saifkhan on 2016-04-23.
 */
public class SkynetPopupMenu extends PopupMenu implements MenuBuilder.Callback, MenuPresenter.Callback {
    private final Context mContext;
    private final MenuBuilder mMenu;
    private final View mAnchor;
    private final MenuPopupHelper mPopup;

    public SkynetPopupMenu(Context context, View anchor) {
        super(context, anchor);
        mContext = context;
        mMenu = new MenuBuilder(context);
        mMenu.setCallback(this);
        mAnchor = anchor;
        mPopup = new MenuPopupHelper(context, mMenu, anchor);
        mPopup.setCallback(this);
        mPopup.setForceShowIcon(true); //ADD THIS LINE
    }

    @Override
    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
        return false;
    }

    @Override
    public void onMenuModeChange(MenuBuilder menu) {

    }

    @Override
    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {

    }

    @Override
    public boolean onOpenSubMenu(MenuBuilder subMenu) {
        return false;
    }
}
