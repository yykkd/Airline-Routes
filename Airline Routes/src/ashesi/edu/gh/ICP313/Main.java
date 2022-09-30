package ashesi.edu.gh.ICP313;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        ArrayList<String> file_contents = readFile();
        String start_city = file_contents.get(0).strip();
        String start_country = file_contents.get(1).strip();
        String destination_city = file_contents.get(2).strip();
        String destination_country = file_contents.get(3).strip();

        ArrayList<ArrayList<String>> all_paths = null;
        Airport starting = new Airport(start_city, start_country);
        Airport destination = new Airport(destination_city, destination_country);

        Map<String, String> starting_airports = starting.read_and_getMatching_airports();
        Map<String, String> destination_airports = destination.read_and_getMatching_airports();

        Airline air = new Airline(start_country);
        ArrayList<String> available_airlines = Airline.available_airlines;

        Route my_route = new Route(available_airlines);
        my_route.get_routebeginning(starting_airports);

        boolean found1 = false;
        boolean found_multiple_paths = false;

//        find routes
        for (String airport : destination_airports.keySet()) {
            found1 = my_route.check_one_step(airport);
            found_multiple_paths = my_route.find_multi_Path(airport);
        }

        if (found1 || found_multiple_paths) {
            all_paths = my_route.path;
        } else{
            System.out.println("No route found");
            System.exit(0);
        }

//      find optimal route
        HashMap<ArrayList<String>, Double> optimals = optimalPath(all_paths);

//      write optimal route to file
        writefile(optimals);
    }

    /**
     * reads input file
     * @return returns start city, start country, stop city, and stop country
     */
    static ArrayList<String> readFile(){
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(new FileInputStream("Accra_Winnipeg.txt"));
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());
            System.out.println("Enter valid input file");
        }
        assert fileReader != null;
        String[] line1 = fileReader.nextLine().split(",");
        String[] line2 = fileReader.nextLine().split(",");

        String start_city;
        String start_country;

        String destination_city;
        String destination_country;

        start_city = line1[0];
        start_country = line1[1].strip();

        destination_city = line2[0];
        destination_country = line2[1].strip();
//        if any of them is greater than 2, merge the first two into one string seperated by comma

        if(line1.length > 2){
            int go = 0;
            String replaced_city = "";
            while(go < line1.length - 1){
                if(go == line1.length - 2){
                    replaced_city += line1[go];
                } else{
                    replaced_city += line1[go] + ",";
                }
                go++;
            }
            start_city = replaced_city.strip();
            start_country = line1[2].strip();
        } else if(line2.length > 2){
            int go = 0;
            String replaced_city = "";
            while(go < line2.length - 1){
                if(go == line2.length - 2){
                    replaced_city += line2[go];
                } else{
                    replaced_city += line2[go] + ",";
                }
                go++;
            }
            destination_city = replaced_city.strip();
            destination_country = line2[2].strip();
        }

        ArrayList<String> individuals = new ArrayList<>();
        individuals.add(start_city);
        individuals.add(start_country);
        individuals.add(destination_city);
        individuals.add(destination_country);

        return individuals;
    }

    /**
     * finds optimal path amongst all possible paths
     * @param all_paths a list of lists of all possible paths
     * @return a hashmap of optimal path and optimal distance
     */
    static HashMap<ArrayList<String>, Double> optimalPath(ArrayList<ArrayList<String>> all_paths){
        HashMap<ArrayList<String>, Double> optimals = new HashMap<>();
        Double total;
        ArrayList<String> optimal_path = new ArrayList<>();
        Double optimal_distance = Double.POSITIVE_INFINITY;
        Map<String, String> all_airports;
        all_airports = Airport.all_airports;

//        calculates optimal path
        for (ArrayList<String> path : all_paths) {
            total = (double) 0;
            for (String smaller_path : path) {
                String[] start_stop = smaller_path.split(",");
                String start = start_stop[0].substring(1);
                String stop = start_stop[1].substring(1, 4);

                double start_lat = Double.parseDouble(all_airports.get(start).split(",")[1]);
                double start_long = Double.parseDouble(all_airports.get(start).split(",")[2].substring(1, 8));

                double stop_lat = Double.parseDouble(all_airports.get(stop).split(",")[1]);
                double stop_long = Double.parseDouble(all_airports.get(stop).split(",")[2].substring(1, 8));

                double distance = haversine(start_lat, start_long, stop_lat, stop_long);

                total += distance;

                if(total < optimal_distance){
                    optimal_distance = total;
                    optimal_path = path;
                }
            }

        }
        optimals.put(optimal_path, optimal_distance);
        return optimals;
    }

    /**
     * writes solution to output file
     * @param optimals a hashmap that maps a string array list to a double
     * @throws FileNotFoundException prints a message if file cannot be found
     */
    static void writefile(HashMap<ArrayList<String>, Double> optimals) throws FileNotFoundException {
        ArrayList<String> optimal_path = null;
        int numbering = 0;
        int total_stops = 0;

        PrintWriter outputStream = null;
//      try opening the file
        try{
            outputStream = new PrintWriter(new FileOutputStream("Accra_Winnipeg_output.txt"));

        } catch(FileNotFoundException fnf){
            System.out.println(fnf.getMessage());
            System.exit(0);
        }

//      retrieve the optimal path
        for(ArrayList<String> path: optimals.keySet()){
            optimal_path = path;
        }
//        retrieve optimal distance
        double optimal_distance = optimals.get(optimal_path);

//        write information for a sub path to file
        for(String path: optimal_path){
            numbering+=1;
            String[] start_stop = path.split(",");
            String start = start_stop[0].substring(1);
            String destination = start_stop[1].substring(1, 4);
            ArrayList<String> airport_code_stops = Route.get_airportID_stops(start, destination);
            String airline_code = airport_code_stops.get(0);


            int stops = Integer.parseInt(airport_code_stops.get(1));
            outputStream.println("\t" + numbering + ". " + airline_code + " from " + start + " to " + destination + " " + stops + " stops");
            total_stops += stops;

        }
//        add additional information
        outputStream.println("Total flights: " + numbering);
        outputStream.println("Total additional stops: " + total_stops);
        outputStream.println("Total distance: " + Math.round(optimal_distance)+" km");
        outputStream.println("Optimality criteria: Total distance");
        outputStream.close();
    }

    /**
     *
     * @param lat1 latitude of start airport
     * @param lon1 longitude of start airport
     * @param lat2 latitude of destination airport
     * @param lon2 longitude of destination airport
     * @return distance between two airports
     */
    static Double haversine(double lat1, double lon1,
                                double lat2, double lon2) {
        // distance between latitudes and longitudes
        double latitude = Math.toRadians(lat2 - lat1);
        double longitude = Math.toRadians(lon2 - lon1);

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(latitude / 2), 2) +
                Math.pow(Math.sin(longitude / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }
}


