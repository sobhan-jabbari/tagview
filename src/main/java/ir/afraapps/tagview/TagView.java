package ir.afraapps.tagview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2015-12-31 11:47
 */
public class TagView extends View {

  /**
   * Border width
   */
  private float mBorderWidth = 0.0f;

  /**
   * Border radius
   */
  private float mBorderRadius;

  /**
   * Text size
   */
  private float mTextSize;

  /**
   * Horizontal padding for this view, include left & right padding(left & right padding are equal
   */
  private int mHorizontalPadding;

  /**
   * Vertical padding for this view, include top & bottom padding(top & bottom padding are equal)
   */
  private int mVerticalPadding;

  /**
   * TagView border color
   */
  private int mBorderColor;

  /**
   * TagView background color
   */
  private int mBackgroundColor;

  /**
   * TagView text color
   */
  private int mTextColor;

  /**
   * Whether this view clickable
   */
  private boolean isViewClickable;

  /**
   * The max length for this tagview view
   */
  private int mTagMaxLength;

  /**
   * OnTagClickListener for click action
   */
  private OnTagClickListener mOnTagClickListener;

  /**
   * Move slop(default 20px)
   */
  private int mMoveSlop = 20;

  /**
   * Scroll slop threshold
   */
  private int mSlopThreshold = 4;

  /**
   * How long trigger long click callback(default 900ms)
   */
  private int mLongPressTime = 900;


  /**
   * The distance between baseline and descent
   */
  private float bdDistance = 0.0f;

  private Paint mPaint;

  private RectF mRectF;
  private Rect  fontBound;

  private String mAbstractText, mOriginText;

  private boolean isUp, isMoved, isExecLongClick;

  private int mLastX, mLastY;

  private float textH, textW, fontH;

  private Typeface mTypeface;

  private LayoutMode mMode = LayoutMode.DEFAULT;
  private TagGroup mNotification;

  private boolean useDrawable = false;
  private boolean flag_on, preset_flag_on;

  private Runnable mLongClickHandle = new Runnable() {
    @Override
    public void run() {
      if (!isMoved && !isUp) {
        int state = ((TagGroup) getParent()).getTagViewState();
        if (state == ViewDragHelper.STATE_IDLE) {
          isExecLongClick = true;
          mOnTagClickListener.onTagLongClick(getPosition(), getText());
        }
      }
    }
  };

  public TagView(Context context, String text) {
    super(context);
    init(text);
  }

  private void init(String text) {
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mRectF = new RectF();
    fontBound = new Rect();
    mOriginText = text == null ? "" : text;
    preset_flag_on = flag_on = isViewClickable = false;
  }

  public void setNotification(TagGroup ly) {
    mNotification = ly;
  }

  private Drawable
    background_drawable_0,
    background_drawable_1,
    background_drawable_2,
    background_drawable_3,
    background_drawable_4,
    background_drawable_5;

  /**
   * setting of using drawable instead of using command attributes
   *
   * @param d0 this cannot be null if the drawable usage is active
   * @param d1 this can be null
   * @param d2 this can be null
   */
  public void setItemDrawableStates(@Nullable Drawable d0, @Nullable Drawable d1, @Nullable Drawable d2) {
    background_drawable_0 = d0;
    background_drawable_1 = d1;
    background_drawable_2 = d2;
    if (d0 != null) {
      useDrawable = true;
      if (d1 == null) {
        background_drawable_1 = d0;
      }
      if (d1 == null) {
        background_drawable_2 = d0;
      }
    }
  }

  public void setItemDrawableHardStates(@Nullable Drawable d0, @Nullable Drawable d1, @Nullable Drawable d2) {
    background_drawable_3 = d0;
    background_drawable_4 = d1;
    background_drawable_5 = d2;
    if (d0 != null) {
      useDrawable = true;
      if (d1 == null) {
        background_drawable_3 = d0;
      }
      if (d1 == null) {
        background_drawable_4 = d0;
      }
    }
  }

  private void onDealText() {
    if (!TextUtils.isEmpty(mOriginText)) {
      mAbstractText = mOriginText.length() <= mTagMaxLength ? mOriginText
        : mOriginText.substring(0, mTagMaxLength - 3) + "...";
    } else {
      mAbstractText = "";
    }
    mPaint.setTypeface(mTypeface);
    mPaint.setTextSize(mTextSize);
    // final Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
    // textH = fontMetrics.descent - fontMetrics.ascent;
    mPaint.getTextBounds(mAbstractText, 0, mAbstractText.length(), fontBound);

    fontH = Math.abs(mPaint.descent() + mPaint.ascent());
    textH = fontBound.height();
    textW = fontBound.width();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(mHorizontalPadding * 2 + (int) textW, mVerticalPadding * 2 + (int) textH);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mRectF.set(mBorderWidth, mBorderWidth, w - mBorderWidth, h - mBorderWidth);
  }

