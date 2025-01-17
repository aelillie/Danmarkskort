package Model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map;

public class AddressSearcher {

    //Type of compare: either 1 = startsWith compare otherwise equality compare
    private static int binSearch(ArrayList<Address> list, Address addr, int low, int high, int type){
        if(low > high) return -1;
        int mid = (low+high)/2;
        if (list.get(mid).searchCompare(addr,type) < 0) return binSearch(list, addr, mid + 1, high, type); //if addr is larger than mid
        else if (list.get(mid).searchCompare(addr, type) > 0) return binSearch(list, addr, low, mid - 1, type); //if addr is smaller than mid
        else return mid;
    }

    /**
     * Since there can be several addresses with the same name (e.g. Lærkevej in Copenhagen and Lærkevej in Roskilde),
     * this method searches the lower part and higher part of the array bounded by the first element which is found in the list to determine
     * the bounds of the similiar results in the array.
     * @param addressInput
     * @return
     */
    private static int[] multipleEntriesSearch(Address addressInput, ArrayList<Address> addressList, int type){
        int index = binSearch(addressList,addressInput,0,addressList.size()-1,type); //Returns the index of the first found element.
        if(index < 0) return null; //Not found

        int lowerBound = index; //Search to the left of the found element
        int i = lowerBound;
        do {
            lowerBound = i;
            i = binSearch(addressList, addressInput, 0, lowerBound-1,type);
        } while (i != -1); //As long as we find a similiar element, keep searching to determine when we don't anymore.

        int upperBound = index; //Search to the right of the found element
        i = upperBound;
        do {
            upperBound = i;
            i = binSearch(addressList, addressInput, upperBound+1, addressList.size() - 1,type);
        }
        while (i != -1); //As long as we find a similiar element, keep searching to determine when we don't anymore.

        int[] range = {lowerBound, upperBound}; //The bounds of the similiar elements in the list.
        return range;
    }

    public static Address[] searchForAddresses(Address addressInput, ArrayList<Address> addressList, int type){
        int[] range = multipleEntriesSearch(addressInput, addressList,type); //search for one or multiple entries
        if(range == null) { //If it is not found, the return value will be negative

            return null;
        } else {

            int lowerBound = range[0], upperBound = range[1];
            Address[] results = new Address[upperBound-lowerBound+1];
            int arrayIndex = 0;
            for(int i = lowerBound; i <= upperBound; i++){
                results[arrayIndex] = addressList.get(i);
                arrayIndex++;
            }
            return results;
        }
    }
}
