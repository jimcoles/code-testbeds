package org.jkcsoft.codetests.wholefoods.jsonget;

import com.oracle.javafx.jmx.json.JSONDocument;
import com.oracle.javafx.jmx.json.JSONReader;
import com.oracle.javafx.jmx.json.impl.JSONStreamReaderImpl;
import jdk.nashorn.internal.runtime.JSONFunctions;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * @author Jim Coles
 */
public class Solution {

    public static void main(String[] args) {

        String query = "https://jsonmock.hackerrank.com/api/countries/search?name=US";

        Reader reader = null;
        try {
            reader = new InputStreamReader(new URL(query).openStream());
            JSONReader jsonReader = new JSONStreamReaderImpl(reader);
            JSONDocument jsonDoc = jsonReader.build();
            reader.close();
            //
            System.out.println("page => " + jsonDoc.getNumber("page"));
            System.out.println("total => " + jsonDoc.getNumber("total"));
            System.out.println("num objects: " + jsonDoc.object().size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
