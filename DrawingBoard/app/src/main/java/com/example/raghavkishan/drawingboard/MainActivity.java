package com.example.raghavkishan.drawingboard;
/* reference for the entore application:
1)from: https://developer.android.com
2)Class slides
*/
/*
The application begins with this activity and a menu is used to switch between draw,delete and move modes.
 */
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    public static Paint blackFill;
    public static Paint blueFill;
    public static Paint redFill;
    public static Paint greenFill;
    public static String mode = "draw_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View circle =(DrawCircleView) findViewById(R.id.draw_circle_view);
        setTitle("DrawingBoard - Draw");
    }

    static {
        blackFill = new Paint();
        blackFill.setColor(Color.BLACK);
        blackFill.setStyle(Paint.Style.STROKE);

        blueFill = new Paint();
        blueFill.setColor(Color.BLUE);
        blueFill.setStyle(Paint.Style.STROKE);

        redFill = new Paint();
        redFill.setColor(Color.RED);
        redFill.setStyle(Paint.Style.STROKE);

        greenFill = new Paint();
        greenFill.setColor(Color.GREEN);
        greenFill.setStyle(Paint.Style.STROKE);
    }

    public static Paint paint = blackFill;

    public static Paint getPaint() {
        return paint;
    }

    public static void setPaint(Paint paint) {
        MainActivity.paint = paint;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.draw_mode:
                mode = "draw_mode";
                setTitle("DrawingBoard - Draw");
                case R.id.select_black:
                    setPaint(blackFill);
                    return true;
                case R.id.select_blue:
                    setPaint(blueFill);
                    return true;
                case R.id.select_green:
                    setPaint(greenFill);
                    return true;
                case R.id.select_red:
                    setPaint(redFill);
                    return true;
            case R.id.delete_mode:
                mode = "delete_mode";
                setTitle("DrawingBoard - Delete");
                return true;
            case R.id.move_mode:
                mode = "move_mode";
                setTitle("DrawingBoard - Move");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
