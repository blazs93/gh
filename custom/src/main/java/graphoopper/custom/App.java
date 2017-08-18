package graphoopper.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.osm.GraphHopperOSM;
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

		GraphHopper hopper = new GraphHopperOSM().forServer();
		hopper.setCHEnabled(false);
		List<String> weightingList = new ArrayList<String>();
		weightingList.add("fastest");
		hopper.setLmFactoryDecoratorWeightingList(weightingList);
		
		hopper.setDataReaderFile("/home/balazs/Dev/graphhopper/pbf/hungary.pbf");
		// where to store graphhopper files?
		hopper.setGraphHopperLocation("/home/balazs/Dev/graphhopper/graphs/hungary_min-gh");
		hopper.setEncodingManager(new EncodingManager("car"));

		// now this can take minutes if it imports or a few seconds for loading
		// of course this is dependent on the area you import
		hopper.importOrLoad();

		// simple configuration of the request object, see the
		// GraphHopperServlet class for more possibilities.
		// for (int i = 0; i < 10; i++) {

		GHRequest req = new GHRequest(47.42143831321862, 19.152817726135257, 46.20817441176365, 20.017502903938297)
				.setWeighting("fastest").setVehicle("car").setLocale(Locale.US)
				.setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);
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
