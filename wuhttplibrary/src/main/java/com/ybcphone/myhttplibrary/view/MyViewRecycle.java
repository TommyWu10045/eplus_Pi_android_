package com.ybcphone.myhttplibrary.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ybcphone.myhttplibrary.R;
import com.ybcphone.myhttplibrary.utils.MyLog;

import java.util.ArrayList;


public class MyViewRecycle extends FrameLayout {
    public Context context = null;
    public boolean firstIN = true;
    public SwipeRefreshLayout myViewRecycleSwipeRefreshLayout;
    public RecyclerView aRecycleView = null;

    public Listener_ViewRSloadData listener_ViewRSArtcleLoadMore;

    //加載更多參數
    private int currentScrollState = 0;
    private int[] lastPositions;
    private int lastVItemPosition = 0;
    private int lastVisibleItemPosition = 0;
    private int lastVisibleItemPositionOffset = 0;


    Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 0:
                    myViewRecycleSwipeRefreshLayout.setRefreshing(false);
                    break;
                case 1:
                    myViewRecycleSwipeRefreshLayout.setRefreshing(true);
                    break;

                case 2:
                    scrollLastPos(msg.arg1,msg.arg2);
                    break;

                default:
                    break;
            }
        }
    };


    public MyViewRecycle(Context context) {
        super(context);
        this.context = context;
        initView();
    }


    public MyViewRecycle(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    /*


     */

    private void initView() {

        LayoutInflater.from(context).inflate(R.layout.view_recycle_sliding, this);
        aRecycleView = (RecyclerView) findViewById(R.id.view_recycle_sliding_recycle_view);
        myViewRecycleSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.view_recycle_sliding_swipeRefreshLayout);
        myViewRecycleSwipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        //加載更新
        myViewRecycleSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                MyLog.d(".MyViewRecycle===>     .setOnRefreshListener    ");
                myHandler.sendEmptyMessageDelayed(0, 500);

                new Thread() {

                    @Override
                    public void run() {
                        try {
                            if (listener_ViewRSArtcleLoadMore != null) {
                                listener_ViewRSArtcleLoadMore.onViewRSReload();
                            } else {
                                MyLog.e("Error Callback null");
                            }
                        } catch (Exception e) {
                            myHandler.sendEmptyMessage(1);
                        }
                    }
                }.start();
            }
        });


        //加載更多 * grid 和 line 不同
        aRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                try {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    StaggeredGridLayoutManager staggeredGridLayoutManager
                            = (StaggeredGridLayoutManager) layoutManager;
                    if (lastPositions == null) {
                        lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                    }
                    staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                    lastVItemPosition = findMax(lastPositions);
                    lastVisibleItemPosition = findTop(lastPositions);
                    lastVisibleItemPositionOffset = aRecycleView.computeVerticalScrollOffset();
                    View topView = layoutManager.getChildAt(0);          //获取可视的第一个view
                    lastVisibleItemPositionOffset = topView.getTop();                                   //获取与该view的顶部的偏移量
                    lastVisibleItemPosition = layoutManager.getPosition(topView);  //得到该View的数组位置
                    //  MyLog.d("Change ===>  Position:   " + lastVisibleItemPosition + ", offset:" + lastVisibleItemPositionOffset);
                }catch (Exception e){

                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                try{
                currentScrollState = newState;
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if ((visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE &&
                        (lastVItemPosition) >= totalItemCount - 1)) {
                    listener_ViewRSArtcleLoadMore.onViewRSLoadMore();
                }

            }catch (Exception e){

            }
            }
        });
    }


    private void scrollLastPos(int v1, int v2) {
      //  MyLog.d("*scrollLastPos===>  Position:   " + v1 + ", offset:" + v2);
        ((StaggeredGridLayoutManager) aRecycleView.getLayoutManager())
                .scrollToPositionWithOffset(v1, v2);
    }

    private void scrollLastPos() {
        int v1 = lastVisibleItemPosition;
        int v2 = lastVisibleItemPositionOffset;
     //   MyLog.d("*scrollLastPos===>  Position:   " + v1 + ", offset:" + v2);
        // RecyclerView.LayoutManager layoutManager = aRecycleView.getLayoutManager();
        // StaggeredGridLayoutManager staggeredGridLayoutManager
        //        = (StaggeredGridLayoutManager) layoutManager;
        //   RecyclerView.LayoutManager. mLayoutManager = aRecycleView.getLayoutManager();
        ((StaggeredGridLayoutManager) aRecycleView.getLayoutManager())
                .scrollToPositionWithOffset(v1, v2);
    }


    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }


    private int findTop(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value < max) {
                max = value;
            }
        }
        return max;
    }


    //pop 2,15
    public void initRecycleView(Listener_ViewRSloadData callback, int spanCount, int spacing) {
        this.listener_ViewRSArtcleLoadMore = callback;
        // 设置布局管理器
        // 1.线性布局
        // LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false);
        // 2.Grid布局
        // RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);
        // 3.瀑布流
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        boolean includeEdge = true;
        aRecycleView.setLayoutManager(layoutManager);

        if (firstIN) {
            firstIN = false;
            aRecycleView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        }


    }

    public void mySetAdapter(RecyclerView.Adapter reAdapter) {

      /*  aRecycleView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                MyViewRecycle.this.removeOnLayoutChangeListener(this);
                scrollLastPos();
            }

        });*/
        aRecycleView.setAdapter(reAdapter);
       // reAdapter.notifyDataSetChanged();
     //   scrollLastPos();
      //  myHandler.sendEmptyMessageDelayed(2,300);//

        Message m = new Message();
        m.what =2;
        m.arg1 =lastVisibleItemPosition;
        m.arg2 =lastVisibleItemPositionOffset;
        myHandler.sendMessageDelayed(m,10);
      //  scrollLastPos();
    }


    private void initAdapter(ArrayList<ViewRecyclePhotoModel> data_list) {
        // 设置 RecyclerView的Adapter
        // 注意一定在设置了布局管理器之后调用
        //   myRecycleAdapter = new MyRecycleAdapter(data_list);
        //  aRecycleView.setAdapter(myRecycleAdapter);
        //  myRecycleAdapter.notifyDataSetChanged();
    }

