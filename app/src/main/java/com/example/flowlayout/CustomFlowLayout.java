package com.example.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kcq on 2020/4/22
 */
public class CustomFlowLayout extends ViewGroup {
    private static final String TAG = "CustomFlowLayout";
    private int mHorizonalSpacing = 10;
    private int mVerticalSpacing = 10;
    List<List<View>> allLines = new ArrayList<>();   //按照行记录所有view
    List<Integer> lineHeights = new ArrayList<>();
    public CustomFlowLayout(Context context) {
        super(context);
    }

    public CustomFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        initMeasure();
        int childCount = getChildCount();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int selftWidth = MeasureSpec.getSize(widthMeasureSpec);
        int selftHeight = MeasureSpec.getSize(heightMeasureSpec);

        List<View> line = new ArrayList<>();
        int lineWidthUsed = 0;
        int lineHeight = 0;

        int parentNeedWidth = 0;     //子view对当前view的宽度要求
        int parentNeedHeight = 0;    //子view对当前view的高度要求

        if(childCount>1){
            //测量所有子view
            for(int i=0;i<childCount;i++){
                View childView = getChildAt(i);
                int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, childView.getLayoutParams().width);
                int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, childView.getLayoutParams().height);
                childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                if (lineWidthUsed + childView.getMeasuredWidth() + mHorizonalSpacing > selftWidth) {
                    //换行
                    allLines.add(line);
                    lineHeights.add(lineHeight);
                    parentNeedHeight = parentNeedHeight + lineHeight + mVerticalSpacing;
                    parentNeedWidth = Math.max(parentNeedWidth, (lineWidthUsed + mHorizonalSpacing));
                    lineHeight = 0;
                    lineWidthUsed = 0;
                    line = new ArrayList<>();      //line.clear()   此处不可用line.clear(),否则保存到allLines中的View会被清除
                }

                line.add(childView);
                lineWidthUsed = lineWidthUsed + childView.getMeasuredWidth() + mHorizonalSpacing;
                lineHeight = Math.max(lineHeight, childView.getMeasuredHeight());

                //最后一行最后一个view
                if (i == childCount - 1) {
                    allLines.add(line);
                    lineHeights.add(lineHeight);
                    parentNeedHeight = parentNeedHeight + lineHeight;
                    parentNeedWidth = Math.max(parentNeedWidth, lineWidthUsed + mHorizonalSpacing);
                }
            }

            //获得子view对当前view的尺寸要求后，结合父view对当前view的要求，得出最终的尺寸
            int realWidth = getRealDimension(widthMeasureSpec, parentNeedWidth);
            int realHeight = getRealDimension(heightMeasureSpec, parentNeedHeight);
            setMeasuredDimension(realWidth, realHeight);    //该方法必须在onMeasure中被调用，否则会报异常
        }
    }

    private void initMeasure(){
        lineHeights = new ArrayList<>();
        allLines = new ArrayList<>();
    }

    /**
     * @param parentMS  父view传过来的MeasureSpec
     * @param childNeedDimension 子view1经测量后得到需要的当前view的尺寸
     * @return 当前view最终的测量尺寸
     */
    private int getRealDimension(int parentMS, int childNeedDimension) {
        int resultSize;
        int parentMSSize = MeasureSpec.getSize(parentMS);
        int parentMSMode = MeasureSpec.getMode(parentMS);
        resultSize = (parentMSMode == MeasureSpec.EXACTLY) ? parentMSSize : childNeedDimension;
        return resultSize;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lines = allLines.size();
        List<View> lineViews;
        int lineViewCount ;
        int curL = 0;
        int curT = getPaddingTop();
        Log.d(TAG, "onLayout lines:"+lines);
        for (int i = 0; i < lines; i++) {
            lineViews = allLines.get(i);
            lineViewCount = lineViews.size();
            Log.d(TAG, "onLayout lineViewCount:"+lineViewCount);
            curL = getPaddingLeft();
            //每一行的每个view依次布局
            for (int j = 0; j < lineViewCount; j++) {
                View childView = lineViews.get(j);
                int bottom = curT + childView.getMeasuredHeight();
                int right = curL + childView.getMeasuredWidth();
                childView.layout(curL,curT,right,bottom);
                curL = right + mHorizonalSpacing;
            }
            curT = curT + lineHeights.get(i)+mVerticalSpacing;
        }
    }
}
