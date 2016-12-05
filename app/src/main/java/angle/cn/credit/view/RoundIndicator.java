package angle.cn.credit.view;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import angle.cn.credit.R;


/**
 * Created by Administrator on 2016/12/1.
 */

public class RoundIndicator extends View {

    private int maxNum;
    private int startAngle;
    private int sweepAngle;
    private Context context;
    private int sweepInWidth;
    private int sweepOutWidth;
    private Paint paint;
    private Paint paint_2;
    private Paint paint_3;
    private Paint paint_4;
    private int mWidth;
    private int mHeight;
    private int radius;
    private int currentNum;

    public RoundIndicator(Context context) {
        this(context, null);
    }

    public RoundIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundIndicator);
        maxNum = array.getInt(R.styleable.RoundIndicator_maxNum, 500);
        startAngle = array.getInt(R.styleable.RoundIndicator_startAngle, 160);
        sweepAngle = array.getInt(R.styleable.RoundIndicator_sweepAngle, 220);
        //内外圆弧的宽度
        sweepInWidth = dp2Px(8);
        sweepOutWidth = dp2Px(3);
        array.recycle();
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xffffffff);
        paint_2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_4 = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        if (wMode == MeasureSpec.EXACTLY) {
            mWidth = wSize;
        } else {
            mWidth = dp2Px(300);
        }
        if (hMode == MeasureSpec.EXACTLY) {
            mHeight = hSize;
        } else {
            mHeight = dp2Px(400);
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        radius = getMeasuredWidth() / 4;
        canvas.save();
        canvas.translate(mWidth / 2, mWidth / 2);
        drawRound(canvas);//画内外圆弧
        drawScale(canvas);//画刻度
        drawIndicator(canvas);//画当前进度值
        drawCenterText(canvas);
        canvas.restore();
    }
    private void drawCenterText(Canvas canvas) {
        canvas.save();
        paint_4.setStyle(Paint.Style.FILL);
        paint_4.setTextSize(radius/2);
        paint_4.setColor(0xffffffff);
        canvas.drawText(currentNum+"",-paint_4.measureText(currentNum+"")/2,0,paint_4);
        paint_4.setTextSize(radius/4);
        String content = "信用";
        if(currentNum < maxNum*1/5){
            content += text[0];
        }else if(currentNum >= maxNum*1/5 && currentNum < maxNum*2/5){
            content += text[1];
        }else if(currentNum >= maxNum*2/5 && currentNum < maxNum*3/5){
            content += text[2];
        }else if(currentNum >= maxNum*3/5 && currentNum < maxNum*4/5){
            content += text[3];
        }else if(currentNum >= maxNum*4/5){
            content += text[4];
        }
        Rect r = new Rect();
        paint_4.getTextBounds(content,0,content.length(),r);
        canvas.drawText(content,-r.width()/2,r.height()+20,paint_4);
        canvas.restore();
    }

    private int[] indicatorColor = {0xffffffff, 0x00ffffff, 0x99ffffff, 0xffffffff};

    private void drawIndicator(Canvas canvas) {
        canvas.save();
        paint_2.setStyle(Paint.Style.STROKE);
        int sweep;
        if (currentNum <= maxNum) {
            sweep = (int) ((float) currentNum / (float) maxNum * sweepAngle);
        } else {
            sweep = sweepAngle;
        }
        paint_2.setStrokeWidth(sweepOutWidth);
        Shader shader = new SweepGradient(0, 0, indicatorColor, null);
        paint_2.setShader(shader);
        int w = dp2Px(10);
        RectF rectF = new RectF(-radius - w, -radius - w, radius + w, radius + w);
        canvas.drawArc(rectF, startAngle, sweep, false, paint_2);
        float x = (float) ((radius + dp2Px(10)) * Math.cos(Math.toRadians(startAngle + sweep)));
        float y = (float) ((radius + dp2Px(10)) * Math.sin(Math.toRadians(startAngle + sweep)));
        paint_3.setStyle(Paint.Style.FILL);
        paint_3.setColor(0xffffffff);
        paint_3.setMaskFilter(new BlurMaskFilter(dp2Px(3), BlurMaskFilter.Blur.SOLID));
        canvas.drawCircle(x, y, dp2Px(3), paint_3);
        canvas.restore();
    }

    private String[] text = {"较差", "中等", "良好", "优秀", "极好"};

    private void drawScale(Canvas canvas) {
        canvas.save();
        float angle = (float) sweepAngle / 30;
        canvas.rotate(-270 + startAngle);
        for (int i = 0; i <= 30; i++) {
            if (i % 6 == 0) {
                paint.setStrokeWidth(dp2Px(2));
                paint.setAlpha(0x70);
                canvas.drawLine(0, -radius - sweepInWidth / 2, 0, -radius + sweepInWidth / 2 + dp2Px(1), paint);
                drawText(canvas, i * maxNum / 30 + "", paint);
            } else {//画细刻度
                paint.setStrokeWidth(dp2Px(1));
                paint.setAlpha(0x50);
                canvas.drawLine(0, -radius - sweepInWidth / 2, 0, -radius + sweepInWidth / 2, paint);
            }
            if (i == 3 || i == 9 || i == 15 || i == 21 || i == 27) {
                paint.setStrokeWidth(dp2Px(2));
                paint.setAlpha(0x50);
                drawText(canvas, text[(i - 3) / 6], paint);
            }
            canvas.rotate(angle);
        }
        canvas.restore();
    }

    private void drawText(Canvas canvas, String s, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(sp2px(8));
        float width = paint.measureText(s);
        canvas.drawText(s, -width / 2, -radius + dp2Px(15), paint);
        paint.setStyle(Paint.Style.STROKE);
    }

    private void drawRound(Canvas canvas) {
        canvas.save();
        //内圆
        paint.setAlpha(0x40);
        paint.setStrokeWidth(sweepInWidth);
        RectF rectF = new RectF(-radius, -radius, radius, radius);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);
        //外圆
        paint.setStrokeWidth(sweepOutWidth);
        int w = dp2Px(10);
        RectF rectF2 = new RectF(-radius - w, -radius - w, radius + w, radius + w);
        canvas.drawArc(rectF2, startAngle, sweepAngle, false, paint);
        canvas.restore();
    }

    private int dp2Px(int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * @param @param  context
     * @param @param  spValue
     * @param @return
     * @return int
     * @throws
     * @方法名称: sp2px
     * @描述: 将sp值转换为px值
     * @author 徐纪伟
     * 2014年10月24日 下午10:01:01
     */
    public int sp2px(float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
        invalidate();
    }

    public void setCurrentNumAnim(int num) {
        float duration = (float)Math.abs(num-currentNum)/maxNum *1500+500; //根据进度差计算动画时间
        ObjectAnimator anim = ObjectAnimator.ofInt(this,"currentNum",num);
        anim.setDuration((long) Math.min(duration,2000));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                int color = calculateColor(value);
                setBackgroundColor(color);
            }
        });
        anim.start();
    }
    private int calculateColor(int value){
        ArgbEvaluator evealuator = new ArgbEvaluator();
        float fraction = 0;
        int color = 0;
        if(value <= maxNum/2){
            fraction = (float)value/(maxNum/2);
            color = (int) evealuator.evaluate(fraction,0xFFFF6347,0xFFFF8C00); //由红到橙
        }else {
            fraction = ( (float)value-maxNum/2 ) / (maxNum/2);
            color = (int) evealuator.evaluate(fraction,0xFFFF8C00,0xFF00CED1); //由橙到蓝
        }
        return color;
    }
}
