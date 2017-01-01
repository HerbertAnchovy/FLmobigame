package uk.ac.reading.sis05kol.mooc;

// Import additional Android libraries required:
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/* *************************** */
/* TheGame() class definition  */
/* *************************** */
public class TheGame extends GameThread {

    // Store the sound player class reference for SoundPool SFX.
    private SoundPlayer sound;

    // Store the number of Lives
    private int mLivesRemaining = 0;

    // Store the image of a ball.
    private Bitmap mBall;

    // Store the image of the smiley ball (Hillary).
    private Bitmap mSmileyBall;

    // Store the image of sad ball (Trump).
    private Bitmap mSadBall;

    // Store the image of the paddle used to hit the ball.
    private Bitmap mPaddle;

    // Store the X and Y position of the 'ball' images on the screen.
    // Note: The (x, y) point is the top left corner, not middle, of the ball.
    // Set values to -100 initially so images are not yet drawn on screen.
    private float mBallX = -100;
    private float mBallY = -100;
    private float mSmileyBallX = -100;
    private float mSmileyBallY = -100;
    private float[] mSadBallX = {-100, -100, -100};
    private float[] mSadBallY = new float[3];

    // Store speed (in pixels/second) of the ball in direction X and Y.
    private float mBallSpeedX = 0;
    private float mBallSpeedY = 0;

    // Store paddle's x position (Y position will always be bottom of the screen).
    private float mPaddleX = 0;

    // Store speed (pixels/second) of the paddle in direction X and Y.
    private float mPaddleSpeedX = 0;

    // Store swap images (allows images to be conditionally swapped during gameplay).
    private Bitmap mSadBall2;
    private Bitmap mSmileyBall2;

    // Store smiley ball shift value (allows smiley ball to be conditionally moved during gameplay).
    private int mSmileyBallShift = 0;

    // Store the min distance allowed between paddle and ball.
    // Used to detect/determine collision events.
    private float mMinDistanceBetweenBallAndPaddle = 0;

    /* *********************************************************************** */
    /* TheGame() is run before anything else, so we can prepare things here.  */
    /* *********************************************************************** */
    public TheGame(GameView gameView) {

        // Application 'house keeping'.
        super(gameView);

        // Prepare ball image so image can later be draw on screen, using a canvas.
        mBall = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.small_red_ball);

