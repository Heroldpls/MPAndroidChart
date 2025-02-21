public boolean onTouch(View v, MotionEvent event) {

    if (mVelocityTracker == null) {
        mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(event);

    if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    if (mTouchMode == NONE) {
        mGestureDetector.onTouchEvent(event);
    }

    if (!mChart.isDragEnabled() && (!mChart.isScaleXEnabled() && !mChart.isScaleYEnabled()))
        return true;

    // Handle touch events here...
    switch (event.getAction() & MotionEvent.ACTION_MASK) {

        case MotionEvent.ACTION_DOWN:

            startAction(event);

            stopDeceleration();

            saveTouchStart(event);

            break;

        case MotionEvent.ACTION_POINTER_DOWN:

            if (event.getPointerCount() >= 2) {

                mChart.disableScroll();

                saveTouchStart(event);

                // get the distance between the pointers on the x-axis
                mSavedXDist = getXDist(event);

                // get the distance between the pointers on the y-axis
                mSavedYDist = getYDist(event);

                // get the total distance between the pointers
                mSavedDist = spacing(event);

                if (mSavedDist > 10f) {

                    if (mChart.isPinchZoomEnabled()) {
                        mTouchMode = PINCH_ZOOM;
                    } else {
                        if (mChart.isScaleXEnabled() != mChart.isScaleYEnabled()) {
                            mTouchMode = mChart.isScaleXEnabled() ? X_ZOOM : Y_ZOOM;
                        } else {
                            mTouchMode = mSavedXDist > mSavedYDist ? X_ZOOM : Y_ZOOM;
                        }
                    }
                }

                // determine the touch-pointer center
                midPoint(mTouchPointCenter, event);
            }
            break;

        case MotionEvent.ACTION_MOVE:

            if (mTouchMode == DRAG) {

                mChart.disableScroll();

                float x = mChart.isDragXEnabled() ? event.getX() - mTouchStartPoint.x : 0.f;
                float y = mChart.isDragYEnabled() ? event.getY() - mTouchStartPoint.y : 0.f;

                performDrag(event, x, y);

            } else if (mTouchMode == X_ZOOM || mTouchMode == Y_ZOOM || mTouchMode == PINCH_ZOOM) {

                mChart.disableScroll();

                if (mChart.isScaleXEnabled() || mChart.isScaleYEnabled())
                    performZoom(event);

            } else if (mTouchMode == NONE
                    && Math.abs(distance(event.getX(), mTouchStartPoint.x, event.getY(),
                    mTouchStartPoint.y)) > mDragTriggerDist) {

                if (mChart.isDragEnabled()) {

                    boolean shouldPan = !mChart.isFullyZoomedOut() ||
                            !mChart.hasNoDragOffset();

                    if (shouldPan) {

                        float distanceX = Math.abs(event.getX() - mTouchStartPoint.x);
                        float distanceY = Math.abs(event.getY() - mTouchStartPoint.y);

                        // Disable dragging in a direction that's disallowed
                        if ((mChart.isDragXEnabled() || distanceY >= distanceX) &&
                                (mChart.isDragYEnabled() || distanceY <= distanceX)) {

                            mLastGesture = ChartGesture.DRAG;
                            mTouchMode = DRAG;
                        }

                    } else {

                        if (mChart.isHighlightPerDragEnabled()) {
                            mLastGesture = ChartGesture.DRAG;

                            if (mChart.isHighlightPerDragEnabled())
                                performHighlightDrag(event);
                        }
                    }

                }

            }
            break;

        case MotionEvent.ACTION_UP:

            final VelocityTracker velocityTracker = mVelocityTracker;
            final int pointerId = event.getPointerId(0);
            velocityTracker.computeCurrentVelocity(1000, Utils.getMaximumFlingVelocity());
            final float velocityY = velocityTracker.getYVelocity(pointerId);
            final float velocityX = velocityTracker.getXVelocity(pointerId);

            if (Math.abs(velocityX) > Utils.getMinimumFlingVelocity() ||
                    Math.abs(velocityY) > Utils.getMinimumFlingVelocity()) {

                if (mTouchMode == DRAG && mChart.isDragDecelerationEnabled()) {

                    stopDeceleration();

                    mDecelerationLastTime = AnimationUtils.currentAnimationTimeMillis();

                    mDecelerationCurrentPoint.x = event.getX();
                    mDecelerationCurrentPoint.y = event.getY();

                    mDecelerationVelocity.x = velocityX;
                    mDecelerationVelocity.y = velocityY;

                    Utils.postInvalidateOnAnimation(mChart); // This causes computeScroll to fire, recommended for this by
                    // Google
                }
            }

            if (mTouchMode == X_ZOOM ||
                    mTouchMode == Y_ZOOM ||
                    mTouchMode == PINCH_ZOOM ||
                    mTouchMode == POST_ZOOM) {

                // Range might have changed, which means that Y-axis labels
                // could have changed in size, affecting Y-axis size.
                // So we need to recalculate offsets.
                mChart.calculateOffsets();
                mChart.postInvalidate();
            }

            mTouchMode = NONE;
            mChart.enableScroll();

            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }

            endAction(event);

            break;
        case MotionEvent.ACTION_POINTER_UP:
            Utils.velocityTrackerPointerUpCleanUpIfNecessary(event, mVelocityTracker);

            mTouchMode = POST_ZOOM;
            break;

        case MotionEvent.ACTION_CANCEL:

            mTouchMode = NONE;
            endAction(event);
            break;
    }

    // perform the transformation, update the chart
    mMatrix = mChart.getViewPortHandler().refresh(mMatrix, mChart, true);

    return true; // indicate event was handled
}