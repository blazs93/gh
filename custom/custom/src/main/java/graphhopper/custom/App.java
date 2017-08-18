package graphhopper.custom;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.shp.GraphHopperSHP;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.GPXEntry;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;

public class App {
	public static void main(String[] args) {

		long beforeRouting;
		long afterRouting;
		long time;

		GraphHopper hopper = new GraphHopperSHP().forServer();
		hopper.setDataReaderFile("/home/balazs/Dev/graphhopper-master/hungary-roads/gis.osm_roads_free_1.shp");
		// where to store graphhopper files?
		hopper.setGraphHopperLocation("/home/balazs/Dev/graphhopper-master/europe_hungary_small-gh");
		hopper.setEncodingManager(new EncodingManager("car"));

		// now this can take minutes if it imports or a few seconds for loading
		// of course this is dependent on the area you import
		hopper.importOrLoad();

		// simple configuration of the request object, see the
		// GraphHopperServlet class for more possibilities.
		// for (int i = 0; i < 10; i++) {

		GHRequest req = new GHRequest(47.510088, 19.056752, 47.512795, 19.034822).setWeighting("fastest")
				.setVehicle("car").setLocale(Locale.US).setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);
		// measure time
		beforeRouting = System.nanoTime();

		GHResponse rsp = hopper.route(req);
		afterRouting = System.nanoTime();
		time = afterRouting - beforeRouting;

		System.out.println("Routing time: " + String.valueOf(time / 1000000) + " ms");

		// first check for errors
		if (rsp.hasErrors()) {
			// handle them!
			// rsp.getErrors()
			return;
		}

		// use the best path, see the GHResponse class for more
		// possibilities.
		PathWrapper path = rsp.getBest();

		// points, distance in meters and time in millis of the full path
		PointList pointList = path.getPoints();
		double distance = path.getDistance();
		double timeInMs = path.getTime();

		System.out.println("Route distance: " + distance / 1000 + " km");
		System.out.println("Route time: " + timeInMs / 1000 / 3600 + " h");

		InstructionList il = path.getInstructions();
		// iterate over every turn instruction
		for (Instruction instruction : il) {
			instruction.getDistance();
		}

		// or get the json
		@SuppressWarnings("unused")
		List<Map<String, Object>> iList = il.createJson();

		// or get the result as gpx entries:
		@SuppressWarnings("unused")
		List<GPXEntry> list = il.createGPXList();
		// }

	}
}
