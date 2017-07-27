package cn.com.multiroommusic.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.com.multiroommusic.R;
import cn.com.multiroommusic.callback.OnItemClickEventCallback;
import cn.com.multiroommusic.utils.LogUtils;

/**
 * Created by wang l on 2017/5/19.
 */

public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter<BaseRecycleAdapter.BaseViewHodle> {
    private Context mContext;
    private List mBaseDataBeenList;
    private int layoutID;
    private OnItemClickEventCallback mOnItemClickEventCallback;

    public BaseRecycleAdapter(Context context, List baseDataBeenList, int layoutID) {
        this.mContext = context;
        if (baseDataBeenList != null)
            this.mBaseDataBeenList = baseDataBeenList;
        else
            mBaseDataBeenList = new ArrayList();
        this.layoutID = layoutID;
    }

    public void setBaseDataBeenList(final List baseDataBeenList) {
        mBaseDataBeenList = baseDataBeenList;
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
                LogUtils.i("setBaseDataBeenList","刷新房间列表");
            }
        });

    }

    public List<T> getBaseDataBeenList() {
        return mBaseDataBeenList;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public BaseViewHodle onCreateViewHolder(ViewGroup parent, int viewType) {
        //从holder基类中获取holder
        return BaseViewHodle.getHolder(mContext, parent, layoutID);
    }

    @Override
    public void onBindViewHolder(BaseViewHodle holder, final int position) {
        //调用由子类实现的抽象方法，将操作下放到子类中
        holder.itemView.setBackgroundResource(position%2 == 0? R.drawable.list_item_bg1:R.drawable.list_item_bg2);
        onBind(holder, mBaseDataBeenList.get(position), position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBaseDataBeenList != null && mOnItemClickEventCallback != null){
                    mOnItemClickEventCallback.onItemClick(mBaseDataBeenList.get(position),position);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBaseDataBeenList.size();
    }

    public abstract void onBind(BaseViewHodle hodle,Object baseDataBean,int position);


    public static class BaseViewHodle extends RecyclerView.ViewHolder{

        public View itemView;
        SparseArray<View> views;//存放Item中的控件
        public BaseViewHodle(View itemView){
            super(itemView);
            this.itemView = itemView;
            views = new SparseArray<View>();
        }
        //供adapter调用，返回holder
        public static <T extends BaseViewHodle> T getHolder(Context cxt, ViewGroup parent, int layoutId){
            return (T) new BaseViewHodle(LayoutInflater.from(cxt).inflate(layoutId, parent, false));
        }
        //根据Item中的控件Id获取控件
        public <T extends View> T getView(int viewId){
            View childreView = views.get(viewId);
            if (childreView == null){
                childreView = itemView.findViewById(viewId);
                views.put(viewId, childreView);
            }
            return (T) childreView;
        }
        //根据Item中的控件Id向控件添加事件监听
        public BaseViewHodle setOnClickListener(int viewId, View.OnClickListener listener){
            View view = getView(viewId);
            view.setOnClickListener(listener);
            return this;
        }
    }



    public OnItemClickEventCallback getOnItemClickEventCallback() {
        return mOnItemClickEventCallback;
    }

    public void setOnItemClickEventCallback(OnItemClickEventCallback onItemClickEventCallback) {
        mOnItemClickEventCallback = onItemClickEventCallback;
    }
}
