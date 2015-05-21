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

	private Drawable compassDrawable;
	private Paint targetPaint;

	private double targetRadius;
	private double degreeRad;
	private double targetDegreeRad;

	private int compassSize;
	private int targetCircleSize;
	private int centerX, centerY;

	public CompassView(Context context) {
		super(context);
		init(context);
	}

	public CompassView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public CompassView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
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

	private void init(Context context) {
		compassSize = context.getResources().getDimensionPixelOffset(R.dimen.compass_size);
		targetCircleSize = context.getResources().getDimensionPixelOffset(R.dimen.target_circle_size);
		targetRadius = context.getResources().getDimensionPixelOffset(R.dimen.target_circle_radius);

		targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		targetPaint.setColor(context.getResources().getColor(R.color.target_color));

		compassDrawable = context.getResources().getDrawable(R.drawable.compass);
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
