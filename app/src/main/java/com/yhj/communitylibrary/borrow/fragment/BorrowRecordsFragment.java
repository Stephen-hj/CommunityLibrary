package com.yhj.communitylibrary.borrow.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yhj.communitylibrary.HomeActivity;
import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.library.bean.Borrow;
import com.yhj.communitylibrary.manage.adapter.BookBorrowAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * author : yhj
 * date  :2020/4/30
 * desc   :我的借书记录
 */
public class BorrowRecordsFragment extends Fragment implements BookBorrowAdapter.OnBookClickListener {


    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.borrow_records_recycle)
    RecyclerView borrowRecordsRecycle;
    @BindView(R.id.borrow_records_swipe)
    SwipeRefreshLayout borrowRecordsSwipe;
    Unbinder unbinder;

    private List<Borrow> bookList = new ArrayList<>();
    private BookBorrowAdapter bookBorrowAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_borrow_records, container, false);
        unbinder = ButterKnife.bind(this, view);


        bookBorrowAdapter = new BookBorrowAdapter(bookList, this, 4);

        initData();

        borrowRecordsSwipe.setColorSchemeResources(R.color.myblue);
        borrowRecordsSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                bookBorrowAdapter.notifyDataSetChanged();
                borrowRecordsSwipe.setRefreshing(false);
            }
        });
        return view;
    }

    private void initData() {
        String bql = "select * from Borrow where borrow_name='" + HomeActivity.name + "' and is_restore=0 order by restoreAt desc";
        BmobQuery<Borrow> query = new BmobQuery<Borrow>();
        query.setSQL(bql);
        query.doSQLQuery(new SQLQueryListener<Borrow>() {

            @Override
            public void done(BmobQueryResult<Borrow> result, BmobException e) {
                if (e == null) {
                    List<Borrow> list = (List<Borrow>) result.getResults();
                    if (list != null && list.size() > 0) {
                        iv.setVisibility(View.GONE);
                        borrowRecordsRecycle.setVisibility(View.VISIBLE);
                        bookList.clear();
                        bookList.addAll(list);
                        borrowRecordsRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
                        borrowRecordsRecycle.setAdapter(bookBorrowAdapter);
                    } else {
                        borrowRecordsRecycle.setVisibility(View.GONE);
                        iv.setVisibility(View.VISIBLE);
                        Glide.with(getContext()).asGif().load(R.drawable.oa_develop).into(iv);
                    }
                } else {
                    if (e.getErrorCode() == 9016) {
                        Toast.makeText(getActivity(), "网络未连接，请检查网络再试！", Toast.LENGTH_SHORT).show();
                        iv.setVisibility(View.VISIBLE);
                        Glide.with(getContext()).asGif().load(R.drawable.oa_develop).into(iv);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(int pos, View view) {

    }
}
