import java.awt.event.*;

public class Controller extends MouseAdapter implements ActionListener {
    Model model;
    View view;
    double x;
    double y;

    public Controller(Model m, View v) {
        model = m;
        view = v;
        //Set up Handlers for mouse and keyboard and let controller set these for view.
        keyHandler kH = new keyHandler();
        view.canvas.addKeyListener(kH);
        MouseHandler mH = new MouseHandler();
        view.addMouseListener(mH);
        view.addMouseMotionListener(mH);
        view.addMouseWheelListener(mH);

        // The controller handles what should happen if a button is pressed.
        view.searchArea.addActionListener(this);
        view.searchButton.addActionListener(this);
        view.zoomInButton.addActionListener(this);
        view.zoomOutButton.addActionListener(this);
        view.fullscreenButton.addActionListener(this);
    }

    @Override
    /**
     * Sets up what should happen when a button is pressed.
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command == "zoomIn") view.zoom(1.2);
        else if (command == "zoomOut") view.zoom(1/1.2);
        else if (command == "fullscreen") view.toggleFullscreen();
        else if (command == "search"){
            String input = view.searchArea.getText().trim();
            Address address = Address.parse(input);
            //System.out.println(address.street()+" " + address.house()+" "+address.side()+ " "+address.city()+" "+address.postcode());
            view.canvas.requestFocusInWindow();
        }
        else if (command == "maptype");
    }

    // sets up events for mouse and calls the methods in view.
    private class MouseHandler extends MouseAdapter {

        public void mouseDragged(MouseEvent e) {
            view.mouseDragged(e);
        }
        public void mouseMoved(MouseEvent e) {}
        public void mouseClicked(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {
            view.mousePressed(e);
        }
        public void mouseWheelMoved(MouseWheelEvent e) {
            view.wheelZoom(e);
        }
        public void mouseReleased(MouseEvent e) {}
    }
    private class keyHandler extends KeyAdapter{

        @Override
        /**
         * Listens for keyboard events
         */
        public void keyPressed(KeyEvent e) {
            //Set up the keyboard handler for different keys.
            switch (e.getKeyChar()) {
                case '+':
                    view.zoom(1.2);
                    break;
                case '-':
                    view.zoom(1/1.2);
                    break;
                case 'a':
                    view.toggleAA();
                    break;
                case 's':
                    model.save("savegame.bin");
                    break;
                case 'l':
                    model.load("savegame.bin");
                    break;
            }
            if (e.getKeyCode() == e.VK_UP) {
                view.pan(0, 10);
            }
            if (e.getKeyCode() == e.VK_DOWN) {
                view.pan(0, -10);
            }
            if (e.getKeyCode() == e.VK_LEFT) {
                view.pan(10, 0);
            }
            if (e.getKeyCode() == e.VK_RIGHT) {
                view.pan(-10, 0);
            }

        }
    }
}
