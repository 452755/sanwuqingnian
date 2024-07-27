package com.smartown.tableview.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:<a href='https://github.com/kungfubrother'>https://github.com/kungfubrother</a>
 * <p>
 * CrateTime:2016-12-08 18:30
 * <p>
 * Description:表格控件
 */
public class TableView extends View {

    /**
     * 单元格基准宽度，设权重的情况下，为最小单元格宽度
     */
    private float unitColumnWidth;
    private float rowHeight;
    private float dividerWidth;
    private int dividerColor;
    private float textSize;
    private int textColor;
    private int headerColor;
    private float headerTextSize;
    private int headerTextColor;

    private int rowCount;
    private int columnCount;

    private Paint paint;

    private float[] columnLefts;
    private float[] columnWidths;
    private float[] rowHeights;

    private int[] columnWeights;
    private List<String[]> tableContents;

    public TableView(Context context) {
        super(context);
        init(null);
    }

    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        tableContents = new ArrayList<>();
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TableView);
            unitColumnWidth = typedArray.getDimensionPixelSize(R.styleable.TableView_unitColumnWidth, 0);
            rowHeight = typedArray.getDimensionPixelSize(R.styleable.TableView_rowHeight, Util.dip2px(getContext(), 36));
            dividerWidth = typedArray.getDimensionPixelSize(R.styleable.TableView_dividerWidth, 1);
            dividerColor = typedArray.getColor(R.styleable.TableView_dividerColor, Color.parseColor("#E1E1E1"));
            textSize = typedArray.getDimensionPixelSize(R.styleable.TableView_textSize, Util.dip2px(getContext(), 10));
            textColor = typedArray.getColor(R.styleable.TableView_textColor, Color.parseColor("#999999"));
            headerColor = typedArray.getColor(R.styleable.TableView_headerColor, Color.parseColor("#00ffffff"));
            headerTextSize = typedArray.getDimensionPixelSize(R.styleable.TableView_headerTextSize, Util.dip2px(getContext(), 10));
            headerTextColor = typedArray.getColor(R.styleable.TableView_headerTextColor, Color.parseColor("#999999"));
            typedArray.recycle();
        } else {
            unitColumnWidth = 0;
            rowHeight = Util.dip2px(getContext(), 36);
            dividerWidth = 1;
            dividerColor = Color.parseColor("#E1E1E1");
            textSize = Util.dip2px(getContext(), 10);
            textColor = Color.parseColor("#999999");
            headerColor = Color.parseColor("#00ffffff");
            headerTextSize = Util.dip2px(getContext(), 10);
            headerTextColor = Color.parseColor("#111111");
        }
        setHeader("Header1", "Header2").addContent("Column1", "Column2");
        initTableSize();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //通过权重计算最小单元格宽度
        int weightSum = 0;
        if (columnWeights != null) {
            for (int i = 0; i < columnCount; i++) {
                if (columnWeights.length > i) {
                    weightSum += columnWeights[i];
                } else {
                    weightSum += 1;
                }
            }
        } else {
            //默认等分，每列权重为1
            weightSum = columnCount;
        }

        //计算宽度及列宽
        float width;
        if (unitColumnWidth == 0) {
            //未设置宽度，根据控件宽度来确定最小单元格宽度
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            width = getMeasuredWidth();
            unitColumnWidth = (width - (columnCount + 1) * dividerWidth) / weightSum;
        } else {
            //设置了最小单元格宽度
            width = dividerWidth * (columnCount + 1) + unitColumnWidth * weightSum;
        }
        //计算高度
        calculateColumns();
        float height = measureTableHeight();
        setMeasuredDimension((int) width, (int) height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateColumns();
        drawHeader(canvas);
        drawFramework(canvas);
        drawContent(canvas);
    }

    /**
     * 画表头
     *
     * @param canvas
     */
    private void drawHeader(Canvas canvas) {
        paint.setColor(headerColor);
        canvas.drawRect(dividerWidth, dividerWidth, getWidth() - dividerWidth, rowHeight + dividerWidth, paint);
    }

    /**
     * 画整体表格框架
     */
    private void drawFramework(Canvas canvas) {
        paint.setColor(dividerColor);
        for (int i = 0; i < columnCount + 1; i++) {
            if (i == 0) {
                //最左侧分割线
                canvas.drawRect(0, 0, dividerWidth, getHeight(), paint);
                continue;
            }
            if (i == columnCount) {
                //最右侧分割线
                canvas.drawRect(getWidth() - dividerWidth, 0, getWidth(), getHeight(), paint);
                continue;
            }
            canvas.drawRect(columnLefts[i], 0, columnLefts[i] + dividerWidth, getHeight(), paint);
        }
        canvas.drawRect(0, 0, getWidth(), dividerWidth, paint);
        float rowTop = 0;
        for (int i = 0; i < rowCount; i++) {
            rowTop += dividerWidth + rowHeights[i];
            canvas.drawRect(0, rowTop, getWidth(), rowTop + dividerWidth, paint);
        }
    }

    /**
     * 画内容
     */
    private void drawContent(Canvas canvas) {
        float[] rowHeights = measureRowHeight();
        float rowStart = 0;
        for (int i = 0; i < rowCount; i++) {
            final String[] rowContent = tableContents.size() > i ? tableContents.get(i) : new String[0];
            if (i == 0) {
                //设置表头文字画笔样式
                paint.setColor(headerTextColor);
                paint.setTextSize(headerTextSize);
                rowStart = dividerWidth;
            } else {
                rowStart += dividerWidth + rowHeights[i - 1];
            }
            for (int j = 0; j < columnCount; j++) {
                if (rowContent.length > j) {
                    TextPaint textPaint = new TextPaint();
                    textPaint.setColor(textColor);
                    textPaint.setTextSize(textSize);
                    textPaint.setAntiAlias(true);
                    StaticLayout layout = new StaticLayout(rowContent[j], textPaint, (int)(columnWidths[j]),Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
                    canvas.save();
                    canvas.translate(columnLefts[j], measureLayoutTranslateY(rowStart, rowHeights[i], layout));
                    layout.draw(canvas);
                    canvas.restore();
                }
            }
            if (i == 0) {
                //恢复表格文字画笔样式
                paint.setColor(textColor);
                paint.setTextSize(textSize);
            }
        }
    }

    /**
     * 计算每列左端坐标及列宽
     */
    private void calculateColumns() {
        columnLefts = new float[columnCount];
        columnWidths = new float[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnLefts[i] = getColumnLeft(i);
            columnWidths[i] = getColumnWidth(i);
        }
    }

    private float getColumnLeft(int columnIndex) {
        if (columnWeights == null) {
            return columnIndex * (unitColumnWidth + dividerWidth);
        }
        //计算左边的权重和
        int weightSum = 0;
        for (int i = 0; i < columnIndex; i++) {
            if (columnWeights.length > i) {
                weightSum += columnWeights[i];
            } else {
                weightSum += 1;
            }
        }
        return columnIndex * dividerWidth + weightSum * unitColumnWidth;
    }

    private float getColumnWidth(int columnIndex) {
        if (columnWeights == null) {
            return unitColumnWidth;
        }
        int weight = columnWeights.length > columnIndex ? columnWeights[columnIndex] : 1;
        return weight * unitColumnWidth;
    }

    /**
     * 实现文本表格垂直居中
     * add by @linkgoo
     */
    private float measureLayoutTranslateY(float rowStart, float rowHeight, StaticLayout layout) {
        return rowStart + rowHeight / 2  - layout.getHeight() / 2;
    }

    private float getTextBaseLine(float rowStart, Paint paint) {
        final Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (rowStart + (rowStart + rowHeight) - fontMetrics.bottom - fontMetrics.top) / 2;
    }

    /**
     * 设置表格内容
     */
    public TableView clearTableContents() {
        columnWeights = null;
        tableContents.clear();
        return this;
    }

    /**
     * 设置每列的权重
     *
     * @param columnWeights
     * @return
     */
    public TableView setColumnWeights(int... columnWeights) {
        this.columnWeights = columnWeights;
        return this;
    }

    /**
     * 设置表头
     *
     * @param headers
     */
    public TableView setHeader(String... headers) {
        tableContents.add(0, headers);
        return this;
    }

    /**
     * 设置表格内容
     */
    public TableView addContent(String... contents) {
        tableContents.add(contents);
        return this;
    }

    /**
     * 设置表格内容
     */
    public TableView addContents(List<String[]> contents) {
        tableContents.addAll(contents);
        return this;
    }

    /**
     * 初始化行列数
     */
    private void initTableSize() {
        rowCount = tableContents.size();
        if (rowCount > 0) {
            //如果设置了表头，根据表头数量确定列数
            columnCount = tableContents.get(0).length;
        }
    }

    /**
     * 设置数据后刷新表格
     */
    public void refreshTable() {
        initTableSize();
        requestLayout();
//         invalidate();
    }

    /**
     * 计算各行表格最大高度
     * add by @linkgoo
     */
    private float[] measureRowHeight() {
        float[] rowHeights = new float[rowCount];
        for (int i = 0; i < rowCount; i++) {
            final String[] rowContent = tableContents.size() > i ? tableContents.get(i) : new String[0];
            rowHeights[i] = rowHeight;
            for (int j = 0; j < columnCount; j++) {
                TextPaint textPaint = new TextPaint();
                textPaint.setColor(textColor);
                textPaint.setTextSize(textSize);
                textPaint.setAntiAlias(true);
                StaticLayout layout = new StaticLayout(rowContent[j], textPaint, (int)(columnWidths[j]),Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
                System.out.println("columnWidths" + columnWidths[j]);
                int textHeight = layout.getHeight();
                rowHeights[i] = textHeight > rowHeights[i] ? textHeight : rowHeights[i];
            }
        }
       return rowHeights;
    }

    /**
     * 计算表格高度
     * add by @linkgoo
     */
    private float measureTableHeight() {
        float tableHeight = 0;
       if(tableContents.size() > 0) {
           rowHeights = measureRowHeight();
           for (int i = 0; i < rowHeights.length; i++) {
               tableHeight += rowHeights[i] + dividerWidth;
           }
           tableHeight += dividerWidth;
       } else {
           tableHeight = (dividerWidth + rowHeight) * rowCount + dividerWidth;
       }
       return tableHeight;
    }

}
