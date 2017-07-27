package cn.com.multiroommusic.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.util.List;

import cn.com.auxdio.protocol.bean.AuxNetModelEntity;
import cn.com.auxdio.protocol.bean.AuxNetRadioTypeEntity;
import cn.com.auxdio.protocol.bean.AuxPlayListEntity;
import cn.com.auxdio.protocol.bean.AuxRoomEntity;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.bean.MRMDeviceBean;
import cn.com.multiroommusic.bean.MRMRadioBean;
import cn.com.multiroommusic.bean.MRMRoomBean;
import cn.com.multiroommusic.bean.MRMSongBean;
import cn.com.multiroommusic.bean.MRMSoundEffect;
import cn.com.multiroommusic.bean.MRMSourceBean;
import cn.com.multiroommusic.dialog.RenameDialog;
import cn.com.multiroommusic.ui.MRMLanguageActivity;

/**
 * Created by wang l on 2017/5/19.
 */

public class MRMCommonAdapter extends BaseRecycleAdapter<Object> {

    public MRMCommonAdapter(Context context, List baseDataBeen, int layoutID) {
        super(context, baseDataBeen, layoutID);
    }

    @Override
    public void onBind(BaseViewHodle hodle, final Object baseDataBean, final int position) {
        CheckBox checkBox = hodle.getView(R.id.cb_item_rc_common);
        TextView textView = hodle.getView(R.id.tv_item_rc_common);
        int color_red = getContext().getResources().getColor(R.color.color_checked);
        int color_yell= getContext().getResources().getColor(R.color.color_checked_yell);
        checkBox.setBackgroundColor(position%2 == 0? color_red:color_yell);
        boolean check = false;
        String showText = "";
        if (baseDataBean instanceof MRMDeviceBean) {
            check = ((MRMDeviceBean) baseDataBean).isCheck();
            showText = ((MRMDeviceBean) baseDataBean).getDevName()+"("+((MRMDeviceBean) baseDataBean).getDevIP()+")";
        }else if (baseDataBean instanceof MRMRoomBean) {
            check = ((MRMRoomBean) baseDataBean).isCheck();
            showText =((MRMRoomBean) baseDataBean).getRoomName();
            hodle.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    RenameDialog renameDialog = new RenameDialog();
                    renameDialog.setAuxRoomEntity((AuxRoomEntity) baseDataBean,MRMCommonAdapter.this);
                    renameDialog.show(((FragmentActivity)getContext()).getFragmentManager(),"renamedialog");
                    return true;
                }
            });
        }else if (baseDataBean instanceof MRMLanguageActivity.LanguageBean) {
            MRMLanguageActivity.LanguageBean languageBean = (MRMLanguageActivity.LanguageBean) baseDataBean;
            check = languageBean.isCheck();
            showText = languageBean.getLanguageName();
        }else if (baseDataBean instanceof AuxPlayListEntity) {
            showText =((AuxPlayListEntity) baseDataBean).getPlayListName();
        }else if (baseDataBean instanceof MRMSongBean) {
            check = ((MRMSongBean) baseDataBean).isCheck();
            showText =((MRMSongBean) baseDataBean).getSongName();
        }else if (baseDataBean instanceof MRMSourceBean) {
            check = ((MRMSourceBean) baseDataBean).isCheck();
            showText =((MRMSourceBean) baseDataBean).getSourceName();
        }else if (baseDataBean instanceof AuxNetRadioTypeEntity) {
            showText =((AuxNetRadioTypeEntity) baseDataBean).getRadioType();
        }else if (baseDataBean instanceof MRMRadioBean) {
            check = ((MRMRadioBean) baseDataBean).isCheck();
            showText =((MRMRadioBean) baseDataBean).getRadioName();
        }else if (baseDataBean instanceof AuxNetModelEntity) {
            showText =((AuxNetModelEntity) baseDataBean).getModelName();
        }else if (baseDataBean instanceof String) {
            showText = (String) baseDataBean;
        }else if (baseDataBean instanceof MRMSoundEffect){
            check = ((MRMSoundEffect) baseDataBean).isCheck();
            showText = ((MRMSoundEffect)baseDataBean).getSoundName();
        }else if (baseDataBean instanceof Container){
            showText = ((Container)baseDataBean).getTitle();
        }else if (baseDataBean instanceof Item){
            showText = ((Item)baseDataBean).getTitle();
        }else if (baseDataBean instanceof Device){
            showText = ((Device)baseDataBean).getDetails().getFriendlyName();
        }
        checkBox.setVisibility(check? View.VISIBLE:View.INVISIBLE);
        textView.setText(showText);
    }
}
