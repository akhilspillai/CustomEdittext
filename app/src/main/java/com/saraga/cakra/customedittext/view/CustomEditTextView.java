package com.saraga.cakra.customedittext.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import com.saraga.cakra.customedittext.R;

/**
 * TODO: document your custom view class.
 */
public class CustomEditTextView extends View {
    private String mExampleString = "Hello"; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;
    private float margin;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private Rect mTextRect;

    public CustomEditTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public CustomEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CustomEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        setFocusableInTouchMode(true);

        this.setOnKeyListener(new OnKeyListener() {
            @Override   public
            boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                } else if(event.getUnicodeChar() ==
                        (int)MyEditable.ONE_UNPROCESSED_CHARACTER.charAt(0)) {
                    return true;
                }

                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyBoard();
                    return true;
                }
                else if(keyCode == KeyEvent.KEYCODE_DEL) {
                    deleteExampleString();
                    return true;
                }
                return false;
            }
        });

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CustomEditTextView, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.CustomEditTextView_exampleString);

        mExampleColor = a.getColor(
                R.styleable.CustomEditTextView_exampleColor,
                mExampleColor);

        mExampleDimension = a.getDimension(
                R.styleable.CustomEditTextView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.CustomEditTextView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.CustomEditTextView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Load attributes
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        mTextRect = new Rect();
        mTextPaint.getTextBounds(mExampleString, 0, mExampleString.length(), mTextRect);
        mTextHeight = mTextRect.height();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.round(mTextWidth);
        } else {
            width = Math.round(mTextWidth);
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.round(mTextHeight) * 2;
        } else {
            height = Math.round(mTextHeight) * 2;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int contentWidth = getWidth();
        int contentHeight = getHeight();
        // Draw the text.
        canvas.drawText(mExampleString,
                (contentWidth - mTextWidth) / 2,
                (contentHeight + mTextHeight) / 2,
                mTextPaint);

    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        MyInputConnection baseInputConnection =
                new MyInputConnection(this, false);
        outAttrs.actionLabel = null;
        outAttrs.inputType = InputType.TYPE_NULL;
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;

        return baseInputConnection;
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }
    private void deleteExampleString() {
        if (mExampleString.length() > 0) {
            StringBuilder sb = new StringBuilder(mExampleString);
            sb.delete(sb.length() - 1, sb.length());
            mExampleString = sb.toString();
            invalidateTextPaintAndMeasurements();
            invalidate();
        }
    }


    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param text The example string attribute value to append.
     */

    public void appendExampleString(CharSequence text) {
        mExampleString += text;
        invalidateTextPaintAndMeasurements();
        invalidate();
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */

    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
        requestLayout();
        invalidate();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hideKeyBoard();
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        requestFocus();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            showKeyboard();
        }
        return true;
    }
}

class MyInputConnection extends BaseInputConnection {
    private SpannableStringBuilder myEditable;
    CustomEditTextView myEditText;

    public MyInputConnection(View targetView, boolean fullEditor) {
        super(targetView, fullEditor);
        myEditText = (CustomEditTextView) targetView;
    }

    public Editable getEditable() {
        if(Build.VERSION.SDK_INT >= 14) {
            if (myEditable == null) {
                myEditable = new MyEditable(
                        MyEditable.ONE_UNPROCESSED_CHARACTER);
                Selection.setSelection(myEditable, 1);
            } else {
                int myEditableLength = myEditable.length();
                if (myEditableLength == 0) {
                    myEditable.append(
                            MyEditable.ONE_UNPROCESSED_CHARACTER);
                    Selection.setSelection(myEditable, 1);
                }
            }
            return myEditable;
        } else {
            return super.getEditable();
        }
    }

    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        if ((Build.VERSION.SDK_INT >= 14)
                && (beforeLength == 1 && afterLength == 0)) {
            return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                    && super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
        } else {
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    public boolean commitText(CharSequence text, int newCursorPosition) {
        myEditable.append(text);
        myEditText.appendExampleString(text);
        return true;
    }
}

class MyEditable extends SpannableStringBuilder {
    MyEditable(CharSequence source) {
        super(source);
    }

    public static CharSequence ONE_UNPROCESSED_CHARACTER = "/";

    @NonNull
    @Override
    public SpannableStringBuilder replace(final int spannableStringStart,
                                          final int spannableStringEnd,
                                          CharSequence replacementSequence,
                                          int replacementStart, int replacementEnd) {
        if (replacementEnd > replacementStart) {
            super.replace(0, length(), "", 0, 0);
            return super.replace(0, 0, replacementSequence, replacementStart, replacementEnd);
        } else if (spannableStringEnd > spannableStringStart) {
            super.replace(0, length(), "", 0, 0);
            return super.replace(0, 0, ONE_UNPROCESSED_CHARACTER, 0, 1);
        }

        return super.replace(spannableStringStart, spannableStringEnd,
                replacementSequence, replacementStart, replacementEnd);
    }
}