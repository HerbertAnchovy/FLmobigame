package uk.ac.reading.sis05kol.mooc;

//Other parts of the android libraries that we use
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class TheGame extends GameThread{

    //Will store the image of a ball
    private Bitmap mBall;

    private Bitmap mPaddle; // Added @3.5.
    private float mPaddleX = 0; // Added @3.5. Note: Paddle can only move from side to side (X direction).

    private float mPaddleSpeedX = 0; // Added @3.5.

    //The X and Y position of the ball on the screen (middle of ball)
    private float mBallX = -100; // Positions ball off screen at start. Why is this good?
    private float mBallY = -100; // (note that top left is 0,0}.

    //The speed (pixel/second) of the ball in direction X and Y
    private float mBallSpeedX = 0;
    private float mBallSpeedY = 0;

    //This is run before anything else, so we can prepare things here
    public TheGame(GameView gameView) {
        //House keeping
        super(gameView);

        //Prepare the mBall image so we can draw it on the screen (using a canvas)
        mBall = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.small_red_ball);

        //Prepare the paddle image so we can draw it on the screen (using a canvas)
        mPaddle = BitmapFactory.decodeResource   // Added @3.5.
                (gameView.getContext().getResources(),
                        R.drawable.yellow_ball); // Yellow better against green background!
    }

    //This is run before a new game (also after an old game)
    @Override
    public void setupBeginning() {
        //Initialise speeds
        mBallSpeedX = mCanvasWidth / 3; // Relate x-axis speed to width of screen; instead of an arbitrary value.
        mBallSpeedY = mCanvasWidth / 3;

        //Place the ball in the middle of the screen.
        //mBall.Width() and mBall.getHeigh() gives us the height and width of the image of the ball
        mBallX = mCanvasWidth / 2;
        mBallY = mCanvasHeight / 2;

        // Place paddle in middle of screen at the beginning.
        mPaddleX = mCanvasWidth / 2;

        // We don't ball moving at start.
        mPaddleSpeedX = 0;  // Added @3.5.
    }

    @Override
    protected void doDraw(Canvas canvas) {
        //If there isn't a canvas to draw on do nothing
        //It is ok not understanding what is happening here
        if(canvas == null) return;

        super.doDraw(canvas);

        //draw the image of the ball using the X and Y of mBall
        //drawBitmap uses top left corner as reference, we use middle of picture
        //null means that we will use the image without any extra features (called Paint)
        canvas.drawBitmap(mBall, mBallX - mBall.getWidth() / 2, mBallY - mBall.getHeight() / 2, null);

        // Added @3.5.
        //Draw the image of the paddle using the X and Y of mPaddle.
        //drawBitmap uses top LH corner as reference (0,0).
        //getWidth and getHeight are divided by 2 because, in our case, we want to reference the middle of the image bitmap.
        //Bottom of screen is mCanvasHeight. That is where the mPaddle is to be positioned.
        canvas.drawBitmap(mPaddle, mPaddleX - mPaddle.getWidth() / 2, mCanvasHeight - mPaddle.getHeight() / 2, null);
    }

    //This is run whenever the phone is touched by the user
    @Override
    protected void actionOnTouch(float x, float y) {
        // Added @3.5. Position paddle where user touches screen.
        mPaddleX = x;
    }


    //This is run whenever the phone moves around its axises
    @Override
    protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {
		// Increase/decrease the speed of the ball.
        mPaddleSpeedX = mPaddleSpeedX + 70f * xDirection;

        // Added @3.5. Prevent paddle from going outside screen, whilst also preventing the
        // tricky 'wobble' problem (solved by taking into account direction of travel).
        // Remember, direction of travel is given by speed polarity where,
        // -ve speed means item is moving to left...
        // +ve speed means moving to right.

        if(mPaddleX <= 0 && mPaddleSpeedX < 0) {   // test position and direction of travel.
            mPaddleSpeedX = 0; // Stop paddle if it will go off screen to the left.
            mPaddleX = 0; // Make sure mPaddleX is never less than ZERO.
                          // Part of paddle 'wobble' prevention.
        }
        if(mPaddleX >= mCanvasWidth && mPaddleSpeedX > 0) { // test position and direction of travel.
            mPaddleSpeedX = 0; // Stop paddle if it will go off screen to the   right.
            mPaddleX = mCanvasWidth; // Make sure mPaddleX is never more than the screen width.
                                     // Part of paddle 'wobble' prevention.
        }
    }

    //This is run just before the game "scenario" is printed on the screen
    @Override
    protected void updateGame(float secondsElapsed) {
        // Move the ball's X and Y using the speed (pixel/sec).
        // i.e. speed = distance / time
        // so time * speed = distance.
        // Distance travelled, in X and Y directions, is what we're calculating here, it seems:
        mBallX = mBallX + secondsElapsed * mBallSpeedX;
        mBallY = mBallY + secondsElapsed * mBallSpeedY;

        // Stop ball going off of the screen. We want it to bounce off the screen edge...
        // IOW,
        // If ball hits LH edge of screen *OR*
        // if ball hits RH edge of screen, make it bounce back.
        // Also, we want to take into account the 'direction of travel'* of the ball.
        // Hence either condition is only true depending on whether mBallSpeedX shows
        // ball going left (<0) or right (>0). So,
        // if ball has reached LH edge (*having come from the right) then bounce it back.
        // if ball has reached RH edge (*having come from the left) then bounce it back.
        // *This fixes the ball 'wobble' problem Karsten talked about, apparently.
        if( ( (mBallX <= mBall.getWidth()/2) && (mBallSpeedX < 0) ) ||
            ( (mBallX >= mCanvasWidth - mBall.getWidth()/2) && (mBallSpeedX > 0) ) )
        {
            mBallSpeedX = -mBallSpeedX; // Bounce back (inverting the speed produces the bounce).
        }

        if( ( (mBallY <= mBall.getHeight()/2) && (mBallSpeedY < 0) ) ||
                ( (mBallY >= mCanvasHeight - mBall.getWidth()/2) && (mBallSpeedY > 0) ) )
        {
            mBallSpeedY = -mBallSpeedY; // Bounce back (inverting the speed produces the bounce).
        }

        // Move the paddle's X using the speed (pixel/sec).
        // i.e. Position paddle (basically the distance in x direction) given the speed and time:
        mPaddleX = mPaddleX + secondsElapsed * mPaddleSpeedX;
    }
}

// This file is part of the course "Begin Programming: Build your first mobile game" from futurelearn.com
// Copyright: University of Reading and Karsten Lundqvist