package cn.com.hesc.materialdialogs;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ProjectName: FastDev-master
 * ClassName: HescMaterialDialog
 * Description: 弹出式对话框
 * Author: liujunlin
 * Date: 2018-02-06 09:34
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class HescMaterialDialog {

    protected final Context context;
    private MaterialDialog materialDialog;
    private ArrayList<String> multiItems = new ArrayList<>();

    /**
     * 构造函数
     *
     * @param context
     */
    public HescMaterialDialog(@NonNull Context context) {
        this.context = context;
    }

    /**
     * 显示无限等待对话框--环形进度条
     *
     * @param title      标题文本
     * @param content    内容文本
     * @param cancelable 设置用户点击空白处或返回键时，是否关闭对话框
     */
    public void showIndeterminateCircleProgressDialog(@NonNull CharSequence title, @NonNull
            CharSequence content, boolean cancelable) {
        if (materialDialog == null)
            materialDialog = new MaterialDialog.Builder(context)
                    .title(title)
                    .content(content)
                    .progress(true, 0)
                    .progressIndeterminateStyle(false)
                    .show();
        else
            materialDialog.show();
        materialDialog.setCancelable(cancelable);
    }

    /**
     * 显示无限等待对话框--横向进度条
     * @param title
     * @param content
     * @param cancelable
     */
    public void showIndeterminateHorizontalProgressDialog(@NonNull CharSequence title, @NonNull
            CharSequence content, boolean cancelable) {
        if (materialDialog == null)
            materialDialog = new MaterialDialog.Builder(context)
                    .title(title)
                    .content(content)
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();
        else
            materialDialog.show();
        materialDialog.setCancelable(cancelable);
    }

    /**
     * 展示自定义布局的dialog
     * @param title 标题
     * @param layoutRes 布局文件id
     * @param positiveText 确定按钮的文本信息
     * @param negativeText 取消按钮的文本信息
     * @param buttonCallback 按钮的点击回调
     */
    public void showCustomViewDialog(@NonNull CharSequence title, @LayoutRes int layoutRes,
                                     @NonNull CharSequence positiveText, @NonNull CharSequence
                                             negativeText, final ButtonCallback buttonCallback) {
        if (materialDialog == null)
            materialDialog = new MaterialDialog.Builder(context)
                    .title(title).customView(layoutRes, true)
                    .positiveText(positiveText).negativeText(negativeText)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            buttonCallback.onPositive(HescMaterialDialog.this);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            buttonCallback.onNegative(HescMaterialDialog.this);
                        }
                    }).build();
        materialDialog.show();
    }

    /**
     * 展示自定义布局的dialog
     * @param title 标题
     * @param view 自定义view
     * @param positiveText 确定按钮的文本信息
     * @param negativeText 取消按钮的文本信息
     * @param buttonCallback 按钮的点击回调
     */
    public void showCustomViewDialog(@NonNull CharSequence title,@NonNull View view,
                                     @NonNull CharSequence positiveText, @NonNull CharSequence
                                             negativeText, final ButtonCallback buttonCallback) {
        if (materialDialog == null)
            materialDialog = new MaterialDialog.Builder(context)
                    .title(title).customView(view, true)
                    .positiveText(positiveText).negativeText(negativeText)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            buttonCallback.onPositive(HescMaterialDialog.this);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            buttonCallback.onNegative(HescMaterialDialog.this);
                        }
                    }).build();
        materialDialog.show();
    }

    /**
     * 普通确定对话框
     * @param title 对话框标题
     * @param content 对话框内容
     * @param positiveText 确定按钮的文本，如"确定"/"好"...
     * @param negativeText 取消按钮的文本，如"取消"/"不好"...
     * @param buttonCallback 按钮点击回调
     */
    public void showConfirmDialog(@NonNull CharSequence title, @NonNull CharSequence content, @NonNull CharSequence positiveText,
                           @NonNull CharSequence negativeText, final ButtonCallback buttonCallback) {
        if (materialDialog == null)
            materialDialog = new MaterialDialog.Builder(context)
                    .title(title).content(content).positiveText(positiveText).negativeText(negativeText)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            buttonCallback.onPositive(HescMaterialDialog.this);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            buttonCallback.onNegative(HescMaterialDialog.this);
                        }
                    }).build();
        materialDialog.show();
    }

    /**
     * 普通确定对话框
     * @param title 对话框标题
     * @param content 对话框内容
     * @param positiveText 确定按钮的文本，如"确定"/"好"...
     * @param negativeText 取消按钮的文本，如"取消"/"不好"...
     * @param buttonCallback 按钮点击回调
     */
    public void showThirdConfirmDialog(@NonNull CharSequence title, @NonNull CharSequence content, @NonNull CharSequence positiveText,
                                  @NonNull CharSequence negativeText, @NonNull CharSequence neutralText, final ThirdButtonCallback buttonCallback) {
        if (materialDialog == null)
            materialDialog = new MaterialDialog.Builder(context)
                    .title(title).content(content).positiveText(positiveText).negativeText(negativeText).neutralText(neutralText)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            buttonCallback.onPositive(HescMaterialDialog.this);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            buttonCallback.onNegative(HescMaterialDialog.this);
                        }

                        @Override
                        public void onNeutral(MaterialDialog dialog) {
                            buttonCallback.onNeutral(HescMaterialDialog.this);
                        }
                    }).build();
        materialDialog.show();
    }

    /**
     * 单选框
     * @param title 标题
     * @param positiveText 确定按钮文本
     * @param negativeText 取消按钮文本
     * @param choseItems 选项数组
     * @param checkindex 设置默认选中的索引，无选中设为-1
     * @param buttonCallback 按钮回调
     */
    public void showSignalDialog(@NonNull CharSequence title, @NonNull CharSequence positiveText,
                                 @NonNull CharSequence negativeText,@NonNull String[] choseItems, int checkindex,final ButtonCallback buttonCallback){
        if(materialDialog == null){
            final MaterialDialog.Builder mb = new MaterialDialog.Builder(context);
            mb.title(title).items(choseItems)
                    .positiveText(positiveText)
                    .negativeText(negativeText)
                    .itemsCallbackSingleChoice(checkindex>=0?checkindex:-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                            if(!TextUtils.isEmpty(text)){
                                multiItems.clear();
                                multiItems.add(text.toString());
                            }
                            return true;
                        }
                    })
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {

                            if (mb.listCallbackSingleChoice == null)
                                return;
                            CharSequence text = null;
                            if (mb.selectedIndex >= 0 && mb.selectedIndex < mb.items.length) {
                                text = mb.items[mb.selectedIndex];
                            }
                            mb.listCallbackSingleChoice.onSelection(materialDialog, null, mb.selectedIndex, text);

                            buttonCallback.onPositive(HescMaterialDialog.this);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            buttonCallback.onNegative(HescMaterialDialog.this);
                        }
                    });
            materialDialog = mb.build();
        }
        materialDialog.show();
    }

    /**
     * 多选对话框
     * @param title 对话框题目
     * @param positiveText 确定
     * @param choseItems 选择项
     * @param negativeText 取消
     * @param selectedIndices 设置选中的索引数组，可为null
     * @param buttonCallback 按钮回调
     */
    public void showMultiDialog(@NonNull CharSequence title, @NonNull CharSequence positiveText,
                                @NonNull CharSequence negativeText, @NonNull String[] choseItems, @Nullable Integer[] selectedIndices, final ButtonCallback buttonCallback){
        if (materialDialog == null){
            final MaterialDialog.Builder mb = new MaterialDialog.Builder(context);
            mb.title(title).items(choseItems)
                    .positiveText(positiveText)
                    .negativeText(negativeText)
                    .itemsCallbackMultiChoice(selectedIndices, new MaterialDialog.ListCallbackMultiChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                            if(text != null){
                                multiItems.clear();
                                for (CharSequence c:text) {
                                    multiItems.add(c.toString());
                                }
                            }

                            return true; // allow selection
                        }
                    })
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {

                            if (mb.listCallbackMultiChoice == null)
                                return ;
                            Collections.sort(materialDialog.selectedIndicesList); // make sure the indicies are in order
                            List<CharSequence> selectedTitles = new ArrayList<>();
                            for (Integer i : materialDialog.selectedIndicesList) {
                                if (i < 0 || i > mb.items.length - 1) continue;
                                selectedTitles.add(mb.items[i]);
                            }
                            mb.listCallbackMultiChoice.onSelection(materialDialog,
                                    materialDialog.selectedIndicesList.toArray(new Integer[materialDialog.selectedIndicesList.size()]),
                                    selectedTitles.toArray(new CharSequence[selectedTitles.size()]));

                            buttonCallback.onPositive(HescMaterialDialog.this);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            buttonCallback.onNegative(HescMaterialDialog.this);
                        }
                    });
            materialDialog = mb.build();
        }
        materialDialog.show();
    }

    /**
     * 将选择好的内容保存在列表里
     * @return
     */
    public ArrayList<String> getMultiItems() {
        return multiItems;
    }

    /**
     * 获取自定义的view
     * @return
     */
    public View getCustomView() {
        return materialDialog == null ? null : materialDialog.getCustomView();
    }

    /**
     * 返回对话框的显示状态
     *
     * @return 显示状态
     */
    public boolean isShowing() {
        return materialDialog == null ? false : materialDialog.isShowing();
    }

    /**
     * 关闭对话框
     */
    public void cancel() {
        if (materialDialog != null)
            materialDialog.cancel();
    }

    /**
     * 设置是否自动关闭弹出框
     * @param isCancelOutside
     */
    public void setIsAutoDismiss(boolean isCancelOutside){
        materialDialog.getBuilder().autoDismiss(isCancelOutside);
    }

    /**
     * 是否可以关闭对话框
     * @param isCancel
     */
    public void setIsCancel(boolean isCancel){
        materialDialog.setCancelable(isCancel);
    }

    public static abstract class ButtonCallback {
        public void onPositive(HescMaterialDialog dialog) {
        }

        public void onNegative(HescMaterialDialog dialog) {
        }

        public ButtonCallback() {
            super();
        }
    }

    public static abstract class  ThirdButtonCallback extends ButtonCallback{
        public void onNeutral(HescMaterialDialog dialog) {
        }

        public ThirdButtonCallback() {
            super();
        }
    }

    /**
     * 返回内置dialog对象
     * @return
     */
    public MaterialDialog getMaterialDialog() {
        return materialDialog;
    }
}