  private void setDrawableBound(Drawable d, Canvas c, RectF rec) {
    d.setBounds((int) rec.left, (int) rec.top, (int) rec.right, (int) rec.bottom);
    d.draw(c);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    canvas.save();
    if (!useDrawable) {

      mPaint.setStyle(Paint.Style.FILL);
      mPaint.setColor(mBackgroundColor);
      canvas.drawRoundRect(mRectF, mBorderRadius, mBorderRadius, mPaint);

      if (mBorderWidth > 0.0f) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setColor(mBorderColor);
        canvas.drawRoundRect(mRectF, mBorderRadius, mBorderRadius, mPaint);
      }

    } else {
      if (isFlag_on()) {
        if (isPresetFlag_on()) {
          setDrawableBound(background_drawable_4, canvas, mRectF);
        } else {
          setDrawableBound(background_drawable_1, canvas, mRectF);
        }
      } else {
        if (isPresetFlag_on()) {
          setDrawableBound(background_drawable_3, canvas, mRectF);
        } else {
          setDrawableBound(background_drawable_0, canvas, mRectF);
        }
      }

    }

    mPaint.setStyle(Paint.Style.FILL);
    mPaint.setColor(mTextColor);

    canvas.drawText(mAbstractText, getWidth() / 2 - textW / 2, getHeight() / 2 + fontH / 2 + bdDistance, mPaint);
    canvas.restore();
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    if (isViewClickable) {
      int y      = (int) event.getY();
      int x      = (int) event.getX();
      int action = event.getAction();
      switch (action) {
        case MotionEvent.ACTION_DOWN:
          getParent().requestDisallowInterceptTouchEvent(true);
          mLastY = y;
          mLastX = x;
          isMoved = false;
          break;

        case MotionEvent.ACTION_MOVE:
          if (Math.abs(mLastY - y) > mSlopThreshold || Math.abs(mLastX - x) > mSlopThreshold) {
            getParent().requestDisallowInterceptTouchEvent(false);
            isMoved = true;
            return false;
          }
          break;
      }
    }
    return super.dispatchTouchEvent(event);
  }

  private long register_down_time;

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (isViewClickable && mOnTagClickListener != null) {
      int x      = (int) event.getX();
      int y      = (int) event.getY();
      int action = event.getAction();
      switch (action) {
        case MotionEvent.ACTION_DOWN:
          mLastY = y;
          mLastX = x;
          isMoved = false;
          isUp = false;
          isExecLongClick = false;
          postDelayed(mLongClickHandle, mLongPressTime);
          register_down_time = System.currentTimeMillis();
          break;

        case MotionEvent.ACTION_MOVE:
          if (isMoved) {
            break;
          }
          if (Math.abs(mLastX - x) > mMoveSlop || Math.abs(mLastY - y) > mMoveSlop) {
            isMoved = true;
          }
          break;

        case MotionEvent.ACTION_UP:
          isUp = true;
                /*    if (!isExecLongClick && !isMoved) {
                        mNotification.notifyInternal((int) getTag());
                        mOnTagClickListener.onTagClick((int) getTag(), getText());
                    }*/
          if (!isMoved) {
            if (System.currentTimeMillis() - register_down_time < mLongPressTime) {
              mNotification.notifyInternal(getPosition());
              mOnTagClickListener.onTagClick(getPosition(), getText());
            }
          }

          break;
      }
      return true;
    }
    return true;
  }

  public String getText() {
    return mOriginText;
  }

  public boolean getIsViewClickable() {
    return isViewClickable;
  }

  public void setTagMaxLength(int maxLength) {
    this.mTagMaxLength = maxLength;
    onDealText();
  }

  private int getPosition() {
    return (int) getTag();
  }

  public void setOnTagClickListener(@Nullable OnTagClickListener listener) {
    this.mOnTagClickListener = listener;
    if (listener != null) {
      this.isViewClickable = true;
    }
  }

  public void setTagBackgroundColor(int color) {
    this.mBackgroundColor = color;
  }

  public void setTagBorderColor(int color) {
    this.mBorderColor = color;
  }

  public void setTagTextColor(int color) {
    this.mTextColor = color;
  }

  public void setBorderWidth(float width) {
    this.mBorderWidth = width;
  }

  public void setBorderRadius(float radius) {
    this.mBorderRadius = radius;
  }

  public void setTextSize(float size) {
    this.mTextSize = size;
    onDealText();
  }


  public void applyProfile(int[] profile) {
    setTagTextColor(profile[2]);
    if (useDrawable) return;
    setTagBackgroundColor(profile[1]);
    setTagBorderColor(profile[0]);
  }

  public void setMode(LayoutMode mode) {
    this.mMode = mode;
  }

  public void setHorizontalPadding(int padding) {
    this.mHorizontalPadding = padding;
  }

  public void setVerticalPadding(int padding) {
    this.mVerticalPadding = padding;
  }

  public interface OnTagClickListener {
    void onTagClick(int position, String text);

    void onTagLongClick(int position, String text);
  }

  public void setTypeface(Typeface typeface) {
    this.mTypeface = typeface;
    onDealText();
  }

  public void setBdDistance(float bdDistance) {
    this.bdDistance = bdDistance;
  }


  public boolean isFlag_on() {
    return flag_on;
  }

  public boolean isPresetFlag_on() {
    return preset_flag_on;
  }

  public void setFlag_on(boolean b) {
    if (useDrawable) {
      //setSelected(b);
    }
    this.flag_on = b;
  }

  public void setPresetFlag(boolean b) {
    this.preset_flag_on = b;
  }
}
