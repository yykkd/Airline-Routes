package ashesi.edu.gh.ICP313;

import java.io.*;
import java.util.*;

public class Airport {

static Map<String, String> all_airports= new HashMap<>();
/**
    instance variables and fields
 */
    Map<String, String> matching_airports = new HashMap<>();
    String country;
    String city;
    String airport_name;
    String icao_code;
    String latitude;
    String longitude;

    /**
     * Constructor:
     * Build and initialise objects of this class
     * @param city_name name of departure city
     * @param country_name name of departure country
     */
    public Airport(String city_name, String country_name) {
        this.city = city_name;
        this.country = country_name;
        readAllAirports();
    }

    /**
     * reads all airports in the airports.csv file and stores it in hashmap
     */
    public static void readAllAirports(){
        BufferedReader breader;

        try {
            breader = new BufferedReader(new FileReader("airports.csv"));
            String line = breader.readLine();

            while (line != null) {
                String[] airport_data = line.split("(,)(?=(?:[^\"]|\"[^\"]*\")*$)");
                    String airport_name = airport_data[1];
                    String icao_code = airport_data[4];
                    String latitude = airport_data[6];
                    String longitude = airport_data[7];

                    all_airports.put(icao_code, Arrays.toString(new String[]{airport_name, latitude, longitude}));

                line = breader.readLine();
            }
        }catch (FileNotFoundException fnf){
            System.out.println(fnf.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns all airports in starting city with a valid destination
     * @throws IOException throws exception when there is a problem opening streams or files
     */
    public void readMatchingAirports() throws IOException{
        BufferedReader breader = null;

        try{
            breader = new BufferedReader(new FileReader("airports.csv"));
            String line = breader.readLine();

            while(line != null){
                String[] airport_data;
                airport_data = line.split("(,)(?=(?:[^\"]|\"[^\"]*\")*$)");

                if(equals(airport_data) && !(airport_data[4].equals("\\N"))){
                    airport_name = airport_data[1];
                    icao_code = airport_data[4];
                    latitude = airport_data[6];
                    longitude = airport_data[7];

                    matching_airports.put(icao_code, Arrays.toString(new String[]{airport_name, latitude, longitude}));

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
        breader.close();

    }

    /**
     * @return a hashmap of all matching airports
     */
    public Map<String, String> getMatching_airports() {
        return matching_airports;
    }

    /**
     * compares entries in airports.csv
     * @param anotherObj an array version of row entry in airports
     * @return a boolean to indicate whether two entries are equal or not
     */
    public boolean equals(String[] anotherObj) {
        String city_holder = anotherObj[2];

        if(city_holder.length() > 0) {
            if (Character.compare(city_holder.charAt(0), '"') == 0) {
                    city_holder = city_holder.substring(1, city_holder.length() - 1);
            }
        }
        return Objects.equals(city_holder, city) && Objects.equals(anotherObj[3], country);
    }

    /**
     *reads and returns hashmap of matching airports
     * @return hashmap of airports that are in a city
     * @throws IOException throws exception when there is a problem opening streams or files
     */
    public Map<String, String> read_and_getMatching_airports() throws IOException {
        readMatchingAirports();
        return getMatching_airports();
    }

}
