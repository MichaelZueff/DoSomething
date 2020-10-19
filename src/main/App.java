package main;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class App extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JSlider slider1;
    private JSlider slider2;
    private JLabel label1_int;
    private JLabel label2_int;
    private JSlider slider4;
    private JLabel label4_int;
    private JLabel label_ico;
    private JCheckBox checkBox1;
    private Boolean isWorking = false;
    private Thread threadWorking;

    public App() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        slider1.setValue(1000);
        slider2.setValue(60);

        label1_int.setText(String.valueOf(slider1.getValue()));
        label2_int.setText(String.valueOf(slider2.getValue()));

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isWorking) {
                    onCancel();
                } else {
                    onOK();
                }
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        slider1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                label1_int.setText(String.valueOf(source.getValue()));
            }
        });

        slider2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                label2_int.setText(String.valueOf(source.getValue()));
            }
        });
    }

    private void onOK() {
        isWorking = true;
        label_ico.setIcon(new ImageIcon(getClass().getResource("/Iamworking.gif")));
        slider1.setEnabled(false);
        slider2.setEnabled(false);
        slider4.setEnabled(false);
        checkBox1.setEnabled(false);
        threadWorking = new Thread(new MouseMoveThread(slider1.getValue(), slider2.getValue()));
        threadWorking.start();
        buttonOK.setText("Стоп");
        dispose();
    }

    private void onCancel() {
        isWorking = false;
        label_ico.setIcon(null);
        slider1.setEnabled(true);
        slider2.setEnabled(true);
        slider4.setEnabled(false);
        checkBox1.setEnabled(true);
        threadWorking.interrupt();
        buttonOK.setText("Поехали");
        dispose();
    }

    private static int rand(int randomMin, int randomMax) {
        int diff = randomMax - randomMin;
        Random random = new Random();
        int i = random.nextInt(diff + 1);
        i += randomMin;
        return i;
    }

    private static int nextLocation(int point) {
        int result = 0;
        while (result == 0) {
            result = rand(-1, 1);
        }
        return point + result;
    }

    private class MouseMoveThread implements Runnable {
        private Robot robot;
        int param1_from;
        int param1_to;
        int param2_from;
        int param2_to;

        MouseMoveThread(int param1_to, int param2_to) {
            this.param1_from = 1000;
            this.param2_from = 1;
            this.param1_to = param1_to;
            this.param2_to = param2_to;

            try {
                this.robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        // ALT+TAB
        void keyPress_ALT_TAB() {
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            robot.delay(10);
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_ALT);
        }

        @Override
        public void run() {
            Point startLocation;
            int i = 0;

            while(true) {
                startLocation = MouseInfo.getPointerInfo().getLocation();
                int mouseX = (int) startLocation.getX();
                int mouseY = (int) startLocation.getY();

                mouseX = nextLocation(mouseX);
                mouseY = nextLocation(mouseY);
                robot.mouseMove(mouseX, mouseY);

                i++;
                try {
                    Thread.sleep( 1 );
                } catch (InterruptedException e) {
                    break;
                }
                if (i > rand(param1_from, param1_to)) {
                    i = 0;
                    if (checkBox1.isSelected()) {
                        keyPress_ALT_TAB();
                    }
                    try {
                        Thread.sleep(1000 * rand(param2_from, param2_to));
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Настройки моей работы");
        frame.setContentPane(new App().contentPane);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(450, 400);
        frame.setLocation(500, 300);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
