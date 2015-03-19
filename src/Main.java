import Controller.Controller;
import Model.Model;
import View.View;


public class Main {
    public static void main(String[] args) {
        Model m = Model.getModel();
        //m.loadFile("binaryModel.bin");
        m.loadFiles("Shapes.bin", "Icons.bin");
        View v = new View(m);
        Controller c = new Controller(m,v);
        v.setVisible(true);
    }
}
