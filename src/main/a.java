package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TestFullScreenPanel {
    private static class FSPanel implements ActionListener {
        private JPanel panel;
        private JButton button;
        private boolean fullScreen = false;
        private Container previousContentPane;
        public FSPanel(String label) {
            panel = new JPanel(new BorderLayout());
            button = new JButton(label);
            button.addActionListener(this);
            panel.add(button);
        }
        public JComponent getComponent() {
            return panel;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!fullScreen) {
                goFullScreen();
            } else {
                ungoFullScreen();
            }
        }
        private void goFullScreen() {
            Window w = SwingUtilities.windowForComponent(button);
            if (w instanceof JFrame) {
                JFrame frame = (JFrame) w;
                frame.dispose();
                frame.setUndecorated(true);
                frame.getGraphicsConfiguration().getDevice().setFullScreenWindow(w);
                previousContentPane = frame.getContentPane();
                frame.setContentPane(button);
                frame.revalidate();
                frame.repaint();
                frame.setVisible(true);
                fullScreen = true;
            }
        }
        private void ungoFullScreen() {
            Window w = SwingUtilities.windowForComponent(button);
            if (w instanceof JFrame) {
                JFrame frame = (JFrame) w;
                frame.dispose();
                frame.setUndecorated(false);
                frame.getGraphicsConfiguration().getDevice().setFullScreenWindow(null);
                frame.setContentPane(previousContentPane);
                panel.add(button);
                frame.revalidate();
                frame.repaint();
                frame.setVisible(true);
                fullScreen = false;
            }
        }
    }
    TestFullScreenPanel() {
        final JFrame f = new JFrame(TestFullScreenPanel.class.getSimpleName());
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.add(new FSPanel("Center").getComponent(), BorderLayout.CENTER);
        f.add(new FSPanel("North").getComponent(), BorderLayout.NORTH);
        f.add(new FSPanel("South").getComponent(), BorderLayout.SOUTH);
        f.add(new FSPanel("West").getComponent(), BorderLayout.WEST);
        f.add(new FSPanel("East").getComponent(), BorderLayout.EAST);
        f.setSize(800, 600);
        f.setLocationByPlatform(true);
        f.setVisible(true);
    }
    public static void main(String[] args) {
        // start the GUI on the EDT
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TestFullScreenPanel();
            }
        });
    }
}