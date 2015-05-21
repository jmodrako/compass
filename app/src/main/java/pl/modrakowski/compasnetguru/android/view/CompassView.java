package pl.modrakowski.compasnetguru.android.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.annotations.res.DrawableRes;
import pl.modrakowski.compasnetguru.R;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;

/**
 * User: jacek
 * Date: 21/05/15
 * Time: 12:13
 * modrakowski.pl/android
 */
@EView
public class CompassView extends View {

	@DimensionPixelOffsetRes(R.dimen.target_circle_radius) int targetRadius;
	@DimensionPixelOffsetRes(R.dimen.compass_size) int compassSize;
	@DimensionPixelOffsetRes(R.dimen.target_circle_size) int targetCircleSize;

	@DrawableRes(R.drawable.compass) Drawable compassDrawable;
	@ColorRes(R.color.target_color) int targetColor;

	private Paint targetPaint;

	private double degreeRad;
	private double targetDegreeRad;
	private int centerX, centerY;

	public CompassView(Context context) {
		super(context);
		init();
	}

	public CompassView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public CompassView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	public void setDegreeRad(double degreeRad) {
		this.degreeRad = degreeRad;
		invalidate();
	}

	public void setTargetDegree(double targetBearingDeg) {
		this.targetDegreeRad = targetBearingDeg;
		invalidate();
	}

	@Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		targetPaint.setColor(targetColor);

		final int width = getMeasuredWidth();
		final int height = getMeasuredHeight();

		centerX = width / 2;
		centerY = height / 2;

		final int halfsize = compassSize / 2;
		compassDrawable.setBounds(centerX - halfsize, centerY - halfsize, centerX + halfsize, centerY + halfsize);
	}

	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawCompassDrawable(canvas);
		drawTargetDot(canvas);
	}

	private void init() {
		targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	private void drawTargetDot(Canvas canvas) {
		final double firstDeg = toDegrees(targetDegreeRad);
		final double secondDeg = toDegrees(degreeRad);
		final double result = secondDeg - firstDeg;
		final double resultRad = toRadians(result);

		final float endX = centerX - (float) (cos(resultRad) * targetRadius);
		final float endY = centerY + (float) (sin(resultRad) * targetRadius);

		canvas.drawCircle(endX, endY, targetCircleSize, targetPaint);
	}

	private void drawCompassDrawable(Canvas canvas) {
		canvas.save();
		final double secondDeg = toDegrees(degreeRad);
		canvas.rotate((float) (270 - secondDeg), centerX, centerY);
		compassDrawable.draw(canvas);
		canvas.restore();
	}
}
