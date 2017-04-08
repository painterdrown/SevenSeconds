package com.goldfish.sevenseconds.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.goldfish.sevenseconds.activities.LogActivity;
import com.goldfish.sevenseconds.bean.MemoryContext;
import com.goldfish.sevenseconds.fragment.SquareFragment;
import com.goldfish.sevenseconds.http.MemoryHttpUtil;
import com.goldfish.sevenseconds.http.UserHttpUtil;
import com.goldfish.sevenseconds.service.NetWorkUtils;
import com.goldfish.sevenseconds.tools.DensityUtil;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.BarActivity;
import com.goldfish.sevenseconds.activities.MemoryActivity;
import com.goldfish.sevenseconds.item.MemorySheetPreview;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemAdapter extends BaseRecyclerAdapter<MemAdapter.memViewHolder> {
    private List<MemoryContext> list;
    private int largeCardHeight, smallCardHeight;
    private Context context;
    private String memID;
    private MemoryContext now;
    private memViewHolder holder;


    public static class memViewHolder extends RecyclerView.ViewHolder {

        public View memView;
        public TextView title;
        public TextView contents;
        public TextView tags;
        public TextView pre_time;
        public ImageView bkg;
        public int position;
        public ImageView pre_add;
        public ImageView pre_like;
        public TextView likeNum;
        public TextView commentNum;

        public memViewHolder(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                this.title = (TextView) itemView
                        .findViewById(R.id.premem_title);
                this.contents = (TextView) itemView
                        .findViewById(R.id.premem_contents);
                this.bkg = (ImageView) itemView.findViewById(R.id.premem_bgr);
                this.memView = itemView.findViewById(R.id.card_view);
                this.pre_add = (ImageView) itemView.findViewById(R.id.premem_button);
                this.pre_time = (TextView)itemView.findViewById(R.id.preview_time);
                this.tags = (TextView)itemView.findViewById(R.id.pre_tag);
                this.pre_like = (ImageView) itemView.findViewById(R.id.premem_like);
                this.likeNum = (TextView) itemView.findViewById(R.id.premem_like_num);
                this.commentNum = (TextView) itemView.findViewById(R.id.premem_com_num);
            }
        }
    }

    public MemoryContext getItem(int position) {
        if (position < list.size())
            return list.get(position);
        else
            return null;
    }

    public MemAdapter(List<MemoryContext> list, Context context) {
        this.list = list;
        largeCardHeight = DensityUtil.dip2px(context, 150);
        smallCardHeight = DensityUtil.dip2px(context, 100);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(final memViewHolder holderTemp, final int position, boolean isItem) {
        now = list.get(position);
        memID = now.getMemoryId();
        this.holder = holderTemp;
        // 更新底部按钮
        refreshAllCount();
        holder.pre_time.setText(now.getTime());
        String labels = "";
        for (int i = 0; i < now.getLabel().length; i++) {
            if (!now.getLabel()[i].equals("")) {
                labels += "#" + now.getLabel()[i] + " ";
            }
        }
        if (labels.equals("# ")) {
            labels = "";
        }
        holder.tags.setText(labels);
        holder.title.setText(now.getTitle());
        getMainContext();
        holder.bkg.setImageBitmap(now.getCover());
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            holder.memView.getLayoutParams().height = position % 2 != 0 ? largeCardHeight : smallCardHeight;
        }

        // 两个按钮的点击事件
        holder.pre_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetWorkUtils.getAPNType(context) != 0) {
                    Log.d("position", String.valueOf(position));
                    if (now.getIsAdd()) {
                        DownTask downTask = new DownTask();
                        downTask.execute("Sub");
                    }
                    else {
                        DownTask downTask = new DownTask();
                        downTask.execute("collect", now.getMemoryId(), SquareFragment.getCollectTime());
                    }
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(context,
                            "哎呀~网络连接有问题！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.pre_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetWorkUtils.getAPNType(context) != 0) {
                    if (now.getIsLike()) {
                        new DownTask().execute("dislike");
                    }
                    else {
                        Log.d("Main", now.getMemoryId());
                        new DownTask().execute("like");
                    }
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(context,
                            "哎呀~网络连接有问题！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.memView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemoryContext now = list.get(position);
                Intent intent = new Intent(BarActivity.barActivity,MemoryActivity.class);
                intent.putExtra("memoryID", now.getMemoryId());
                BarActivity.barActivity.startActivity(intent);
            }
        });
    }

    /**
     * 异步操作
     */
    class DownTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "Failed";
            if(params[0].equals("collect")) { result = collect(params); }
            else if (params[0].equals("refreshAllTotally")) { result = refreshAllTotally(); }
            else if (params[0].equals("Sub")) { result = Sub(); }
            else if (params[0].equals("like")) { result = like(); }
            else if (params[0].equals("dislike")) { result = dislike(); }
            return result;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Succeed in collect")) { showAddOne(); }
            else if (result.equals("Succeed in sub")) { refreshAllCount(); }
            else if (result.equals("Succeed in like memory")) { refreshAllCount(); }
            else if (result.equals("Succeed in unlike")) { refreshAllCount(); }
            else if (result.equals("Succeed in refreshAllTotally")) { refreshUI(); }
            else { Toast.makeText(context, result, Toast.LENGTH_SHORT).show(); }
        }
    }

    // 收藏
    private String collect(String... params) {
        String result = "Failed in collect";
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", LogActivity.user);
            jo.put("memoryId", params[1]);
            jo.put("time", params[2]);
            JSONObject jo_return = UserHttpUtil.collectMemory(jo);
            if (jo_return.getBoolean("ok")) {
                result = "Succeed in collect";
            }
            else {
                result = jo_return.getString("errMsg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    // 更新忆单的信息
    public void refreshAllCount() {
        new DownTask().execute("refreshAllTotally");
    }
    // 加载忆单信息
    private String refreshAllTotally() {
        String result = "Failed in refreshAllTotally";
        try {
            JSONObject jo = new JSONObject();
            jo.put("memoryId", memID);
            JSONObject jo_review = MemoryHttpUtil.getCommentCount(jo);
            JSONObject jo_like = MemoryHttpUtil.getLikeCount(jo);
            JSONObject jo_add = MemoryHttpUtil.getCollectCount(jo);
            jo.put("account", LogActivity.user);
            JSONObject jo_isLike =  UserHttpUtil.ifLikeMemory(jo);
            JSONObject jo_isAdd = UserHttpUtil.ifCollectMemory(jo);
            if (jo_like.getBoolean("ok") && jo_review.getBoolean("ok") && jo_add.getBoolean("ok")) {
                now.setLikeCount(jo_like.getInt("count"));
                now.setReviewCount(jo_review.getInt("count"));
                now.setIsAdd(jo_isAdd.getBoolean("ok"));
                now.setIsLike(jo_isLike.getBoolean("ok"));
                now.setCollectCount(jo_add.getInt("count"));
                result = "Succeed in refreshAllTotally";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    // 更新UI
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void refreshUI() {
        if (now.getCollectCount() > 0)
            if (now.getCollectCount() <= 99)
                holder.commentNum.setText(String.valueOf(now.getCollectCount()));
            else
                holder.commentNum.setText("99+");
        else holder.commentNum.setText("");
        if (now.getLikeCount() > 0)
            if (now.getLikeCount() <= 99)
                holder.likeNum.setText(String.valueOf(now.getLikeCount()));
            else
                holder.likeNum.setText("99+");
        else holder.likeNum.setText("");
        if (now.getIsLike()) {
            holder.pre_like.setAlpha((float) 0.9);
            holder.pre_like.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.lightorange)));
        }
        else {
            holder.pre_like.setAlpha((float) 0.4);
            holder.pre_like.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.black)));
        }
        if (now.getIsAdd()) {
            holder.pre_add.setAlpha((float) 0.9);
            holder.pre_add.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.lightorange)));
        }
        else {
            holder.pre_add.setAlpha((float) 0.4);
            holder.pre_add.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.black)));
        }
    }
    // 取消收藏
    private String Sub() {
        String result = "Failed in sub";
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", LogActivity.user);
            jo.put("memoryId", memID);
            JSONObject jo_return = UserHttpUtil.uncollectMemory(jo);
            if (jo_return.getBoolean("ok")) {
                now.setIsAdd(false);
                result = "Succeed in sub";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    //点赞
    private String like() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("memoryId", memID);
            Log.d("Async", memID);
            jo.put("account", LogActivity.user);
            JSONObject jo_return = UserHttpUtil.likeMemory(jo);
            if (jo_return.getBoolean("ok")) {
                result = "Succeed in like memory";
                now.setIsLike(true);
            }
            else {
                result = "Failed in like Memory";
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            result = "Failed in like Memory";
        }
        return result;
    }

    // 取消点赞
    private String dislike() {
        String result = "Failed in unlike";
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", LogActivity.user);
            jo.put("memoryId", memID);
            JSONObject jo_return = UserHttpUtil.unlikeMemory(jo);
            if (jo_return.getBoolean("ok")) {
                now.setIsLike(false);
                result = "Succeed in unlike";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    // +1动画
    private void showAddOne() {
        refreshAllCount();
        SquareFragment.showAddOne();
    }
    // 抓取正文
    private void getMainContext() {
        String mainContext = now.getContext();
        String[] text = new String[100];
        int textCount = 0;
        ArrayList<Integer>image = new ArrayList<Integer>();
        int imageCount = 0;
        String temp;
        String tempImage;
        int lastpos1 = 0;
        int pos1 = 0;
        int pos2 = 0;
        int isValid;
        while (true) {
            lastpos1 = pos1;
            pos1 = mainContext.indexOf("<", pos1);
            if (pos1 == -1) {
                if (text[textCount] == null) text[textCount] = "";
                text[textCount] += mainContext.substring(lastpos1);
                textCount++;
                break;
            } else {
                temp = mainContext.substring(lastpos1, pos1);
                if (text[textCount] == null) text[textCount] = "";
                text[textCount] += temp;
            }
            pos2 = mainContext.indexOf(">", pos1 + 1);
            if (pos2 == -1) {
                text[textCount] = mainContext.substring(lastpos1);
                textCount++;
                break;
            }
            tempImage = mainContext.substring(pos1, pos2 + 1);
            isValid = tempImage.indexOf("img", 0);
            if (isValid != -1) {
                String regEx = "[^0-9]";//匹配指定范围内的数字
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(tempImage);
                String string = m.replaceAll(" ").trim();
                String[] strArr = string.split(" ");
                image.add(imageCount, Integer.parseInt(strArr[0]));
                textCount++;
                // 标志要放图
                text[textCount++] = "<img>";
                imageCount++;
            } else {
                if (text[textCount] == null) text[textCount] = "";
                text[textCount]+= tempImage;
            }
            pos1 = pos2 + 1;
        }
        String contents = "";
        for (int i = 0; i < textCount; i++) {
            if (text[i].equals("<img>")) {
                if (i != 0) contents += "\n";
            } else {
                contents += text[i];
            }
        }
        holder.contents.setText(contents);
    }

    @Override
    public  memViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.square_memory_item, parent, false);
        memViewHolder vh = new memViewHolder(v, true);
        return vh;
    }

    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {
        return list.size();
    }

    @Override
    public memViewHolder getViewHolder(View view) {
        return new memViewHolder(view, false);
    }

    public void setData(List<MemoryContext> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void insert(MemoryContext now, int position) {
        insert(list, now, position);
    }

    public void remove(int position) {
        remove(list, position);
    }

    public void clear() {
        clear(list);
    }

}
