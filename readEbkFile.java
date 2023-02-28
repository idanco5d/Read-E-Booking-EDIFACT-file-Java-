package bkauto1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class readEbkFile {
//parameters
final static String otherReferenceRowPrefix = "BGM+335";
final static int otherReferenceLength = 10;
final static String vesselRowPrefix = "TDT+20";
final static String polPrefix = "LOC+11";
final static String podPrefix = "LOC+9";
final static int portStart = 6;
final static int portLength = 5;
final static String bookingPartyPrefix = "NAD+ZZZ";
final static String forwarderPrefix = "NAD+FW";
final static String weightPrefix = "MEA+AAE+G+KGM:";
final static String containerPrefix = "EQD+CN";
final static String otherReferencePreStrs[] = {"INTTRA-Act+","INTTRA-Link+","INTTRA-ACT+"};
final static int otherReferencePreStrsSize = 3;

public static boolean isOtherReferenceRow (String row) {
    return isMatching(row, otherReferenceRowPrefix);
}

public static boolean isVesselRow (String row) {
    return isMatching(row, vesselRowPrefix);
}

public static boolean isPolRow (String row) {
    return isMatching(row, polPrefix);
}

public static boolean isPodRow (String row) {
    return isMatching(row, podPrefix);
}

public static boolean isBPartyRow (String row) {
    return isMatching(row, bookingPartyPrefix);
}

public static boolean isForwarderRow (String row) {
    return isMatching(row, forwarderPrefix);
}

public static boolean isWeightRow (String row) {
    return isMatching(row, weightPrefix);
}

public static boolean isContainerRow (String row) {
    return isMatching(row, containerPrefix);
}

public static boolean isMatching(String row, String target) {
    if (row.length() < target.length()) {
        return false;
    }
    if (row.substring(0,target.length()).equals(target)) {
        return true;
    }
    return false;
}

public static String extractCustomerName(String input) {
    // Find the index of the first occurrence of "160:87+"
    int startIndex = input.indexOf("160:87+");
    if (startIndex == -1) {
        // The text "160:87+" was not found in the input
        return null;
    }

    // Find the index of the first ":" that doesn't have a question mark next to it
    int endIndex = startIndex + 7;
    while (endIndex < input.length()) {
        char c = input.charAt(endIndex);
        if (c == '\'' || c == ':' && (endIndex == input.length() - 1 || input.charAt(endIndex + 1) != '?')) {
            break;
        }
        endIndex++;
    }

    // Extract the text between the two indices and remove question marks
    String extractedText = input.substring(startIndex + 7, endIndex).replace("?", "");
    return extractedText;
}

public static double extractWeight(String row) {
    int prefixIndex = row.indexOf(weightPrefix);
    if (prefixIndex == -1) {
        return 0.0; // prefix not found in row
    }
    int startIndex = prefixIndex + weightPrefix.length();
    int endIndex = row.indexOf("'", startIndex);
    if (endIndex == -1) {
        return 0.0; // closing ' not found after prefix
    }
    String weightString = row.substring(startIndex, endIndex);
    return Double.parseDouble(weightString);
}


public static String extractContainer(String inputString) {
    int plusCount = 0;
    int startIndex = 0;
    int endIndex = inputString.length();

    for (int i = 0; i < inputString.length(); i++) {
        char c = inputString.charAt(i);
        if (c == '+') {
            plusCount++;
            if (plusCount == 3) {
                startIndex = i + 1; // skip over the third '+'
            }
        } else if (c == ':' && plusCount >= 3) {
            endIndex = i; // stop searching at the first ':' after the third '+'
            break;
        }
    }

    return inputString.substring(startIndex, endIndex);
}

public static String fetchOtherReference(String input) {
    int otherRefsStart[] = new int[otherReferencePreStrsSize];
    int searchIndex = -1;
    String searchString = "";
    for (int i=0; i< otherReferencePreStrsSize; i++) {
        otherRefsStart[i] = input.indexOf(otherReferencePreStrs[i]);
        if (otherRefsStart[i] > -1) {
            searchIndex = otherRefsStart[i];
            searchString = otherReferencePreStrs[i];
            break;
        }
    }
    if (searchIndex == -1) {
        return null;
    }
        // The search string was not found in the input string
    int startIndex = searchIndex + searchString.length();
    int endIndex = startIndex + otherReferenceLength;
    if (endIndex > input.length()) {
        // There are not 10 characters remaining after the search string
        return null;
    }
    return input.substring(startIndex, endIndex);
}

public static Booking readFile(String filePath) {
    Booking fileBk = new Booking();
    BufferedReader br;
    try {
        br = new BufferedReader(new FileReader(filePath));
        String row;
        boolean readPorts = false;
        ArrayList<Double> weights = new ArrayList<>();
        int currentContainer = -1;
        while ((row = br.readLine()) != null) {
            if (isOtherReferenceRow(row)) {
                fileBk.setOtherReference(fetchOtherReference(row));
                continue;
            }
            if (isVesselRow(row)) {
                readPorts = true;
                continue;
            }
            if (readPorts) {
                if (fileBk.getPol() != null && fileBk.getPod() != null) {
                    readPorts = false;
                    continue;
                }
                if (isPodRow(row)) {
                    fileBk.setPod(row.substring(portStart, portStart+portLength));
                    continue;
                }
                if (isPolRow(row)) {
                    fileBk.setPol(row.substring(portStart+1, portStart+portLength+1));
                    continue;
                }
            }
            if (isBPartyRow(row)) {
                Customer shipper = new Customer(CustType.customerType.BOOKING_PARTY, extractCustomerName(row));
                fileBk.addCustomer(shipper);
                continue;
            }
            if (isForwarderRow(row)) {
                Customer forwarder = new Customer(CustType.customerType.FORWARDER, extractCustomerName(row));
                fileBk.addCustomer(forwarder);
                continue;
            }

            //needs adjustment for files other than EDIFACT format
            if(isWeightRow(row)) {
                weights.add(extractWeight(row));
                continue;
            }
            if (isContainerRow(row)) {
                currentContainer++;
                try {
                    BookedItem container = new BookedItem(extractContainer(row), 1, weights.get(currentContainer));
                    fileBk.addBookedItem(container);
                    continue;
                }
                catch (IndexOutOfBoundsException e) {
                    System.out.println("Error: " + e.getMessage());
                    br.close();
                }
            }
        }
        br.close();
    } catch (IOException e) {
        System.err.println("Failed to read file: " + e.getMessage());
        return fileBk;
    }
    return fileBk;
}

}
