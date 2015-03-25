package View;

import Model.MapIcon;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Kevin on 17-03-2015.
 */
public class MapMenu extends JComboBox<Icon> {

    private HashMap<Icon, String> mapNameMap = new HashMap<>();

    /**
     * Sets up ComboBox with different types of maps.
     */
    public MapMenu(){
        loadImagesAndOptions();
        initialize();
    }


    private void initialize(){
        Dimension prefered = getPreferredSize();
        setEditable(false);

        setBorder(BorderFactory.createRaisedBevelBorder());
        setBounds((int) prefered.getWidth() - 160, (int) (prefered.getHeight() - prefered.getHeight() / 3 * 2 - 50), 130, 30);
        setBackground(Color.white);
        setFocusable(false);
        setActionCommand("mapTypeChange");
        MapTypeBoxRenderer maptypeRend = new MapTypeBoxRenderer(mapNameMap);
        maptypeRend.setPreferredSize(new Dimension(300, 50));
        setRenderer(maptypeRend);
    }

    private void loadImagesAndOptions(){
        ImageIcon standardMapImage = new ImageIcon(MapIcon.standard);
        ImageIcon colorblindMapImage = new ImageIcon(MapIcon.colorblind);
        ImageIcon transportMapImage = new ImageIcon(MapIcon.transport);
        mapNameMap.put(standardMapImage, "Standard");
        mapNameMap.put(colorblindMapImage, "Colorblind map");
        mapNameMap.put(transportMapImage, "Transport map");
        addItem(standardMapImage);
        addItem(colorblindMapImage);
        addItem(transportMapImage);
    }

    public String getChosen(){
        return mapNameMap.get(getSelectedItem());

    }
}
