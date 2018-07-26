package com.liberition.tool;


import java.text.ParseException;

public class conversion {

    public static void main(String[] args) throws Exception {

        Double convUom = CheckAndConvertDoubles("0",1.00, 3);
        if (convUom == 0) {
            throw new IllegalStateException("Invalid conversion UOM " + convUom.toString() + " (lots characteristic 812) for order " + "123");
        }
        Double purchaseQuantityReceived = CheckAndConvertDoubles("20.000",1.00, 3) / convUom;
        System.out.println(purchaseQuantityReceived);
    }

    public static Double CheckAndConvertDoubles(String strIn, Double quot, int truncate) throws Exception {
        Double dOut = 0.0;
        if (strIn==null || strIn.equals(""))
            return dOut;
        try
        {
            dOut = quot * java.text.NumberFormat.getNumberInstance(java.util.Locale.US).parse(strIn).doubleValue();
            String strD = dOut.toString() +"000";
            int dotPos = strD.indexOf(".");
            strD = strD.substring(0, (dotPos+(truncate+1)) );
            dOut = Double.parseDouble(strD);

            //BRLTools.Log("Quot used:" + quot.toString() + " amount in:" + strIn + " amount out: " + dOut.toString() );
        }
        catch(NumberFormatException e) {
            dOut = 0.001;
            //throw new Exception("Exception in number processing (smaller than 0.001, so defaulting to 0.001:" + e.getMessage());
        }
        catch (ParseException e) {
            throw new Exception(e.getMessage());
        }
        return dOut;
    }

}

