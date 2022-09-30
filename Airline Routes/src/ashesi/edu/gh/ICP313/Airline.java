package ashesi.edu.gh.ICP313;

import java.io.*;
import java.util.*;

public class Airline {
    /**
     * instance variables and fields
     */
    static ArrayList<String> available_airlines = new ArrayList<>();
    static Map<String, String> available_airlines_icao = new HashMap<>();
    String country;
    String airline_code;
    String icao_code;
    String is_active;

    /**
     * Constructor:
     * Build and initialise objects of this class
     * @param country_name name of departure country
     * @throws IOException throws exception when there is a problem opening streams or files
     */
    public Airline(String country_name) throws IOException {
        this.country = country_name;
        readFile();
    }

    /**
     * @param airline_code code of airline
     * @return String icao code for a given airline code
     * @throws IOException throws exception when there is a problem opening streams or files
     */
    public static String getAvailable_airlines_icao(String airline_code) throws IOException {
        return available_airlines_icao.get(airline_code);
    }

    /**
     * reads airline.csv and adds data to hashmap and arraylist
     * @throws IOException throws exception when there is a problem opening streams or files
     */
    public void readFile() throws IOException{
        BufferedReader breader;

        try{
            breader = new BufferedReader(new FileReader("airlines.csv"));
            String line = breader.readLine();

            while(line != null){
                String[] airline_data = line.split(",");
                if(canFly(airline_data)){
                    airline_code = airline_data[3];
                    icao_code = airline_data[4];
                    is_active = airline_data[7];
                    available_airlines_icao.put(airline_code, icao_code);
                    available_airlines.add(airline_code);
                }
                line = breader.readLine();
            }

        }
        catch (FileNotFoundException fnf){
            System.out.println(fnf.getMessage());
        }
        catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }

    }

    /**
     * checks whether an airline is available to fly
     * @param airline_data an array of an airlines information
     * @return boolean that checks whether airline can fly
     */
    public boolean canFly(String[] airline_data) {
        return airline_data[7].equals("Y");
    }

}

