import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import static java.lang.Math.max;

public class View extends JFrame implements Observer {
    public static final long serialVersionUID = 0;
    Model model;
    Canvas canvas;
    private AffineTransform transform = new AffineTransform();
    private boolean antialias = true;
    private Point dragEndScreen, dragStartScreen;
    protected double zoomLevel;
    protected JTextField searchArea;
    protected JButton searchButton;
    protected JButton zoomInButton;
    protected JButton zoomOutButton;
    protected JButton fullscreenButton;
    private boolean isFullscreen = false;
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    private String promptText = "Enter Address";

    /**
     * Creates the window of our application.
     *
     * @param m Reference to Model class
     */
    public View(Model m) {
        super("This is our map");
        model = m;

        /*Two helper functions to set up the AfflineTransform object and
        make the buttons and layout for the frame*/
        setScale();
        makeGUI();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        canvas.requestFocusInWindow();
        //This sets up a listener for when the frame is re-sized.
        this.getRootPane().addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                //Re position the buttons.
                zoomOutButton.setBounds(getWidth()-115, getHeight()-getHeight()/3*2,39,37);
                fullscreenButton.setBounds(getWidth()-70, getHeight()-getHeight()/3*2,39,37);
                zoomInButton.setBounds(getWidth()-160,getHeight()- getHeight()/3*2,39,37);
            }
        });
        model.addObserver(this);
        zoomLevel = model.bbox.getWidth() * -1;
    }

    /**
     * Sets the scale for the afflineTransform object using to bounds from the osm file
     * Also sets up the frame size from screenSize
     */
    private void setScale(){
        // bbox.width * xscale * .56 = 512
        // bbox.height * yscale = 512

        //Get the monitors size.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        //Set up the scale amount for our Afflinetransform
        double xscale = width / .56 / model.bbox.getWidth();
        double yscale = height / model.bbox.getHeight();
        double scale = max(xscale, yscale);
        transform.scale(.56*scale,-scale);
        transform.translate(-model.bbox.getMinX(), -model.bbox.getMaxY());

        //Set up the JFrame using the monitors resolution.
        setSize(screenSize);
        setPreferredSize(screenSize);
        setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    /**
     * Makes use of different layers to put JComponent on top
     * of the canvas. Creates the GUI for the
     */
    private void makeGUI(){
        //retrieve the LayeredPane stored in the frame.
        JLayeredPane layer = getLayeredPane();
        //Create the canvas and Components for GUI.
        canvas = new Canvas();

        canvas.setBounds(0,0,getWidth(),getHeight());

        searchArea = new JTextField();
        searchArea.setText(promptText);
        //Create a FocusListener for the textField
        searchArea.addFocusListener(new FocusListener() {
            @Override
            /**
             * If selected remove prompt text
             */
            public void focusGained(FocusEvent e) {
                if (searchArea.getText().equals(promptText)) {
                    searchArea.setText("");
                }
            }

            @Override
            /**
             * if unselected and search field is empty sets up promptText.
             */
            public void focusLost(FocusEvent e) {
                if (searchArea.getText().isEmpty()) {
                    searchArea.setText(promptText);
                }
            }

        });
        Font font = new Font("Arial",Font.PLAIN,16);

        //Create The buttons and configure their visual design.
        searchArea.setFont(font);
        searchArea.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(4, 7, 4, 7, new Color(75, 138, 247)),
                BorderFactory.createRaisedBevelBorder()));
        searchArea.setBounds(20,20,300,37);

        searchButton = new JButton();
        searchButton.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 4, 7, new Color(75, 138,247)),
                BorderFactory.createRaisedBevelBorder()));
        searchButton.setBackground(new Color(36, 45, 50));
        searchButton.setIcon(new ImageIcon("data//searchIcon.png"));
        searchButton.setFocusable(false);
        searchButton.setBounds(320,20,43,37);
        searchButton.setActionCommand("search");

        zoomInButton = new JButton();
        zoomInButton.setBackground(new Color(255,255,255));
        zoomInButton.setIcon(new ImageIcon("data//plusIcon.png"));
        zoomInButton.setBorder(BorderFactory.createRaisedBevelBorder()); //Temp border
        zoomInButton.setFocusable(false);
        zoomInButton.setActionCommand("zoomIn");
        zoomInButton.setBounds(getWidth()-160,getHeight()-getHeight()/3*2,39,37);

        zoomOutButton = new JButton();
        zoomOutButton.setBackground(new Color(255,255,255));
        zoomOutButton.setIcon(new ImageIcon("data//minusIcon.png"));
        zoomOutButton.setBorder(BorderFactory.createRaisedBevelBorder());
        zoomOutButton.setFocusable(false);
        zoomOutButton.setActionCommand("zoomOut");
        zoomOutButton.setBounds(getWidth()-115, getHeight()-getHeight()/3*2,39,37);

        fullscreenButton = new JButton();
        fullscreenButton.setBackground(new Color(255,255,255));
        fullscreenButton.setIcon(new ImageIcon("data//fullscreenIcon.png"));
        fullscreenButton.setBorder(BorderFactory.createRaisedBevelBorder());
        fullscreenButton.setFocusable(false);
        fullscreenButton.setActionCommand("fullscreen");
        fullscreenButton.setBounds(getWidth()-70, getHeight()-getHeight()/3*2,39,37);


        JComboBox<Icon> mapMenu = new JComboBox<>();
        mapMenu.setEditable(false);
        mapMenu.setActionCommand("maptype");

        //Add all the components to their the Frame on the right layer.
        layer.add(canvas, new Integer(1));
        layer.add(searchArea, new Integer(2));
        layer.add(searchButton, new Integer(2));
        layer.add(zoomInButton, new Integer(2));
        layer.add(zoomOutButton, new Integer(2));
        layer.add(fullscreenButton, new Integer(2));

    }



    @Override
    public void update(Observable obs, Object obj) {
        canvas.repaint();

    }

    /**
     * The function of this method is to scale the view of the canvas by a factor given.
     * then pans the view to remove the moving towards 0,0 coord.
     * @param factor Double, the factor scaling
     */
    public void zoom(double factor) {
        //Check whether we zooming in or out for adjusting the zoomLvl field
        if(factor > 1) zoomLevel += 0.0765;
        else zoomLevel -= 0.0765;
        //Scale the graphic and pan accordingly
        transform.preConcatenate(AffineTransform.getScaleInstance(factor, factor));
        pan(getWidth() * (1-factor) / 2, getHeight() * (1-factor) / 2);
    }

    /**
     * Creates the inverse transformation of a point given.
     * It simply transforms a device space coordinate back
     * to user space coordinates.
     * @param p1 Point2D mouse position
     * @return Point2D The point after inverse of the scale.
     * @throws NoninvertibleTransformException
     */
    private Point2D.Float transformPoint(Point p1) throws NoninvertibleTransformException {
        Point2D.Float p2 = new Point2D.Float();
        transform.inverseTransform(p1, p2); // create a destination p2 from the Point p1
        return p2;
    }

    /**
     * Zooms in on the canvas with the mouseWheel. Also translates (pans) towards
     * mouse point when zooming by using its Point2d.
     * @param e MouseWheelEvent
     */
    public void wheelZoom(MouseWheelEvent e){
        try {
            int wheelRotation = e.getWheelRotation();
            Point p = e.getPoint();
            if (wheelRotation > 0) {
                //point2d before zoom
                Point2D p1 = transformPoint(p);
                transform.scale(1 / 1.2, 1 / 1.2);
                //point after zoom
                Point2D p2 = transformPoint(p);
                transform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY()); //Pan towards mouse
                zoomLevel -= 0.0765; //Decrease zoomLevel
                repaint();

            } else {
                Point2D p1 = transformPoint(p);
                transform.scale(1.2, 1.2);
                Point2D p2 = transformPoint(p);
                transform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY()); //Pan towards mouse
                zoomLevel += 0.0765; //increase zoomLevel
                repaint();

            }
        } catch (NoninvertibleTransformException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Sets the point of where the mouse was dragged from
     *
     * @param e MouseEvent
     */
    public void mousePressed(MouseEvent e){
        dragStartScreen = e.getPoint();
        dragEndScreen = null;
    }

    /**
     * Moves the screen to where the mouse was dragged, using the transforms translate method with the
     * the difference dragged by the mouse.
     * @param e MouseEvent
     */
    public void mouseDragged(MouseEvent e){
        try {
            dragEndScreen = e.getPoint();
            //Create a point2d.float with the
            Point2D.Float dragStart = transformPoint(dragStartScreen);
            Point2D.Float dragEnd = transformPoint(dragEndScreen);
            //calculate how far the screen is dragged from its start position.
            double dx = dragEnd.getX() - dragStart.getX();
            double dy = dragEnd.getY() - dragStart.getY();
            transform.translate(dx, dy);

            //remember to reposition points to avoid unstable dragging.
            dragStartScreen = dragEndScreen;
            dragEndScreen = null;
            repaint();
        } catch (NoninvertibleTransformException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Moves the canvas by a fixed amount using the Translate method.
     * @param dx
     * @param dy
     */
    public void pan(double dx, double dy) {
        transform.preConcatenate(AffineTransform.getTranslateInstance(dx,dy));
        repaint();
    }

    /**
     * Making that canvas look crisp and then back to shit.
     */
    public void toggleAA() {
        antialias = !antialias;
        repaint();
    }

    /**
     * Makes the view Frame fullscreen with help of a graphic device.
     */
    public void toggleFullscreen(){
        if(!isFullscreen) {
            gd.setFullScreenWindow(this);
        } else {
            gd.setFullScreenWindow(null);
        }
        isFullscreen = !isFullscreen;
    }

    /**
     * The canvas object is where our map of paths and images (points) will be drawn on
     *
     */
    class Canvas extends JComponent {
        public static final long serialVersionUID = 4;
        Stroke min_value = new BasicStroke(Float.MIN_VALUE);

        @Override
        public void paint(Graphics _g) {
            Graphics2D g = (Graphics2D) _g;
            //Set the Transform for Graphic2D element before drawing.
            g.setTransform(transform);
            if (antialias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setStroke(min_value); //Just for good measure.
            g.setColor(Color.BLACK);
            //Drawing everything not categorized as a area or line object.
            for (Shape line : model) {
                g.draw(line);
            }
            //Draw EVERYTHING
            for (Drawable drawable : model.drawables) {
                if (zoomLevel > -0.4)
                    drawable.drawBoundary(g);
            }
            /*
            for (Drawable drawable : model.drawables) {
                if (drawable.drawLevel < zoomLevel)
                    drawable.draw(g);
            }*/

            /*The use of layers list makes it so the paths will be drawn in right order
            So f.eks. paths don't overlap motor ways and so forth.
             */
            for (Drawable drawable : model.firstLayer) {
                if (drawable.drawLevel < zoomLevel)
                    drawable.draw(g);
            }

            for (Drawable drawable : model.secondLayer) {
                if (drawable.drawLevel < zoomLevel)
                    drawable.draw(g);
            }
            for (Drawable drawable : model.thirdLayer) {
                if (drawable.drawLevel < zoomLevel)
                    drawable.draw(g);
            }
            for (Drawable drawable : model.lastLayer) {
                if (drawable.drawLevel < zoomLevel)
                    drawable.draw(g);
            }

            //Draws the icons.
            if (zoomLevel > 0.0) {
                for (MapIcon mapIcon : model.getMapIcons()) {
                    mapIcon.draw(g, transform);
                }


                // }
/*
                //AMALIE Iterator it = model.getStreetMap().entrySet().iterator();
            while (it.hasNext()) {
                int count1 = 0;
                Map.Entry pair = (Map.Entry) it.next();
                java.util.List<Shape> list = (java.util.List<Shape>) pair.getValue();
                String streetName = (String) pair.getKey();
                //g.setStroke(txSt);
                TextDraw txtDr = new TextDraw();
                System.out.println(streetName);
                Path2D.Double street1 = new Path2D.Double();
                g.setColor(Color.BLACK);
                for (Shape street : list) {
                    //if(count == 0){
                    //	street1 =  (Path2D.Double) street;
                    //	count++;
                    //} else {
                    //	street1.append(street,true);
                    //}
                    txtDr.draw(g,new GeneralPath(street),streetName,70.);
                }
            }
*/

            /*
			//Prints out the current center coordinates
			Point2D center = new Point2D.Double(getWidth() / 2, getHeight() / 2);
			try {
				System.out.println("Center: " + transform.inverseTransform(center, null));
			} catch (NoninvertibleTransformException e) {} */
            }
        }
    }

}