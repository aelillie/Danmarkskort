package Model;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

/**
 * Created by Kevin on 16-03-2015.
 */
public class AddressSearcher {


    private static int binSearch(List<Address> list, Address addr, int low, int high){
        if(low > high) return -1;
        int mid = (low+high)/2;
        if (list.get(mid).compareTo(addr) < 0) return binSearch(list, addr, mid + 1, high); //if addr is larger than mid
        else if (list.get(mid).compareTo(addr) > 0) return binSearch(list, addr, low, mid - 1); //if addr is smaller than mid
        else return mid;
    }

    /**
     * Since there can be several addresses with the same name (e.g. Lærkevej in Copenhagen and Lærkevej in Roskilde),
     * this method searches the lower part and higher part of the array bounded by the first element which is found in the list to determine
     * the bounds of the similiar results in the array.
     * @param addressInput
     * @return
     */
    private static int[] multipleEntriesSearch(Address addressInput, List<Address> addressList){
        int index = binSearch(addressList,addressInput,0,addressList.size()-1); //Returns the index of the first found element.
        if(index < 0) return null; //Not found

        int lowerBound = index; //Search to the left of the found element
        int i = lowerBound;
        do {
            lowerBound = i;
            i = binSearch(addressList, addressInput, 0, lowerBound-1);
        } while (i != -1); //As long as we find a similiar element, keep searching to determine when we don't anymore.

        int upperBound = index; //Search to the right of the found element
        i = upperBound;
        do {
            upperBound = i;
            i = binSearch(addressList, addressInput, upperBound+1, addressList.size() - 1);
        }
        while (i != -1); //As long as we find a similiar element, keep searching to determine when we don't anymore.

        int[] range = {lowerBound, upperBound}; //The bounds of the similiar elements in the list.
        return range;
    }

    public static void searchForAddresses(Address addressInput, List<Address> addressList, Map<Address, Point2D> addressMap){
        int[] range = multipleEntriesSearch(addressInput, addressList); //search for one or multiple entries
        if(range == null) { //If it is not found, the return value will be negative
            System.out.println("Too bad - didn't find!");
        } else {
            System.out.println("Found something");
            int lowerBound = range[0], upperBound = range[1];
            System.out.printf("low: "+lowerBound + ", high: "+upperBound);
            for(int i = lowerBound; i <= upperBound; i++){
                Address foundAddr = addressList.get(i);
                Point2D coordinate = addressMap.get(foundAddr);
                //System.out.println("x = " + coordinate.getX() + ", y = " +coordinate.getY());
            }
        }
    }
}