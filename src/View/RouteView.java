package View;

import Controller.RoutePanelController;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * Created by Kevin on 16-03-2015.
 */
public class RouteView extends JPanel{

    private JTextField startAddressField, endAddressField;
    private JPanel startEndAddressPanel;
    private JButton findRouteButton;
    private JButton carButton, bicycleButton, footButton;

    /**
     * Creates a panel used for getting a path from A to B in the program
     */
    public RouteView(){

        setVisible(false);
        setBounds(20, 79, 342, 180);
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(new MatteBorder(1, 1, 1, 1, new Color(161, 161, 161)));
        setLayout(new BorderLayout());
        makeFindRoutePanel();
        add(startEndAddressPanel, BorderLayout.CENTER);

        RoutePanelController rp = new RoutePanelController(this);

    }

    private void makeFindRoutePanel(){
        JPanel transportTypePanel = new JPanel();
        transportTypePanel.setBackground(Color.WHITE);
        transportTypePanel.setBounds(20, 200, 342, 50);
        transportTypePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        transportTypePanel.setBorder(new MatteBorder(0,0,1,0, new Color(161, 161, 161)));


        carButton = new JButton("Car");
        carButton.setFocusable(false);
        carButton.setForeground(new Color(114, 114, 114));
        carButton.setBackground(Color.WHITE);


        bicycleButton = new JButton("Bicycle");
        bicycleButton.setFocusable(false);
        bicycleButton.setForeground(new Color(114, 114, 114));
        bicycleButton.setBackground(Color.WHITE);


        footButton = new JButton("By foot");
        footButton.setFocusable(false);
        footButton.setForeground(new Color(114, 114, 114));
        footButton.setBackground(Color.WHITE);


        transportTypePanel.add(carButton);
        transportTypePanel.add(bicycleButton);
        transportTypePanel.add(footButton);



        makeStartEndAddressPanel();

        add(transportTypePanel, BorderLayout.NORTH);

    }

    private void makeStartEndAddressPanel(){
        startEndAddressPanel = new JPanel();
        startEndAddressPanel.setLayout(new GridBagLayout()); //A layout allowing you to customize grids moreso than a standard GridLayout.

        GridBagConstraints c;

        JLabel startIconLabel = new JLabel(new ImageIcon("data//startPointIcon.png"));
        c = new GridBagConstraints();
        c.fill= GridBagConstraints.NONE;
        c.gridx = 0; //Which column the component should be in.
        c.gridy = 0; //Which row the component should be in.
        c.weightx = 0.15; //The weight amongst the elements in the row
        c.weighty = 0.5; //The weight amongst the elements in the column.
        startEndAddressPanel.add(startIconLabel,c);


        startAddressField = new JTextField();
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.ipady = 15;
        c.weightx = 0.85;
        c.weighty = 0.5;
        c.insets = new Insets(5,0,0,25); //Inset/distance from the right.
        startEndAddressPanel.add(startAddressField,c);

        JLabel endIconLabel = new JLabel(new ImageIcon("data//endPointIcon.png"));
        c = new GridBagConstraints();
        c.fill= GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.15;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.PAGE_START;
        startEndAddressPanel.add(endIconLabel,c);

        endAddressField = new JTextField();
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        c.ipady = 15;
        c.weightx = 0.85;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(0,0,0,25); //Inset/distance from the right
        startEndAddressPanel.add(endAddressField,c);

        findRouteButton = new JButton("Find route");
        findRouteButton.setBackground(Color.WHITE);
        findRouteButton.setFocusable(false);
        findRouteButton.setActionCommand("findRoute");
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LAST_LINE_END; //Where the component is situated - in this case to the lower right corner.
        //c.weighty = 0.1;
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(0,0,10,10);
        startEndAddressPanel.add(findRouteButton,c);
    }

    /**
     * Toggle the panel on and off
     */
    public void showRoutePanel(){
        boolean isVisible = isVisible();
        setVisible(!isVisible);
    }


    public JButton getFindRouteButton() { return findRouteButton; }

    public JButton getCarButton() { return carButton; }

    public JButton getBicycleButton() { return bicycleButton; }

    public JButton getFootButton() { return footButton; }
}