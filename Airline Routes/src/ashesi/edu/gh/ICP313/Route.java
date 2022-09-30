package ashesi.edu.gh.ICP313;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Route {
    /**
     * instance variables and fields
     */
    static ArrayList<String> all_routes= new ArrayList<>();
    ArrayList<String[]> source_airports= new ArrayList<>();
    String airline_code;
    String airline_id;
    String source_acode;
    String destination_acode;
    String stops;
    static Map<String, ArrayList<String>> airport_graph;
    Map<String, String> store_parent = new HashMap<>();
    ArrayList<ArrayList<String>> path = new ArrayList<>();

    /**
     * Constructor:
     * Build and initialise objects of this class
     * @param available_airlines arraylist of all available airlines
     */
    public Route(ArrayList<String> available_airlines) throws IOException {
        create_graph(available_airlines);
        readAllRoutes();
    }

    /**
     * reads data from routes.csv and stores it in arraylist
     */
    public static void readAllRoutes() throws IOException {
        BufferedReader breader = null;

        try {
            breader = new BufferedReader(new FileReader("routes.csv"));
            String line = breader.readLine();

            while (line != null) {
                String[] route_data = line.split(",");

                    String airline_code = route_data[0];
                    String start_airport = route_data[2];
                    String stop_airport = route_data[4];
                    String stops = route_data[7];
//                    if the flight that will take you from one airport to another is not available, don't add it to all_routes
                ArrayList<String> available_airlines = Airline.available_airlines;

                if(available_airlines.contains(airline_code)){
                    String airline_icao = Airline.getAvailable_airlines_icao(airline_code);
                    all_routes.add(Arrays.toString(new String[]{start_airport, airline_code, stop_airport, stops, airline_icao}));
                }
                line = breader.readLine();
            }
        }catch (FileNotFoundException fnf){
            System.out.println(fnf.getMessage());
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        breader.close();
    }

    /**
     * creates a graph of all valid routes and stores it in a hasmap
     * @param available_airlines the airlines that are available to fly
     */
    public static void create_graph(ArrayList<String> available_airlines){
        airport_graph = new HashMap<>();
        BufferedReader breader;
        try {
//            read from file
            breader = new BufferedReader(new FileReader("routes.csv"));
            String line = breader.readLine();
            String[] route_data;

//            while file has not ended run the code
            while(line != null){
                route_data = line.split(",");
                String airline_code = route_data[0].strip();

//              if the airline in a route is valid continue
                if(available_airlines.contains(airline_code)){
//                    if an airport exists in the hashmap update its values
                    if(!(airport_graph.containsKey(route_data[2]))){
                        if(!Objects.equals(route_data[4], "/N")) {
                            ArrayList<String> temp = new ArrayList<>();
                            temp.add(route_data[4]);
                            airport_graph.put(route_data[2], temp);
                        }
                    }
//                    else create a new key,value pair in the hashmap with airport as key and all possible airports it can reach as value
                    else{
                        ArrayList<String> temp = airport_graph.get(route_data[2]);
                        if(!Objects.equals(route_data[4], "/N")){
                            temp.add(route_data[4]);
                            airport_graph.put(route_data[2], temp);
                        }
                    }
                }
                line = breader.readLine();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * looks for valid route if more than one flight has to be taken
     * @param destination_airport the destination airport
     * @return whether a path was found or not
     */
    public boolean find_multi_Path(String destination_airport){
        String current_airport = source_acode;
        ArrayList<String> next_level;

//        checks if destination airport is the same as start airport
        if(current_airport==null) {
            System.out.println("No solution found");
            System.exit(0);
        }
            if(current_airport.equals(destination_airport)){
                System.out.println("Already in city. Enter a different city");
                return true;
            }


        ArrayList<String> frontier = new ArrayList<>();
        frontier.add(current_airport);
        store_parent.put(current_airport, "None");
        ArrayList<String> explored = new ArrayList<>();

//        performs search to find all possible paths from start to destination
        while(frontier.size() > 0){
            current_airport = frontier.remove(0);
            explored.add(current_airport);

            next_level= airport_graph.get(current_airport);
            for (String child : next_level) {
                if (!(explored.contains(child)) && !(frontier.contains(child))) {
                    store_parent.put(child, current_airport);

                    if (child.equals(destination_airport)) {
                        solution_path(destination_airport);
                        return true;
                    }
                    frontier.add(child);
                }
            }
        }
        return false;
    }

    /**
     * finds all routes that start in the city
     * @param city_airports all airports that are in start city
     */
    public void get_routebeginning(Map<String, String> city_airports){
        BufferedReader breader;
        try {
            breader = new BufferedReader(new FileReader("routes.csv"));
            String line = breader.readLine();

            while (line != null) {
                String[] route_data = line.split(",");
                if (city_airports.containsKey(route_data[2])) {
                    airline_code = route_data[0];
                    airline_id = route_data[1];
                    source_acode = route_data[2];
                    destination_acode = route_data[4];
                    stops = route_data[7];

                    source_airports.add(new String[]{airline_code, airline_id, source_acode, destination_acode, stops});
                }
                line = breader.readLine();
            }
        }catch (FileNotFoundException fnf){
            System.out.println(fnf.getMessage());
        }
        catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * looks for a valid route if one flight has to be taken
     * @param destination_airport the destination airport
     * @return whether or not a one flight solution has been found
     */
    public boolean check_one_step(String destination_airport){
        for(String[] arr: source_airports){
            if(arr[3].equals(destination_airport)){
                ArrayList<String> temp = new ArrayList<>();
                temp.add(Arrays.toString(new String[]{arr[2], arr[3]}));
                path.add(temp);
                return true;
            }
        }
        return false;
    }

    /**
     * takes a start and destination airports and returns airline id and stops
     * @param start the starting city
     * @param destination the destination city
     * @return arraylist with flight id and number of stops
     */
    public static ArrayList<String> get_airportID_stops(String start, String destination){
        BufferedReader breader;
        try {
            breader = new BufferedReader(new FileReader("routes.csv"));
            String line = breader.readLine();


            while (line != null) {
                String[] route_data = line.split(",");
                if (route_data[2].equals(start) && route_data[4].equals(destination)){
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(route_data[0]);
                    temp.add(route_data[7]);
                    return temp;
                }
                line = breader.readLine();
            }
        } catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }
        return null;
    }

    /**
     * traces the solution path when a route is found
     * @param destination destination airport
     */
    public void solution_path(String destination){
        ArrayList<String> temp = new ArrayList<>();
        while(!Objects.equals(store_parent.get(destination), "None")){
            temp.add(0, Arrays.toString(new String[]{store_parent.get(destination), destination}));
            destination = store_parent.get(destination);
        }
        if(!path.contains(temp)){
            path.add(temp);
        }
    }
}
