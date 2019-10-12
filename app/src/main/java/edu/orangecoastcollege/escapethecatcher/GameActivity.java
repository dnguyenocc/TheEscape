package edu.orangecoastcollege.escapethecatcher;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class GameActivity extends Activity implements GestureDetector.OnGestureListener {

    private GestureDetector aGesture;

    //FLING THRESHOLD VELOCITY
    final int FLING_THRESHOLD = 500;

    //BOARD INFORMATION
    final int SQUARE = 150;
    final int OFFSET = 5;
    final int COLUMNS = 7;
    final int ROWS = 8;
    final int gameBoard[][] = {
            {1, 1, 1, 1, 1, 1, 1},
            {1, 2, 2, 1, 2, 1, 1},
            {1, 2, 2, 2, 2, 2, 1},
            {1, 2, 1, 2, 2, 2, 1},
            {1, 2, 2, 2, 2, 1, 1},
            {1, 2, 2, 2, 2, 2, 3},
            {1, 2, 1, 2, 2, 2, 1},
            {1, 1, 1, 1, 1, 1, 1}
    };

    private Player player;
    private Zombie zombie;

    //LAYOUT AND INTERACTIVE INFORMATION
    private ArrayList<ImageView> visualObjects;
    private RelativeLayout activityGameRelativeLayout;
    private ImageView zombieImageView;
    private ImageView playerImageView;
    private ImageView obstacleImageView;
    private ImageView exitImageView;
    private int exitRow;
    private int exitCol;

    //  WINS AND LOSSES
    private int wins;
    private int losses;
    private TextView winsTextView;
    private TextView lossesTextView;

    private LayoutInflater layoutInflater;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        activityGameRelativeLayout = (RelativeLayout) findViewById(R.id.activity_game);
        winsTextView = (TextView) findViewById(R.id.winsTextView);
        lossesTextView = (TextView) findViewById(R.id.lossesTextView);

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resources = getResources();

        visualObjects = new ArrayList<ImageView>();

        wins = 0;
        losses = 0;
        winsTextView.setText(resources.getString(R.string.win) + wins);
        lossesTextView.setText(resources.getString(R.string.losses) + losses);

        // Instantiate the GestureDetector
        aGesture = new GestureDetector(this,this);

        startNewGame();
    }

    private void startNewGame() {
        //TASK 1:  CLEAR THE BOARD (ALL IMAGE VIEWS)
        for (int i = 0; i < visualObjects.size(); i++) {
            ImageView visualObj = visualObjects.get(i);
            activityGameRelativeLayout.removeView(visualObj);
        }
        visualObjects.clear();

        //TASK 2:  BUILD THE  BOARD
        buildGameBoard();

        //TASK 3:  ADD THE CHARACTERS
        createZombie();
        createPlayer();
    }

    private void buildGameBoard() {
        // TODO: Inflate the entire game board (obstacles and exit)
        for (int i=0; i<ROWS; i++)
            for (int j=0; j<COLUMNS; j++)
            {
                if (gameBoard[i][j] == BoardCodes.OBSTACLE)
                {
                    obstacleImageView = (ImageView) layoutInflater.inflate(R.layout.obstacle_layout,null);
                    obstacleImageView.setX(j*SQUARE+OFFSET);
                    obstacleImageView.setY(i*SQUARE+OFFSET);

                    activityGameRelativeLayout.addView(obstacleImageView);

                    visualObjects.add(obstacleImageView);
                }
                else if (gameBoard[i][j] == BoardCodes.EXIT)
                {
                    exitImageView = (ImageView) layoutInflater.inflate(R.layout.exit_layout,null);
                    exitImageView.setX(j*SQUARE+OFFSET);
                    exitImageView.setY(i*SQUARE+OFFSET);

                    activityGameRelativeLayout.addView(exitImageView);

                    visualObjects.add(exitImageView);
                    exitRow = i;
                    exitCol = j;
                }
            }
    }

    private void createZombie() {
        // TODO: Determine where to place the Zombie (at game start)
        // TODO: Then, inflate the zombie layout
        int row = 5;
        int col = 5;
        // Let's instantiate a new zombie object
        zombie = new Zombie();
        zombie.setRow(row);
        zombie.setCol(col);

        // Let's inflate the zombie layout at a specific x and y location:
        zombieImageView = (ImageView) layoutInflater.inflate(R.layout.zombie_layout,null);

        // Set the x coordinate of the imageView
        zombieImageView.setX(col*SQUARE+OFFSET);
        zombieImageView.setY(row*SQUARE+OFFSET);

        // Display the zombie image view within relative layout
        activityGameRelativeLayout.addView(zombieImageView);

        visualObjects.add(zombieImageView);

    }

    private void createPlayer() {
        // TODO: Determine where to place the Player (at game start)
        // TODO: Then, inflate the player layout
        int row = 1;
        int col = 1;
        // Let's instantiate a new zombie object
        player = new Player();
        player.setRow(row);
        player.setCol(col);

        // Let's inflate the zombie layout at a specific x and y location:
        playerImageView = (ImageView) layoutInflater.inflate(R.layout.player_layout,null);

        // Set the x coordinate of the imageView
        playerImageView.setX(col*SQUARE+OFFSET);
        playerImageView.setY(row*SQUARE+OFFSET);

        // Display the zombie image view within relative layout
        activityGameRelativeLayout.addView(playerImageView);

        visualObjects.add(playerImageView);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return aGesture.onTouchEvent(event);
    }

    private void movePlayer(float velocityX, float velocityY) {
        // TODO: This method gets called in the onFling event

        String direction = "";
        // TODO: Determine which absolute velocity is greater (x or y)
        if (Math.abs(velocityX)>Math.abs(velocityY))
        {
            // X is larger (Move LEFT or RIGHT)
            // Determine if move is LEFT:
            if (velocityX < -FLING_THRESHOLD)
                direction = "LEFT";
            else if (velocityX > FLING_THRESHOLD)
                direction = "RIGHT";
        }
        else
        {
            if (velocityY < -FLING_THRESHOLD)
                direction = "UP";
            else if (velocityY > FLING_THRESHOLD)
                direction = "DOWN";
        }

        // Only move the player if the direction is NOT an empty string.
        if (!direction.equals(""))
        {
            player.move(gameBoard,direction);
            playerImageView.setX(player.getCol()*SQUARE + OFFSET);
            playerImageView.setY(player.getRow()*SQUARE + OFFSET);
        }

        // Move the zombie not matter what
        zombie.move(gameBoard,player.getCol(),player.getRow());
        zombieImageView.setX(zombie.getCol()*SQUARE+OFFSET);
        zombieImageView.setY(zombie.getRow()*SQUARE+OFFSET);

        // Determine if the game is won or lost!!
        // Game Win
        if (player.getRow() == exitRow && player.getCol() == exitCol)
        {
            winsTextView.setText(resources.getString(R.string.win) + (++wins));
            Toast.makeText(this,"WIN",Toast.LENGTH_LONG).show();
            startNewGame();
        }
        else if (player.getCol() == zombie.getCol() && player.getRow() == zombie.getRow())
        {
            lossesTextView.setText(resources.getString(R.string.losses) + (++losses));
            Toast.makeText(this,"LOSE",Toast.LENGTH_LONG).show();
            startNewGame();
        }

        // TODO: If x is negative, move player left.  Else if x is positive, move player right.
        // TODO: If y is negative, move player down.  Else if y is positive, move player up.

        // TODO: Then move the zombie, using the player's row and column position.
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        movePlayer(velocityX,velocityY);
        return false;
    }
}