/*
    private class MyRecycleAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<ViewRecyclePhotoModel> m_List_Category;

        public MyRecycleAdapter(ArrayList<ViewRecyclePhotoModel> m_List_Category) {
            this.m_List_Category = m_List_Category;
        }

        @Override
        public int getItemCount() {
            int ret = 0;
            if (m_List_Category != null) {
                ret = m_List_Category.size();
            }
            return ret;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ViewHolder ret = null;
            // 不需要检查是否复用，因为只要进入此方法，必然没有复用
            // 因为RecyclerView 通过Holder检查复用
            View v = LayoutInflater.from(getContext()).inflate(R.layout.view_rs_article, viewGroup, false);
            v.getLayoutParams().height = (int) ((float) MyApplication.DisplayMetrics_widthPixels / 2);

            ret = new ViewHolder(v);
            return ret;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ViewRecyclePhotoModel map = m_List_Category.get(i);
            viewHolder.itemView.setTag(map);
          //  viewHolder.textView.setTag(map);
          //  viewHolder.imageView.setTag(map);
            viewHolder.textView.setText(map.title);
            viewHolder.imageView.setUrlPic(map.picUrl, 4);
        }
    }


    private static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public MyImageView imageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            // 通常ViewHolder的构造，就是用于获取控件视图的
            imageView = (MyImageView) itemView.findViewById(R.id.view_rs_article_photo);
            textView = (TextView) itemView.findViewById(R.id.view_rs_article_text);
            // TODO 后续处理点击事件的操作
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            ViewRecyclePhotoModel map = (ViewRecyclePhotoModel)v.getTag();
            int position = getAdapterPosition();
            Context context = imageView.getContext();
            //  Toast.makeText(context, "显示第" + position + "个项", Toast.LENGTH_SHORT).show();
            ((MainActivity) context).showHairStyleDataFragment(map.id);
        }
    }*/


}
