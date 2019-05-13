package com.dzkandian.mvp.mine.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.storage.bean.mine.QuestionAllBean;
import java.util.List;

/**
 * 一次性获取所有问题列表Activity的Adapter
 */
public class QuestionAllAdapter extends BaseExpandableListAdapter{
    private Context context;// 上下文Context
    private List<QuestionAllBean> info;// 数据源

    public QuestionAllAdapter(Context context,List<QuestionAllBean> infos){
        this.context=context;
        this.info=infos;
    }

    //获取分组的个数
    @Override
    public int getGroupCount() {
        return info.size();
    }

    //获取指定分组中的子选项的个数
    @Override
    public int getChildrenCount(int i) {
        return info.get(i).getList().size();
    }

    //获取指定的分组数据
    @Override
    public Object getGroup(int i) {
        return info.get(i).getTypeName();
    }

    //获取指定分组中的指定子选项数据
    @Override
    public Object getChild(int i, int i1) {
        return info.get(1).getList().get(i1);
    }

    //获取指定分组的ID, 这个ID必须是唯一的
    @Override
    public long getGroupId(int i) {
        return i;
    }

    //获取子选项的ID, 这个ID必须是唯一的
    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    //分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
    @Override
    public boolean hasStableIds() {
        return true;
    }

    //获取显示指定分组的视图
    @Nullable
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, @Nullable View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_quest_type,parent,false);
            groupViewHolder=new GroupViewHolder();
            groupViewHolder.tvWentiTou=convertView.findViewById(R.id.tv_wenti_tou);
            convertView.setTag(groupViewHolder);
        }else {
            groupViewHolder= (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.tvWentiTou.setText(info.get(groupPosition).getTypeName());
        return convertView;
    }

    //获取显示指定分组中的指定子选项的视图
    @Nullable
    @Override
    public View getChildView(int groupPosition, int i1, boolean isExpanded, @Nullable View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView==null){
            convertView=LayoutInflater.from(context).inflate(R.layout.item_quest,parent,false);
            childViewHolder=new ChildViewHolder();
            childViewHolder.llItemWenti=convertView.findViewById(R.id.ll_item_wenti);
            childViewHolder.tvItemWenti=convertView.findViewById(R.id.tv_item_wenti);
            childViewHolder.ivWentiXia=convertView.findViewById(R.id.iv_wenti_xia);
            childViewHolder.ivWentiShang=convertView.findViewById(R.id.iv_wenti_shang);
            childViewHolder.tvItemDaan=convertView.findViewById(R.id.tv_item_daan);
            convertView.setTag(childViewHolder);
        }else {
            childViewHolder= (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.tvItemWenti.setText(info.get(groupPosition).getList().get(i1).getQuestion());
        childViewHolder.tvItemDaan.setText(info.get(groupPosition).getList().get(i1).getAnswer());
        if (!info.get(groupPosition).getList().get(i1).getIsOpen()){
            childViewHolder.ivWentiShang.setVisibility(View.GONE);
            childViewHolder.ivWentiXia.setVisibility(View.VISIBLE);
            childViewHolder.tvItemDaan.setVisibility(View.GONE);
        }else {
            childViewHolder.ivWentiShang.setVisibility(View.VISIBLE);
            childViewHolder.ivWentiXia.setVisibility(View.GONE);
            childViewHolder.tvItemDaan.setVisibility(View.VISIBLE);
        }
        childViewHolder.llItemWenti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (info.get(groupPosition).getList().get(i1).getIsOpen()){
                    info.get(groupPosition).getList().get(i1).setIsOpen(false);
                    childViewHolder.ivWentiShang.setVisibility(View.GONE);
                    childViewHolder.ivWentiXia.setVisibility(View.VISIBLE);
                    childViewHolder.tvItemDaan.setVisibility(View.GONE);
                }else {
                    info.get(groupPosition).getList().get(i1).setIsOpen(true);
                    childViewHolder.ivWentiShang.setVisibility(View.VISIBLE);
                    childViewHolder.ivWentiXia.setVisibility(View.GONE);
                    childViewHolder.tvItemDaan.setVisibility(View.VISIBLE);
                }
            }
        });
        return convertView;
    }

    //指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


    /*父项View*/
    private class GroupViewHolder{
        TextView tvWentiTou;
    }

    /*子项View*/
    private class ChildViewHolder{
        RelativeLayout llItemWenti;
        TextView tvItemWenti;
        ImageView ivWentiXia;
        ImageView ivWentiShang;
        TextView tvItemDaan;
    }
}
