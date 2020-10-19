package main;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

public class MouseMoving {
    private Thread thread;

    public static void main(String[] args) {
        new MouseMoving().execute();
    }

    private void execute() {
        thread = new Thread( new MouseMoveThread( 300, 300, 300, 1 ) );
        thread.start();
    }

    private class MouseMoveThread implements Runnable {
        private Robot robot;
        private int startX;
        private int startY;
        private int currentX;
        private int currentY;
        private int xAmount;
        private int yAmount;
        private int xAmountPerIteration;
        private int yAmountPerIteration;
        private int numberOfIterations;
        private long timeToSleep;

        MouseMoveThread(int xAmount, int yAmount, int numberOfIterations, long timeToSleep) {
            this.xAmount = xAmount;
            this.yAmount = yAmount;
            this.numberOfIterations = numberOfIterations;
            this.timeToSleep = timeToSleep;

            try {
                robot = new Robot();
                Point startLocation = MouseInfo.getPointerInfo().getLocation();
                startX = startLocation.x;
                startY = startLocation.y;
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            currentX = startX;
            currentY = startY;
            xAmountPerIteration = xAmount / numberOfIterations;
            yAmountPerIteration = yAmount / numberOfIterations;
            int i = 0;

            while (i < numberOfIterations) {
                //currentX < startX + xAmount && currentY < startY + yAmount

                currentX += xAmountPerIteration;
                currentY += yAmountPerIteration;
                robot.mouseMove( currentX, currentY );

                try {
                    Thread.sleep( timeToSleep );
                } catch (InterruptedException e) {
                    break;
                }
                i++;
            }
        }
    }
}