        // Prepare paddle image so image can later be draw on screen, using a canvas.
        mPaddle = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.yellow_ball);

        // Prepare smiley ball image so image can later be draw on screen, using a canvas.
        mSmileyBall = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.smiley_ball);

        // Prepare sad ball image so image can later be draw on screen, using a canvas.
        mSadBall = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.sad_ball);

        // Prepare smiley ball swap image so image can later be draw on screen, using a canvas.
        mSmileyBall2 = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.smiley_ball_2);

        // Prepare sad ball swap image so image can later be draw on screen, using a canvas.
        mSadBall2 = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.sad_ball_2);
    }

    /* ******************************************************************** */
    /* setupBeginning() is run before a new game (also after an old game).  */
    /* ******************************************************************** */
    @Override
    public void setupBeginning() {

        // Set number of lives to start with.

        if(mLivesRemaining == 0) {
            mLivesRemaining = 3;
            updateLives(mLivesRemaining);
        }

        // Initialise SoundPlayer object for SoundPool SFX use.
        sound = new SoundPlayer(mContext);

        // Initialise ball speeds based on device dimensions, mCanvasWidth and mCanvasHeight.
        mBallSpeedX = mCanvasWidth / 3;
        mBallSpeedY = mCanvasHeight / 3;

        // Place the ball in the middle of the screen.
        // mBall.Width() and mBall.getHeight() gives the height and width of the ball image.
        mBallX = mCanvasWidth / 2;
        mBallY = mCanvasHeight / 2;

        // Place paddle in the middle of the screen.
        mPaddleX = mCanvasWidth / 2;

        // Place smiley ball in the top middle of the screen.
        mSmileyBallX = mCanvasWidth / 2;
        mSmileyBallY = mSmileyBall.getHeight() / 2;

        // Position all sad balls in a pyramid formation underneath the smiley ball.
        mSadBallX[0] = mCanvasWidth / 3;
        mSadBallY[0] = mCanvasHeight / 3;

        mSadBallX[1] = mCanvasWidth - mCanvasWidth / 3;
        mSadBallY[1] = mCanvasHeight / 3;

        mSadBallX[2] = mCanvasWidth / 2;
        mSadBallY[2] = mCanvasHeight / 5;

        // Get the minimum distance between the ball and paddle.
        // Using Pythagoras, c^2 = a^2 + b^2 and hence c = sqrt(a^2 + b^2).
        // Note though, that we leave out the square root step to simplify
        // the calculations required during gameplay.
        mMinDistanceBetweenBallAndPaddle = (mPaddle.getWidth() / 2 + mBall.getWidth() / 2) * (mPaddle.getWidth() / 2 + mBall.getWidth() / 2);
    }

    /* ********************************************************************* */
    /* doDraw() is used to draw bitmap images on screen using canvas class.  */
    /* ********************************************************************* */
    @Override
    protected void doDraw(Canvas canvas) {
        // If there is no canvas, do nothing.
        // For #FLmobigame, it is OK not understanding what is happening here!
        if (canvas == null) return;

        // More 'house keeping'.
        super.doDraw(canvas);

        // The canvas.drawBitmap(bitmap, x, y, paint) method uses top left corner of bitmap as 0,0 reference point.
        // However, we use 0,0 as the middle of the bitmap; Hence we need to negate half of the width and height
        // of the ball image in order for the ball to appear on screen as intended.
        // Note: A paint of null means the image is drawn without any extra 'Paint' features.

        // Draw the ball image using the ball X and Y values.
        canvas.drawBitmap(mBall, mBallX - mBall.getWidth() / 2, mBallY - mBall.getHeight() / 2, null);

        // Draw paddle using X values at bottom of the screen.
        canvas.drawBitmap(mPaddle, mPaddleX - mPaddle.getWidth() / 2, mCanvasHeight - mPaddle.getHeight() / 2, null);

        // Draw smiley ball using X values at top of the screen.
        canvas.drawBitmap(mSmileyBall, mSmileyBallX - mSmileyBall.getWidth() / 2, mSmileyBallY - mSmileyBall.getHeight() / 2, null);

        // Loop through all of the sad balls...
        for (int i = 0; i < mSadBallX.length; i++) {
            // drawing each one in position i
            canvas.drawBitmap(mSadBall, mSadBallX[i] - mSadBall.getWidth() / 2, mSadBallY[i] - mSadBall.getHeight() / 2, null);
        }
    }

    // Simple method run whenever device screen touch event occurs.
    @Override
    protected void actionOnTouch(float x, float y) {
        // Move the ball to the x position of the touch
        mPaddleX = x;
    }

    // Simple method run whenever the device moves around its axes.
    @Override
    protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {
        // Change the paddle speed.
        mPaddleSpeedX = mPaddleSpeedX + 70f * xDirection;

        // If paddle is outside the screen and moving further away
        // move it into the screen and set its speed to 0.
        if (mPaddleX <= 0 && mPaddleSpeedX < 0) {
            mPaddleSpeedX = 0;
            mPaddleX = 0;
        }
        if (mPaddleX >= mCanvasWidth && mPaddleSpeedX > 0) {
            mPaddleSpeedX = 0;
            mPaddleX = mCanvasWidth;
        }

    }

    // Simple method which swaps one smiley ball image for another, when required.
    private void swapSmileyBallImages() {
        if (mSmileyBall != mSmileyBall2) {
            mSmileyBall = mSmileyBall2; // Swap smiley ball image.
        } else // Prepare smiley ball image so we can draw it on the screen.
            mSmileyBall = BitmapFactory.decodeResource
                    (mGameView.getContext().getResources(),
                            R.drawable.smiley_ball);
      }

    // Simple method which swaps one sad ball image for another, when required.
    private void swapSadBallImages() {
        if (mSadBall != mSadBall2) {
            mSadBall = mSadBall2; // Swap sad ball image.
        } else // Prepare smiley ball image so we can draw it on the screen.
            mSadBall = BitmapFactory.decodeResource
                (mGameView.getContext().getResources(),
                        R.drawable.sad_ball);
    }

    /* *************************************************************************** */
    /* updateGame() is run just before the game "scenario" is rendered on screen . */
    /* *************************************************************************** */
    @Override
    protected void updateGame(float secondsElapsed) {
        float distanceBetweenBallAndPaddle;

        // Check for paddle collision.
        if (mBallSpeedY > 0) {
            // Execute collision behaviour logic and play collision (hit) sound.
            if(updateBallCollision(mPaddleX, mCanvasHeight)) { sound.playHitSound(); }
        }

        // Move the ball's X and Y using the speed (pixels/sec).
        mBallX = mBallX + secondsElapsed * mBallSpeedX;
        mBallY = mBallY + secondsElapsed * mBallSpeedY;

        // Move the paddle's X and Y using the speed (pixels/sec).
        mPaddleX = mPaddleX + secondsElapsed * mPaddleSpeedX;

        // Check if the ball hits either the left side or the right side of the screen.
        // BUT only cause ball to rebound if the ball is moving towards that side of the screen.
        // Ball rebound is actioned by reversing the polarity of the ball's X direction value.
        if ((mBallX <= mBall.getWidth() / 2 && mBallSpeedX < 0) || (mBallX >= mCanvasWidth - mBall.getWidth() / 2 && mBallSpeedX > 0)) {
            mBallSpeedX = -mBallSpeedX;
        }

        // Check for smiley ball collision.
        if(updateBallCollision(mSmileyBallX, mSmileyBallY)) {
            // Increase score by 3 if manage to hit smiley ball (i.e. hit Hillary).
            updateScore(3);
            // If score reaches 3, start shifting smiley ball to RHS.
            if(getScore() > 3) {
                mSmileyBallShift = 2;
            }
            sound.playHillarySound(); // Hillary speaks... but only if she's been hit!
            swapSmileyBallImages();   // Swap smiley ball image for fun.
            resetPaddlePosition();

            // Set win threshold and behaviour in event of a win.
            if(score >= 10) {
                sound.playWinSound();
                setState(GameThread.STATE_WIN);
            }
        }

        // Loop through all sad balls...
        for (int i = 0; i < mSadBallX.length; i++) {
            // and execute collision logic between sad ball in position i and ball.
            if(updateBallCollision(mSadBallX[i], mSadBallY[i])) {
                sound.playTrumpSound(); // Play random Trumpism when collision with sad ball occurs.
                swapSadBallImages();    // Swap sad ball images for fun.
                updateScore(-1);        // Deduct 1 point if you hit Trump. Harsh?
            }
        }

        // If the ball goes out of the top of the screen, change (reverse) the direction of the
        // ball in the Y direction.
        if (mBallY <= mBall.getWidth() / 2 && mBallSpeedY < 0) {
            mBallSpeedY = -mBallSpeedY;
        }

        // Apply smiley ball X positional shift value.
        mSmileyBallX = mSmileyBallX + mSmileyBallShift;

        // If smiley ball reaches LH edge..
        if(mSmileyBallX >= (mCanvasWidth - mSmileyBall.getWidth()/2))
            mSmileyBallShift = -2; //  reverse the direction of the ball shift.
        // If smiley ball reaches RH edge...
        if(mSmileyBallX <= (mSmileyBall.getWidth()/2))
            mSmileyBallShift = 2;  // reverse the direction of the ball shift.

        // If the ball goes out of the bottom of the screen...
        if (mBallY >= mCanvasHeight) {
            // reduce number of lives by 1.
            mLivesRemaining--;
            updateLives(mLivesRemaining);
            // If lives remain, play whoops sound and put game into pause state, ready for next play.
            if (mLivesRemaining > 0) {
                sound.playWhoopsSound();
                setupBeginning();
                setState(GameThread.STATE_PAUSE);
            }
            // If no lives left then the game is lost.
            else{
                // So play a lose sound (Yep, it's that tw*t Trump again)...
                sound.playLoseSound();
                // ...and set game to lose state.
                setState(GameThread.STATE_LOSE);

            }
        }
    }

    /* *********************************************************************** */
    /* updateBallCollision() is used to execute collision logic, as required.  */
    /* *********************************************************************** */
    private boolean updateBallCollision(float x, float y) {

        // Get actual distance (Pythagoras without the square root, remember?) between the mBall
        // and the ball being checked.
        float distanceBetweenBallAndPaddle = (x - mBallX) * (x - mBallX) + (y - mBallY) * (y - mBallY);

        // Check if the actual distance is lower than that allowed (i.e. if a collision has occurred).
        if (mMinDistanceBetweenBallAndPaddle >= distanceBetweenBallAndPaddle) {
            // Get the present velocity (this should also be the speed going away after the collision).
            float velocityOfBall = (float) Math.sqrt(mBallSpeedX * mBallSpeedX + mBallSpeedY * mBallSpeedY);

            // Change the direction of the ball.
            mBallSpeedX = mBallX - x;
            mBallSpeedY = mBallY - y;

            // If rebound is nearly horizontal (y = 0), change y to a larger angle.
            // (Otherwise ball bounces across the screen for ages!).
            if((mBallSpeedY >= -1.5) && (mBallSpeedY <= 1.5)) {
                mBallSpeedY = (float)0.2*mCanvasHeight; // Change angle by a factor of 20%...
                                                        // Yep, will need to experiment here!
            }

            // Get the velocity after the collision.
            float newVelocityOfBall = (float) Math.sqrt(mBallSpeedX * mBallSpeedX + mBallSpeedY * mBallSpeedY);

            // Using the fraction between the original speed and present speed to calculate the needed
            // velocities in X and Y to get the original speed but with the new angle.
            mBallSpeedX = mBallSpeedX * velocityOfBall / newVelocityOfBall;
            mBallSpeedY = mBallSpeedY * velocityOfBall / newVelocityOfBall;

            return true; // Flags that a collision has occurred.
        }
        return false; // Flags that no collision has occurred.
    }

    // Simple method repositions paddle to bottom centre of screen.
    private void resetPaddlePosition() {
        mPaddleX = mCanvasWidth / 2;
     }

}
// This file is part of the course "Begin Programming: Build your first mobile game"
// (aka #FLmobigame) from FutureLearn.com.
// Copyright: University of Reading and Karsten Lundqvist.