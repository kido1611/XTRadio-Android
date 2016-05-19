package com.pheelicks.visualizer.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.pheelicks.visualizer.AudioData;
import com.pheelicks.visualizer.FFTData;

public class TileBarGraphRenderer extends Renderer {
    private int mDivisions;
    private Paint mPaint;
    private int mDbFuzz;
    private int mDbFuzzFactor;

    /**
     * Renders the FFT data as a series of lines, in histogram form
     *
     * @param divisions - must be a power of 2. Controls how many lines to draw
     * @param paint - Paint to draw lines with
     * @param dbfuzz - final dB display adjustment
     * @param dbFactor - dbfuzz is multiplied by dbFactor.
     */
    public TileBarGraphRenderer(int divisions, Paint paint,
            int dbfuzz, int dbFactor) {
        super();
        mDivisions = divisions;
        mPaint = paint;
        mDbFuzz = dbfuzz;
        mDbFuzzFactor = dbFactor;
    }

    public void setPaint(Paint paint){
    	mPaint = paint;
    }
    
    @Override
    public void onRender(Canvas canvas, AudioData data, Rect rect) {
        // Do nothing, we only display FFT data
    }

    @Override
    public void onRender(Canvas canvas, FFTData data, Rect rect) {
        for (int i = 0; i < data.bytes.length / mDivisions; i++) {
            mFFTPoints[i * 4] = i * 4 * mDivisions;
            mFFTPoints[i * 4 + 2] = i * 4 * mDivisions;
            byte rfk = data.bytes[mDivisions * i];
            byte ifk = data.bytes[mDivisions * i + 1];
            float magnitude = (rfk * rfk + ifk * ifk);
            int dbValue = (int) (10 * Math.log10(magnitude));

            mFFTPoints[i * 4 + 1] = rect.height();
            mFFTPoints[i * 4 + 3] = rect.height() - (dbValue * mDbFuzzFactor + mDbFuzz);
        }

        canvas.drawLines(mFFTPoints, mPaint);
    }
}