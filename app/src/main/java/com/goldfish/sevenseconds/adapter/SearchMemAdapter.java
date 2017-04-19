package com.goldfish.sevenseconds.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.LightingColorFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.goldfish.sevenseconds.activities.LogActivity;
import com.goldfish.sevenseconds.activities.SearchActivity;
import com.goldfish.sevenseconds.bean.MemoryContext;
import com.goldfish.sevenseconds.fragment.SquareFragment;
import com.goldfish.sevenseconds.http.MemoryHttpUtil;
import com.goldfish.sevenseconds.http.UserHttpUtil;
import com.goldfish.sevenseconds.service.NetWorkUtils;
import com.goldfish.sevenseconds.tools.DensityUtil;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.BarActivity;
import com.goldfish.sevenseconds.activities.MemoryActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchMemAdapter extends BaseRecyclerAdapter<SearchMemAdapter.memViewHolder> {
    private List<MemoryContext> list;
    private int largeCardHeight, smallCardHeight;
    private Context context;
    private memViewHolder holder;
    private memViewHolder currentHolder;
    private int currentPosition;
    private int position;
    private MemoryContext startNow;

    /*
     * sub = 0
     * collect = 1
     * dislike = 2
     * like = 3
     * refreshAllTotally = 4
     */

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

    public SearchMemAdapter(List<MemoryContext> list, Context context) {
        this.list = list;
        largeCardHeight = DensityUtil.dip2px(context, 150);
        smallCardHeight = DensityUtil.dip2px(context, 100);
        this.context = context;
    }

    /**
     * 异步操作
     */
    class DownTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... params) {
            String result = "Failed";
            if(params[0].equals(1)) { result = collect(params[1]); }
            else if (params[0].equals(4)) { result = refreshAllTotally(params[1]); }
            else if (params[0].equals(0)) { result = Sub(params[1]); }
            else if (params[0].equals(3)) { result = like(params[1]); }
            else if (params[0].equals(2)) { result = dislike(params[1]); }
            else if (params[0].equals(5)) { result = refreshStartTotally(); }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Succeed in collect")) { showAddOne(); }
            else if (result.equals("Succeed in sub")) { refreshAllCount(); }
            else if (result.equals("Succeed in like memory")) { refreshAllCount(); }
            else if (result.equals("Succeed in unlike")) { refreshAllCount(); }
            else if (result.equals("Succeed in refreshAllTotally")) { refreshUI(); }
            else if (result.equals("Succeed in refreshStartTotally")) { refreshAllUI(); }
            else { Toast.makeText(context, result, Toast.LENGTH_SHORT).show(); }
        }
    }

    // 收藏
    private String collect(int postion) {
        String result = "Failed in collect";
        MemoryContext now = list.get(postion);
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", LogActivity.user);
            jo.put("memoryId", now.getMemoryId());
            jo.put("time", SearchActivity.getCollectTime());
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
        new DownTask().execute(4, currentPosition);
    }

    // 刚开始更新
    public void refreshStartCount() {
        new DownTask().execute(5);
    }

    // 更新UI
    private void refreshAllUI() {
        if (startNow.getReviewCount() > 0) {
            if (startNow.getReviewCount() <= 99) {
                holder.commentNum.setText(String.valueOf(startNow.getReviewCount()));
            }
            else {
                holder.commentNum.setText("99+");
            }
        }
        else holder.commentNum.setText("");
        if (startNow.getLikeCount() > 0)
            if (startNow.getLikeCount() <= 99)
                holder.likeNum.setText(String.valueOf(startNow.getLikeCount()));
            else
                holder.likeNum.setText("99+");
        else holder.likeNum.setText("");

        if (Build.VERSION.SDK_INT >= 21) {
            if (startNow.getIsLike()) {
                holder.pre_like.setAlpha((float) 0.9);
                holder.pre_like.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lightorange)));
            }
            else {
                holder.pre_like.setAlpha((float) 0.4);
                holder.pre_like.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            }
            if (startNow.getIsAdd()) {
                holder.pre_add.setAlpha((float) 0.9);
                holder.pre_add.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lightorange)));
            }
            else {
                holder.pre_add.setAlpha((float) 0.4);
                holder.pre_add.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            }
        }
        else {
            if (startNow.getIsLike()) {
                holder.pre_like.setAlpha((int) (255 * 0.9));
                holder.pre_like.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xffff8900));
            }
            else {
                holder.pre_like.setAlpha((int) (255 * 0.4));
                holder.pre_like.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFF000000));
            }
            if (startNow.getIsAdd()) {
                holder.pre_add.setAlpha((int) (255 * 0.9));
                holder.pre_add.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xffff8900));
            }
            else {
                holder.pre_add.setAlpha((int) (255 * 0.4));
                holder.pre_add.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFF000000));
            }
        }
    }

    // 同上
    private String refreshStartTotally() {
        String result = "Failed in refreshStartTotally";
        MemoryContext now = list.get(position);
        try {
            JSONObject jo = new JSONObject();
            jo.put("memoryId", now.getMemoryId());
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
                result = "Succeed in refreshStartTotally";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 加载忆单信息
    private String refreshAllTotally(int position) {
        String result = "Failed in refreshAllTotally";
        MemoryContext now = list.get(position);
        try {
            JSONObject jo = new JSONObject();
            jo.put("memoryId", now.getMemoryId());
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
    private void refreshUI() {
        MemoryContext now = list.get(currentPosition);

        if (now.getReviewCount() > 0) {
            if (now.getReviewCount() <= 99) {
                currentHolder.commentNum.setText(String.valueOf(now.getReviewCount()));
            }
            else {
                currentHolder.commentNum.setText("99+");
            }
        }
        else currentHolder.commentNum.setText("");

        if (now.getLikeCount() > 0)
            if (now.getLikeCount() <= 99)
                currentHolder.likeNum.setText(String.valueOf(now.getLikeCount()));
            else
                currentHolder.likeNum.setText("99+");
        else currentHolder.likeNum.setText("");

        if (Build.VERSION.SDK_INT >= 21) {
            if (now.getIsLike()) {
                currentHolder.pre_like.setAlpha((float) 0.9);
                currentHolder.pre_like.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lightorange)));
            }
            else {
                currentHolder.pre_like.setAlpha((float) 0.4);
                currentHolder.pre_like.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            }
            if (now.getIsAdd()) {
                currentHolder.pre_add.setAlpha((float) 0.9);
                currentHolder.pre_add.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lightorange)));
            }
            else {
                currentHolder.pre_add.setAlpha((float) 0.4);
                currentHolder.pre_add.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            }
        }
        else {
            if (now.getIsLike()) {
                currentHolder.pre_like.setAlpha((int) (255 * 0.9));
                currentHolder.pre_like.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xffff8900));
            }
            else {
                currentHolder.pre_like.setAlpha((int) (255 * 0.4));
                currentHolder.pre_like.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFF000000));
            }
            if (now.getIsAdd()) {
                currentHolder.pre_add.setAlpha((int) (255 * 0.9));
                currentHolder.pre_add.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xffff8900));
            }
            else {
                currentHolder.pre_add.setAlpha((int) (255 * 0.4));
                currentHolder.pre_add.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFF000000));
            }
        }
    }
    // 取消收藏
    private String Sub(int position) {
        String result = "Failed in sub";
        MemoryContext now = list.get(position);
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", LogActivity.user);
            jo.put("memoryId", now.getMemoryId());
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
    private String like(int position) {
        String result;
        MemoryContext now = list.get(position);
        try {
            JSONObject jo = new JSONObject();
            jo.put("memoryId", now.getMemoryId());
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
    private String dislike(int position) {
        String result = "Failed in unlike";
        MemoryContext now = list.get(position);
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", LogActivity.user);
            jo.put("memoryId", now.getMemoryId());
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
        BarActivity.isCollectOrAdd = true;
        SearchActivity.showAddOne();
    }
    // 抓取正文
    private void getMainContext(int position) {
        MemoryContext now = list.get(position);
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
    public void onBindViewHolder(final memViewHolder holderTemp, final int position, boolean isItem) {
        holder = holderTemp;
        startNow = list.get(position);
        final MemoryContext now = list.get(position);
        String memID = now.getMemoryId();
        this.position = position;
        // 更新底部按钮
        refreshStartCount();
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
        getMainContext(position);
        holder.bkg.setImageBitmap(now.getCover());
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            holder.memView.getLayoutParams().height = position % 2 != 0 ? largeCardHeight : smallCardHeight;
        }

        // 两个按钮的点击事件
        holder.pre_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentHolder = holderTemp;
                currentPosition = position;
                if (NetWorkUtils.getAPNType(context) != 0) {
                    Log.d("position", String.valueOf(position));
                    if (now.getIsAdd()) {
                        DownTask downTask = new DownTask();
                        downTask.execute(0, position);
                    }
                    else {
                        DownTask downTask = new DownTask();
                        downTask.execute(1, position);
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
                MemoryContext now = list.get(position);
                currentPosition = position;
                currentHolder = holderTemp;
                if (NetWorkUtils.getAPNType(context) != 0) {
                    if (now.getIsLike()) {
                        new DownTask().execute(2, position);
                    }
                    else {
                        Log.d("Main", now.getMemoryId());
                        new DownTask().execute(3, position);
                    }
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    holder.likeNum.setText("99+");
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
                Intent intent = new Intent(context, MemoryActivity.class);
                intent.putExtra("memoryID", now.getMemoryId());
                context.startActivity(intent);
            }
        });
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
