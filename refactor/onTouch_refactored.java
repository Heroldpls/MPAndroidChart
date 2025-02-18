public boolean onTouch(View v, MotionEvent event) {
    initializeVelocityTracker(event);

    if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
        cleanUpVelocityTracker();
    }

    if (mTouchMode == NONE) {
        mGestureDetector.onTouchEvent(event);
    }

    if (!mChart.isDragEnabled() && (!mChart.isScaleXEnabled() && !mChart.isScaleYEnabled())) {
        return true;
    }

    switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            handleActionDown(event);
            break;

        case MotionEvent.ACTION_POINTER_DOWN:
            handlePointerDown(event);
            break;

        case MotionEvent.ACTION_MOVE:
            handleActionMove(event);
            break;

        case MotionEvent.ACTION_UP:
            handleActionUp(event);
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

    // Update the chart's transformation matrix
    mMatrix = mChart.getViewPortHandler().refresh(mMatrix, mChart, true);

    return true;
}

private void initializeVelocityTracker(MotionEvent event) {
    if (mVelocityTracker == null) {
        mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(event);
}

private void cleanUpVelocityTracker() {
    if (mVelocityTracker != null) {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }
}

private void handleActionDown(MotionEvent event) {
    startAction(event);
    stopDeceleration();
    saveTouchStart(event);
}

private void handlePointerDown(MotionEvent event) {
    if (event.getPointerCount() < 2) return;

    mChart.disableScroll();
    saveTouchStart(event);

    mSavedXDist = getXDist(event);
    mSavedYDist = getYDist(event);
    mSavedDist = spacing(event);

    if (mSavedDist > 10f) {
        determineZoomMode();
    }

    midPoint(mTouchPointCenter, event);
}

private void determineZoomMode() {
    if (mChart.isPinchZoomEnabled()) {
        mTouchMode = PINCH_ZOOM;
    } else if (mChart.isScaleXEnabled() != mChart.isScaleYEnabled()) {
        mTouchMode = mChart.isScaleXEnabled() ? X_ZOOM : Y_ZOOM;
    } else {
        mTouchMode = mSavedXDist > mSavedYDist ? X_ZOOM : Y_ZOOM;
    }
}

private void handleActionMove(MotionEvent event) {
    if (mTouchMode == DRAG) {
        performDragIfAllowed(event);
    } else if (mTouchMode == X_ZOOM || mTouchMode == Y_ZOOM || mTouchMode == PINCH_ZOOM) {
        if (mChart.isScaleXEnabled() || mChart.isScaleYEnabled()) {
            performZoom(event);
        }
    } else if (shouldStartDrag(event)) {
        startDragging(event);
    }
}

private void performDragIfAllowed(MotionEvent event) {
    mChart.disableScroll();

    float x = mChart.isDragXEnabled() ? event.getX() - mTouchStartPoint.x : 0.f;
    float y = mChart.isDragYEnabled() ? event.getY() - mTouchStartPoint.y : 0.f;

    performDrag(event, x, y);
}

private boolean shouldStartDrag(MotionEvent event) {
    return mTouchMode == NONE &&
            Math.abs(distance(event.getX(), mTouchStartPoint.x, event.getY(), mTouchStartPoint.y)) > mDragTriggerDist &&
            mChart.isDragEnabled();
}

private void startDragging(MotionEvent event) {
    boolean shouldPan = !mChart.isFullyZoomedOut() || !mChart.hasNoDragOffset();

    if (shouldPan) {
        float distanceX = Math.abs(event.getX() - mTouchStartPoint.x);
        float distanceY = Math.abs(event.getY() - mTouchStartPoint.y);

        if ((mChart.isDragXEnabled() || distanceY >= distanceX) &&
                (mChart.isDragYEnabled() || distanceY <= distanceX)) {
            mLastGesture = ChartGesture.DRAG;
            mTouchMode = DRAG;
        }
    } else if (mChart.isHighlightPerDragEnabled()) {
        mLastGesture = ChartGesture.DRAG;
        performHighlightDrag(event);
    }
}

private void handleActionUp(MotionEvent event) {
    handleVelocity(event);

    if (mTouchMode == X_ZOOM || mTouchMode == Y_ZOOM || mTouchMode == PINCH_ZOOM || mTouchMode == POST_ZOOM) {
        mChart.calculateOffsets();
        mChart.postInvalidate();
    }

    mTouchMode = NONE;
    mChart.enableScroll();
    cleanUpVelocityTracker();
    endAction(event);
}

private void handleVelocity(MotionEvent event) {
    final VelocityTracker velocityTracker = mVelocityTracker;
    final int pointerId = event.getPointerId(0);
    velocityTracker.computeCurrentVelocity(1000, Utils.getMaximumFlingVelocity());

    final float velocityY = velocityTracker.getYVelocity(pointerId);
    final float velocityX = velocityTracker.getXVelocity(pointerId);

    if (Math.abs(velocityX) > Utils.getMinimumFlingVelocity() ||
            Math.abs(velocityY) > Utils.getMinimumFlingVelocity()) {

        if (mTouchMode == DRAG && mChart.isDragDecelerationEnabled()) {
            initiateDeceleration(event, velocityX, velocityY);
        }
    }
}

private void initiateDeceleration(MotionEvent event, float velocityX, float velocityY) {
    stopDeceleration();

    mDecelerationLastTime = AnimationUtils.currentAnimationTimeMillis();
    mDecelerationCurrentPoint.x = event.getX();
    mDecelerationCurrentPoint.y = event.getY();
    mDecelerationVelocity.x = velocityX;
    mDecelerationVelocity.y = velocityY;

    Utils.postInvalidateOnAnimation(mChart);
}